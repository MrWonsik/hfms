package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.NewShopRequest;
import com.wasacz.hfms.expense.controller.ShopResponse;
import com.wasacz.hfms.persistence.Shop;
import com.wasacz.hfms.persistence.ShopRepository;
import com.wasacz.hfms.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopManagementServiceTest {
    final String SHOP_NAME = "Ikea";

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopManagementService shopManagementService;

    @Test
    public void whenAddNewShop_givenNewShopRequest_thenSaveShop() {
        //given
        NewShopRequest newShopRequest = new NewShopRequest();
        newShopRequest.setShopName(SHOP_NAME);
        User user = User.builder().id(1L).username("Test").build();

        Shop ikeaShop = Shop.builder().shopName(SHOP_NAME).user(user).isDeleted(false).build();
        when(shopRepository.findByShopNameAndUser(SHOP_NAME, user)).thenReturn(Optional.empty());
        when(shopRepository.save(any(Shop.class))).thenReturn(ikeaShop);

        //when
        ShopResponse shopResponse = shopManagementService.addNewShop(newShopRequest, user);

        //then
        assertEquals(shopResponse.getShopName(), ikeaShop.getShopName());
        assertFalse(shopResponse.isDeleted());
    }

    @Test
    public void whenAddNewShop_givenNewShopRequestWithNameThatAlreadyExists_thenThrowException() {
        //given
        NewShopRequest newShopRequest = new NewShopRequest();
        newShopRequest.setShopName(SHOP_NAME);
        User user = User.builder().id(1L).username("Test").build();

        Shop ikeaShop = Shop.builder().shopName(SHOP_NAME).user(user).isDeleted(false).build();
        when(shopRepository.findByShopNameAndUser(SHOP_NAME, user)).thenReturn(Optional.of(ikeaShop));

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopManagementService.addNewShop(newShopRequest, user));
        assertEquals(exception.getMessage(), "Shop with this name exist.");
    }


    @Test
    public void whenDeleteShop_givenShopId_thenSetShopAsDeleted() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        Shop ikeaShopBeforeDeleted = Shop.builder().id(1L).shopName(SHOP_NAME).user(user).isDeleted(false).build();
        Shop ikeaShopDeleted = Shop.builder().id(1L).shopName(SHOP_NAME).user(user).isDeleted(true).build();
        when(shopRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(ikeaShopBeforeDeleted));
        when(shopRepository.save(any(Shop.class))).thenReturn(ikeaShopDeleted);

        //when
        ShopResponse shopResponse = shopManagementService.deleteShop(1L, user);

        //then
        assertTrue(shopResponse.isDeleted());
    }

    @Test
    public void whenDeleteShop_givenIdOfShopThatNotExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(shopRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopManagementService.deleteShop(1L, user));
        assertEquals(exception.getMessage(), "Shop not found.");
    }

}