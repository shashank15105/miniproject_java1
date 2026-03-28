const bookingForm = document.getElementById("booking-form");
const bookingMessage = document.getElementById("booking-message");
const bookingValidation = document.getElementById("booking-validation");
const workspaceSelect = document.getElementById("workspaceId");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const startTimeInput = document.getElementById("startTime");
const endTimeInput = document.getElementById("endTime");
const submitButton = document.getElementById("booking-submit");
const durationPreview = document.getElementById("durationPreview");
const ratePreview = document.getElementById("ratePreview");
const totalPreview = document.getElementById("totalPreview");
const workspaceDetails = document.getElementById("workspace-details");
const assistMessage = document.getElementById("assistMessage");

let workspaces = [];
let isSubmitting = false;

loadWorkspaces();
prefillTimeRange();
prefillUserDetails();
updatePreview();

workspaceSelect.addEventListener("change", handleWorkspaceChange);
startTimeInput.addEventListener("input", updatePreview);
endTimeInput.addEventListener("input", updatePreview);
nameInput.addEventListener("input", updatePreview);
emailInput.addEventListener("input", updatePreview);

bookingForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const validation = validateForm();
    if (!validation.valid) {
        showValidation(validation.message);
        return;
    }

    const name = nameInput.value.trim();
    const email = emailInput.value.trim();
    const workspaceId = workspaceSelect.value;
    const startTime = startTimeInput.value;
    const endTime = endTimeInput.value;

    clearValidation();
    setLoading(true);
    showBookingMessage("Submitting booking request...", "success");

    try {
        const response = await fetch("/book", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name,
                email,
                workspaceId,
                startTime: toIsoLocal(startTime),
                endTime: toIsoLocal(endTime)
            })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.message || "Booking failed.");
        }

        persistUserDetails(data.userId || "", data.name || name, email);
        showBookingMessage(`Booking confirmed for ${data.name || name}. Booking ID: ${data.bookingId}. Total price: Rs ${data.totalPrice}.`, "success");
        assistMessage.textContent = `Booked successfully for ${data.name || name}.`;
        await loadWorkspaces(workspaceId);
        emailInput.value = email;
        updatePreview();
        redirectToBookings(data.userId || "", data.name || name, email);
    } catch (error) {
        showBookingMessage(error.message, "error");
    } finally {
        setLoading(false);
    }
});

async function loadWorkspaces(preferredWorkspaceId = null) {
    try {
        const response = await fetch("/workspaces");
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to load workspaces.");
        }

        workspaces = data;
        populateWorkspaceOptions(preferredWorkspaceId);
        handleWorkspaceChange();
    } catch (error) {
        workspaceSelect.innerHTML = `<option value="">Unable to load workspaces</option>`;
        workspaceDetails.innerHTML = `<div class="workspace-selection-empty">${error.message}</div>`;
        assistMessage.textContent = "Workspace list is unavailable right now.";
        showValidation(error.message);
    }
}

function populateWorkspaceOptions(preferredWorkspaceId) {
    const params = new URLSearchParams(window.location.search);
    const urlWorkspaceId = params.get("workspaceId");
    const selectedWorkspaceId = preferredWorkspaceId || urlWorkspaceId;

    const options = workspaces.map((workspace) => `
        <option value="${workspace.workspaceId}">
            ${workspace.name} · ${workspace.location} · Rs ${workspace.pricePerHour}/hr
        </option>
    `).join("");

    workspaceSelect.innerHTML = `<option value="">Select a workspace</option>${options}`;

    if (selectedWorkspaceId && workspaces.some((workspace) => workspace.workspaceId === selectedWorkspaceId)) {
        workspaceSelect.value = selectedWorkspaceId;
    } else if (workspaces.length) {
        workspaceSelect.value = workspaces[0].workspaceId;
    }
}

function prefillTimeRange() {
    const now = new Date();
    now.setMinutes(0, 0, 0);

    if (now <= new Date()) {
        now.setHours(now.getHours() + 1);
    }

    const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000);
    startTimeInput.value = toLocalDateTimeInput(now);
    endTimeInput.value = toLocalDateTimeInput(oneHourLater);
    startTimeInput.min = toLocalDateTimeInput(new Date());
    endTimeInput.min = toLocalDateTimeInput(oneHourLater);
}

function prefillUserDetails() {
    const storedName = localStorage.getItem("coworking_name");
    const storedEmail = localStorage.getItem("coworking_email");

    nameInput.value = storedName || "";
    emailInput.value = storedEmail || "";
}

function toLocalDateTimeInput(date) {
    const adjusted = new Date(date.getTime() - (date.getTimezoneOffset() * 60000));
    return adjusted.toISOString().slice(0, 16);
}

function toIsoLocal(inputValue) {
    return new Date(inputValue).toISOString().slice(0, 19);
}

function handleWorkspaceChange() {
    const workspace = getSelectedWorkspace();

    if (!workspace) {
        workspaceDetails.innerHTML = `<div class="workspace-selection-empty">Choose a workspace to see price, location, and remaining seats.</div>`;
        assistMessage.textContent = "Select a workspace to start your booking.";
        updatePreview();
        return;
    }

    workspaceDetails.innerHTML = `
        <div class="workspace-selection-top">
            <span class="card-badge">${workspace.workspaceId}</span>
            <span class="price-tag">Rs ${workspace.pricePerHour}/hr</span>
        </div>
        <h4>${workspace.name}</h4>
        <p>${workspace.location}</p>
        <div class="workspace-selection-meta">
            <span>Capacity: ${workspace.capacity}</span>
            <span>Remaining Seats: ${workspace.availableSeats}</span>
        </div>
    `;
    assistMessage.textContent = workspace.availableSeats > 0
        ? `${workspace.availableSeats} seat(s) left in ${workspace.name}.`
        : `${workspace.name} is currently full.`;
    updatePreview();
}

function updatePreview() {
    const workspace = getSelectedWorkspace();
    const validation = validateForm();

    ratePreview.textContent = workspace ? `Rs ${workspace.pricePerHour}/hr` : "Rs 0/hr";

    if (!workspace) {
        durationPreview.textContent = "-";
        totalPreview.textContent = "Rs 0";
        submitButton.disabled = true;
        return;
    }

    const startDate = new Date(startTimeInput.value);
    const endDate = new Date(endTimeInput.value);
    if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
        durationPreview.textContent = "-";
        totalPreview.textContent = "Rs 0";
        submitButton.disabled = true;
        return;
    }

    const minimumEndDate = new Date(startDate.getTime() + 60 * 60 * 1000);
    endTimeInput.min = toLocalDateTimeInput(minimumEndDate);

    const minutes = Math.max(0, (endDate - startDate) / (1000 * 60));
    const hours = minutes / 60;
    const chargedHours = Math.ceil(hours);

    durationPreview.textContent = `${formatHours(hours)}`;
    totalPreview.textContent = `Rs ${chargedHours * workspace.pricePerHour}`;
    submitButton.disabled = isSubmitting || !validation.valid || workspace.availableSeats <= 0;

    if (!validation.valid) {
        showValidation(validation.message);
    } else {
        clearValidation();
    }

    if (workspace.availableSeats <= 0) {
        showValidation("This workspace has no available seats right now.");
    }
}

function validateForm() {
    const workspace = getSelectedWorkspace();
    if (!nameInput.value.trim()) {
        return { valid: false, message: "Please enter your name." };
    }
    if (emailInput.value.trim() && !isValidEmail(emailInput.value.trim())) {
        return { valid: false, message: "Please enter a valid email address." };
    }
    if (!workspace) {
        return { valid: false, message: "Please choose a workspace." };
    }
    if (!startTimeInput.value || !endTimeInput.value) {
        return { valid: false, message: "Please choose both start and end times." };
    }

    const startDate = new Date(startTimeInput.value);
    const endDate = new Date(endTimeInput.value);

    if (endDate <= startDate) {
        return { valid: false, message: "End time must be after start time." };
    }

    const minutes = (endDate - startDate) / (1000 * 60);
    if (minutes < 60) {
        return { valid: false, message: "Minimum booking duration is 1 hour." };
    }

    return { valid: true, message: "" };
}

function getSelectedWorkspace() {
    return workspaces.find((workspace) => workspace.workspaceId === workspaceSelect.value);
}

function formatHours(hours) {
    if (!Number.isFinite(hours) || hours <= 0) {
        return "-";
    }
    return hours % 1 === 0 ? `${hours} hour${hours === 1 ? "" : "s"}` : `${hours.toFixed(1)} hours`;
}

function showValidation(message) {
    bookingValidation.textContent = message;
    bookingValidation.className = "inline-validation";
}

function clearValidation() {
    bookingValidation.textContent = "";
    bookingValidation.className = "inline-validation hidden";
}

function showBookingMessage(message, type) {
    bookingMessage.textContent = message;
    bookingMessage.className = `message ${type}`;
}

function persistUserDetails(userId, name, email) {
    if (userId) {
        localStorage.setItem("coworking_user_id", userId);
    }
    localStorage.setItem("coworking_name", name);
    localStorage.setItem("coworking_email", email);
}

function isValidEmail(email) {
    return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email);
}

function redirectToBookings(userId, name, email) {
    if (!userId) {
        return;
    }

    const params = new URLSearchParams({
        userId,
        name,
        email
    });

    window.setTimeout(() => {
        window.location.href = `/bookings.html?${params.toString()}`;
    }, 900);
}

function setLoading(isLoading) {
    isSubmitting = isLoading;
    updatePreview();
    submitButton.textContent = isLoading ? "Confirming..." : "Confirm Booking";
}
