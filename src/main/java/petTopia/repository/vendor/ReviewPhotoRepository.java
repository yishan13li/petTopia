package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.ReviewPhoto;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Integer> {
	List<ReviewPhoto> findByVendorReviewId(Integer reviewId);
}
