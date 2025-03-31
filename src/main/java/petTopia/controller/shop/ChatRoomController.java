package petTopia.controller.shop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ChatMessagesDto;
import petTopia.model.shop.ChatMessages;
import petTopia.model.user.Member;
import petTopia.service.shop.ChatMessagesService;
import petTopia.service.user.MemberService;

@RequestMapping("/chatRoom")
@RestController
public class ChatRoomController {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessagesService chatMessagesService;
	private final MemberService memberService;

    public ChatRoomController(
    		ChatMessagesService chatMessagesService, 
    		SimpMessagingTemplate messagingTemplate, 
    		MemberService memberService) {
    	this.messagingTemplate = messagingTemplate;
    	this.chatMessagesService = chatMessagesService;
    	this.memberService = memberService;
    }
    
	@MessageMapping("/send")  // 客戶端發送至 `/app/send`
    public ResponseEntity<?> sendMessage(@Payload Map<String, Object> message) {
		
		ChatMessagesDto chatMessagesDto = new ChatMessagesDto();
		
        Integer senderId = (Integer) message.get("senderId");
        Integer receiverId = (Integer) message.get("receiverId");
        String content = (String) message.get("content");
        String sendTime = (String) message.get("sendTime");
        Instant instant = Instant.parse(sendTime);
        Date parsedDate = Date.from(instant);
        
        Integer saveMessageId = chatMessagesService.saveMessage(senderId, receiverId, content, parsedDate);
        
        if (saveMessageId != 0) {
        	chatMessagesDto.setId(saveMessageId);
        	chatMessagesDto.setSenderId(senderId);
        	chatMessagesDto.setReceiverId(receiverId);
        	chatMessagesDto.setIsRead(false);
        	chatMessagesDto.setContent(content);
        	chatMessagesDto.setSendTime(parsedDate);
        	
        	// **發送給發送者**
            messagingTemplate.convertAndSend("/topic/messages/" + senderId, chatMessagesDto);
        	// **發送給接收者**
            messagingTemplate.convertAndSend("/topic/messages/" + receiverId, chatMessagesDto);
            
        }
        else {
        	chatMessagesDto = null;
        }
		
        return new ResponseEntity<ChatMessagesDto>(chatMessagesDto, HttpStatus.OK);
		
    }

	// 後台聊天室 => 獲取所有聊天用戶
	@GetMapping("/api/getChatUsers")
	public ResponseEntity<?> getChatUsers(@RequestParam Integer senderId) {
		Map<String, Object> responseBody = new HashMap<>();
		List<Map<String, Object>> chatUsers = new ArrayList<>();
		
		List<Integer> chatUserIds = chatMessagesService.getChatUsers(senderId);
		if (chatUserIds != null && chatUserIds.size() != 0) {
			List<Member> members = memberService.findAllById(chatUserIds);
			if (members != null && members.size() != 0) {
				for (Member member : members) {
					Map<String, Object> memberMap = new HashMap<>();
					memberMap.put("id", member.getId());
					memberMap.put("name", member.getName());
					chatUsers.add(memberMap);
				}
				
			}
			responseBody.put("chatUsers", chatUsers);
		}
		else
			responseBody.put("chatUsers", null);
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	// 前台&後台聊天室 => 獲取歷史訊息
	@GetMapping("/api/getChatMessagesHistory")
	public ResponseEntity<?> getChatMessagesHistory(
			@RequestParam Integer senderId, 
			@RequestParam Integer receiverId
			) {
		Map<String, Object> responseBody = new HashMap<>();
		List<ChatMessagesDto> chatMessagesDtoList = new ArrayList<>();
		
		List<ChatMessages> chatMessagesHistory = chatMessagesService.getChatMessagesHistory(senderId, receiverId);
		if (chatMessagesHistory != null) {
			for (ChatMessages chatMessages : chatMessagesHistory) {
				ChatMessagesDto chatMessagesDto = new ChatMessagesDto(
						chatMessages.getId(), 
						chatMessages.getSender().getId(), 
						chatMessages.getReceiver().getId(), 
						chatMessages.getContent(), 
						chatMessages.getIsRead(), 
						chatMessages.getSendTime()
						);
				chatMessagesDtoList.add(chatMessagesDto);
				
			}
			responseBody.put("chatMessagesHistory", chatMessagesDtoList);
			
		}
		else {
			responseBody.put("chatMessagesHistory", null);
			
		}
		
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
}
