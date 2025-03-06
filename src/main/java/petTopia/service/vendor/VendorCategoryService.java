package petTopia.service.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.repository.vendor.VendorCategoryRepository;

@Service
public class VendorCategoryService {
	
	@Autowired
	private VendorCategoryRepository vendorCategoryRepository;
	

	
}
