package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.VendorActivityRepository;

@Service
public class VendorActivityService {
	
	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	public List<VendorActivity> findAllActivity() {
		List<VendorActivity> vendorList = vendorActivityRepository.findAll();
		return vendorList;
	}
	
	public VendorActivity findActivityById(Integer id) {
		Optional<VendorActivity> optional = vendorActivityRepository.findById(id);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}
	
}
