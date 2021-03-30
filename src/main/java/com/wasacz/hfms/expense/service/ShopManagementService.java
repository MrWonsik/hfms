package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.NewShopRequest;
import com.wasacz.hfms.expense.controller.ShopResponse;
import com.wasacz.hfms.expense.controller.ShopsResponse;
import com.wasacz.hfms.persistence.Shop;
import com.wasacz.hfms.persistence.ShopRepository;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopManagementService {

    private final ShopRepository shopRepository;

    public ShopManagementService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public ShopsResponse getAllNotDeletedShops(User user) {
        List<Shop> shops = shopRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new ShopsResponse(shops.stream().map(this::getShopResponse).collect(Collectors.toList()));
    }

    private ShopResponse getShopResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .isDeleted(shop.isDeleted())
                .createDate(LocalDate.ofInstant(shop.getCreatedDate(), ZoneId.systemDefault()))
                .build();
    }

    public ShopResponse addNewShop(NewShopRequest newShopRequest, User user) {
        validateNewShopName(newShopRequest.getShopName(), user);
        Shop savedShop = shopRepository.save(Shop.builder().shopName(newShopRequest.getShopName()).user(user).build());
        return getShopResponse(savedShop);
    }

    public ShopResponse deleteShop(Long shopId, User user) {
        Shop shop = shopRepository.findByIdAndUser(shopId, user).orElseThrow(() -> new IllegalArgumentException("Shop not found."));
        shop.setDeleted(true);
        Shop deletedShop = shopRepository.save(shop);
        return getShopResponse(deletedShop);
    }

    private void validateNewShopName(String shopName, User user) {
        if(shopRepository.findByShopNameAndUser(shopName, user).isPresent()) {
            throw new IllegalArgumentException("Shop with this name exist.");
        }
    }

}
