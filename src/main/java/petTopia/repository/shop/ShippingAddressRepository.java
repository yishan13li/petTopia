package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import petTopia.model.shop.ShippingAddress;
import petTopia.model.user.Member;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, java.lang.Integer> {
    //找該會員所有的地址
	List<ShippingAddress> findByMemberId(Integer memberId);
    
    //找上一次地址
	ShippingAddress findByMemberAndIsCurrent(petTopia.model.user.Member member, boolean b);
	
	//找該會員之前是否已有相同的運送地址
	Optional<ShippingAddress> findByMemberAndCityAndStreet(Member member, String city, String street);
	
	//設之前所有運送地址為false
	@Modifying
	@Query("UPDATE ShippingAddress s SET s.isCurrent = false WHERE s.member.id = :memberId")
	void updateAllIsCurrentFalse(Integer memberId);

}
