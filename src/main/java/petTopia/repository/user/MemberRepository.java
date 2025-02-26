package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Member findById(int memberId);
}
