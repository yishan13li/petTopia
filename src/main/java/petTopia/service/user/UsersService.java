package petTopia.service.user;

import petTopia.model.user.Users;

public interface UsersService {
    void bindOAuth2Account(Integer userId, Users.Provider provider);
} 