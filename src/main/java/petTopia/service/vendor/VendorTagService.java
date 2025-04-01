package petTopia.service.vendor;

import java.util.ArrayList;
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
		List<VendorCertificationTag> tagList = vendorCertificationTagRepository.findByVendorId(vendorId);

		List<VendorCertificationTag> confirmedList = new ArrayList<>();
		for (VendorCertificationTag tag : tagList) {
			if ("已認證".equals(tag.getCertification().getCertificationStatus())) {
				confirmedList.add(tag);
			}
		}

		return confirmedList;
	}
}
