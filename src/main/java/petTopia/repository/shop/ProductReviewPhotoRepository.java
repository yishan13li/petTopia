package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petTopia.model.shop.ProductReview;
import petTopia.model.shop.ProductReviewPhoto;

@Repository
public interface ProductReviewPhotoRepository extends JpaRepository<ProductReviewPhoto, Integer> {
	
	@Query("SELECT pr FROM ProductReview pr LEFT JOIN FETCH pr.reviewPhotos WHERE pr.member.id = :memberId")
	List<ProductReviewPhoto> findByProductReviewId(Integer productReviewId);

	@Modifying
	@Transactional
	@Query("DELETE FROM ProductReviewPhoto p WHERE p.id IN :ids")
	void deleteByIdIn(@Param("ids") List<Integer> ids);

}
