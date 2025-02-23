package petTopia.repository.vendor_admin;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.vendor_admin.ActivityType;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Integer> {
}
