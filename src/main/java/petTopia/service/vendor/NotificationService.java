package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

// 取得會員的所有通知
	public List<Notification> getNotificationsByMember(Integer memberId) {
		return notificationRepository.findByMemberId(memberId);
	}

// 標記通知為已讀
	public void markNotificationAsRead(Integer id) {
		Optional<Notification> notification = notificationRepository.findById(id);
		notification.ifPresent(n -> {
			n.setIsRead(true);
			notificationRepository.save(n);
		});
	}

// 清除會員的所有通知
	@Transactional
	public void clearNotificationsByMember(Integer memberId) {
		notificationRepository.deleteByMemberId(memberId);
	}
}
