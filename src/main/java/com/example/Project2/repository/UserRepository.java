package com.example.Project2.repository;

import com.example.Project2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>{

    Boolean existsByEmail(String email);
    User findByAccountNumber(String accountNumber);
    Boolean existsByAccountNumber(String accountNumber);
}

//TO BE CONTINUED IT DEPENDS ON THE SERVICES THAT I LL NEED
