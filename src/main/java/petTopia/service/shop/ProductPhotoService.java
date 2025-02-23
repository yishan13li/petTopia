package petTopia.service.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.ProductPhoto;
import petTopia.model.shop.ProductPhotoRepository;

@Service
public class ProductPhotoService {

	@Autowired
	private ProductPhotoRepository productPhotoRepository;
	

	
}
