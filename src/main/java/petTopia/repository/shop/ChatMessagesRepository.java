package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.shop.ChatMessages;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Integer>{

	List<ChatMessages> findBySenderIdAndReceiverIdOrderByIdAsc(Integer senderId, Integer receiverId);
	
	@Query("""
		    SELECT DISTINCT CASE 
		        WHEN c.sender.id != :senderId THEN c.sender.id 
		        ELSE c.receiver.id 
		    END 
		    FROM ChatMessages c 
		    WHERE c.sender.id = :senderId OR c.receiver.id = :senderId
		    ORDER BY 1 ASC
			""")
	List<Integer> findDistinctChatUserIds(@Param("senderId") Integer senderId);
	
}
