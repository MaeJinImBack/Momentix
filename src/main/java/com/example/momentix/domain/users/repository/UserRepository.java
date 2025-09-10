package com.example.momentix.domain.users.repository;

import com.example.momentix.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    boolean existsByBusinessNumber(String businessNumber);;

    Optional<Users> findBySignIn_Username(String username);
}
