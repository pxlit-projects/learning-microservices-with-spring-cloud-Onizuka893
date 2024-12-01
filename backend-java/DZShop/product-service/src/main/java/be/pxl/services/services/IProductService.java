package be.pxl.services.services;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.ProductRequest;
import be.pxl.services.domain.ProductResponse;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getAllProducts();

    List<ProductResponse> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice, String rating);

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest);

    ProductResponse addProductToCart(Long productId);
    ProductResponse removeProductFromCart(Long productId);
}
