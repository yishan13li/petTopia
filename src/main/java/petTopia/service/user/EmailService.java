package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("請驗證您的電子郵件");
        message.setText("請點擊以下連結驗證您的電子郵件：\n\n" +
                "http://localhost:8080/verify-email?token=" + token);
        
        mailSender.send(message);
    }
    
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Waggy商家註冊驗證碼");
        message.setText("您的驗證碼是: " + code + "\n\n此驗證碼將在5分鐘後過期。");
        
        mailSender.send(message);
    }
} 