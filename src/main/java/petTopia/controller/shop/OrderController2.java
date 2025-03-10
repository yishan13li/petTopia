package petTopia.controller.shop;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
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
    public ResponseEntity<Page<OrderHistoryDto>> getOrderHistory(
            HttpSession session,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String paymentCategory, 
            @RequestParam(required = false) String shippingCategory, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            // 從 session 取得會員資訊
            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Integer memberId = member.getId();

            // 查詢訂單
            Page<OrderHistoryDto> orderHistoryPage = orderService.getOrderHistoryFilter(
                    memberId, orderStatus, startDate, endDate, keyword, paymentCategory, shippingCategory, page, size);

            // 若無訂單資料，回傳 204 No Content
            if (orderHistoryPage.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // 回傳分頁結果
            return new ResponseEntity<>(orderHistoryPage, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
