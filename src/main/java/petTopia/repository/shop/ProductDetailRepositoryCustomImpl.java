package petTopia.repository.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import petTopia.model.shop.ProductDetail;

@Repository
public class ProductDetailRepositoryCustomImpl implements ProductDetailRepositoryCustom {

	@PersistenceContext
    private EntityManager entityManager;

	// 模糊搜尋 (多個關鍵字)
	@Override
	public List<ProductDetail> searchProducts(String keywordString){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> productDetail = query.from(ProductDetail.class);

        // 拆分關鍵字
        String[] keywords = keywordString.split(" ");
        List<Predicate> predicates = new ArrayList<>();

        for (String keyword : keywords) {
            predicates.add(cb.like(productDetail.get("name"), "%" + keyword + "%"));
        }

        // 使用 AND 確保符合所有關鍵字
        query.select(productDetail).where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
	};

	
}
