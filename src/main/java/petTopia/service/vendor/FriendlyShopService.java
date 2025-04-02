package petTopia.service.vendor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.FriendlyShopRepository;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.util.ImageConverter;

@Service
public class FriendlyShopService {

	private static final String GOOGLE_MAPS_API_KEY = "AIzaSyAdtvNzj4RCUhcxxFuXDpvjXCglqPja6cI";

	private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

	@Autowired
	private FriendlyShopRepository friendlyShopRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorCategoryRepository vendorCategoryRepository;

	/* 透過 Google API 取得地址之經緯度 */
	public BigDecimal[] getLatLng(String address) {
		try {
			/* 1. 建立URL字串 */
			String urlString = UriComponentsBuilder.fromUriString(GOOGLE_MAPS_API_URL).queryParam("address", address)
					.queryParam("key", GOOGLE_MAPS_API_KEY).toUriString();

			/* 2. 建立 URI 物件並轉換為 URL */
			URI uri = new URI(urlString);
			URL url = uri.toURL();

			/* 3. 開啟 HTTP 連線並設置 User-Agent 標頭 */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			/* 4. 取得響應輸入流 */
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			/* 5. 讀取響應內容 */
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			/* 6. 取得響應的 JSON 字串 */
			String responseBody = response.toString();

			/* 7. 解析 JSON */
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode location = root.path("results").get(0).path("geometry").path("location");

			double lat = location.path("lat").asDouble();
			double lng = location.path("lng").asDouble();

			BigDecimal latitude = BigDecimal.valueOf(lat);
			BigDecimal longitude = BigDecimal.valueOf(lng);

			return new BigDecimal[] { latitude, longitude };

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public FriendlyShop findFirstByVendorId(Integer vendorId) {
		Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
		FriendlyShop friendlyShop = friendlyShopRepository.findFirstByVendor(vendor);

		if (friendlyShop == null) {
			FriendlyShop newFriendlyShop = new FriendlyShop();
			if (vendor.getName() != null) {
				newFriendlyShop.setName(vendor.getName());
			} else {
				newFriendlyShop.setName("( 無店家名稱 )");
			}

			newFriendlyShop.setVendor(vendor);
			newFriendlyShop.setVendorCategory(vendor.getVendorCategory());
			newFriendlyShop.setAddress(vendor.getAddress());

			BigDecimal[] latLng = getLatLng(vendor.getAddress());
			newFriendlyShop.setLatitude(latLng[0]);
			newFriendlyShop.setLongitude(latLng[1]);
			friendlyShopRepository.save(newFriendlyShop);

			byte[] logoImg = vendor.getLogoImg();
			if (logoImg != null) {
				String mimeType = ImageConverter.getMimeType(logoImg);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
				vendor.setLogoImgBase64(base64);
			}

			return friendlyShop;
		}

		if (vendor.getName() != null) {
			friendlyShop.setName(vendor.getName());
		} else {
			friendlyShop.setName("( 無店家名稱 )");
		}

		friendlyShop.setVendorCategory(vendor.getVendorCategory());
		friendlyShop.setAddress(vendor.getAddress());

		BigDecimal[] latLng = getLatLng(vendor.getAddress());
		friendlyShop.setLatitude(latLng[0]);
		friendlyShop.setLongitude(latLng[1]);
		friendlyShopRepository.save(friendlyShop);

		byte[] logoImg = vendor.getLogoImg();
		if (logoImg != null) {
			String mimeType = ImageConverter.getMimeType(logoImg);
			String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
			vendor.setLogoImgBase64(base64);
		}

		return friendlyShop;
	}

	public List<FriendlyShop> findAll() {
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findAll();

		for (FriendlyShop friendlyShop : friendlyShopList) {
			if (friendlyShop.getVendor() != null) {
				byte[] logoImg = friendlyShop.getVendor().getLogoImg();
				if (logoImg != null) {
					String mimeType = ImageConverter.getMimeType(logoImg);
					String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
					friendlyShop.getVendor().setLogoImgBase64(base64);
				}
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByKeyword(String keyword) {
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByNameContaining(keyword);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			if (friendlyShop.getVendor() != null) {
				byte[] logoImg = friendlyShop.getVendor().getLogoImg();
				if (logoImg != null) {
					String mimeType = ImageConverter.getMimeType(logoImg);
					String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
					friendlyShop.getVendor().setLogoImgBase64(base64);
				}
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByVendorId(Integer vendorId) {
		Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendor(vendor);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			if (friendlyShop.getVendor() != null) {
				byte[] logoImg = friendlyShop.getVendor().getLogoImg();
				if (logoImg != null) {
					String mimeType = ImageConverter.getMimeType(logoImg);
					String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
					friendlyShop.getVendor().setLogoImgBase64(base64);
				}
			}
		}
		return friendlyShopList;
	}

	public List<FriendlyShop> findByCategoryId(Integer categoryId) {
		VendorCategory category = vendorCategoryRepository.findById(categoryId).orElse(null);
		List<FriendlyShop> friendlyShopList = friendlyShopRepository.findByVendorCategory(category);

		for (FriendlyShop friendlyShop : friendlyShopList) {
			if (friendlyShop.getVendor() != null) {
				byte[] logoImg = friendlyShop.getVendor().getLogoImg();
				if (logoImg != null) {
					String mimeType = ImageConverter.getMimeType(logoImg);
					String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(logoImg);
					friendlyShop.getVendor().setLogoImgBase64(base64);
				}
			}
		}
		return friendlyShopList;
	}

	/* 新增友善店家 */
	public FriendlyShop addFriendlyShop(String name, Integer categoryId, String address) {
		FriendlyShop friendlyShop = new FriendlyShop();
		VendorCategory category = vendorCategoryRepository.findById(categoryId).orElse(null);
		BigDecimal[] latLng = getLatLng(address);

		friendlyShop.setName(name);
		friendlyShop.setVendorCategory(category);
		friendlyShop.setAddress(address);
		friendlyShop.setLatitude(latLng[0]);
		friendlyShop.setLongitude(latLng[1]);
		friendlyShopRepository.save(friendlyShop);

		return friendlyShop;
	}

	/* 修改友善店家 */
	public FriendlyShop modifyFriendlyShop(Integer id, String name, Integer categoryId, String address) {
		FriendlyShop friendlyShop = friendlyShopRepository.findById(id).orElse(null);
		VendorCategory category = vendorCategoryRepository.findById(categoryId).orElse(null);
		BigDecimal[] latLng = getLatLng(address);

		friendlyShop.setName(name);
		friendlyShop.setVendorCategory(category);
		friendlyShop.setAddress(address);
		friendlyShop.setLatitude(latLng[0]);
		friendlyShop.setLongitude(latLng[1]);
		friendlyShopRepository.save(friendlyShop);

		return friendlyShop;
	}

	/* 刪除友善店家 */
	public void deleteFriendlyShop(Integer id) {
		friendlyShopRepository.deleteById(id);
	}
	
	/* 獲取友善店家 */
	public FriendlyShop getFriendlyShop(Integer id) {
		FriendlyShop friendlyShop = friendlyShopRepository.findById(id).orElse(null);
		return friendlyShop;
	}
}
