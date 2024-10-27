package be.pxl.services.controller;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.CategoryDTO;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductDTO;
import be.pxl.services.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity getAllCategories() {
        List<CategoryDTO> categoriesDTO = new ArrayList<>();
        var categories = categoryService.getAllCategories();

        for (Category category : categories) {
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (Product prod: category.getProducts()) {
                productDTOS.add(ProductDTO.builder()
                                .id(prod.getId())
                                .stock(prod.getStock())
                                .description(prod.getDescription())
                                .name(prod.getName())
                                .price(prod.getPrice())
                        .build());
            }

            categoriesDTO.add(CategoryDTO.builder()
                    .id(category.getId())
                            .products(productDTOS)
                    .name(category.getName()).build());
        }

        return new ResponseEntity(categoriesDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryDTO category) {
        Category catToAdd = Category.builder()
                .name(category.getName())
                .build();
        return new ResponseEntity(categoryService.addCategory(catToAdd), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCategory(@PathVariable Long id, @RequestBody CategoryDTO category) {
        Category catToUpdate = Category.builder()
                .id(id)
                .name(category.getName())
                .build();

        Category updated = categoryService.updateCategory(catToUpdate);

        CategoryDTO DTO = CategoryDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .build();

        return new ResponseEntity(DTO, HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity addProductToCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {

        Category updatedCategory = categoryService.addProductToCategory(categoryId, productId);

        CategoryDTO DTO = CategoryDTO.builder()
                .id(updatedCategory.getId())
                .name(updatedCategory.getName())
                .build();
        return ResponseEntity.ok(DTO);
    }

    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity removeProductFromCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {

        Category updatedCategory = categoryService.removeProductFromCategory(categoryId,productId);
        CategoryDTO DTO = CategoryDTO.builder()
                .id(updatedCategory.getId())
                .name(updatedCategory.getName())
                .build();
        return ResponseEntity.ok(DTO);
    }
}
