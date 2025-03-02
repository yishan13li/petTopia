package petTopia.service.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorLike;
import petTopia.repository.vendor.VendorLikeRepository;

@Service
public class VendorLikeService {

	@Autowired
	private VendorLikeRepository vendorLikeRepository;

	/* 新增或取消店家收藏 */
	public void addOrCancelVendorLike(Integer memberId, Integer vendorId) {
		VendorLike vendorLike = vendorLikeRepository.findByMemberIdAndVendorId(memberId, vendorId);

		if (vendorLike == null) {
			VendorLike newVendorLike = new VendorLike();
			newVendorLike.setMemberId(memberId);
			newVendorLike.setVendorId(vendorId);
			vendorLikeRepository.save(newVendorLike);
		} else {
			Integer vendorLikeId = vendorLike.getId();
			vendorLikeRepository.deleteById(vendorLikeId);
		}

	}
}
