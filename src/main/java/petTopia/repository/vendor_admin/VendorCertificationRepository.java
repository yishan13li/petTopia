package petTopia.repository.vendor_admin;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorCertification;

public interface VendorCertificationRepository extends JpaRepository<VendorCertification, Integer> {

}
