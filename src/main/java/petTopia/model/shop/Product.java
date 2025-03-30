package petTopia.model.shop;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product", 
	   uniqueConstraints = {
			   @UniqueConstraint(columnNames = { "product_detail_id", "product_color_id", "product_size_id" }) 
		})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "stock_quantity", nullable = false)
	private Integer stockQuantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 0)
	private BigDecimal unitPrice;

	@Column(name = "discount_price", precision = 10, scale = 0)
	private BigDecimal discountPrice;

	@Column(name = "created_time", insertable = false, updatable = false)
	private Date createdTime;

	@Column(name = "status", nullable = false)
	private Boolean status;
	
	@JsonIgnore
	@Column(name="photo")
	private byte[] photo;

	@ManyToOne()
	@JoinColumn(name = "product_detail_id", nullable = false)
	private ProductDetail productDetail;

	@ManyToOne()
	@JoinColumn(name = "product_color_id")
	private ProductColor productColor;

	@ManyToOne()
	@JoinColumn(name = "product_size_id")
	private ProductSize productSize;
	
}
