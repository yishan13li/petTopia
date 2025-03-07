package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.VendorReviews;

@Repository
public interface VendorReviewsRepository extends JpaRepository<VendorReviews, Integer> {

	List<VendorReviews> findByVendorId(Integer vendorId);
}