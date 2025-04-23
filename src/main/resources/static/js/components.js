function renderNavbar() {
    const navbar = document.createElement('nav');
    navbar.className = 'navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top';

    navbar.innerHTML = `
    <div class="container">
      <a class="navbar-brand d-flex align-items-center" href="/index.html">
        <i class="bi bi-house-door-fill me-2 text-primary"></i>
        <span>RealEstateVR</span>
      </a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto" id="nav-auth">
        </ul>
      </div>
    </div>
  `;

    document.body.insertBefore(navbar, document.body.firstChild);

    renderAuthNav();
}

function renderFooter() {
    const footer = document.createElement('footer');
    footer.className = 'bg-white py-4 mt-5 border-top';

    footer.innerHTML = `
    <div class="container">
      <div class="row">
        <div class="col-md-6">
          <a class="d-flex align-items-center text-decoration-none" href="/index.html">
            <i class="bi bi-house-door-fill me-2 text-primary"></i>
            <span class="fw-bold text-dark">RealEstateVR</span>
          </a>
          <p class="text-muted small mt-2">
            New virtual reality real estate service,<br>
            Find your dream property with us!
          </p>
        </div>
        <div class="col-md-3">
          <h6 class="fw-bold mb-3">Quick Links</h6>
          <ul class="list-unstyled">
            <li class="mb-2"><a href="/index.html" class="text-decoration-none text-secondary">Home</a></li>
            <li class="mb-2"><a href="/index.html" class="text-decoration-none text-secondary">About Us</a></li>
            <li class="mb-2"><a href="/index.html" class="text-decoration-none text-secondary">Contact</a></li>
            <li class="mb-2"><a href="/index.html" class="text-decoration-none text-secondary">FAQ</a></li>
          </ul>
        </div>
        <div class="col-md-3">
          <h6 class="fw-bold mb-3">Contact Us</h6>
          <ul class="list-unstyled">
            <li class="mb-2"><i class="bi bi-envelope me-2"></i> info@realestatevr.com</li>
            <li class="mb-2"><i class="bi bi-telephone me-2"></i> +371 1111 1111</li>
            <li class="mb-2"><i class="bi bi-geo-alt me-2"></i> Lauvas iela 2, Rīga, LV-1019</li>
          </ul>
        </div>
      </div>
      <hr class="my-4">
      <div class="d-flex justify-content-between align-items-center">
        <p class="text-muted small mb-0">© 2025 RealEstateVR. All rights reserved.</p>
      </div>
    </div>
  `;

    document.body.appendChild(footer);
}

document.addEventListener('DOMContentLoaded', () => {
    const needsStandardLayout = !document.body.classList.contains('no-standard-layout');

    if (needsStandardLayout) {
        renderNavbar();
        renderFooter();
    }
});