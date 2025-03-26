package petTopia.service.vendor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor.VendorLikeDto;
import petTopia.model.user.Member;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorLike;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.VendorLikeRepository;
import petTopia.repository.vendor.VendorRepository;

@Service
public class VendorLikeService {

	@Autowired
	private VendorLikeRepository vendorLikeRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private MemberRepository memberRepository;

	/* 取得店家收藏狀態 */
	public Boolean getActivityLikeStatus(Integer memberId, Integer vendorId) {
		VendorLike vendorLike = vendorLikeRepository.findByMemberIdAndVendorId(memberId, vendorId);

		if (vendorLike != null) {
			return true;
		} else {
			return false;
		}
	}

	/* 新增或取消店家收藏 */
	public boolean toggleVendorLike(Integer memberId, Integer vendorId) {
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

	/* 將Member和VendorLike轉換成DTO */
	public VendorLikeDto ConvertVendorLikeToDto(Member member, VendorLike like) {
		VendorLikeDto dto = new VendorLikeDto();
		dto.setId(like.getId());
		dto.setVendorId(like.getVendorId());
		
		Vendor vendor = vendorRepository.findById(like.getVendorId()).orElse(null);
		dto.setVendorName(vendor.getName());
		dto.setVendorDescription(vendor.getDescription());
		dto.setVendorCategory(vendor.getVendorCategory().getName());
		
		dto.setMemberId(member.getId());
		dto.setName(member.getName());
		dto.setGender(member.getGender());
		dto.setProfilePhoto(member.getProfilePhoto());
		return dto;
	}

	/* 查詢某個vendorId所有收藏之DTO */
	public List<VendorLikeDto> findMemberListByVendorId(Integer vendorId) {
		List<VendorLike> likeList = vendorLikeRepository.findByVendorId(vendorId);

		List<VendorLikeDto> dtoList = likeList.stream().map(like -> {
			Member member = memberRepository.findById(like.getMemberId()).get();
			return ConvertVendorLikeToDto(member, like);
		}).collect(Collectors.toList());

		return dtoList;
	}

	/* 查詢某個vendorId所有收藏之DTO */
	public List<VendorLikeDto> findListByMemberId(Integer memberId) {
		List<VendorLike> likeList = vendorLikeRepository.findByMemberId(memberId);

		List<VendorLikeDto> dtoList = likeList.stream().map(like -> {
			Member member = memberRepository.findById(like.getMemberId()).get();
			return ConvertVendorLikeToDto(member, like);
		}).collect(Collectors.toList());

		return dtoList;
	}
	
	/* 藉ID刪除收藏 */
	public boolean deleteByLikeId(Integer likeId) {
		if (vendorLikeRepository.existsById(likeId)) {
			vendorLikeRepository.deleteById(likeId);
			return true;
		}
		return false;
	}
}
