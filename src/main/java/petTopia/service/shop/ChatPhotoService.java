package petTopia.service.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.ChatMessages;
import petTopia.model.shop.ChatPhoto;
import petTopia.repository.shop.ChatMessagesRepository;
import petTopia.repository.shop.ChatPhotoRepository;

@Service
public class ChatPhotoService {

	@Autowired
    private ChatPhotoRepository chatPhotoRepository;
	
	// 獲取該訊息的所有圖片
	public List<String> getChatPhotos(Integer chatMessagesId){
		
		List<String> photos = new ArrayList<>();
		List<ChatPhoto> chatPhotos = chatPhotoRepository.findByChatMessagesId(chatMessagesId);
		if (chatPhotos != null || chatPhotos.size() != 0) {
			for (ChatPhoto chatPhoto : chatPhotos) {
				photos.add(chatPhoto.getPhoto());
			}
		}
		else {
			photos = null;
		}
		
		return photos;
		
	}
		
	// 儲存聊天訊息的圖片
	public Boolean savePhoto(ChatMessages chatMessages, String photo) {
		
		ChatPhoto chatPhoto = new ChatPhoto();
		
		chatPhoto.setChatMessages(chatMessages);
		chatPhoto.setPhoto(photo);
		
		ChatPhoto save = chatPhotoRepository.save(chatPhoto);
	    
		if (save != null)
			return true;
		
		return false;
		
	}
	
}
