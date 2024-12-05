package be.pxl.services.controller;

import be.pxl.services.domain.*;
import be.pxl.services.services.CategoryService;
import be.pxl.services.services.HeaderValidationService;
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
    private final HeaderValidationService headerValidationService;

    @GetMapping
    public ResponseEntity getAllCategories() {
        log.info("Get all categories endpoint called");
        return new ResponseEntity(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createCategory(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@RequestBody CategoryRequest categoryRequest) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Create category endpoint called with body {}", categoryRequest);
            return new ResponseEntity(categoryService.addCategory(categoryRequest), HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCategory(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Update category endpoint called with categoryId {} and body {}", id, categoryRequest);
            return new ResponseEntity(categoryService.updateCategory(id,categoryRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCategory(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long id) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Delete category endpoint called with categoryId {}", id);
            categoryService.deleteCategory(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity addProductToCategory(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader ,
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Add product to category endpoint called with categoryId {} and productId {}", categoryId, productId);
            return ResponseEntity.ok(categoryService.addProductToCategory(categoryId, productId));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity removeProductFromCategory(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader ,
            @PathVariable Long categoryId,
            @PathVariable Long productId) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Remove product from category endpoint called with categoryId {} and productId {}", categoryId, productId);
            return ResponseEntity.ok(categoryService.removeProductFromCategory(categoryId,productId));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

}
