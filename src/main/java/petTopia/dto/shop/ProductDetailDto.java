package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petTopia.model.shop.ProductDetail;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {

	private ProductDetail productDetail;
	
	private BigDecimal unitPrice;
	
}
