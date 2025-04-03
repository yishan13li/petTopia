package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Order;
@Repository
public interface OrderRepository extends JpaRepository<petTopia.model.shop.Order, Integer> {

	List<Order> findByMemberId(Integer memberId);
	
	List<Order> findAllById(Iterable<Integer> orderIds);

	// 統計總訂單數
	@Query("SELECT COUNT(o) FROM Order o")
	long countTotalOrders();

	// 統計總收入
	@Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus.name != '已取消'")
	long getTotalRevenue();
}