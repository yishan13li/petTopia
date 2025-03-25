package petTopia.service.vendor_admin;

import java.util.Arrays;
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
import petTopia.repository.vendor.VendorReviewRepository;
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
	
	@Autowired	
	private VendorReviewRepository vendorReviewRepository;

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
		
		Optional<CertificationTag> tagOpt = certificationTagRepository.findById(tagId);
		if (tagOpt.isPresent()) {
		    boolean meetsStandard = checkMeetsStandard(vendorId, tagOpt.get());
		    certificationTag.setMeetsStandard(meetsStandard);
		} else {
		    throw new RuntimeException("標語不存在！");
		}
		
	    
	    
		vendorCertificationTagRepository.save(certificationTag);
	}

	

	// 获取所有认证语类型
    public List<CertificationTag> getAllCertificationTypes() {
        return certificationTagRepository.findAll(); // 这个方法需要根据您的实际数据库实现
    }
    
    private boolean checkMeetsStandard(Integer vendorId, CertificationTag tag) {
        String keywordsStr = tag.getKeywords(); // 例如："快速,反應,效率,即時,馬上"
        if (keywordsStr == null || keywordsStr.isBlank()) {
            return false; // 如果標語沒有關鍵字，直接不符合
        }

        List<String> keywords = Arrays.asList(keywordsStr.split(","));
        while (keywords.size() < 5) {
            keywords.add(""); // 避免 SQL 查詢錯誤
        }

        // 查詢符合關鍵字的評論數量
        int matchingReviewCount = vendorReviewRepository.countMatchingReviews(
                vendorId,
                keywords.get(0), keywords.get(1), keywords.get(2),
                keywords.get(3), keywords.get(4)
        );

        return matchingReviewCount >= 5; // 例如至少 10 則符合評論才算合格
    }

}
