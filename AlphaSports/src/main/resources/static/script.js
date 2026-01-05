// Cart Management
let cart = JSON.parse(localStorage.getItem("alphasports_cart")) || []

function saveCart() {
  localStorage.setItem("alphasports_cart", JSON.stringify(cart))
  updateCartCount()
}

function updateCartCount() {
  const cartCount = document.getElementById("cartCount")
  if (cartCount) {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0)
    cartCount.textContent = totalItems
    cartCount.style.display = totalItems > 0 ? "flex" : "none"
  }
}

function addToCart(productId, size = null, quantity = 1) {
  const product = window.products.find((p) => p.id === productId) // Assuming products is declared globally or passed to the script
  if (!product) return

  const existingItem = cart.find((item) => item.id === productId && item.size === size)

  if (existingItem) {
    existingItem.quantity += quantity
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      brand: product.brand,
      price: product.price,
      image: product.image,
      size: size,
      quantity: quantity,
    })
  }

  saveCart()
  showToast("Produto adicionado ao carrinho!", "success")
}

function removeFromCart(productId, size) {
  cart = cart.filter((item) => !(item.id === productId && item.size === size))
  saveCart()
  window.renderCart() // Assuming renderCart is declared globally or passed to the script
}

function updateCartQuantity(productId, size, quantity) {
  const item = cart.find((item) => item.id === productId && item.size === size)
  if (item) {
    item.quantity = Math.max(1, quantity)
    saveCart()
    window.renderCart() // Assuming renderCart is declared globally or passed to the script
  }
}

// Toast Notification
function showToast(message, type = "success") {
  const existingToast = document.querySelector(".toast")
  if (existingToast) {
    existingToast.remove()
  }

  const toast = document.createElement("div")
  toast.className = `toast ${type}`
  toast.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            ${
              type === "success"
                ? '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline>'
                : '<circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line>'
            }
        </svg>
        <span>${message}</span>
    `
  document.body.appendChild(toast)

  setTimeout(() => toast.classList.add("show"), 10)
  setTimeout(() => {
    toast.classList.remove("show")
    setTimeout(() => toast.remove(), 300)
  }, 3000)
}

// Product Card HTML
function createProductCard(product) {
  const hasDiscount = product.discount > 0
  return `
        <div class="product-card">
            <div class="product-image">
                <a href="produto.html?id=${product.id}">
                    <img src="${product.image}" alt="${product.name}">
                </a>
                ${hasDiscount ? `<span class="product-badge">-${product.discount}%</span>` : ""}
                ${product.isNew ? `<span class="product-badge new">Novo</span>` : ""}
                <button class="wishlist-btn" aria-label="Adicionar aos favoritos">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"></path></svg>
                </button>
            </div>
            <div class="product-info">
                <span class="product-brand">${product.brand}</span>
                <h3 class="product-name">
                    <a href="produto.html?id=${product.id}">${product.name}</a>
                </h3>
                <div class="product-rating">
                    <div class="stars">
                        ${generateStars(product.rating)}
                    </div>
                    <span class="rating-count">(${product.reviews})</span>
                </div>
                <div class="product-price">
                    ${hasDiscount ? `<span class="original-price">R$ ${product.originalPrice.toFixed(2).replace(".", ",")}</span>` : ""}
                    <span class="current-price ${hasDiscount ? "discount-price" : ""}">R$ ${product.price.toFixed(2).replace(".", ",")}</span>
                    <span class="installments">ou 10x de R$ ${(product.price / 10).toFixed(2).replace(".", ",")} sem juros</span>
                </div>
                <button class="add-to-cart-btn" onclick="addToCart(${product.id}, '${product.sizes[0]}')">
                    Adicionar ao Carrinho
                </button>
            </div>
        </div>
    `
}

function generateStars(rating) {
  let stars = ""
  for (let i = 1; i <= 5; i++) {
    if (i <= rating) {
      stars +=
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>'
    } else {
      stars +=
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>'
    }
  }
  return stars
}

// Initialize Page
document.addEventListener("DOMContentLoaded", () => {
  updateCartCount()

  // Mobile Menu
  const mobileMenuBtn = document.getElementById("mobileMenuBtn")
  const mobileMenu = document.getElementById("mobileMenu")
  const closeMenu = document.getElementById("closeMenu")

  if (mobileMenuBtn && mobileMenu) {
    mobileMenuBtn.addEventListener("click", () => {
      mobileMenu.classList.add("active")
      document.body.style.overflow = "hidden"
    })

    closeMenu?.addEventListener("click", () => {
      mobileMenu.classList.remove("active")
      document.body.style.overflow = ""
    })
  }

  // Banner Slider
  const slides = document.querySelectorAll(".banner-slide")
  const dots = document.querySelectorAll(".dot")
  const prevBtn = document.getElementById("prevSlide")
  const nextBtn = document.getElementById("nextSlide")
  let currentSlide = 0

  function showSlide(index) {
    slides.forEach((slide) => slide.classList.remove("active"))
    dots.forEach((dot) => dot.classList.remove("active"))

    currentSlide = (index + slides.length) % slides.length
    slides[currentSlide]?.classList.add("active")
    dots[currentSlide]?.classList.add("active")
  }

  if (slides.length > 0) {
    prevBtn?.addEventListener("click", () => showSlide(currentSlide - 1))
    nextBtn?.addEventListener("click", () => showSlide(currentSlide + 1))

    dots.forEach((dot, index) => {
      dot.addEventListener("click", () => showSlide(index))
    })

    // Auto slide
    setInterval(() => showSlide(currentSlide + 1), 5000)
  }

  // Render Best Sellers
  const bestSellersGrid = document.getElementById("bestSellersGrid")
  if (bestSellersGrid) {
    const bestSellers = window.products.filter((p) => p.isBestSeller).slice(0, 4) // Assuming products is declared globally or passed to the script
    bestSellersGrid.innerHTML = bestSellers.map(createProductCard).join("")
  }

  // Render New Arrivals
  const newArrivalsGrid = document.getElementById("newArrivalsGrid")
  if (newArrivalsGrid) {
    const newArrivals = window.products.filter((p) => p.isNew).slice(0, 4) // Assuming products is declared globally or passed to the script
    if (newArrivals.length < 4) {
      const remaining = window.products.filter((p) => !p.isNew).slice(0, 4 - newArrivals.length) // Assuming products is declared globally or passed to the script
      newArrivals.push(...remaining)
    }
    newArrivalsGrid.innerHTML = newArrivals.map(createProductCard).join("")
  }

  // Search
  const searchInput = document.getElementById("searchInput")
  if (searchInput) {
    searchInput.addEventListener("keypress", (e) => {
      if (e.key === "Enter") {
        const query = searchInput.value.trim()
        if (query) {
          window.location.href = `produtos.html?busca=${encodeURIComponent(query)}`
        }
      }
    })
  }
})
