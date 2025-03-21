package petTopia.controller.vendor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor.ReviewPhotoService;
import petTopia.service.vendor.VendorReviewService;

/**
 * 商家評論相關的控制器
 * 處理所有與商家評論相關的API請求
 */
@CrossOrigin
@RestController
public class VendorReviewController {

	@Autowired
	private VendorReviewService vendorReviewService;
	
	@Autowired
	private ReviewPhotoService reviewPhotoService;

	/**
	 * 獲取特定商家的評論列表
	 * @param vendorId 商家ID
	 * @return 商家的評論列表
	 */
	@GetMapping("/api/vendor/{vendorId}/review")
	public ResponseEntity<List<VendorReviewDto>> getVendorReview(@PathVariable Integer vendorId) {
		List<VendorReviewDto> reviewList = vendorReviewService.findReviewListByVendorId(vendorId);
		return ResponseEntity.ok(reviewList);
	}
	
	/**
	 * 獲取特定評論的詳細資訊
	 * @param reviewId 評論ID
	 * @return 評論的詳細資訊
	 */
	@GetMapping("/api/vendor/review/{reviewId}")
	public ResponseEntity<Map<String, Object>> getVendorReviewById(@PathVariable Integer reviewId) {
		VendorReview review = vendorReviewService.findReviewById(reviewId);		
		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 獲取特定評論的圖片列表
	 * @param reviewId 評論ID
	 * @return 評論的圖片列表
	 */
	@GetMapping("/api/vendor/review/{reviewId}/photo")
	public ResponseEntity<List<ReviewPhoto>> getReviewPhoto(@PathVariable Integer reviewId) {
		List<ReviewPhoto> photoList = reviewPhotoService.findPhotoListByReviewId(reviewId);
		return ResponseEntity.ok(photoList);
	}
	
	/**
	 * 新增商家評論
	 * @param vendorId 商家ID
	 * @param memberId 會員ID
	 * @param content 評論內容
	 * @param reviewPhotos 評論圖片
	 * @return 新增的評論資訊
	 */
	@PostMapping(value = "/api/vendor/{vendorId}/review/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> giveReview(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam String content, @RequestPart(required = false) List<MultipartFile> reviewPhotos)
			throws IOException {
		VendorReview review = new VendorReview();
		if (reviewPhotos != null) {
			review = vendorReviewService.addReview(memberId, vendorId, content, reviewPhotos);
		} else {
			List<MultipartFile> nullList = new ArrayList<MultipartFile>();
			review = vendorReviewService.addReview(memberId, vendorId, content, nullList);
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("review", review);
		return ResponseEntity.ok(response);
	}

	/**
	 * 新增商家評分
	 * @param vendorId 商家ID
	 * @param memberId 會員ID
	 * @param ratingEnv 環境評分
	 * @param ratingPrice 價格評分
	 * @param ratingService 服務評分
	 * @return 新增的評分資訊
	 */
	@PostMapping(value = "/api/vendor/{vendorId}/review/star/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> giveReviewStar(@PathVariable Integer vendorId, @RequestParam Integer memberId,
			@RequestParam Integer ratingEnv, @RequestParam Integer ratingPrice, @RequestParam Integer ratingService)
			throws IOException {
		VendorReview starReview = vendorReviewService.addStarReview(memberId, vendorId, ratingEnv, ratingPrice,
				ratingService);
		Map<String, Object> response = new HashMap<>();
		response.put("review", starReview);
		return ResponseEntity.ok(response);
	}

	/**
	 * 修改評論內容
	 * @param reviewId 評論ID
	 * @param content 新的評論內容
	 * @return 修改後的評論資訊
	 */
	@PostMapping(value = "/api/vendor/review/{reviewId}/rewrite", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> rewriteTextReview(@PathVariable Integer reviewId, @RequestParam String content) {
		VendorReview review = vendorReviewService.rewriteReviewById(reviewId, content);
		Map<String, Object> response = new HashMap<>();
		response.put("review", review);
		return ResponseEntity.ok(response);
	}

	/**
	 * 刪除評論
	 * @param reviewId 評論ID
	 * @return 刪除結果
	 */
	@DeleteMapping("/api/vendor/review/{reviewId}/delete")
	public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Integer reviewId) {
		vendorReviewService.deleteReviewById(reviewId);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return ResponseEntity.ok(response);
	}

}
