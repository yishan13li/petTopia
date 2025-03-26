package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.ProductReviewPhoto;

@Repository
public interface ProductReviewPhotoRepository extends JpaRepository<ProductReviewPhoto, Integer> {
	
	@Query("SELECT pr FROM ProductReview pr LEFT JOIN FETCH pr.reviewPhotos WHERE pr.member.id = :memberId")
	List<ProductReviewPhoto> findByProductReviewId(Integer productReviewId);
}
