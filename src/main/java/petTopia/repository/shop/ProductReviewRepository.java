package petTopia.repository.shop;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    List<ProductReview> findByProductId(Integer productId);

    Optional<ProductReview> findByProductIdAndMemberId(Integer productId, Integer memberId);
    
    Optional<ProductReview> findById(Integer reviewId);

    List<ProductReview> findByMemberId(Integer memberId);
}
