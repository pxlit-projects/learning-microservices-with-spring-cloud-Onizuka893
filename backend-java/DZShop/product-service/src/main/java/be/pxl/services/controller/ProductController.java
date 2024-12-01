package be.pxl.services.controller;

import be.pxl.services.domain.ProductRequest;
import be.pxl.services.services.HeaderValidationService;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
//@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final IProductService productService;
    private final HeaderValidationService headerValidationService;

    @GetMapping
    public ResponseEntity getProduct() {
        log.info("Get products endpoint called");
        return new ResponseEntity(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity searchProducts(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "rating", required = false) String rating) {

        return new ResponseEntity(productService.searchProducts(searchTerm, category, minPrice, maxPrice, rating), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@RequestBody ProductRequest productRequest) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Create product endpoint called with body {}", productRequest);
            return new ResponseEntity(productService.addProduct(productRequest), HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        boolean isAdmin = headerValidationService.hasAdminRole(authorizationHeader);
        if (isAdmin) {
            log.info("Update product endpoint called with productId {} and body {}", id, productRequest);
            return new ResponseEntity(productService.updateProduct(id, productRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}/add")
    public ResponseEntity addProduct(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long id) {
        log.info("Add product endpoint called with productId {}", id);
        return new ResponseEntity(productService.addProductToCart(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/remove")
    public ResponseEntity removeProduct(@RequestHeader(value = "Authorization", required = false) String authorizationHeader ,@PathVariable Long id) {
        log.info("Remove product endpoint called with productId {}", id);
        return new ResponseEntity(productService.removeProductFromCart(id), HttpStatus.OK);
    }
}
