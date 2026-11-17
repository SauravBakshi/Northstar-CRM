package com.northstar.crm.data;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CrmTaskRepository extends JpaRepository<CrmTask,Long>{List<CrmTask> findByOwnerEmailIgnoreCase(String email);}
