package petTopia.service.vendor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.user.MemberBean;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Service
public class VendorReviewService {

	@Autowired
	private VendorReviewRepository vendorReviewRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;

	/* 尋找單一店家其所有的評分及留言 */
	public List<VendorReview> findVendorReviewByVendorId(Integer vendorId) {
		List<VendorReview> vendorReviewList = vendorReviewRepository.findByVendorId(vendorId);
		return vendorReviewList;
	}

	/* 上傳多張圖片 */
	@Transactional
	public void addReviewPhotos(Integer reviewId, List<MultipartFile> reviewPhotos) throws IOException {

		List<ReviewPhoto> photoList = new ArrayList<>();

		for (MultipartFile photo : reviewPhotos) {
			ReviewPhoto reviewPhoto = new ReviewPhoto();

			Optional<VendorReview> optional = vendorReviewRepository.findById(reviewId);
			VendorReview vendorReview = optional.get();

			reviewPhoto.setVendorReview(vendorReview);
			reviewPhoto.setPhoto(photo.getBytes()); // 取得byte[]
			reviewPhotoRepository.save(reviewPhoto);

			photoList.add(reviewPhoto);
		}

	}

	/* 新增或修改文字評論 */
	public void addOrModifyVendorTextReview(Integer memberId, Integer vendorId, String content) {
		VendorReview vendorReview = vendorReviewRepository.findByMemberIdAndVendorId(memberId, vendorId);

		if (vendorReview == null) {

			VendorReview newVendorReview = new VendorReview();
			newVendorReview.setMemberId(memberId);
			newVendorReview.setVendorId(vendorId);
			newVendorReview.setReviewContent(content);
			newVendorReview.setReviewTime(new Date());
			vendorReviewRepository.save(newVendorReview);

		} else {

			vendorReview.setReviewContent(content);
			vendorReview.setReviewTime(new Date());
			vendorReviewRepository.save(vendorReview);

		}
	}

	/* 新增或修改星星評分 */
	public void addOrModifyVendorStarReview(Integer memberId, Integer vendorId, Integer ratingEnv, Integer ratingPrice,
			Integer ratingService) {
		VendorReview vendorReview = vendorReviewRepository.findByMemberIdAndVendorId(memberId, vendorId);

		if (vendorReview == null) {

			VendorReview newVendorReview = new VendorReview();
			newVendorReview.setMemberId(memberId);
			newVendorReview.setVendorId(vendorId);
			newVendorReview.setRatingEnvironment(ratingEnv);
			newVendorReview.setRatingPrice(ratingPrice);
			newVendorReview.setRatingService(ratingService);
			vendorReviewRepository.save(newVendorReview);

		} else {

			vendorReview.setRatingEnvironment(ratingEnv);
			vendorReview.setRatingPrice(ratingPrice);
			vendorReview.setRatingService(ratingService);
			vendorReviewRepository.save(vendorReview);

		}
	}

	/* 將Member和VendorReview轉換成DTO */
	public VendorReviewDto ConvertVendorReviewToDto(MemberBean member, VendorReview review) {

		VendorReviewDto dto = new VendorReviewDto();

		/* 設定評價資訊 */
		dto.setReviewId(review.getId());
		dto.setVendorId(review.getVendorId());
		dto.setReviewTime(review.getReviewTime());
		dto.setReviewContent(review.getReviewContent());
		dto.setRatingEnvironment(review.getRatingEnvironment());
		dto.setRatingPrice(review.getRatingPrice());
		dto.setRatingService(review.getRatingService());

		/* 設定會員資訊 */
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());

		return dto;
	}

	/* 查詢某個vendorId所有評價之DTO */
	public List<VendorReviewDto> getReviewListByVendorId(Integer vendorId) {
		List<VendorReview> reviewList = vendorReviewRepository.findByVendorId(vendorId);

		List<VendorReviewDto> dtoList = reviewList.stream().map(review -> {
			Optional<MemberBean> optional = memberRepository.findById(review.getMemberId());
			if (optional.isEmpty()) {
				return null;
			}
			MemberBean member = optional.get();
			return ConvertVendorReviewToDto(member, review);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 刪除某成員對某店家之評論及評分 */
	public void deleteReviewByMemberIdAndVendorId(Integer memberId, Integer vendorId) {
		VendorReview vendorReview = vendorReviewRepository.findByMemberIdAndVendorId(memberId, vendorId);

		/* 將評論內容及時間設為空，避免刪掉星星評分 */
//		vendorReview.setReviewContent(null);
//		vendorReview.setReviewTime(null);
//		vendorReviewRepository.save(vendorReview);

		Integer reviewId = vendorReview.getId();
		vendorReviewRepository.deleteById(reviewId);
	}

}
