package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.service.user.RegistrationService;


@Controller
public class EmailVerificationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        boolean verified = registrationService.verifyEmail(token);
        
        if (verified) {
            return "verification-success";
        } else {
            return "verification-failed";
        }
    }
} 