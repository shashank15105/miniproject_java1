const workspaceGrid = document.getElementById("workspace-grid");
const workspaceMessage = document.getElementById("workspace-message");
const refreshButton = document.getElementById("refresh-workspaces");
const cityFilter = document.getElementById("city-filter");
const clearCityFilterButton = document.getElementById("clear-city-filter");

let allWorkspaces = [];

refreshButton.addEventListener("click", loadWorkspaces);
cityFilter.addEventListener("change", applyFilters);
clearCityFilterButton.addEventListener("click", () => {
    cityFilter.value = "ALL";
    applyFilters();
});
loadWorkspaces();

async function loadWorkspaces() {
    showMessage(workspaceMessage, "Loading workspaces...", "success");

    try {
        const response = await fetch("/workspaces");
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to load workspaces.");
        }

        allWorkspaces = data;
        populateCityFilter(data);
        applyFilters();
        showMessage(
            workspaceMessage,
            data.length ? `Loaded ${data.length} workspace(s).` : "No workspaces with available seats right now.",
            data.length ? "success" : "error"
        );
    } catch (error) {
        workspaceGrid.innerHTML = `<div class="empty-state">${error.message}</div>`;
        showMessage(workspaceMessage, error.message, "error");
    }
}

function populateCityFilter(workspaces) {
    const cities = [...new Set(workspaces.map((workspace) => workspace.location))].sort((left, right) =>
        left.localeCompare(right)
    );

    const currentValue = cityFilter.value || "ALL";
    cityFilter.innerHTML = `
        <option value="ALL">All Cities</option>
        ${cities.map((city) => `<option value="${city}">${city}</option>`).join("")}
    `;

    if (cities.includes(currentValue)) {
        cityFilter.value = currentValue;
    }
}

function applyFilters() {
    const selectedCity = cityFilter.value;
    const filteredWorkspaces = selectedCity === "ALL"
        ? allWorkspaces
        : allWorkspaces.filter((workspace) => workspace.location === selectedCity);

    renderWorkspaces(filteredWorkspaces);
    showMessage(
        workspaceMessage,
        filteredWorkspaces.length
            ? `Showing ${filteredWorkspaces.length} workspace(s)${selectedCity === "ALL" ? "" : ` in ${selectedCity}`}.`
            : `No workspaces available in ${selectedCity}.`,
        filteredWorkspaces.length ? "success" : "error"
    );
}

function renderWorkspaces(workspaces) {
    if (!workspaces.length) {
        workspaceGrid.innerHTML = `<div class="empty-state">No workspaces are available at the moment.</div>`;
        return;
    }

    workspaceGrid.innerHTML = workspaces.map((workspace) => `
        <article class="workspace-card">
            <div class="workspace-top">
                <span class="card-badge">${workspace.workspaceId}</span>
                <span class="price-tag">Rs ${workspace.pricePerHour}/hr</span>
            </div>
            <div class="workspace-heading">
                <h3>${workspace.name}</h3>
                <span class="seat-pill ${workspace.availableSeats <= 3 ? "seat-pill-low" : "seat-pill-open"}">
                    ${workspace.availableSeats <= 3 ? `Only ${workspace.availableSeats} left` : `${workspace.availableSeats} seats left`}
                </span>
            </div>
            <p class="workspace-location">${workspace.location}</p>
            <div class="workspace-review-row">
                <span class="review-pill">
                    ${workspace.reviewCount > 0 ? `${formatRating(workspace.averageRating)} ★` : "New workspace"}
                </span>
                <span class="review-meta">
                    ${workspace.reviewCount > 0 ? `${workspace.reviewCount} review${workspace.reviewCount === 1 ? "" : "s"}` : "No reviews yet"}
                </span>
            </div>
            <div class="workspace-meta">
                <span>Capacity: ${workspace.capacity}</span>
                <span>Live availability: ${workspace.availableSeats}</span>
            </div>
            <div class="amenity-list">
                ${(workspace.amenities && workspace.amenities.length
                    ? workspace.amenities.slice(0, 3)
                    : ["Workspace ready"]).map((amenity) => `
                        <span class="amenity-chip">${amenity}</span>
                    `).join("")}
            </div>
            <a class="primary-button card-button" href="/book.html?workspaceId=${encodeURIComponent(workspace.workspaceId)}">Book Now</a>
        </article>
    `).join("");
}

function showMessage(target, message, type) {
    target.textContent = message;
    target.className = `message ${type}`;
}

function formatRating(value) {
    return Number(value).toFixed(1);
}
