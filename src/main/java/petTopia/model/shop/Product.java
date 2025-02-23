package petTopia.model.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

	@Column(name = "stock_quantity")
	private Integer stockQuantity;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;

	@Column(name = "discount_price")
	private BigDecimal discountPrice;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss EEEE", timezone = "GMT+8") // JSON 時間格式化
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 在 thymeleaf 要用 {{}} 強制轉換
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "status")
	private Boolean status;

	@ManyToOne()
	@JoinColumn(name = "product_detail_id")
	private ProductDetail productDetail;

	@ManyToOne()
	@JoinColumn(name = "product_color_id")
	private ProductColor productColor;

	@ManyToOne()
	@JoinColumn(name = "product_size_id")
	private ProductSize productSize;
	
//	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//	private List<ProductPhoto> productPhoto = new ArrayList<>();

}
