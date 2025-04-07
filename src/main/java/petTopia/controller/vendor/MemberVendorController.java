package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.vendor.VendorLikeDto;
import petTopia.dto.vendor.VendorReviewDto;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;

@CrossOrigin
@RestController
public class MemberVendorController {
	@Autowired
	private VendorLikeService vendorLikeService;
	
	@Autowired
	private VendorReviewService vendorReviewService;

	@GetMapping("/api/vendor/member/{memberId}/like")
	public ResponseEntity<List<VendorLikeDto>> getLikeList(@PathVariable Integer memberId) {
		List<VendorLikeDto> likeList = vendorLikeService.findListByMemberId(memberId);
		return ResponseEntity.ok(likeList);
	}

	@GetMapping("/api/vendor/member/{memberId}/review")
	public ResponseEntity<List<VendorReviewDto>> getReviewList(@PathVariable Integer memberId) {
		List<VendorReviewDto> likeList = vendorReviewService.findReviewListByMemberId(memberId);
		return ResponseEntity.ok(likeList);
	}

	@DeleteMapping("/api/vendor/like/{likeId}/delete")
	public ResponseEntity<?> deleteLike(@PathVariable Integer likeId){
		boolean result = vendorLikeService.deleteByLikeId(likeId);
		return ResponseEntity.ok(result);
	}
}
