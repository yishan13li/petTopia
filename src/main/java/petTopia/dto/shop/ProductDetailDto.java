package petTopia.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {

	private ProductDetail productDetail;
	
	private Product minPriceProduct;
	
	private Double avgRating;
	
}
