package be.pxl.services.services;

import be.pxl.services.client.ProductClient;
import be.pxl.services.exceptions.NotFoundException;
import be.pxl.services.exceptions.ProductClientException;
import be.pxl.services.model.Cart;
import be.pxl.services.model.CartItem;
import be.pxl.services.model.Product;
import be.pxl.services.model.dto.CartItemResponse;
import be.pxl.services.model.dto.CartResponse;
import be.pxl.services.repository.CartItemRepository;
import be.pxl.services.repository.CartRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final RabbitMQProducer rabbitMQProducer;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final HeaderValidationService headerValidationService;

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
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
        log.info("Getting cart with id [{}]", id);
        Cart cart =  cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("cart with id [" + id + "] not found "));
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        log.info("Getting cart with userId [{}]", userId);
        rabbitMQProducer.sendMessage("getting cart for userId [" + userId + "]" + " invoked by user: " + headerValidationService.user);
        Cart cart = getCartByUserIdHelper(userId);

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse createCart(Long userId) {
        //check if cart already exist with userID
        log.info("Creating cart for userId [{}]", userId);
        rabbitMQProducer.sendMessage("Creating cart for userId " + userId + " invoked by user: " + headerValidationService.user);
        Cart cart = new Cart();
        cart.setUserId(userId);
        return mapToCartResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse addToCart(Long userId, Long productId) {
        log.info("Adding product to cart belonging to userId [{}] with product id [{}]", userId, productId);
        rabbitMQProducer.sendMessage("Adding product to cart belonging to userId " + userId + " with product id " +  productId + " invoked by user: " + headerValidationService.user);
        Cart cart = getCartByUserIdHelper(userId);
        Product productFromProductService;

        try {
            productFromProductService = productClient.addProductToCart(productId);
        } catch (FeignException e) {
            throw new ProductClientException(e.getMessage().split(":")[2]);
        }

        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(cartItem -> cartItem.getCart().equals(cart)).toList();

        CartItem product = cartItems.stream()
                .filter(productItem -> productItem.getProductId().equals(productId)).findFirst().orElse(null);

        if (product == null) {
            CartItem cartItem = CartItem.builder()
                    .quantity(1)
                    .productId(productFromProductService.getId())
                    .productName(productFromProductService.getName())
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
        log.info("Removing product from cart belonging to userId [{}] with product id [{}]", userId, productId);
        rabbitMQProducer.sendMessage("Removing product from cart belonging to userId " + userId + " with product id " +  productId + " invoked by user: " + headerValidationService.user);
        Cart cart = getCartByUserIdHelper(userId);

        try {
            productClient.removeProductFromCart(productId);
        } catch (FeignException e) {
            throw new ProductClientException(e.getMessage().split(":")[2]);
        }

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
