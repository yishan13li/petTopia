package petTopia.dto.vendor;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VendorReviewDto {
    
    /* 評價資訊 */
	private Integer reviewId;
    private Integer vendorId;
    private Date reviewTime;
    private String reviewContent;
    private Integer ratingEnvironment;
    private Integer ratingPrice;
    private Integer ratingService;
	
    /* 會員資訊 */
    private Integer memberId;
    private String name;
    private boolean gender;
    private byte[] profilePhoto;
    private String profilePhotoBase64;
    
    /* 設定圖片之Base64 */
    public void setProfilePhoto(byte[] profilePhoto) {
    	if(profilePhoto!=null) {    		
    		String mimeType = getMimeType(profilePhoto);
    		this.profilePhotoBase64 = "data:%s;base64,".formatted(mimeType)
    				+ Base64.getEncoder().encodeToString(profilePhoto);
    	}
		this.profilePhoto = profilePhoto;
    }
    
    /* 讀取檔案之MimeType */
	public static String getMimeType(byte[] imageBytes) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
			String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
			
			inputStream.close();
			
			return mimeType;

		} catch (IOException e) {
			return "image/jpg";
		}
	}
}

