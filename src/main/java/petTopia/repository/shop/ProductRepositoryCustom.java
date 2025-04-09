package petTopia.repository.shop;

import java.util.List;

import org.json.JSONObject;

import petTopia.model.shop.Product;

public interface  ProductRepositoryCustom{

	// 根據條件搜尋商品的總數
	public long count(JSONObject obj);
	
	// 根據條件搜尋商品
	public List<Product> find(JSONObject obj);

	
}
