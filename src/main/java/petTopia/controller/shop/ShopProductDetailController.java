package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import petTopia.model.shop.Cart;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductSize;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;
import petTopia.service.user.MemberService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/shop/productDetail")
public class ShopProductDetailController {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MemberService memberService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductDetailService productDetailService;
	@Autowired
	private CartService cartService;
	
	// 商品詳情頁面 vue
	@GetMapping
	public ResponseEntity<?> showShopProductDetail(@RequestParam Integer productDetailId) {

		ObjectNode responseBody = objectMapper.createObjectNode();
		
		// 獲取ProductDetail一樣的所有Poduct
		List<Product> productList = productService.getAvailableProductByProductDetailId(productDetailId, true);
		
		// 獲取商品資訊(ProductDetail)
		ProductDetail productDetail = productDetailService.findByProductDetailId(productDetailId);

		// 獲取尺寸和顏色的List
		List<ProductSize> sizeList = new ArrayList<ProductSize>();
		List<ProductColor> colorList = new ArrayList<ProductColor>();
		// 總庫存
		Integer totalStockQuantity = 0;

		if (productList != null) {
			for (Product product : productList) {
				
				if (product.getProductSize() != null && !sizeList.contains(product.getProductSize())) {
					sizeList.add(product.getProductSize());
				}
				if (product.getProductColor() != null && !colorList.contains(product.getProductColor())) {
					colorList.add(product.getProductColor());
				}
				
				// 計算總庫存
				totalStockQuantity += product.getStockQuantity();

			}
			
			// 依照ID排序sizeList&colorList
			Collections.sort(sizeList, (ps1, ps2) -> Integer.compare(ps1.getId(), ps2.getId()));
			Collections.sort(colorList, (ps1, ps2) -> Integer.compare(ps1.getId(), ps2.getId()));
			
			// 獲取productList內最低和最高價商品
			Product minPriceProduct = productList.stream()
				    .min(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);
			
			Product maxPriceProduct = productList.stream()
				    .max(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);
			
			JsonNode productListJson = objectMapper.convertValue(productList, JsonNode.class);
			JsonNode sizeListJson = objectMapper.convertValue(sizeList, JsonNode.class);
			JsonNode colorListJson = objectMapper.convertValue(colorList, JsonNode.class);
			JsonNode minPriceProductJson = objectMapper.convertValue(minPriceProduct, JsonNode.class);
			JsonNode maxPriceProductJson = objectMapper.convertValue(maxPriceProduct, JsonNode.class);
			JsonNode productDetailJson = objectMapper.convertValue(productDetail, JsonNode.class);
			JsonNode totalStockQuantityJson = objectMapper.convertValue(totalStockQuantity, JsonNode.class);
			
			responseBody.set("productList", productListJson);
			responseBody.set("sizeList", sizeListJson);
			responseBody.set("colorList", colorListJson);
			responseBody.set("minPriceProduct", minPriceProductJson);
			responseBody.set("maxPriceProduct", maxPriceProductJson);
			responseBody.set("productDetail", productDetailJson);
			responseBody.set("totalStockQuantity", totalStockQuantityJson);
			
			return new ResponseEntity<ObjectNode>(responseBody, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			
	}

	// 商品詳情頁面 => 獲取商品資訊的圖片
	@ResponseBody
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getProductPhoto(@RequestParam Integer productId) {

		Product product = productService.findById(productId);

		byte[] photo = product.getPhoto();

		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	// 商品詳情頁面 => 獲取確認商品規格的Product & 獲取會員購物車該商品的數量
	@ResponseBody
	@PostMapping("/api/getConfirmProductByDetailIdSizeIdColorId")
	public ResponseEntity<?> getConfirmProduct(
			@RequestParam Integer memberId,
			@RequestParam Integer productDetailId, 
			@RequestParam Optional<Integer> productSizeId,
			@RequestParam Optional<Integer> productColorId) {

		Map<String, Object> responseData = new HashMap<>();
		
		// 獲取確認商品規格的Product
		Product product = productService.getConfirmProduct(
				productDetailId, productSizeId.orElse(null), productColorId.orElse(null));
		
		// 會員購物車內該商品的數量
		Integer productQuantityInCart = 0;
		Cart cart = null;
		
		if (product != null)
			cart = cartService.getCartByMemberIdAndProductId(memberId, product.getId());
		
		if (cart != null) {
			productQuantityInCart = cart.getQuantity();
			responseData.put("productQuantityInCart", productQuantityInCart);
		}

		if (product != null) {

			System.out.println(product.getId());
			System.out.println(product.getProductDetail().getName());
			
			responseData.put("product", product);
			
			return new ResponseEntity<Map<String, Object>>(responseData, HttpStatus.OK);

		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}
	
	// 商品詳情頁面 => 選擇一個規格後篩選Product vue
	@ResponseBody
	@PostMapping("/api/getProductByOption")
	public ResponseEntity<?> getProductByOption(
			@RequestParam Integer productDetailId, 
			@RequestParam Integer optionId,
			@RequestParam String optionName) {

		Map<String, Object> responseData = new HashMap<>();

		// 獲取當前商品詳情一樣的所有Product
		List<Product> productList = null;
		
		// 總庫存
		Integer totalStockQuantity = 0;

		// 比對規格點選尺寸or顏色
		if ("size".equals(optionName)) {
			productList = productService.findByProductDetailIdAndSizeId(productDetailId, optionId);
			System.out.println("size");
		} else if ("color".equals(optionName)) {
			productList = productService.findByProductDetailIdAndColorId(productDetailId, optionId);
			System.out.println("color");
		}

		if (productList != null) {

			for (Product product : productList) {
				System.out.println(product.getId());
				System.out.println(product.getProductDetail().getName());
			}
			
			// 計算總庫存
			for (Product product : productList) {
				totalStockQuantity += product.getStockQuantity();
			}
						
			// 獲取productList內最低和最高價商品
			Product minPriceProduct = productList.stream()
				    .min(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);
			
			Product maxPriceProduct = productList.stream()
				    .max(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);
			
			responseData.put("productList", productList);
			responseData.put("minPriceProduct", minPriceProduct);
			responseData.put("maxPriceProduct", maxPriceProduct);
			responseData.put("totalStockQuantity", totalStockQuantity);
			
			return new ResponseEntity<Map<String, Object>>(responseData, HttpStatus.OK);

		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	// 商品詳情頁面 => 取消所有規格選項後重新獲得同商品詳情的Product
	@ResponseBody
	@GetMapping("/api/getProductByProductDetailId")
	public ResponseEntity<?> getProductByProductDetailId(
			@RequestParam Integer productDetailId, 
			Model model) {

		Map<String, Object> responseData = new HashMap<>();
		
		// 獲取ProductDetail一樣的所有Poduct
		List<Product> productList = productService.getAvailableProductByProductDetailId(productDetailId, true);

		// 總庫存
		Integer totalStockQuantity = 0;

		if (productList != null) {
			// 計算總庫存
			for (Product product : productList) {
				totalStockQuantity += product.getStockQuantity();
			}

			// 獲取productList內最低和最高價商品
			Product minPriceProduct = productList.stream()
				    .min(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);
			
			Product maxPriceProduct = productList.stream()
				    .max(Comparator.comparing(p -> 
				        p.getDiscountPrice() != null 
				        ? p.getUnitPrice().min(p.getDiscountPrice()) 
				        : p.getUnitPrice(), 
				        Comparator.naturalOrder()
				    )).orElse(null);

			responseData.put("productList", productList);
			responseData.put("minPriceProduct", minPriceProduct);
			responseData.put("maxPriceProduct", maxPriceProduct);
			responseData.put("totalStockQuantity", totalStockQuantity);
			
			return new ResponseEntity<Map<String, Object>>(responseData, HttpStatus.OK);

		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	// 商品詳情頁面 => 加入購物車
	@ResponseBody
	@PostMapping("/api/addProductToCart")
	public ResponseEntity<?> addProductToCart(
			@RequestParam Integer memberId, 
			@RequestParam Integer productDetailId, 
			@RequestParam Optional<Integer> productSizeId,
			@RequestParam Optional<Integer> productColorId, 
			@RequestParam Integer quantity) {

		// 獲取會員
		Member member = null;
		Optional<Member> memberOpt = memberService.findById(memberId);
		if (memberOpt.isPresent())
			member = memberOpt.get();
		
		// 獲取選擇的商品
		Product product = productService.getConfirmProduct(
				productDetailId, productSizeId.orElse(null), productColorId.orElse(null));
		
		if (member != null && product != null) {
			// 商品加入購物車
			Cart cart = cartService.addProductToCart(member, product, quantity);
			
			return new ResponseEntity<Cart>(cart, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	
}
