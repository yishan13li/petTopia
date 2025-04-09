package petTopia.dto.vendor;


import java.util.Base64;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.util.ImageConverter;

@Getter
@Setter
@NoArgsConstructor
public class VendorReviewDto {
    
    /* 評價資訊 */
	private Integer reviewId;
	private Integer vendorId;
	private String vendorName;
    private Date reviewTime;
    private String reviewContent;
    private Integer ratingEnvironment;
    private Integer ratingPrice;
    private Integer ratingService;
    private List<ReviewPhoto> reviewPhotos;
    private Boolean hasPhotos;
	
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

