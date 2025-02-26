package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import petTopia.model.user.MemberBean;
import petTopia.repository.user.MemberRepository;

@Service
@Transactional
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public MemberBean createOrUpdateMember(MemberBean member) {
        validateMemberInput(member);
        member.setUpdatedDate(LocalDateTime.now());
        
        // 檢查是否已存在會員資料
        boolean exists = memberRepository.existsById(member.getId());
        if (!exists) {
            member.setStatus(false);  // 新會員預設未認證
        }
        
        return memberRepository.save(member);
    }

    public MemberBean getMemberById(Integer id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
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
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能為空");
        }
        if (member.getPhone() == null || !member.getPhone().matches("^09\\d{8}$")) {
            throw new IllegalArgumentException("手機號碼格式不正確");
        }
    }
} 