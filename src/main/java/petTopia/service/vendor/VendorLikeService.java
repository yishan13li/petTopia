package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorLike;
import petTopia.repository.vendor.VendorLikeRepository;

@Service
public class VendorLikeService {

	@Autowired
	private VendorLikeRepository vendorLikeRepository;

	/* 新增或取消店家收藏 */
	public boolean addOrCancelVendorLike(Integer memberId, Integer vendorId) {
		VendorLike vendorLike = vendorLikeRepository.findByMemberIdAndVendorId(memberId, vendorId);

		if (vendorLike == null) {
			VendorLike newVendorLike = new VendorLike();
			newVendorLike.setMemberId(memberId);
			newVendorLike.setVendorId(vendorId);
			vendorLikeRepository.save(newVendorLike);
			return true;
		} else {
			Integer vendorLikeId = vendorLike.getId();
			vendorLikeRepository.deleteById(vendorLikeId);
			return false;
		}

	}
	
	public List<VendorLike> findMemberListByVendorId(Integer vendorId) {
		List<VendorLike> list = vendorLikeRepository.findByVendorId(vendorId);
//		List<String> memberIdList = new ArrayList<>();
		for(VendorLike like:list) {
			System.out.println(like.getId());
		}
		return list;
	}
}
