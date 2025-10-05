package com.postech.itemservice.mapper;

import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;
import com.postech.itemservice.data.document.ItemDocument;
import com.postech.itemservice.dto.ItemResponseDto;
import com.postech.itemservice.dto.NewItemRequestDto;
import com.postech.itemservice.dto.UpdateItemRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IItemMapper {
    NewItemDto toNewItemDto(NewItemRequestDto requestDto);

    ItemResponseDto toItemResponseDto(ItemDto itemDto);

    UpdateItemDto toUpdateItemDto(UpdateItemRequestDto updateItemRequestDto);

    ItemDocument toItemDocument(ItemDto itemDto);

    ItemDto toItemDto(ItemDocument itemDocument);
}
