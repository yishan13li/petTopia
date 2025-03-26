package petTopia.controller.vendor_admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;
import petTopia.service.vendor_admin.VendorReviewsServiceAdmin;

@Controller
public class VendorReviewsController {

	@Autowired
	private VendorReviewsServiceAdmin vendorReviewsService;

	@Autowired
	private VendorReviewRepository vendorReviewRepository;

	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;

	@GetMapping("/vendor_admin/reviews")
	public String getReviewsPage() {
		return "vendor_admin/vendor_admin_reviews";
	}

	@ResponseBody
	@GetMapping("/api/vendor_admin/reviews/{vendorId}")
	public ResponseEntity<?> getAllReviews() {
		List<VendorReview> reviews = vendorReviewRepository.findAll();
		if (reviews.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList()); // ✅ 返回空数组 []
		}
		return ResponseEntity.ok(reviews);
	}

	// 取得店家的所有評論
	@ResponseBody
	@GetMapping("/api/vendor_admin/review")
	public ResponseEntity<?> getReviewsByVendorId(@RequestParam Integer vendorId) {
		List<VendorReview> vendorReviews = vendorReviewsService.getReviewsByVendorId(vendorId);
		if (vendorReviews.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList()); // ✅ 返回空数组 []
		}
		return ResponseEntity.ok(vendorReviews);
	}

	// 取得評論的所有照片
	@GetMapping("/api/vendor_admin/review/photos/{reviewId}")
	public List<ReviewPhoto> getPhotosByReviewId(@PathVariable Integer reviewId) {
		return vendorReviewsService.getPhotosByReviewId(reviewId);
	}

	@GetMapping("/review_photos/ids")
	public ResponseEntity<?> findPhotoIdByVendorReviewId(@RequestParam Integer vendorReviewId) {
		Optional<VendorReview> op = vendorReviewRepository.findById(vendorReviewId);

		List<Integer> photoIdList = new ArrayList<>();

		if (op.isPresent()) {
			VendorReview vendorReviews = op.get();
			List<ReviewPhoto> photos = vendorReviews.getReviewPhotos();

			for (ReviewPhoto photo : photos) {
				photoIdList.add(photo.getId()); // 假設每個 VendorActivityPhoto 實體有一個 id 字段
			}

			return new ResponseEntity<>(photoIdList, HttpStatus.OK); // 返回所有照片的 ID 列表
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果沒有找到活動，返回 404
	}

	@GetMapping("/review_photos/download")
	public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
		Optional<ReviewPhoto> photoOpt = reviewPhotoRepository.findById(photoId);

		if (photoOpt.isPresent()) {
			ReviewPhoto image = photoOpt.get();
			byte[] photoFile = image.getPhoto(); // 假設每個 VendorActivityPhoto 實體有一個 photoFile 字段，存儲圖片二進制數據

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG); // 假設圖片是 JPEG 格式

			return new ResponseEntity<>(photoFile, headers, HttpStatus.OK); // 返回圖片的二進制數據
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果找不到圖片，返回 404
	}

	// 新增評論
//	@Transactional
	@ResponseBody
	@PostMapping("/api/vendor_admin/review/add")
	public ResponseEntity<?> addReview(@RequestBody VendorReview review,
			@RequestPart(value = "photo", required = false) MultipartFile photo) {
		try {
			// 假设saveReview是保存评论的方法
			VendorReview vendorReviews = new VendorReview();
//			vendorReviews.setId(review.getId());
			vendorReviews.setVendorId(review.getVendorId());
			vendorReviews.setMemberId(review.getMemberId());
			vendorReviews.setReviewContent(review.getReviewContent());
			vendorReviews.setReviewTime(review.getReviewTime());
			vendorReviews.setRatingEnvironment(review.getRatingEnvironment());
			vendorReviews.setRatingPrice(review.getRatingPrice());
			vendorReviews.setRatingService(review.getRatingService());

			VendorReview savedReview = vendorReviewRepository.save(vendorReviews);

			// 如果有上传图片，保存图片
			if (photo != null && !photo.isEmpty()) {
				ReviewPhoto reviewPhoto = new ReviewPhoto();
				reviewPhoto.setVendorReview(savedReview);
				reviewPhoto.setPhoto(photo.getBytes()); // 转换为 byte[]
				reviewPhotoRepository.save(reviewPhoto); // 保存图片
			}

			return new ResponseEntity<>(savedReview, HttpStatus.CREATED); // 返回保存的评论数据
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to add review", HttpStatus.BAD_REQUEST); // 提供错误信息
		}
	}
//
//	// 新增評論照片
//	@PostMapping("/api/vendor_admin/review/add/photo")
//	public ReviewPhoto addReviewPhoto(@RequestBody ReviewPhoto photo) {
//		return vendorReviewsService.addReviewPhoto(photo);
//	}

	// 刪除評論
	@ResponseBody
	@DeleteMapping("/api/vendor_admin/review/delete/{reviewId}")
	public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
		Optional<VendorReview> review = vendorReviewRepository.findById(reviewId);
		Map<String, String> response = new HashMap<>();
		if (review.isPresent()) {
			boolean deleted = vendorReviewsService.deleteReview(reviewId);

			response.put("message", "刪除成功");
		} else {
			response.put("message", "刪除失敗無此資料");
		}

		return ResponseEntity.ok(response);
	}

}
