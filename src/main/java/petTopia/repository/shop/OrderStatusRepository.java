package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.OrderStatus;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer>{

}
