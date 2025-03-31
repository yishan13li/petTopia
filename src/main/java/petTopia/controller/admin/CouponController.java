package petTopia.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.shop.Coupon;
import petTopia.repository.shop.CouponRepository;

@RestController
@RequestMapping("/api/admin/coupons")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    // 獲取優惠券列表（分頁）
    @GetMapping
    public ResponseEntity<?> getCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Coupon> couponPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            // 如果有關鍵字，搜尋名稱
            couponPage = couponRepository.findByNameContaining(keyword, pageRequest);
        } else {
            couponPage = couponRepository.findAll(pageRequest);
        }

        return ResponseEntity.ok(couponPage);
    }

    // 新增優惠券
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        try {
            Coupon savedCoupon = couponRepository.save(coupon);
            return ResponseEntity.ok(savedCoupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("新增優惠券失敗：" + e.getMessage());
        }
    }

    // 獲取單個優惠券
    @GetMapping("/{id}")
    public ResponseEntity<?> getCoupon(@PathVariable Integer id) {
        Optional<Coupon> coupon = couponRepository.findById(id);
        if (coupon.isPresent()) {
            return ResponseEntity.ok(coupon.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 更新優惠券
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        try {
            if (!couponRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            coupon.setId(id);
            Coupon updatedCoupon = couponRepository.save(coupon);
            return ResponseEntity.ok(updatedCoupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新優惠券失敗：" + e.getMessage());
        }
    }

    // 刪除優惠券
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer id) {
        try {
            if (!couponRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            couponRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("刪除優惠券失敗：" + e.getMessage());
        }
    }
} 