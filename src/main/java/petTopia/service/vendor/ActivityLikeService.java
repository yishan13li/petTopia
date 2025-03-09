package petTopia.service.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ActivityLike;
import petTopia.repository.vendor.ActivityLikeRepository;

@Service
public class ActivityLikeService {

	@Autowired
	private ActivityLikeRepository activityLikeRepository;
	
	/* 新增或取消活動收藏 */	
	public void addOrCancelActivityLike(Integer memberId, Integer activityId) {
		ActivityLike activityLike = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

		if (activityLike == null) {
			ActivityLike newActivityLike = new ActivityLike();
			newActivityLike.setMemberId(memberId);
			newActivityLike.setVendorActivityId(activityId);
			activityLikeRepository.save(newActivityLike);
		} else {
			Integer activityLikeId = activityLike.getId();
			activityLikeRepository.deleteById(activityLikeId);
		}

	}
}
