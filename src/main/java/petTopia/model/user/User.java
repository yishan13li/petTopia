package petTopia.model.user;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
public class Users {
========
public class User {
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "user_role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "is_super_admin")
    private Boolean isSuperAdmin = false;

    @Column(name = "admin_level")
    @Min(0)
    @Max(1)
    private Integer adminLevel = 0;

    @Column(name = "provider", length = 20)
    @Enumerated(EnumType.STRING)
    private Provider provider = Provider.LOCAL;
<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
========

    @Column(name = "local_enabled")
    private boolean localEnabled = false;
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java

    public enum UserRole {
        MEMBER,
        VENDOR,
        ADMIN
    }

    public enum Provider {
        LOCAL,
        GOOGLE
    }

<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
    public Users() {
    }

    public Users(String password, String email, UserRole userRole) {
========
    public User() {
    }

    public User(String password, String email, UserRole userRole) {
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java
        this.password = password;
        this.email = email;
        this.userRole = userRole;
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.userRole);
    }

    public boolean isSuperAdmin() {
        return isAdmin() && Boolean.TRUE.equals(this.isSuperAdmin);
    }

    // 權限管理
    private static final Set<String> ADMIN_PERMISSIONS = Set.of(Permissions.USER_MANAGE, Permissions.COUPON_MANAGE);

    public boolean hasPermission(String permission) {
        if (!isAdmin())
            return false;
        if (isSuperAdmin())
            return true;
        return ADMIN_PERMISSIONS.contains(permission);
    }

<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
    public boolean canManageUser(Users targetUser) {
========
    public boolean canManageUser(User targetUser) {
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java
        return isAdmin() && (!targetUser.isAdmin() || isSuperAdmin());
    }

    @Override
    public String toString() {
        return "UsersBean{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userRole=" + userRole +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
        Users usersBean = (Users) o;
========
        User usersBean = (User) o;
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java
        return Objects.equals(id, usersBean.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final class Permissions {
        public static final String USER_MANAGE = "USER_MANAGE";
        public static final String COUPON_MANAGE = "COUPON_MANAGE";
    }

    public void setProvider(String provider) {
        try {
            this.provider = Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.provider = Provider.LOCAL;
        }
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }
<<<<<<<< HEAD:src/main/java/petTopia/model/user/Users.java
========

    public boolean isLocalEnabled() {
        return localEnabled;
    }

    public void setLocalEnabled(boolean localEnabled) {
        this.localEnabled = localEnabled;
    }
>>>>>>>> f3/vendor:src/main/java/petTopia/model/user/User.java
}
