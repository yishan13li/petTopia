// package petTopia.repository.user;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import petTopia.model.user.CouponBean;

// import java.time.LocalDateTime;
// import java.util.List;

// @Repository
// public interface CouponRepository extends JpaRepository<CouponBean, Integer> {
    
//     // 查找有效的優惠券
// 	List<CouponBean> findByStatusTrueAndValidStartBeforeAndValidEndAfterAndUsedCountLessThan(
// 		    LocalDateTime validStart, LocalDateTime validEnd, Integer limitCount);
    
//     // 根據折扣類型查找優惠券
//     List<CouponBean> findByDiscountType(CouponBean.DiscountType discountType);
    
//     // 查找特定最低消費金額以下的優惠券
//     List<CouponBean> findByMinOrderValueLessThanEqual(Integer orderAmount);
// } 