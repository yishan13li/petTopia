package petTopia.model.shop;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="coupons")
public class Coupon {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "discount_type", nullable = false)
    private Boolean discountType; // 0 for fixed amount, 1 for discount percentage

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 0)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", nullable = false, precision = 10, scale = 0)
    private BigDecimal minOrderValue;

    @Column(name = "limit_count", nullable = false)
    private Integer limitCount;

    @Column(name = "valid_start", nullable = false)
    private java.util.Date validStart;

    @Column(name = "valid_end", nullable = false)
    private java.util.Date validEnd;

    @Column(name = "status", nullable = false, columnDefinition = "bit default 0")
    private Boolean status;
}
