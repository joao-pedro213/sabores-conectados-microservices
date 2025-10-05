package com.postech.restaurantservice.controller;

import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.restaurantservice.data.mapper.IRestaurantMapper;
import com.postech.restaurantservice.dto.NewRestaurantRequestDto;
import com.postech.restaurantservice.dto.RestaurantResponseDto;
import com.postech.restaurantservice.dto.UpdateRestaurantRequestDto;
import com.postech.restaurantservice.service.SecurityService;
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

import java.util.UUID;

@RestController
@AllArgsConstructor
public class RestaurantRestController {
    private final RestaurantControllerFactory restaurantControllerFactory;
    private final IRestaurantMapper mapper;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("@securityService.isRestaurantOwner() and hasAuthority('SCOPE_restaurant:write')")
    public ResponseEntity<RestaurantResponseDto> create(@Valid @RequestBody NewRestaurantRequestDto requestDto) {
        NewRestaurantDto newRestaurantDto = this.mapper.toNewRestaurantDto(requestDto);
        newRestaurantDto.setOwnerId(this.securityService.getIdentityFromSecurityContext());
        RestaurantDto restaurantDto = this.restaurantControllerFactory.build().createRestaurant(newRestaurantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mapper.toRestaurantResponseDto(restaurantDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> retrieveById(@PathVariable UUID id) {
        RestaurantDto restaurantDto = this.restaurantControllerFactory.build().retrieveRestaurantById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.mapper.toRestaurantResponseDto(restaurantDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "@securityService.isRestaurantOwner()"
                    + " and hasAuthority('SCOPE_restaurant:write')"
                    + " and @securityService.isResourceOwner(#id)")
    public ResponseEntity<RestaurantResponseDto> updateById(@PathVariable UUID id, @Valid @RequestBody UpdateRestaurantRequestDto requestDto) {
        UpdateRestaurantDto updateRestaurantDto = this.mapper.toUpdateRestaurantDto(requestDto);
        RestaurantDto restaurantDto = this.restaurantControllerFactory.build().updateRestaurant(id, updateRestaurantDto);
        return ResponseEntity.status(HttpStatus.OK).body(this.mapper.toRestaurantResponseDto(restaurantDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "@securityService.isRestaurantOwner()"
                    + " and hasAuthority('SCOPE_restaurant:write')"
                    + " and @securityService.isResourceOwner(#id)")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        this.restaurantControllerFactory.build().deleteRestaurantById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
