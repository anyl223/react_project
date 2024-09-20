package com.gis.gis.security.services;

import com.gis.gis.models.User;
import com.gis.gis.models.Users;
import com.gis.gis.repositories.UserRepo;
import com.gis.gis.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityUserService implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Autowired
  UserRepo userrepo;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users user = userrepo.findByUsername(username);
    return SecurityUser.build(user);
  }

}
