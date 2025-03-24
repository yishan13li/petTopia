package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOneOrderDto {
	
    private String orderStatus;
    private String paymentStatus;
    private String paymentCategory;
    private String shippingCategory;
    private String note;
    private BigDecimal totalAmount;

}
