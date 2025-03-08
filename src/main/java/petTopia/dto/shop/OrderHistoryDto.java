package petTopia.dto.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryDto {

    private Integer orderId;         // 訂單編號
    private String orderStatus;      // 訂單狀態
    private Date createdTime;        // 訂單日期
    private String paymentStatus;    // 付款狀態
    private String paymentCategory;  // 付款方式
    private String shippingCategory;  // 配送方式
    private BigDecimal totalAmount;      // 訂單總金額
    private List<OrderItemDto> orderItems; // 商品列表

}
