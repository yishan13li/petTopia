package petTopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import petTopia.model.user.Users;
import petTopia.repository.user.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("找不到使用者：" + email);
        }
        Users user = users.get(0); // 取第一個匹配的用戶
        if (user == null) {
            throw new UsernameNotFoundException("找不到使用者：" + email);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
        
        if (user.isAdmin()) {
            if (user.isSuperAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            }
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(
            user.getEmail(),
            user.getPassword(),
            user.isEmailVerified(),
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            authorities
        );
    }
} 