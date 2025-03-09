package petTopia.controller.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import petTopia.model.vendor.VendorActivity;
import petTopia.service.vendor.VendorActivityService;

@Controller
public class ActivityFrontEndRouteController {
	
	
	@Autowired
	private VendorActivityService vendorActivityService;
	
	@GetMapping("/activity")
	public String activityHome() {
		
		return "/vendor/activity_home.html";
	}
	
	@GetMapping("/activity/detail/{activityId}")
	public String activityDetail(@PathVariable Integer activityId, Model model) {
		
		/* 該活動資料之賦值 */
		VendorActivity activity = vendorActivityService.findActivityById(activityId);
		model.addAttribute("activity", activity);
		
		return "/vendor/activity_detail.html";
	}
}
