document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("editForm");
    const id = new URLSearchParams(window.location.search).get("id");
    const token = getToken();

    const houseFields = document.getElementById("houseFields");
    const apartmentFields = document.getElementById("apartmentFields");
    const propertyTypeSelect = form.propertyType;

    propertyTypeSelect.addEventListener("change", () => {
        if (propertyTypeSelect.value === "HOUSE") {
            houseFields.style.display = "block";
            apartmentFields.style.display = "none";
        } else if (propertyTypeSelect.value === "APARTMENT") {
            houseFields.style.display = "none";
            apartmentFields.style.display = "block";
        } else {
            houseFields.style.display = "none";
            apartmentFields.style.display = "none";
        }
    });

    fetch(`/api/v1/properties/${id}`)
        .then(res => res.json())
        .then(data => {
            form.title.value = data.title;
            form.description.value = data.description;
            form.address.value = data.address;
            form.area.value = data.area;
            form.rooms.value = data.rooms;
            form.phoneNumber.value = data.phoneNumber;
            form.email.value = data.email;
            form.price.value = data.price;
            form.propertyType.value = data.propertyType;
            form.constructionType.value = data.constructionType;
            form.district.value = data.district;

            if (data.totalFloors) form.totalFloors.value = data.totalFloors;
            if (data.floor) form.floor.value = data.floor;

            propertyTypeSelect.dispatchEvent(new Event("change"));
        });

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            title: form.title.value,
            description: form.description.value,
            address: form.address.value,
            area: parseFloat(form.area.value),
            rooms: parseInt(form.rooms.value),
            phoneNumber: form.phoneNumber.value,
            email: form.email.value,
            price: parseFloat(form.price.value),
            propertyType: form.propertyType.value,
            constructionType: form.constructionType.value,
            district: form.district.value,
            totalFloors: form.totalFloors.value ? parseInt(form.totalFloors.value) : null,
            floor: form.floor.value ? parseInt(form.floor.value) : null
        };

        const res = await fetch(`/api/v1/properties/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + token
            },
            body: JSON.stringify(data)
        });

        const result = document.getElementById("result");
        if (res.ok) {
            result.textContent = "Property details updated successfully!";
        } else {
            result.textContent = "Error updating property.";
        }
    });
});