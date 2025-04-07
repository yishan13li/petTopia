package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryAmoutDto {

	 	private BigDecimal subtotal;   // 商品總金額
	    private BigDecimal discountAmount;   // 折扣金額
	    private BigDecimal shippingFee; // 運費
	    private BigDecimal orderTotal;      // 訂單總金額
}
