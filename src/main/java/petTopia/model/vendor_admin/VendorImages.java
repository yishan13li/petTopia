package petTopia.model.vendor_admin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorImages {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JsonIgnore
    @ManyToOne	
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    
    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;
}
