package petTopia.service.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.user.Member;
import petTopia.repository.user.MemberRepository;

@Service
public class MemberService {
	
    @Autowired
    private MemberRepository memberRepo;

    public Optional<Member> findById(int userId) {
        return memberRepo.findById(userId);  // 根據 userId 查找 Member 資料
    }
}
