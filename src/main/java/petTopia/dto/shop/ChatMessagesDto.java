package petTopia.dto.shop;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagesDto {
	
	private Integer id;
	private Integer senderId;
	private Integer receiverId;
	private String content;
	private Boolean isRead;
	private Date sendTime;
	private List<byte[]> photos;
	
}
