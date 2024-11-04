package be.pxl.services.services;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductRequest;
import be.pxl.services.domain.ProductResponse;
import be.pxl.services.repository.ProductRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
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
        return productRepository.findAll().stream().map(this::mapToProductResponse).toList();
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        product.removeFromStock();
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse removeProductFromCart(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        product.addToStock();
        return mapToProductResponse(productRepository.save(product));
    }
}
