package petTopia.service.vendor_admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Service
public class VendorCertificationTagService {

	@Autowired
	private VendorCertificationTagRepository vendorCertificationTagRepository;
	
	public boolean checkIfExists(int vendorId, int certificationTagId) {
	    return vendorCertificationTagRepository.existsByVendorIdAndTagId(vendorId, certificationTagId);
	}

}
