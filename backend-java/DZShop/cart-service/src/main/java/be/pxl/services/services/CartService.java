package be.pxl.services.services;

import be.pxl.services.model.Cart;
import be.pxl.services.model.CartItem;
import be.pxl.services.model.dto.CartItemDTO;
import be.pxl.services.repository.CartItemRepository;
import be.pxl.services.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Override
    public Cart addItem(CartItemDTO cartItemDTO, Long cartId) {
        // Logic to find or create the cart
        Cart cart = cartRepository.findById(cartId).orElse(null);

        // Create a new CartItem and add it to the cart
        CartItem cartItem = new CartItem(cartItemDTO);
        cartItem.setCart(cart);

        cartItemRepository.save(cartItem);
        return cart;
    }
}
