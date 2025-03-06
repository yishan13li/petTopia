package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorCategory;

public interface VendorCategoryRepository extends JpaRepository<VendorCategory, Integer> {

}
