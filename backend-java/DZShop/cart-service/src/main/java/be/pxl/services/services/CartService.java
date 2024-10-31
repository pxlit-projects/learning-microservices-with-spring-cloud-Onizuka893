package be.pxl.services.services;

import be.pxl.services.model.Cart;
import be.pxl.services.model.CartItem;
import be.pxl.services.model.dto.CartItemDTO;
import be.pxl.services.repository.CartItemRepository;
import be.pxl.services.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findAll().stream().filter(cart -> cart.getUserId().equals(userId)).findFirst().orElse(null);
    }

    @Override
    public Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    @Override
    public Cart addToCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        List<CartItem> cartItems = cartItemRepository.findAll().stream().filter(cartItem -> cartItem.getCart().equals(cart)).toList();

        CartItem product = cartItems.stream().filter(productItem -> productItem.getProductId().equals(productId)).findFirst().orElse(null);
        if (product == null) {
            CartItem cartItem = CartItem.builder()
                    .quantity(1)
                    .productId(productId)
                    .cart(cart)
                    .build();
            cartItem = cartItemRepository.save(cartItem);
            cart.AddCartItem(cartItem);
            return cartRepository.save(cart);
        }

        cart.getCartItems().remove(product);
        product.setQuantity(product.getQuantity() + 1);
        product = cartItemRepository.save(product);
        cart.getCartItems().add(product);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        List<CartItem> cartItems = cartItemRepository.findAll().stream().filter(cartItem -> cartItem.getCart().equals(cart)).toList();
        CartItem itemToRemove = cartItems.stream().filter(cartItem -> cartItem.getProductId().equals(productId)).findFirst().orElse(null);

        if (itemToRemove != null) {

            if (itemToRemove.getQuantity() == 1) {
                cart.getCartItems().remove(itemToRemove);
                cartItemRepository.delete(itemToRemove);
                return cartRepository.save(cart);
            }

            cart.getCartItems().remove(itemToRemove);
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
            cart.getCartItems().add(cartItemRepository.save(itemToRemove));

            return cartRepository.save(cart);
        } else {
            throw new RuntimeException("No cart item found");
        }

    }
}
