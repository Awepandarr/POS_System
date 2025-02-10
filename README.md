# POS_System
```mermaid
erDiagram
    Customers {
        int customer_id PK
        varchar customer_name
        varchar customer_email
        varchar contact
        int loyalty_points
    }

    Categories {
        int category_id PK
        varchar name
        varchar description
        boolean is_active
        varchar category_type
        datetime created_date
    }

    Products {
        int product_id PK
        varchar name
        decimal price
        int category_id FK
        int stock_quantity
        varchar barcode
    }

    Orders {
        int order_id PK
        int customer_id FK
        datetime order_date
        decimal total_amount
        enum payment_status
        enum order_type
        decimal tax_amount
        decimal final_amount
        decimal discount_amount
        varchar delivery_type
    }

    Order_Items {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
        decimal subtotal
        decimal price
    }

    Payments {
        int payment_id PK
        int order_id FK
        decimal payment_amount
        enum payment_type
        enum payment_status
        datetime payment_date
        varchar transaction_id
    }

    Invoices {
        int invoice_id PK
        int order_id FK
        datetime date_issued
        decimal subtotal
        decimal discount
        decimal tax_amount
        decimal final_amount
        int customer_id FK
    }

    Refunds {
        int refund_id PK
        int order_id FK
        int invoice_id FK
        decimal refund_amount
        varchar refund_reason
        datetime refund_date
        enum refund_status
    }

    EndOfDayReport {
        int report_id PK
        date date
        decimal total_sales
        decimal total_payments
        decimal total_discounts
        text payment_breakdown
    }

    DeliveryCharge {
        int charge_id PK
        varchar delivery_type
        decimal delivery_cost
        varchar delivery_address
        int order_id FK
    }

    Customers ||--o| Orders : places
    Orders ||--o| Order_Items : contains
    Order_Items ||--o| Products : includes
    Orders ||--o| Payments : has
    Orders ||--o| Invoices : generates
    Orders ||--o| Refunds : has
    Orders ||--o| DeliveryCharge : has
    Payments ||--o| Orders : paid_for
    Invoices ||--o| Customers : generated_for
    Refunds ||--o| Orders : refunded_from
    Refunds ||--o| Invoices : linked_to
    Products ||--o| Categories : belongs_to
    EndOfDayReport ||--o| Orders : summarizes

















classDiagram
    class POS_System {
        +String shopName
        +String location
        +startTransaction(): Transaction
        +endOfDayReport(): EndOfDayReport
    }

    class Transaction {
        +String transactionID
        +Date dateTime
        +double totalAmount
        +String paymentMethod
        +List<Product> products
        +addProduct(product: Product): void
        +calculateTotal(): double
        +processPayment(payment: Payment): boolean
        +generateReceipt(): Receipt
        +removeProduct(product: Product): void
    }

    class Payment {
        +String paymentID
        +double amount
        +String paymentMethod
        +boolean processPayment(): boolean
    }

    class BarcodeScanner {
        +String scannerID
        +String connectionType
        +scanBarcode(): String
        +identifyProduct(barcode: String): Product
    }

    class Receipt {
        +String receiptID
        +String transactionID
        +Date date
        +generatePDF(): void
    }

    class EndOfDayReport {
        +String reportID
        +Date date
        +double totalSales
        +int totalTransactions
        +double cashPayments
        +double cardPayments
        +generateReport(): void
        +exportToPDF(): void
        +sendReport(email: String): void
    }

    class GUI {
        +String screenType
        +String theme
        +displayMenu(): void
        +showTransactionDetails(transaction: Transaction): void
        +printReceipt(receipt: Receipt): void
    }

    class User {
        +String userID
        +String name
        +String role
        +boolean login(username: String, password: String)
    }

    class Product {
        +String productID
        +String name
        +double price
        +int stockQuantity
        +updateStock(quantity: int): void
    }

    class Customer {
        +String customerID
        +String name
        +String email
        +String contact
        +int loyaltyPoints
    }

    class Order {
        +String orderID
        +Date orderDate
        +double totalAmount
        +String paymentStatus
        +String orderType
        +double taxAmount
        +double finalAmount
        +double discountAmount
        +String deliveryType
        +String orderStatus
    }

    class Order_Items {
        +String orderItemID
        +String orderID
        +String productID
        +int quantity
        +double price
        +double subtotal
    }

    class Invoice {
        +String invoiceID
        +String orderID
        +Date dateIssued
        +double subtotal
        +double discount
        +double taxAmount
        +double finalAmount
        +String customerID
    }

    class Refund {
        +String refundID
        +String orderID
        +String invoiceID
        +double refundAmount
        +String refundReason
        +Date refundDate
        +String refundStatus
    }

    class DeliveryCharge {
        +String chargeID
        +String deliveryType
        +double deliveryCost
        +String deliveryAddress
        +String orderID
    }

    %%% Relationships %%%
    POS_System --> Transaction : "1 *"
    Transaction --> Payment : "1 1"
    Transaction --> Receipt : "1 1"
    Transaction --> Order_Items : "1 *"
    Transaction --> Product : "1 *"
    Order_Items --> Product : "1 1"
    Transaction --> Customer : "1 1"
    Payment --> Order : "1 1"
    Order --> Invoice : "1 1"
    Order --> Refund : "1 *"
    Order --> DeliveryCharge : "1 1"
    Refund --> Invoice : "1 1"
    BarcodeScanner --> Product : "1 1"
