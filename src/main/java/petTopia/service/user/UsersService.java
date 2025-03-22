package petTopia.service.user;

import petTopia.model.user.User;

public interface UsersService {
    User findById(Integer id);
    void bindOAuth2Account(Integer userId, User.Provider provider);
} 