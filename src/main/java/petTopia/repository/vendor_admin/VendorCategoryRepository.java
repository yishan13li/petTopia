package petTopia.repository.vendor_admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.VendorCategory;

@Repository
public interface VendorCategoryRepository extends JpaRepository<VendorCategory, Integer> {
	Optional<VendorCategory> findById(Integer id);

	Optional<VendorCategory> findByName(String category);
}
