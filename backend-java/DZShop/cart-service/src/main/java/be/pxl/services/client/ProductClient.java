package be.pxl.services.client;

import be.pxl.services.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/{productId}/add")
    Product addProductToCart(@PathVariable Long productId);

    @GetMapping("/{productId}/remove")
    Product removeProductFromCart(@PathVariable Long productId);
}
