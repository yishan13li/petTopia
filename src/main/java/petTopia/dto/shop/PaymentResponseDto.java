package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponseDto {
	
    private boolean success;
    private BigDecimal paymentAmount;
}
