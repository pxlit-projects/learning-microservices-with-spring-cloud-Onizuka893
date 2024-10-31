package be.pxl.services.controller;

import be.pxl.services.model.Cart;
import be.pxl.services.model.dto.CartDTO;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        Cart cartById = cartService.getCartByUserId(userId);
        CartDTO cartDTO = CartDTO.builder()
                .userId(cartById.getUserId())
                .id(cartById.getId())
                .total(cartById.GetTotal())
                .items(cartById.getCartItems().stream().map(CartItemDTO::new).toList())
                .build();
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartDTO> createCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        cart = cartService.createCart(userId);
        CartDTO DTO = CartDTO.builder()
                .userId(cart.getUserId())
                .id(cart.getId())
                .total(cart.GetTotal())
                .items(cart.getCartItems().stream().map(CartItemDTO::new).toList())
                .build();
        return ResponseEntity.ok(DTO);
    }

    @PostMapping("/user/{userId}/{productId}/add")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            cartService.createCart(userId);
        }
        cart = cartService.addToCart(userId, productId);
        CartDTO DTO = CartDTO.builder()
                .userId(cart.getUserId())
                .id(cart.getId())
                .total(cart.GetTotal())
                .items(cart.getCartItems().stream().map(CartItemDTO::new).toList())
                .build();
        return ResponseEntity.ok(DTO);
    }

    @PostMapping("/user/{userId}/{productId}/remove")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        cart = cartService.removeFromCart(userId, productId);
        CartDTO DTO = CartDTO.builder()
                .userId(cart.getUserId())
                .id(cart.getId())
                .total(cart.GetTotal())
                .items(cart.getCartItems().stream().map(CartItemDTO::new).toList())
                .build();
        return ResponseEntity.ok(DTO);
    }

}