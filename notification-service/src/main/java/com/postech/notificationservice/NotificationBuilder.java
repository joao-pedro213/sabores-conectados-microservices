package com.postech.notificationservice;

import com.postech.core.order.dto.OrderDto;
import com.postech.core.reservation.dto.ReservationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationBuilder {
    public String build(OrderDto orderDto) {
        String notification = "|id=%s|client=%s|restaurant=%s|status=%s|createdAt=%s|";
        return notification
                .formatted(
                        orderDto.getId(),
                        orderDto.getCustomerId(),
                        orderDto.getRestaurantId(),
                        orderDto.getStatus(),
                        orderDto.getCreatedAt());
    }

    public String build(ReservationDto reservationDto) {
        String notification = "|id=%s|client=%s|restaurant=%s|status=%s|createdAt=%s|";
        return notification
                .formatted(
                        reservationDto.getId(),
                        reservationDto.getCustomerId(),
                        reservationDto.getRestaurantId(),
                        reservationDto.getStatus(),
                        reservationDto.getCreatedAt());
    }
}
