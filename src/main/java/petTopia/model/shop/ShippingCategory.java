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
@Table(name = "shipping_category")
public class ShippingCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "shipping_cost", nullable = false, columnDefinition = "DECIMAL(10,0) DEFAULT 0")
    private BigDecimal shippingCost;

    @Column(name = "shipping_day", nullable = false, columnDefinition = "INT DEFAULT 7")
    private Integer shippingDay;
}
