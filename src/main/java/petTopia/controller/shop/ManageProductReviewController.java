package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.projection.shop.ProductDetailRatingProjection;
import petTopia.projection.shop.ProductRatingProjection;
import petTopia.service.shop.ProductReviewService;

@RestController
@RequestMapping("/manage/shop")
public class ManageProductReviewController {

	@Autowired
    private ProductReviewService productReviewService;

	//所有評論 
	@GetMapping("/reviews")
	public ResponseEntity<?> getAllReviews(
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "time") String sortBy) {
	    try {
	        Page<ProductReviewResponseDto> reviews;

	        // 根據 sortBy 參數決定排序方式
	        switch (sortBy.toLowerCase()) {
	            case "id":
	                reviews = productReviewService.getAllReviewsSortedById(page, size);
	                break;
	            case "rating":
	                reviews = productReviewService.getAllReviewsSortedByRating(page, size);
	                break;
	            case "time":
	            default:
	                reviews = productReviewService.getAllReviewsSortedByTime(page, size);
	                break;
	        }

	        if (reviews.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No reviews found.");
	        }
	        return ResponseEntity.ok(reviews);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching reviews: " + e.getMessage());
	    }
	}


    // 模糊搜尋（可搜尋商品 ID、會員 ID、評論 ID 或評論描述）
    @GetMapping("/reviews/search")
    public ResponseEntity<?> searchReviews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductReviewResponseDto> reviews = productReviewService.searchReviews(keyword, page, size);
            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
            }
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error searching reviews: " + e.getMessage());
        }
    }
    
    //刪除評論
    @DeleteMapping("/review/{reviewId}/delete")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        try {
            productReviewService.deleteReviewById(reviewId);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting review: " + e.getMessage());
        }
    }
    
    // 獲取評分最高的前 5 名商品
    @GetMapping("/review/ratingTop5Product")
    public ResponseEntity<List<ProductRatingProjection>> getTop5ProductsByAverageRating() {
        try {
            List<ProductRatingProjection> products = productReviewService.getTop5ProductsByAverageRating();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(products); // 200 OK
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>()); 
        }
    }

    // 獲取評分最高的前 3 名商品種類
    @GetMapping("/review/ratingTop3ProductDetail")
    public ResponseEntity<List<ProductDetailRatingProjection>> getTop3ProductDetailsByAverageRating() {
        try {
            List<ProductDetailRatingProjection> productDetails = productReviewService.getTop3ProductDetailsByAverageRating();
            if (productDetails.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(productDetails); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>()); 
        }
    }
    
}
