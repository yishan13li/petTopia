package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.FriendlyShopRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.util.ImageConverter;

@Service
public class FriendlyShopService {

	@Autowired
	private FriendlyShopRepository friendlyShopRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorCategoryRepository vendorCategoryRepository;

	public FriendlyShop findFirstByVendorId(Integer vendorId) {
		FriendlyShop friendlyShop = friendlyShopRepository.findFirstByVendorId(vendorId);

		byte[] logoImg = friendlyShop.getVendor().getLogoImg();
		if (logoImg != null) {
			String mimeType = ImageConverter.getMimeType(logoImg);
			String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
			friendlyShop.getVendor().setLogoImgBase64(base64);
		}

		return friendlyShop;
	}

	public List<FriendlyShop> findAll() {
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findAll();

		for (FriendlyShop friendlyShop : friendlyShopList) {
			byte[] logoImg = friendlyShop.getVendor().getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				friendlyShop.getVendor().setLogoImgBase64(base64);
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByKeyword(String keyword) {
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByNameContaining(keyword);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			byte[] logoImg = friendlyShop.getVendor().getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				friendlyShop.getVendor().setLogoImgBase64(base64);
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByVendorId(Integer vendorId) {
		Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendor(vendor);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			byte[] logoImg = friendlyShop.getVendor().getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				friendlyShop.getVendor().setLogoImgBase64(base64);
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByVendorCategoryId(Integer categoryId) {
		VendorCategory category = vendorCategoryRepository.findById(categoryId).orElse(null);
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendorCategory(category);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			byte[] logoImg = friendlyShop.getVendor().getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				friendlyShop.getVendor().setLogoImgBase64(base64);
			}
		}
		return friendlyShopList;
	}
}
