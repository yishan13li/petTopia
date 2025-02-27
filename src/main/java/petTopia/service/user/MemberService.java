package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import petTopia.model.user.MemberBean;
import petTopia.model.user.UsersBean;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UsersRepository;

@Service
@Transactional
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional
    public MemberBean createOrUpdateMember(MemberBean member) {
        try {
            validateMemberInput(member);
    
            // 確保用戶已存在於 `users` 表
            UsersBean user = usersRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
            member.setUser(user);  // 關聯 `UsersBean`
    
            // 查詢 `member` 是否已存在
            Optional<MemberBean> existingMemberOpt = memberRepository.findById(member.getId());
    
            if (existingMemberOpt.isPresent()) {
                // 更新已存在的 `Member`
                MemberBean existingMember = existingMemberOpt.get();
                existingMember.setName(member.getName());
                existingMember.setPhone(member.getPhone());
                existingMember.setBirthdate(member.getBirthdate());
                existingMember.setGender(member.getGender());
                existingMember.setAddress(member.getAddress());
                existingMember.setUpdatedDate(LocalDateTime.now());
    
                // 只在 `profilePhoto` 不為空時更新
                if (member.getProfilePhoto() != null) {
                    existingMember.setProfilePhoto(member.getProfilePhoto());
                }
    
                return memberRepository.save(existingMember);
            } else {
                // **如果 `member` 不存在，則新增**
                member.setStatus(false);
                member.setUpdatedDate(LocalDateTime.now());
                return memberRepository.save(member);
            }
        } catch (Exception e) {
            e.printStackTrace();  // 添加錯誤日誌
            throw new RuntimeException("保存會員資料失敗: " + e.getMessage());
        }
    }

    public MemberBean getMemberById(Integer userId) {
        return memberRepository.findById(userId).orElse(null);
    }

    public MemberBean updateMember(MemberBean member) {
        validateMemberInput(member);
        member.setUpdatedDate(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public MemberBean createMember(MemberBean member) {
        validateMemberInput(member);
        member.setStatus(false);  // 預設未認證
        member.setUpdatedDate(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public void updateProfilePhoto(Integer memberId, byte[] photoData) {
        MemberBean member = getMemberById(memberId);
        member.setProfilePhoto(photoData);
        member.setUpdatedDate(LocalDateTime.now());
        memberRepository.save(member);
    }

    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }

    private void validateMemberInput(MemberBean member) {
        // 確保必要的關聯存在
        if (member.getUser() == null || member.getUser().getId() == null) {
            throw new IllegalArgumentException("用戶關聯不能為空");
        }
        
        // 確保ID匹配
        if (!member.getId().equals(member.getUser().getId())) {
            throw new IllegalArgumentException("用戶ID不匹配");
        }
        
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能為空");
        }
    }

    public boolean verifyMember(Integer userId) {
        MemberBean member = getMemberById(userId);
        if (member != null) {
            member.setStatus(true);  // 設置為已驗證
            memberRepository.save(member);
            return true;
        }
        return false;
    }
} 