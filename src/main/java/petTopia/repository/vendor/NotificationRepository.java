package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import petTopia.model.vendor.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 可以在這裡新增你需要的查詢方法
    // 例如：根據會員ID查詢通知
    List<Notification> findByMemberIdAndIsRead(int memberId, boolean isRead);

    // 根據店家ID查詢通知
    List<Notification> findByVendorId(int vendorId);

    // 根據活動ID查詢通知
    List<Notification> findByVendorActivityId(int vendorActivityId);

    List<Notification> findByMemberId(Integer memberId);

    @Transactional
	List<Notification> deleteByMemberId(Integer memberId);
	
   
}

