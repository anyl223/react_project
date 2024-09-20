package com.gis.gis.repositories;

import java.util.List;

import com.gis.gis.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepo extends JpaRepository<Role, Integer> {

  List<Role> findAllByOrderByRole();

}
