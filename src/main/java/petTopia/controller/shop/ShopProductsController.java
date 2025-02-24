package petTopia.controller.shop;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;
import petTopia.model.shop.ProductPhoto;
import petTopia.model.shop.ProductPhotoRepository;
import petTopia.model.shop.ProductRepository;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductPhotoService;
import petTopia.service.shop.ProductService;

@Controller
@RequestMapping("/shop/products")
public class ShopProductsController {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductDetailService productDetailService;
	@Autowired
	private ProductPhotoService productPhotoService;
	
	
	// 商品瀏覽頁面
	@GetMapping
	public String showShopProducts(Model model) {
		
		//TODO: 獲取Product，從中選出最低價放到瀏覽頁面
		
		// 獲取商品資訊(ProductDetail)
		List<ProductDetail> allProductDetail = productDetailService.findAll();
		
		model.addAttribute("allProductDetail", allProductDetail);
		
		return "shop/shop_products";
	}
	
	// 商品瀏覽頁面 => 獲取商品資訊的第一個商品的圖片 
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productDetailId) {
		
		Product product = productService.findFirstByProductDetailId(productDetailId);
		
		ProductPhoto productPhoto = product.getProductPhoto();
		
		if (productPhoto != null) {
			byte[] photo = productPhoto.getPhoto();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	// 商品照片上傳 (暫時用)
	@PostMapping("/uploadPhoto")
	public String postMethodName(
			@RequestParam Integer productId, 
			@RequestParam MultipartFile file, 
			Model model) throws IOException {

		ProductPhoto productPhoto = productService.addProductPhotoByProductId(productId, file);
		
		
		return "shop/shop_products";
	}
	
	
	
}
