package petTopia.service.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor.VendorLikeDto;
import petTopia.model.user.MemberBean;
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
	/* 藉VendorId找出有收藏此店家的會員 */
	public List<VendorLike> findMemberListByVendorId(Integer vendorId) {
		List<VendorLike> list = vendorLikeRepository.findByVendorId(vendorId);
		return list;
	}
	
	/* 將Member和VendorLike轉換成DTO */
	public VendorLikeDto ConvertVendorLikeToDto(MemberBean member, VendorLike like) {
		VendorLikeDto dto = new VendorLikeDto();
		dto.setId(like.getId());
		dto.setVendorId(like.getVendorId());
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());
		return dto;
	}
}
