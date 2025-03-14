package petTopia.controller.shop;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.model.shop.Cart;
import petTopia.model.shop.Product;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.ProductService;
import petTopia.service.user.MemberService;

@Controller
@RequestMapping("/shop/cart")
public class ShopCartController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CartService cartService;

	// 會員購物車頁面
	@PostMapping
	public ResponseEntity<?> showMemberCart(@RequestParam Integer memberId) {

		List<Cart> cartList = cartService.getCartByMemberId(memberId);
		if (cartList != null) {
			return new ResponseEntity<List<Cart>>(cartList, HttpStatus.OK);
			
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

	}
	
	// 會員購物車頁面 => 更改數量直接更新購物車
	@PostMapping("/api/updateCartProductQuantity")
	public ResponseEntity<?> updateCartProductQuantity(
			@RequestParam Integer memberId, 
			@RequestParam Integer productId, 
			@RequestParam Integer quantity) {
		
		// 獲取會員
		Member member = null;
		Optional<Member> memberOpt = memberService.findById(memberId);
		if (memberOpt.isPresent())
			member = memberOpt.get();
		
		// 獲取選擇的商品
		Product product = productService.findById(productId);
		
		if (member != null && product != null) {
			// 更新購物車該商品數量
			Cart cart = cartService.updateCartProductQuantity(member, product, quantity);
			
			return new ResponseEntity<Cart>(cart, HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

	}
		
	// 會員購物車頁面 => 獲取商品圖片
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getPhoto(@RequestParam Integer productId) {
		
		// 獲取商品
		Product product = productService.findById(productId);
		
		byte[] photo = product.getPhoto();

		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

	}

	// 會員購物車頁面 => 獲取商品圖片
	@GetMapping("/api/deleteCartById")
	public ResponseEntity<?> deleteCartById(@RequestParam Integer cartId) {
		
		// 獲取該商品的購物車
		Integer deleteCartId = cartService.deleteCartById(cartId);
		
		if (deleteCartId != null) {
			return new ResponseEntity<Integer>(deleteCartId, HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

	}

	
	
	
}
