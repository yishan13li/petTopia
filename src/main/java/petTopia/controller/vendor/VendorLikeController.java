package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.vendor.VendorLikeDto;
import petTopia.service.vendor.VendorLikeService;

/**
 * 商家按讚相關的控制器
 * 處理所有與商家按讚相關的API請求
 */
@CrossOrigin
@RestController
public class VendorLikeController {

	@Autowired
	private VendorLikeService vendorLikeService;
	
	/**
	 * 獲取特定商家的按讚列表
	 * @param vendorId 商家ID
	 * @return 商家的按讚列表
	 */
	@GetMapping("api/vendor/{vendorId}/like")
	public ResponseEntity<List<VendorLikeDto>> getVendorLikeTest(@PathVariable Integer vendorId) {
		List<VendorLikeDto> likeList = vendorLikeService.findMemberListByVendorId(vendorId);
		return ResponseEntity.ok(likeList);
	}
	
	/**
	 * 切換商家的按讚狀態
	 * @param vendorId 商家ID
	 * @param data 包含會員ID的請求體
	 * @return 按讚狀態的切換結果
	 */
	@PostMapping("/api/vendor/{vendorId}/like/toggle")
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Integer vendorId, @RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		boolean isLiked = vendorLikeService.addOrCancelVendorLike(memberId, vendorId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return ResponseEntity.ok(response);
	}
}
