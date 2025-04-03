package petTopia.repository.shop;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductReview;
import petTopia.projection.shop.ProductDetailRatingProjection;
import petTopia.projection.shop.ProductRatingProjection;

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
    
    // 查詢所有會員的評論並根據 reviewTime 降冪排序（含分頁）
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.reviewTime DESC")
    Page<ProductReview> findAllReviewsOrderByReviewTimeDesc(Pageable pageable);

    // 查詢所有評論並根據 id 降冪排序（含分頁）
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.id DESC")
    Page<ProductReview> findAllReviewsByIdDesc(Pageable pageable);

    // 根據 rating 降冪排序（含分頁）
    @Query("SELECT pr FROM ProductReview pr ORDER BY pr.rating DESC")
    Page<ProductReview> findAllReviewsByRatingDesc(Pageable pageable);

    // 模糊搜尋（商品 ID、會員 ID、評論 ID 或評論描述）
    @Query("""
            SELECT pr FROM ProductReview pr
            JOIN pr.product p
            JOIN p.productDetail pd
            WHERE CAST(pr.product.id AS string) LIKE CONCAT('%', :keyword, '%')
               OR CAST(pr.member.id AS string) LIKE CONCAT('%', :keyword, '%')
               OR CAST(pr.id AS string) LIKE CONCAT('%', :keyword, '%')
               OR LOWER(pr.reviewDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(pd.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY pr.reviewTime DESC
        """)
    Page<ProductReview> searchReviews(@Param("keyword") String keyword, Pageable pageable);

    //刪除評論
    void deleteById(Integer reviewId);
    
    //========統計分析=========
    //評分最高的商品
    @Query("SELECT p AS product, AVG(pr.rating) AS avgRating " +
    	       "FROM Product p " +
    	       "JOIN ProductReview pr ON p.id = pr.product.id " +
    	       "GROUP BY p " +
    	       "ORDER BY avgRating DESC")
    List<ProductRatingProjection> findTop5ProductsByAverageRating(Pageable pageable);

    //評分最高商品種類
    @Query("SELECT pd AS productDetail, AVG(pr.rating) AS avgRating " +
    	       "FROM ProductReview pr " +
    	       "JOIN pr.product p " +
    	       "JOIN p.productDetail pd " +
    	       "GROUP BY pd " +
    	       "ORDER BY avgRating DESC")
    	List<ProductDetailRatingProjection> findTop3ProductDetailsByAverageRating(Pageable pageable);

    @Query("SELECT COUNT(pr) FROM ProductReview pr")
    long countTotalProductReviews();
}
