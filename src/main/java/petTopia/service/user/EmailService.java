package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PetTopia 會員註冊驗證碼");
        message.setText("您的驗證碼是: " + code + "\n\n此驗證碼將在5分鐘後過期。");
        
        mailSender.send(message);
    }
    
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Waggy商家註冊驗證碼");
        message.setText("您的驗證碼是: " + code + "\n\n此驗證碼將在5分鐘後過期。");
        
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("HTML郵件發送成功 - 收件人: {}, 主題: {}", to, subject);
            
        } catch (Exception e) {
            logger.error("發送HTML郵件失敗 - 收件人: {}, 主題: {}", to, subject, e);
            throw new RuntimeException("發送郵件失敗：" + e.getMessage());
        }
    }
} 