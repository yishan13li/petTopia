package petTopia.model.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.user.Member;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "member_coupon")
public class MemberCoupon {

    @EmbeddedId
    private MemberCouponId id; // 使用複合主鍵

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @MapsId("memberId") // 這樣可以直接透過 memberId 取得會員
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne
    @MapsId("couponId") // 這樣可以直接透過 couponId 取得優惠券
    @JoinColumn(name = "coupons_id", referencedColumnName = "id")
    private Coupon coupon;
    
    @Column(name = "usage_count")
    private Integer usageCount; // 新增剩餘次數
    
    @Column(name="status", columnDefinition = "bit default 1")
    private Boolean status;
}