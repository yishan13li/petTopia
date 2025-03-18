package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.VendorImages;

public interface VendorImagesRepository extends JpaRepository<VendorImages, Integer> {
	
	/* 尋找單一店家其所有圖片 */
	public List<VendorImages> findByVendorId(Integer vendorId);
}
