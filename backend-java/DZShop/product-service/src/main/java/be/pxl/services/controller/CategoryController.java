package be.pxl.services.controller;

import be.pxl.services.domain.*;
import be.pxl.services.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity getAllCategories() {
        log.info("Get all categories endpoint called");
        return new ResponseEntity(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryRequest categoryRequest) {
        log.info("Create category endpoint called with body {}", categoryRequest);
        return new ResponseEntity(categoryService.addCategory(categoryRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        log.info("Update category endpoint called with categoryId {} and body {}", id, categoryRequest);
        return new ResponseEntity(categoryService.updateCategory(id,categoryRequest), HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity addProductToCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        log.info("Add product to category endpoint called with categoryId {} and productId {}", categoryId, productId);
        return ResponseEntity.ok(categoryService.addProductToCategory(categoryId, productId));
    }

    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity removeProductFromCategory(
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        log.info("Remove product from category endpoint called with categoryId {} and productId {}", categoryId, productId);
        return ResponseEntity.ok(categoryService.removeProductFromCategory(categoryId,productId));
    }
}
