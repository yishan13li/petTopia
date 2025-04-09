package petTopia.controller.shop;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.model.shop.ProductReview;
import petTopia.service.shop.ProductReviewService;
import petTopia.service.shop.ProductReviewService.AlreadyReviewedException;

@RestController
@RequestMapping("/shop")
public class ShopProductReviewController {
	
	@Autowired
    private ProductReviewService productReviewService;

    // 新增會員評論
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
    public ResponseEntity<?> getReviewsByMemberId(
            @PathVariable Integer memberId,
            @RequestParam(defaultValue = "1", required = false) int page,  // 默認第1頁
            @RequestParam(defaultValue = "10", required = false) int size) {  // 默認每頁10條評論
        try {
            // 獲取分頁結果
            Page<ProductReviewResponseDto> reviews = productReviewService.getReviewsByMemberId(memberId, page, size);

            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this member");
            }

            return ResponseEntity.ok(reviews);  // 返回 Page 格式的評論資料
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
        }
    }
    
    //修改會員評論
    @PutMapping("/reviews/{reviewId}/update")
    public ResponseEntity<?> updateReview(@PathVariable Integer reviewId, 
    	    @RequestParam(value = "rating", required = false) Integer rating,
    	    @RequestParam(value = "reviewDescription", required = false) String reviewDescription,
    	    @RequestPart(value = "newPhotos", required = false) List<MultipartFile> newPhotos,
    	    @RequestParam(value = "deletePhotoIds", required = false) List<Integer> deletePhotoIds) throws IOException {
	try {
		boolean updated = productReviewService.updateReview(reviewId, rating,reviewDescription, newPhotos,deletePhotoIds);
	
		if (updated) {
			return new ResponseEntity<>("Review successfully updated", HttpStatus.OK);
		}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("評論未找到，更新失敗");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片處理出錯");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新評論出錯");
		}
    }
    
    // 根據 productDetailId 查找商品的平均評分
    @GetMapping("/products/{productDetailId}/reviews/avgRating")
    public ResponseEntity<?> getAverageRating(@PathVariable Integer productDetailId) {
    	try {
    		Double averageRating = productReviewService.getAverageRatingByProductDetailId(productDetailId);
    		if (averageRating == null) {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product.");
    		}
    		return ResponseEntity.ok(averageRating);
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching average rating: " + e.getMessage());
    	}
    }
    
 // 查詢某個商品的評論總數
    @GetMapping("/products/{productDetailId}/reviews/count")
    public ResponseEntity<?> getReviewsCount(@PathVariable Integer productDetailId) {
        try {
            Integer count = productReviewService.getReviewsCountByProductDetailId(productDetailId);
            if (count == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product.");
            }
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching review count: " + e.getMessage());
        }
    }
    
    //該商品的所有評論
    @GetMapping("/reviews/product/{productDetailId}")
    public ResponseEntity<?> getReviewsByProductDetailId(
            @PathVariable Integer productDetailId,
            @RequestParam(defaultValue = "1", required = false) int page,  // 默認第1頁
            @RequestParam(defaultValue = "10", required = false) int size) {  // 默認每頁10條評論
        try {
            Page<ProductReviewResponseDto> reviews = productReviewService.getReviewsByProductDetailId(productDetailId, page, size);

            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found for this product.");
            }

            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
        }
    }
    
    // 檢查會員是否已經對該商品評論過
    @GetMapping("/review/hasReviewed")
    public ResponseEntity<Map<String, Boolean>> checkIfReviewed(
            @RequestParam Integer productId, 
            @RequestParam Integer memberId) {

        boolean hasReviewed = productReviewService.hasReviewed(productId, memberId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReviewed", hasReviewed);
        return ResponseEntity.ok(response);
    }

}
