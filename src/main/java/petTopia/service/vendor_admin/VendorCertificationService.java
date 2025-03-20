package petTopia.service.vendor_admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.CertificationTag;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCertification;
import petTopia.model.vendor.VendorCertificationTag;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor_admin.CertificationTagRepository;
import petTopia.repository.vendor_admin.VendorCertificationRepository;
import petTopia.repository.vendor_admin.VendorCertificationTagRepository;

@Service
public class VendorCertificationService {

	@Autowired
	private VendorCertificationRepository vendorCertificationRepository;

	@Autowired
	private VendorCertificationTagRepository vendorCertificationTagRepository;

	@Autowired
	private CertificationTagRepository certificationTagRepository;

	@Autowired
	private VendorRepository vendorRepository;

	public void createVendorCertificationWithTag(Integer vendorId, Integer tagId) {
		// 1. 获取 Vendor 和 CertificationTag 实体
		Optional<Vendor> vendor = vendorRepository.findById(vendorId);
		Optional<CertificationTag> tag = certificationTagRepository.findById(tagId);

		// 2. 创建 VendorCertification 实体并保存
		VendorCertification certification = new VendorCertification();
		certification.setVendor(vendor.get());
		certification.setCertificationStatus("申請中"); // 默认为 "申請中"
		certification.setRequestDate(new Date()); // 设置申请日期
		vendorCertificationRepository.save(certification);

		// 3. 创建 VendorCertificationTag 实体并保存
		VendorCertificationTag certificationTag = new VendorCertificationTag();
		certificationTag.setCertification(certification);
		certificationTag.setTag(tag.get());
		certificationTag.setMeetsStandard(false); // 默认不符合标准
		certificationTag.setVendor(vendor.get());
		vendorCertificationTagRepository.save(certificationTag);
	}

	// 获取所有认证语类型
    public List<CertificationTag> getAllCertificationTypes() {
        return certificationTagRepository.findAll(); // 这个方法需要根据您的实际数据库实现
    }

}
