document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    if (!loginForm) return;

    const params = new URLSearchParams(window.location.search);
    const redirectUrl = params.get('redirect') || '/index.html';

    loginForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const form = e.target;
        const data = {
            usernameOrEmail: form.username.value,
            password: form.password.value
        };

        try {
            const response = await fetch("/api/v1/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("Invalid credentials");
            }

            const responseData = await response.json();
            saveToken(responseData.token);

            window.location.href = redirectUrl;
        } catch (err) {
            document.getElementById("error").innerText = err.message;
        }
    });
});