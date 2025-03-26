package petTopia.config; // 指定該類別的封裝（package），屬於 `petTopia.config` 這個組件。

// 引入必要的 Spring 及 JavaMail 相關類別
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;

// @Configuration 表示這是一個 Spring 配置類別，Spring 會將它作為設定檔載入
@Configuration
public class MailConfig {

    // @Value 註解用於從 application.properties 或 application.yml 中讀取對應的屬性值
    @Value("${spring.mail.host}") // 讀取郵件伺服器 (SMTP) 的主機地址
    private String mailHost;

    @Value("${spring.mail.port}") // 讀取郵件伺服器的端口號
    private int mailPort;

    @Value("${spring.mail.username}") // 讀取 SMTP 登入的使用者名稱
    private String mailUsername;

    @Value("${spring.mail.password}") // 讀取 SMTP 登入的密碼
    private String mailPassword;

    // @Bean 表示這個方法會返回一個 Spring 託管的 Bean，供應用程式使用
    @Bean
    public JavaMailSender getJavaMailSender() {
        // 建立 JavaMailSenderImpl 物件，這是 Spring 提供的郵件發送實作類
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 設定郵件伺服器的主機地址和端口號
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        // 設定 SMTP 認證所需的使用者名稱和密碼
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        // 建立 Properties 物件來儲存 SMTP 相關設定
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // 設定傳輸協議為 SMTP
        props.put("mail.smtp.auth", "true"); // 啟用 SMTP 身份驗證
        props.put("mail.smtp.starttls.enable", "true"); // 啟用 TLS 加密
        props.put("mail.debug", "true"); // 啟用除錯模式，方便日誌輸出
        
        return mailSender; // 返回配置好的 JavaMailSender 實例
    }
}
