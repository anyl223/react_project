package com.gis.gis.services;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import com.gis.gis.exceptions.BadRequestException;
import com.gis.gis.models.User;
import com.gis.gis.models.Users;
import com.gis.gis.repositories.UserRepo;
import com.gis.gis.repositories.UserRepository;
import com.gis.gis.security.jwt.Jwt;
import com.gis.gis.security.jwt.JwtActions;
import com.gis.gis.utils.OtpLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kotlin.text.Regex;

@Service
public class UserService {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  BCryptPasswordEncoder encoder;

  @Autowired
  UserRepository userRepo;

  @Autowired
  UserRepo userrepo;

  @Autowired
  JwtActions jwtActions;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  OtpLimiter otpLimiter;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  static final String USERNAME_REGEX = "^(?=.{3,40}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
  static final String OTP_REGEX = "^[0-9]{6}$";
  static final String CAPTCHA_REGEX = "^[0-9a-zA-Z]{6}$";
  static final String CAPTCHA_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

  public void validateUsername(String username) throws Exception {
    if (StringUtils.isBlank(username) || !isValidRegex(username, USERNAME_REGEX)) {
      throw new BadRequestException("Invalid username");
    }
  }

  public void validatePassword(String password) throws Exception {
    if (StringUtils.isBlank(password) || password.length() < 8) {
      throw new BadRequestException("Invalid password");
    }
  }

  public boolean isValidRegex(String inputString, String pattern) {
    var regex = new Regex(pattern);
    return regex.matches(inputString);
  }

  public User authenticateUser(String username, String password) throws Exception {
    Authentication authentication = null;

    try {
      authentication = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return userRepo.findByUsername(username);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new BadRequestException("Invalid username or password");
    }
  }

  public Users authenticateUsers(String username, String password) throws Exception {
    Authentication authentication = null;

    try {
      authentication = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return userrepo.findByUsername(username);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new BadRequestException("Invalid username or password");
    }
  }

  public void verifyOtp(String otp, User user) throws Exception {
    if (StringUtils.isBlank(otp) || !isValidRegex(otp, OTP_REGEX)) {
      throw new BadRequestException("Invalid OTP");
    }
    if (!user.getOtp().equals(otp)) {
      throw new BadRequestException("Incorrect OTP");
    }
  }

  public void verifyCaptcha(String captcha, User user) throws Exception {
    if (StringUtils.isBlank(captcha) || !isValidRegex(captcha, CAPTCHA_REGEX)) {
      throw new BadRequestException("Invalid captcha");
    }
    if (!user.getCaptcha().equals(captcha)) {
      throw new BadRequestException("Incorrect captcha");
    }
  }

  public String assignNewCaptcha(User user) {
    String captcha = activeProfile.equals("dev") ? "hahaha" : generateCaptcha(6);
    jdbcTemplate.update("UPDATE users SET captcha = ? WHERE id = ?", captcha, user.getId());
    return captcha;
  }

  public String generateJwt(User user, String fp) {
    long now = System.currentTimeMillis();
    String jwt = jwtActions.generate(new Jwt(user.getUsername(), fp, now));
    jdbcTemplate.update("""
        UPDATE users SET jwt_timestamp = ? WHERE id = ?
        """, now, user.getId());
    return jwt;
  }

  public String generateJwt(Users user, String fp) {
    long now = System.currentTimeMillis();
    String jwt = jwtActions.generate(new Jwt(user.getUsername(), fp, now));
    jdbcTemplate.update("""
        UPDATE login_users SET jwt_timestamp = ? WHERE id = ?
        """, now, user.getId());
    return jwt;
  }

  private String generateCaptcha(int captchaLength) {
    StringBuffer captchaBuffer = new StringBuffer();
    Random random = new Random();
    while (captchaBuffer.length() < captchaLength) {
      int index = random.nextInt(CAPTCHA_ALPHABET.length());
      captchaBuffer.append(CAPTCHA_ALPHABET.charAt(index));
    }
    return captchaBuffer.toString();
  }

  public String generateOtp() {
    return activeProfile.equals("dev") ? "000000"
        : new DecimalFormat("000000").format(new Random().nextInt(999999));
  }

  public BufferedImage makeCaptchaImage(String captcha, Color textColor) {
    var image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    graphics.setComposite(AlphaComposite.Clear);
    graphics.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    graphics.setComposite(AlphaComposite.Src);
    graphics.setFont(FONT);
    graphics.setColor(textColor);
    FontMetrics metrics = graphics.getFontMetrics(FONT);
    int x = (IMAGE_WIDTH - metrics.stringWidth(captcha) - 6) / 2;
    int y = ((IMAGE_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent() - 4;
    graphics.drawString(captcha, x, y);
    graphics.dispose();
    return image;
  }

  private static int IMAGE_WIDTH = 0;
  private static int IMAGE_HEIGHT = 0;
  private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 48);
  public static final Color TEXT_COLOR_LIGHT = new Color(255, 255, 255);
  public static final Color TEXT_COLOR_DARK = new Color(54, 64, 79);

  static {
    var image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    graphics.setFont(FONT);
    graphics.setColor(TEXT_COLOR_LIGHT);
    FontMetrics metrics = graphics.getFontMetrics(FONT);
    var width = metrics.stringWidth("WWWWWW");
    IMAGE_WIDTH = (width) + ((int) Math.ceil(width / 3d));
    IMAGE_HEIGHT = metrics.getHeight() + 12;
    graphics.dispose();
    image.flush();
  }

}
