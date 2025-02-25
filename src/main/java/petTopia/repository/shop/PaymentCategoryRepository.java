package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.PaymentCategory;

@Repository
public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, java.lang.Integer> {

}
