package be.pxl.services.model.dto;

import be.pxl.services.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long productId;
    private Integer quantity;
    private Double price;

    public CartItemDTO(CartItem cartItem) {
        this.productId = cartItem.getProductId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
    }
}
