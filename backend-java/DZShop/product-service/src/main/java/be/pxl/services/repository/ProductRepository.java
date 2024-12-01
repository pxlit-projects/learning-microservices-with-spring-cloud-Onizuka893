package be.pxl.services.repository;

import be.pxl.services.domain.EnergyRating;
import be.pxl.services.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:searchTerm IS NULL OR LOWER(CAST(p.name AS String)) LIKE LOWER(CAST(CONCAT('%', :searchTerm, '%') AS String)) " +
            "OR LOWER(CAST(p.description AS String)) LIKE LOWER(CAST(CONCAT('%', :searchTerm, '%') AS String))) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)" +
            "AND (:rating IS NULL OR p.energyRating = :rating)")
    List<Product> search(@Param("searchTerm") String searchTerm,
                         @Param("category") String category,
                         @Param("minPrice") Double minPrice,
                         @Param("maxPrice") Double maxPrice,
                         @Param("rating") EnergyRating rating);
}
