package petTopia.service.vendor_admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorRepository;

@Service
public class ManageVendorService {

	@Autowired
	private VendorRepository vendorRepository;

	/** 📌 取得所有店家（可分類） */
	public List<Vendor> getAllVendors(Integer categoryId, Boolean status) {
	    if (categoryId == null && (status == null)) {
	        // 如果类别和状态都没有指定，返回所有商家
	        return vendorRepository.findAll();
	    } else if (categoryId != null && (status == null)) {
	        // 如果只筛选类别
	        return vendorRepository.findByVendorCategoryId(categoryId);
	    } else if (categoryId == null && status != null) {
	        // 如果只筛选状态
	        return vendorRepository.findByStatus(status);
	    } else {
	        // 如果两个筛选条件都有，则同时筛选
	        return vendorRepository.findByVendorCategoryIdAndStatus(categoryId, status);
	    }
	}



	/** 📌 更新單個店家狀態 */
	@Transactional
	public boolean updateVendorStatus(Integer id, boolean status) {
		return vendorRepository.findById(id).map(vendor -> {
			vendor.setStatus(status);
			vendorRepository.save(vendor);
			return true;
		}).orElse(false);
	}

	/** 📌 批量更新店家狀態 */
	@Transactional
	public boolean bulkUpdateVendorStatus(List<Integer> vendorIds, boolean status) {
		if (vendorIds == null || vendorIds.isEmpty()) {
			vendorRepository.updateAllVendorStatus(status);
		} else {
			vendorRepository.updateVendorStatusByIds(vendorIds, status);
		}
		return true;
	}
}