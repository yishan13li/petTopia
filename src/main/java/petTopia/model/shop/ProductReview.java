package petTopia.model.shop;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petTopia.model.user.Member;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_review", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "member_id"}))
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "review_description", length = 255)
    private String reviewDescription;
    
    @Column(name = "review_time", nullable = false)
    private Date reviewTime;
    
}
