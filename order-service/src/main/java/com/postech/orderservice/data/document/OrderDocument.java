package com.postech.orderservice.data.document;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDocument implements Persistable<UUID> {
    @Id
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private OrderStatus status;
    private List<OrderItemDocument> items;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
