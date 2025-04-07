package petTopia.repository.vendor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.CalendarEvent;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {

    // 根據活動標題查詢事件
    List<CalendarEvent> findByEventTitleContaining(String title);

    // 根據 vendor_activity_id 查詢事件
    Optional<CalendarEvent> findByVendorActivityId(Integer vendorActivityId);

    // 查詢在特定時間範圍內的事件
    List<CalendarEvent> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

	List<CalendarEvent> findByVendorId(Integer vendorId);

	Optional<CalendarEvent> findByColor(String color);

	Optional<CalendarEvent> findByEventId(Integer id);

	void deleteByVendorActivityId(Integer id);
	
}
