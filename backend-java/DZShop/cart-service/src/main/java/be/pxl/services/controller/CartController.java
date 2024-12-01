package be.pxl.services.controller;

import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.services.CartService;
import be.pxl.services.services.HeaderValidationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
//@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final HeaderValidationService headerValidationService;

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long id) {
        log.info("get cart endpoint called: cartId={}", id);
        return new ResponseEntity<>(cartService.getCart(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long userId) {
        String user = headerValidationService.isLoggedIn(authorizationHeader);
        if (user != null) {
            log.info("get cart endpoint called: userId={}", userId);
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        } else  {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartResponse> createCart(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Long userId) {
        String user = headerValidationService.isLoggedIn(authorizationHeader);
        if (user != null) {
            log.info("create cart endpoint called: userId={}", userId);
            return ResponseEntity.ok(cartService.createCart(userId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/user/{userId}/{productId}/add")
    public ResponseEntity<CartResponse> addItemToCart(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long userId, @PathVariable Long productId) {
        String user = headerValidationService.isLoggedIn(authorizationHeader);
        if (user != null) {
            log.info("add item to cart endpoint called: userId={}, productId={}", userId, productId);
            return ResponseEntity.ok(cartService.addToCart(userId, productId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/user/{userId}/{productId}/remove")
    public ResponseEntity<CartResponse> removeItemFromCart(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long userId, @PathVariable Long productId) {
        String user = headerValidationService.isLoggedIn(authorizationHeader);
        if (user != null) {
            log.info("remove item to cart endpoint called: userId={}, productId={}", userId, productId);
            return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}