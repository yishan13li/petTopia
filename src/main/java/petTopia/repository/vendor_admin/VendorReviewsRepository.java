package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.VendorReview;

@Repository
public interface VendorReviewsRepository extends JpaRepository<VendorReview, Integer> {

	List<VendorReview> findByVendorId(Integer vendorId);
}