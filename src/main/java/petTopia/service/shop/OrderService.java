package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.OrderStatus;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.Shipping;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.CouponRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;

@Service
public class OrderService {
	
	@Autowired
	private CouponRepository couponRepo;
	
	@Autowired
	private CartService cartService;
    
	@Autowired
    private ShippingCategoryRepository shippingCategoryRepo;
    
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private PaymentCategoryRepository paymentCategoryRepo;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private ShippingRepository shippingRepo;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private OrderStatusRepository orderStatusRepo;
	
	@Autowired
	private ShippingService shippingService;
	
	@Autowired
	private OrderDetailService orderDetailService;
//	================================================
	
	//計算訂單總金額
    public BigDecimal calculateOrderTotal(Member member, List<Cart> cartItems, Coupon coupon, Integer shippingCategoryId) {
        // 計算購物車subtotal金額
        BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);

        // 使用優惠券，算出的折扣金額
        BigDecimal discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);

        // 透過運送方式 ID 拿到對應的運費
        BigDecimal shippingCost = shippingCategoryRepo.findById(shippingCategoryId)
                .map(ShippingCategory::getShippingCost) 
                .orElse(BigDecimal.ZERO); // 如果找不到運送方式，預設運費為 0

        // 計算最終金額：小計 - 折扣 + 運費
        BigDecimal orderTotal = subtotal.subtract(discountAmount).add(shippingCost);
		return orderTotal;
    }

    
    //新增訂單
    @Transactional
    public Order createOrder(Member member,Integer memberId, 
    		Integer couponId, Integer shippingCategoryId, 
    		Integer paymentCategoryId, BigDecimal paymentAmount,
    		String street,String city, String receiverName, String receiverPhone) {
        
    	//找到該member的購物車資訊
    	List<Cart> cartItems = cartService.getCartItems(memberId);
    	
    	
    	// 計算購物車subtotal金額
        BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);

        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 若有使用優惠券，算出的折扣金額
        if (couponId != null) {
            coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的優惠券"));
            discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);
        }

        // 透過運送方式 ID 拿到對應的運費
        BigDecimal shippingFee = shippingCategoryRepo.findById(shippingCategoryId)
                .map(ShippingCategory::getShippingCost) 
                .orElse(BigDecimal.ZERO); // 如果找不到運送方式，預設運費為 0

        // 計算最終金額：商品總金額 - 折扣 + 運費
        BigDecimal orderTotal = subtotal.subtract(discountAmount).add(shippingFee);
    	
        //==================建立訂單==================
        
        //找訂單狀態   //待出貨
        OrderStatus orderStatus = orderStatusRepo.findById(2)   //找待出貨的訂單狀態
        		.orElseThrow(() -> new IllegalArgumentException("找不到對應的訂單狀態"));
        //建立新訂單
        Order order = new Order();
        order.setMember(member);
        order.setSubtotal(subtotal);
        order.setCoupon(coupon);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(orderTotal);
        order.setOrderStatus(orderStatus);//設定訂單狀態
        order.setCreatedTime(new Date());
        order.setUpdatedDate(new Date());
        
        orderRepo.save(order); // 存入資料庫
        
        //OrderDetailService 來建立訂單詳情**
        orderDetailService.createOrderDetails(order, cartItems);

        //更新優惠券使用次數
        if (couponId != null) {
            couponService.updateCouponUsageCount(memberId); 
        }
        
        //==================建立運送資訊================
        
        // 創建 ShippingAddress
        ShippingAddress shippingAddress = shippingService.createShippingAddress(member, city, street, true);
        
        //取得配送方式 
        ShippingCategory shippingCategory = shippingCategoryRepo.findById(shippingCategoryId)
        		.orElseThrow(() -> new IllegalArgumentException("找不到運送方式"));
        
        Shipping shipping = shippingService.createShipping(order, shippingAddress, shippingCategory, receiverName, receiverPhone);
        shippingRepo.save(shipping);
        
        //==============建立付款資訊==============   
        
        // 取得付款方式 
        PaymentCategory paymentCategory = paymentCategoryRepo.findById(paymentCategoryId)
        		.orElseThrow(() -> new IllegalArgumentException("找不到付款方式"));
        
        // 預設為成功（貨到付款不需檢查）
        boolean paymentSuccess = true; 
        
        //檢查是否為信用卡付款
        if (paymentCategory.getId()==1) {
        	// **信用卡付款時，先處理付款**
        	paymentSuccess = paymentService.processCreditCardPayment(order, paymentCategory, paymentAmount);
        	
            if (!paymentSuccess) {
                order.setOrderStatus(orderStatusRepo.findById(1)  // 例如設為 "待處理"
                    .orElseThrow(() -> new IllegalArgumentException("找不到待付款狀態")));
                orderRepo.save(order); // 更新訂單狀態
                return order;  // 返回訂單，讓用戶重新付款
            }
        }
        
        //檢查是否為貨到付款
        if (paymentCategory.getId() == 2) {
            // 貨到付款時不需要付款金額，確保 paymentAmount 為 null
            paymentAmount = null;
            
            paymentSuccess = paymentService.createCashOnDeliveryPayment(order, paymentCategory);
            
            if (!paymentSuccess) {
                throw new RuntimeException("貨到付款處理失敗，請重新操作");
            }
        }

        
        //========================清空購物車========================
        cartService.clearCart(memberId);

        return order;
    }
    
//    @Transactional
//    public void cancelOrder(Integer orderId) {
//        Order order = orderRepo.findById(orderId)
//            .orElseThrow(() -> new RuntimeException("訂單不存在"));
//
//        // **恢復優惠券使用次數**
//        if (order.getCouponId() != null) {
//            MemberCoupon memberCoupon = memberCouponRepo.findById(
//                new MemberCouponId(order.getMemberId(), order.getCouponId()))
//                .orElse(null);
//
//            if (memberCoupon != null) {
//                memberCoupon.setUsageCount(memberCoupon.getUsageCount() - 1);
//                memberCoupon.setStatus(true);
//                memberCouponRepo.save(memberCoupon);
//            }
//        }
//
//        // **標記訂單為取消**
//        order.setStatus("CANCELED");
//        orderRepo.save(order);
//    }
}
