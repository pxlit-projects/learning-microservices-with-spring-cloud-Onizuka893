package be.pxl.services.services;

import be.pxl.services.client.ProductClient;
import be.pxl.services.model.Cart;
import be.pxl.services.model.CartItem;
import be.pxl.services.model.Product;
import be.pxl.services.model.dto.CartItemResponse;
import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.repository.CartItemRepository;
import be.pxl.services.repository.CartRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .total(cart.GetTotal())
                .items(cart.getCartItems().stream().map(this::mapToCartItemResponse).toList())
                .build();
    }

    private Cart getCartByUserIdHelper(long userId) {
        return cartRepository.findAll().stream()
                .filter(c -> c.getUserId().equals(userId)).findFirst()
                .orElseThrow(() -> new NotFoundException("cart belonging to this userid [" + userId + "] not found "));
    }

    @Override
    public CartResponse getCart(Long id) {
        Cart cart =  cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("cart with id [" + id + "] not found "));
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = getCartByUserIdHelper(userId);

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return mapToCartResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse addToCart(Long userId, Long productId) {
        Cart cart = getCartByUserIdHelper(userId);
        Product productFromProductService = productClient.addProductToCart(productId);

        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(cartItem -> cartItem.getCart().equals(cart)).toList();

        CartItem product = cartItems.stream()
                .filter(productItem -> productItem.getProductId().equals(productId)).findFirst().orElse(null);

        if (product == null) {
            CartItem cartItem = CartItem.builder()
                    .quantity(1)
                    .productId(productFromProductService.getId())
                    .price(productFromProductService.getPrice())
                    .cart(cart)
                    .build();
            cartItem = cartItemRepository.save(cartItem);
            cart.AddCartItem(cartItem);
            return mapToCartResponse(cartRepository.save(cart));
        }

        cart.getCartItems().remove(product);
        product.setQuantity(product.getQuantity() + 1);
        product = cartItemRepository.save(product);
        cart.getCartItems().add(product);
        return mapToCartResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserIdHelper(userId);
        productClient.removeProductFromCart(productId);

        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(cartItem -> cartItem.getCart().equals(cart)).toList();
        CartItem itemToRemove = cartItems.stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId)).findFirst().orElse(null);

        if (itemToRemove != null) {
            if (itemToRemove.getQuantity() == 1) {
                cart.getCartItems().remove(itemToRemove);
                cartItemRepository.delete(itemToRemove);
                return mapToCartResponse(cartRepository.save(cart));
            }

            cart.getCartItems().remove(itemToRemove);
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
            cart.getCartItems().add(cartItemRepository.save(itemToRemove));

            return mapToCartResponse(cartRepository.save(cart));
        } else {
            throw new RuntimeException("No cart item found");
        }

    }
}
