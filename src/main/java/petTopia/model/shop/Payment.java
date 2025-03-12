package petTopia.model.shop;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_amount", nullable = false, precision = 10, scale = 0)
    private BigDecimal paymentAmount;

    @ManyToOne
    @JoinColumn(name = "payment_category_id", nullable = false)
    private PaymentCategory paymentCategory;

    @ManyToOne
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_date", nullable = false)
    private java.util.Date paymentDate;

    @Column(name = "updated_date", nullable = false)
    private java.util.Date updatedDate;
    
    @Column(name = "trade_no")
    private String tradeNo;
}
