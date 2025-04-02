package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class VendorActivityServiceAdmin {

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;

	@Autowired
	private CalendarEventRepository calendarEventRepository;

	@Autowired
	private ActivityRegistrationRepository activityRegistrationRepository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	public VendorActivity saveVendorActivity(VendorActivity vendorActivity) {
		return vendorActivityRepository.save(vendorActivity);
	}

	public List<VendorActivity> getAllVendorActivities() {
		return vendorActivityRepository.findAll();
	}

	public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
		return vendorActivityRepository.findByVendorId(vendorId);
	}

	@Transactional
	public void deleteVendorActivity(Integer id) {
		calendarEventRepository.deleteByVendorActivityId(id);
		vendorActivityRepository.deleteById(id);

	}

	public void addActivity(VendorActivity activity) {
//        activity.setRegistrationDate(LocalDateTime.now()); // 設定當前時間為註冊時間
		vendorActivityRepository.save(activity);
	}

//	public Optional<Integer> getFirstImageIdByVendorActivityId(Integer vendorActivityId) {
//		return vendorActivityImagesRepository.findFirstByVendorActivityId(vendorActivityId)
//				.map(VendorActivityImages::getId);
//	}

	public Optional<VendorActivity> getVendorActivityById(Integer id) {
		return vendorActivityRepository.findById(id);
	}

	public List<TopActivityDTO> getTop5Activities() {
		Pageable pageable = PageRequest.of(0, 5);
	        return activityRegistrationRepository.findTop5Activities(pageable);
	}
	
	public boolean checkTimeConflictDetail(Integer vendorId,Integer activityId,String startTime, String endTime) throws DataAccessException {
	    String query = "SELECT COUNT(*) FROM vendor_activity WHERE vendor_id = ? AND id != ? AND (" +
                "(start_time BETWEEN ? AND ?) " +
                "OR (end_time BETWEEN ? AND ?) " +
                "OR (? BETWEEN start_time AND end_time) " +
                "OR (? BETWEEN start_time AND end_time))";
	    int count = 0;
		try {
			count = jdbcTemplate.queryForObject(query, Integer.class, vendorId,activityId,startTime, endTime, startTime, endTime, startTime, endTime);
			System.out.println("SQL Query: " + query);
			System.out.println("Parameters: vendorId=" + vendorId + ", activityId=" + activityId + ", startTime=" + startTime + ", endTime=" + endTime);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return count > 0;
	}
	
	public boolean checkTimeConflict(Integer vendorId,String startTime, String endTime) throws DataAccessException {
	    String query = "SELECT COUNT(*) FROM vendor_activity WHERE vendor_id = ? AND (start_time BETWEEN ? AND ?) OR (end_time BETWEEN ? AND ?) OR (? BETWEEN start_time AND end_time) OR (? BETWEEN start_time AND end_time)";
	    int count = 0;
		try {
			count = jdbcTemplate.queryForObject(query, Integer.class, vendorId,startTime, endTime, startTime, endTime, startTime, endTime);
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return count > 0;
	}
	

}
