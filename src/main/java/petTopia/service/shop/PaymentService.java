package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.PaymentResponseDto;
import petTopia.model.shop.Order;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;
    
    @Autowired
    private PaymentStatusRepository paymentStatusRepo;

    public PaymentInfoDto getPaymentInfoDto(Order order) {
    	PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
    	paymentInfoDto.setPaymentAmount(order.getPayment().getPaymentAmount());
    	paymentInfoDto.setPaymentCategory(order.getPayment().getPaymentCategory().getName());
    	paymentInfoDto.setPaymentStatus(order.getPayment().getPaymentStatus().getName());
    	return paymentInfoDto;
    }
    // 處理信用卡支付
    public boolean processCreditCardPayment(Order order,PaymentCategory paymentCategory, BigDecimal paymentAmount) {

    	// 取得應付金額
        BigDecimal orderTotal = order.getTotalAmount();
        
        // 呼叫信用卡API，回傳支付結果 & 付款金額
        PaymentResponseDto paymentResponse = creditCardAPI(orderTotal);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentAmount(paymentResponse.getPaymentAmount());
        payment.setPaymentCategory(paymentCategory); 
        payment.setPaymentDate(new Date());
        payment.setUpdatedDate(new Date());

     // 根據支付結果設置支付狀態
        if (paymentResponse.isSuccess()) {
            setPaymentStatus(payment, 2);  // 設置為 "已付款"（ID = 2）
        } else {
            setPaymentStatus(payment, 3);  // 設置為 "付款失敗"（ID = 3）
        }

        paymentRepo.save(payment);  // 儲存支付紀錄
        return paymentResponse.isSuccess();  // 返回支付是否成功
    }

 // 虛擬信用卡支付API
    private PaymentResponseDto creditCardAPI(BigDecimal paymentAmount) {
        // 假設的支付模擬，實際應該與第三方平台交互
        boolean success = paymentAmount.compareTo(BigDecimal.ZERO) > 0;  // 如果金額大於 0 返回 true
        BigDecimal paidAmount = success ? paymentAmount : BigDecimal.ZERO;  // 若支付成功，返回應付金額，否則為0
        
        // 回傳一個 PaymentResponse 物件，包含支付狀態與金額
        return new PaymentResponseDto(success,paidAmount);
    }


    // 處理貨到付款支付
    public boolean createCashOnDeliveryPayment(Order order, PaymentCategory paymentCategory) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentCategory(paymentCategory);
        payment.setPaymentDate(new Date());
        payment.setUpdatedDate(new Date());

        // 設定支付狀態為待處理
        setPaymentStatus(payment, 1); // 設置為 "待付款"（ID = 1）

        try {
            paymentRepo.save(payment);
            return true;  // 如果成功保存，回傳 true
        } catch (Exception e) {
            return false; // 如果發生錯誤，回傳 false
        }
    }

    // 將設置支付狀態的邏輯提取成一個方法
    private void setPaymentStatus(Payment payment, int statusId) {
        Optional<PaymentStatus> paymentStatus = paymentStatusRepo.findById(statusId);
        paymentStatus.ifPresent(payment::setPaymentStatus); // 只有當 paymentStatus 存在時才設置
    }
}
