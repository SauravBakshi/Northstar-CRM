package com.northstar.crm.data;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerRepository extends JpaRepository<Customer,Long>{List<Customer> findByOwnerEmailIgnoreCase(String email);}
