package be.pxl.services.domain;

import be.pxl.services.exceptions.StockEmptyException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)  // Store the enum as a string in the database
    private EnergyRating energyRating;
    private Double price;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public void removeFromStock() {
        if (this.stock <= 0) throw new StockEmptyException("Stock is empty");

        this.stock--;
    }

    public void addToStock() {
        this.stock++;
    }
}
