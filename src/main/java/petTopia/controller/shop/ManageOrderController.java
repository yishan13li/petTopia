package petTopia.controller.shop;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import petTopia.dto.shop.ManageAllOrdersDto;
import petTopia.dto.shop.OrderHistoryDto;
import petTopia.model.user.Member;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.service.shop.ManageOrderService;
import petTopia.service.shop.OrderDetailService;
import petTopia.service.shop.OrderService;

@RestController
@RequestMapping("/manage/shop")
public class ManageOrderController {
	
	@Autowired
	private ManageOrderService manageOrderService;
	
	@Autowired
	private PaymentCategoryRepository paymentCategoryRepo;
	
	@Autowired
	private ShippingCategoryRepository shippingCategoryRepo;
	
	@Autowired
	private OrderStatusRepository orderStatusRepo;
	
    // 後台訂單管理>>查詢會員訂單歷史紀錄
	@GetMapping("/orders")
	public ResponseEntity<Map<String, Object>> getManageOrderHistory(
	        HttpSession session,
	        @RequestParam(required = false) String memberId,
	        @RequestParam(required = false) String orderStatus,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
	        @RequestParam(required = false) String orderId, // 訂單編號篩選
	        @RequestParam(required = false) String paymentStatus, // 付款狀態篩選
	        @RequestParam(required = false) String productKeyword, // 商品名稱或訂單編號關鍵字篩選
	        @RequestParam(required = false) String paymentCategory, // 付款方式篩選
	        @RequestParam(required = false) String shippingCategory, // 配送方式篩選
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    try {

	        // 查詢訂單
	        Page<ManageAllOrdersDto> manageOrdersPage = manageOrderService.getManageOrderHistoryFilter(
	                memberId, orderStatus, startDate, endDate, orderId, paymentStatus, productKeyword, 
	                paymentCategory, shippingCategory, page, size);


	        // 查詢付款方式和運送方式資料，直接從 Repository 中取得
	        List<String> paymentMethods = paymentCategoryRepo.findAllPaymentCategory(); // 透過 Repository 查詢付款方式
	        List<String> shippingMethods = shippingCategoryRepo.findAllShippingCategory(); // 透過 Repository 查詢運送方式
	        List<String> orderStatusList = orderStatusRepo.findAllOrderStatus(); // 透過 Repository 查詢運送方式

	        // 建立回傳結果
	        Map<String, Object> response = new HashMap<>();
	        response.put("manageOrders", manageOrdersPage);
	        response.put("paymentMethods", paymentMethods);
	        response.put("shippingMethods", shippingMethods);
	        response.put("orderStatusList", orderStatusList);

	        // 若無訂單資料，回傳 204 No Content
	        if (manageOrdersPage.isEmpty()) {
	        	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }
	        
	        // 回傳資料
	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
