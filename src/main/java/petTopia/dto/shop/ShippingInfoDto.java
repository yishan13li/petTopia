package petTopia.dto.shop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShippingInfoDto {
    private String receiverName;
    private String receiverPhone;
    private String street;
    private String city;
    private String shippingCategory;
}
