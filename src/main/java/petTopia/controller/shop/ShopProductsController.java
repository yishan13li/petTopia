package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.List;

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

import petTopia.dto.shop.ProductDetailDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;

@Controller
@RequestMapping("/shop/products")
public class ShopProductsController {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductDetailService productDetailService;
	
	
	// 商品瀏覽頁面
	@GetMapping
	public String showShopProducts(Model model) {
		
		// 獲取商品資訊(ProductDetail)
		List<ProductDetail> productDetailList = productDetailService.findAll();
		
		// 獲取ProductDetail一樣的所有Poduct，選出最低價
		List<ProductDetailDto> productDetailDtoList = new ArrayList<ProductDetailDto>();
		for (ProductDetail productDetail : productDetailList) {
			
			ProductDetailDto productDetailDto = new ProductDetailDto();
			Product minPriceProduct = productService.findMinPriceProduct(productDetail.getId());
			
			productDetailDto.setProductDetail(productDetail);
			productDetailDto.setUnitPrice(minPriceProduct.getUnitPrice());
			productDetailDtoList.add(productDetailDto);
		}
		
		model.addAttribute("productDetailDtoList", productDetailDtoList);
		
		return "shop/shop_products";
	}
	
	// 商品瀏覽頁面 => 獲取商品資訊的第一個商品的圖片 
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productDetailId) {
		
		Product product = productService.findFirstByProductDetailId(productDetailId);
		
		byte[] photo = product.getPhoto();
		
		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	
	
	
}
