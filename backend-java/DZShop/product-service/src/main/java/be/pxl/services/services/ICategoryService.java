package be.pxl.services.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.CategoryRequest;
import be.pxl.services.domain.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse addCategory(CategoryRequest catToAdd);

    CategoryResponse updateCategory(Long categoryId,CategoryRequest catToUpdate);

    CategoryResponse addProductToCategory(Long categoryId, Long productId);

    CategoryResponse removeProductFromCategory(Long categoryId, Long productId);
}
