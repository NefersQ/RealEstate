let allProperties = [];
let isLoading = true;

document.addEventListener("DOMContentLoaded", () => {
    showLoadingState();
    loadProperties();

    document.querySelectorAll('select, input').forEach(el => {
        el.addEventListener('input', applyFiltersAndSort);
    });

    document.getElementById('resetFilters').addEventListener('click', resetFilters);
});

function showLoadingState() {
    const grid = document.getElementById('propertyGrid');
    if (!grid) return;

    grid.innerHTML = "";

    for (let i = 0; i < 6; i++) {
        const card = document.createElement("div");
        card.className = "col-md-6 col-lg-4 mb-4";
        card.innerHTML = `
            <div class="property-card h-100 loading">
                <div class="card-img-top bg-light"></div>
                <div class="card-body d-flex flex-column">
                    <div class="bg-light w-75 py-3 mb-2 rounded"></div>
                    <div class="bg-light w-50 py-2 mb-2 rounded"></div>
                    <div class="bg-light w-75 py-2 mb-3 rounded"></div>
                    <div class="bg-light w-25 py-3 mb-2 rounded"></div>
                </div>
            </div>
        `;
        grid.appendChild(card);
    }
}

function loadProperties() {
    fetch('/api/v1/properties')
        .then(response => {
            if (!response.ok) throw new Error("Failed to load properties: " + response.status);
            return response.json();
        })
        .then(data => {
            allProperties = data;
            isLoading = false;
            applyFiltersAndSort();
        })
        .catch(error => {
            isLoading = false;
            showError(error);
        });
}

function resetFilters() {
    const elements = {
        'districtFilter': '',
        'typeFilter': '',
        'sortFilter': 'priceAsc',
        'constructionBrick': false,
        'constructionPanel': false,
        'constructionMonolith': false,
        'priceMin': '',
        'priceMax': '',
        'pricePerSqmMin': '',
        'pricePerSqmMax': '',
        'areaMin': '',
        'areaMax': '',
        'has3DModel': false
    };

    Object.keys(elements).forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            if (typeof elements[id] === 'boolean') {
                element.checked = elements[id];
            } else {
                element.value = elements[id];
            }
        }
    });

    applyFiltersAndSort();
}

function applyFiltersAndSort() {
    if (isLoading) return;

    const getValue = (id, defaultValue) => {
        const element = document.getElementById(id);
        return element ? element.value : defaultValue;
    };

    const getChecked = (id, defaultValue) => {
        const element = document.getElementById(id);
        return element ? element.checked : defaultValue;
    };

    const getNumber = (id, defaultMin, defaultMax) => {
        const element = document.getElementById(id);
        if (!element) return defaultMin;
        const value = parseFloat(element.value);
        return isNaN(value) ? (defaultMax !== undefined ? defaultMax : defaultMin) : value;
    };

    const district = getValue('districtFilter', '');
    const type = getValue('typeFilter', '');
    const sort = getValue('sortFilter', 'priceAsc');

    const priceMin = getNumber('priceMin', 0);
    const priceMax = getNumber('priceMax', 0, Number.MAX_SAFE_INTEGER);

    const pricePerSqmMin = getNumber('pricePerSqmMin', 0);
    const pricePerSqmMax = getNumber('pricePerSqmMax', 0, Number.MAX_SAFE_INTEGER);

    const areaMin = getNumber('areaMin', 0);
    const areaMax = getNumber('areaMax', 0, Number.MAX_SAFE_INTEGER);

    const constructionTypes = [];
    if (getChecked('constructionBrick', false)) constructionTypes.push('BRICK');
    if (getChecked('constructionPanel', false)) constructionTypes.push('PANEL');
    if (getChecked('constructionMonolith', false)) constructionTypes.push('MONOLITH');

    const has3DModel = getChecked('has3DModel', false);

    let filtered = [...allProperties];

    if (district) filtered = filtered.filter(p => p.district === district);
    if (type) filtered = filtered.filter(p => p.propertyType === type);
    if (constructionTypes.length > 0) filtered = filtered.filter(p => p.constructionType && constructionTypes.includes(p.constructionType));
    if (has3DModel) filtered = filtered.filter(p => p.modelFileUrl);

    filtered = filtered.filter(p =>
        p.price >= priceMin &&
        p.price <= priceMax &&
        p.area >= areaMin &&
        p.area <= areaMax &&
        (p.pricePerSqm || 0) >= pricePerSqmMin &&
        (p.pricePerSqm || 0) <= pricePerSqmMax
    );

    if (sort === 'priceAsc') filtered.sort((a, b) => a.price - b.price);
    else if (sort === 'priceDesc') filtered.sort((a, b) => b.price - a.price);
    else if (sort === 'areaAsc') filtered.sort((a, b) => a.area - b.area);
    else if (sort === 'areaDesc') filtered.sort((a, b) => b.area - a.area);

    displayProperties(filtered);

    updateFilterCount(filtered.length);
}

function updateFilterCount(count) {
    let heading = document.querySelector('.properties-header');
    if (!heading) {
        heading = document.createElement('div');
        heading.className = 'properties-header mb-3';
        const grid = document.getElementById('propertyGrid');
        if (grid) {
            grid.parentNode.insertBefore(heading, grid);
        }
    }

    if (heading) {
        heading.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0">Properties <span class="badge bg-primary">${count}</span></h4>
            </div>
        `;
    }
}

function displayProperties(properties) {
    const grid = document.getElementById('propertyGrid');
    if (!grid) return;

    grid.innerHTML = "";

    if (properties.length === 0) {
        grid.innerHTML = `
            <div class="col-12 empty-state">
                <i class="bi bi-search" style="font-size: 3rem; color: #ccc;"></i>
                <p>No properties match your search criteria.<br>Try adjusting your filters.</p>
                <button class="btn btn-outline-primary mt-3" id="emptyStateReset">Reset Filters</button>
            </div>
        `;
        const resetButton = document.getElementById('emptyStateReset');
        if (resetButton) {
            resetButton.addEventListener('click', resetFilters);
        }
        return;
    }

    properties.forEach(property => {
        if (!property) return;

        const image = property.imageFileUrls && property.imageFileUrls.length ?
            property.imageFileUrls[0] : "https://via.placeholder.com/300x200?text=No+Image";

        const card = document.createElement("div");
        card.className = "col-md-6 col-lg-4 mb-4";

        const formattedPrice = property.price ?
            property.price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") : "0";

        const pricePerSqm = property.price && property.area ?
            Math.round(property.price / property.area) : 0;

        const formattedPricePerSqm = pricePerSqm.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");

        const propertyTypeDisplay = property.propertyType === 'HOUSE' ? 'House' : 'Apartment';

        let districtDisplay = "Unknown";
        if (property.district) {
            districtDisplay = property.district.charAt(0) +
                (property.district.slice(1).toLowerCase() || '');
        }

        let constructionTypeDisplay = "Unknown";
        if (property.constructionType) {
            constructionTypeDisplay = property.constructionType.charAt(0) +
                (property.constructionType.slice(1).toLowerCase() || '');
        }

        card.innerHTML = `
            <div class="property-card h-100" data-id="${property.id || ''}">
                <div class="position-relative">
                    <img src="${image}" class="card-img-top" alt="${property.title || 'Property'}">
                    ${property.propertyType === 'HOUSE' ?
            '<span class="position-absolute top-0 start-0 m-2 badge bg-dark">House</span>' :
            '<span class="position-absolute top-0 start-0 m-2 badge bg-info">Apartment</span>'}
                </div>
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${property.title || 'Untitled Property'}</h5>
                    <p class="card-text text-muted mb-2">
                        <i class="bi bi-geo-alt"></i> ${property.address || 'No address'}, ${districtDisplay}
                    </p>
                    <div class="d-flex mb-2">
                        <div class="me-3">
                            <span class="d-block text-muted small">Area</span>
                            <span class="fw-bold">${property.area || 0} m²</span>
                        </div>
                        <div class="me-3">
                            <span class="d-block text-muted small">Rooms</span>
                            <span class="fw-bold">${property.rooms || 0}</span>
                        </div>
                        <div>
                            <span class="d-block text-muted small">Type</span>
                            <span class="fw-bold">${constructionTypeDisplay}</span>
                        </div>
                    </div>
                    <p class="property-price mt-auto mb-2">${formattedPrice} €</p>
                    <p class="small text-muted">${formattedPricePerSqm} €/m²</p>
                    <div class="mt-auto d-flex justify-content-between align-items-center">
                        <button class="btn btn-sm btn-primary">View Details</button>
                        ${property.modelFileUrl ?
            '<span class="model-available">3D Model</span>' :
            '<span class="text-muted small">No 3D model</span>'}
                    </div>
                </div>
            </div>
        `;

        card.querySelector(".property-card").addEventListener("click", (e) => {
            if (e.target.tagName !== 'BUTTON') {
                window.location.href = `/view.html?id=${property.id || ''}`;
            } else {
                e.preventDefault();
                window.location.href = `/view.html?id=${property.id || ''}`;
            }
        });

        grid.appendChild(card);
    });
}

function showError(error) {
    const grid = document.getElementById('propertyGrid');
    if (!grid) return;

    grid.innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                ${error.message || 'An error occurred'}
            </div>
            <button class="btn btn-outline-primary mt-3" onclick="location.reload()">Try Again</button>
        </div>
    `;
    console.error(error);
}