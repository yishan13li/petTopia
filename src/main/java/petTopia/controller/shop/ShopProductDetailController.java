package petTopia.controller.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;

@Controller
public class ShopProductDetailController {

	@Autowired
	private ProductDetailRepository productDetailRepository;
	
	// 商品詳情頁面
	@GetMapping("/shop/productsDetail")
	public String showShopProductDetail(
			@RequestParam Integer productDetailId, Model model) {
		
		//TODO: 獲取Poduct => 價錢顯示($最低價 - $最高價)
		
		
		// 獲取商品資訊(ProductDetail)
		Optional<ProductDetail> productDetailOpt = productDetailRepository.findById(productDetailId);
		if (productDetailOpt.isPresent()) {
			ProductDetail productDetail = productDetailOpt.get();
			model.addAttribute("productDetail", productDetail);
			
		}
		
		return "shop/shop_product_detail";
	}
		
}
