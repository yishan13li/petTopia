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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.model.shop.Order;
import petTopia.model.user.Member;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.service.shop.OrderDetailService;
import petTopia.service.shop.OrderService;

@RestController
@RequestMapping("/shop")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderDetailService orderDetailService;
	
	@Autowired
	private OrderRepository orderRepo;
	
	//訂單詳情頁
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@RequestParam Integer memberId, @PathVariable("orderId") Integer orderId) {
        try {
            
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
    		@RequestParam Integer memberId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String paymentCategory, 
            @RequestParam(required = false) String shippingCategory, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {

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
    
    // 取消訂單的 API
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam Integer memberId,
        @PathVariable Integer orderId) {

        try {
            // 找訂單
            Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("找不到訂單"));
            order.setId(orderId);
            orderService.cancelOrder(order, memberId); // 呼叫服務層的 cancelOrder 方法

            return ResponseEntity.ok("訂單已成功取消");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("訂單不存在或取消失敗");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("伺服器錯誤，請稍後再試");
        }
    }

}
