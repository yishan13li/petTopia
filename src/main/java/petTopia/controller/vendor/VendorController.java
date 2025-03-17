package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorService;

@CrossOrigin
@RestController
public class VendorController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorImagesService vendorImagesService;

	@GetMapping("api/vendor/all")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList = vendorService.findAllVendor();
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("api/vendor/{vendorId}")
	public ResponseEntity<Vendor> getVendorDetail(@PathVariable Integer vendorId) {
		Vendor vendor = vendorService.findVendorById(vendorId);
		return ResponseEntity.ok(vendor);
	}

	@GetMapping("api/vendor/{vendorId}/image")
	public ResponseEntity<List<VendorImages>> getVendorImage(@PathVariable Integer vendorId) {
		List<VendorImages> imageList = vendorImagesService.findImageListByVendorId(vendorId);
		return ResponseEntity.ok(imageList);
	}


}
