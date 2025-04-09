package petTopia.repository.vendor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorCategory;

public interface VendorCategoryRepository extends JpaRepository<VendorCategory, Integer> {

	Optional<VendorCategory> findById(Integer id);

	Optional<VendorCategory> findByName(String category);
}
