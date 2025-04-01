package petTopia.dto.vendor_admin;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopActivityDTO {
    private Integer activityId;
    private String activityName;
    private Long  registrationCount;
    private String description;
    
	

}
