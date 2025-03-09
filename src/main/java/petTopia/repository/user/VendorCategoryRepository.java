package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.user.VendorCategory;

public interface VendorCategoryRepository extends JpaRepository<VendorCategory, Integer> {
    VendorCategory findByName(String name);
} 