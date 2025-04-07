package petTopia.repository.shop;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.ProductCategory;
import petTopia.projection.shop.ProductCategorySalesProjection;
import petTopia.projection.shop.ProductSalesProjection;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>{

	List<OrderDetail> findByOrderId(Integer orderId);
	
    // 查詢銷售量最高的前五名商品及其商品詳情，只選擇已完成的訂單
	@Query("SELECT pd AS productDetail, SUM(od.quantity) AS totalQuantity " +
		       "FROM OrderDetail od " +
		       "JOIN od.product p " +
		       "JOIN p.productDetail pd " +
		       "JOIN od.order o " +
		       "WHERE o.orderStatus.name = '已完成' " +
		       "GROUP BY pd " +
		       "ORDER BY totalQuantity DESC")
    List<ProductSalesProjection> findTop5BestSellingProductsWithDetails(Pageable page);

	@Query("SELECT pc.name AS categoryName, SUM(od.quantity) AS totalQuantity " +
		       "FROM OrderDetail od " +
		       "JOIN od.product p " +
		       "JOIN p.productDetail pd " +
		       "JOIN pd.productCategory pc " +
		       "JOIN od.order o " +
		       "WHERE o.orderStatus.name = '已完成' " +
		       "GROUP BY pc.name " +
		       "ORDER BY totalQuantity DESC")
		List<ProductCategorySalesProjection> findProductCategorySales();

    @Query("SELECT od FROM OrderDetail od WHERE od.order.createdTime BETWEEN :startDate AND :endDate")
    List<OrderDetail> findOrderDetailsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
