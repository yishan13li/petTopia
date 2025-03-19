package petTopia.model.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vendor_category")
@Data
public class VendorCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String name;
} 