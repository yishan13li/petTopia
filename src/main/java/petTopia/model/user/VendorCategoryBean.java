package petTopia.model.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vendor_category")
@Data
public class VendorCategoryBean {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "category_name", nullable = false)
    private String categoryName;
} 