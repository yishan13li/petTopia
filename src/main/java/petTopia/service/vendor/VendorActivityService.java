package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ActivityType;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityTypeRepository;

@Service
public class VendorActivityService {
	
	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	@Autowired
	private ActivityTypeRepository activityTypeRepository;
	
	public List<VendorActivity> findAllActivity() {
		List<VendorActivity> vendorList = vendorActivityRepository.findAll();
		return vendorList;
	}
	
	public VendorActivity findActivityById(Integer id) {
		Optional<VendorActivity> optional = vendorActivityRepository.findById(id);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}
	
	public List<VendorActivity> findAllActivityExceptOne(Integer activityId){
		List<VendorActivity> activityList = vendorActivityRepository.findAll();
		VendorActivity activityToRemove = vendorActivityRepository.findById(activityId).orElse(null);
		
		if (activityToRemove != null) {
			activityList.removeIf(v -> v.getId().equals(activityToRemove.getId()));
		}
		
		return activityList;
	}
	
	public List<VendorActivity> findActivityByTypeId(Integer typeId){
		ActivityType type = activityTypeRepository.findById(typeId).orElse(null);
		List<VendorActivity> activityList = vendorActivityRepository.findByActivityType(type);
		return activityList;
	}
	
}
