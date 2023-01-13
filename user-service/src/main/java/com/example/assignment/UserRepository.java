package com.example.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserName(String userName);//to find by any other key other than primary key we use this

    boolean existsByUserName(String userName);
}
