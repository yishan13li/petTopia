package petTopia.service.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
import petTopia.dto.shop.OrderAnalysisDto;
import petTopia.dto.shop.OrderItemAnalysisDto;
import petTopia.dto.shop.OrderSummaryAmoutDto;
import petTopia.dto.shop.SalesDto;
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
import petTopia.projection.shop.ProductCategorySalesProjection;
import petTopia.repository.shop.CartRepository;
import petTopia.repository.shop.CouponRepository;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;

@Service
public class ManageOrderService {
	
	@Autowired
    private ShippingCategoryRepository shippingCategoryRepo;
	
	@Autowired
	private PaymentCategoryRepository paymentCategoryRepo;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private ShippingRepository shippingRepo;
	
	@Autowired
	private OrderStatusRepository orderStatusRepo;
	
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
                
            } else if ("待收貨".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("待收貨")
                        .orElseThrow(() -> new RuntimeException("Status not found"));
                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

            } else if ("已完成".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("已完成")
                        .orElseThrow(() -> new RuntimeException("Status not found"));
                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

            } else if ("已取消".equals(batchStatus)) {
                OrderStatus existOrderStatus = orderStatusRepo.findByName("已取消")
                        .orElseThrow(() -> new RuntimeException("Status not found"));
                order.setOrderStatus(existOrderStatus);
                order.setUpdatedDate(new Date());

            } 

            orderRepo.save(order);
        }
    }

    //刪除訂單(包含訂單細節、配送資訊、付款資訊)
    public void deleteOrder(Integer orderId) {
    	Optional<Order> orderOpt = orderRepo.findById(orderId);
    	if(orderOpt.isPresent()) {
    		orderRepo.delete(orderOpt.get());
    	}
    }
    
    // 獲取銷售數據（總銷售額、每日銷售趨勢、每月銷售趨勢）
    public SalesDto getSalesData() {
        // 取得每日銷售額趨勢
        List<Object[]> dailySalesData = orderRepo.calculateDailySalesTrend();
        Map<String, BigDecimal> dailySalesMap = new HashMap<>();
        for (Object[] data : dailySalesData) {
        	String date = data[0].toString();
        	BigDecimal sales = data[1] != null ? new BigDecimal(data[1].toString()) : BigDecimal.ZERO;
            dailySalesMap.put(date, sales);
        }

        // 生成每日銷售額趨勢
        List<Map<String, Object>> dailySalesTrend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(2025, month);
            int daysInMonth = yearMonth.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                String date = String.format("2025-%02d-%02d", month, day);
                BigDecimal sales = dailySalesMap.getOrDefault(date, BigDecimal.ZERO);
                Map<String, Object> dailySales = new HashMap<>();
                dailySales.put("date", date);
                dailySales.put("sales", sales);
                dailySalesTrend.add(dailySales);
            }
        }

        // 計算每月銷售額
        List<Object[]> monthlySalesData = orderRepo.calculateMonthlySalesTrend();
        Map<Integer, BigDecimal> monthlySalesMap = new HashMap<>();

        // 先放入 SQL 查詢結果
        for (Object[] data : monthlySalesData) {
            int month = (Integer) data[1];
            BigDecimal sales = data[2] != null ? (BigDecimal) data[2] : BigDecimal.ZERO;
            monthlySalesMap.put(month, sales);
        }

        // 1~12 月的銷售額
        List<Map<String, Object>> monthlySalesTrend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthlySales = new HashMap<>();
            monthlySales.put("year", 2025);
            monthlySales.put("month", month);
            monthlySales.put("sales", monthlySalesMap.getOrDefault(month, BigDecimal.ZERO));
            monthlySalesTrend.add(monthlySales);
        }

        // 總銷售額
        BigDecimal totalSales = orderRepo.calculateTotalSales();

        // 回傳 SaleDto 物件
        return new SalesDto(totalSales, dailySalesTrend, monthlySalesTrend);
    }
    
    //商品種類比例
    public List<ProductCategorySalesProjection> getProductCategorySales() {
        return orderDetailRepo.findProductCategorySales();
    }
    
    //財務報表分析
    public OrderAnalysisDto getOrderAnalysisById(Integer orderId) {
        // 查詢訂單
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        Order order = orderOpt.get();
        
        // 查詢配送資訊
        Shipping shipping = shippingRepo.findByOrderId(orderId);
        
        // 查詢付款資訊
        Payment payment = paymentRepo.findByOrderId(orderId);
        
        // 組合為 OrderAnalysisDto
        OrderAnalysisDto orderAnalysisDto = new OrderAnalysisDto();
        orderAnalysisDto.setOrderId(order.getId());
        orderAnalysisDto.setCreatedTime(order.getCreatedTime());
        orderAnalysisDto.setOrderStatus(order.getOrderStatus().getName());
        orderAnalysisDto.setMemberId(order.getMember().getId());
        orderAnalysisDto.setMemberName(order.getMember().getName() != null ? order.getMember().getName() : "無");
        orderAnalysisDto.setMemberPhone(order.getMember().getPhone() != null ? order.getMember().getPhone() : "無");
        orderAnalysisDto.setSubtotal((order.getSubtotal() != null) ? order.getSubtotal().doubleValue() : 0.0);
        orderAnalysisDto.setDiscountAmount((order.getDiscountAmount() != null) ? order.getDiscountAmount().doubleValue() : 0.0);
        orderAnalysisDto.setShippingFee((order.getShippingFee() != null) ? order.getShippingFee().doubleValue() : 0.0);
        orderAnalysisDto.setTotalAmount((order.getTotalAmount() != null) ? order.getTotalAmount().doubleValue() : 0.0);

        if (payment != null) {
            orderAnalysisDto.setPaymentCategory(payment.getPaymentCategory() != null ? payment.getPaymentCategory().getName() : "無");
            orderAnalysisDto.setPaymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().getName() : "無");
            orderAnalysisDto.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate() : null);
            orderAnalysisDto.setPaymentAmount((payment.getPaymentAmount() != null) ? payment.getPaymentAmount().doubleValue() : 0.0);
            } else {
            orderAnalysisDto.setPaymentCategory("無");
            orderAnalysisDto.setPaymentStatus("無");
            orderAnalysisDto.setPaymentDate(null);
            orderAnalysisDto.setPaymentAmount(0.0);
        }

        if (shipping != null) {
            orderAnalysisDto.setShippingCategory(shipping.getShippingCategory() != null ? shipping.getShippingCategory().getName() : "無");
            orderAnalysisDto.setLastModifiedDate(shipping.getUpdatedTime() != null ? shipping.getUpdatedTime() : null);
        } else {
            orderAnalysisDto.setShippingCategory("無");
            orderAnalysisDto.setLastModifiedDate(null);
        }

        return orderAnalysisDto;
    }


    public List<OrderAnalysisDto> getOrdersAnalysisByDateRange(Date startDate, Date endDate) {
        // 查詢指定日期範圍內的所有訂單
        List<Order> orders = orderRepo.findOrdersByDateRange(startDate, endDate);
        
        // 轉換為 DTO 列表
        List<OrderAnalysisDto> orderAnalysisDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderAnalysisDto orderAnalysisDto = getOrderAnalysisById(order.getId());
            orderAnalysisDtos.add(orderAnalysisDto);
        }
        
        return orderAnalysisDtos;
    }
    
    //=====orderItems=====
    // 根據時間範圍查詢商品明細
    public List<OrderItemAnalysisDto> getOrderItemsByDateRange(Date startDate, Date endDate) {
        List<OrderDetail> orderDetails = orderDetailRepo.findOrderDetailsByDateRange(startDate, endDate);
        
        // 轉換為 OrderItemAnalysisDto
        List<OrderItemAnalysisDto> orderItemAnalysisDtos = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            OrderItemAnalysisDto orderItemAnalysisDto = new OrderItemAnalysisDto();
            orderItemAnalysisDto.setOrderId(orderDetail.getOrder().getId());
            orderItemAnalysisDto.setProductId(orderDetail.getProduct().getId());
            orderItemAnalysisDto.setProductDetailId(
                orderDetail.getProduct().getProductDetail() != null ? orderDetail.getProduct().getProductDetail().getId() : null
            );
            orderItemAnalysisDto.setProductName(
                orderDetail.getProduct().getProductDetail() != null ? orderDetail.getProduct().getProductDetail().getName() : "無"
            );
            orderItemAnalysisDto.setProductColor(
                orderDetail.getProduct().getProductColor() != null ? orderDetail.getProduct().getProductColor().getName() : "無"
            );
            orderItemAnalysisDto.setProductSize(
                orderDetail.getProduct().getProductSize() != null ? orderDetail.getProduct().getProductSize().getName() : "無"
            );
            orderItemAnalysisDto.setQuantity(orderDetail.getQuantity() != null ? orderDetail.getQuantity() : 0);
            orderItemAnalysisDto.setUnitPrice((orderDetail.getUnitPrice() != null) ? orderDetail.getUnitPrice().doubleValue() : 0.0);
            orderItemAnalysisDto.setDiscountPrice((orderDetail.getDiscountPrice() != null) ? orderDetail.getDiscountPrice().doubleValue() : 0.0);
            orderItemAnalysisDto.setTotalPrice((orderDetail.getTotalPrice() != null) ? orderDetail.getTotalPrice().doubleValue() : 0.0);

            orderItemAnalysisDtos.add(orderItemAnalysisDto);
        }

        return orderItemAnalysisDtos;
    }

}
