package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;

public interface FriendlyShopRepository extends JpaRepository<FriendlyShop, Integer> {

	public FriendlyShop findFirstByVendor(Vendor vendor);

	public List<FriendlyShop> findByNameContaining(String name);

	public List<FriendlyShop> findByVendor(Vendor vendor);

	public List<FriendlyShop> findByVendorCategory(VendorCategory vendorCategory);

	List<FriendlyShop> findByVendorCategoryIsNull();
}
