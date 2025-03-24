package petTopia.controller.vendor_admin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.ActivityPeopleNumber;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;
import petTopia.repository.vendor_admin.ActivityPeopleNumberRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;

@Controller
public class VendorActivityRegistrationController {

	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private ActivityPeopleNumberRepository activityPeopleNumberRepository;

	@GetMapping("/api/vendor_admin/activity/registration")
	public ResponseEntity<?> getRegistrationByActivityId(@RequestParam Integer activityId) {
		List<ActivityRegistration> registration = activityRegistrationRepository.findByVendorActivityId(activityId);
		if (registration.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList()); // ✅ 返回空数组 []
		}
		return new ResponseEntity<>(registration, HttpStatus.OK);

	}

	// 批量更新状态为 "confirmed" 并发送通知
	@PutMapping("/api/vendor_admin/activity/confirmAll")
	public ResponseEntity<?> confirmAllRegistrations(@RequestParam Integer activityId) {
		List<ActivityRegistration> registrations = activityRegistrationRepository.findByVendorActivityId(activityId);

		if (registrations.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registrations found.");
		}

		// 批量更新状态为 "confirmed"
		registrations.forEach(registration -> {
			registration.setStatus("confirmed");
			activityRegistrationRepository.save(registration);

			// 发送通知（假设你有一个发送通知的服务）
//			sendNotification(registration.getMember(),
//					"Your registration for activity " + activityId + " has been confirmed.");
		});

		return ResponseEntity.ok("All registrations have been confirmed and notifications sent.");
	}

	@ResponseBody
	@PutMapping("/api/vendor_admin/registration/update/{id}")
	public ResponseEntity<?> updateRegistrationStatus(@PathVariable Integer id,
			@RequestBody String status) {
		Optional<ActivityRegistration> registrationopt = activityRegistrationRepository.findById(id);

		if (registrationopt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registrations found.");
			
		}
	    ActivityRegistration registration = registrationopt.get();

		registration.setStatus(status);
		activityRegistrationRepository.save(registration);
		return ResponseEntity.ok(registration);
	}

	// 单个报名 ID 更新状态为 "confirmed" 并发送通知
	@PutMapping("/api/vendor_admin/registration/confirmById/{registrationId}")
	public ResponseEntity<?> confirmRegistrationById(@PathVariable Integer registrationId) {
		ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

		if (registration == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");
		}
		
		Optional<ActivityPeopleNumber> current = activityPeopleNumberRepository.findByVendorActivity_Id(registration.getVendorActivity().getId());

		if (!current.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ActivityPeopleNumber not found.");
	    }
		// 更新状态为 "confirmed"
		registration.setStatus("confirmed");
		activityRegistrationRepository.save(registration);
		// 增加当前的已确认参与人数
	    ActivityPeopleNumber peopleNumber = current.get();
	    peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() + 1);
	    activityPeopleNumberRepository.save(peopleNumber);

		// 发送通知
//		sendNotification(registration.getMember(), "Your registration has been confirmed.");

		return ResponseEntity.ok("Registration confirmed and notification sent.");
	}

	// 单个报名 ID 更新状态为 "canceled" 并发送通知
	@PutMapping("/api/vendor_admin/registration/cancelById/{registrationId}")
	public ResponseEntity<?> cancelRegistrationById(@PathVariable Integer registrationId) {
		ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

		if (registration == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");
		}

		// 更新状态为 "canceled"
		registration.setStatus("canceled");
		activityRegistrationRepository.save(registration);

		// 发送通知
//		sendNotification(registration.getMember(), "Your registration has been canceled.");

		return ResponseEntity.ok("Registration canceled and notification sent.");
	}
	
	
	@DeleteMapping("/api/vendor_admin/registration/deleteById/{registrationId}")
	public ResponseEntity<?> deleteRegistrationById(@PathVariable Integer registrationId) {
	    // 查找注册信息
	    ActivityRegistration registration = activityRegistrationRepository.findById(registrationId).orElse(null);

	    if (registration == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");
	    }

	    
	    Optional<ActivityPeopleNumber> current = activityPeopleNumberRepository.findByVendorActivity_Id(registration.getVendorActivity().getId());

		if (!current.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ActivityPeopleNumber not found.");
	    }
		
		if("confirmed".equals(registration.getStatus())) {
		// 增加当前的已确认参与人数
	    ActivityPeopleNumber peopleNumber = current.get();
	    peopleNumber.setCurrentParticipants(peopleNumber.getCurrentParticipants() - 1);
	    activityPeopleNumberRepository.save(peopleNumber);
		}
	    
	    // 查找成员相关的通知
	    List<Notification> notifications = notificationRepository.findByMemberId(registration.getMember().getId());

	    // 删除所有通知
	    for (Notification deleteNotification : notifications) {
	        notificationRepository.delete(deleteNotification);
	    }

	    // 删除注册信息
	    activityRegistrationRepository.delete(registration);

	    // 返回成功响应
	    return ResponseEntity.ok("Registration canceled and notifications sent.");
	}


	@ResponseBody
	@PostMapping("/api/vendor_admin/registration/notification/{memberId}/{activityId}")
	public ResponseEntity<?> sendNotification(@PathVariable Integer memberId,@PathVariable Integer activityId,@RequestParam String title,@RequestParam String content) {
		Optional<ActivityRegistration> registrationOpt = activityRegistrationRepository.findByMemberIdAndVendorActivityId(memberId,activityId);

	    if (registrationOpt.isPresent()) {
	        ActivityRegistration registration = registrationOpt.get();

	        // 假設 Notification 是你的通知實體
	        Notification notification = new Notification();
	        notification.setMember(registration.getMember());
	        notification.setVendor(registration.getVendorActivity().getVendor());
	        notification.setVendorActivity(registration.getVendorActivity());
	        notification.setNotificationTitle(title);
	        notification.setNotificationContent(content);
	        

	        // 儲存通知
	        notificationRepository.save(notification);

	        return ResponseEntity.ok("Notification sent.");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity registration not found.");
	    }
	}
	
	// 发送通知的示例方法
//	private void sendNotification(String member, String message) {
//		// 这里你需要实现发送通知的逻辑，可以是邮件、消息队列或其他方式
//		// 假设使用某个 NotificationService 来发送通知
//		System.out.println("Sending notification to " + member + ": " + message);
//	}

}
