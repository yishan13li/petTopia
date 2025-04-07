package petTopia.dto.shop;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductReviewPhotoDto {
	
	private Integer reviewPhotoId;
	private String reviewPhotos; //base64
}
