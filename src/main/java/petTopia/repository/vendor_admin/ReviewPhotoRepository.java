package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.ReviewPhoto;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Integer> {

	List<ReviewPhoto> findByVendorReviewId(Integer reviewId);

}