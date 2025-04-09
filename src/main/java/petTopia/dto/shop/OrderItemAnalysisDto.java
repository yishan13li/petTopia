package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemAnalysisDto {
	private Integer orderId;
	private Integer productId;
	private Integer productDetailId;
	private String productName;
	private String productColor;
	private String productSize;
    private Integer quantity;
    private double unitPrice;
    private double discountPrice;
    private double totalPrice;
}
