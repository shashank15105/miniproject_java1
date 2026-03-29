const authControls = document.getElementById("auth-controls");
const authModal = document.getElementById("auth-modal");
const authMessage = document.getElementById("auth-message");
const loginForm = document.getElementById("login-form");
const signupForm = document.getElementById("signup-form");

const authState = {
    user: null,
    activeTab: "login"
};

document.addEventListener("click", handleAuthClicks);

if (loginForm) {
    loginForm.addEventListener("submit", handleLogin);
}

if (signupForm) {
    signupForm.addEventListener("submit", handleSignup);
}

initializeAuth();

async function initializeAuth() {
    await refreshAuthState();
    renderAuthControls();
    syncStoredProfile();
    notifyAuthChange();
}

function handleAuthClicks(event) {
    const trigger = event.target.closest("[data-auth-action], [data-auth-tab], [data-auth-close]");
    if (!trigger) {
        return;
    }

    if (trigger.hasAttribute("data-auth-close")) {
        closeAuthModal();
        return;
    }

    const tab = trigger.getAttribute("data-auth-tab");
    if (tab) {
        setAuthTab(tab);
        return;
    }

    const action = trigger.getAttribute("data-auth-action");
    if (action === "open-login") {
        openAuthModal("login");
    } else if (action === "open-signup") {
        openAuthModal("signup");
    } else if (action === "logout") {
        logout();
    }
}

async function refreshAuthState() {
    try {
        const response = await fetch("/auth/me");
        const data = await response.json();
        authState.user = data.authenticated ? data : null;
    } catch (error) {
        authState.user = null;
    }
}

function renderAuthControls() {
    if (!authControls) {
        return;
    }

    if (authState.user) {
        authControls.innerHTML = `
            <div class="member-pill">
                <span class="member-pill-label">Signed In</span>
                <strong>${escapeHtml(authState.user.name)}</strong>
            </div>
            <button type="button" class="secondary-button auth-inline-button" data-auth-action="logout">Log Out</button>
        `;
        return;
    }

    authControls.innerHTML = `
        <button type="button" class="secondary-button auth-inline-button" data-auth-action="open-login">Log In</button>
        <button type="button" class="primary-button auth-inline-button" data-auth-action="open-signup">Sign Up</button>
    `;
}

function openAuthModal(tab) {
    if (!authModal) {
        window.location.href = "/index.html";
        return;
    }

    setAuthTab(tab);
    authModal.classList.remove("hidden");
    authModal.setAttribute("aria-hidden", "false");
    document.body.classList.add("modal-open");
}

function closeAuthModal() {
    if (!authModal) {
        return;
    }

    authModal.classList.add("hidden");
    authModal.setAttribute("aria-hidden", "true");
    document.body.classList.remove("modal-open");
    clearAuthMessage();
}

function setAuthTab(tab) {
    authState.activeTab = tab === "signup" ? "signup" : "login";
    document.querySelectorAll(".auth-tab").forEach((button) => {
        button.classList.toggle("active", button.getAttribute("data-auth-tab") === authState.activeTab);
    });

    if (loginForm) {
        loginForm.classList.toggle("hidden", authState.activeTab !== "login");
    }
    if (signupForm) {
        signupForm.classList.toggle("hidden", authState.activeTab !== "signup");
    }

    clearAuthMessage();
}

async function handleLogin(event) {
    event.preventDefault();

    const emailInput = document.getElementById("login-email");
    const email = emailInput.value.trim();

    if (!email) {
        showAuthMessage("Email is required.", "error");
        return;
    }

    setAuthLoading(true, "login");

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to log in.");
        }

        authState.user = data;
        syncStoredProfile();
        renderAuthControls();
        notifyAuthChange();
        showAuthMessage(data.message || "Logged in successfully.", "success");
        loginForm.reset();
        window.setTimeout(closeAuthModal, 600);
    } catch (error) {
        showAuthMessage(error.message, "error");
    } finally {
        setAuthLoading(false, "login");
    }
}

async function handleSignup(event) {
    event.preventDefault();

    const name = document.getElementById("signup-name").value.trim();
    const email = document.getElementById("signup-email").value.trim();
    const phone = document.getElementById("signup-phone").value.trim();

    if (!name || !email) {
        showAuthMessage("Name and email are required.", "error");
        return;
    }

    setAuthLoading(true, "signup");

    try {
        const response = await fetch("/auth/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, phone })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Unable to create account.");
        }

        authState.user = data;
        syncStoredProfile();
        renderAuthControls();
        notifyAuthChange();
        showAuthMessage(data.message || "Account created successfully.", "success");
        signupForm.reset();
        window.setTimeout(closeAuthModal, 700);
    } catch (error) {
        showAuthMessage(error.message, "error");
    } finally {
        setAuthLoading(false, "signup");
    }
}

async function logout() {
    try {
        await fetch("/auth/logout", { method: "POST" });
    } catch (error) {
        // Ignore logout network errors and still clear local state.
    }

    authState.user = null;
    localStorage.removeItem("coworking_user_id");
    localStorage.removeItem("coworking_name");
    localStorage.removeItem("coworking_email");
    localStorage.removeItem("coworking_phone");
    renderAuthControls();
    notifyAuthChange();

    if (window.location.pathname.endsWith("/bookings.html")) {
        window.location.reload();
    }
}

function showAuthMessage(message, type) {
    if (!authMessage) {
        return;
    }
    authMessage.textContent = message;
    authMessage.className = `message ${type}`;
}

function clearAuthMessage() {
    if (!authMessage) {
        return;
    }
    authMessage.textContent = "";
    authMessage.className = "message hidden";
}

function setAuthLoading(isLoading, formType) {
    const targetForm = formType === "signup" ? signupForm : loginForm;
    if (!targetForm) {
        return;
    }

    const button = targetForm.querySelector("button[type='submit']");
    if (!button) {
        return;
    }

    button.disabled = isLoading;
    if (formType === "signup") {
        button.textContent = isLoading ? "Creating..." : "Create Account";
    } else {
        button.textContent = isLoading ? "Logging In..." : "Log In";
    }
}

function syncStoredProfile() {
    if (!authState.user) {
        return;
    }
    localStorage.setItem("coworking_user_id", authState.user.userId);
    localStorage.setItem("coworking_name", authState.user.name || "");
    localStorage.setItem("coworking_email", authState.user.email || "");
    localStorage.setItem("coworking_phone", authState.user.phone || "");
}

function notifyAuthChange() {
    document.dispatchEvent(new CustomEvent("coworking:auth-changed", {
        detail: { user: authState.user }
    }));
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

window.CoworkingApp = {
    getCurrentUser() {
        return authState.user;
    },
    async refreshAuth() {
        await refreshAuthState();
        renderAuthControls();
        syncStoredProfile();
        notifyAuthChange();
        return authState.user;
    },
    openAuthModal
};
