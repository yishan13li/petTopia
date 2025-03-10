package petTopia.repository.vendor_admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.model.vendor.VendorReview;

public interface VendorActivityReviewRepository extends JpaRepository<VendorActivityReview, Integer> {

	List<VendorActivityReview> findByVendorActivityId(Integer vendorActivityId);

}
