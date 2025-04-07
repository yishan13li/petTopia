package petTopia.dto.vendor_admin;


import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import petTopia.model.vendor.Vendor;

@Getter
@Setter
public class CertificationDTO {

	private Vendor vendor;
	private Integer certificationId;
    private String certificationStatus;
    private String reason;
    private Date requestDate;
    private Date approvedDate;
    private List<CertificationTagDTO> certificationTags;

    // Getters and Setters

    @Getter
    @Setter
    public static class CertificationTagDTO {
        private String tagName;
        private boolean meetsStandard;

        // Getters and Setters
    }
}
