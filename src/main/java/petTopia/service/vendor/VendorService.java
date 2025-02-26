package petTopia.service.vendor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.Vendor;
import petTopia.repository.vendor.VendorRepository;

@Service
public class VendorService {

	@Autowired
	private VendorRepository vendorRepository;

	public List<Vendor> findAllVendor() {
		List<Vendor> vendorList = vendorRepository.findAll();
		for (Vendor v : vendorList) {
			byte[] logoImg = v.getLogoImg();
			if (logoImg != null) {
				String mimeType = getMimeType(logoImg);
				String base64="data:%s;base64,".formatted(mimeType)
	    				+ Base64.getEncoder().encodeToString(logoImg);
				v.setLogoImgBase64(base64);
			}
		}
		return vendorList;
	}

	public Vendor findVendorById(Integer id) {
		Optional<Vendor> optional = vendorRepository.findById(id);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}
	
    /* 讀取檔案之MimeType */
	public static String getMimeType(byte[] imageBytes) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
			String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
			
			inputStream.close();
			
			return mimeType;

		} catch (IOException e) {
			return "image/jpg";
		}
	}
}
