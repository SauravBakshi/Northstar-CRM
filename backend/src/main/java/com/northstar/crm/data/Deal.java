package com.northstar.crm.data;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="deals")
public class Deal {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String name; private String company; private Double value; private String stage="Qualified"; private String owner; private String closeDate; private Integer probability=0; private String nextStep; private String notes; private String ownerEmail; private Instant createdAt=Instant.now();
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getCompany(){return company;} public void setCompany(String v){company=v;} public Double getValue(){return value;} public void setValue(Double v){value=v;} public String getStage(){return stage;} public void setStage(String v){stage=v;} public String getOwner(){return owner;} public void setOwner(String v){owner=v;} public String getClose(){return closeDate;} public void setClose(String v){closeDate=v;} public Integer getProbability(){return probability;} public void setProbability(Integer v){probability=v;} public String getNextStep(){return nextStep;} public void setNextStep(String v){nextStep=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getOwnerEmail(){return ownerEmail;} public void setOwnerEmail(String v){ownerEmail=v;} public Instant getCreatedAt(){return createdAt;}
}
