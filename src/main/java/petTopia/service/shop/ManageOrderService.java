package petTopia.service.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import petTopia.dto.shop.ManageAllOrdersDto;
import petTopia.dto.shop.ManageOrderItemDto;
import petTopia.dto.shop.OrderSummaryAmoutDto;
import petTopia.dto.shop.UpdateOneOrderDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.OrderStatus;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
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
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;

@Service
public class ManageOrderService {
	
	@Autowired
	private CouponRepository couponRepo;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartRepository cartRepo;
    
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
	
	@Autowired
	private PaymentStatusRepository paymentStatusRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
//	================================================

	//把order轉成manageAllOrdersDto
	private ManageAllOrdersDto convertToManageAllOrdersDto(Order order) {
	    ManageAllOrdersDto managedOrder = new ManageAllOrdersDto();

	    managedOrder.setMemberId(order.getMember().getId());
	    managedOrder.setOrderId(order.getId());
	    managedOrder.setOrderStatus(order.getOrderStatus().getName());
	    managedOrder.setOrderDate(new java.sql.Date(order.getCreatedTime().getTime()));
	    managedOrder.setTotalAmount(order.getTotalAmount());
	    managedOrder.setNote(order.getNote());

	    // 查詢付款狀態
	    Payment payment = paymentRepo.findByOrderId(order.getId());
	    managedOrder.setPaymentStatus(payment != null ? payment.getPaymentStatus().getName() : "待付款");
	    managedOrder.setPaymentCategory(payment.getPaymentCategory().getName());
	    
	    // 配送狀態
	    Shipping shipping = shippingRepo.findByOrderId(order.getId());
	    managedOrder.setShippingCategory(shipping.getShippingCategory().getName());

	    // 查詢該訂單的商品明細
	    List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

	    // 使用 getManagedOrderItemDto 方法來轉換商品明細
	    List<ManageOrderItemDto> manageOrderItemDtos = orderDetails.stream()
	        .map(orderDetail -> {
	            ManageOrderItemDto manageOrderItemDto = orderDetailService.getManagedOrderItemDto(orderDetail);
	           
	            return manageOrderItemDto;
	        })
	        .collect(Collectors.toList());

	    managedOrder.setManagedOrderItems(manageOrderItemDtos);

	    return managedOrder;
	}

    public Page<ManageAllOrdersDto> getManageOrderHistoryFilter(
    	    String memberId, String orderStatus, Date startDate, Date endDate, String orderId, 
    	    String paymentStatus, String productKeyword,
    	    String paymentCategory, String shippingCategory, int page, int size) {

    	    if (page < 1) page = 1;
    	    if (size < 1) size = 10;

    	    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	    CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
    	    Root<Order> root = query.from(Order.class);

    	    List<Predicate> predicates = new ArrayList<>();

    	    // 會員編號
    	    if (memberId != null && !memberId.isEmpty()) {
    	        predicates.add(criteriaBuilder.equal(root.get("member").get("id"), memberId));
    	    }
    	    
    	 // 訂單編號篩選
    	    if (orderId != null && !orderId.isEmpty()) {
    	        predicates.add(criteriaBuilder.equal(root.get("id"), orderId));
    	    }
    	    
    	    // 訂單狀態篩選
    	    if (orderStatus != null && !orderStatus.isEmpty()) {
    	        Predicate statusPredicate = criteriaBuilder.equal(root.get("orderStatus").get("name"), orderStatus);
    	        predicates.add(statusPredicate);
    	    }

    	    // 付款狀態篩選
    	    if (paymentStatus != null && !paymentStatus.isEmpty()) {
    	        Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
    	        Predicate paymentStatusPredicate = criteriaBuilder.equal(paymentJoin.get("paymentStatus").get("name"), paymentStatus);
    	        predicates.add(paymentStatusPredicate);
    	    }

    	    // 付款方式篩選
    	    if (paymentCategory != null && !paymentCategory.isEmpty()) {
    	        Join<Order, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
    	        predicates.add(criteriaBuilder.equal(paymentJoin.get("paymentCategory").get("name"), paymentCategory));
    	    }

    	    // 配送方式篩選
    	    if (shippingCategory != null && !shippingCategory.isEmpty()) {
    	        Join<Order, Shipping> shippingJoin = root.join("shipping", JoinType.LEFT);
    	        predicates.add(criteriaBuilder.equal(shippingJoin.get("shippingCategory").get("name"), shippingCategory));
    	    }

    	    // 訂單日期範圍
    	    if (startDate != null) {
    	        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), startDate));
    	    }
    	    if (endDate != null) {
    	        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), endDate));
    	    }

    	    // 搜尋關鍵字（訂單編號 or 商品名稱）
    	    if (productKeyword != null && !productKeyword.isEmpty()) {
    	        Predicate orderIdPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("id").as(String.class)), "%" + productKeyword.toLowerCase() + "%");

    	        // 商品名稱搜尋
    	        Join<Order, OrderDetail> orderDetailsJoin = root.join("orderDetails", JoinType.LEFT);
    	        Join<OrderDetail, Product> productJoin = orderDetailsJoin.join("product", JoinType.LEFT);
    	        Predicate productNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("productDetail").get("name")), "%" + productKeyword.toLowerCase() + "%");

    	        predicates.add(criteriaBuilder.or(orderIdPredicate, productNamePredicate));
    	    }
    	    
    	    // 設定查詢條件
    	    query.where(predicates.toArray(new Predicate[0]));
    	    query.orderBy(criteriaBuilder.desc(root.get("id")));

    	    // 執行查詢，得到所有符合條件的資料
    	    List<Order> orders = entityManager.createQuery(query).getResultList();

    	    // 計算總記錄數
    	    long totalRecords = orders.size();

    	    // 分頁
    	    int startIndex = (page - 1) * size;
    	    int endIndex = Math.min(startIndex + size, orders.size());
    	    List<Order> paginatedOrders = orders.subList(startIndex, endIndex);

    	    // 轉換 DTO
    	    List<ManageAllOrdersDto> orderDtos = paginatedOrders.stream().map(this::convertToManageAllOrdersDto).collect(Collectors.toList());

    	    return new PageImpl<>(orderDtos, PageRequest.of(page - 1, size), totalRecords);
    	}
	
    //更新單一訂單
    public Order updateOrder(Integer orderId,UpdateOneOrderDto updatedOrderRequest) {
        
    	String orderStatus = updatedOrderRequest.getOrderStatus();
        String paymentStatus = updatedOrderRequest.getPaymentStatus();
        String paymentCategory = updatedOrderRequest.getPaymentCategory();
        String shippingCategory = updatedOrderRequest.getShippingCategory();
        BigDecimal totalAmount = updatedOrderRequest.getTotalAmount();
        String note = updatedOrderRequest.getNote();
        
    	// 根據訂單 ID 查詢訂單
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        //更新訂單備註
        if(note!=null) {
        	order.setNote(note);
        	order.setUpdatedDate(new Date());
        }
        
        // 更新訂單狀態(order>orderStatus)
        if (orderStatus != null) {
            OrderStatus existOrderStatus = orderStatusRepo.findByName(orderStatus)
                    .orElseThrow(() -> new RuntimeException("Order status not found"));

            // 如果狀態是配送中就更新配送日期
            if ("配送中".equals(orderStatus)) {
            	Shipping shipping = shippingRepo.findByOrderId(orderId);
                shipping.setShippingDate(new Date());  
                shipping.setUpdatedTime(new Date());  
            }
            
            order.setOrderStatus(existOrderStatus);
            order.setUpdatedDate(new Date());  
        }
        
        // 更新付款狀態(payment>paymentStatus)
        if (paymentStatus != null) {
            PaymentStatus existPaymentStatus = paymentStatusRepo.findByName(paymentStatus)
                    .orElseThrow(() -> new RuntimeException("Payment status not found"));

            Payment payment = paymentRepo.findByOrderId(orderId);
            if (payment == null) {
                throw new RuntimeException("Payment not found");
            }
            
            // 只有在付款狀態為 "已付款" 時，才設置付款日期
            if ("已付款".equals(paymentStatus)) {
                payment.setPaymentDate(new Date());  
            }
            
            payment.setPaymentStatus(existPaymentStatus);
            payment.setUpdatedDate(new Date());  
            paymentRepo.save(payment); 
        }
        
     // 更新付款方式 (payment>paymentCategory)
        if (paymentCategory != null) {
            PaymentCategory existPaymentCategory = paymentCategoryRepo.findByName(paymentCategory)
                    .orElseThrow(() -> new RuntimeException("Payment category not found"));

            Payment payment = paymentRepo.findByOrderId(orderId);
            if (payment == null) {
                throw new RuntimeException("Payment not found");
            }

            payment.setPaymentCategory(existPaymentCategory);
            payment.setUpdatedDate(new Date());

            paymentRepo.save(payment);
        }

        // 更新配送方式 (shipping>shippingCategory)
        if (shippingCategory != null) {
            ShippingCategory existShippingCategory = shippingCategoryRepo.findByName(shippingCategory)
                    .orElseThrow(() -> new RuntimeException("Shipping category not found"));

            Shipping shipping = shippingRepo.findByOrderId(orderId);
            if (shipping == null) {
                throw new RuntimeException("Shipping not found");
            }

            shipping.setShippingCategory(existShippingCategory);
            shipping.setUpdatedTime(new Date());  

            shippingRepo.save(shipping);
        }
        
        if (totalAmount != null) {
            order.setTotalAmount(totalAmount);
        }

        return orderRepo.save(order);
    }
    
    //批量更新訂單狀態或是付款狀態
    public void updateBatchOrders(List<Integer> orderIds, String batchStatus) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new RuntimeException("訂單 ID 清單不能為空");
        }

        List<Order> orders = orderRepo.findAllById(orderIds);
        
        for (Order order : orders) {
            if ("已付款".equals(batchStatus)) {
                // 更新付款狀態
                Payment payment = paymentRepo.findByOrderId(order.getId());
                if (payment != null) {
                    PaymentStatus existPaymentStatus = paymentStatusRepo.findByName("已付款")
                            .orElseThrow(() -> new RuntimeException("Payment status not found"));
                    payment.setPaymentStatus(existPaymentStatus);
                    payment.setPaymentDate(new Date());  // 設置付款日期
                    payment.setUpdatedDate(new Date());
                    paymentRepo.save(payment);
                }

            } else if ("配送中".equals(batchStatus)) {
                // 更新配送中狀態
                OrderStatus existOrderStatus = orderStatusRepo.findByName("配送中")
                        .orElseThrow(() -> new RuntimeException("Order status not found"));
                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

                Shipping shipping = shippingRepo.findByOrderId(order.getId());
                if (shipping != null) {
                    shipping.setShippingDate(new Date());  // 設置配送日期
                    shipping.setUpdatedTime(new Date());
                    shippingRepo.save(shipping);
                }

            } else if ("待收貨".equals(batchStatus)||"已完成".equals(batchStatus)||"已取消".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("待收貨")
                        .orElseThrow(() -> new RuntimeException("Status not found"));
                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

            } 

            orderRepo.save(order);
        }
    }

    
	public OrderSummaryAmoutDto calculateOrderSummary(Integer memberId, Integer couponId, Integer shippingCategoryId,List<Integer> productIds) {

	    // 計算商品總金額
	    BigDecimal subtotal = cartService.calculateTotalPrice(memberId,productIds);

	    // 計算折扣
	    BigDecimal discountAmount = BigDecimal.ZERO;
	    if (couponId != null) {
	        Coupon coupon = couponRepo.findById(couponId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到對應的優惠券"));
	        discountAmount = couponService.getDiscountAmountByCoupon(coupon, subtotal);
	    
	        // 四捨五入到整數
	        discountAmount = discountAmount.setScale(0, RoundingMode.HALF_UP);
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

        // 四捨五入到整數
        orderTotal = orderTotal.setScale(0, RoundingMode.HALF_UP);
        
	    // 回傳 DTO
	    return new OrderSummaryAmoutDto(subtotal, discountAmount, shippingFee, orderTotal);
	}
	
    //新增訂單
	@Transactional
	public Map<String, Object> createOrder(Member member, Integer memberId, 
	        Integer couponId, Integer shippingCategoryId, 
	        Integer paymentCategoryId, BigDecimal paymentAmount,
	        String street, String city, String receiverName, String receiverPhone,List<Integer> productIds) throws Exception {

	    // 計算購物車 subtotal 金額
	    BigDecimal subtotal = cartService.calculateTotalPrice(memberId,productIds);

	    Coupon coupon = null;
	    BigDecimal discountAmount = BigDecimal.ZERO;

	    // 若有使用優惠券，計算折扣金額
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

	    // 四捨五入到整數
	    orderTotal = orderTotal.setScale(0, RoundingMode.HALF_UP);

	    // ==================建立訂單==================
	    // 設定訂單狀態 (預設為 "待付款")
	    OrderStatus orderStatus = orderStatusRepo.findById(1)  // "待付款" 狀態
	            .orElseThrow(() -> new IllegalArgumentException("找不到待付款狀態"));

	    // 建立新訂單
	    Order order = new Order();
	    order.setMember(member);
	    order.setSubtotal(subtotal);
	    order.setCoupon(coupon);
	    order.setDiscountAmount(discountAmount);
	    order.setShippingFee(shippingFee);
	    order.setTotalAmount(orderTotal);
	    order.setOrderStatus(orderStatus);
	    order.setCreatedTime(new Date());
	    order.setUpdatedDate(new Date());

	    orderRepo.save(order); // 存入資料庫

	    // 訂單詳情
        // 查詢會員購物車中這些商品
        List<Cart> cartItems = cartRepo.findByMemberIdAndProductIdIn(memberId, productIds);

	    orderDetailService.createOrderDetails(order, cartItems);

	    // 更新優惠券使用次數
	    if (couponId != null) {
	        couponService.updateCouponUsageCount(memberId); 
	    }

	    // ==================建立運送資訊================
	    ShippingAddress shippingAddress = shippingService.createShippingAddress(member, city, street);
	    ShippingCategory shippingCategory = shippingCategoryRepo.findById(shippingCategoryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到運送方式"));

	    Shipping shipping = shippingService.createShipping(order, shippingAddress, shippingCategory, receiverName, receiverPhone);
	    shippingRepo.save(shipping);

	    // ==============建立付款資訊==============
	    PaymentCategory paymentCategory = paymentCategoryRepo.findById(paymentCategoryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到付款方式"));


	    // **貨到付款**
	    if (paymentCategory.getId() == 2) {
	        paymentAmount = null;
	        boolean paymentSuccess = paymentService.createCashOnDeliveryPayment(order, paymentCategory);
	        
	        if (!paymentSuccess) {
	            throw new RuntimeException("貨到付款處理失敗，請重新操作");
	        }

	        // 直接將訂單狀態設為 "待出貨"
	        order.setOrderStatus(orderStatusRepo.findById(2)
	            .orElseThrow(() -> new IllegalArgumentException("找不到待出貨狀態")));
	    } else {
	        // **信用卡支付或其他支付方式處理**
	        if (paymentCategory.getId() == 1) {  // 若是信用卡付款

	            // 直接將訂單狀態設為 "待處理"    //等付款後再變成待出貨
	            order.setOrderStatus(orderStatusRepo.findById(1)
	                .orElseThrow(() -> new IllegalArgumentException("找不到待處理狀態")));
		        boolean paymentSuccess = paymentService.createCreditCardPayment(order, paymentCategory);
		        
		        if (!paymentSuccess) {
		            throw new RuntimeException("信用卡付款處理失敗，請重新操作");
		        }
	        }
	    }

	    orderRepo.save(order); // 更新訂單狀態

	    // 清空購物車
	    cartService.clearCart(memberId, productIds);

	    // **回傳訂單資訊 & ECPay 付款網址**
	    Map<String, Object> response = new HashMap<>();
	    response.put("order", order);

	    return response;
	}

    @Transactional
    public void cancelOrder(Order order,Integer memberId) {
        order = orderRepo.findById(order.getId())
            .orElseThrow(() -> new RuntimeException("訂單不存在"));

        order.getOrderStatus();
        
        // **標記訂單為取消**
        setOrderStatus(order,6);
        orderRepo.save(order);
        
        // **恢復優惠券使用次數**
        if (order.getCoupon() != null) {
            couponService.updateCouponUsageCount(memberId);
        }

    }
    
    // 將設置支付狀態的邏輯提取成一個方法
    private void setOrderStatus(Order order, Integer statusId) {
        Optional<OrderStatus> orderStatus = orderStatusRepo.findById(statusId);
        orderStatus.ifPresent(order::setOrderStatus); // 只有當 orderStatus 存在時才設置
    }

	public jakarta.persistence.criteria.Order updateOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
