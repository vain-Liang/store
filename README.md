<div align="center">
  <h1>Online Shopping Store</h1>
</div>

This project is an online shopping store demo built with [SpringBoot3](https://github.com/spring-projects/spring-boot) and [Vue3](https://github.com/vuejs/core).

## Technology Stack

- Frontend: **Vue**
- Backend: **Spring Boot**
- Database: **MySQL 8.4**
- Cache: **Redis**

## Directory Structure

<details>

  <summary>Click to view</summary>

  ```
  ├─📁 backend
│ ├─📁 .mvn
│ ├─📁 src
│ │ ├─📁 main
│ │ │ ├─📁 java
│ │ │ │ └─📁 online
│ │ │ │   └─📁 store
│ │ │ │     ├─📁 common
│ │ │ │     │ ├─📁 exception------------------------ # Exception Handle class
│ │ │ │     │ ├─📁 filter
│ │ │ │     │ │ └─📄 JwtAuthenticationFilter.java
│ │ │ │     │ └─📁 utils
│ │ │ │     │   ├─📄 JwtUtil.java
│ │ │ │     │   ├─📄 MaskingUtil.java
│ │ │ │     │   └─📄 SnowflakeIdGenerator.java
│ │ │ │     ├─📁 config
│ │ │ │     │ ├─📄 JwtConfig.java
│ │ │ │     │ ├─📄 SecurityConfig.java--------------- # Spring Security config
│ │ │ │     │ ├─📄 SwaggerConfig.java---------------- # Swagger API doc config
│ │ │ │     │ └─📄 WebConfig.java-------------------- # RESTful API config
│ │ │ │     ├─📁 controller
│ │ │ │     │ ├─📁 auth
│ │ │ │     │ │ └─📄 AuthController.java
│ │ │ │     │ ├─📁 order
│ │ │ │     │ │ └─📄 OrderController.java
│ │ │ │     │ ├─📁 product
│ │ │ │     │ │ └─📄 ProductController.java
│ │ │ │     │ └─📁 transaction
│ │ │ │     │   └─📄 PaymentController.java
│ │ │ │     ├─📁 dto
│ │ │ │     │ ├─📁 auth
│ │ │ │     │ │ ├─📄 LoginRequest.java
│ │ │ │     │ │ ├─📄 RefreshTokenRequest.java
│ │ │ │     │ │ └─📄 RegisterRequest.java
│ │ │ │     │ ├─📁 order
│ │ │ │     │ │ └─📄 OrderCreateRequest.java
│ │ │ │     │ ├─📁 product
│ │ │ │     │ │ ├─📄 ProductCreate.java
│ │ │ │     │ │ └─📄 ProductUpdate.java
│ │ │ │     │ └─📁 transaction
│ │ │ │     │   └─📄 RechargeRequest.java
│ │ │ │     ├─📁 enums
│ │ │ │     │ ├─📄 OrderStatus.java
│ │ │ │     │ ├─📄 ProductStatus.java
│ │ │ │     │ ├─📄 SelectedStatus.java
│ │ │ │     │ ├─📄 TransactionStatus.java
│ │ │ │     │ ├─📄 TransactionType.java
│ │ │ │     │ └─📄 UserStatus.java
│ │ │ │     ├─📁 handler
│ │ │ │     │ └─📄 TimeMetaObjectHandler.java
│ │ │ │     ├─📁 mapper
│ │ │ │     │ ├─📄 OrderItemMapper.java
│ │ │ │     │ ├─📄 OrderMapper.java
│ │ │ │     │ ├─📄 ProductMapper.java
│ │ │ │     │ ├─📄 RoleMapper.java
│ │ │ │     │ ├─📄 TransactionMapper.java
│ │ │ │     │ ├─📄 UserMapper.java
│ │ │ │     │ └─📄 UserRoleMapper.java
│ │ │ │     ├─📁 pojo
│ │ │ │     │ ├─📄 BaseEntity.java
│ │ │ │     │ ├─📄 CartItem.java
│ │ │ │     │ ├─📄 OrderItem.java
│ │ │ │     │ ├─📄 Orders.java
│ │ │ │     │ ├─📄 Product.java
│ │ │ │     │ ├─📄 Result.java
│ │ │ │     │ ├─📄 Role.java
│ │ │ │     │ ├─📄 Transaction.java
│ │ │ │     │ ├─📄 User.java
│ │ │ │     │ └─📄 UserRole.java
│ │ │ │     ├─📁 service
│ │ │ │     │ ├─📁 auth
│ │ │ │     │ │ ├─📁 impl
│ │ │ │     │ │ │ ├─📄 PermissionServiceImpl.java
│ │ │ │     │ │ │ └─📄 UserServiceImpl.java
│ │ │ │     │ │ ├─📄 PermissionService.java
│ │ │ │     │ │ └─📄 UserService.java
│ │ │ │     │ ├─📁 common
│ │ │ │     │ │ └─📄 TokenBlacklistService.java
│ │ │ │     │ ├─📁 order
│ │ │ │     │ │ ├─📁 impl
│ │ │ │     │ │ │ └─📄 OrderServiceImpl.java
│ │ │ │     │ │ └─📄 OrderService.java
│ │ │ │     │ ├─📁 product
│ │ │ │     │ │ ├─📁 impl
│ │ │ │     │ │ │ └─📄 ProductServiceImpl.java
│ │ │ │     │ │ └─📄 ProductService.java
│ │ │ │     │ └─📁 transaction
│ │ │ │     │   ├─📁 impl
│ │ │ │     │   │ └─📄 PaymentServiceImpl.java
│ │ │ │     │   └─📄 PaymentService.java
│ │ │ │     ├─📁 vo
│ │ │ │     │ ├─📁 auth
│ │ │ │     │ │ ├─📄 LoginResponse.java
│ │ │ │     │ │ └─📄 RegisterResponse.java
│ │ │ │     │ ├─📁 order
│ │ │ │     │ │ └─📄 OrderResponse.java
│ │ │ │     │ ├─📁 product
│ │ │ │     │ │ ├─📄 ProductDetail.java
│ │ │ │     │ │ └─📄 ProductPublic.java
│ │ │ │     │ └─📁 transaction
│ │ │ │     │   └─📄 RechargeResponse.java
│ │ │ │     └─📄 StoreApplication.java
│ │ │ └─📁 resources
│ │ │   ├─📁 sql
│ │ │   │ ├─📄 origin_store.sql
│ │ │   │ ├─📄 store-data.sql------------------------ # Initial data import
│ │ │   │ └─📄 store.sql----------------------------- # Database initialization
│ │ │   ├─📁 static
│ │ │   ├─📁 templates
│ │ │   ├─📄 application-dev.yaml-------------------- # Development environment config
│ │ │   ├─📄 application-production.yaml------------- # Production environment config
│ │ │   └─📄 application.yaml------------------------ # Global basic config
│ │ └─📁 test
│ │   └─📁 java
│ │     └─📁 online
│ │       └─📁 store
│ │         └─📄 StoreApplicationTests.java
│ ├─📄 .gitattributes
│ ├─📄 .gitignore
│ ├─📄 mvnw
│ ├─📄 mvnw.cmd
│ └─📄 pom.xml
├─📁 frontend
│ └─📁 store
│   ├─📁 public
│   │ └─📄 favicon.ico
│   ├─📁 src
│   │ ├─📁 __tests__
│   │ │ └─📄 App.spec.js
│   │ ├─📁 api
│   │ │ ├─📄 auth.js
│   │ │ ├─📄 product.js
│   │ │ └─📄 request.js
│   │ ├─📁 asserts
│   │ │ ├─📁 img
│   │ │ │ ├─📄 narrow.webp
│   │ │ │ └─📄 wide.webp
│   │ │ └─📄 logo.svg
│   │ ├─📁 components
│   │ │ ├─📄 AppHeader.vue
│   │ │ └─📄 ProductCard.vue
│   │ ├─📁 router
│   │ │ └─📄 index.js
│   │ ├─📁 stores
│   │ │ ├─📄 counter.js
│   │ │ └─📄 user.js
│   │ ├─📁 views
│   │ │ ├─📄 HomeView.vue
│   │ │ ├─📄 LoginView.vue
│   │ │ ├─📄 ProductDetailView.vue
│   │ │ └─📄 RegisterView.vue
│   │ ├─📄 App.vue
│   │ └─📄 main.js
│   ├─📄 .editorconfig
│   ├─📄 .env.development---------------------------- # Development environment variables
│   ├─📄 .env.production----------------------------- # Production environment variables
│   ├─📄 .gitattributes
│   ├─📄 .gitignore
│   ├─📄 .prettierrc.json
│   ├─📄 bun.lock
│   ├─📄 eslint.config.js
│   ├─📄 index.html
│   ├─📄 jsconfig.json
│   ├─📄 package.json
│   ├─📄 vite.config.js
│   └─📄 vitest.config.js
├─📄 .gitignore
├─📄 LICENSE
└─📄 README.md
  ```

*Generated using [Annotree](https://github.com/itchaox/annotree)*
   
</details>

## Feature

1. **User Authentication & Authorization:**  
   Role-Based Access Control (*RBAC*) is used to manage permissions. Roles can be customized, with three default roles available: **consumer** (regular user), **admin**, and **merchant**.

2. **Product Management:**  
   Includes product display, search, purchase, procurement (adding products), toggling availability (for sale / not for sale), and managing prices and stock quantities.

3. **Payment System:**
   A simulated payment flow is implemented. Users can recharge their account balance, make purchases with it, and receive refunds directly back to their balance.

4. **Order Management:**
   Supports shopping cart functionality, as well as order viewing, searching, and management.

5. ~~More...~~

<details>

  <summary>⚠️ Note</summary>

  > I am currently learning full-stack web development.  
  > So far, I have implemented **JWT-based user authentication**, **along with basic product query** and **account balance recharge** API functionalities.  
  > The **frontend** and **other features** are still under development.

</details>

## Requirement

### Backend

  - JDK `21` or `25`
  - MySQL `8.4` or later
  - Redis

### Frontend

  - Nodejs `22` or later
