package com.gis.gis.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.gis.gis.exceptions.UnauthorizedException;
import com.gis.gis.models.User;
import com.gis.gis.models.Users;
import com.gis.gis.repositories.UserRepo;
import com.gis.gis.repositories.UserRepository;
import com.gis.gis.security.services.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Autowired
  private JwtActions jwtActions;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserRepo userrepo;
  @Autowired
  private SecurityUserService securityUserService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String fpFromRequest = request.getHeader("fp");
    try {
      String token = parseJwt(request);
      var optJwt = jwtActions.validate(token, fpFromRequest);
      if (optJwt.isEmpty()) {
        return;
      }
      Jwt jwt = optJwt.get();
      String username = jwt.subject();
      Users user = userrepo.findFirstByUsername(username);
      matchTimestampWithDb(user, jwt.timestamp());
      setAuthentication(request, username);
      request.setAttribute("user", user);
    } catch (Exception ex) {
      System.err.println("--- Caught in AuthTokenFilter ---");
      ex.printStackTrace();
      System.err.println("--- x ---");
    } finally {
      filterChain.doFilter(request, response);
    }

  }

  private void setAuthentication(HttpServletRequest request, String username) {
    UserDetails userDetails = securityUserService.loadUserByUsername(username);
    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  private void matchTimestampWithDb(Users user, long timestamp) throws UnauthorizedException {
    if (activeProfile.equals("dev")) {
      return;
    }
    long differenceInMillis = user.getJwtTimestamp() - timestamp;
    if (Math.abs(differenceInMillis) > 2000) {
      System.out.println("! M T");
      throw new UnauthorizedException("Invalid token");
    }
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }
    return null;
  }

}
