package com.example.trituxuser.repository;

import com.example.trituxuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetPasswordToken(String resetpass);

}

