<div align="center">

# 🛍️ ShopVerse

### Premium Full-Stack E-Commerce Platform

**Spring Boot REST API** × **React 19 + Redux Toolkit**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.3-blue?logo=react)](https://react.dev/)
[![Redux Toolkit](https://img.shields.io/badge/Redux%20Toolkit-2.11.2-764ABC?logo=redux)](https://redux-toolkit.js.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791?logo=postgresql)](https://www.postgresql.org/)
[![Stripe](https://img.shields.io/badge/Payments-Stripe-635BFF?logo=stripe)](https://stripe.com/)
[![PayPal](https://img.shields.io/badge/Payments-PayPal-00457C?logo=paypal)](https://www.paypal.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](#license)

**[🎥 Demo Video](#) • [🌐 Live Site](#) • [📸 Screenshots](#-screenshots) • [📘 API Docs](#)**

</div>

---

## 📌 Overview

**ShopVerse** is a production-grade, multi-role e-commerce platform built to demonstrate real-world backend architecture and full-stack engineering practices — not a toy CRUD app. It supports three distinct user roles (**Customer**, **Seller**, **Admin**), dual payment gateway integration (**Stripe** + **PayPal**), JWT-secured multi-user data isolation, and audit-safe data handling across the order lifecycle.

### The Problem
Many e-commerce implementations struggle with:
- 🔓 **Data isolation leaks** — cart/address data bleeding across user sessions
- 💳 **Payment rigidity** — a single point of failure when one gateway goes down
- 🔗 **Audit breakage** — deleting an address crashing historical order records
- ⚡ **Inventory race conditions** — overselling due to non-atomic stock updates

### The Solution
ShopVerse solves these with a clean, decoupled architecture: strict JWT-scoped resource ownership, dual Stripe/PayPal redundancy, audit-safe soft-disassociation on delete, and role-based workflows tailored to each user type.

---

## ✨ Key Features

| Category | Highlights |
|---|---|
| 🔐 **Security & Auth** | JWT-based auth, HTTP-only cookies, `ROLE_USER` / `ROLE_SELLER` / `ROLE_ADMIN` RBAC |
| 🛍️ **Catalog** | Paginated search, category filters, live stock validation, multipart image uploads |
| 🛒 **Cart** | Backend-persisted + Redux-synced cart, coupon engine, session-safe purging on logout |
| 📍 **Addresses** | Multi-address book, default checkout selection, FK-safe deletion (preserves order history) |
| 💳 **Payments** | Stripe `PaymentIntent` flow + PayPal Express Checkout (sandbox) |
| 📊 **Dashboards** | Seller inventory/order console, Admin sales analytics + global order oversight |

---

## 🏗️ System Architecture

```mermaid
graph TD
    Client[React 19 Frontend + Vite] -->|HTTPS / Axios Interceptor| SecurityFilter[Spring Security JWT Filter]
    SecurityFilter -->|Authenticated Principal| Controllers[REST Controllers]

    subgraph "Spring Boot Backend Layer"
        Controllers -->|DTOs| Services[Service Layer]
        Services -->|Business Logic| Repositories[Spring Data JPA Repositories]
        Services -->|Payment Calls| StripeSDK[Stripe Java SDK]
        Services -->|Payment Calls| PayPalSDK[PayPal REST Client]
    end

    subgraph "Data Persistence Layer"
        Repositories -->|SQL| PostgreSQL[(PostgreSQL Database)]
    end
```

**Layered backend design:**
- **Controller Layer** → validates payloads (`@Valid`), routes requests
- **Service Layer** → business logic, `@Transactional` boundaries
- **Repository Layer** → `JpaRepository` + Spring Data JPA
- **Security Filter** → JWT parsing, signature verification, `SecurityContext` population
- **DTO Mapping** → `ModelMapper` decouples JPA entities from API contracts

---

## 🔄 Control Flow

```mermaid
sequenceDiagram
    actor U as User
    participant FE as React Frontend
    participant SEC as JWT Security Filter
    participant API as Spring Boot API
    participant PAY as Stripe / PayPal
    participant DB as PostgreSQL

    U->>FE: Browse storefront, add to cart
    FE->>API: POST /api/carts/products/{id}/quantity/{qty}
    API->>SEC: Validate JWT
    SEC->>API: Authenticated principal
    API->>DB: Persist cart item
    U->>FE: Proceed to checkout
    FE->>API: GET /api/users/addresses
    API->>DB: Fetch saved addresses
    U->>FE: Select address + payment method
    FE->>API: POST /api/order/stripe-client-secret
    API->>PAY: Create PaymentIntent
    PAY-->>API: clientSecret
    API-->>FE: Return clientSecret
    FE->>PAY: Confirm payment (Stripe Elements / PayPal SDK)
    PAY-->>FE: Payment approved
    FE->>API: POST /api/order/users/payments/{method}
    API->>PAY: Verify payment status
    API->>DB: Create Order + OrderItems, update stock, clear cart
    API-->>FE: Order confirmation
    Note over API,DB: Seller/Admin later update order status
```

---

## 🗂️ Entity Relationship (UML)

```mermaid
erDiagram
    USER ||--o{ ADDRESS : owns
    USER ||--o{ CART : has
    USER ||--o{ ORDER : places
    USER }o--o{ ROLE : assigned

    CART ||--o{ CART_ITEM : contains
    PRODUCT ||--o{ CART_ITEM : "referenced in"
    PRODUCT }o--|| CATEGORY : "belongs to"

    ORDER ||--o{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : "referenced in"
    ORDER ||--|| PAYMENT : "paid via"
    ORDER }o--|| ADDRESS : "shipped to"

    USER {
        Long userId PK
        String username
        String email
        String password
    }
    ROLE {
        Long roleId PK
        String roleName
    }
    ADDRESS {
        Long addressId PK
        String street
        String city
        String state
        String pincode
    }
    PRODUCT {
        Long productId PK
        String productName
        Double price
        Integer quantity
    }
    CATEGORY {
        Long categoryId PK
        String categoryName
    }
    CART {
        Long cartId PK
        Double totalPrice
    }
    ORDER {
        Long orderId PK
        LocalDate orderDate
        String orderStatus
    }
    PAYMENT {
        Long paymentId PK
        String paymentMethod
        String pgStatus
    }
```

---

## 🧰 Tech Stack

<table>
<tr><td><b>Backend</b></td><td>Java 21 · Spring Boot 3.4.2 · Spring Security · Spring Data JPA · Hibernate · JJWT 0.13.0 · ModelMapper · Lombok</td></tr>
<tr><td><b>Database</b></td><td>PostgreSQL</td></tr>
<tr><td><b>Payments</b></td><td>Stripe Java SDK 29.3.0 · PayPal Sandbox REST API</td></tr>
<tr><td><b>Frontend</b></td><td>React 19.2.3 · Vite 7.3.1 · React Router DOM 7.12.0</td></tr>
<tr><td><b>State</b></td><td>Redux Toolkit 2.11.2 · React Redux 9.2.0</td></tr>
<tr><td><b>UI</b></td><td>TailwindCSS 4.1.18 · Material-UI 7.3.7 · React Icons 5.5.0</td></tr>
<tr><td><b>Networking</b></td><td>Axios 1.13.2 · React Hot Toast 2.6.0</td></tr>
<tr><td><b>Docs</b></td><td>SpringDoc OpenAPI (Swagger UI)</td></tr>
</table>

---

## 📡 API Reference (Highlights)

<details>
<summary><b>🔑 Authentication</b></summary>

| Endpoint | Method | Description | Access |
|---|---|---|---|
| `/api/auth/signup` | POST | Register a new account | Public |
| `/api/auth/signin` | POST | Login, issue JWT | Public |
| `/api/auth/signout` | POST | Invalidate session | Authenticated |
| `/api/auth/user` | GET | Get logged-in user profile | Authenticated |
| `/api/auth/sellers` | GET | List sellers (paginated) | Admin |

</details>

<details>
<summary><b>📦 Products & Categories</b></summary>

| Endpoint | Method | Description | Access |
|---|---|---|---|
| `/api/public/products` | GET | Search/browse catalog | Public |
| `/api/public/products/keyword/{keyword}` | GET | Keyword search | Public |
| `/api/admin/categories/{id}/product` | POST | Add product to category | Admin/Seller |
| `/api/admin/products/{id}/image` | PUT | Upload product image | Admin/Seller |
| `/api/seller/products/{id}` | DELETE | Remove seller product | Seller |

</details>

<details>
<summary><b>🛒 Cart & 📍 Addresses</b></summary>

| Endpoint | Method | Description | Access |
|---|---|---|---|
| `/api/carts/users/cart` | GET | Get active cart | Authenticated |
| `/api/cart/products/{id}/quantity/{op}` | PUT | Update item quantity | Authenticated |
| `/api/users/addresses` | GET | List saved addresses | Authenticated |
| `/api/addresses/{id}` | DELETE | Safely unlink address | Authenticated |

</details>

<details>
<summary><b>💳 Orders & Payments</b></summary>

| Endpoint | Method | Description | Access |
|---|---|---|---|
| `/api/order/stripe-client-secret` | POST | Create Stripe PaymentIntent | Authenticated |
| `/api/order/users/payments/{method}` | POST | Finalize order (Stripe/PayPal) | Authenticated |
| `/api/admin/app/analytics` | GET | Sales & order analytics | Admin |
| `/api/seller/orders/{id}/status` | PUT | Update fulfillment status | Seller |

</details>

> 📘 Full interactive API documentation available via **Swagger UI** at `/swagger-ui.html` once the backend is running.

---

## 💳 Payment Integration Snapshot

**Stripe** — Backend creates a `PaymentIntent`, returns a `clientSecret`, frontend mounts Stripe `<PaymentElement />`; on approval the backend verifies status server-side before persisting the order.

**PayPal** — Frontend embeds the PayPal JS SDK sandbox buttons; on `onApprove`, the captured transaction details are posted to the backend, which persists the order tagged `paymentMethod: "paypal"`.

---

## 📁 Project Structure

```
ShopVerse/
├── sb-ecom/                        # Spring Boot Backend
│   └── src/main/java/.../project/
│       ├── config/                 # App constants, OpenAPI config
│       ├── controller/             # REST controllers
│       ├── exceptions/             # Global exception handling
│       ├── model/                  # JPA entities
│       ├── payload/                # Request/response DTOs
│       ├── repositories/           # Spring Data JPA interfaces
│       ├── security/               # JWT filters, Spring Security config
│       └── service/                # Business logic (Stripe, Order, Cart...)
│
└── ecom-frontend/                  # React 19 + Vite Frontend
    └── src/
        ├── api/                    # Axios instance & interceptors
        ├── components/
        │   ├── admin/               # Admin & Seller dashboards
        │   ├── auth/                 # Login/Register
        │   ├── cart/                 # Cart UI
        │   ├── checkout/              # Stripe/PayPal checkout flow
        │   ├── products/              # Catalog & filters
        │   └── profile/                # Address & profile management
        ├── store/                   # Redux Toolkit store
        └── utils/                   # Formatters & helpers
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 · Maven 3.8+ · Node.js 18+/20+ · PostgreSQL

### 1. Database Setup
```sql
CREATE DATABASE ecommerce;
```

### 2. Configure `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=your_password

stripe.secret.key=your_stripe_secret_key
paypal.client.id=your_paypal_client_id
paypal.client.secret=your_paypal_client_secret
paypal.mode=sandbox
```

### 3. Run the Backend
```bash
cd sb-ecom
./mvnw spring-boot:run
```
Runs on **`http://localhost:8080`**

### 4. Run the Frontend
```bash
cd ecom-frontend
npm install
npm run dev
```
Runs on **`http://localhost:5173`**

---

## 📸 Screenshots

> _Add product screenshots here — storefront, cart, checkout, admin dashboard._

| Storefront | Product Detail | Checkout |
|---|---|---|
| ![placeholder](#) | ![placeholder](#) | ![placeholder](#) |

| Seller Dashboard | Admin Analytics |
|---|---|
| ![placeholder](#) | ![placeholder](#) |

---

## 🎥 Demo Video

> _Embed or link a walkthrough video here (Loom / YouTube)._

[![Watch the demo](#)](#)

---

## 🔗 Links

- **Live Demo:** _add link_
- **API Docs (Swagger):** _add link_
- **Frontend Repo / Monorepo:** _add link_
- **LinkedIn Post / Case Study:** _add link_

---

## 🗺️ Roadmap

- [ ] Order tracking notifications (email/SMS)
- [ ] Wishlist & product reviews
- [ ] Elasticsearch-based product search
- [ ] Redis caching for catalog endpoints
- [ ] CI/CD pipeline (GitHub Actions) + Docker Compose deployment

---

## 👨‍💻 Author

**Mahak Singh**
B.Tech CS Student · Software Developer Intern

[GitHub](#) · [LinkedIn](#) · [Portfolio](#)

---

<div align="center">

⭐ If you found this project interesting, consider giving it a star!

</div>
