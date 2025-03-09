package petTopia.controller.vendor_admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.service.vendor_admin.VendorCertificationService;

@Controller
public class VendorCertificationController {

	@Autowired
	private VendorCertificationService vendorCertificationService;

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

}
