// Cart Management
let cart = JSON.parse(localStorage.getItem("alphasports_cart")) || []
let products = [] // Agora será carregado do banco de dados

// Carregar produtos do banco de dados
async function carregarProdutos(categoria = null) {
  try {
    let url = 'http://localhost:2022/api/produtos'

    // Se uma categoria específica foi selecionada, adiciona à URL
    if (categoria && categoria !== 'todos') {
      url += `?categoria=${categoria}`
    }

    const response = await fetch(url, {
      credentials: 'include'
    })

    if (response.ok) {
      products = await response.json()
      console.log('Produtos carregados:', products)
      return products
    } else {
      console.error('Erro ao carregar produtos')
      return []
    }
  } catch (error) {
    console.error('Erro ao conectar com o servidor:', error)
    return []
  }
}

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
  const product = products.find((p) => p.id === productId)
  if (!product) return

  const existingItem = cart.find((item) => item.id === productId && item.size === size)

  if (existingItem) {
    existingItem.quantity += quantity
  } else {
    cart.push({
      id: product.id,
      name: product.nome || product.name,
      brand: product.marca || product.brand,
      price: product.preco || product.price,
      image: product.imagem || product.image,
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
  if (typeof window.renderCart === 'function') {
    window.renderCart()
  }
}

function updateCartQuantity(productId, size, quantity) {
  const item = cart.find((item) => item.id === productId && item.size === size)
  if (item) {
    item.quantity = Math.max(1, quantity)
    saveCart()
    if (typeof window.renderCart === 'function') {
      window.renderCart()
    }
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

// Product Card HTML - Adaptado para dados do banco
function createProductCard(product) {
  // Adapta os nomes dos campos do banco para o formato esperado
  const nome = product.nome || product.name
  const marca = product.marca || product.brand
  const preco = product.preco || product.price
  const precoOriginal = product.precoOriginal || product.originalPrice
  const desconto = product.desconto || product.discount || 0
  const imagem = product.imagem || product.image
  const avaliacao = product.avaliacao || product.rating || 4.5
  const numeroAvaliacoes = product.numeroAvaliacoes || product.reviews || 0
  const tamanhos = product.tamanhos || product.sizes || ['Único']
  const isNew = product.novo || product.isNew || false

  const hasDiscount = desconto > 0

  return `
        <div class="product-card">
            <div class="product-image">
                <a href="produto.html?id=${product.id}">
                    <img src="${imagem}" alt="${nome}" onerror="this.src='/images/placeholder.png'">
                </a>
                ${hasDiscount ? `<span class="product-badge">-${desconto}%</span>` : ""}
                ${isNew ? `<span class="product-badge new">Novo</span>` : ""}
                <button class="wishlist-btn" aria-label="Adicionar aos favoritos">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"></path></svg>
                </button>
            </div>
            <div class="product-info">
                <span class="product-brand">${marca}</span>
                <h3 class="product-name">
                    <a href="produto.html?id=${product.id}">${nome}</a>
                </h3>
                <div class="product-rating">
                    <div class="stars">
                        ${generateStars(avaliacao)}
                    </div>
                    <span class="rating-count">(${numeroAvaliacoes})</span>
                </div>
                <div class="product-price">
                    ${hasDiscount && precoOriginal ? `<span class="original-price">R$ ${precoOriginal.toFixed(2).replace(".", ",")}</span>` : ""}
                    <span class="current-price ${hasDiscount ? "discount-price" : ""}">R$ ${preco.toFixed(2).replace(".", ",")}</span>
                    <span class="installments">ou 10x de R$ ${(preco / 10).toFixed(2).replace(".", ",")} sem juros</span>
                </div>
                <button class="add-to-cart-btn" onclick="addToCart(${product.id}, '${tamanhos[0]}')">
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
document.addEventListener("DOMContentLoaded", async () => {
  updateCartCount()

  // Carregar produtos do banco de dados
  await carregarProdutos()

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

  // Render Best Sellers - agora do banco de dados
  const bestSellersGrid = document.getElementById("bestSellersGrid")
  if (bestSellersGrid) {
    const bestSellers = products.filter((p) => p.maisVendido || p.isBestSeller).slice(0, 4)
    if (bestSellers.length > 0) {
      bestSellersGrid.innerHTML = bestSellers.map(createProductCard).join("")
    } else {
      // Se não houver produtos marcados como mais vendidos, mostra os 4 primeiros
      bestSellersGrid.innerHTML = products.slice(0, 4).map(createProductCard).join("")
    }
  }

  // Render New Arrivals - agora do banco de dados
  const newArrivalsGrid = document.getElementById("newArrivalsGrid")
  if (newArrivalsGrid) {
    let newArrivals = products.filter((p) => p.novo || p.isNew).slice(0, 4)
    if (newArrivals.length < 4) {
      const remaining = products.filter((p) => !(p.novo || p.isNew)).slice(0, 4 - newArrivals.length)
      newArrivals = [...newArrivals, ...remaining]
    }
    if (newArrivals.length > 0) {
      newArrivalsGrid.innerHTML = newArrivals.map(createProductCard).join("")
    }
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