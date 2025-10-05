package com.postech.orderservice.mapper;

import com.postech.core.order.dto.NewOrderDto;
import com.postech.core.order.dto.OrderDto;
import com.postech.orderservice.data.document.OrderDocument;
import com.postech.orderservice.dto.NewOrderRequestDto;
import com.postech.orderservice.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderMapper {

    @Mapping(target = "customerId", ignore = true)
    NewOrderDto toNewOrderDto(NewOrderRequestDto newOrderRequestDto);

    OrderResponseDto toOrderResponseDto(OrderDto orderDto);

    OrderDocument toOrderDocument(OrderDto orderDto);

    OrderDto toOrderDto(OrderDocument orderDocument);
}
