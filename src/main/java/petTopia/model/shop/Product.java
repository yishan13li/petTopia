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

	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "discount_price")
	private BigDecimal discountPrice;

//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss EEEE", timezone = "GMT+8") // JSON 時間格式化
//	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 在 thymeleaf 要用 {{}} 強制轉換
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_time", nullable = false)
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
