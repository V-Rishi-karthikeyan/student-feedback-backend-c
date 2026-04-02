package com.feedbacksystem.service;

import com.feedbacksystem.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    User updateUser(Long id, User userDetails);

    void deleteUser(Long id);

    List<User> getUsersByRole(String role);
}
