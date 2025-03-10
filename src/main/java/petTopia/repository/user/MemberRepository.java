package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.MemberBean;

public interface MemberRepository extends JpaRepository<MemberBean, Integer> {

}
