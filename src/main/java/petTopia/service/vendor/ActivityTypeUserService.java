package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ActivityType;
import petTopia.repository.vendor_admin.ActivityTypeRepository;


@Service
public class ActivityTypeUserService {
	
	@Autowired
	private ActivityTypeRepository activityTypeRepository;
	
	public List<ActivityType> findAllActivityType(){
		List<ActivityType> typeList = activityTypeRepository.findAll();
		
		List<ActivityType> filterdList = new ArrayList<>();

		
		// 過濾類別內無店家之類別
		for (ActivityType type : typeList) {
			if (!type.getVendorActivities().isEmpty()) {
				filterdList.add(type);
			}
		}
		
		return filterdList;
	}
}
