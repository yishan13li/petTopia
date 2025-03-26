package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorActivity;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import petTopia.util.ImageConverter;

@Service
public class VendorActivityImagesService {
	@Autowired
	private VendorActivityRepository vendorActivityRepository;
	
	@Autowired
	private VendorActivityImagesRepository vendorActivityImagesRepository;
	
	/* 設定Base64 */
	public List<VendorActivityImages> findImageListByActivityId(Integer activityId) {
		VendorActivity activity = vendorActivityRepository.findById(activityId).orElse(null);
		List<VendorActivityImages> imageList = vendorActivityImagesRepository.findByVendorActivity(activity);
		for (VendorActivityImages images : imageList) {
			byte[] imageByte = images.getImage();
			if (imageByte != null) {
				String mimeType = ImageConverter.getMimeType(imageByte);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(imageByte);
				images.setImageBase64(base64);
			}
		}
		return imageList;
	}
}
