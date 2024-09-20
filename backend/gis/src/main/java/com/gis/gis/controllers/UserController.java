package com.gis.gis.controllers;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import com.gis.gis.exceptions.BadRequestException;
import com.gis.gis.models.Role;
import com.gis.gis.models.User;
import com.gis.gis.payloads.request.EncryptedRequest;
import com.gis.gis.payloads.request.Id;
import com.gis.gis.payloads.request.Register;
import com.gis.gis.payloads.request.Report;
import com.gis.gis.payloads.response.EncryptedResponse;
import com.gis.gis.repositories.UserRepository;
import com.gis.gis.services.UserService;
import com.gis.gis.utils.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  UserRepository userRepo;

  @Autowired
  UserService userService;

  @Autowired
  BCryptPasswordEncoder encoder;

  @PostMapping(value = "/register")
  public <json> EncryptedResponse registerUser(@RequestBody EncryptedRequest req)
      throws Exception {
    var body = Json.deserialize(Register.class, req.data());
    User user = new User();
    if (body.roleId() == null) {
      throw new BadRequestException("Invalid role or centre");
    }
   
    user.setUsername(body.mobile());
    user.setEmail(body.email());
    user.setPassword(encoder.encode(body.password()));
    user.setMobile(body.mobile());
    user.setRoles(Set.of(new Role(body.roleId())));
    
    try {
      userRepo.save(user);
    } catch (DataIntegrityViolationException ex) {
      throw new BadRequestException("Given details have already been registered.");
    }
    return new EncryptedResponse("success");
  }

  @PostMapping("/get-users")
  public EncryptedResponse table(@RequestBody EncryptedRequest req)
      throws Exception {
    var reportReq = Json.deserialize(Report.class, req.data());
    var pagination = reportReq.pagination();
    var searchQuery =
        StringUtils.isBlank(reportReq.search()) ? null : "%" + reportReq.search() + "%";
    var result = userRepo.findByFilters(searchQuery,
        PageRequest.of(pagination.page(), pagination.size()));
    return new EncryptedResponse(result);
  }

  
  


  @PostMapping(value = "/edit")
  public <json> EncryptedResponse editUser(@RequestBody EncryptedRequest req)
  throws Exception {
  var body = Json.deserialize(Register.class, req.data());
  if (body.roleId() == null) {
  throw new BadRequestException("Invalid role");
  }
  var user = userRepo.findById(body.id());
  user.setUsername(body.username());
  user.setEmail(body.email());
  if (StringUtils.isNotBlank(body.password())) {
  user.setPassword(encoder.encode(body.password()));
  }
  user.setMobile(body.mobile());
  user.getRoles().clear();
  user.getRoles().add(new Role(body.roleId()));
  try {
  userRepo.save(user);
  return new EncryptedResponse("edited");
  } catch (DataIntegrityViolationException ex) {
  throw new BadRequestException("Username or email already exists.");
  }
  }

  @PostMapping("/delete")
  @Transactional
  public <json> EncryptedResponse deleteUser(@RequestBody EncryptedRequest req)
      throws Exception {
    var body = Json.deserialize(Id.class, req.data());
    userRepo.deleteById(body.id());
    return new EncryptedResponse("deleted");
  }

 

  private static final String FIXED_PASSWORD = "2?7:no*K394:";

}
