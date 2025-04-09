package petTopia.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private BigDecimal unitPrice;
	private BigDecimal discountPrice;
	private Integer stockQuantity;
	private Integer status;
	private byte[] photo;
	
	private ProductDetailDto2 productDetail;
    private ProductSizeDto productSize;
    private ProductColorDto productColor;
	
}
