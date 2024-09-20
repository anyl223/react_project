package com.gis.gis.controllers;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.Set;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.tomcat.util.codec.binary.Base64;
import com.gis.gis.exceptions.BadRequestException;

import com.gis.gis.models.Role;
import com.gis.gis.models.User;
import com.gis.gis.payloads.request.EncryptedRequest;
import com.gis.gis.payloads.request.LoginRequest;
import com.gis.gis.payloads.request.SignupRequest;
import com.gis.gis.payloads.response.EncryptedResponse;
import com.gis.gis.payloads.response.LoginResponse;
import com.gis.gis.repositories.RolesRepo;
import com.gis.gis.repositories.UserRepository;
import com.gis.gis.services.UserService;
import com.gis.gis.utils.Json;
import com.gis.gis.utils.OtpLimiter;
import com.gis.gis.utils.SmsThread;
import org.locationtech.jts.io.WKTReader;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  BCryptPasswordEncoder encoder;

  @Autowired
  UserRepository userRepository;

  @Autowired
  OtpLimiter otpLimiter;

  @Autowired
  UserService userService;
  @Autowired
  RolesRepo rolesRepo;

  @PostMapping("/sign-in")
  public <json> EncryptedResponse signIn(@Valid @RequestBody EncryptedRequest req)
      throws Exception {

    LoginRequest loginRequest = Json.deserialize(LoginRequest.class, req.data());

    String username = loginRequest.username();
    String password = loginRequest.password();

    String otp = loginRequest.otp();
    String captcha = loginRequest.captcha();
    String fp = loginRequest.fp();

    userService.validateUsername(username);
    User user = userService.authenticateUser(username, password);
    userService.verifyCaptcha(captcha, user);
    userService.verifyOtp(otp, user);

    String jwt = userService.generateJwt(user, fp);
    return new EncryptedResponse(new LoginResponse(jwt, user));
  }

  @PostMapping(value = "/2fa")
  public <json> EncryptedResponse generateOtp(@Valid @RequestBody EncryptedRequest req,
      HttpServletResponse response) throws Exception {

    LoginRequest loginRequest = Json.deserialize(LoginRequest.class, req.data());
    String username = loginRequest.username();
    String captcha = loginRequest.captcha();
    String password = loginRequest.password();
    userService.validateUsername(username);

    User user = userService.authenticateUser(username, password);
    userService.verifyCaptcha(captcha, user);

    String otp = userService.generateOtp();
    String message =
        "You have reached the limit of 3 OTPs that may be generated in 24 hours. Please use the most recently received OTP to proceed.";
    if (otpLimiter.shouldSendOtp(user.getUsername(), otp)) {
      String mobile = user.getMobile();
      SmsThread.send(otp, mobile);
      message = "An OTP has been sent to your registered mobile number.";
    }
    return new EncryptedResponse(message);
  }

  @PostMapping(value = "/generate-captcha")
  public <json> EncryptedResponse generateCaptcha(@Valid @RequestBody EncryptedRequest req,
      HttpServletResponse response) throws Exception {

    LoginRequest loginRequest = Json.deserialize(LoginRequest.class, req.data());
    String username = loginRequest.username();
    String password = loginRequest.password();
    userService.validateUsername(username);

    User user = userService.authenticateUser(username, password);
    String captcha = userService.assignNewCaptcha(user);
    var image = userService.makeCaptchaImage(captcha, UserService.TEXT_COLOR_LIGHT);
    var baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    byte[] bytes = baos.toByteArray();
    String captchaImgBase64 = Base64.encodeBase64String(bytes);
    return new EncryptedResponse("data:image/png;base64, " + captchaImgBase64);
  }

  @PostMapping("/roles")
  public EncryptedResponse getAllRoles() throws Exception {
    return new EncryptedResponse(rolesRepo.findAllByOrderByRole());
  }



  @PostMapping("/register")
  public String registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws Exception {
    // SignupRequest signUpRequest = Json.deserialize(SignupRequest.class, req.getData());
    String username = signUpRequest.username();
    String email = signUpRequest.email();
    String password = signUpRequest.password();
    String mobile = signUpRequest.mobile();

    if (userRepository.existsByUsername(username)) {
      throw new BadRequestException("Username already exists!");
    }
    if (userRepository.existsByEmail(email)) {
      throw new BadRequestException("Email is already in use!");
    }

    User user = new User(username, email, encoder.encode(password), mobile);
    userRepository.save(user);

    return "Registered";
  }

  @GetMapping("/ping")
  public String ping() {
    return """
          <div style="display: grid; place-items: center; font-size: 4rem;">
            pong 🏓
          </div>
        """;
  }

  private static final String FIXED_PASSWORD = "2?7:no*K394:";
}
