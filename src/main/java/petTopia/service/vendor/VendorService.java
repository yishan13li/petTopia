package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorRepository;
import petTopia.util.ImageConverter;

@Service
public class VendorService {

	@Autowired
	private VendorRepository vendorRepository;

	/* 所有店家清單 */
	public List<Vendor> findAllVendor() {
		List<Vendor> vendorList = vendorRepository.findAll();

		for (Vendor v : vendorList) {
			byte[] logoImg = v.getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				v.setLogoImgBase64(base64);
			}
		}

		return vendorList;
	}

	/* 單一店家 */
	public Vendor findVendorById(Integer vendorId) {
		Optional<Vendor> optional = vendorRepository.findById(vendorId);

		if (optional.isPresent()) {
			Vendor vendor = optional.get();

			byte[] logoImg = vendor.getLogoImg();
			String mimeType = ImageConverter.getMimeType(logoImg);

			if (logoImg == null) {
				vendor.setLogoImgBase64(null);
			} else {
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				vendor.setLogoImgBase64(base64);
			}

			return vendor;
		}

		return null;
	}

	/* 排除特定店家之清單 */
	public List<Vendor> findAllVendorExceptOne(Integer vendorId) {
		List<Vendor> vendorList = vendorRepository.findAll();
		Vendor vendorToRemove = vendorRepository.findById(vendorId).orElse(null);

		if (vendorToRemove != null) {
			vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId()));
		}

		for (Vendor v : vendorList) {
			byte[] logoImg = v.getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				v.setLogoImgBase64(base64);
			}
		}

		return vendorList;
	}

	/* 藉類別來找店家 */
	public List<Vendor> findVendorByCategoryId(Integer categoryId) {
		List<Vendor> vendorList = vendorRepository.findByVendorCategoryId(categoryId);

		for (Vendor v : vendorList) {
			byte[] logoImg = v.getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				v.setLogoImgBase64(base64);
			}
		}

		return vendorList;
	}

	/* 藉類別來找店家 */
	public List<Vendor> findVendorByCategoryIdExceptOne(Integer categoryId, Integer vendorId) {
		List<Vendor> vendorList = vendorRepository.findByVendorCategoryId(categoryId);
		Vendor vendorToRemove = vendorRepository.findById(vendorId).orElse(null);
		
		if (vendorToRemove != null) {
			vendorList.removeIf(v -> v.getId().equals(vendorToRemove.getId())); // 刪除ID與vendorToRemove相同ID相同之店家
		}

		for (Vendor v : vendorList) {
			byte[] logoImg = v.getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				v.setLogoImgBase64(base64);
			}
		}

		return vendorList;
	}

	/* 模糊搜尋店家 */
	public List<Vendor> findVendorByNameOrDescription(String keyword) {
		List<Vendor> list1 = vendorRepository.findByNameContaining(keyword);
		List<Vendor> list2 = vendorRepository.findByDescriptionContaining(keyword);

		/* 使用set來過濾重複之資料 */
		Set<Integer> set = new HashSet<>();
		List<Vendor> finalList = new ArrayList<>();

		for (Vendor v : list1) {
			if (set.add(v.getId())) { // set.add(id)會回傳布林值，如果id沒出現過則加入
				finalList.add(v);
			}
		}

		for (Vendor v : list2) {
			if (set.add(v.getId())) { 
				finalList.add(v);
			}
		}

		for (Vendor vendor : finalList) {
			byte[] imageByte = vendor.getLogoImg();
			if (imageByte != null) {
				String mimeType = ImageConverter.getMimeType(imageByte);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(imageByte);
				vendor.setLogoImgBase64(base64);
			}
		}

		return finalList;
	}

}
