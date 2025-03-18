package petTopia.service.user;

import petTopia.model.user.Users;

public interface UsersService {
    Users findById(Integer id);
    void bindOAuth2Account(Integer userId, Users.Provider provider);
} 