package petTopia.service.shop;

import org.springframework.stereotype.Service;

import petTopia.dto.shop.OrderAnalysisDto;
import petTopia.dto.shop.OrderItemAnalysisDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class OrderAnalysisExcelService {
	
    // 生成訂單和商品明細 Excel
    public byte[] generateOrdersAndItemsExcel(List<OrderAnalysisDto> orders, List<OrderItemAnalysisDto> orderItems) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 設置訂單報表分頁
        Sheet ordersSheet = workbook.createSheet("訂單");
        createOrdersSheetHeader(ordersSheet);
        fillOrdersData(ordersSheet, orders);

        // 設置商品明細報表分頁
        Sheet orderItemsSheet = workbook.createSheet("訂單詳細項目");
        createOrderItemsSheetHeader(orderItemsSheet);
        fillOrderItemsData(orderItemsSheet, orderItems);

        // 轉換為 byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    // 設置訂單分頁的表頭
    private void createOrdersSheetHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("訂單編號");
        headerRow.createCell(1).setCellValue("訂單日期");
        headerRow.createCell(2).setCellValue("訂單狀態");
        headerRow.createCell(3).setCellValue("會員編號");
        headerRow.createCell(4).setCellValue("會員姓名");
        headerRow.createCell(5).setCellValue("會員電話");
        headerRow.createCell(6).setCellValue("商品總金額");
        headerRow.createCell(7).setCellValue("折扣金額");
        headerRow.createCell(8).setCellValue("運費");
        headerRow.createCell(9).setCellValue("訂單總金額");
        headerRow.createCell(10).setCellValue("實際付款金額");
        headerRow.createCell(11).setCellValue("付款方式");
        headerRow.createCell(12).setCellValue("付款狀態");
        headerRow.createCell(13).setCellValue("付款時間");
        headerRow.createCell(14).setCellValue("配送方式");
        headerRow.createCell(15).setCellValue("訂單更新時間");
    }

    // 填充訂單資料
    private void fillOrdersData(Sheet sheet, List<OrderAnalysisDto> orders) {
        int rowNum = 1;
        for (OrderAnalysisDto order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getCreatedTime().toString());
            row.createCell(2).setCellValue(order.getOrderStatus());
            row.createCell(3).setCellValue(order.getMemberId());
            row.createCell(4).setCellValue(order.getMemberName());
            row.createCell(5).setCellValue(order.getMemberPhone());
            
            row.createCell(6).setCellValue(Math.round(order.getSubtotal())); 
            row.createCell(7).setCellValue(Math.round(order.getDiscountAmount())); 
            row.createCell(8).setCellValue(Math.round(order.getShippingFee())); 
            row.createCell(9).setCellValue(Math.round(order.getTotalAmount())); 
            row.createCell(10).setCellValue(Math.round(order.getPaymentAmount())); 

            row.createCell(11).setCellValue(order.getPaymentCategory());
            row.createCell(12).setCellValue(order.getPaymentStatus());
            row.createCell(13).setCellValue(order.getPaymentDate() != null ? order.getPaymentDate().toString() : "");
            row.createCell(14).setCellValue(order.getShippingCategory());
            row.createCell(15).setCellValue(order.getLastModifiedDate() != null ? order.getLastModifiedDate().toString() : "");
        }
    }

    // 設置商品明細分頁的表頭
    private void createOrderItemsSheetHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("訂單編號");
        headerRow.createCell(1).setCellValue("商品編號");
        headerRow.createCell(2).setCellValue("商品詳細編號");
        headerRow.createCell(3).setCellValue("商品名稱");
        headerRow.createCell(4).setCellValue("商品顏色");
        headerRow.createCell(5).setCellValue("商品尺寸");
        headerRow.createCell(6).setCellValue("數量");
        headerRow.createCell(7).setCellValue("單價");
        headerRow.createCell(8).setCellValue("優惠價");
        headerRow.createCell(9).setCellValue("總價");
    }

    // 填充商品明細資料
    private void fillOrderItemsData(Sheet sheet, List<OrderItemAnalysisDto> orderItems) {
        int rowNum = 1;
        for (OrderItemAnalysisDto item : orderItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getOrderId());
            row.createCell(1).setCellValue(item.getProductId());
            row.createCell(2).setCellValue(item.getProductDetailId());
            row.createCell(3).setCellValue(item.getProductName());
            row.createCell(4).setCellValue(item.getProductColor());
            row.createCell(5).setCellValue(item.getProductSize());
            row.createCell(6).setCellValue(item.getQuantity());
            row.createCell(7).setCellValue(Math.round(item.getUnitPrice())); 
            row.createCell(8).setCellValue(Math.round(item.getDiscountPrice())); 
            row.createCell(9).setCellValue(Math.round(item.getTotalPrice())); 
       }
    }
}
