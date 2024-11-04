package be.pxl.services.controller;

import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long id) {
        return new ResponseEntity<>(cartService.getCart(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartResponse> createCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.createCart(userId));
    }

    @PostMapping("/user/{userId}/{productId}/add")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.addToCart(userId, productId));
    }

    @PostMapping("/user/{userId}/{productId}/remove")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

}