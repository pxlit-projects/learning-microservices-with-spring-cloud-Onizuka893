package be.pxl.services.services;

import be.pxl.services.domain.Product;
import be.pxl.services.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
   private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Long addProduct(Product prodToAdd) {
        return productRepository.save(prodToAdd).getId();
    }

    @Override
    public Product updateProduct(Product prodToUpdate) {
        return productRepository.findById(prodToUpdate.getId())
                .map(p -> {
                    p.setName(prodToUpdate.getName());
                    p.setDescription(prodToUpdate.getDescription());
                    p.setPrice(prodToUpdate.getPrice());
                    p.setStock(prodToUpdate.getStock());
                    return productRepository.save(p);
                })
                .orElseGet(() -> productRepository.save(prodToUpdate));
    }
}
