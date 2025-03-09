package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.ActivityLike;

public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Integer> {

	/* 尋找單一會員對單一活動是否有收藏 */
	public ActivityLike findByMemberIdAndVendorActivityId(Integer memberId, Integer activityId);
}
