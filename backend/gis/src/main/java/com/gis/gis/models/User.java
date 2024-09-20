package com.gis.gis.models;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email") })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String otp;
    @JsonIgnore
    private Integer otpCount;
    @JsonIgnore
    private Long otpTimestamp;
    @JsonIgnore
    private String captcha;
    private String mobile;
    @JsonIgnore
    public Long jwtTimestamp;

    public Long getJwtTimestamp() {
        return jwtTimestamp;
    }

    public void setJwtTimestamp(Long jwtTimestamp) {
        this.jwtTimestamp = jwtTimestamp;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role_mapping", joinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Integer getOtpCount() {
        return otpCount;
    }

    public void setOtpCount(Integer otpCount) {
        this.otpCount = otpCount;
    }

    public Long getOtpTimestamp() {
        return otpTimestamp;
    }

    public void setOtpTimestamp(Long otpTimestamp) {
        this.otpTimestamp = otpTimestamp;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String moblie) {
        this.mobile = moblie;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String mobile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

}
