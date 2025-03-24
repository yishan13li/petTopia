package petTopia.model.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "member")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Member {

	@Id
	private Integer id; // 與 user id 相同

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id")
	@JsonIgnore
	private User user; // 用這個屬性來建立與 Users 的關聯

	@Column(name = "name", nullable = true)
	private String name;

	@Column(nullable = true)
	private String phone;

	@Column(name = "birthdate")
	private LocalDate birthdate;

	@Column(nullable = false)
	private Boolean gender = false; // false = 男性, true = 女性

	private String address;

	@Lob
	@Column(name = "profile_photo")
	private byte[] profilePhoto;

	@Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
	private Boolean status = false; // 預設為未認證 (0)

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	// 這裡不需要額外的 user_id 外鍵欄位，因為 id 已經作為外鍵連結
	// 不需要額外的 @ManyToOne 或 @JoinColumn

	// getters 和 setters 會自動由 @Data 提供

	
	@Transient
	private String profilePhotoBase64;

}
