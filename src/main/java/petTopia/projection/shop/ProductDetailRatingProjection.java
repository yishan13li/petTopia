package petTopia.projection.shop;

import petTopia.model.shop.ProductDetail;

public interface ProductDetailRatingProjection {

    ProductDetail getProductDetail();
    Double getAvgRating();
}
