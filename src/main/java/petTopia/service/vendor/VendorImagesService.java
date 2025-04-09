package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.util.ImageConverter;

@Service
public class VendorImagesService {

	@Autowired
	private VendorImagesRepository vendorImagesRepository;

	/* 設定Base64 */
	public List<VendorImages> findImageListByVendorId(Integer vendorId) {
		List<VendorImages> imageList = vendorImagesRepository.findByVendorId(vendorId);
		for (VendorImages images : imageList) {
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
