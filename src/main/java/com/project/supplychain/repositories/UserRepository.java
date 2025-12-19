package com.project.supplychain.repositories;

import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}
