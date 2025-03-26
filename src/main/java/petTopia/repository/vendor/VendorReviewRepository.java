package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.vendor.VendorReview;

public interface VendorReviewRepository extends JpaRepository<VendorReview, Integer> {

	/* 尋找單一店家其所有的評分及留言 */
	public List<VendorReview> findByVendorId(Integer vendorId);

	/* 尋找單一會員對單一店家的評分及留言 */
	public VendorReview findFirstByMemberIdAndVendorId(Integer memberId, Integer vendorId); // 有多筆時僅回傳第一筆

	/* 尋找單一會員對單一活動的評論 */
	public VendorReview findByMemberIdAndVendorId(Integer memberId, Integer vendorId);
	
	/* 尋找單一會員其所有的評分及留言 */
	public List<VendorReview> findByMemberId(Integer memberId);

	 @Query("SELECT COUNT(vr) FROM VendorReview vr WHERE vr.vendorId = :vendorId " +
	           "AND (vr.reviewContent LIKE %:keyword1% OR vr.reviewContent LIKE %:keyword2% " +
	           "OR vr.reviewContent LIKE %:keyword3% OR vr.reviewContent LIKE %:keyword4% " +
	           "OR vr.reviewContent LIKE %:keyword5%)")
	    int countMatchingReviews(@Param("vendorId") Integer vendorId,
	                             @Param("keyword1") String keyword1,
	                             @Param("keyword2") String keyword2,
	                             @Param("keyword3") String keyword3,
	                             @Param("keyword4") String keyword4,
	                             @Param("keyword5") String keyword5);
	 
	 
}
