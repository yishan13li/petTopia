package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@Controller
public class VendorFrontEndRouteController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;
	
	@Autowired
	private VendorImagesService vendorImagesService;

	@GetMapping("/vendor")
	public String vendorHome(Model model) {

		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		return "/vendor/vendor_home.html";
	}

	@GetMapping("/vendor/detail/{vendorId}")
	public String vendorDetail(@PathVariable Integer vendorId, Model model) {

		/* 該店家資料之賦值 */
		Vendor vendor = vendorService.findVendorById(vendorId);
		model.addAttribute("vendor", vendor);

		/* 所有店家資料之賦值 */
		List<Vendor> vendorList = vendorService.findAllVendor();
		model.addAttribute("vendorList", vendorList);

		/* 該店家評論之賦值 */
		List<VendorReviewDto> reviewList = vendorReviewService.getReviewListByVendorId(vendorId);
		model.addAttribute("reviewList", reviewList);
		
		/* 該店家所有圖片賦值 */
		List<VendorImages> imageList = vendorImagesService.findImagesByVendorId(vendorId);
		model.addAttribute("imageList",imageList);

		return "/vendor/vendor_detail.html";
	}

}
