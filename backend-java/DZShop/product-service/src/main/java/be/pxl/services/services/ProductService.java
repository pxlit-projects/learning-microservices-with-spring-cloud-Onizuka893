package be.pxl.services.services;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductRequest;
import be.pxl.services.domain.ProductResponse;
import be.pxl.services.repository.ProductRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Get all products");
        return productRepository.findAll().stream().map(this::mapToProductResponse).toList();
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        log.info("Add product with request: {}", productRequest);
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .build();
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long productId,ProductRequest productRequest) {
        log.info("Update product with id: {} and request {}", productId, productRequest);
        return mapToProductResponse( productRepository.findById(productId)
                .map(p -> {
                    p.setName(productRequest.getName());
                    p.setDescription(productRequest.getDescription());
                    p.setPrice(productRequest.getPrice());
                    p.setStock(productRequest.getStock());
                    return productRepository.save(p);
                })
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found")));
    }

    @Override
    public ProductResponse addProductToCart(Long productId) {
        log.info("Add product to cart with id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        product.removeFromStock();
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse removeProductFromCart(Long productId) {
        log.info("Remove product from cart with id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        product.addToStock();
        return mapToProductResponse(productRepository.save(product));
    }
}
