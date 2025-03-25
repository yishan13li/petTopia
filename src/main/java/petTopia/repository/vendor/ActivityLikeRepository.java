package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Member;
import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.VendorActivity;

public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Integer> {

	/* 尋找單一會員對單一活動是否有收藏 */
	public ActivityLike findByMemberIdAndVendorActivityId(Integer memberId, Integer activityId);

	public List<ActivityLike> findByVendorActivity(VendorActivity vendorActivity);

	public List<ActivityLike> findByMember(Member member);
}
