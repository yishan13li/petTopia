package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.service.vendor.ActivityRegistrationUserService;

@RestController
public class ActivityRegistrationController {

	@Autowired
	private ActivityRegistrationUserService activityRegistrationUserService;
	
	@GetMapping("/api/activity/{activityId}/registration/people/number")
	public ResponseEntity<ActivityPeopleNumber> getPeopleNumber(@PathVariable Integer activityId){
		ActivityPeopleNumber peopleNumber = activityRegistrationUserService.getPeopleNumber(activityId);
		return ResponseEntity.ok(peopleNumber);
	}

	@GetMapping("/api/activity/{activityId}/registration/pending")
	public ResponseEntity<List<ActivityRegistration>> getPendingMembers(@PathVariable Integer activityId) {
		List<ActivityRegistration> pendingList = activityRegistrationUserService.getActivityPendingList(activityId);
		return ResponseEntity.ok(pendingList);
	}

	@GetMapping("/api/activity/{activityId}/registration/confirmed")
	public ResponseEntity<List<ActivityRegistration>> getConfirmedMembers(@PathVariable Integer activityId) {
		List<ActivityRegistration> confirmedList = activityRegistrationUserService.getActivityConfirmedList(activityId);
		return ResponseEntity.ok(confirmedList);
	}

	@GetMapping("/api/activity/{activityId}/member/{memberId}/regist/status")
	public Map<String, Object> getRegistrationCondition(@PathVariable Integer activityId, 
			@PathVariable Integer memberId) {
		boolean isRegisted = activityRegistrationUserService.getRegistrationStatus(memberId, activityId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isRegisted ? true : false);
		return response;
	}
	
	@PostMapping("/api/activity/{activityId}/regist")
	public Map<String, Object> registActivity(@PathVariable Integer activityId,
			@RequestBody Map<String, Integer> data) {
		Integer memberId = data.get("memberId");
		boolean isRegisted = activityRegistrationUserService.toggleRegistration(memberId, activityId);

		Map<String, Object> response = new HashMap<>();
		response.put("action", isRegisted ? true : false);
		return response;
	}
	
	@GetMapping("/api/activity/{activityId}/registration/status")
	public ResponseEntity<Boolean> isActivityAvailable(@PathVariable Integer activityId) {
		boolean isAvailable = activityRegistrationUserService.isActivityAvailable(activityId);
		return ResponseEntity.ok(isAvailable);
	}
}
