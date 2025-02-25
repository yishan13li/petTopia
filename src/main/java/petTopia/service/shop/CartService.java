package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Cart;
import petTopia.repository.shop.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;  // 你的 CartRepository

    // 根據用戶 ID 獲取購物車商品
    public List<Cart> getCartItems(Integer memberId) {
        return cartRepo.findByMemberId(memberId);  // 根據 userId 查詢購物車
    }

 // 計算購物車的總金額
    public BigDecimal calculateTotalPrice(List<Cart> cartItems) {
        BigDecimal total = BigDecimal.ZERO; // 初始化為 0

        for (Cart item : cartItems) {
            // 取得商品的價格（折扣價或原價）
            BigDecimal price;

            // 檢查折扣價是否為 null，若為 null 則使用單價
            if (item.getProduct().getDiscountPrice() != null) {
                price = item.getProduct().getDiscountPrice();
            } else {
                price = item.getProduct().getUnitPrice();
            }

            // 取得數量並轉換為 BigDecimal
            BigDecimal quantity = new BigDecimal(item.getQuantity());

            // 計算總金額並加到 total
            total = total.add(price.multiply(quantity)); // 使用 add() 方法進行加法
        }

        return total; // 返回計算後的總金額
    }


    // 清空用戶的購物車
    public void clearCart(Integer memberId) {
        cartRepo.deleteByMemberId(memberId);  // 根據 memberId 刪除購物車內容
    }
}
