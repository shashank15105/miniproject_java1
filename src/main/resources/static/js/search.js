const workspaceGrid = document.getElementById("workspace-grid");
const workspaceMessage = document.getElementById("workspace-message");
const refreshButton = document.getElementById("refresh-workspaces");

refreshButton.addEventListener("click", loadWorkspaces);
loadWorkspaces();

async function loadWorkspaces() {
    showMessage(workspaceMessage, "Loading workspaces...", "success");

    try {
        const response = await fetch("/workspaces");
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to load workspaces.");
        }

        renderWorkspaces(data);
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
            <h3>${workspace.name}</h3>
            <p class="workspace-location">${workspace.location}</p>
            <div class="workspace-meta">
                <span>Capacity: ${workspace.capacity}</span>
                <span>Available: ${workspace.availableSeats}</span>
            </div>
            <a class="primary-button card-button" href="/book.html?workspaceId=${encodeURIComponent(workspace.workspaceId)}">Book</a>
        </article>
    `).join("");
}

function showMessage(target, message, type) {
    target.textContent = message;
    target.className = `message ${type}`;
}
