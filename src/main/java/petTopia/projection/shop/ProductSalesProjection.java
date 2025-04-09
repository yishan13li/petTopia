package petTopia.projection.shop;

import petTopia.model.shop.ProductDetail;

public interface ProductSalesProjection {
	
    ProductDetail getProductDetail();
    Integer getTotalQuantity();
}
