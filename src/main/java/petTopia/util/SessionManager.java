package petTopia.util;

import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;

@Component
public class SessionManager {
    
    public void updateProfilePhoto(HttpSession session, byte[] photoData) {
        if (photoData != null) {
            String photoBase64 = Base64.getEncoder().encodeToString(photoData);
            session.setAttribute("memberProfilePhotoBase64", photoBase64);
            session.setAttribute("photoVersion", System.currentTimeMillis());
        } else {
            session.removeAttribute("memberProfilePhotoBase64");
            session.removeAttribute("photoVersion");
        }
    }
    
    public void updateMemberInfo(HttpSession session, String memberName, String email) {
        if (memberName != null) {
            session.setAttribute("memberName", memberName);
        }
        if (email != null) {
            session.setAttribute("userEmail", email);
        }
    }
    
    public void clearSession(HttpSession session) {
        session.removeAttribute("memberProfilePhotoBase64");
        session.removeAttribute("photoVersion");
        session.removeAttribute("memberName");
        session.removeAttribute("userEmail");
        session.removeAttribute("userId");
    }
} 