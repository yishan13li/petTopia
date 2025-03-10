package petTopia.controller.vendor_admin;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import petTopia.model.vendor.User;
import petTopia.model.vendor.UserRole;
import petTopia.model.vendor.Vendor;
import petTopia.service.vendor_admin.UserService;
import petTopia.service.vendor_admin.VendorService;

@Controller
public class LoginSystemController {
	@Autowired
	private UserService userServiceImpl;

	@Autowired
	private VendorService vendorService;

	@GetMapping("/loginsystemmain.controller")
	public String processMainAction() {
		return "vendor_admin/page-login";
	}

//	@PostMapping("/checklogin.controller")
//	public String processAction(@RequestParam("userEmail") String useremail, @RequestParam("userPwd") String pwd,
//			Model m) {
//		Map<String, String> errors = new HashMap<String, String>();
//		m.addAttribute("errors", errors);
//
//		if (useremail == null || useremail.length() == 0) {
//			errors.put("email", "email is required");
//		}
//
//		if (pwd == null || pwd.length() == 0) {
//			errors.put("pwd", "user password is required");
//		}
//
//		if (errors != null && !errors.isEmpty()) {
//			return "page-login";
//		}
//
//		boolean status = userServiceImpl.checkLogin(new User(pwd, useremail));
//
//		if (status) {
//			m.addAttribute("useremail", useremail);
//			m.addAttribute("pwd", pwd);
//			return "app-profile";
//		}
//
//		errors.put("msg", "please input username or password correctly");
//		return "page-login";
//	}

	@PostMapping("/checklogin.controller")
	public String processAction(@RequestParam("userEmail") String useremail, @RequestParam("userPwd") String pwd,
			Model m,
            HttpSession httpSession) {
		Map<String, String> errors = new HashMap<>();
		m.addAttribute("errors", errors);

		if (useremail == null || useremail.isEmpty()) {
			errors.put("email", "email is required");
		}
		if (pwd == null || pwd.isEmpty()) {
			errors.put("pwd", "user password is required");
		}
		if (!errors.isEmpty()) {
			return "vendor_admin/page-login";
		}

		// 確認帳號密碼是否正確
		Optional<User> userOpt = userServiceImpl.getUserByEmailAndPassword(useremail, pwd);
		if (userOpt.isPresent()) {
			User user = userOpt.get();

			// 如果是 Vendor，載入店家資訊
			if (user.getUserRole() == UserRole.vendor) {
				Optional<Vendor> vendorOpt = vendorService.getVendorById(user.getUserId());
				if (vendorOpt.isPresent()) {
					Vendor vendor = vendorOpt.get();
					httpSession.setAttribute("userId", user.getUserId());
	                httpSession.setAttribute("vendor", vendorOpt.get());  // 存入 Member 資訊

					// 把圖片轉換成 Base64
					String vendorLogoImgBase64 = (vendor.getLogoImg() != null)
							? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg())
							: null;

					// 將 user 和 vendor 資訊放入 model
					m.addAttribute("user", user);
					m.addAttribute("vendor", vendor);
					m.addAttribute("vendorLogoImgBase64", vendorLogoImgBase64);

				}
				return "vendor_admin/app-profile"; // 導向 app-profile.jsp
			}
		}

		// 登入失敗
		errors.put("msg", "please input username or password correctly");
		return "vendor_admin/page-login";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 清除所有 session 資料
		return "redirect:/loginsystemmain.controller"; // 重新導向到登入頁面
	}

}
