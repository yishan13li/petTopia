package petTopia.controller.shop;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.model.user.Member;
import petTopia.service.shop.OrderDetailService;
import petTopia.service.shop.OrderService;

@RestController
@RequestMapping("/shop")
public class OrderController2 {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderDetailService orderDetailService;
	
    @GetMapping("/orderHistory2")
    public String orderHistoryPage(HttpSession session) {
        return "shop/shop_orderHistory";
    }
    
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(HttpSession session,@PathVariable("orderId") Integer orderId) {
        try {
            // 從 session 取得 userId 和 member 資訊
            Member member = (Member) session.getAttribute("member");
        
            Integer memberId = member.getId();
            
            // 使用 Service 層方法查詢訂單詳情
            OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailById(orderId);
            
            Integer memberLogin = orderDetailDto.getMemberId();
            
            if(memberLogin!=memberId) {
            	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // 返回成功的響應與訂單詳細資料
            return new ResponseEntity<>(orderDetailDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            // 若訂單不存在，返回 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // 查詢會員訂單歷史紀錄
    @GetMapping("/orderHistory")
    public ResponseEntity<List<OrderHistoryDto>> getOrderHistory(
            HttpSession session,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String keyword) {  
    	try {
            // 從 session 取得會員資訊
            Member member = (Member) session.getAttribute("member");

            if (member == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 若無會員，返回未授權
            }

            Integer memberId = member.getId();

            // 使用 Service 層方法查詢會員的歷史訂單，並傳入過濾條件
            List<OrderHistoryDto> orderHistoryList = orderService.getOrderHistoryFilter(
                    memberId, orderStatus, startDate, endDate, keyword);

            // 若查無訂單，返回 204
            if (orderHistoryList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // 返回成功的訂單歷史紀錄
            return new ResponseEntity<>(orderHistoryList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();  // 輸出錯誤日誌
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 內部錯誤
        }
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
