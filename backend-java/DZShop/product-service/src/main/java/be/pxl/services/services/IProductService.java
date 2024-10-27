package be.pxl.services.services;

import be.pxl.services.domain.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();

    Long addProduct(Product prodToAdd);

    Product updateProduct(Product prodToUpdate);
}
