# POS_System
```mermaid
erDiagram
    Customers {
        int customer_id PK
        string customer_name
        string customer_email
        string contact
        int loyalty_points
    }

    Categories {
        int category_id PK
        string name
        string description
        boolean is_active
        string category_type
        datetime created_date
    }

    Products {
        int product_id PK
        string name
        decimal price
        int category_id FK
        int stock_quantity
        string barcode
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
        string transaction_id
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
        string refund_reason
        datetime refund_date
        enum refund_status
    }

    EndOfDayReport {
        int report_id PK
        date date
        decimal total_sales
        decimal total_payments
        decimal total_discounts
        string payment_breakdown
    }

    %% Relationships
    Customers ||--o| Orders : places
    Categories ||--o| Products : contains
    Products ||--o| Order_Items : listed_in
    Orders ||--o| Order_Items : contains
    Orders ||--o| Payments : includes
    Orders ||--o| Invoices : generates
    Orders ||--o| Refunds : has_refund
    Orders ||--o| EndOfDayReport : reports
    Invoices ||--o| Refunds : refunded_by
