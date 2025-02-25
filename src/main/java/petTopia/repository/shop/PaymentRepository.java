package petTopia.repository.shop;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
