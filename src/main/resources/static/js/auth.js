function saveToken(token) {
    localStorage.setItem('token', token);
}

function getToken() {
    return localStorage.getItem('token');
}

function isLoggedIn() {
    return getToken() !== null;
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login.html';
}

function renderAuthNav() {
    const navs = document.querySelectorAll("#nav-auth");
    if (navs.length === 0) return;

    const authLinks = isLoggedIn()
        ? `
            <li class="nav-item"><a class="nav-link" href="/dashboard.html">Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="/upload.html">Upload</a></li>
            <li class="nav-item"><a class="nav-link" href="#" onclick="logout()">Logout</a></li>
          `
        : `
            <li class="nav-item"><a class="nav-link" href="/login.html">Login</a></li>
            <li class="nav-item"><a class="nav-link" href="/register.html">Register</a></li>
          `;

    navs.forEach(nav => {
        nav.innerHTML = authLinks;
    });
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return false;
    }
    return true;
}

document.addEventListener("DOMContentLoaded", () => {
    const requiresAuth = document.body.hasAttribute('data-requires-auth');

    if (requiresAuth && !isLoggedIn()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
    }

    if (typeof window.renderNavbar !== 'function') {
        renderAuthNav();
    }
});