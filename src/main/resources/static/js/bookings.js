const loadHistoryButton = document.getElementById("load-history-button");
const clearHistoryButton = document.getElementById("clear-history-button");
const historyMessage = document.getElementById("history-message");
const historyResults = document.getElementById("history-results");
const savedUserName = document.getElementById("savedUserName");
const savedUserMeta = document.getElementById("savedUserMeta");

let currentUser = null;

loadHistoryButton.addEventListener("click", () => {
    loadBookings();
});

clearHistoryButton.addEventListener("click", () => {
    clearHistory();
});

document.addEventListener("coworking:auth-changed", (event) => {
    currentUser = event.detail.user || null;
    initializeProfile();
});

initializeProfile();

function initializeProfile() {
    currentUser = window.CoworkingApp?.getCurrentUser?.() || null;

    if (!currentUser) {
        savedUserName.textContent = "No active member session";
        savedUserMeta.textContent = "Log in from the header to view and manage only your own bookings.";
        loadHistoryButton.disabled = true;
        clearHistoryButton.disabled = true;
        renderEmptyState("Log in to see your booking history here.");
        showHistoryMessage("Please log in to load your bookings.", "error");
        return;
    }

    savedUserName.textContent = currentUser.name;
    savedUserMeta.textContent = currentUser.email
        ? `${currentUser.email} · Phone: ${currentUser.phone || "NA"}`
        : `Phone: ${currentUser.phone || "NA"}`;
    loadHistoryButton.disabled = false;
    clearHistoryButton.disabled = false;
    loadBookings();
}

async function loadBookings() {
    if (!currentUser) {
        showHistoryMessage("Please log in to load your bookings.", "error");
        return;
    }

    showHistoryMessage("Loading bookings...", "success");

    try {
        const response = await fetch("/bookings");
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to fetch bookings.");
        }

        renderBookingRows(data);
        showHistoryMessage(
            data.length ? `Loaded ${data.length} booking record(s).` : "No bookings found for this account.",
            data.length ? "success" : "error"
        );
    } catch (error) {
        renderEmptyState("Booking history could not be loaded.");
        showHistoryMessage(error.message, "error");
    }
}

async function clearHistory() {
    if (!currentUser) {
        showHistoryMessage("Please log in to clear your booking history.", "error");
        return;
    }

    const confirmed = window.confirm(
        "Clear all booking history for your account? This will permanently delete the booking records from the database."
    );
    if (!confirmed) {
        return;
    }

    clearHistoryButton.disabled = true;
    loadHistoryButton.disabled = true;
    showHistoryMessage("Clearing booking history...", "success");

    try {
        const response = await fetch("/bookings", {
            method: "DELETE"
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to clear booking history.");
        }

        renderEmptyState("No bookings found for this account.");
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
        renderEmptyState("No bookings found for this account.");
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
            <td>
                <div class="table-actions">
                    <a class="table-action-button" href="/bookings/${encodeURIComponent(booking.bookingId)}/receipt" target="_blank" rel="noreferrer">Receipt PDF</a>
                    <a class="table-secondary-link" href="/bookings/${encodeURIComponent(booking.bookingId)}/email-preview" target="_blank" rel="noreferrer">Email Preview</a>
                </div>
            </td>
        </tr>
    `).join("");
}

function renderEmptyState(message) {
    historyResults.innerHTML = `<tr><td colspan="8" class="empty-cell">${message}</td></tr>`;
}

function formatDateTime(value) {
    return new Date(value).toLocaleString();
}

function showHistoryMessage(message, type) {
    historyMessage.textContent = message;
    historyMessage.className = `message ${type}`;
}
