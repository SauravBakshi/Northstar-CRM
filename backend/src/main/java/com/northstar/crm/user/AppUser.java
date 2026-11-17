package com.northstar.crm.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity @Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=120) private String name;
    @Email @Column(nullable=false, unique=true, length=180) private String email;
    @NotBlank @Column(nullable=false) private String password;
    @Size(max=40) private String phone;
    @Size(max=80) private String team = "Sales";
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Role role = Role.EMPLOYEE;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getTeam(){return team==null?"Sales":team;} public void setTeam(String v){team=v;}
    public Role getRole(){return role;} public void setRole(Role v){role=v;}
    public Instant getCreatedAt(){return createdAt;}
}
