# ERP System for Inventory and Sales Management

A full-stack ERP system developed using Java Spring Boot and React for managing inventory, customers, suppliers, sales, purchases, goods received notes, invoices, and financial information.

## Tech Stack

### Backend

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- JWT Authentication
- MySQL
- Maven
- Swagger / OpenAPI
- JUnit
- Mockito

### Frontend

- React
- Vite
- React Router
- Material UI
- Axios
- React Hook Form
- Yup
- Recharts
- Vitest
- React Testing Library

## User Roles

- **Admin** – Full system access
- **Sales Executive** – Customers, products, sales orders and invoices
- **Purchase Manager** – Suppliers, purchase orders and GRNs
- **Inventory Manager** – Products and inventory/GRN operations
- **Accountant** – Dashboard, invoices and financial information

## Main Modules

### Authentication

- User registration
- User login
- JWT authentication
- Password encryption using BCrypt
- Role-based authorization

### Product Management

- Create products
- View products
- Update products
- Delete products
- SKU validation
- Stock and reorder-level management

### Customer Management

- Customer CRUD operations
- Customer contact and address management

### Supplier Management

- Supplier CRUD operations
- Supplier contact and address management

### Sales Orders

- Create sales orders
- View sales orders
- Update order status
- Delete sales orders
- Automatic total amount calculation

### Purchase Orders

- Create purchase orders
- View purchase orders
- Update order status
- Delete purchase orders

### Goods Received Notes

- Create GRNs
- Track received quantities
- Automatically update product stock

### Invoice Management

- Generate invoices from sales orders
- Invoice status management
- Invoice number generation
- PDF invoice download

### Dashboard

- Inventory summary
- Sales information
- Purchase information
- Invoice information
- Charts and summary cards

## Database

MySQL database:

text
erp_system