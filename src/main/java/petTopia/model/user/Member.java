package petTopia.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "member")
public class Member {
	
    @Id
    @Column(name = "id")
    private int id; // 會員ID，對應到 Users 表中的 UserId
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)  // 外來鍵關聯
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;  // 對應到 Users 表中的 UserId（外來鍵）
    
    @Column(name = "name", nullable = false)
    private String name; // 會員姓名
    
    @Column(name = "phone", nullable = false)
    private String phone; // 會員電話，必填
    
    @Column(name = "address")
    private String address; // 會員地址
}
