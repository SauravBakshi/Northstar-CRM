package com.northstar.crm.auth;

import com.northstar.crm.security.JwtService;
import com.northstar.crm.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager auth; private final JwtService jwt;
 public AuthController(UserRepository users,PasswordEncoder encoder,AuthenticationManager auth,JwtService jwt){this.users=users;this.encoder=encoder;this.auth=auth;this.jwt=jwt;}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record RegisterRequest(@NotBlank @Size(max=120) String name,@Email @NotBlank String email,@Size(max=40) String phone,@NotBlank @Size(min=8,max=72) String password,@NotBlank String confirmPassword){}
 @PostMapping("/register") public Map<String,Object> register(@Valid @RequestBody RegisterRequest r){if(!r.password().equals(r.confirmPassword()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Passwords do not match");if(users.existsByEmailIgnoreCase(r.email()))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");AppUser u=new AppUser();u.setName(r.name());u.setEmail(r.email().trim().toLowerCase());u.setPhone(r.phone());u.setPassword(encoder.encode(r.password()));u.setRole(Role.EMPLOYEE);return response(users.save(u));}
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody LoginRequest r){try{auth.authenticate(new UsernamePasswordAuthenticationToken(r.email(),r.password()));}catch(Exception ex){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");}return users.findByEmailIgnoreCase(r.email()).map(this::response).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
 private Map<String,Object> response(AppUser u){return Map.of("token",jwt.create(u),"user",Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"phone",u.getPhone()==null?"":u.getPhone(),"role",u.getRole().name()));}
}
