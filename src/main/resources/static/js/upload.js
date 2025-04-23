document.addEventListener("DOMContentLoaded", () => {
    const propertyTypeSelect = document.getElementById("propertyTypeSelect");
    const houseFields = document.getElementById("houseFields");
    const apartmentFields = document.getElementById("apartmentFields");

    propertyTypeSelect.addEventListener("change", function() {
        const selected = this.value;
        if (selected === "HOUSE") {
            houseFields.style.display = "block";
            apartmentFields.style.display = "none";
            document.querySelector('input[name="totalFloors"]').setAttribute('required', '');
            document.querySelector('input[name="floor"]').removeAttribute('required');
        } else if (selected === "APARTMENT") {
            houseFields.style.display = "none";
            apartmentFields.style.display = "block";
            document.querySelector('input[name="totalFloors"]').removeAttribute('required');
            document.querySelector('input[name="floor"]').setAttribute('required', '');
        } else {
            houseFields.style.display = "none";
            apartmentFields.style.display = "none";
            document.querySelector('input[name="totalFloors"]').removeAttribute('required');
            document.querySelector('input[name="floor"]').removeAttribute('required');
        }
    });

    const imageInput = document.querySelector('input[name="images"]');
    const preview = document.getElementById("preview");

    imageInput.addEventListener("change", function() {
        preview.innerHTML = "";
        if (this.files.length === 0) {
            preview.classList.add('d-none');
            return;
        }

        preview.classList.remove('d-none');
        for (const file of this.files) {
            const imgContainer = document.createElement("div");
            imgContainer.classList.add("position-relative");

            const img = document.createElement("img");
            img.src = URL.createObjectURL(file);
            img.classList.add("rounded", "shadow-sm");
            img.style.width = "120px";
            img.style.height = "90px";
            img.style.objectFit = "cover";

            const label = document.createElement("small");
            label.classList.add("d-block", "text-truncate", "text-muted", "mt-1");
            label.style.maxWidth = "120px";
            label.textContent = file.name;

            imgContainer.appendChild(img);
            imgContainer.appendChild(label);
            preview.appendChild(imgContainer);
        }
    });

    const form = document.getElementById("uploadForm");
    const resultDiv = document.getElementById("result");

    form.addEventListener("submit", async function(e) {
        e.preventDefault();

        const submitBtn = this.querySelector('button[type="submit"]');
        const originalBtnText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Uploading...';

        resultDiv.classList.add('d-none');

        const formData = new FormData();

        formData.append("title", this.title.value);
        formData.append("description", this.description.value);
        formData.append("address", this.address.value);
        formData.append("area", this.area.value);
        formData.append("rooms", this.rooms.value);
        formData.append("propertyType", this.propertyType.value);
        formData.append("constructionType", this.constructionType.value);
        formData.append("district", this.district.value);
        formData.append("phoneNumber", this.phoneNumber.value);
        formData.append("email", this.email.value);
        formData.append("price", this.price.value);

        if (this.propertyType.value === "HOUSE" && this.totalFloors.value) {
            formData.append("totalFloors", this.totalFloors.value);
        } else if (this.propertyType.value === "APARTMENT" && this.floor.value) {
            formData.append("floor", this.floor.value);
        }

        if (this.file.files.length > 0) {
            formData.append("file", this.file.files[0]);
        }

        for (const image of this.images.files) {
            formData.append("images", image);
        }

        try {
            const token = getToken();
            if (!token) {
                throw new Error("You must be logged in to upload properties.");
            }

            const response = await fetch("/api/v1/properties/upload-direct", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + token
                },
                body: formData
            });

            if (response.ok) {
                resultDiv.classList.remove('d-none', 'alert-danger');
                resultDiv.classList.add('alert-success');
                resultDiv.innerHTML = '<i class="bi bi-check-circle-fill me-2"></i> Property uploaded successfully!';

                form.reset();
                preview.innerHTML = "";
                preview.classList.add('d-none');

                setTimeout(() => window.location.href = "/index.html", 1500);
            } else {
                const errorText = await response.text();
                throw new Error(errorText || "Failed to upload property. Please try again.");
            }
        } catch (error) {
            resultDiv.classList.remove('d-none', 'alert-success');
            resultDiv.classList.add('alert-danger');
            resultDiv.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-2"></i> ${error.message}`;
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalBtnText;
        }
    });

    (() => {
        'use strict';

        const forms = document.querySelectorAll('.needs-validation');

        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }

                form.classList.add('was-validated');
            }, false);
        });
    })();
});