package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorActivityReview;

public interface VendorActivityReviewRepository extends JpaRepository<VendorActivityReview, Integer> {

	/* 尋找單一活動其所有的評論 */
	public List<VendorActivityReview> findByVendorActivityId(Integer activityId);

	/* 尋找單一會員對單一活動的評論 */
	public VendorActivityReview findByMemberIdAndVendorActivityId(Integer memberId, Integer activityId);

	/* 藉活動ID刪除活動 */
	public void deleteByVendorActivityId(Integer id);

	/* 尋找單一會員其所有的評論 */
	public List<VendorActivityReview> findByMemberId(Integer memberId);
}
