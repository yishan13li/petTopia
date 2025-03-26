package petTopia.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto2 {

	private ProductCategoryDto productCategory;
	
	private String name;
	private String description;
	
}
