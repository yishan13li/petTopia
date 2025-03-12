package petTopia.controller.vendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

@CrossOrigin
@RestController
public class VendorVueController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;

	@Autowired
	private VendorImagesService vendorImagesService;

	@GetMapping("/vendor/all")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList = vendorService.findAllVendor();
		return ResponseEntity.ok(vendorList);
	}
	
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<Map<String, Object>> getVendorDetail(@PathVariable("vendorId") Integer vendorId) {

        Map<String, Object> response = new HashMap<>();

        Vendor vendor = vendorService.findVendorById(vendorId);
        response.put("vendor", vendor);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vendor/{vendorId}/review")
    public ResponseEntity<Map<String, Object>> getVendorReview(@PathVariable("vendorId") Integer vendorId) {
        Map<String, Object> response = new HashMap<>();

        List<VendorReviewDto> reviewList = vendorReviewService.getReviewListByVendorId(vendorId);
        response.put("reviewList", reviewList);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vendor/{vendorId}/image")
    public ResponseEntity<Map<String, Object>> getVendorImage(@PathVariable("vendorId") Integer vendorId) {
        Map<String, Object> response = new HashMap<>();

		List<VendorImages> imageList = vendorImagesService.findImagesByVendorId(vendorId);

        response.put("imageList", imageList);

        return ResponseEntity.ok(response);
    }

//	@GetMapping("/vendor/detail/{vendorId}")
//	public String vendorDetail(@PathVariable("vendorId") Integer vendorId, Model model) {
//
//		/* 該店家資料之賦值 */
//		Vendor vendor = vendorService.findVendorById(vendorId);
//		model.addAttribute("vendor", vendor);
//
//		/* 所有店家資料之賦值 */
//		List<Vendor> vendorList = vendorService.findAllVendor();
//		model.addAttribute("vendorList", vendorList);
//
//		/* 該店家評論之賦值 */
//		List<VendorReviewDto> reviewList = vendorReviewService.getReviewListByVendorId(vendorId);
//		model.addAttribute("reviewList", reviewList); // (reviewList != null) ? reviewList : new ArrayList<>())
//
//		/* 該店家所有圖片賦值 */
//		List<VendorImages> imageList = vendorImagesService.findImagesByVendorId(vendorId);
//		model.addAttribute("imageList", imageList); // (imageList != null) ? imageList : new ArrayList<>()
//
//		return "/vendor/vendor_detail.html";
//	}

}
