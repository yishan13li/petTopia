package petTopia.service.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Service
public class VendorTagService {
	@Autowired
	private VendorCertificationTagRepository vendorCertificationTagRepository;

	public List<VendorCertificationTag> findConfirmedTagByVendorId(Integer vendorId) {
		List<VendorCertificationTag> tagList = vendorCertificationTagRepository.findByVendorIdAndMeetsStandard(vendorId,
				true);
		return tagList;
	}
}
