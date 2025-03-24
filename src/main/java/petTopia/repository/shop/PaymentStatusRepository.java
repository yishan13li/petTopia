package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.PaymentStatus;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer>{
    @Query("SELECT p.name FROM PaymentStatus p")
	List<String> findAllPaymentStatus();
    
    Optional<PaymentStatus> findByName(String name);

}
