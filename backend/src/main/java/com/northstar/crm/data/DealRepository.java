package com.northstar.crm.data;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DealRepository extends JpaRepository<Deal,Long>{List<Deal> findByOwnerEmailIgnoreCase(String email);}
