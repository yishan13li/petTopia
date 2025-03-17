package petTopia.repository.shop;

import java.util.List;

import petTopia.model.shop.ProductDetail;

public interface  ProductDetailRepositoryCustom{

	// 模糊搜尋 (多個關鍵字)
	public List<ProductDetail> searchProducts(String keywords);

	
}
