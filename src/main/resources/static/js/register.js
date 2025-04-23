document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");

    registerForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const form = e.target;
        const data = {
            name: form.name.value,
            surname: form.surname.value,
            username: form.username.value,
            email: form.email.value,
            password: form.password.value
        };

        try {
            const response = await fetch("/api/v1/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("Registration failed");
            }

            const message = await response.text();
            document.getElementById("message").innerText = message + ". You can now login.";
        } catch (err) {
            document.getElementById("message").innerText = err.message;
        }
    });
});