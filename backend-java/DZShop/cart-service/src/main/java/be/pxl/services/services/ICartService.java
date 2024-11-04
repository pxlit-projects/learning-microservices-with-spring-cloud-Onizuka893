package be.pxl.services.services;

import be.pxl.services.model.Cart;
import be.pxl.services.model.dto.CartItemResponse;
import be.pxl.services.model.dto.CartResponse;


public interface ICartService {
    CartResponse getCart(Long id);

    CartResponse getCartByUserId(Long userId);

    CartResponse createCart(Long userId);

    CartResponse addToCart(Long userId, Long productId);
    CartResponse removeFromCart(Long userId, Long productId);
}
