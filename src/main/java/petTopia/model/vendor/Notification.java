package petTopia.model.vendor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.user.Member;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 通知ID

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;  // 接收通知的會員
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private Vendor vendor;  // 發送通知的店家

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_activity_id", referencedColumnName = "id")
    private VendorActivity vendorActivity;  // 對應的活動

    @Column(name = "notification_title", nullable = false, length = 255)
    private String notificationTitle;  // 通知標題

    @Column(name = "notification_content", nullable = false, length = 1000)
    private String notificationContent;  // 通知內容

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;  // 是否已讀 (0: 未讀, 1: 已讀)

    @Column(name = "sent_time", nullable = false)
    private LocalDateTime sentTime = LocalDateTime.now();  // 發送時間

}

