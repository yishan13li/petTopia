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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ManageAllOrdersDto;
import petTopia.dto.shop.OrderDetailDto;
import petTopia.dto.shop.UpdateOneOrderDto;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.service.shop.ManageOrderService;
import petTopia.service.shop.OrderDetailService;

@RestController
@RequestMapping("/manage/shop")
public class ManageOrderController {
	
	@Autowired
	private ManageOrderService manageOrderService;
	
	@Autowired
	private PaymentStatusRepository paymentStatusRepo;
	
	@Autowired
	private OrderStatusRepository orderStatusRepo;
	
	@Autowired
	private OrderDetailService orderDetailService;
	
	@Autowired
	private PaymentCategoryRepository paymentCategoryRepo;
	
	@Autowired
	private ShippingCategoryRepository shippingCategoryRepo;
	
	@GetMapping("/orders/options")
	public ResponseEntity<Map<String, Object>> getOrderOptions() {
	    try {
	        Map<String, Object> response = new HashMap<>();
	        response.put("paymentStatusList", paymentStatusRepo.findAllPaymentStatus());
	        response.put("orderStatusList", orderStatusRepo.findAllOrderStatus());
	        response.put("paymentCategoryList", paymentCategoryRepo.findAllPaymentCategory());
	        response.put("shippingCategoryList", shippingCategoryRepo.findAllShippingCategory());
	        
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

    // 後台訂單管理>>查詢會員訂單歷史紀錄
	@GetMapping("/orders")
	public ResponseEntity<Map<String, Object>> getManageOrderHistory(
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

	        // 建立回傳結果
	        Map<String, Object> response = new HashMap<>();
	        response.put("manageOrders", manageOrdersPage);

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
	
	//訂單詳情頁
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable("orderId") Integer orderId) {
        try {
            // 使用 Service 層方法查詢訂單詳情
            OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailById(orderId);
            
            // 返回成功的響應與訂單詳細資料
            return new ResponseEntity<>(orderDetailDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            // 若訂單不存在，返回 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    //更新單一訂單狀態/各種方式
    @PutMapping("/orders/{orderId}/update")
    public ResponseEntity<String> updateOrder(
            @PathVariable Integer orderId,
            @RequestBody UpdateOneOrderDto updatedOrderRequest) {
       
    	try {
    		
            manageOrderService.updateOrder(orderId, updatedOrderRequest);
            return ResponseEntity.ok("訂單已更新");
            
        } catch (RuntimeException e) {
        	
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("更新失敗: " + e.getMessage());
        }
    }

    //批量更新訂單狀態 或 付款狀態
    @PutMapping("/orders/updateBatch")
    public ResponseEntity<String> updateBatchOrders(
    		@RequestParam List<Integer> orderIds,
            @RequestParam String batchStatus ) {
    	
        try {
        	
            manageOrderService.updateBatchOrders(orderIds, batchStatus);
            return ResponseEntity.ok("批量更新成功");
            
        } catch (RuntimeException e) {
        	
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("批量更新失敗: " + e.getMessage());
        }
    }

    //刪除訂單
    @DeleteMapping("/orders/{orderId}/delete")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
    	
        try {
        	
        	manageOrderService.deleteOrder(orderId);  // 呼叫服務層刪除訂單
            return ResponseEntity.ok("訂單已成功刪除");
            
        } catch (IllegalArgumentException e) {
        	
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("訂單未找到");
            
        } catch (Exception e) {
        	
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除訂單時發生錯誤");
        }
    }
}
