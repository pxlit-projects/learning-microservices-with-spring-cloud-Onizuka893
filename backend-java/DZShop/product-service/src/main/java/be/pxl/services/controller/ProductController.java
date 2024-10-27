package be.pxl.services.controller;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductDTO;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public ResponseEntity getProduct() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        var products = productService.getAllProducts();

        for (Product product : products) {
            ProductDTO productDTO = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .stock(product.getStock())
                    .build();

            productDTOList.add(productDTO);
        }

        return new ResponseEntity(productDTOList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody ProductDTO product) {
        Product prodToAdd = Product.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return new ResponseEntity(productService.addProduct(prodToAdd), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody ProductDTO product) {
        Product prodToUpdate = Product.builder()
                .id(id)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();

        var updated = productService.updateProduct(prodToUpdate);

        ProductDTO DTO = ProductDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .stock(updated.getStock())
                .build();

        return new ResponseEntity(DTO, HttpStatus.OK);
    }
}
