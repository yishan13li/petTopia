package petTopia.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;
public class ImageConverter {
	
	/* 讀取檔案之MimeType */
	public static String getMimeType(byte[] imageBytes) {
	    if (imageBytes == null) {
	        return "image/jpg";
	    }
	    
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
			String mimeType = URLConnection.guessContentTypeFromStream(inputStream);

			inputStream.close();

			return mimeType;

		} catch (IOException e) {
			return "image/jpg";
		}
	}
	
	//單張照片轉base64
	public static String byteToBase64(byte[] bytes) {
	    if (bytes == null) return null;
	    return Base64.getEncoder().encodeToString(bytes);
	}
	
	//照片list轉base64
	public static List<String> byteListToBase64(List<byte[]> byteList) {
	    if (byteList == null) return List.of();
	    return byteList.stream()
	            .map(bytes -> byteToBase64(bytes))  // 使用 byteToBase64 方法處理每個圖片
	            .collect(Collectors.toList());
	}

}