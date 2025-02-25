package petTopia.service.vendor_admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import petTopia.model.vendor_admin.ActivityType;
import petTopia.repository.vendor_admin.ActivityTypeRepository;

@Service
public class ActivityTypeService {

	private final ActivityTypeRepository activityTypeRepository;

	public ActivityTypeService(ActivityTypeRepository activityTypeRepository) {
		this.activityTypeRepository = activityTypeRepository;
	}

	public List<ActivityType> getAllActivityTypes() {
		return activityTypeRepository.findAll();
	}
}
