package petTopia.controller.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Member;
import petTopia.model.user.Users;
import petTopia.repository.user.UserRepository;
import petTopia.service.user.MemberService;

@Controller
public class LoginController {

    @Autowired
    private UserRepository uRepo;  // 注入 UserRepository

    @Autowired
    private MemberService mService;
    
 // 顯示登入頁面
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // 返回的 login.html 頁面
    }

    // 處理登入請求
    @PostMapping("/login2")
    public String handleLogin(@RequestParam String email, 
                              @RequestParam String password,
                              Model model,
                              HttpSession httpSession) {

        // 根據 email 查找用戶
        Optional<Users> userOpt = uRepo.findByEmail(email);

        // 檢查用戶是否存在，並且密碼是否匹配
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            Users user = userOpt.get();

            // 查找對應的 Member 資訊
            Optional<Member> memberOpt = mService.findById(user.getId());  //根據 userId 查詢 Member 資料

            if (memberOpt.isPresent()) {
                // 存入 Session，讓其他功能可讀取 userId 和 Member 資訊
                httpSession.setAttribute("userId", user.getId());
                httpSession.setAttribute("member", memberOpt.get());  // 存入 Member 資訊

                return "shop/shop_index";  // 登入成功後跳轉到主頁
            } else {
                model.addAttribute("error", "這個user找不到member資訊");
            }
        } else {
            model.addAttribute("error", "無效的信箱或密碼");
        }
        
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        return "login";  // 登入失敗，重新顯示登入頁
    }
    
    @PostMapping("/login")
    public String handleLogin(@RequestBody Map<String, String> loginData, Model model, HttpSession httpSession) {

        // 從請求體中提取 email 和 password
        String email = loginData.get("email");
        String password = loginData.get("password");

        // 根據 email 查找用戶
        Optional<Users> userOpt = uRepo.findByEmail(email);

        // 檢查用戶是否存在，並且密碼是否匹配
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            Users user = userOpt.get();

            // 查找對應的 Member 資訊
            Optional<Member> memberOpt = mService.findById(user.getId());

            if (memberOpt.isPresent()) {
                // 存入 Session，讓其他功能可讀取 userId 和 Member 資訊
                httpSession.setAttribute("userId", user.getId());
                httpSession.setAttribute("member", memberOpt.get());  // 存入 Member 資訊

                return "shop/shop_index";  // 登入成功後跳轉到主頁
            } else {
                model.addAttribute("error", "這個user找不到member資訊");
            }
        } else {
            model.addAttribute("error", "無效的信箱或密碼");
        }

        model.addAttribute("email", email);
        model.addAttribute("password", password);
        return "login";  // 登入失敗，重新顯示登入頁
    }

}
