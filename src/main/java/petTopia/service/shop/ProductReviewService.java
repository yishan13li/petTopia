package petTopia.service.shop;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.shop.ProductReviewPhotoDto;
import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.model.shop.ProductReview;
import petTopia.model.shop.ProductReviewPhoto;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductReviewPhotoRepository;
import petTopia.repository.shop.ProductReviewRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.util.ImageConverter;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private ProductReviewPhotoRepository productReviewPhotoRepository;

    //判斷是否已評論過該商品的異常
    public class AlreadyReviewedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

		public AlreadyReviewedException(String message) {
            super(message);
        }
    }
    
    // 新增評論
    public ProductReview createReview(ProductReview productReview,Integer productId,Integer memberId, List<MultipartFile> reviewPhotos) throws IOException {
        // 檢查會員是否已經對該商品評論過
        Optional<ProductReview> existingReview = productReviewRepository.findByProductIdAndMemberId(
        		productId, memberId
        );
        
        if (existingReview.isPresent()) {
            // 拋出自定義的異常
            throw new AlreadyReviewedException("您已經評論過該商品");
        }
        
        // 設定商品與會員
        productReview.setProduct(productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")));
        productReview.setMember(memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found")));

        productReview.setRating(productReview.getRating());
        productReview.setReviewDescription(productReview.getReviewDescription());
        productReview.setReviewTime(new Date());
        // 儲存評論
        ProductReview savedReview = productReviewRepository.save(productReview);

        // 處理圖片
        if (reviewPhotos != null && !reviewPhotos.isEmpty()) {
            for (int i = 0; i < reviewPhotos.size() && i < 5; i++) {  // 限制最多5張圖片
                ProductReviewPhoto reviewPhoto = new ProductReviewPhoto();
                reviewPhoto.setProductReview(savedReview);
                reviewPhoto.setReviewPhoto(reviewPhotos.get(i).getBytes());
                productReviewPhotoRepository.save(reviewPhoto);
            }
        }

        return savedReview;
    }
    
    // 根據 memberId 查找所有評論，並確保載入評論的圖片
    public List<ProductReviewResponseDto> getReviewsByMemberId(Integer memberId) {
        List<ProductReview> reviews = productReviewRepository.findByMemberIdOrderByReviewTimeDesc(memberId);

        // 確保每條評論的圖片都被載入
        for (ProductReview review : reviews) {
            review.getReviewPhotos().size(); // 強制 Hibernate 載入評論的圖片
        }

        // 將 ProductReview 轉換為 ProductReviewDTO
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 將 ProductReview 轉換為 ProductReviewDTO
    private ProductReviewResponseDto convertToDTO(ProductReview review) {
    	ProductReviewResponseDto reviewDTO = new ProductReviewResponseDto();
        
        // 設定基本欄位
        reviewDTO.setReviewId(review.getId());
        reviewDTO.setMemberId(review.getMember().getId());
        reviewDTO.setMemberName(review.getMember().getName());
        reviewDTO.setProductId(review.getProduct().getId());
        reviewDTO.setProductDetailId(review.getProduct().getProductDetail().getId());
        reviewDTO.setProductName(review.getProduct().getProductDetail().getName());
        reviewDTO.setProductColor(review.getProduct().getProductColor() != null ? review.getProduct().getProductColor().getName() : "無");
        reviewDTO.setProductSize(review.getProduct().getProductSize() != null ? review.getProduct().getProductSize().getName() : "無");
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReviewDescription(review.getReviewDescription());
        reviewDTO.setReviewTime(review.getReviewTime());

        // 處理圖片為 Base64 格式
        List<ProductReviewPhotoDto> productReviewPhotoList = review.getReviewPhotos().stream()
                .map(photo -> {
                    ProductReviewPhotoDto reviewPhotoDto = new ProductReviewPhotoDto();
                    reviewPhotoDto.setReviewPhotoId(photo.getId());

                    // 轉換每個圖片為 Base64 字符串
                    String base64Image = ImageConverter.byteToBase64(photo.getReviewPhoto()); // 假设 ImageConverter.byteToBase64 处理的是单张图片

                    // 将 Base64 图片设置到 DTO 中
                    reviewPhotoDto.setReviewPhotos(base64Image); // 直接设置为字符串，而不是列表

                    return reviewPhotoDto;
                })
                .collect(Collectors.toList());

        reviewDTO.setProductReviewPhoto(productReviewPhotoList);
        
        return reviewDTO;
    }

    // 修改單一評論
    public boolean updateReview(Integer reviewId, Integer rating, String reviewDescription, List<MultipartFile> newPhotos,List<Integer> deletePhotoIds) throws IOException {
        Optional<ProductReview> optionalReview = productReviewRepository.findById(reviewId);
        
        if (optionalReview.isPresent()) {
            ProductReview review = optionalReview.get();
            
            if(rating!=null) {
            	review.setRating(rating);
            }
            
            if(reviewDescription!=null) {
            	review.setReviewDescription(reviewDescription);
            }

            if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
                productReviewPhotoRepository.deleteByIdIn(deletePhotoIds);
            }

            
            // **新增新圖片**
            if (newPhotos != null && !newPhotos.isEmpty()) {
                for (int i = 0; i < newPhotos.size() && i < 5; i++) {  // 限制最多5張圖片
                    ProductReviewPhoto reviewPhoto = new ProductReviewPhoto();
                    reviewPhoto.setProductReview(review);
                    reviewPhoto.setReviewPhoto(newPhotos.get(i).getBytes());
                    productReviewPhotoRepository.save(reviewPhoto);
                }
            }

            // 儲存更新
            productReviewRepository.save(review);
            return true;
        }
        
        return false;
    }
    
    // 根據評論ID找單個評論
    public ProductReview getReviewById(Integer reviewId) {
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }
    
    // 讀取所有評論（根據商品ID查詢）
    public List<ProductReview> getReviewsByProductId(Integer productId) {
        return productReviewRepository.findByProductId(productId);
    }

    // 刪除評論
    public void deleteReview(Integer reviewId) {
        ProductReview existingReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        productReviewRepository.delete(existingReview);
    }


}
