package petTopia.service.vendor_admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.vendor_admin.CertificationDTO;
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

        return matchingReviewCount >= 3; // 例如至少 10 則符合評論才算合格
    }
    
    public List<CertificationDTO> getAllCertificationsWithTags() {
        List<CertificationDTO> certificationsWithTags = new ArrayList<>();

        // 获取所有认证申请
        List<VendorCertification> certifications = vendorCertificationRepository.findAll();
        
        // 遍历所有认证申请
        for (VendorCertification certification : certifications) {
        	CertificationDTO certificationDTO = new CertificationDTO();
            certificationDTO.setVendor(certification.getVendor());
            certificationDTO.setCertificationId(certification.getId());
            certificationDTO.setCertificationStatus(certification.getCertificationStatus());
            certificationDTO.setReason(certification.getReason());
            certificationDTO.setRequestDate(certification.getRequestDate());
            certificationDTO.setApprovedDate(certification.getApprovedDate());

            // 获取认证标签
            List<VendorCertificationTag> certificationTags = vendorCertificationTagRepository.findByCertification(certification);

            // 将认证标签封装到DTO中
            List<CertificationDTO.CertificationTagDTO> tagDTOList = new ArrayList<>();
            for (VendorCertificationTag certificationTag : certificationTags) {
            	CertificationDTO.CertificationTagDTO tagDTO = new CertificationDTO.CertificationTagDTO();
                tagDTO.setTagName(certificationTag.getTag().getTagName());
                tagDTO.setMeetsStandard(certificationTag.isMeetsStandard());
                tagDTOList.add(tagDTO);
            }
            certificationDTO.setCertificationTags(tagDTOList);
            
            certificationsWithTags.add(certificationDTO);
        }
        return certificationsWithTags;
    }

 // 更新认证状态
    public VendorCertification updateCertificationStatus(Integer certificationId, String status,String reason) {
    	VendorCertification certification = vendorCertificationRepository.findById(certificationId)
                .orElseThrow(() -> new RuntimeException("Certification not found with id: " + certificationId));

        // 更新状态
        certification.setCertificationStatus(status);
        certification.setReason(reason);
        certification.setApprovedDate(new Date());
        return vendorCertificationRepository.save(certification); // 保存更新后的认证记录
    }
}
