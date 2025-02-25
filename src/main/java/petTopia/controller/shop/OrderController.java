package petTopia.controller.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

    @GetMapping("/shop/orderHistory")
    public String orderHistoryPage(HttpSession session) {
        return "shop/shop_orderHistory";
    }
    
    @GetMapping("/shop/orderHistory/detail")
    public String orderHistoryDetailPage(HttpSession session) {
        return "shop/shop_orderHistory_detail";
    }
    
    @GetMapping("/admin/orders")
    public String adminOrderPage() {
        return "shop/manage_shop_order";
    }
    
    @GetMapping("/")
    public String indexPage() {
        return "index";
    }
}
