package petTopia.repository.vendor_admin;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.vendor.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
}