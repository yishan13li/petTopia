package petTopia.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;

public class ImageConverter {
	
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
