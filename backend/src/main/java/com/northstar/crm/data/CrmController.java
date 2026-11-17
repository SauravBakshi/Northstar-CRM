package com.northstar.crm.data;

import com.northstar.crm.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api")
public class CrmController {
 private final CustomerRepository customers; private final LeadRepository leads; private final DealRepository deals;
 private final CrmTaskRepository tasks; private final UserRepository users; private final PasswordEncoder encoder;
 public CrmController(CustomerRepository c,LeadRepository l,DealRepository d,CrmTaskRepository t,UserRepository u,PasswordEncoder e){customers=c;leads=l;deals=d;tasks=t;users=u;encoder=e;}
 private boolean manager(Authentication a){return a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN")||x.getAuthority().equals("ROLE_MANAGER"));}
 private void requireOwner(String owner,Authentication a){if(!manager(a)&&(owner==null||!a.getName().equalsIgnoreCase(owner)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You can only access records assigned to you");}
 @GetMapping("/customers") public List<Customer> customers(Authentication a){return manager(a)?customers.findAll():customers.findByOwnerEmailIgnoreCase(a.getName());}
 @PostMapping("/customers") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public Customer addCustomer(@RequestBody Customer c,Authentication a){c.setId(null);c.setOwnerEmail(c.getOwnerEmail()==null?a.getName():assignedEmail(c.getOwnerEmail()));return customers.save(c);}
 @PutMapping("/customers/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public Customer updateCustomer(@PathVariable Long id,@RequestBody Customer c){Customer old=customers.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));c.setId(id);c.setOwnerEmail(c.getOwnerEmail()==null?old.getOwnerEmail():assignedEmail(c.getOwnerEmail()));return customers.save(c);}
 @DeleteMapping("/customers/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public void deleteCustomer(@PathVariable Long id){customers.deleteById(id);}
 @GetMapping("/leads") public List<Lead> leads(Authentication a){return manager(a)?leads.findAll():leads.findByOwnerEmailIgnoreCase(a.getName());}
 @PostMapping("/leads") public Lead addLead(@RequestBody Lead l,Authentication a){l.setId(null);String email=manager(a)&&l.getOwnerEmail()!=null?assignedEmail(l.getOwnerEmail()):a.getName();setLeadOwner(l,email);return leads.save(l);}
 @PutMapping("/leads/{id}") public Lead updateLead(@PathVariable Long id,@RequestBody Lead input,Authentication a){Lead old=leads.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));requireOwner(old.getOwnerEmail(),a);input.setId(id);String email=manager(a)&&input.getOwnerEmail()!=null?assignedEmail(input.getOwnerEmail()):old.getOwnerEmail();setLeadOwner(input,email);return leads.save(input);}
 @DeleteMapping("/leads/{id}") public void deleteLead(@PathVariable Long id,Authentication a){Lead old=leads.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));requireOwner(old.getOwnerEmail(),a);leads.delete(old);}
 @GetMapping("/deals") public List<Deal> deals(Authentication a){return manager(a)?deals.findAll():deals.findByOwnerEmailIgnoreCase(a.getName());}
 @PostMapping("/deals") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public Deal addDeal(@RequestBody Deal d,Authentication a){d.setId(null);String email=d.getOwnerEmail()==null?a.getName():assignedEmail(d.getOwnerEmail());d.setOwnerEmail(email);d.setOwner(assignedName(email));return deals.save(d);}
 @PutMapping("/deals/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public Deal updateDeal(@PathVariable Long id,@RequestBody Deal d){Deal old=deals.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));d.setId(id);String email=d.getOwnerEmail()==null?old.getOwnerEmail():assignedEmail(d.getOwnerEmail());d.setOwnerEmail(email);d.setOwner(assignedName(email));return deals.save(d);}
 @DeleteMapping("/deals/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public void deleteDeal(@PathVariable Long id){deals.deleteById(id);}
 @GetMapping("/tasks") public List<CrmTask> tasks(Authentication a){return manager(a)?tasks.findAll():tasks.findByOwnerEmailIgnoreCase(a.getName());}
 @PostMapping("/tasks") public CrmTask addTask(@RequestBody CrmTask t,Authentication a){t.setId(null);t.setOwnerEmail(manager(a)&&t.getOwnerEmail()!=null?assignedEmail(t.getOwnerEmail()):a.getName());return tasks.save(t);}
 @PutMapping("/tasks/{id}") public CrmTask updateTask(@PathVariable Long id,@RequestBody CrmTask t,Authentication a){CrmTask old=tasks.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));requireOwner(old.getOwnerEmail(),a);t.setId(id);t.setOwnerEmail(manager(a)&&t.getOwnerEmail()!=null?assignedEmail(t.getOwnerEmail()):old.getOwnerEmail());return tasks.save(t);}
 @DeleteMapping("/tasks/{id}") public void deleteTask(@PathVariable Long id,Authentication a){CrmTask old=tasks.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));requireOwner(old.getOwnerEmail(),a);tasks.delete(old);}
 @GetMapping("/users") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public List<UserView> team(){return users.findAll().stream().map(CrmController::view).toList();}
 @PostMapping("/users") @PreAuthorize("hasRole('ADMIN')") public UserView addUser(@Valid @RequestBody NewUser r){if(users.existsByEmailIgnoreCase(r.email()))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");AppUser u=new AppUser();u.setName(r.name().trim());u.setEmail(r.email().trim().toLowerCase());u.setPhone(r.phone());u.setPassword(encoder.encode(r.password()));u.setRole(r.role()==null?Role.EMPLOYEE:r.role());u.setTeam(r.team()==null||r.team().isBlank()?"Sales":r.team().trim());return view(users.save(u));}
 @PutMapping("/users/{id}/role") @PreAuthorize("hasRole('ADMIN')") public UserView role(@PathVariable Long id,@RequestBody RoleChange r,Authentication auth){AppUser u=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(u.getEmail().equalsIgnoreCase(auth.getName())&&r.role()!=Role.ADMIN)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot remove your own admin role");u.setRole(r.role());return view(users.save(u));}
 @DeleteMapping("/users/{id}") @PreAuthorize("hasRole('ADMIN')") public void deleteUser(@PathVariable Long id,Authentication auth){AppUser u=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(u.getEmail().equalsIgnoreCase(auth.getName()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot delete your own account");users.delete(u);}
 private static UserView view(AppUser u){return new UserView(u.getId(),u.getName(),u.getEmail(),u.getPhone(),u.getRole().name(),u.getTeam(),"Active");}
 private AppUser assignedUser(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a team member who has an account"));}
 private String assignedEmail(String email){return assignedUser(email).getEmail();}
 private String assignedName(String email){return assignedUser(email).getName();}
 private void setLeadOwner(Lead lead,String email){AppUser owner=assignedUser(email);lead.setOwnerEmail(owner.getEmail());lead.setOwner(owner.getName());}
 public record RoleChange(Role role){}
 public record UserView(Long id,String name,String email,String phone,String role,String team,String status){}
 public record NewUser(@NotBlank @Size(max=120) String name,@Email @NotBlank String email,@Size(max=40) String phone,@NotBlank @Size(min=8,max=72) String password,Role role,@Size(max=80) String team){}
}
