package be.pxl.services.controller;

import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.services.CartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long id) {
        log.info("get cart endpoint called: cartId={}", id);
        return new ResponseEntity<>(cartService.getCart(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId) {
        log.info("get cart endpoint called: userId={}", userId);
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartResponse> createCart(@PathVariable Long userId) {
        log.info("create cart endpoint called: userId={}", userId);
        return ResponseEntity.ok(cartService.createCart(userId));
    }

    @PostMapping("/user/{userId}/{productId}/add")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long userId, @PathVariable Long productId) {
        log.info("add item to cart endpoint called: userId={}, productId={}", userId, productId);
        return ResponseEntity.ok(cartService.addToCart(userId, productId));
    }

    @PostMapping("/user/{userId}/{productId}/remove")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        log.info("remove item to cart endpoint called: userId={}, productId={}", userId, productId);
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

}