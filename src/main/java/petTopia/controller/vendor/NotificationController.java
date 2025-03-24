package petTopia.controller.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // 🔹 根據會員ID獲取通知列表
    @ResponseBody
    @GetMapping("/api/vendor/notification/{memberId}")
    public ResponseEntity<List<Notification>> getNotificationsByMemberId(@PathVariable Integer memberId) {
        List<Notification> notifications = notificationRepository.findByMemberId(memberId);
        return ResponseEntity.ok(notifications);
    }

    // 🔹 根據店家ID獲取通知
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Notification>> getNotificationsByVendorId(@PathVariable Integer vendorId) {
        List<Notification> notifications = notificationRepository.findByVendorId(vendorId);
        return ResponseEntity.ok(notifications);
    }

//    // 🔹 根據活動ID獲取通知
//    @GetMapping("/activity/{activityId}")
//    public ResponseEntity<List<Notification>> getNotificationsByActivityId(@PathVariable Integer activityId) {
//        List<Notification> notifications = notificationRepository.findByVendorActivityId(activityId);
//        return ResponseEntity.ok(notifications);
//    }
//
//    // 🔹 根據會員ID獲取所有未讀通知
//    @GetMapping("/member/{memberId}/unread")
//    public ResponseEntity<List<Notification>> getUnreadNotificationsByMemberId(@PathVariable Integer memberId) {
//        List<Notification> unreadNotifications = notificationRepository.findByMemberIdAndIsRead(memberId, false);
//        return ResponseEntity.ok(unreadNotifications);
//    }

    // 🔹 標記通知為已讀
    @PutMapping("/api/vendor/notification/read/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Integer notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsRead(true); // 設置為已讀
            notificationRepository.save(notification);
            return ResponseEntity.ok("通知已標記為已讀");
        }
        return ResponseEntity.notFound().build();
    }

    // 🔹 根據會員ID刪除所有通知
    @DeleteMapping("/api/vendor/notification/delete/{memberId}")
    public ResponseEntity<?> deleteNotificationsByMemberId(@PathVariable Integer memberId) {
        notificationRepository.deleteByMemberId(memberId);
        return ResponseEntity.ok("已刪除該會員的所有通知");
    }
}
