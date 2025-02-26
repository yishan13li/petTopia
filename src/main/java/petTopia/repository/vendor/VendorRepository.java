package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {

}
