package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.model.vendor.VendorLike;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor.ReviewPhotoService;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorLikeService;
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

	@Autowired
	private ReviewPhotoService reviewPhotoService;
	
	@Autowired
	private VendorLikeService vendorLikeService;

	@GetMapping("/vendor/all")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList = vendorService.findAllVendor();
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("/vendor/{vendorId}")
	public ResponseEntity<Vendor> getVendorDetail(@PathVariable Integer vendorId) {
		Vendor vendor = vendorService.findVendorById(vendorId);
		return ResponseEntity.ok(vendor);
	}

	@GetMapping("/vendor/{vendorId}/review")
	public ResponseEntity<List<VendorReviewDto>> getVendorReview(@PathVariable Integer vendorId) {
		List<VendorReviewDto> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
		return ResponseEntity.ok(reviewList);
	}

	@GetMapping("/vendor/{vendorId}/image")
	public ResponseEntity<List<VendorImages>> getVendorImage(@PathVariable("vendorId") Integer vendorId) {
		List<VendorImages> imageList = vendorImagesService.findImagesByVendorId(vendorId);
		return ResponseEntity.ok(imageList);
	}

	@GetMapping("/vendor/review/{reviewId}/photo")
	public ResponseEntity<List<ReviewPhoto>> getReviewPhoto(@PathVariable Integer reviewId) {
		List<ReviewPhoto> photoList = reviewPhotoService.findPhotoListByReviewId(reviewId);
		return ResponseEntity.ok(photoList);
	}
	
	@GetMapping("api/vendor/{vendorId}/like") // 這裡有誤要改
	public ResponseEntity<List<VendorLike>> getVendorLike(Integer vendorId){
		List<VendorLike> likeList = vendorLikeService.findMemberListByVendorId(vendorId);
		return ResponseEntity.ok(likeList);
	}

}
