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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;
import petTopia.model.shop.ProductPhoto;
import petTopia.model.shop.ProductPhotoRepository;
import petTopia.model.shop.ProductRepository;
import petTopia.service.shop.ProductPhotoService;
import petTopia.service.shop.ProductService;




@Controller
public class ShopProductsController {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductPhotoService productPhotoService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductDetailRepository productDetailRepository;
	@Autowired
	private ProductPhotoRepository productPhotoRepository;
	
	// 商品瀏覽頁面
	@GetMapping("/shop/products")
	public String showShopProducts(Model model) {
		
		//TODO: 獲取Product，從中選出最低價放到瀏覽頁面
		
		// 獲取商品資訊(ProductDetail)
		List<ProductDetail> allProductDetail = productDetailRepository.findAll();
		
		if (allProductDetail != null && allProductDetail.size() != 0) {
			model.addAttribute("allProductDetail", allProductDetail);
			
		}
		
		return "shop/shop_products";
	}
	
	// 商品瀏覽頁面 => 獲取商品第一張圖片 
	@GetMapping("/api/shop/products/getPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productDetailId) {
		System.out.println("/api/product/getPhoto");
		ProductPhoto productPhoto = productPhotoRepository.findFirstByProductDetailIdOrderByIdAsc(productDetailId);
		
		if (productPhoto != null) {
			byte[] photo = productPhoto.getPhoto();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	// 商品照片上傳 (暫時用)
	@PostMapping("/shop/product/uploadPhoto")
	public String postMethodName(
			@RequestParam Integer productDetailId, 
			@RequestParam MultipartFile file, 
			Model model) throws IOException {

		Optional<ProductDetail> productDetailOpt = productDetailRepository.findById(productDetailId);
		if (productDetailOpt.isPresent()) {
			ProductPhoto productPhoto = new ProductPhoto();
			productPhoto.setProductDetail(productDetailOpt.get());
			productPhoto.setPhoto(file.getBytes());
			
			productPhotoRepository.save(productPhoto);
		}
		
		return "shop/shop_products";
	}
	
	
	
}
