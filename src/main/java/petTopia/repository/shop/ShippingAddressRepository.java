package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.ShippingAddress;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, java.lang.Integer> {
    List<ShippingAddress> findByMemberId(Integer memberId);
    
	ShippingAddress findByMemberAndIsCurrent(petTopia.model.user.Member member, boolean b);
}
