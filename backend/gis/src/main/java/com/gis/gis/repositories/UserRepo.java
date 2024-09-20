package com.gis.gis.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gis.gis.models.Users;

public interface UserRepo extends JpaRepository<Users, Long> {

    Users findByUsername(String username);

    @Query(nativeQuery = true, value = "select * from login_users where role='user' order by id")
    List<Map<String, Object>> getUsers(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Users findFirstByUsername(String username);

}
