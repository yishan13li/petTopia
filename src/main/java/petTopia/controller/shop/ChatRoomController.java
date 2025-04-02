package petTopia.controller.shop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ChatMessagesDto;
import petTopia.model.shop.ChatMessages;
import petTopia.model.shop.ChatPhoto;
import petTopia.model.user.Member;
import petTopia.service.shop.ChatMessagesService;
import petTopia.service.shop.ChatPhotoService;
import petTopia.service.user.MemberService;

@RequestMapping("/chatRoom")
@RestController
public class ChatRoomController {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessagesService chatMessagesService;
	private final ChatPhotoService chatPhotoService;
	private final MemberService memberService;
	
	private final String PATH = "src/main/resources/static";

    public ChatRoomController(
    		SimpMessagingTemplate messagingTemplate, 
    		ChatMessagesService chatMessagesService,
    		ChatPhotoService chatPhotoService, 
    		MemberService memberService) {
    	this.messagingTemplate = messagingTemplate;
    	this.chatMessagesService = chatMessagesService;
    	this.chatPhotoService = chatPhotoService;
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
        List<String> urlPhotos = (List<String>) message.get("photos"); // 圖片url列表
        List<byte[]> bytePhotos = new ArrayList<>();
        
        // 儲存訊息
        ChatMessages saveMessage = chatMessagesService.saveMessage(senderId, receiverId, content, parsedDate);
        Integer saveMessageId = saveMessage.getId();
        // 儲存圖片
        if (urlPhotos != null && !urlPhotos.isEmpty()) {
            for (String photo : urlPhotos) {
                chatPhotoService.savePhoto(saveMessage, photo);
                // 本地位置轉byte[]丟回前端
                try {
					byte[] bytePhoto = convertUrlToByteArray(PATH + photo);
					bytePhotos.add(bytePhoto);
				} catch (IOException e) {
					e.printStackTrace();
				}
                
            }
        }
        
        if (saveMessageId != 0) {
        	chatMessagesDto.setId(saveMessageId);
        	chatMessagesDto.setSenderId(senderId);
        	chatMessagesDto.setReceiverId(receiverId);
        	chatMessagesDto.setIsRead(false);
        	chatMessagesDto.setContent(content);
        	chatMessagesDto.setSendTime(parsedDate);
        	chatMessagesDto.setPhotos(bytePhotos);
        	
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
				List<byte[]> bytePhotos = new ArrayList<>();
				
				// 獲取圖片url
				List<String> urlPhotos = chatPhotoService.getChatPhotos(chatMessages.getId());
				for (String photo : urlPhotos) {
	                // 本地位置轉byte[]丟回前端
	                try {
						byte[] bytePhoto = convertUrlToByteArray(PATH + photo);
						bytePhotos.add(bytePhoto);
					} catch (IOException e) {
						e.printStackTrace();
					}
	                
	            }
				
				// ChatMessagesDto
				ChatMessagesDto chatMessagesDto = new ChatMessagesDto(
						chatMessages.getId(), 
						chatMessages.getSender().getId(), 
						chatMessages.getReceiver().getId(), 
						chatMessages.getContent(), 
						chatMessages.getIsRead(), 
						chatMessages.getSendTime(), 
						bytePhotos
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
	
	// 上傳圖片
	@PostMapping("/api/uploadPhoto")
	public ResponseEntity<?> uploadPhoto(@RequestBody Map<String, Object> photo) {
	    try {
	    	String userId = (String) photo.get("userId");
	    	List<String> base64Images = (List<String>) photo.get("image");
	    	List<Map<String, String>> uploadedImages = new ArrayList<>();
	    	
	        // 檢查資料夾是否存在
	        Path uploadDir = Paths.get("src/main/resources/static/chatRoomPhoto");
	        if (!Files.exists(uploadDir)) {
	            Files.createDirectories(uploadDir);
	        }
	        
	        for (String base64String : base64Images) {
	            // 解碼 Base64 -> byte[]
	            byte[] imageBytes = Base64.getDecoder().decode(base64String.split(",")[1]);

	            // 產生唯一檔名
	            LocalDateTime now = LocalDateTime.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	            String formattedDate = now.format(formatter);
	            String fileName = userId + "_" + formattedDate + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
	            Path filePath = uploadDir.resolve(fileName);

	            // 儲存圖片
	            Files.write(filePath, imageBytes);

	            // 準備回傳 URL
	            Map<String, String> response = new HashMap<>();
	            response.put("url", "/chatRoomPhoto/" + fileName);
	            uploadedImages.add(response);

	        }
	        
	        return ResponseEntity.ok(uploadedImages);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	// 本地位置轉byte[]
    public byte[] convertUrlToByteArray(String filePath) throws IOException {
    	Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
}
