package be.pxl.services.services;

import be.pxl.services.model.Cart;
import be.pxl.services.model.dto.CartItemDTO;


public interface ICartService {
    Cart getCart(Long id);

    Cart addItem(CartItemDTO cartItemDTO, Long cartId);

    Cart getCartByUserId(Long userId);

    Cart createCart(Long userId);

    Cart addToCart(Long userId, Long productId);
    Cart removeFromCart(Long userId, Long productId);
}
