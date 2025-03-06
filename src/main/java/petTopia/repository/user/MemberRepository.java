package petTopia.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(int id);  // 根據 id 查找 Member 資料
}
