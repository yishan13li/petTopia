package petTopia.dto.shop;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductReviewResponseDto {

    private Integer reviewId;
    private Integer memberId;
    private String memberName;
    private Integer productId;
    private Integer productDetailId;
    private String productName;
    private String productColor;
    private String productSize;
    private byte[] productPhoto;
    private Integer rating;
    private String reviewDescription;
    private List<ProductReviewPhotoDto> productReviewPhoto; // 存 Base64 格式的圖片
    private Date reviewTime;
}
