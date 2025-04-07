package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.service.vendor.ActivityLikeService;
import petTopia.service.vendor.ActivityRegistrationUserService;
import petTopia.service.vendor.VendorActivityReviewService;

@CrossOrigin
@RestController
public class MemberActivityController {
	@Autowired
	private ActivityLikeService activityLikeService;

	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;

	@Autowired
	private ActivityRegistrationUserService activityRegistrationUserService;

	@GetMapping("/api/activity/member/{memberId}/like")
	public ResponseEntity<List<ActivityLike>> getLikeList(@PathVariable Integer memberId) {
		List<ActivityLike> likeList = activityLikeService.findLikeListByMemberId(memberId);
		return ResponseEntity.ok(likeList);
	}

	@GetMapping("/api/activity/member/{memberId}/review")
	public ResponseEntity<List<VendorActivityReview>> getReviewList(@PathVariable Integer memberId) {
		List<VendorActivityReview> likeList = vendorActivityReviewService.findReviewListByMemberId(memberId);
		return ResponseEntity.ok(likeList);
	}

	@GetMapping("/api/activity/member/{memberId}/registration")
	public ResponseEntity<List<ActivityRegistration>> getRegistrationList(@PathVariable Integer memberId) {
		List<ActivityRegistration> registrationList = activityRegistrationUserService
				.findRegistrationListByMemberId(memberId);
		return ResponseEntity.ok(registrationList);
	}
	
	@DeleteMapping("/api/activity/like/{likeId}/delete")
	public ResponseEntity<?> deleteLike(@PathVariable Integer likeId){
		boolean result = activityLikeService.deleteByLikeId(likeId);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping("/api/activity/registration/{registrationId}/delete")
	public ResponseEntity<?> deleteRegistration(@PathVariable Integer registrationId){
		boolean result = activityRegistrationUserService.deleteByRegistrationId(registrationId);
		return ResponseEntity.ok(result);
	}
}
