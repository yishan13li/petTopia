package petTopia.service.vendor;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.vendor.ReviewPhoto;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.util.ImageConverter;

@Service
public class ReviewPhotoService {

	@Autowired
	private ReviewPhotoRepository reviewPhotoRepository;

	/* 設定Base64 */
	public List<ReviewPhoto> findPhotoListByReviewId(Integer reviewId){
		List<ReviewPhoto> list = reviewPhotoRepository.findByVendorReviewId(reviewId);
		
		for (ReviewPhoto photo : list) {
			byte[] photoByte = photo.getPhoto();
			if (photoByte != null) {
				String mimeType = ImageConverter.getMimeType(photoByte);
				String base64 = "data:%s;base64,".formatted(mimeType) + Base64.getEncoder().encodeToString(photoByte);
				photo.setPhotoBase64(base64);
			}
		}	
		return list;
	}
	
	/* 傳入多個reviewId，回傳reviewId對應之所有圖片的map */
//	public Map<Integer, List<ReviewPhoto>> findPhotosByReviewIds(List<Integer> reviewIdList) {
//
//		Map<Integer, List<ReviewPhoto>> photoMap = new HashMap<>();
//		for (Integer id : reviewIdList) {
//			List<ReviewPhoto> photoList = reviewPhotoRepository.findByVendorReviewId(id);
//			photoMap.put(id, photoList);
//		}
//
//		return photoMap;
//	}
	
}
