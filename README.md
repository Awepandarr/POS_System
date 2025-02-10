# POS System Documentation

## **1. Entity-Relationship (ER) Diagram**
```mermaid
---
title: POS System - ERD & UML Class Diagram
---

%% ER Diagram
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
        enum order_status
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

%% UML Class Diagram
classDiagram
    class POS_System {
        shopName: String
        location: String
        startTransaction(): Transaction
        endOfDayReport(): EndOfDayReport
    }

    class Transaction {
        transactionID: String
        dateTime: Date
        totalAmount: double
        paymentMethod: String
        addProduct(product: Product): void
        calculateTotal(): double
        processPayment(payment: Payment): boolean
        generateReceipt(): Receipt
        removeProduct(product: Product): void
    }

    class Order {
        orderID: String
        orderDate: Date
        totalAmount: double
        paymentStatus: String
        orderType: String
        taxAmount: double
        finalAmount: double
        discountAmount: double
        deliveryType: String
        orderStatus: String
    }

    class Order_Items {
        orderItemID: String
        orderID: String
        productID: String
        quantity: int
        price: double
        subtotal: double
    }

    class Payment {
        paymentID: String
        amount: double
        paymentMethod: String
        paymentStatus: String
        paymentDate: Date
        transactionID: String
        processPayment(): boolean
    }

    class Refund {
        refundID: String
        orderID: String
        invoiceID: String
        refundAmount: double
        refundReason: String
        refundDate: Date
        refundStatus: String
    }

    class DeliveryCharge {
        chargeID: String
        deliveryType: String
        deliveryCost: double
        deliveryAddress: String
        orderID: String
    }

    class Product {
        productID: String
        name: String
        price: double
        stockQuantity: int
        updateStock(quantity: int): void
    }

    class Invoice {
        invoiceID: String
        orderID: String
        dateIssued: Date
        subtotal: double
        discount: double
        taxAmount: double
        finalAmount: double
    }

    POS_System --> Transaction
    Transaction --> Order
    Transaction --> Order_Items
    Transaction --> Product
    Transaction --> Payment
    Order --> Invoice
    Order --> Refund
    Order --> DeliveryCharge
    Refund --> Invoice
