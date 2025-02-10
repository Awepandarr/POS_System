# POS System Documentation

## **1. Entity-Relationship (ER) Diagram**
```mermaid
---
title: POS System - ERD & UML Class Diagram
---
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

