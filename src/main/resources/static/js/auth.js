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