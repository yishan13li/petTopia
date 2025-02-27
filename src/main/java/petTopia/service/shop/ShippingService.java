package petTopia.service.shop;

import java.util.Date;
import java.util.Optional;

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

    public ShippingAddress createShippingAddress(Member member, String city, String street) {
    	
        // 1. 檢查會員是否已經有相同的地址
        Optional<ShippingAddress> existingAddressOpt = shippingAddressRepo.findByMemberAndCityAndStreet(member, city, street);

        if (existingAddressOpt.isPresent()) {
            // 2. 若地址已存在，將所有舊地址 isCurrent 設為 false，然後將此地址設為 true
            shippingAddressRepo.updateAllIsCurrentFalse(member.getId());
            
            ShippingAddress existingAddress = existingAddressOpt.get();
            existingAddress.setIsCurrent(true);
            return shippingAddressRepo.save(existingAddress);
        } 
        
        // 3. 若地址不存在，則新增新地址
        shippingAddressRepo.updateAllIsCurrentFalse(member.getId());  // 先將舊地址 isCurrent 設為 false
   
    	ShippingAddress shippingAddress =new ShippingAddress();
    	shippingAddress.setMember(member);
    	shippingAddress.setCity(city);
    	shippingAddress.setStreet(street);
    	shippingAddress.setIsCurrent(true);
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
