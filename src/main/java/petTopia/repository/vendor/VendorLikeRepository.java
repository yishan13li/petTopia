package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorLike;

public interface VendorLikeRepository extends JpaRepository<VendorLike, Integer> {
	
	/* 尋找單一會員對單一店家是否有收藏 */
	public VendorLike findByMemberIdAndVendorId(Integer memberId,Integer vendorId);
}
