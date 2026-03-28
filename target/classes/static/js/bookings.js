const loadHistoryButton = document.getElementById("load-history-button");
const historyMessage = document.getElementById("history-message");
const historyResults = document.getElementById("history-results");
const savedUserName = document.getElementById("savedUserName");
const savedUserMeta = document.getElementById("savedUserMeta");

loadHistoryButton.addEventListener("click", () => {
    loadBookings();
});

initializeSavedUser();

function initializeSavedUser() {
    const params = new URLSearchParams(window.location.search);
    const queryUserId = params.get("userId");
    const queryName = params.get("name");
    const queryEmail = params.get("email");

    if (queryUserId) {
        localStorage.setItem("coworking_user_id", queryUserId);
    }
    if (queryName) {
        localStorage.setItem("coworking_name", queryName);
    }
    if (queryEmail) {
        localStorage.setItem("coworking_email", queryEmail);
    }

    const userId = queryUserId || localStorage.getItem("coworking_user_id");
    const name = queryName || localStorage.getItem("coworking_name");
    const email = queryEmail || localStorage.getItem("coworking_email");

    if (!userId) {
        savedUserName.textContent = "No recent booking user";
        savedUserMeta.textContent = "Book a workspace first. Once booked, this page will automatically know who you are.";
        loadHistoryButton.disabled = true;
        renderEmptyState("Book a workspace first to see your booking history here.");
        return;
    }

    savedUserName.textContent = name || "Saved booking user";
    savedUserMeta.textContent = email
        ? `${email} · Internal ID: ${userId}`
        : `Internal ID: ${userId}`;
    loadHistoryButton.disabled = false;
    loadBookings(userId);
}

async function loadBookings(explicitUserId = null) {
    const userId = explicitUserId || localStorage.getItem("coworking_user_id");
    if (!userId) {
        showHistoryMessage("No recent booking user found. Please book a workspace first.", "error");
        return;
    }

    showHistoryMessage("Loading bookings...", "success");

    try {
        const response = await fetch(`/bookings?userId=${encodeURIComponent(userId)}`);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to fetch bookings.");
        }

        renderBookingRows(data);
        showHistoryMessage(
            data.length ? `Loaded ${data.length} booking record(s).` : "No bookings found for this user.",
            data.length ? "success" : "error"
        );
    } catch (error) {
        renderEmptyState("Booking history could not be loaded.");
        showHistoryMessage(error.message, "error");
    }
}

function renderBookingRows(bookings) {
    if (!bookings.length) {
        renderEmptyState("No bookings found for this user.");
        return;
    }

    historyResults.innerHTML = bookings.map((booking) => `
        <tr>
            <td>${booking.bookingId}</td>
            <td>${booking.workspaceId}</td>
            <td>${booking.workspaceName}</td>
            <td>${booking.location}</td>
            <td>${formatDateTime(booking.startTime)}</td>
            <td>${formatDateTime(booking.endTime)}</td>
            <td>Rs ${booking.totalPrice}</td>
        </tr>
    `).join("");
}

function renderEmptyState(message) {
    historyResults.innerHTML = `<tr><td colspan="7" class="empty-cell">${message}</td></tr>`;
}

function formatDateTime(value) {
    return new Date(value).toLocaleString();
}

function showHistoryMessage(message, type) {
    historyMessage.textContent = message;
    historyMessage.className = `message ${type}`;
}
