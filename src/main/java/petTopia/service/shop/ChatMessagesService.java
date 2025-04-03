package petTopia.service.shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.ChatMessages;
import petTopia.model.user.User;
import petTopia.repository.shop.ChatMessagesRepository;
import petTopia.repository.user.UserRepository;

@Service
public class ChatMessagesService {

	@Autowired
    private ChatMessagesRepository chatMessagesRepository;
	@Autowired
	private UserRepository userRepository;
	
	// 管理員獲取所有聊天用戶ID
	public List<Integer> getChatUsers(Integer senderId){
		
		List<Integer> chatUserIds = chatMessagesRepository.findDistinctChatUserIds(senderId);
		
		return chatUserIds;
		
	}
	
	// 使用者獲取歷史聊天訊息
	public List<ChatMessages> getChatMessagesHistory(Integer senderId, Integer receiverId){
		
		List<ChatMessages> senderMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(senderId, receiverId);
		List<ChatMessages> receiverMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(receiverId, senderId);
		
		List<ChatMessages> allMessages = new ArrayList<>();
		allMessages.addAll(senderMessages);
		allMessages.addAll(receiverMessages);

		Collections.sort(allMessages, Comparator.comparing(ChatMessages::getId));
		return allMessages;
		
	}
		
	// 儲存輸入的聊天訊息 回傳該訊息
	public ChatMessages saveMessage(Integer senderId, Integer receiverId, String content, Date sendTime) {
		
		ChatMessages chatMessages = new ChatMessages();
		
		Optional<User> senderOpt = userRepository.findById(senderId);
		if (senderOpt.isPresent()) {
			chatMessages.setSender(senderOpt.get());
		}
		Optional<User> receiverOpt = userRepository.findById(receiverId);
		if (receiverOpt.isPresent()) {
			chatMessages.setReceiver(receiverOpt.get());
		}
		chatMessages.setContent(content);
		chatMessages.setSendTime(sendTime);
	    chatMessages.setIsRead(false);
		
	    ChatMessages save = chatMessagesRepository.save(chatMessages);
	    
	    return save != null ? save : null;
		
	}
	
}
