package petTopia.service.shop;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.shop.ProductReviewPhotoDto;
import petTopia.dto.shop.ProductReviewResponseDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductReview;
import petTopia.model.shop.ProductReviewPhoto;
import petTopia.projection.shop.ProductDetailRatingProjection;
import petTopia.projection.shop.ProductRatingProjection;
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
    
    public boolean hasReviewed(Integer productId, Integer memberId) {
        Optional<ProductReview> existingReview = productReviewRepository.findByProductIdAndMemberId(productId, memberId);
        return existingReview.isPresent();
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
        reviewDTO.setProductPhoto(review.getProduct().getPhoto());
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

                    // 將base64放到dto中
                    reviewPhotoDto.setReviewPhotos(base64Image); // 直接设置为字符串，而不是列表

                    return reviewPhotoDto;
                })
                .collect(Collectors.toList());

        reviewDTO.setProductReviewPhoto(productReviewPhotoList);
        
        return reviewDTO;
    }
    
    //===================會員評論==================
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
    public Page<ProductReviewResponseDto> getReviewsByMemberId(Integer memberId, int page, int size) {
        // 設定分頁請求，根據 reviewTime 降冪排序
        Pageable pageable = PageRequest.of(page - 1, size);  // 頁數從0開始
        
        // 查詢會員的評論，並使用分頁
        Page<ProductReview> reviewsPage = productReviewRepository.findByMemberIdOrderByReviewTimeDesc(memberId, pageable);
        
        // 確保每條評論的圖片都被載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        // 將 ProductReview 轉換為 ProductReviewResponseDto
        return reviewsPage.map(this::convertToDTO);
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
    
    //===================商品評論===================
    
    // 讀取所有評論（根據商品ID查詢）
    public List<ProductReview> getReviewsByProductId(Integer productId) {
        return productReviewRepository.findByProductId(productId);
    }
    
    // 找某商品的平均評分
    public Double getAverageRatingByProductDetailId(Integer productDetailId) {
        return productReviewRepository.findAverageRatingByProductDetailId(productDetailId);
    }

    // 找某商品的所有評論
    public Page<ProductReviewResponseDto> getReviewsByProductDetailId(Integer productDetailId, int page, int size) {
        // 設定分頁請求，根據 reviewTime 降冪排序
        Pageable pageable = PageRequest.of(page - 1, size);  // 頁數從0開始
        
        // 查詢該商品的所有評論，並使用分頁
        Page<ProductReview> reviewsPage = productReviewRepository.findAllReviewsByProductDetailIdOrderByReviewTimeDesc(productDetailId, pageable);

        // 確保每條評論的圖片都被載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        // 將 ProductReview 轉換為 ProductReviewResponseDto
        return reviewsPage.map(this::convertToDTO);
    }
    
    //找某商品的總評論數
    public Integer getReviewsCountByProductDetailId(Integer productDetailId) {
        return productReviewRepository.countReviewsByProductDetailId(productDetailId);
    }
    
 // 取得所有評論，根據 reviewTime 降冪排序（含分頁）
    public Page<ProductReviewResponseDto> getAllReviewsSortedByTime(int page, int size) {
        // 設定分頁請求，頁數從 0 開始
        Pageable pageable = PageRequest.of(page - 1, size);

        // 查詢評論並使用分頁
        Page<ProductReview> reviewsPage = productReviewRepository.findAllReviewsOrderByReviewTimeDesc(pageable);

        // 確保每條評論的圖片都被載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size()); // 強制 Hibernate 載入評論的圖片

        // 將 ProductReview 轉換為 ProductReviewResponseDto
        return reviewsPage.map(this::convertToDTO);
    }

    // 取得所有評論，根據 id 降冪排序（含分頁）
    public Page<ProductReviewResponseDto> getAllReviewsSortedById(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductReview> reviewsPage = productReviewRepository.findAllReviewsByIdDesc(pageable);
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size());
        return reviewsPage.map(this::convertToDTO);
    }

    // 取得所有評論，根據 rating 降冪排序（含分頁）
    public Page<ProductReviewResponseDto> getAllReviewsSortedByRating(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductReview> reviewsPage = productReviewRepository.findAllReviewsByRatingDesc(pageable);
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size());
        return reviewsPage.map(this::convertToDTO);
    }
    
    //模糊搜尋
    public Page<ProductReviewResponseDto> searchReviews(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 執行模糊搜尋
        Page<ProductReview> reviewsPage = productReviewRepository.searchReviews(keyword, pageable);

        // 確保評論的圖片載入
        reviewsPage.getContent().forEach(review -> review.getReviewPhotos().size());

        // 轉換為 DTO
        return reviewsPage.map(this::convertToDTO);
    }

    // 根據評論ID找單個評論
    public ProductReview getReviewById(Integer reviewId) {
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    // 刪除評論
    public void deleteReviewById(Integer reviewId) {
        if (!productReviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review with ID " + reviewId + " not found.");
        }
        productReviewRepository.deleteById(reviewId);
    }

    //=================評分統計=======================
    //評分最高商品
    public List<ProductRatingProjection> getTop5ProductsByAverageRating() {
    	Pageable top5Page = PageRequest.of(0, 3);
        return productReviewRepository.findTop5ProductsByAverageRating(top5Page);
    }

    //評分最高商品種類
    public List<ProductDetailRatingProjection> getTop3ProductDetailsByAverageRating() {
    	Pageable top3Page = PageRequest.of(0, 3);
    	return productReviewRepository.findTop3ProductDetailsByAverageRating(top3Page);
    }
}
