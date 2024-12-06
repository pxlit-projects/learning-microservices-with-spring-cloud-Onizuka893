package be.pxl.services.services;

import be.pxl.services.domain.EnergyRating;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductRequest;
import be.pxl.services.domain.ProductResponse;
import be.pxl.services.exceptions.NotFoundException;
import be.pxl.services.exceptions.StockEmptyException;
import be.pxl.services.repository.CategoryRepository;
import be.pxl.services.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final HeaderValidationService headerValidationService;
    private final RabbitMQProducer rabbitMQProducer;

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .energyRating(product.getEnergyRating().toString())
                .stock(product.getStock())
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Get all products");
        return productRepository.findAll().stream().map(this::mapToProductResponse).toList();
    }

    public List<ProductResponse> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice, String rating) {
        log.info("Searching products with searchTerm: {}, category: {}, minPrice: {}, maxPrice: {}, rating: {}",
                searchTerm, category, minPrice, maxPrice, rating);

        if (rating != null && !rating.isEmpty()) {
            EnergyRating energyRating = EnergyRating.valueOf(rating);
            return productRepository.search(searchTerm, category, minPrice, maxPrice, energyRating)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        } else {
            return productRepository.search(searchTerm, category, minPrice, maxPrice, null)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        }

        // Call to repository to search for products based on filters
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        log.info("Add product with request: {}", productRequest);
        rabbitMQProducer.sendMessage("Add product with request [" + productRequest + "]" + " invoked by user: " + headerValidationService.user);
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .energyRating(EnergyRating.valueOf(productRequest.getEnergyRating()))
                .stock(productRequest.getStock())
                .build();
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long productId,ProductRequest productRequest) {
        log.info("Update product with id: {} and request {}", productId, productRequest);
        rabbitMQProducer.sendMessage("Update product with id [" + productId + "] with request [" + productRequest + "]" + " invoked by user: " + headerValidationService.user);
        return mapToProductResponse( productRepository.findById(productId)
                .map(p -> {
                    p.setName(productRequest.getName());
                    p.setDescription(productRequest.getDescription());
                    p.setPrice(productRequest.getPrice());
                    p.setEnergyRating(EnergyRating.valueOf(productRequest.getEnergyRating()));
                    p.setStock(productRequest.getStock());
                    return productRepository.save(p);
                })
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found")));
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("Delete product with id: {}", productId);
        rabbitMQProducer.sendMessage("Delete product with id [" + productId + "]" + " invoked by user: " + headerValidationService.user);

        // Fetch the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));

        if(product.getCategory() != null) {
            categoryService.removeProductFromCategory(product.getCategory().getId(), productId);
        }

        productRepository.deleteById(productId);
    }

    @Override
    public ProductResponse addProductToCart(Long productId) {
        log.info("Add product to cart with id: {}", productId);
        rabbitMQProducer.sendMessage("Add product with id [" + productId + "] to cart");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        try {
            product.removeFromStock();
        } catch (StockEmptyException e) {
            throw e;
        }
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse removeProductFromCart(Long productId) {
        log.info("Remove product from cart with id: {}", productId);
        rabbitMQProducer.sendMessage("Remove product with id [" + productId + "] from cart");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));

        product.addToStock();
        return mapToProductResponse(productRepository.save(product));
    }
}
