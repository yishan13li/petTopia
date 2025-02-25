package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.repository.shop.ProductDetailRepository;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;

@Controller
@RequestMapping("/shop/productDetail")
public class ShopProductDetailController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductDetailService productDetailService;
	
	// 商品詳情頁面
	@GetMapping
	public String showShopProductDetail(
			@RequestParam Integer productDetailId, Model model) {
		
		// 獲取ProductDetail一樣的所有Poduct
		List<Product> productList = productService.findByProductDetailId(productDetailId);
		// 獲取productList內最低和最高價商品
		Product minPriceProduct = productService.findMinPriceProduct(productDetailId);
		Product maxPriceProduct = productService.findMaxPriceProduct(productDetailId);
		
		// 獲取商品資訊(ProductDetail)
		ProductDetail productDetail = productDetailService.findByProductDetailId(productDetailId);
		
		// 獲取尺寸和顏色的List
		List<String> sizeList = new ArrayList<String>();
		List<String> colorList = new ArrayList<String>();
		
		for (Product product : productList) {
			if (product.getProductSize() != null) {
				sizeList.add(product.getProductSize().getName());
			}
			if (product.getProductColor() != null) {
				colorList.add(product.getProductColor().getName());
			}
			
		}
		
		model.addAttribute("productList", productList);
		model.addAttribute("sizeList", sizeList);
		model.addAttribute("colorList", colorList);
		model.addAttribute("minPriceProduct", minPriceProduct);
		model.addAttribute("maxPriceProduct", maxPriceProduct);
		model.addAttribute("productDetail", productDetail);
		
		return "shop/shop_product_detail";
	}
	
	// 商品詳情頁面 => 獲取商品資訊的圖片 
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productId) {
		
		Product product = productService.findById(productId);
		
		byte[] photo = product.getPhoto();
		
		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
		
}
