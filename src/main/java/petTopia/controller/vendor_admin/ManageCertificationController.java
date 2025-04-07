package petTopia.controller.vendor_admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.dto.vendor_admin.CertificationDTO;
import petTopia.model.vendor.VendorCertification;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;
import petTopia.service.vendor_admin.VendorCertificationService;

@Controller
public class ManageCertificationController {

	@Autowired
	private VendorCertificationTagRepository vendorCertificationTagRepository;

	@Autowired
	private VendorCertificationService vendorCertificationService;

	
//	@GetMapping("/api/admin/certification")
//	public ResponseEntity<?> getAllCertification() {
//		
//	}
	@ResponseBody
	@GetMapping("/api/admin/certification")
	public ResponseEntity<?> getAllCertificationsWithTags() {
		try {
			List<CertificationDTO> certificationsWithTags = vendorCertificationService.getAllCertificationsWithTags();
			if (certificationsWithTags.isEmpty()) {
				return ResponseEntity.noContent().build(); // 没有数据时返回 204 No Content
			}
			return ResponseEntity.ok(certificationsWithTags); // 返回 200 OK 和认证申请数据
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取认证申请和标签失败"); // 捕获异常并返回 500 错误
		}
	}
	
	// 更新认证状态
    @PutMapping("/api/admin/certification/status/update/{certificationId}")
    public ResponseEntity<VendorCertification> updateCertificationStatus(
            @PathVariable Integer certificationId,
            @RequestParam String status,@RequestParam String reason) {
    	VendorCertification updatedCertification = vendorCertificationService.updateCertificationStatus(certificationId, status,reason);
        return ResponseEntity.ok(updatedCertification);
    }
}
