package petTopia.service.vendor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorCategoryRepository;

@Service
public class VendorCategoryService {
	@Autowired
	private VendorCategoryRepository vendorCategoryRepository;

	public List<VendorCategory> findAllVendorCategory() {
		List<VendorCategory> categoryList = vendorCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		List<VendorCategory> filterdCategoryList = new ArrayList<>();

		// 過濾類別內無店家之類別
		for (VendorCategory category : categoryList) {
			if (!category.getVendors().isEmpty()) {
				filterdCategoryList.add(category);
			}
		}

		return filterdCategoryList;
	}

	public List<VendorCategory> findAllIncludeNoVendor() {
		List<VendorCategory> categoryList = vendorCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		return categoryList;
	}
}
