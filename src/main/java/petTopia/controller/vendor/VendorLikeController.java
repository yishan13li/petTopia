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

@CrossOrigin
@RestController
public class VendorLikeController {

	@Autowired
	private VendorLikeService vendorLikeService;
	
	@GetMapping("api/vendor/{vendorId}/like")
	public ResponseEntity<List<VendorLikeDto>> getVendorLikeTest(@PathVariable Integer vendorId) {
		List<VendorLikeDto> likeList = vendorLikeService.findMemberListByVendorId(vendorId);
		return ResponseEntity.ok(likeList);
	}
	
//	@GetMapping("api/vendor/{vendorId}/like")
//	public ResponseEntity<List<VendorLike>> getVendorLike(@PathVariable Integer vendorId) {
//		List<VendorLike> likeList = vendorLikeService.findMemberListByVendorId(vendorId);
//		return ResponseEntity.ok(likeList);
//	}
	
	@PostMapping("/api/vendor/{vendorId}/like/toggle")
	public Map<String, Object> toggleLike(@PathVariable Integer vendorId, @RequestBody Map<String, Integer> data) {

		Integer memberId = data.get("memberId");
		boolean isLiked = vendorLikeService.addOrCancelVendorLike(memberId, vendorId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}
	



}
