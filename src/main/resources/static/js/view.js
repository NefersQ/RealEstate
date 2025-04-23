document.addEventListener("DOMContentLoaded", () => {
    const id = new URLSearchParams(window.location.search).get("id");
    if (!id) {
        showError("No property ID provided");
        return;
    }

    loadProperty(id);

    const fullscreenBtn = document.getElementById("fullscreenBtn");
    if (fullscreenBtn) {
        fullscreenBtn.addEventListener("click", () => {
            const scene = document.querySelector("a-scene");
            if (scene.requestFullscreen) {
                scene.requestFullscreen();
            } else if (scene.webkitRequestFullscreen) {
                scene.webkitRequestFullscreen();
            } else if (scene.msRequestFullscreen) {
                scene.msRequestFullscreen();
            }
        });
    }
});

function loadProperty(id) {
    fetch('/api/v1/properties/' + id)
        .then(res => {
            if (!res.ok) {
                throw new Error("Failed to load property data");
            }
            return res.json();
        })
        .then(data => {
            displayProperty(data);
            setupGallery(data.imageFileUrls);
        })
        .catch(error => {
            document.getElementById('scene-wrapper').innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
            console.error(error);
        });
}

function displayProperty(data) {
    setTextContent('title', data.title || 'Untitled Property');
    setTextContent('address', data.address || 'No address provided');
    setTextContent('description', data.description || 'No description provided');
    setTextContent('area', data.area || '-');
    setTextContent('rooms', data.rooms || '-');
    setTextContent('district', formatEnumValue(data.district) || '-');
    setTextContent('constructionType', formatEnumValue(data.constructionType) || '-');
    setTextContent('propertyType', formatEnumValue(data.propertyType) || '-');
    setTextContent('pricePerSqm', data.pricePerSqm || '-');
    setTextContent('price', formatCurrency(data.price));
    setTextContent('phoneNumber', data.phoneNumber || '-');
    setTextContent('email', data.email || '-');

    const floorInfo = document.getElementById('floorInfo');
    if (floorInfo) {
        if (data.propertyType === "HOUSE" && data.totalFloors) {
            floorInfo.innerHTML = `
                <div class="d-flex align-items-center">
                    <i class="bi bi-layers text-primary me-2"></i>
                    <div>
                        <strong>Total Floors:</strong> ${data.totalFloors}
                    </div>
                </div>
            `;
        } else if (data.propertyType === "APARTMENT" && data.floor !== undefined) {
            floorInfo.innerHTML = `
                <div class="d-flex align-items-center">
                    <i class="bi bi-layers text-primary me-2"></i>
                    <div>
                        <strong>Floor:</strong> ${data.floor}
                    </div>
                </div>
            `;
        } else {
            floorInfo.style.display = "none";
        }
    }

    if (data.modelFileUrl) {
        document.getElementById('modelEntity').setAttribute('gltf-model', data.modelFileUrl);
    } else {
        document.getElementById('scene-wrapper').innerHTML = '<div class="alert alert-warning">3D model not available.</div>';
    }
}

function setupGallery(images) {
    const wrapper = document.getElementById("swiperWrapper");
    wrapper.innerHTML = "";

    if (images && images.length > 0) {
        images.forEach(url => {
            const slide = document.createElement("div");
            slide.className = "swiper-slide";
            slide.innerHTML = `
                <div class="ratio ratio-16x9">
                    <img src="${url}" class="img-fluid" alt="Property image" 
                         style="object-fit: contain; background-color: #f8f9fa;">
                </div>
            `;
            wrapper.appendChild(slide);
        });
    } else {
        wrapper.innerHTML = `
            <div class="swiper-slide">
                <div class="ratio ratio-16x9">
                    <img src="https://via.placeholder.com/800x300?text=No+Images" class="img-fluid" 
                         alt="No images available" style="object-fit: contain; background-color: #f8f9fa;">
                </div>
            </div>
        `;
    }

    new Swiper("#swiperGallery", {
        loop: images && images.length > 1,
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev"
        },
        pagination: {
            el: ".swiper-pagination",
            type: "fraction"
        },
        slidesPerView: 1,
        spaceBetween: 10
    });
}

function formatEnumValue(value) {
    if (!value) return null;

    return value.charAt(0) + value.slice(1).toLowerCase();
}

function formatCurrency(value) {
    if (value === undefined || value === null) return "- €";

    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " €";
}

function setTextContent(id, content) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = content;
    }
}

function showError(message) {
    document.getElementById('scene-wrapper').innerHTML = `
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-triangle-fill text-danger me-2"></i>
            ${message || "An unexpected error occurred."}
        </div>
    `;
}