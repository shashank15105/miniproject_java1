const loadHistoryButton = document.getElementById("load-history-button");
const clearHistoryButton = document.getElementById("clear-history-button");
const historyMessage = document.getElementById("history-message");
const historyResults = document.getElementById("history-results");
const savedUserName = document.getElementById("savedUserName");
const savedUserMeta = document.getElementById("savedUserMeta");

loadHistoryButton.addEventListener("click", () => {
    loadBookings();
});

clearHistoryButton.addEventListener("click", () => {
    clearHistory();
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
        clearHistoryButton.disabled = true;
        renderEmptyState("Book a workspace first to see your booking history here.");
        return;
    }

    savedUserName.textContent = name || "Saved booking user";
    savedUserMeta.textContent = email
        ? `${email} · Internal ID: ${userId}`
        : `Internal ID: ${userId}`;
    loadHistoryButton.disabled = false;
    clearHistoryButton.disabled = false;
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

async function clearHistory() {
    const userId = localStorage.getItem("coworking_user_id");
    if (!userId) {
        showHistoryMessage("No recent booking user found. Please book a workspace first.", "error");
        return;
    }

    const confirmed = window.confirm(
        "Clear all booking history for this profile? This will permanently delete the booking records from the database."
    );
    if (!confirmed) {
        return;
    }

    clearHistoryButton.disabled = true;
    loadHistoryButton.disabled = true;
    showHistoryMessage("Clearing booking history...", "success");

    try {
        const response = await fetch(`/bookings?userId=${encodeURIComponent(userId)}`, {
            method: "DELETE"
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to clear booking history.");
        }

        renderEmptyState("No bookings found for this user.");
        showHistoryMessage(`${data.message} Deleted ${data.deletedCount} record(s).`, "success");
    } catch (error) {
        showHistoryMessage(error.message, "error");
    } finally {
        clearHistoryButton.disabled = false;
        loadHistoryButton.disabled = false;
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
