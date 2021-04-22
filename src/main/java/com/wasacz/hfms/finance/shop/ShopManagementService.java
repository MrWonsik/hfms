package com.wasacz.hfms.finance.shop;

import com.wasacz.hfms.utils.date.DateTime;
import com.wasacz.hfms.persistence.Shop;
import com.wasacz.hfms.persistence.ShopRepository;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;

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
                .createDate(new DateTime(shop.getCreatedDate()))
                .build();
    }

    public ShopResponse addNewShop(CreateShopRequest createShopRequest, User user) {
        ShopValidator.validate(createShopRequest);
        Shop savedShop = shopRepository.save(Shop.builder().shopName(createShopRequest.getShopName()).user(user).build());
        return getShopResponse(savedShop);
    }

    public ShopResponse deleteShop(Long shopId, User user) {
        Shop shop = shopRepository.findByIdAndUserAndIsDeletedFalse(shopId, user).orElseThrow(() -> new IllegalArgumentException("Shop not found."));
        shop.setDeleted(true);
        Shop deletedShop = shopRepository.save(shop);
        return getShopResponse(deletedShop);
    }

}
