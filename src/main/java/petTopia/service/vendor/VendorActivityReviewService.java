package petTopia.service.vendor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorActivityReviewRepository;
import petTopia.repository.vendor.VendorRepository;

@Service
public class VendorActivityReviewService {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorActivityRepository vendorActivityRepository;

	@Autowired
	private VendorActivityReviewRepository vendorActivityReviewRepository;

	@Autowired
	private MemberRepository memberRepository;

	/* 尋找單一活動其所有的評分及留言 */
	public List<VendorActivityReview> findActivityReviewByVendorId(Integer activityId) {
		List<VendorActivityReview> activityReviewList = vendorActivityReviewRepository
				.findByVendorActivityId(activityId);
		return activityReviewList;
	}

	/* 新增或修改活動評論 */
	public void addOrModifyActivityReview(Integer memberId, Integer activityId, String content) {
		VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId,
				activityId);

		Optional<VendorActivity> optional = vendorActivityRepository.findById(activityId);
		VendorActivity activityOptional = optional.get();
		Integer vendorId = activityOptional.getVendor().getId();

		Optional<Vendor> vendorOptional = vendorRepository.findById(vendorId);
		Vendor vendor = vendorOptional.get();

		if (review == null) {
			VendorActivityReview newReview = new VendorActivityReview();
			newReview.setMemberId(memberId);
			newReview.setVendor(vendor);
			newReview.setVendorActivity(activityOptional);
			newReview.setReviewContent(content);
			newReview.setReviewTime(new Date());
			vendorActivityReviewRepository.save(newReview);
		} else {
			review.setReviewContent(content);
			review.setReviewTime(new Date());
			vendorActivityReviewRepository.save(review);
		}
	}

	/* 刪除某成員對某活動之評論及評分 */
	public void deleteReviewByMemberIdAndVendorId(Integer memberId, Integer activityId) {
		VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId,
				activityId);
		Integer reviewId = review.getId();
		vendorActivityReviewRepository.deleteById(reviewId);
	}

	/* 將Member和ActivityReview轉換成DTO */
	public ActivityReviewDto ConvertActivityReviewToDto(Member member, VendorActivityReview review) {

		ActivityReviewDto dto = new ActivityReviewDto();

		// 設定評價資訊
		dto.setReviewId(review.getId());
		dto.setVendorId(review.getVendor().getId());
		dto.setReviewTime(review.getReviewTime());
		dto.setReviewContent(review.getReviewContent());

		// 設定會員資訊
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());

		return dto;
	}

	/* 查詢某個Activity所有評價之DTO */
	public List<ActivityReviewDto> findReviewListByActivityId(Integer activiyId) {
		List<VendorActivityReview> reviewList = vendorActivityReviewRepository.findByVendorActivityId(activiyId);

		List<ActivityReviewDto> dtoList = reviewList.stream().map(review -> {
			Member member = memberRepository.findById(review.getMemberId()).orElse(null);
			return ConvertActivityReviewToDto(member, review);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 藉由ID尋找評論 */
	public VendorActivityReview findReviewById(Integer reviewId) {
		VendorActivityReview review = vendorActivityReviewRepository.findById(reviewId).orElse(null);
		return review;
	}

	/* 藉由ID修改評論 */
	public VendorActivityReview rewriteReviewById(Integer reviewId, String content) {
		VendorActivityReview review = this.findReviewById(reviewId);
		review.setReviewContent(content);
		review.setReviewTime(new Date());
		vendorActivityReviewRepository.save(review);
		return review;
	}

	/* 藉由ID刪除評論 */
	public void deleteReviewById(Integer reviewId) {
		vendorActivityReviewRepository.deleteById(reviewId);
	}

	/* 新增文字評論 */
	public VendorActivityReview addReview(Integer memberId, Integer activityId, String content) {
		VendorActivity activity = vendorActivityRepository.findById(activityId).orElseGet(null);
		Vendor vendor = vendorRepository.findById(activity.getVendor().getId()).orElse(null);

		VendorActivityReview review = new VendorActivityReview();
		review.setMemberId(memberId);
		review.setVendor(vendor);
		review.setVendorActivity(activity);
		review.setReviewContent(content);
		review.setReviewTime(new Date());
		vendorActivityReviewRepository.save(review);

		return review;
	}

	/* 尋找某會員是否有對某活動留下評論 */
	public boolean getReviewIsExisted(Integer memberId, Integer activityId) {
		VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId,
				activityId);
		if (review != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/* 藉由member ID找到所有評論 */
	public List<VendorActivityReview> findReviewListByMemberId(Integer memberId) {
		List<VendorActivityReview> reviewList = vendorActivityReviewRepository.findByMemberId(memberId);
		return reviewList;
	}
}
