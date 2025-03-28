package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.VendorCertificationTag;
import petTopia.service.vendor.VendorTagService;

@CrossOrigin
@RestController
public class VendorTagController {
	@Autowired
	private VendorTagService vendorTagService;

	@GetMapping("/api/vendor/{vendorId}/tag")
	public ResponseEntity<List<VendorCertificationTag>> getVendorTagList(@PathVariable Integer vendorId) {
		List<VendorCertificationTag> tagList = vendorTagService.findConfirmedTagByVendorId(vendorId);
		return ResponseEntity.ok(tagList);
	}
}
