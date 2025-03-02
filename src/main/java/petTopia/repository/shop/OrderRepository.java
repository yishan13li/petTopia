package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Order;
@Repository
public interface OrderRepository extends JpaRepository<petTopia.model.shop.Order, Integer> {

	List<Order> findByMemberId(Integer memberId);
	
	Order findByIdAndMemberId(Integer orderId,Integer memberId);
}