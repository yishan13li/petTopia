package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import petTopia.dto.shop.ManageOrderItemDto;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderItemDto;
import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.ShippingInfoDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.Payment;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductSize;
import petTopia.model.shop.Shipping;
import petTopia.projection.shop.ProductSalesProjection;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.ShippingRepository;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepo;

	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private ShippingRepository shippingRepo;
	
	@Autowired
	private PaymentRepository paymentRepo;
	
    public List<OrderDetail> createOrderDetails(Order order, List<Cart> cartItems) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal discountPrice = product.getDiscountPrice();
            
            // 如果 discountPrice 是 null，則使用 unitPrice 計算 totalPrice
            BigDecimal totalPrice = (discountPrice == null) 
                ? unitPrice.multiply(BigDecimal.valueOf(quantity)) 
                : discountPrice.multiply(BigDecimal.valueOf(quantity));
            
            // **建立訂單詳情**
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setUnitPrice(unitPrice);
            orderDetail.setDiscountPrice(discountPrice);
            orderDetail.setTotalPrice(totalPrice);

            orderDetails.add(orderDetail);
        }

        // **批量儲存 OrderDetail**
        orderDetailRepo.saveAll(orderDetails);

        return orderDetails;
    }
    
  //將訂單的商品細節轉成orderItem
    public OrderItemDto getOrderItemDto(OrderDetail orderDetail) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(orderDetail.getProduct().getId());
        orderItemDto.setProductPhoto(orderDetail.getProduct().getPhoto());
        orderItemDto.setProductName(orderDetail.getProduct().getProductDetail().getName());

        // 檢查ProductSize是否為null，避免NullPointerException
        ProductSize productSize = orderDetail.getProduct().getProductSize();
        orderItemDto.setProductSize(productSize != null ? productSize.getName() : null);

        // 檢查ProductColor是否為null，避免NullPointerException
        ProductColor productColor = orderDetail.getProduct().getProductColor();
        orderItemDto.setProductColor(productColor != null ? productColor.getName() : null);

        orderItemDto.setQuantity(orderDetail.getQuantity());
        orderItemDto.setUnitPrice(orderDetail.getUnitPrice());
        orderItemDto.setDiscountPrice(orderDetail.getDiscountPrice());
        orderItemDto.setTotalPrice(orderDetail.getTotalPrice());

        return orderItemDto;
    }
    
    // 查詢訂單的詳情
    public OrderDetailDto getOrderDetailById(Integer orderId) {
        // 查詢訂單
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("找不到該訂單"));

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
        shippingInfoDto.setReceiverName(shipping.getReceiverName() != null ? shipping.getReceiverName() : "無"); // 如果 null 設為 "無"
        shippingInfoDto.setReceiverPhone(shipping.getReceiverPhone() != null ? shipping.getReceiverPhone() : "無"); // 如果 null 設為 "無"
        shippingInfoDto.setStreet(shipping.getShippingAddress() != null ? shipping.getShippingAddress().getStreet() : "無"); // 如果 null 設為 "無"
        shippingInfoDto.setCity(shipping.getShippingAddress() != null ? shipping.getShippingAddress().getCity() : ""); // 如果 null 設為 "無"
        shippingInfoDto.setShippingCategory(shipping.getShippingCategory() != null ? shipping.getShippingCategory().getName() : "無"); // 如果 null 設為 "無"
   
     // 填充 PaymentInfoDto，加入 null 檢查
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        if (payment != null) {
            paymentInfoDto.setPaymentCategory(payment.getPaymentCategory().getName());
            paymentInfoDto.setPaymentAmount(payment.getPaymentAmount());
            paymentInfoDto.setPaymentStatus(payment.getPaymentStatus().getName());
        } else {
            // 如果沒有支付資訊，設置預設的資訊
            paymentInfoDto.setPaymentCategory("待確認");
            paymentInfoDto.setPaymentAmount(new BigDecimal(0));  
            paymentInfoDto.setPaymentStatus("待付款");
        }
        // 設定配送和支付資訊
        orderDetailDto.setShippingInfo(shippingInfoDto);
        orderDetailDto.setPaymentInfo(paymentInfoDto);

        return orderDetailDto;
    }
    

    //將訂單的商品細節轉成manageOrderItem  //後台用
    public ManageOrderItemDto getManagedOrderItemDto(OrderDetail orderDetail) {
    	ManageOrderItemDto manageOrderItemDto = new ManageOrderItemDto();
    	manageOrderItemDto.setProductId(orderDetail.getProduct().getId());
    	manageOrderItemDto.setProductName(orderDetail.getProduct().getProductDetail().getName());
        return manageOrderItemDto;
    }
    
    // 銷售最好的前五名商品及其商品詳情（只計算已完成的訂單）
    public List<ProductSalesProjection> getTop5BestSellingProductsWithDetails() {
    	Pageable top5Page = PageRequest.of(0, 5);
    	return orderDetailRepo.findTop5BestSellingProductsWithDetails(top5Page);
    }
    
}
