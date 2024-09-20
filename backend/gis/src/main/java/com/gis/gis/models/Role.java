package com.gis.gis.models;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer roleId;
  @Column(nullable = false, length = 45)
  private String role;

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Role() {}

  public Role(Integer roleId, String role) {
    this.roleId = roleId;
    this.role = role;
  }

  public Role(Integer roleId) {
    this.roleId = roleId;
  }


}
