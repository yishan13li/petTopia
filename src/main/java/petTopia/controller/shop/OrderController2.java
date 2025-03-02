package petTopia.controller.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/shop")
public class OrderController2 {

    @GetMapping("/orderHistory2")
    public String orderHistoryPage(HttpSession session) {
        return "shop/shop_orderHistory";
    }
    
    @GetMapping("/orderHistory/{id}")
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
