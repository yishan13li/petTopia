package petTopia.controller.vendor_admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;
import petTopia.service.vendor_admin.VendorCertificationService;

@Controller
public class ManageCertificationController {
	
	@Autowired
	private VendorCertificationTagRepository vendorCertificationTagRepository;
	
	@Autowired
	private VendorCertificationService vendorCertificationService;
	
	
}
