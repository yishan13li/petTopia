package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UserRepository;

@Service
@Transactional
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository usersRepository;

    @Transactional
    public Member createOrUpdateMember(Member member) {
        try {
            validateMemberInput(member);
    
            // 確保用戶已存在於 `users` 表
            User user = usersRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
            member.setUser(user);  // 關聯 `Users`
    
            // 查詢 `member` 是否已存在
            Optional<Member> existingMemberOpt = memberRepository.findById(member.getId());
    
            Member savedMember;
            if (existingMemberOpt.isPresent()) {
                // 更新已存在的 `Member`
                Member existingMember = existingMemberOpt.get();
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
    
                savedMember = memberRepository.save(existingMember);
            } else {
                // 如果 `member` 不存在，則新增
                member.setStatus(false);
                member.setUpdatedDate(LocalDateTime.now());
                savedMember = memberRepository.save(member);
            }
            
            return savedMember;
        } catch (Exception e) {
            e.printStackTrace();  // 添加錯誤日誌
            throw new RuntimeException("保存會員資料失敗: " + e.getMessage());
        }
    }

    public Member getMemberById(Integer userId) {
        return memberRepository.findById(userId).orElse(null);
    }

    public Member updateMember(Member member) {
        validateMemberInput(member);
        member.setUpdatedDate(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public Member createMember(Member member) {
        validateMemberInput(member);
        member.setStatus(false);  // 預設未認證
        member.setUpdatedDate(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public void updateProfilePhoto(Integer memberId, byte[] photoData) {
        Member member = getMemberById(memberId);
        if (member != null) {
            member.setProfilePhoto(photoData);
            member.setUpdatedDate(LocalDateTime.now());
            memberRepository.save(member);
        }
    }

    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }

    private void validateMemberInput(Member member) {
        // 確保必要的關聯存在
        if (member.getUser() == null || member.getUser().getId() == null) {
            throw new IllegalArgumentException("用戶關聯不能為空");
        }
        
        // 確保ID匹配
        if (!member.getId().equals(member.getUser().getId())) {
            throw new IllegalArgumentException("用戶ID不匹配");
        }
    }

    public boolean verifyMember(Integer userId) {
        Member member = getMemberById(userId);
        if (member != null) {
            member.setStatus(true);  // 設置為已驗證
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    public Optional<Member> findById(int userId) {
        return memberRepository.findById(userId);  // 根據 userId 查找 Member 資料
    }

    public List<Member> findAllById(List<Integer> memberIds) {
        return memberRepository.findAllById(memberIds);
    }

} 
