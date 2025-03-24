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

import petTopia.dto.vendor.ActivityLikeDto;
import petTopia.service.vendor.ActivityLikeService;

@CrossOrigin
@RestController
public class ActivityLikeController {
	@Autowired
	private ActivityLikeService activityLikeService;

	@GetMapping("/api/activity/{activityId}/like")
	public ResponseEntity<List<ActivityLikeDto>> getActivitylikeList(@PathVariable Integer activityId) {
		List<ActivityLikeDto> likeList = activityLikeService.findMemberLikeListByActivityId(activityId);
		return ResponseEntity.ok(likeList);
	}

	@GetMapping("/api/activity/{activityId}/member/{memberId}/like/status")
	public Map<String, Object> getLikeStatus(@PathVariable Integer activityId, @PathVariable Integer memberId) {
		boolean isLiked = activityLikeService.getActivityLikeStatus(memberId, activityId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}
	
	@PostMapping("/api/activity/{activityId}/like/toggle")
	public Map<String, Object> toggleLike(@PathVariable Integer activityId, @RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		boolean isLiked = activityLikeService.toggleActivityLike(memberId, activityId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isLiked ? true : false);
		return response;
	}
}
