# 🧩 Spring Boot Microservices Architecture — Product & Order Management System

## 📘 Overview
This project demonstrates a **Microservices-based architecture** using **Spring Boot**, **Spring Cloud**, and **Eureka Discovery**.  
It manages **Products** and **Orders**, with inter-service communication via **Feign Clients** and centralized routing through an **API Gateway**.

Each microservice is independently deployable and secured using **Basic Authentication**.

---

## 🏗️ Microservices Overview

| Service | Description | Port | Key Features |
|----------|-------------|------|---------------|
| **Eureka Server** | Service Registry for discovery | `8761` | Registers all microservices |
| **API Gateway** | Entry point for all clients | `8080` | Routes requests, Basic Auth security |
| **Products Service** | CRUD operations for products | `8081` | JPA, Validation, Exception Handling |
| **Order Service** | Manages orders and communicates with Product Service | `8082` | Feign Client, Transactional updates |
| **Common Module** | Shared models (e.g., Product class) | — | Reused across services |

---

## ⚙️ Technologies Used
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Cloud (Eureka, OpenFeign, Gateway)**
- **Spring Security (Basic Auth)**
- **Spring Data JPA & Hibernate**
- **Lombok**
- **Jakarta Validation**
- **Maven**

---

## 🧭 Architecture Diagram (Text View)
```
          +-------------------+
          |   Eureka Server   |
          |  (Service Registry)|
          +---------+---------+
                    |
      +-------------+--------------+
      |                            |
+-----v-----+                +-----v-----+
| API Gateway|               | Products  |
|   (8080)   |               | Service   |
|             |--------------| (8081)    |
|  Basic Auth |  Feign       | CRUD Ops  |
+-------------+               +-----------+
      |
      |
+-----v------+
| Order      |
| Service    |
| (8082)     |
| Feign -> ProductSvc |
+------------+
```

---

## 🔐 Authentication

| Component | Username | Password |
|------------|-----------|-----------|
| Gateway | `admin` | `pass` |
| Products Service | `admin` | `pass` |
| Order Service | `admin` | `pass` |

Use these credentials for Basic Authentication when making API calls.

---

## 🚀 Getting Started

### 1️⃣ Clone the repository
```bash
git clone <your-repo-url>
cd <project-folder>
```

### 2️⃣ Run services in order
Each service has its own `pom.xml`. Start them sequentially:

```bash
# Terminal 1
cd eureka-server
mvn spring-boot:run

# Terminal 2
cd api-gateway
mvn spring-boot:run

# Terminal 3
cd products-service
mvn spring-boot:run

# Terminal 4
cd order-service
mvn spring-boot:run
```

---

## 🌐 Service Endpoints

### 🔸 Eureka Dashboard
```
http://localhost:8761
```

### 🔸 API Gateway Routes
```
http://localhost:8080/products
http://localhost:8080/orders
```

### 🔸 Products Service (direct)
```
GET    /products
GET    /products/{id}
POST   /products/single
POST   /products/bulk
PUT    /products/{id}
DELETE /products/{id}
```

### 🔸 Orders Service (direct)
```
GET    /orders
GET    /orders/{id}
POST   /orders
PUT    /orders/{id}
DELETE /orders/{id}
```

---

## 🔁 Inter-Service Communication
- The **Order Service** calls the **Products Service** using **Feign Client** (`ProductClient.java`).
- Quantity updates and validation are handled before order creation/update.

---

## ⚠️ Exception Handling
Both services use **GlobalExceptionHandler** to handle:
- Validation errors  
- Resource not found  
- Feign communication failures  
- Generic server errors  

---

## 🧪 Testing
Each module includes test stubs under:
```
src/test/java/com/example/demo/
```

Run all tests:
```bash
mvn test
```

---

## 📄 Folder Structure
```
.
├── api-gateway/
├── eureka-server/
├── products-service/
├── order-service/
└── common/
```

---

## 🧰 Future Enhancements
- Add Swagger/OpenAPI documentation  
- Implement centralized configuration server  
- Integrate database migrations via Flyway  
- Add Docker Compose for multi-container setup  

