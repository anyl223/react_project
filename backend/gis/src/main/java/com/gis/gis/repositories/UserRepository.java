package com.gis.gis.repositories;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gis.gis.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
  User findById(long id);

  User deleteById(long id);

  User findByUsername(String username);

  User findFirstByUsername(String username);

  Boolean existsByUsername(String username);

  Page<User> findAllByIdNot(long userid, Pageable pageable);

  Boolean existsByEmail(String email);

  Page<User> findAll(Pageable pageable);

  @Query(nativeQuery = true, value = """
        SELECT u.* FROM users u
        WHERE (?1 IS NULL OR u\\:\\:text ILIKE ?1)
        ORDER BY id
      """)
  Page<Map<String, Object>> findByFilters(String search, Pageable pageable);

  @Query(nativeQuery = true, value = "SELECT * FROM users txt WHERE LOWER(txt\\:\\:text) ILIKE ?1")
  Page<User> search(String query, Pageable pageable);

  @Query(nativeQuery = true, value = """
      SELECT fcm_token
      FROM users
      WHERE (
        fcm_token IS NOT NULL AND
        (?1\\:\\:TEXT IS NULL OR districtcode IN (?1))
      )
        """)
  List<String> getFcmTokens(List<String> districts);


}
