package petTopia.controller.shop;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.shop.ProductReview;
import petTopia.service.shop.ProductReviewService;
import petTopia.service.shop.ProductReviewService.AlreadyReviewedException;

@RestController
@RequestMapping("/shop")
public class ShopProductReviewController {
	
	@Autowired
    private ProductReviewService productReviewService;

    // 新增評論
    @PostMapping("/product/{productId}/review/create")
    public ResponseEntity<String> createReview(@PathVariable Integer productId,
    	    @RequestParam Integer memberId,
    	    @RequestParam(value = "rating") Integer rating,
    	    @RequestParam(value = "reviewDescription") String reviewDescription,
    	    @RequestPart(value = "reviewPhotos", required = false) List<MultipartFile> reviewPhotos) throws IOException {
    	  
    	try {
            // 這裡處理產品評價邏輯
            ProductReview review = new ProductReview(rating, reviewDescription);
            productReviewService.createReview(review,productId,memberId, reviewPhotos);
            return new ResponseEntity<>("Review successfully created", HttpStatus.CREATED);
        } catch (AlreadyReviewedException e) {
            // 如果已經評論過該商品，返回具體的錯誤訊息
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // 捕獲其他 RuntimeException 並返回錯誤訊息
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 根據 memberId 找該會員的所有評論
    @GetMapping("/reviews/member/{memberId}")
    public ResponseEntity<?> getReviewsByMemberId(@PathVariable Integer memberId) {
        try {
            List<ProductReview> reviews = productReviewService.getReviewsByMemberId(memberId);

            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this member");
            }

            // 轉換評論列表，包含 Base64 照片
            List<Map<String, Object>> reviewList = reviews.stream().map(review -> {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("id", review.getId());
                reviewMap.put("rating", review.getRating());
                reviewMap.put("reviewDescription", review.getReviewDescription());
                reviewMap.put("reviewTime", review.getReviewTime());
                reviewMap.put("productId", review.getProduct().getId());

                // 處理圖片
                List<String> photosBase64 = review.getReviewPhotos().stream()
                    .map(photo -> Base64.getEncoder().encodeToString(photo.getReviewPhoto()))
                    .collect(Collectors.toList());
                reviewMap.put("photos", photosBase64);

                return reviewMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(reviewList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
        }
    }

}
