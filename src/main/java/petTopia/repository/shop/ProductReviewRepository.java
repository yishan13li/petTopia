package petTopia.repository.shop;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    List<ProductReview> findByProductId(Integer productId);

    Optional<ProductReview> findByProductIdAndMemberId(Integer productId, Integer memberId);
    
    Optional<ProductReview> findById(Integer reviewId);

    Page<ProductReview> findByMemberIdOrderByReviewTimeDesc(Integer memberId, Pageable pageable);

    // 查詢某個商品（透過 productDetailId）所有評論的平均 rating
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.productDetail.id = :productDetailId")
    Double findAverageRatingByProductDetailId(@Param("productDetailId") Integer productDetailId);
    
    // 查詢每個商品的評論總數
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.product.productDetail.id = :productDetailId")
    Integer countReviewsByProductDetailId(@Param("productDetailId") Integer productDetailId);

    // 查詢該商品的所有評論
    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.productDetail.id = :productDetailId ORDER BY pr.reviewTime DESC")
    Page<ProductReview> findAllReviewsByProductDetailIdOrderByReviewTimeDesc(@Param("productDetailId") Integer productDetailId, Pageable pageable);
    
    //==========未完成==========
    // 查詢所有會員的評論並根據 reviewTime 降冪排序
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.reviewTime DESC")
    List<ProductReview> findAllReviewsOrderByReviewTimeDesc();

    // 查詢所有評論並根據 id 降冪排序
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.id DESC")
    List<ProductReview> findAllReviewsByIdDesc();

    // 根據 rating 降冪排序
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.rating DESC")
    List<ProductReview> findAllReviewsByRatingDesc();

}
