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

import petTopia.dto.vendor.VendorReviewDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Service
public class VendorReviewService {

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorReviewRepository vendorReviewRepository;

	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReviewPhotoService reviewPhotoService;

	/* 尋找單一店家其所有的評分及留言 */
	public List<VendorReview> findVendorReviewByVendorId(Integer vendorId) {
		List<VendorReview> vendorReviewList = vendorReviewRepository.findByVendorId(vendorId);
		return vendorReviewList;
	}

	/* 上傳多張圖片 */
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
		VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId);

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

	/* 將Member和VendorReview轉換成DTO */
	public VendorReviewDto ConvertVendorReviewToDto(Member member, VendorReview review) {

		VendorReviewDto dto = new VendorReviewDto();

		// 設定評價資訊
		dto.setReviewId(review.getId());
		dto.setVendorId(review.getVendorId());
		Vendor vendor = vendorRepository.findById(review.getVendorId()).orElse(null);
		dto.setVendorName(vendor.getName());
		dto.setReviewTime(review.getReviewTime());
		dto.setReviewContent(review.getReviewContent());
		dto.setRatingEnvironment(review.getRatingEnvironment());
		dto.setRatingPrice(review.getRatingPrice());
		dto.setRatingService(review.getRatingService());

		// 設定Base64後再寫入
		List<ReviewPhoto> reviewPhotoList = reviewPhotoService.findPhotoListByReviewId(review.getId());
		dto.setReviewPhotos(reviewPhotoList);

		// 判斷 ReviewPhoto 是否為空，以控制按鈕
		List<ReviewPhoto> reviewPhotos = review.getReviewPhotos();
		if (reviewPhotos != null && !reviewPhotos.isEmpty()) {
			dto.setHasPhotos(true);
		} else {
			dto.setHasPhotos(false);
		}

		// 設定會員資訊
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());

		return dto;
	}

	/* 查詢某個vendorId所有評價之DTO */
	public List<VendorReviewDto> findReviewListByVendorId(Integer vendorId) {
		List<VendorReview> reviewList = vendorReviewRepository.findByVendorId(vendorId);

		List<VendorReviewDto> dtoList = reviewList.stream().map(review -> {
			Optional<Member> optional = memberRepository.findById(review.getMemberId());
			if (optional.isEmpty()) {
				return null;
			}
			Member member = optional.get();
			return ConvertVendorReviewToDto(member, review);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 查詢某個vendorId所有評價之Entity */
	public List<VendorReview> findReviewsByVendorId(Integer vendorId) {
		List<VendorReview> reviewList = vendorReviewRepository.findByVendorId(vendorId);

		return reviewList;
	}

	/* 刪除某成員對某店家之評論及評分 */
	public void deleteReviewByMemberIdAndVendorId(Integer memberId, Integer vendorId) {
		VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId);
		Integer reviewId = vendorReview.getId();
		vendorReviewRepository.deleteById(reviewId);
	}

	/* 藉由ID尋找評論 */
	public VendorReview findReviewById(Integer reviewId) {
		VendorReview review = vendorReviewRepository.findById(reviewId).orElse(null);
		return review;
	}

	/* 藉由ID修改評論 */
	public VendorReview rewriteReviewById(Integer reviewId, String content) {
		Optional<VendorReview> optional = vendorReviewRepository.findById(reviewId);
		VendorReview review = optional.get();
		review.setReviewContent(content);
		review.setReviewTime(new Date());
		vendorReviewRepository.save(review);
		return review;
	}

	/* 藉由ID刪除評論 */
	public void deleteReviewById(Integer reviewId) {
		vendorReviewRepository.deleteById(reviewId);
	}

	/* 新增文字及圖片評論 */
	public VendorReview addReview(Integer memberId, Integer vendorId, String content, List<MultipartFile> reviewPhotos)
			throws IOException {
		VendorReview review = new VendorReview();
		review.setMemberId(memberId);
		review.setVendorId(vendorId);
		review.setReviewContent(content);
		review.setReviewTime(new Date());
		vendorReviewRepository.save(review);

		Integer reviewId = review.getId();
		addReviewPhotos(reviewId, reviewPhotos);

		return review;
	}

	/* 新增星星評分 */
	public VendorReview addStarReview(Integer memberId, Integer vendorId, Integer ratingEnv, Integer ratingPrice,
			Integer ratingService) {
		VendorReview vendorReview = vendorReviewRepository.findFirstByMemberIdAndVendorId(memberId, vendorId);

		if (vendorReview == null) {

			VendorReview newVendorReview = new VendorReview();
			newVendorReview.setMemberId(memberId);
			newVendorReview.setVendorId(vendorId);
			newVendorReview.setRatingEnvironment(ratingEnv);
			newVendorReview.setRatingPrice(ratingPrice);
			newVendorReview.setRatingService(ratingService);
			newVendorReview.setReviewTime(new Date());
			vendorReviewRepository.save(newVendorReview);

			return newVendorReview;
		} else {

			vendorReview.setRatingEnvironment(ratingEnv);
			vendorReview.setRatingPrice(ratingPrice);
			vendorReview.setRatingService(ratingService);
			vendorReview.setReviewTime(new Date());
			vendorReviewRepository.save(vendorReview);

			return vendorReview;
		}
	}

	/* 找出單一店家有評分的留言 */
	public List<VendorReview> findReviewsWithRating(Integer vendorId) {
		List<VendorReview> list = vendorReviewRepository.findByVendorId(vendorId);
		List<VendorReview> filteredList = new ArrayList<>();
		for (VendorReview review : list) {
			if (review.getRatingEnvironment() != null && review.getRatingPrice() != null
					&& review.getRatingService() != null) {
				filteredList.add(review);
			}
		}
		return filteredList;
	}

	/* 計算單一店家評分之平均數 */
	public Vendor setAverageRating(Integer vendorId) {

		List<VendorReview> unfilteredList = vendorReviewRepository.findByVendorId(vendorId);
		List<VendorReview> reviewList = new ArrayList<>();
		for (VendorReview review : unfilteredList) {
			if (review.getRatingEnvironment() != null && review.getRatingPrice() != null
					&& review.getRatingService() != null) {
				reviewList.add(review);
			}
		}

		Integer sumRatingEnvironment = 0;
		Integer sumRatingPrice = 0;
		Integer sumRatingService = 0;
		Integer sumRatingAll = 0;

		for (VendorReview review : reviewList) {
			Integer ratingEnvironment = review.getRatingEnvironment();
			sumRatingEnvironment = sumRatingEnvironment + ratingEnvironment;

			Integer ratingPrice = review.getRatingPrice();
			sumRatingPrice = sumRatingPrice + ratingPrice;

			Integer ratingService = review.getRatingService();
			sumRatingService = sumRatingService + ratingService;

			Integer ratingAll = ratingEnvironment + ratingPrice + ratingService;
			sumRatingAll = sumRatingAll + ratingAll;
		}

		Integer count = reviewList.size();
		Float avgRatingEnvironment = Math.round((sumRatingEnvironment / (float) count) * 10) / 10.0f;
		Float avgRatingPrice = Math.round((sumRatingPrice / (float) count) * 10) / 10.0f;
		Float avgRatingService = Math.round((sumRatingService / (float) count) * 10) / 10.0f;
		Float avgRatingAll = Math.round((sumRatingAll / 3.0f / (float) count) * 10) / 10.0f;

		Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
		vendor.setAvgRatingEnvironment(avgRatingEnvironment);
		vendor.setAvgRatingPrice(avgRatingPrice);
		vendor.setAvgRatinService(avgRatingService);
		vendor.setTotalRating(avgRatingAll);
		vendorRepository.save(vendor);

		return vendor;
	}

	/* 尋找某會員是否有對某店家留下評論 */
	public boolean getReviewIsExisted(Integer memberId, Integer vendorId) {
		VendorReview review = vendorReviewRepository.findByMemberIdAndVendorId(memberId, vendorId);
		if (review != null) {
			return true;
		} else {
			return false;
		}
	}

	/* 查詢某個member所有評價之DTO */
	public List<VendorReviewDto> findReviewListByMemberId(Integer memberId) {
		List<VendorReview> reviewList = vendorReviewRepository.findByMemberId(memberId);

		List<VendorReviewDto> dtoList = reviewList.stream().map(review -> {
			Member member = memberRepository.findById(review.getMemberId()).get();
			return ConvertVendorReviewToDto(member, review);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 新增評論(完整版) */
	public VendorReview addNewReview(Integer memberId, Integer vendorId, String content, Integer ratingEnv,
			Integer ratingPrice, Integer ratingService, List<MultipartFile> reviewPhotos) throws IOException {
		VendorReview review = new VendorReview();
		review.setMemberId(memberId);
		review.setVendorId(vendorId);
		review.setReviewContent(content);
		review.setRatingEnvironment(ratingEnv);
		review.setRatingPrice(ratingPrice);
		review.setRatingService(ratingService);
		review.setReviewTime(new Date());
		vendorReviewRepository.save(review);

		Integer reviewId = review.getId();
		addReviewPhotos(reviewId, reviewPhotos);

		return review;
	}

	/* 修改評論(完整版) */
	public VendorReview modifyReview(Integer reviewId, String content, Integer ratingEnv, Integer ratingPrice,
			Integer ratingService, List<MultipartFile> reviewPhotos, List<Integer> deletePhotoIds) throws IOException {
		VendorReview review = vendorReviewRepository.findById(reviewId).orElse(null);
		review.setReviewContent(content);
		review.setRatingEnvironment(ratingEnv);
		review.setRatingPrice(ratingPrice);
		review.setRatingPrice(ratingPrice);
		review.setRatingService(ratingService);
		review.setReviewTime(new Date());

		addReviewPhotos(reviewId, reviewPhotos);

		if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
			for (Integer photoId : deletePhotoIds) {
				reviewPhotoRepository.deleteById(photoId);
			}
		}

		return review;
	}
}
