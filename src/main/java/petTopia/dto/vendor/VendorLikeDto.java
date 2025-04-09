package petTopia.dto.vendor;

import java.util.Base64;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.util.ImageConverter;

@Getter
@Setter
@NoArgsConstructor
public class VendorLikeDto {
	
	/* 評價資訊 */
	private Integer id;
	private Integer vendorId;
	private String vendorName;
	private String vendorDescription;
	private String vendorCategory;
	
	/* 會員資訊 */
    private Integer memberId;
    private String name;
    private boolean gender;
    private byte[] profilePhoto;
    private String profilePhotoBase64;
    
    /* 設定圖片之Base64 */
    public void setProfilePhoto(byte[] profilePhoto) {
    	if(profilePhoto!=null) {    		
    		String mimeType = ImageConverter.getMimeType(profilePhoto);
    		this.profilePhotoBase64 = "data:%s;base64,".formatted(mimeType)
    				+ Base64.getEncoder().encodeToString(profilePhoto);
    	}
		this.profilePhoto = profilePhoto;
    }
}
