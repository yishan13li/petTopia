package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorReview;

public interface VendorReviewRepository extends JpaRepository<VendorReview, Integer> {

	/* 尋找單一店家其所有的評分及留言 */
	public List<VendorReview> findByVendorId(Integer vendorId);

	/* 尋找單一會員對單一店家的評分及留言 */
	public VendorReview findFirstByMemberIdAndVendorId(Integer memberId, Integer vendorId); // 有多筆時僅回傳第一筆

}
