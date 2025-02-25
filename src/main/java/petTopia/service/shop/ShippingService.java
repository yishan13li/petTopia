package petTopia.service.shop;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Shipping;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.repository.shop.ShippingAddressRepository;
import petTopia.repository.shop.ShippingRepository;

@Service
public class ShippingService {

    @Autowired
    private ShippingRepository shippingRepo;
    
    @Autowired
    private ShippingAddressRepository shippingAddressRepo;

    public ShippingAddress createShippingAddress(Member member, String city, String street, boolean isCurrent) {
    	ShippingAddress shippingAddress =new ShippingAddress();
    	shippingAddress.setMember(member);
    	shippingAddress.setCity(city);
    	shippingAddress.setStreet(street);
    	shippingAddress.setIsCurrent(isCurrent);
    	return shippingAddressRepo.save(shippingAddress);
    	
    }
    
    public Shipping createShipping(petTopia.model.shop.Order order, ShippingAddress shippingAddress, ShippingCategory shippingCategory, String receiverName, String receiverPhone) {
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setShippingAddress(shippingAddress);
        shipping.setShippingCategory(shippingCategory);
        shipping.setReceiverName(receiverName);
        shipping.setReceiverPhone(receiverPhone);
        shipping.setShippingDate(new Date());
        shipping.setUpdatedTime(new Date());

        return shippingRepo.save(shipping);
    }
}
