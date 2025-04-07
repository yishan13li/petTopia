package petTopia.repository.shop;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
    // 計算總銷售額（僅包含已完成的訂單）
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus.name = '已完成'")
    BigDecimal calculateTotalSales();

    // 計算每日銷售額（僅包含已完成的訂單）
    @Query(value = "SELECT CONVERT(DATE, o.created_time) AS date, SUM(o.total_amount) " +
            "FROM [order] o " +
            "WHERE o.order_status_id = (SELECT id FROM order_status WHERE name = '已完成') " +
            "GROUP BY CONVERT(DATE, o.created_time) " +
            "ORDER BY date DESC", nativeQuery = true)
    List<Object[]> calculateDailySalesTrend();

	//計算每月銷售額趨勢
    @Query(value = "SELECT YEAR(o.created_time) AS year, MONTH(o.created_time) AS month, SUM(o.total_amount) " +
            "FROM [order] o " +
            "WHERE o.order_status_id = (SELECT id FROM order_status WHERE name = '已完成') " +
            "GROUP BY YEAR(o.created_time), MONTH(o.created_time) " +
            "ORDER BY year DESC, month DESC", nativeQuery = true)
    List<Object[]> calculateMonthlySalesTrend();

    //財務報表用
    @Query("SELECT o FROM Order o WHERE o.createdTime BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") java.util.Date startDate, @Param("endDate") java.util.Date endDate);
    
}