package petTopia.projection.shop;

import petTopia.model.shop.Product;

public interface ProductRatingProjection {

    Product getProduct();
    Double getAvgRating();
}
