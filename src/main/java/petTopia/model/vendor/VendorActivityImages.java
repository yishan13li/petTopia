package petTopia.model.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor_activity_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorActivityImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @JsonIgnore
    @ManyToOne	
    @JoinColumn(name = "vendor_activity_id", nullable = false)
    private VendorActivity vendorActivity;
    
    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;
    
    /* 使用Transient防止被序列化，用於Service層賦值 */
    @Transient
    private String imageBase64;
}
