package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Payment findByOrderId(Integer orderId);

    // 根據交易號查找 Payment 記錄
    Payment findByTradeNo(String tradeNo);
    
}
