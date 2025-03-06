package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.user.VendorCategoryBean;

public interface VendorCategoryRepository extends JpaRepository<VendorCategoryBean, Integer> {
    VendorCategoryBean findByName(String name);
} 