package petTopia.service.vendor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityReview;
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
	
	
	/* 尋找單一活動其所有的評分及留言 */
	public List<VendorActivityReview> findActivityReviewByVendorId(Integer activityId) {
		List<VendorActivityReview> activityReviewList = vendorActivityReviewRepository.findByVendorActivityId(activityId);
		return activityReviewList;
	}
	
	/* 新增或修改活動評論 */
	public void addOrModifyActivityReview(Integer memberId, Integer activityId, String content) {
		VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId);
		
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
		VendorActivityReview review = vendorActivityReviewRepository.findByMemberIdAndVendorActivityId(memberId, activityId);
		Integer reviewId = review.getId();
		vendorActivityReviewRepository.deleteById(reviewId);
	}
}
