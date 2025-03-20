package petTopia.controller.vendor_admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.CertificationTag;
import petTopia.service.vendor_admin.VendorCertificationService;
import petTopia.service.vendor_admin.VendorCertificationTagService;

@Controller
public class VendorCertificationController {

	@Autowired
	private VendorCertificationService vendorCertificationService;
	
	@Autowired
	private VendorCertificationTagService vendorCertificationTagService;

	@PostMapping("/api/vendor_admin/certification")
	@ResponseBody
	public ResponseEntity<String> AddCertification(@RequestParam Integer vendorId, @RequestParam Integer tagId) {
		try {
			// 调用服务创建认证信息
			vendorCertificationService.createVendorCertificationWithTag(vendorId, tagId);
			return ResponseEntity.ok("申请已提交成功！");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("申请提交失败，请稍后重试！");
		}
	}
	
	@GetMapping("/api/vendor_certification/exists/{vendorId}/{certificationTagId}")
	public ResponseEntity<Map<String, Object>> checkIfExists(@PathVariable int vendorId, @PathVariable int certificationTagId) {
	    Map<String, Object> response = new HashMap<>();
	    
	    boolean exists = vendorCertificationTagService.checkIfExists(vendorId, certificationTagId); // 检查数据库中是否有相同的申请
	    response.put("exists", exists);
	    
	    return ResponseEntity.ok(response);
	}


	
	// 新增 API 來獲取所有認證語
	@ResponseBody
    @GetMapping("/api/certification_type/all")
    public ResponseEntity<List<CertificationTag>> getAllCertificationTypes() {
        try {
            List<CertificationTag> certificationTypes = vendorCertificationService.getAllCertificationTypes();
            return ResponseEntity.ok(certificationTypes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
