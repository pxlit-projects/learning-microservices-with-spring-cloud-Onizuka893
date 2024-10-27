package be.pxl.services.services;

import be.pxl.services.domain.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();

    Long addCategory(Category catToAdd);

    Category updateCategory(Category catToUpdate);

    Category addProductToCategory(Long categoryId, Long productId);

    Category removeProductFromCategory(Long categoryId, Long productId);
}
