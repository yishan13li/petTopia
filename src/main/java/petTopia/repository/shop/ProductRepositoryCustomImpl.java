package petTopia.repository.shop;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductCategory;
import petTopia.model.shop.ProductDetail;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	// 根據條件搜尋商品的總數
	@Override
	public long count(JSONObject obj) {

		String keywordStr = obj.isNull("keyword") ? null : obj.getString("keyword");
		String category = obj.isNull("category") ? null : obj.getString("category");
		String status = obj.isNull("status") ? null : obj.getString("status");

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Product> product = criteriaQuery.from(Product.class);

		// select count(*)
		criteriaQuery = criteriaQuery.select(cb.count(product));
		
		// Product join ProductDetail 
		Join<Product, ProductDetail> detailJoin = product.join("productDetail");
		// ProductDetail join ProductCategory 獲得分類name
		Join<ProductDetail, ProductCategory> categoryJoin = detailJoin.join("productCategory");

		// 拆分關鍵字
		String[] keywords = keywordStr.split(" ");
		List<Predicate> predicates = new ArrayList<>();

		// sql where
		if (keywords != null && keywords.length != 0) {
			for (String keyword : keywords) {
				predicates.add(cb.like(detailJoin.get("name"), "%" + keyword + "%"));
			}
		}
		
		if (category != null && category.length() != 0 && !"所有商品".equals(category)) {
			predicates.add(cb.equal(categoryJoin.get("name"), category));
		}

		if (status != null && status.length() != 0) {
			boolean statusBool = "1".equals(status) || "true".equals(status);
			predicates.add(cb.equal(product.get("status"), statusBool));
		}

		// 合併 sql where
		if (predicates != null && !predicates.isEmpty()) {
			criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		// 回傳結果
		long total = typedQuery.getSingleResult();
		return total;

	};
	
	// 根據條件搜尋商品並分頁
	@Override
	public List<Product> find(JSONObject obj) {

		int start = obj.isNull("start") ? 0 : obj.getInt("start");
		int rows = obj.isNull("rows") ? 0 : obj.getInt("rows");

		String category = obj.isNull("category") ? null : obj.getString("category");
		String keywordStr = obj.isNull("keyword") ? null : obj.getString("keyword");

		// Criteria
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductDetail> criteriaQuery = cb.createQuery(ProductDetail.class);
		Root<ProductDetail> productDetail = criteriaQuery.from(ProductDetail.class);
		
		// ProductDetail join ProductCategory 獲得name
		Join<ProductDetail, ProductCategory> categoryJoin = productDetail.join("productCategory");

		// 拆分關鍵字
		String[] keywords = keywordStr.split(" ");
		List<Predicate> predicates = new ArrayList<>();

		// sql where
		if (category != null && category.length() != 0 && !"所有商品".equals(category)) {
			predicates.add(cb.equal(categoryJoin.get("name"), category));
		}

		if (keywords != null && keywords.length != 0) {
			for (String keyword : keywords) {
				predicates.add(cb.like(productDetail.get("name"), "%" + keyword + "%"));
			}
		}
		
		// 加入 `EXISTS` 子查詢（確保至少有一個 `status = true` 的 Product）
		Subquery<Long> existsSubquery = criteriaQuery.subquery(Long.class);
		Root<Product> product = existsSubquery.from(Product.class);

		existsSubquery
		    .select(cb.literal(1L))  // 只回傳是否存在
		    .where(
		        cb.equal(product.get("productDetail"), productDetail), // 關聯條件
		        cb.equal(product.get("status"), true) // 只篩選 status = true 的 Product
		    );

		// WHERE EXISTS (子查詢)
		predicates.add(cb.exists(existsSubquery));
		
		// 合併 sql where
		if (predicates != null && !predicates.isEmpty()) {
			criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		// 分頁 => 從跳握start筆開始，取出rows筆資料
		TypedQuery<ProductDetail> typedQuery = entityManager.createQuery(criteriaQuery).setFirstResult(start);
		if (rows != 0) {
			typedQuery = typedQuery.setMaxResults(rows);
		}

		// 回傳結果
		List<ProductDetail> resultList = typedQuery.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return null;
		} else {
			return null;
		}

	};

	
}
