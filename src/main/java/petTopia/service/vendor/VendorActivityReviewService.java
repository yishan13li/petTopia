package petTopia.service.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.vendor.VendorActivityReviewRepository;

@Service
public class VendorActivityReviewService {
	
	@Autowired
	private VendorActivityReviewRepository vendorActivityReviewRepository;
	
	
	/* 尋找單一活動其所有的評分及留言 */
	public List<VendorActivityReview> findActivityReviewByVendorId(Integer activityId) {
		List<VendorActivityReview> activityReviewList = vendorActivityReviewRepository.findByActivityId(activityId);
		return activityReviewList;
	}
}
