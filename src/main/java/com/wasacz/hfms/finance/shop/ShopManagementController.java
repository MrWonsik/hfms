package com.wasacz.hfms.finance.shop;

import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/shop")
public class ShopManagementController {

    private final ShopManagementService shopManagementService;

    public ShopManagementController(ShopManagementService shopManagementService) {
        this.shopManagementService = shopManagementService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping("/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> addShop(@CurrentUser UserPrincipal user, @RequestBody ShopObj shopObj) {
        ShopResponse shopResponse = shopManagementService.addNewShop(shopObj, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(shopResponse);
    }

    @DeleteMapping("/{shopId}")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> deleteShop(@CurrentUser UserPrincipal user, @PathVariable Long shopId) {
        ShopResponse shopResponse = shopManagementService.deleteShop(shopId, user.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(shopResponse);
    }

    @GetMapping("/")
    @Secured({"ROLE_USER"})
    public ResponseEntity<?> getAllShopsByUser(@CurrentUser UserPrincipal user) {
        return ResponseEntity.status(HttpStatus.OK).body(shopManagementService.getAllNotDeletedShops(user.getUser()));
    }
}