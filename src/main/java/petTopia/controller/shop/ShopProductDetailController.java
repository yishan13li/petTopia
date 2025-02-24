package petTopia.controller.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;
import petTopia.service.shop.ProductDetailService;

@Controller
@RequestMapping("/shop/productDetail")
public class ShopProductDetailController {

	@Autowired
	private ProductDetailService productDetailService;
	
	// 商品詳情頁面
	@GetMapping
	public String showShopProductDetail(
			@RequestParam Integer productDetailId, Model model) {
		
		//TODO: 獲取Poduct => 價錢顯示($最低價 - $最高價)
		
		
		// 獲取商品資訊(ProductDetail)
		ProductDetail productDetail = productDetailService.findByProductDetailId(productDetailId);
		
		model.addAttribute("productDetail", productDetail);
		
		return "shop/shop_product_detail";
	}
		
}
