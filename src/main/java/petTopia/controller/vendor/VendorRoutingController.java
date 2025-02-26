package petTopia.controller.vendor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorRoutingController {

//	@GetMapping("/vendor")
//	public String vendorHome() {
//		
//		return "/vendor/vendor_home.html";
//	}
	
//	@GetMapping("/vendor/detail")
//	public String vendorDetail() {
//		
//		return "/vendor/vendor_detail.html";
//	}
	
	@GetMapping("/activity")
	public String activityHome() {
		
		return "/vendor/activity_home.html";
	}
	
	@GetMapping("/activity/detail")
	public String activityDetail() {
		
		return "/vendor/activity_detail.html";
	}
}
