package com.example.trituxuser.repository;

import com.example.trituxuser.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {
    public Role findByName(String name);
}
