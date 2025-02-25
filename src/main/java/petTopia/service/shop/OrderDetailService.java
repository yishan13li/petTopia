package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Cart;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.Product;
import petTopia.repository.shop.OrderDetailRepository;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepo;

    public List<OrderDetail> createOrderDetails(Order order, List<Cart> cartItems) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal discountPrice = (product.getDiscountPrice() != null) ? product.getDiscountPrice() : unitPrice;
            BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(quantity));

            // **建立訂單詳情**
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setUnitPrice(unitPrice);
            orderDetail.setDiscountPrice(discountPrice);
            orderDetail.setTotalPrice(totalPrice);

            orderDetails.add(orderDetail);
        }

        // **批量儲存 OrderDetail**
        orderDetailRepo.saveAll(orderDetails);

        return orderDetails;
    }
}
