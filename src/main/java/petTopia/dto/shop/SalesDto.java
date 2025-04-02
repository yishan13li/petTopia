package petTopia.dto.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
public class SalesDto {

    private BigDecimal totalSales;
    private List<Map<String, Object>> dailySalesTrend;  // 用來存每日銷售趨勢
    private List<Map<String, Object>> monthlySalesTrend; // 用來存每月銷售趨勢

    public Map<String, Object> getFormattedSalesData() {
        Map<String, Object> formattedData = new HashMap<>();

        // 格式化每日銷售數據
        List<String> dailyLabels = new ArrayList<>();
        List<BigDecimal> dailyData = new ArrayList<>();
        for (Map<String, Object> dailySales : dailySalesTrend) {
            dailyLabels.add((String) dailySales.get("date"));
            dailyData.add((BigDecimal) dailySales.get("sales"));
        }

        // 格式化每月銷售數據
        List<String> monthlyLabels = new ArrayList<>();
        List<BigDecimal> monthlyData = new ArrayList<>();
        for (Map<String, Object> monthlySales : monthlySalesTrend) {
            String month = String.format("%d-%02d", monthlySales.get("year"), monthlySales.get("month"));
            monthlyLabels.add(month);
            monthlyData.add((BigDecimal) monthlySales.get("sales"));
        }

        // 添加到返回的 Map 中
        formattedData.put("dailyLabels", dailyLabels);
        formattedData.put("dailyData", dailyData);
        formattedData.put("monthlyLabels", monthlyLabels);
        formattedData.put("monthlyData", monthlyData);
        formattedData.put("totalSales", totalSales);
        
        return formattedData;
    }
}
