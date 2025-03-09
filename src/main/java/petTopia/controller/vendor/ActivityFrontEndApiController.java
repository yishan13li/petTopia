package petTopia.controller.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import petTopia.service.vendor.ActivityLikeService;
import petTopia.service.vendor.VendorActivityReviewService;

@Controller
public class ActivityFrontEndApiController {

	@Autowired
	private ActivityLikeService activityLikeService;

	@Autowired
	private VendorActivityReviewService vendorActivityReviewService;

	@PostMapping("/activity/give_activity_like")
	public String giveVendorLike(Integer memberId, Integer activityId) {
		activityLikeService.addOrCancelActivityLike(memberId, activityId);

		return "/vendor/activity_detail.html";
	}

	@PostMapping("/activity/give_review")
	public String giveReview(Integer memberId, Integer activityId, String context) {
		vendorActivityReviewService.addOrModifyActivityReview(memberId, activityId, context);

		return "/vendor/activity_detail.html";
	}

	@DeleteMapping("/activity/delete_review")
	public String deleteReview(Integer memberId, Integer activityId) {
		vendorActivityReviewService.deleteReviewByMemberIdAndVendorId(memberId, activityId);
		return "/vendor/activity_detail.html";
	}
}
