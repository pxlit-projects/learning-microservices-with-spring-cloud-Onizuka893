package be.pxl.services.controller;

import be.pxl.services.domain.ProductRequest;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
//@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public ResponseEntity getProduct() {
        return new ResponseEntity(productService.getAllProducts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody ProductRequest productRequest) {
        return new ResponseEntity(productService.addProduct(productRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return new ResponseEntity(productService.updateProduct(id,productRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}/add")
    public ResponseEntity addProduct(@PathVariable Long id) {
        return new ResponseEntity(productService.addProductToCart(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/remove")
    public ResponseEntity removeProduct(@PathVariable Long id) {
        return new ResponseEntity(productService.removeProductFromCart(id), HttpStatus.OK);
    }
}
