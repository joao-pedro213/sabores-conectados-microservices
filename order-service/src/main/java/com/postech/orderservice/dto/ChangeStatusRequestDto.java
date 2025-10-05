package com.postech.orderservice.dto;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeStatusRequestDto {
    private OrderStatus newStatus;
}
