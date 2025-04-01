package petTopia.dto.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleDto {

    private BigDecimal totalSales;
    private List<Map<String, Object>> dailySalesTrend;  // 用來存每日銷售趨勢
    private List<Map<String, Object>> monthlySalesTrend; // 用來存每月銷售趨勢

}
