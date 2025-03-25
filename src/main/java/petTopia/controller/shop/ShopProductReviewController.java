package petTopia.controller.shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    											@RequestBody ProductReview review,
    											@RequestParam Integer memberId
    											) {
        try {
            productReviewService.createReview(review,productId,memberId);
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
            // 查詢會員的所有評論
            List<ProductReview> reviews = productReviewService.getReviewsByMemberId(memberId);

            // 如果沒有找到評論，返回 404 Not Found
            if (reviews.isEmpty()) {
                return new ResponseEntity<>("No reviews found for this member", HttpStatus.NOT_FOUND);
            }

            // 返回成功，並帶有評論列表
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            // 捕獲例外並返回 500 Internal Server Error
            return new ResponseEntity<>("Error fetching reviews: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
