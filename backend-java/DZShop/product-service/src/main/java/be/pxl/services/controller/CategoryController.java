package be.pxl.services.controller;

import be.pxl.services.domain.*;
import be.pxl.services.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity getAllCategories() {
        return new ResponseEntity(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity(categoryService.addCategory(categoryRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity(categoryService.updateCategory(id,categoryRequest), HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity addProductToCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(categoryService.addProductToCategory(categoryId, productId));
    }

    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity removeProductFromCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(categoryService.removeProductFromCategory(categoryId,productId));
    }
}
