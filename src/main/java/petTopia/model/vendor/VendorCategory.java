package petTopia.model.vendor;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "vendor_category")
@Data
public class VendorCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "vendorCategory")
    private List<Vendor> vendors;
} 