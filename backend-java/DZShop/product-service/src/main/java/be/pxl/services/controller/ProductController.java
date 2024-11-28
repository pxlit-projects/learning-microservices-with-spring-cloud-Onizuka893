package be.pxl.services.controller;

import be.pxl.services.domain.ProductRequest;
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

    @GetMapping
    public ResponseEntity getProduct() {
        log.info("Get products endpoint called");
        return new ResponseEntity(productService.getAllProducts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Create product endpoint called with body {}", productRequest);
        return new ResponseEntity(productService.addProduct(productRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        log.info("Update product endpoint called with productId {} and body {}", id, productRequest);
        return new ResponseEntity(productService.updateProduct(id,productRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}/add")
    public ResponseEntity addProduct(@PathVariable Long id) {
        log.info("Add product endpoint called with productId {}", id);
        return new ResponseEntity(productService.addProductToCart(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/remove")
    public ResponseEntity removeProduct(@PathVariable Long id) {
        log.info("Remove product endpoint called with productId {}", id);
        return new ResponseEntity(productService.removeProductFromCart(id), HttpStatus.OK);
    }
}
