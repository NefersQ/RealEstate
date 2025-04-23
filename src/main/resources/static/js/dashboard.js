let showingProfile = true;

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("nav-profile").addEventListener("click", showProfile);
    document.getElementById("nav-properties").addEventListener("click", showProperties);
    loadProfile();
    loadMyProperties();

    const editProfileModal = document.getElementById("editProfileForm");
    if (editProfileModal) {
        editProfileModal.addEventListener("submit", submitProfileEdit);
    }

    const modalElement = document.getElementById('editProfileModal');
    if (modalElement) {
        modalElement.addEventListener('show.bs.modal', function (event) {
            loadUserDataIntoModal();
        });
    }
});

async function loadUserDataIntoModal() {
    try {
        const token = getToken();
        const res = await fetch("/api/v1/users/me", {
            headers: { Authorization: "Bearer " + token }
        });

        if (res.ok) {
            const data = await res.json();
            document.getElementById("modalName").value = data.name;
            document.getElementById("modalSurname").value = data.surname;
            document.getElementById("modalUsername").value = data.username;
            document.getElementById("modalEmail").value = data.email;
            document.getElementById("modalPassword").value = "";
            document.getElementById("modalMessage").textContent = "";
        }
    } catch (error) {
        console.error("Error loading user data:", error);
    }
}

async function loadProfile() {
    const token = getToken();
    const res = await fetch("/api/v1/users/me", { headers: { Authorization: "Bearer " + token } });
    if (!res.ok) return window.location.href = "/login.html";
    const data = await res.json();
    document.getElementById("displayName").textContent = data.name + " " + data.surname;
    document.getElementById("displayEmail").textContent = data.email;
    document.getElementById("displayFirstName").textContent = data.name;
    document.getElementById("displayLastName").textContent = data.surname;
    document.getElementById("displayUsername").textContent = data.username;
    document.getElementById("avatarDisplay").textContent = data.name.charAt(0).toUpperCase();
}

async function submitProfileEdit(e) {
    e.preventDefault();
    const token = getToken();
    const payload = {
        name: document.getElementById("modalName").value,
        surname: document.getElementById("modalSurname").value,
        username: document.getElementById("modalUsername").value,
        email: document.getElementById("modalEmail").value,
    };

    try {
        const res = await fetch("/api/v1/users/me", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + token
            },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            window.location.reload();
        } else {
            const errorText = await res.text();
            document.getElementById("modalMessage").textContent = errorText || "Ошибка обновления профиля";
        }
    } catch (error) {
        document.getElementById("modalMessage").textContent = "Ошибка соединения с сервером";
    }
}

async function loadMyProperties() {
    const token = getToken();
    const res = await fetch("/api/v1/properties/my", { headers: { Authorization: "Bearer " + token } });
    const data = await res.json();
    const grid = document.getElementById("myPropertiesGrid");
    grid.innerHTML = "";
    if (!data.length) {
        grid.innerHTML = "<p>You have no properties yet.</p>";
        return;
    }
    data.forEach(p => {
        const col = document.createElement("div");
        col.className = "col-md-6 col-lg-4 mb-4";
        col.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${p.imageFileUrls?.[0]||'https://via.placeholder.com/300x200'}" class="card-img-top">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${p.title}</h5>
                    <p class="card-text text-muted">${p.address}</p>
                    <div class="mt-auto">
                        <button class="btn btn-sm btn-outline-primary me-2" onclick="editProperty(${p.id})">Edit</button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteProperty(${p.id})">Delete</button>
                    </div>
                </div>
            </div>`;
        grid.appendChild(col);
    });
}

function showProfile() {
    if (!showingProfile) {
        document.getElementById("profile-view").classList.remove("d-none");
        document.getElementById("properties-view").classList.add("d-none");
        toggleNav("nav-profile","nav-properties");
        showingProfile = true;
    }
}

function showProperties() {
    if (showingProfile) {
        document.getElementById("properties-view").classList.remove("d-none");
        document.getElementById("profile-view").classList.add("d-none");
        toggleNav("nav-properties","nav-profile");
        showingProfile = false;
    }
}

function toggleNav(activeId, inactiveId) {
    document.getElementById(activeId).classList.add("active");
    document.getElementById(inactiveId).classList.remove("active");
}

function editProperty(id) {
    window.location.href = `/edit-property.html?id=${id}`;
}

async function deleteProperty(id) {
    if (!confirm("Are you sure you want to delete this property?")) return;
    const token = getToken();
    const res = await fetch(`/api/v1/properties/${id}`, {
        method: "DELETE",
        headers: { Authorization: "Bearer " + token }
    });
    if (res.ok) loadMyProperties();
    else alert("Error deleting property.");
}
