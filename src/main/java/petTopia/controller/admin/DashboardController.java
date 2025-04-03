package petTopia.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petTopia.service.DashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            return ResponseEntity.ok()
                .body(new DashboardStats(
                    dashboardService.getTotalOrders(),
                    dashboardService.getTotalMembers(),
                    dashboardService.getTotalProducts(),
                    dashboardService.getTotalVendors(),
                    dashboardService.getTotalActivities(),
                    dashboardService.getTotalRevenue()
                ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取統計數據失敗：" + e.getMessage());
        }
    }

    // 內部類用於封裝統計數據
    private static class DashboardStats {
        private final long totalOrders;
        private final long totalMembers;
        private final long totalProducts;
        private final long totalVendors;
        private final long totalActivities;
        private final long totalRevenue;

        public DashboardStats(long totalOrders, long totalMembers, long totalProducts, 
                            long totalVendors, long totalActivities, long totalRevenue) {
            this.totalOrders = totalOrders;
            this.totalMembers = totalMembers;
            this.totalProducts = totalProducts;
            this.totalVendors = totalVendors;
            this.totalActivities = totalActivities;
            this.totalRevenue = totalRevenue;
        }

        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getTotalMembers() { return totalMembers; }
        public long getTotalProducts() { return totalProducts; }
        public long getTotalVendors() { return totalVendors; }
        public long getTotalActivities() { return totalActivities; }
        public long getTotalRevenue() { return totalRevenue; }
    }
} 