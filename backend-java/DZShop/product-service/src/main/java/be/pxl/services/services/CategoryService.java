package be.pxl.services.services;

import be.pxl.services.domain.*;
import be.pxl.services.repository.CategoryRepository;
import be.pxl.services.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final HeaderValidationService headerValidationService;
    private final ProductRepository productRepository;
    private final RabbitMQProducer rabbitMQProducer;

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .products(category.getProducts().stream().map(this::mapToProductResponse).toList())
                .build();
    }

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
    public List<CategoryResponse> getAllCategories() {
        log.info("Get all categories");
        return categoryRepository.findAll().stream().map(this::mapToCategoryResponse).toList();
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        log.info("Add category with request {}", categoryRequest);
        rabbitMQProducer.sendMessage("Add category with request [" + categoryRequest + "]" + " invoked by user: " + headerValidationService.user);
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .products(new ArrayList<>())
                .build();
        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest catToUpdate) {
        log.info("Update category with id {} and request {}", categoryId, catToUpdate);
        rabbitMQProducer.sendMessage("Update category with id [" + categoryId + "] with request [" + catToUpdate + "]" + " invoked by user: " + headerValidationService.user);
        return mapToCategoryResponse( categoryRepository.findById(categoryId)
                .map(c -> {
                    c.setName(catToUpdate.getName());
                    return categoryRepository.save(c);
                })
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name(catToUpdate.getName())
                                .build()
                )));
    }

    @Override
    public CategoryResponse addProductToCategory(Long categoryId, Long productId) {
        log.info("Add product to a category with id {} and request {}", categoryId, productId);
        rabbitMQProducer.sendMessage("Add product to category with id [" + categoryId + "] with request [" + productId + "]" + " invoked by user: " + headerValidationService.user);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setCategory(category);
        category.getProducts().add(product);

        productRepository.save(product);
        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse removeProductFromCategory(Long categoryId, Long productId) {
        log.info("Remove product from a category with id {} and request {}", categoryId, productId);
        rabbitMQProducer.sendMessage("Remove product from category with id [" + categoryId + "] with request [" + productId + "]" + " invoked by user: " + headerValidationService.user);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!category.getProducts().contains(product)) {
            throw new NotFoundException("Product does not belong to this category");
        }

        category.getProducts().remove(product);
        product.setCategory(null);

        productRepository.save(product);
        return mapToCategoryResponse(categoryRepository.save(category));
    }
}
