package petTopia.service.vendor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.MemberBean;
import petTopia.model.vendor.ActivityLike;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ActivityLikeRepository;
import petTopia.repository.vendor.VendorActivityRepository;

@Service
public class ActivityLikeService {

	@Autowired
	private ActivityLikeRepository activityLikeRepository;
	
	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	/* 新增或取消活動收藏 */	
	public void addOrCancelActivityLike(Integer memberId, Integer activityId) {
		Optional<MemberBean> member = memberRepository.findById(memberId);
		Optional<VendorActivity> vendorActivity = vendorActivityRepository.findById(activityId);
		ActivityLike activityLike = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

		if (activityLike == null) {
			ActivityLike newActivityLike = new ActivityLike();
			newActivityLike.setMember(member.get());
			newActivityLike.setVendorActivity(vendorActivity.get());
			activityLikeRepository.save(newActivityLike);
		} else {
			Integer activityLikeId = activityLike.getId();
			activityLikeRepository.deleteById(activityLikeId);
		}

	}
}
