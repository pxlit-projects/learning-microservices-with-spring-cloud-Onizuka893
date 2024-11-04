package be.pxl.services.model;

import be.pxl.services.model.dto.CartItemResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;
    private Long productId;
    private int quantity;
    private double price;

    public double GetTotal() {
        return quantity * price;
    }
}
