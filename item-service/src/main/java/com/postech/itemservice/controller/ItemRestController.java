package com.postech.itemservice.controller;


import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;
import com.postech.itemservice.dto.ItemResponseDto;
import com.postech.itemservice.dto.NewItemRequestDto;
import com.postech.itemservice.dto.UpdateItemRequestDto;
import com.postech.itemservice.mapper.IItemMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ItemRestController {
    private final ItemControllerFactory itemControllerFactory;
    private final IItemMapper mapper;

    @PostMapping
    @PreAuthorize(
            "(@securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_item:write')"
                    + " and @securityService.isResourceOwner(#requestDto.restaurantId)")
    public ResponseEntity<ItemResponseDto> create(@Valid @RequestBody NewItemRequestDto requestDto) {
        NewItemDto newItemDto = this.mapper.toNewItemDto(requestDto);
        ItemDto itemDto = this.itemControllerFactory.build().createItem(newItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mapper.toItemResponseDto(itemDto));
    }

    @GetMapping("/restaurant/{restaurantId}/menu")
    public ResponseEntity<List<ItemResponseDto>> retrieveByRestaurantId(@PathVariable UUID restaurantId) {
        List<ItemDto> foundItems = this.itemControllerFactory.build().retrieveItemsByRestaurantId(restaurantId);
        List<ItemResponseDto> itemResponseDtos = foundItems.stream().map(this.mapper::toItemResponseDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(itemResponseDtos);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "(@securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_item:write')"
                    + " and @securityService.isResourceOwner(@itemRepository.findById(#id).orElse(null)?.restaurantId)")
    public ResponseEntity<ItemResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateItemRequestDto requestDto) {
        UpdateItemDto updateItemDto = this.mapper.toUpdateItemDto(requestDto);
        ItemDto itemDto = this.itemControllerFactory.build().updateItem(id, updateItemDto);
        return ResponseEntity.status(HttpStatus.OK).body(this.mapper.toItemResponseDto(itemDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "(@securityService.isRestaurantOwner() or @securityService.isRestaurantManager())"
                    + " and hasAuthority('SCOPE_item:write')"
                    + " and @securityService.isResourceOwner(@itemRepository.findById(#id).orElse(null)?.restaurantId)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.itemControllerFactory.build().deleteItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
