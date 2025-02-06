# POS_System
`mermaid`
erDiagram
    Customer {
        int customer_id PK
        string customer_name
        string contact_info
        string address
        string loyalty_points
    }
    Order {
        int order_id PK
        int customer_id FK
        datetime order_date
        string order_status
        float total_amount
        string shipping_address
        string billing_address
        string payment_method
        string order_notes
        datetime estimated_delivery_date
        int delivery_agent_id FK
    }
    OrderItem {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
        float price
    }
    Product {
        int product_id PK
        string product_name
        string description
        float price
        int stock_quantity
        int category_id FK
        int supplier_id FK
    }
    Category {
        int category_id PK
        string category_name
        string description
    }
    Supplier {
        int supplier_id PK
        string supplier_name
        string contact_info
    }
    Payment {
        int payment_id PK
        int order_id FK
        float amount
        datetime payment_date
        string payment_status
        string transaction_id
    }
