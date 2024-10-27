package be.pxl.services.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Product;
import be.pxl.services.repository.CategoryRepository;
import be.pxl.services.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Long addCategory(Category catToAdd) {
        return categoryRepository.save(catToAdd).getId();
    }

    @Override
    public Category updateCategory(Category catToUpdate) {
        return categoryRepository.findById(catToUpdate.getId())
                .map(c -> {
                    c.setName(catToUpdate.getName());
                    return categoryRepository.save(c);
                })
                .orElseGet(() -> categoryRepository.save(catToUpdate));
    }

    @Override
    public Category addProductToCategory(Long categoryId, Long productId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setCategory(category);
        category.getProducts().add(product);

        productRepository.save(product);
        return categoryRepository.save(category);
    }

    @Override
    public Category removeProductFromCategory(Long categoryId, Long productId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!category.getProducts().contains(product)) {
            throw new IllegalArgumentException("Product does not belong to this category");
        }

        category.getProducts().remove(product);
        product.setCategory(null);

        productRepository.save(product);
        return categoryRepository.save(category);
    }
}
