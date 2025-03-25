package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.model.user.Admin;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.AdminRepository;
import petTopia.jwt.JwtUtil;

import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@Primary
public class AdminService implements UserDetailsService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findFirstByEmailOrderByIdDesc(email);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("用戶不存在");
        }
        
        User user = userOpt.get();
        Optional<Admin> adminOpt = adminRepository.findById(user.getId());
        if (!adminOpt.isPresent()) {
            throw new UsernameNotFoundException("管理員不存在");
        }
        
        Admin admin = adminOpt.get();
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(admin.getRole().name())
                .build();
    }

    public Map<String, Object> adminLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findFirstByEmailOrderByIdDesc(email);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("用戶不存在");
        }
        
        User user = userOpt.get();
        Optional<Admin> adminOpt = adminRepository.findById(user.getId());
        if (!adminOpt.isPresent()) {
            throw new UsernameNotFoundException("管理員不存在");
        }
        
        Admin admin = adminOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密碼錯誤");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", admin.getRole().name());

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), admin.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("admin", admin);
        return response;
    }

    @Transactional
    public void initSuperAdmin(String email, String password) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmailVerified(true);
            user.setLocalEnabled(true);
            user = userRepository.save(user);
            
            Admin admin = new Admin();
            admin.setUsers(user);
            admin.setName("Super Admin");
            admin.setRole(Admin.AdminRole.SA);
            admin.setRegistrationDate(LocalDateTime.now());
            adminRepository.save(admin);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getAllMembers() {
        return userRepository.findByUserRole(User.UserRole.MEMBER);
    }
    
    public List<User> getAllVendors() {
        return userRepository.findByUserRole(User.UserRole.VENDOR);
    }
} 