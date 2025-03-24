package petTopia.dto.shop;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponseDto {
	
    private String merchantId;             // MerchantID
    private String merchantTradeNo;        // MerchantTradeNo
    private String merchantTradeDate;      // MerchantTradeDate
    private String paymentType;            // PaymentType
    private String totalAmount;        // TotalAmount
    private String tradeDesc;              // TradeDesc
    private String itemName;               // ItemName
    private String returnURL;              // ReturnURL
    private String choosePayment;          // ChoosePayment
    private String checkMacValue;          // CheckMacValue
    private String encryptType;            // EncryptType
    private String orderResultURL;         // OrderResultURL
    private String clientBackURL;         // ClientBackURL
    
    // 轉換 DTO 為 Map
    public Map<String, String> toMap() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", merchantTradeDate);
        params.put("PaymentType", paymentType);
        params.put("TotalAmount", totalAmount);
        params.put("TradeDesc", tradeDesc);
        params.put("ItemName", itemName);
        params.put("ReturnURL", returnURL);
        params.put("OrderResultURL", orderResultURL);
        params.put("ChoosePayment", choosePayment);
        params.put("EncryptType", encryptType);
        params.put("ClientBackURL", clientBackURL);
        return params;
    }
}
