package petTopia.repository.shop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
		String isProductDiscount = obj.isNull("isProductDiscount") ? null : obj.getString("isProductDiscount");
		String stockQuantityStr = obj.optString("stockQuantityLessThan", "").trim();
		Integer stockQuantityLessThan = stockQuantityStr.isEmpty() ? null : Integer.parseInt(stockQuantityStr);
		String startDate = obj.isNull("startDate") ? null : obj.getString("startDate");
		String endDate = obj.isNull("endDate") ? null : obj.getString("endDate");
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<Product> product = criteriaQuery.from(Product.class);

		// select count(*)
		criteriaQuery = criteriaQuery.select(cb.count(product));
		
		// Product join ProductDetail 
		Join<Product, ProductDetail> detailJoin = product.join("productDetail");
		// ProductDetail join ProductCategory 獲得分類name
		Join<ProductDetail, ProductCategory> categoryJoin = detailJoin.join("productCategory");

		List<Predicate> predicates = new ArrayList<>();

		// sql where
		if (keywordStr != null && keywordStr.length() != 0) {
			// 拆分關鍵字
			String[] keywords = keywordStr.split(" ");
			if (keywords != null && keywords.length != 0) {
				for (String keyword : keywords) {
					predicates.add(cb.like(detailJoin.get("name"), "%" + keyword + "%"));
				}
			}
		}
		
		if (category != null && category.length() != 0) {
			predicates.add(cb.equal(categoryJoin.get("name"), category));
		}

		if (status != null && status.length() != 0) {
			boolean statusBool = "1".equals(status) || "true".equals(status);
			predicates.add(cb.equal(product.get("status"), statusBool));
		}
		
		if (isProductDiscount != null && isProductDiscount.length() != 0) {
			boolean isProductDiscountBool = "1".equals(isProductDiscount) || "true".equals(isProductDiscount);
			if (isProductDiscountBool ) 
				predicates.add(cb.isNotNull(product.get("discountPrice")));
			else
				predicates.add(cb.isNull(product.get("discountPrice")));
		}
		
		if (stockQuantityLessThan != null && stockQuantityLessThan != 0) {
			predicates.add(cb.lessThanOrEqualTo(product.get("stockQuantity"), stockQuantityLessThan));
		}
		
		if (startDate != null && !startDate.isEmpty()) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    Date startDateParsed = null;
			try {
				startDateParsed = sdf.parse(startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			} // 將字串轉為 java.util.Date
		    predicates.add(cb.greaterThanOrEqualTo(product.get("createdTime"), startDateParsed));
		}

		if (endDate != null && !endDate.isEmpty()) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    Date endDateParsed = null;
			try {
				endDateParsed = sdf.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			} // 將字串轉為 java.util.Date
		    predicates.add(cb.lessThanOrEqualTo(product.get("createdTime"), endDateParsed));
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

		String keywordStr = obj.isNull("keyword") ? null : obj.getString("keyword");
		String category = obj.isNull("category") ? null : obj.getString("category");
		String status = obj.isNull("status") ? null : obj.getString("status");
		String isProductDiscount = obj.isNull("isProductDiscount") ? null : obj.getString("isProductDiscount");
		String stockQuantityStr = obj.optString("stockQuantityLessThan", "").trim();
		Integer stockQuantityLessThan = stockQuantityStr.isEmpty() ? null : Integer.parseInt(stockQuantityStr);
		String startDate = obj.isNull("startDate") ? null : obj.getString("startDate");
		String endDate = obj.isNull("endDate") ? null : obj.getString("endDate");
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = cb.createQuery(Product.class);
		Root<Product> product = criteriaQuery.from(Product.class);
		
		// Product join ProductDetail 
		Join<Product, ProductDetail> detailJoin = product.join("productDetail");
		// ProductDetail join ProductCategory 獲得分類name
		Join<ProductDetail, ProductCategory> categoryJoin = detailJoin.join("productCategory");

		List<Predicate> predicates = new ArrayList<>();

		// sql where
		if (keywordStr != null && keywordStr.length() != 0) {
			// 拆分關鍵字
			String[] keywords = keywordStr.split(" ");
			if (keywords != null && keywords.length != 0) {
				for (String keyword : keywords) {
					predicates.add(cb.like(detailJoin.get("name"), "%" + keyword + "%"));
				}
			}
		}
		
		if (category != null && category.length() != 0) {
			predicates.add(cb.equal(categoryJoin.get("name"), category));
		}

		if (status != null && status.length() != 0) {
			boolean statusBool = "1".equals(status) || "true".equals(status);
			predicates.add(cb.equal(product.get("status"), statusBool));
		}
		
		if (isProductDiscount != null && isProductDiscount.length() != 0) {
			boolean isProductDiscountBool = "1".equals(isProductDiscount) || "true".equals(isProductDiscount);
			if (isProductDiscountBool ) 
				predicates.add(cb.isNotNull(product.get("discountPrice")));
			else
				predicates.add(cb.isNull(product.get("discountPrice")));
		}
		
		if (stockQuantityLessThan != null && stockQuantityLessThan != 0) {
			predicates.add(cb.lessThanOrEqualTo(product.get("stockQuantity"), stockQuantityLessThan));
		}
		
		if (startDate != null && !startDate.isEmpty()) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    Date startDateParsed = null;
			try {
				startDateParsed = sdf.parse(startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			} // 將字串轉為 java.util.Date
		    predicates.add(cb.greaterThanOrEqualTo(product.get("createdTime"), startDateParsed));
		}

		if (endDate != null && !endDate.isEmpty()) {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    Date endDateParsed = null;
			try {
				endDateParsed = sdf.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			} // 將字串轉為 java.util.Date
		    predicates.add(cb.lessThanOrEqualTo(product.get("createdTime"), endDateParsed));
		}

		// 合併 sql where
		if (predicates != null && !predicates.isEmpty()) {
			criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
		}

		// 分頁 => 從跳握start筆開始，取出rows筆資料
		TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery).setFirstResult(start);
		if (rows != 0) {
			typedQuery = typedQuery.setMaxResults(rows);
		}

		// 回傳結果
		List<Product> resultList = typedQuery.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList;
		} else {
			return null;
		}

	};

	
}
