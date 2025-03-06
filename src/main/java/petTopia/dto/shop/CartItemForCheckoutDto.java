package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductSize;

@NoArgsConstructor
@Getter
@Setter
public class CartItemForCheckoutDto {
    private Integer cartId;
    private Integer productId;
    private String productDetailName;  // 只存 productDetail 的 name
    private ProductColor productColor;
    private ProductSize productSize;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPrice;
}
