package uk.ac.Thematics;
import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public boolean createOrderWithItems(OrderDTO orderDto) {
        try {
            // Convert DTO to domain objects
            Order order = convertToOrder(orderDto);
            List<OrderItem> orderItems = convertToOrderItems(orderDto.getOrderItems(), order);
            
            // Validate order and items
            if (!validateOrder(order) || !validateOrderItems(orderItems)) {
                logger.error("Order validation failed");
                return false;
            }
            
            // Use transaction-managed method to insert order and items
            return Database.insertOrderWithItems(order, orderItems);
        } catch (Exception e) {
            logger.error("Error creating order with items", e);
            return false;
        }
    }
    
    private Order convertToOrder(OrderDTO orderDto) {
        // If OrderDTO doesn't have getOrderDate(), we have two options:
        // Option 1: Add it to OrderDTO (preferred solution)
        // Option 2: Use the current date as a fallback (temporary solution)
        Date orderDate = new Date(); // Default to current date
        
        // If OrderDTO has getOrderDate implemented, use the following instead:
        // Date orderDate = orderDto.getOrderDate();
        
        return new Order(
            0, // ID will be generated
            orderDto.getCustomerId(),
            orderDto.getTotalAmount(),
            orderDate, // Use the date we determined above
            PaymentStatus.PENDING,
            orderDto.getOrderType(),
            orderDto.getTaxAmount(),
            orderDto.getFinalAmount(),
            orderDto.getDiscountAmount(),
            orderDto.getDeliveryType(),
            "PROCESSING"
        );
    }
    
    private List<OrderItem> convertToOrderItems(List<OrderItemDTO> orderItemDtos, Order order) {
        return orderItemDtos.stream()
            .map(dto -> new OrderItem(
                0, // ID will be generated
                order.getOrderId(),
                dto.getProductId(),
                dto.getQuantity(),
                dto.getPrice(),
                dto.getSubtotal()
            ))
            .collect(Collectors.toList());
    }
    
    private boolean validateOrder(Order order) {
        return order != null &&
               order.getCustomerId() > 0 &&
               order.getTotalAmount() != null &&
               order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0;
    }
    
    private boolean validateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            logger.error("No order items provided");
            return false;
        }
        
        return orderItems.stream().allMatch(item ->
            item.getProductId() > 0 &&
            item.getQuantity() > 0 &&
            item.getPrice() != null &&
            item.getPrice().compareTo(BigDecimal.ZERO) > 0
        );
    }
}