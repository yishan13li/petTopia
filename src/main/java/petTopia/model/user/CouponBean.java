package petTopia.model.user;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "coupons")
@Data
public class CouponBean {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "discount_type", nullable = false)
    private Boolean discountType = false;  // 0為固定扣額，1為打折
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue = BigDecimal.ZERO;
    
    @Column(name = "limit_count", nullable = false)
    private Integer limitCount;
    
    @Column(name = "used_count")
    private Integer usedCount;
    
    @Column(name = "valid_start", nullable = false)
    private LocalDateTime validStart;
    
    @Column(name = "valid_end", nullable = false)
    private LocalDateTime validEnd;
    
    @Column(nullable = false)
    private Boolean status = false;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Boolean getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(Boolean discountType) {
        this.discountType = discountType;
    }
    
    public BigDecimal getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }
    
    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }
    
    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }
    
    public Integer getLimitCount() {
        return limitCount;
    }
    
    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }
    
    public Integer getUsedCount() {
        return usedCount;
    }
    
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
    
    public LocalDateTime getValidStart() {
        return validStart;
    }
    
    public void setValidStart(LocalDateTime validStart) {
        this.validStart = validStart;
    }
    
    public LocalDateTime getValidEnd() {
        return validEnd;
    }
    
    public void setValidEnd(LocalDateTime validEnd) {
        this.validEnd = validEnd;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
}