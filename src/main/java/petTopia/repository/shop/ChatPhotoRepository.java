package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.ChatPhoto;

public interface ChatPhotoRepository extends JpaRepository<ChatPhoto, Integer>{

	List<ChatPhoto> findByChatMessagesId(Integer chatMessagesId);
	
}
