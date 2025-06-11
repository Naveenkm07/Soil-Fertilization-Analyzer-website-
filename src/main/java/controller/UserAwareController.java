package controller;

import model.User;

public interface UserAwareController {
    void setCurrentUser(User user);
} 