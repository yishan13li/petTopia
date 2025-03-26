package petTopia.model.shop;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_color")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductColor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="name", unique = true, nullable = false)
	private String name;
	
	// 使用 id 比較兩個 entity 是否相等
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductColor that = (ProductColor) o;
        return Objects.equals(this.getId(), that.getId());  // 使用 getId() 方法比較 id
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());  // 使用 getId() 方法計算 hash code
    }
    
}
