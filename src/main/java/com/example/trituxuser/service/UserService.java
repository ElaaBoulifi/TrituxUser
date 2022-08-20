package com.example.trituxuser.service;

import com.example.trituxuser.entity.RegistrationForm;
import com.example.trituxuser.entity.Role;
import com.example.trituxuser.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    void saveRole(Role role);
    void affecterRoleaUser(String username,String rolename);
    User getUser(String username);
    List<User> getUsers();
    void deleteUser(int id);
    void updateResetPasswordToken(String token,String email);
    User getUserByResetPasswordToken(String token);
    String updatePassword(String token,String newPassword);
    void signup(RegistrationForm registrationForm);

}
