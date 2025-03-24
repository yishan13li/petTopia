package petTopia.dto.shop;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDto {
	private Integer memberId;
    private Integer orderId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Date createdTime;
    private Date updatedDate;
    private List<OrderItemDto> orderItems;
    private ShippingInfoDto shippingInfo;
    private PaymentInfoDto paymentInfo;
}
