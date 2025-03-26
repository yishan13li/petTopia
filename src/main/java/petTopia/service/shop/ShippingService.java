package petTopia.service.shop;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.shop.ShippingInfoDto;
import petTopia.model.shop.Order;
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

    public ShippingInfoDto getShippingInfoDto(Order order) {
    	ShippingInfoDto shippingInfoDto = new ShippingInfoDto();
    	shippingInfoDto.setReceiverName(order.getShipping().getReceiverName());
    	shippingInfoDto.setReceiverPhone(order.getShipping().getReceiverPhone());
    	shippingInfoDto.setStreet(order.getShipping().getShippingAddress().getStreet());
    	shippingInfoDto.setCity(order.getShipping().getShippingAddress().getCity());
    	shippingInfoDto.setShippingCategory(order.getShipping().getShippingCategory().getName());
    	return shippingInfoDto;
    }
    
    public ShippingAddress createShippingAddress(Member member, String city, String street) {

        // 1. 檢查會員是否已經有相同的地址
        Optional<ShippingAddress> existingAddressOpt = shippingAddressRepo.findByMemberAndCityAndStreet(member, city, street);

        if (existingAddressOpt.isPresent()) {
            // 2. 若地址已存在，且 isCurrent 已經是 true，直接返回
            ShippingAddress existingAddress = existingAddressOpt.get();
            if (!existingAddress.getIsCurrent()) {
                // 只有當地址的 isCurrent 是 false 時才需要更新
                existingAddress.setIsCurrent(true);
                return shippingAddressRepo.save(existingAddress);
            } else {
                // 如果該地址已經是當前地址，則直接返回
                return existingAddress;
            }
        }

        // 3. 先把所有舊地址 isCurrent 設為 false，確保只有一個 isCurrent = true
        shippingAddressRepo.updateAllIsCurrentFalse(member.getId());

        // 4. 若地址不存在，則新增新地址並設為 isCurrent = true
        ShippingAddress shippingAddress = new ShippingAddress();
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
        shipping.setShippingDate(null);
        shipping.setUpdatedTime(new Date());

        return shippingRepo.save(shipping);
    }
}
