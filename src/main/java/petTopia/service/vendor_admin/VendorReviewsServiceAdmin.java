package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Service
public class VendorReviewsServiceAdmin {

	@Autowired
	private VendorReviewRepository vendorReviewRepository;

	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;

	// 根據店家 ID 取得評論
	public List<VendorReview> getReviewsByVendorId(Integer vendorId) {
		return vendorReviewRepository.findByVendorId(vendorId);
	}

	// 根據評論 ID 取得對應的照片
	public List<ReviewPhoto> getPhotosByReviewId(Integer reviewId) {
		return reviewPhotoRepository.findByVendorReviewId(reviewId);
	}

	// 新增評論
	public VendorReview addReview(VendorReview review) {
		return vendorReviewRepository.save(review);
	}

	// 新增評論照片
	public ReviewPhoto addReviewPhoto(ReviewPhoto photo) {
		return reviewPhotoRepository.save(photo);
	}

	// 刪除評論
	public boolean deleteReview(Integer reviewId) {
		Optional<VendorReview> review = vendorReviewRepository.findById(reviewId);
		if (review.isPresent()) {
			vendorReviewRepository.deleteById(reviewId);
			return true;
		}
		return false;
	}
}
