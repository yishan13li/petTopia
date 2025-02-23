package petTopia.service.vendor_admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor_admin.Vendor;
import petTopia.repository.vendor_admin.VendorRepository;

@Service
public class VendorServiceImpl implements VendorService {

	@Autowired
	private VendorRepository vendorRepository;

	@Override
	public Optional<Vendor> getVendorById(Integer vendorId) {
		return vendorRepository.findById(vendorId);
	}

	@Override
	public Vendor updateVendor(Vendor vendor) {
		return vendorRepository.save(vendor);
	}

	@Override
	public void deleteVendor(Vendor vendor) {
		vendorRepository.delete(vendor);
	}

	@Override
	public Optional<Vendor> getVendorByUserId(Integer userId) {
		return vendorRepository.findById(userId);
	}
}
