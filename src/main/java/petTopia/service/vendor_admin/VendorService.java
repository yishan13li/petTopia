package petTopia.service.vendor_admin;


import java.util.Optional;

import petTopia.model.vendor_admin.Vendor;

public interface VendorService {
    Optional<Vendor> getVendorById(Integer vendorId);
    Vendor updateVendor(Vendor vendor);
    public void deleteVendor(Vendor vendor);
    public Optional<Vendor> getVendorByUserId(Integer userId);
}
