package petTopia.service.shop;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.dto.shop.OrderItemDto;
import petTopia.dto.shop.OrderSummaryAmoutDto;
import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.ShippingInfoDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.OrderStatus;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.Product;
import petTopia.model.shop.Shipping;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.CouponRepository;
import petTopia.repository.shop.OrderDetailRepository;
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
	
	@Autowired
	private OrderDetailRepository orderDetailRepo;
	
	@Autowired
	private PaymentRepository paymentRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
//	================================================

	
	public OrderSummaryAmoutDto calculateOrderSummary(Integer memberId, Integer couponId, Integer shippingCategoryId) {
	    // 找到購物車內容
	    List<Cart> cartItems = cartService.getCartItems(memberId);

	    // 計算商品總金額
	    BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);

	    // 計算折扣
	    BigDecimal discountAmount = BigDecimal.ZERO;
	    if (couponId != null) {
	        Coupon coupon = couponRepo.findById(couponId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到對應的優惠券"));
	        discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);
	    }

	    // 取得運費（允許 null，預設 0）
	    BigDecimal shippingFee = BigDecimal.ZERO;
	    if (shippingCategoryId != null) {
	        shippingFee = shippingCategoryRepo.findById(shippingCategoryId)
	            .map(ShippingCategory::getShippingCost)
	            .orElse(BigDecimal.ZERO);
	    }
	    
	    // 計算最終總金額
	    BigDecimal orderTotal = subtotal.subtract(discountAmount).add(shippingFee);

	    // 回傳 DTO
	    return new OrderSummaryAmoutDto(subtotal, discountAmount, shippingFee, orderTotal);
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
        ShippingAddress shippingAddress = shippingService.createShippingAddress(member, city, street);
        
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
    
    //將訂單的商品細節轉成orderItem
    public OrderItemDto getOrderItemDto(OrderDetail orderDetail) {
    	OrderItemDto orderItemDto = new OrderItemDto();
    	orderItemDto.setProductPhoto(orderDetail.getProduct().getPhoto());
    	orderItemDto.setProductName(orderDetail.getProduct().getProductDetail().getName());
    	orderItemDto.setProductSize(orderDetail.getProduct().getProductSize().getName());
    	orderItemDto.setProductColor(orderDetail.getProduct().getProductColor().getName());
    	orderItemDto.setQuantity(orderDetail.getQuantity());
    	orderItemDto.setUnitPrice(orderDetail.getUnitPrice());
    	orderItemDto.setDiscountPrice(orderDetail.getDiscountPrice());
    	orderItemDto.setTotalPrice(orderDetail.getTotalPrice());
    	
    	return orderItemDto;
    }
    
    // 查詢訂單的詳情
    public OrderDetailDto getOrderDetailById(Integer orderId) {
        // 查詢訂單
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // 查詢該訂單的明細
        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(orderId);

        // 把 OrderDetail 轉換成 OrderItemDto
        List<OrderItemDto> orderItemDtos = orderDetails.stream()
            .map(this::getOrderItemDto)
            .collect(Collectors.toList());

        // 填充 OrderDetailDto
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setMemberId(order.getMember().getId());
        orderDetailDto.setOrderId(order.getId());
        orderDetailDto.setSubtotal(order.getSubtotal());
        orderDetailDto.setDiscountAmount(order.getDiscountAmount());
        orderDetailDto.setShippingFee(order.getShippingFee());
        orderDetailDto.setTotalAmount(order.getTotalAmount());
        orderDetailDto.setOrderStatus(order.getOrderStatus().getName());
        orderDetailDto.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
        orderDetailDto.setUpdatedDate(order.getUpdatedDate() != null ? new java.sql.Date(order.getUpdatedDate().getTime()) : null);
        orderDetailDto.setOrderItems(orderItemDtos);

        // 查詢並填充配送和支付資訊
        Shipping shipping = shippingRepo.findByOrderId(orderId);
        Payment payment = paymentRepo.findByOrderId(orderId);

        // 填充 ShippingInfoDto
        ShippingInfoDto shippingInfoDto = new ShippingInfoDto();
        shippingInfoDto.setReceiverName(shipping.getReceiverName());
        shippingInfoDto.setReceiverPhone(shipping.getReceiverPhone());
        shippingInfoDto.setStreet(shipping.getShippingAddress().getStreet());
        shippingInfoDto.setCity(shipping.getShippingAddress().getCity());
        shippingInfoDto.setShippingCategory(shipping.getShippingCategory().getName());

        // 填充 PaymentInfoDto
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        paymentInfoDto.setPaymentCategory(payment.getPaymentCategory().getName());
        paymentInfoDto.setPaymentAmount(payment.getPaymentAmount());
        paymentInfoDto.setPaymentStatus(payment.getPaymentStatus().getName());

        // 設定配送和支付資訊
        orderDetailDto.setShippingInfo(shippingInfoDto);
        orderDetailDto.setPaymentInfo(paymentInfoDto);

        return orderDetailDto;
    }

    // 查詢該會員的所有訂單
    public List<OrderHistoryDto> getOrderHistoryByMemberId(Integer memberId) {
        List<Order> orders = orderRepo.findByMemberId(memberId);

        return orders.stream().map(order -> {
            OrderHistoryDto orderHistory = new OrderHistoryDto();
            orderHistory.setOrderId(order.getId());
            orderHistory.setOrderStatus(order.getOrderStatus().getName());
            orderHistory.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
            orderHistory.setTotalAmount(order.getTotalAmount());

            // 查詢付款狀態
            Payment payment = paymentRepo.findByOrderId(order.getId());
            orderHistory.setPaymentStatus(payment.getPaymentStatus().getName());

            // 查詢該訂單的商品明細
            List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

            // **這裡改用 getOrderItemDto 方法**
            List<OrderItemDto> orderItemDtos = orderDetails.stream()
                    .map(this::getOrderItemDto) // 呼叫已經寫好的方法
                    .collect(Collectors.toList());

            orderHistory.setOrderItems(orderItemDtos);

            return orderHistory;
        }).collect(Collectors.toList());
    }

    //把order轉成orderHistoryDto
    private OrderHistoryDto convertToOrderHistoryDto(Order order) {
        OrderHistoryDto orderHistory = new OrderHistoryDto();
        orderHistory.setOrderId(order.getId());
        orderHistory.setOrderStatus(order.getOrderStatus().getName()); // 設定訂單狀態
        orderHistory.setCreatedTime(new java.sql.Date(order.getCreatedTime().getTime()));
        orderHistory.setTotalAmount(order.getTotalAmount());

        // 查詢付款狀態
        Payment payment = paymentRepo.findByOrderId(order.getId());
        orderHistory.setPaymentStatus(payment != null ? payment.getPaymentStatus().getName() : "待付款"); // 設定付款狀態

        // 查詢該訂單的商品明細
        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

        // 使用 getOrderItemDto 方法來轉換商品明細
        List<OrderItemDto> orderItemDtos = orderDetails.stream()
            .map(this::getOrderItemDto)
            .collect(Collectors.toList());

        orderHistory.setOrderItems(orderItemDtos);

        return orderHistory;
    }

    public List<OrderHistoryDto> getOrderHistoryFilter(Integer memberId, String orderStatus, Date startDate, Date endDate, String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("member").get("id"), memberId));

        // 訂單狀態 or 付款狀態
        if (orderStatus != null && !orderStatus.isEmpty()) {
            Predicate statusPredicate;
            
            // 訂單狀態查詢
            if (orderStatus.equals("已付款") || orderStatus.equals("待付款")) {
                // 首先加入對 payment 的左聯結
                Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
                statusPredicate = criteriaBuilder.equal(paymentJoin.get("paymentStatus").get("name"), orderStatus);
            } else {
                // 查詢訂單狀態
                statusPredicate = criteriaBuilder.equal(root.get("orderStatus").get("name"), orderStatus);
            }
            
            predicates.add(statusPredicate);
        }

        // 訂單日期範圍
        if (startDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), startDate));
        }
        if (endDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), endDate));
        }

     // 搜尋關鍵字（訂單編號 or 商品名稱）
        if (keyword != null && !keyword.isEmpty()) {
            // 訂單編號搜尋，將其轉為字串進行比較
            Predicate orderIdPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("id").as(String.class)), "%" + keyword.toLowerCase() + "%");

            // 查詢商品名稱，確保聯接邏輯正確
            Join<Order, OrderDetail> orderDetailsJoin = root.join("orderDetails", JoinType.LEFT);  // 使用LEFT JOIN確保有關聯資料
            Join<OrderDetail, Product> productJoin = orderDetailsJoin.join("product", JoinType.LEFT);  // 進一步聯接 Product
            Predicate productNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("productDetail").get("name")), "%" + keyword.toLowerCase() + "%");

            // 將條件加入到預測條件列表中
            predicates.add(criteriaBuilder.or(orderIdPredicate, productNamePredicate));
        }

        query.where(predicates.toArray(new Predicate[0]));
        List<Order> orders = entityManager.createQuery(query).getResultList();
        
        return orders.stream().map(this::convertToOrderHistoryDto).collect(Collectors.toList());
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
