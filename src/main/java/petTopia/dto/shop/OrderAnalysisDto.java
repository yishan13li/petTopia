package petTopia.dto.shop;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderAnalysisDto {
	private Integer orderId; //訂單編號
	private Date createdTime; //訂單日期
	private String orderStatus; //訂單狀態
	private Integer memberId; //會員編號
	private String memberName; //會員姓名
	private String memberPhone; //會員電話
    private double subtotal; // 商品總金額
    private double discountAmount; 	//折扣金額
    private double shippingFee; //運費
    private double totalAmount; //訂單總金額(應付金額)
    private double paymentAmount; //實際付款金額
    private String paymentCategory;  //付款方式
    private String paymentStatus; //付款狀態
    private Date paymentDate; //付款時間
    private String shippingCategory; //配送方式
    private Date lastModifiedDate; //訂單更新時間
}
