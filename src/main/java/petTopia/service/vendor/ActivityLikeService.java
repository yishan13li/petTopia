package petTopia.service.vendor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor.ActivityLikeDto;
import petTopia.model.user.Member;
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

	/* 取得活動收藏狀態 */
	public Boolean getActivityLikeStatus(Integer memberId, Integer activityId) {
		ActivityLike activityLike = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

		if (activityLike != null) {
			return true;
		} else {
			return false;
		}
	}

	/* 新增或取消活動收藏 */
	public boolean toggleActivityLike(Integer memberId, Integer activityId) {
		Optional<Member> member = memberRepository.findById(memberId);
		Optional<VendorActivity> vendorActivity = vendorActivityRepository.findById(activityId);
		ActivityLike activityLike = activityLikeRepository.findByMemberIdAndVendorActivityId(memberId, activityId);

		if (activityLike == null) {
			ActivityLike newActivityLike = new ActivityLike();
			newActivityLike.setMember(member.get());
			newActivityLike.setVendorActivity(vendorActivity.get());
			activityLikeRepository.save(newActivityLike);
			return true;
		} else {
			Integer activityLikeId = activityLike.getId();
			activityLikeRepository.deleteById(activityLikeId);
			return false;
		}
	}

	/* 將Member和ActivityLike轉換成DTO */
	public ActivityLikeDto ConvertActivityLikeToDto(Member member, ActivityLike like) {
		ActivityLikeDto dto = new ActivityLikeDto();
		dto.setId(like.getId());
		dto.setVendorId(like.getVendorActivity().getVendor().getId());
		dto.setActivityId(like.getVendorActivity().getId());
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());
		return dto;
	}

	/* 查詢某個vendorId所有收藏之DTO */
	public List<ActivityLikeDto> findMemberLikeListByActivityId(Integer activityId) {
		VendorActivity activity = vendorActivityRepository.findById(activityId).orElse(null);
		List<ActivityLike> likeList = activityLikeRepository.findByVendorActivity(activity);

		List<ActivityLikeDto> dtoList = likeList.stream().map(like -> {
			Member member = memberRepository.findById(like.getMember().getId()).get();
			return ConvertActivityLikeToDto(member, like);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 查詢某個memberId所有收藏的活動 */
	public List<ActivityLike> findLikeListByMemberId(Integer memberId) {
		Member member = memberRepository.findById(memberId).orElse(null);
		List<ActivityLike> likeList = activityLikeRepository.findByMember(member);
		return likeList;
	}

	/* 藉ID刪除收藏 */
	public boolean deleteByLikeId(Integer likeId) {
		if (activityLikeRepository.existsById(likeId)) {
			activityLikeRepository.deleteById(likeId);
			return true;
		}
		return false;
	}

}
