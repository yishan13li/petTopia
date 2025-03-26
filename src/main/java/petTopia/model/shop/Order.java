package petTopia.model.shop;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.ShippingInfoDto;
import petTopia.model.user.Member;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class Order {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 0)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 0)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 0)
    private BigDecimal shippingFee;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 0)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "created_time", nullable = false)
    private java.util.Date createdTime;

    @Column(name = "updated_date")
    private java.util.Date updatedDate;
    
    @Column(name="note")
    private String note;

    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Shipping shipping;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Payment payment;
    
    @OneToMany(mappedBy =  "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

	public void setOrderStatus(Order order, int i) {
		
	}
    
}
