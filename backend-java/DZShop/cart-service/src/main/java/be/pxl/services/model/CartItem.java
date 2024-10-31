package be.pxl.services.model;

import be.pxl.services.model.dto.CartItemDTO;
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

    public CartItem(CartItemDTO cartItemDTO) {
        this.productId = cartItemDTO.getProductId();
        this.quantity = cartItemDTO.getQuantity();
        this.price = cartItemDTO.getPrice();
    }

    public double GetTotal() {
        return quantity * price;
    }
}
