package petTopia.service.shop;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.ProductReview;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.user.MemberRepository;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    //判斷是否已評論過該商品的異常
    public class AlreadyReviewedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

		public AlreadyReviewedException(String message) {
            super(message);
        }
    }
    // 新增評論
    public ProductReview createReview(ProductReview productReview,Integer productId,Integer memberId) {
        // 檢查會員是否已經對該商品評論過
        Optional<ProductReview> existingReview = productReviewRepository.findByProductIdAndMemberId(
        		productId, memberId
        );
        
        if (existingReview.isPresent()) {
            // 拋出自定義的異常
            throw new AlreadyReviewedException("您已經評論過該商品");
        }
        
        // 設定商品與會員
        productReview.setProduct(productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")));
        productReview.setMember(memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found")));

        productReview.setRating(productReview.getRating());
        productReview.setReviewDescription(productReview.getReviewDescription());
        productReview.setReviewTime(new Date());
        // 儲存評論
        return productReviewRepository.save(productReview);
    }
    
    // 根據 memberId 查找所有評論
    public List<ProductReview> getReviewsByMemberId(Integer memberId) {
        return productReviewRepository.findByMemberId(memberId);
    }

    // 根據評論ID找單個評論
    public ProductReview getReviewById(Integer reviewId) {
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }
    
    // 讀取所有評論（根據商品ID查詢）
    public List<ProductReview> getReviewsByProductId(Integer productId) {
        return productReviewRepository.findByProductId(productId);
    }

    // 修改評論
    public ProductReview updateReview(Integer reviewId, ProductReview productReview) {
        ProductReview existingReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 更新商品與會員（如果有需要）
        existingReview.setProduct(productReview.getProduct());
        existingReview.setMember(productReview.getMember());

        // 更新評分與評論描述
        existingReview.setRating(productReview.getRating());
        existingReview.setReviewDescription(productReview.getReviewDescription());

        // 儲存修改後的評論
        return productReviewRepository.save(existingReview);
    }

    // 刪除評論
    public void deleteReview(Integer reviewId) {
        ProductReview existingReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        productReviewRepository.delete(existingReview);
    }


}
