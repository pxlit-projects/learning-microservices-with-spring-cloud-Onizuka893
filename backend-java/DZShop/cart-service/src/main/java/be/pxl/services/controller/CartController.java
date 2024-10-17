package be.pxl.services.controller;

import be.pxl.services.model.Cart;
import be.pxl.services.model.dto.CartItemDTO;
import be.pxl.services.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        return new ResponseEntity<>(cartService.getCart(id), HttpStatus.OK);
    }

    @PostMapping("/add/{cartId}")
    public ResponseEntity<Cart> addItemToCart(@RequestBody CartItemDTO cartItemDTO, @PathVariable Long cartId) {
        Cart updatedCart = cartService.addItem(cartItemDTO, cartId);
        return ResponseEntity.ok(updatedCart);
    }
}
