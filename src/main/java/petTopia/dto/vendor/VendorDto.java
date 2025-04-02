package petTopia.dto.vendor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorDto {
	
    private Integer id;
    
    private String name;
    
    private String description;
    
	private float totalRating = 0;
	
	private byte[] logoImag;
    
	private String logoImgBase64;
    
    private List<ActivityDto> activityDtoList;
    
}
