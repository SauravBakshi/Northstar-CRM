package com.northstar.crm.data;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="crm_tasks")
public class CrmTask {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String title; private String person; private String dueDate; private String priority="Medium"; private Boolean done=false; private String type; private String relatedTo; private String notes; private String ownerEmail; private Instant createdAt=Instant.now();
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getPerson(){return person;} public void setPerson(String v){person=v;} public String getDate(){return dueDate;} public void setDate(String v){dueDate=v;} public String getPriority(){return priority;} public void setPriority(String v){priority=v;} public Boolean getDone(){return done;} public void setDone(Boolean v){done=v;} public String getType(){return type;} public void setType(String v){type=v;} public String getRelatedTo(){return relatedTo;} public void setRelatedTo(String v){relatedTo=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getOwnerEmail(){return ownerEmail;} public void setOwnerEmail(String v){ownerEmail=v;} public Instant getCreatedAt(){return createdAt;}
}
