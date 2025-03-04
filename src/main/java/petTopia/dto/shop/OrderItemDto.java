package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.shop.ProductPhoto;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {
	private byte[] productPhoto;
	private String productSize;
	private String productColor;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;
}
