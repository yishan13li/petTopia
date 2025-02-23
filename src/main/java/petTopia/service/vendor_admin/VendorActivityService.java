package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor_admin.VendorActivity;
import petTopia.repository.vendor_admin.VendorActivityRepository;

@Service
public class VendorActivityService {

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	public VendorActivity saveVendorActivity(VendorActivity vendorActivity) {
		return vendorActivityRepository.save(vendorActivity);
	}

	public List<VendorActivity> getAllVendorActivities() {
		return vendorActivityRepository.findAll();
	}

	public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
		return vendorActivityRepository.findByVendorId(vendorId);
	}

	public void deleteVendorActivity(Integer id) {
		vendorActivityRepository.deleteById(id);
	}
}
