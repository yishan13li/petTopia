package petTopia.service;

public interface DashboardService {
    // 獲取總訂單數
    long getTotalOrders();
    
    // 獲取總會員數
    long getTotalMembers();
    
    // 獲取總商品數
    long getTotalProducts();
    
    // 獲取總店家數
    long getTotalVendors();
    
    // 獲取總活動數
    long getTotalActivities();

    // 獲取總收入
    long getTotalRevenue();
} 