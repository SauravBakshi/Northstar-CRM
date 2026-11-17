package com.northstar.crm.data;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/reports")
public class ReportController {
 private final CustomerRepository customers; private final LeadRepository leads; private final DealRepository deals; private final CrmTaskRepository tasks;
 public ReportController(CustomerRepository c,LeadRepository l,DealRepository d,CrmTaskRepository t){customers=c;leads=l;deals=d;tasks=t;}
 @GetMapping("/dashboard") public DashboardReport dashboard(Authentication auth){
  boolean manager=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN")||a.getAuthority().equals("ROLE_MANAGER")); String email=auth.getName();
  var customerRows=manager?customers.findAll():customers.findByOwnerEmailIgnoreCase(email);
  var leadRows=manager?leads.findAll():leads.findByOwnerEmailIgnoreCase(email);
  var dealRows=manager?deals.findAll():deals.findByOwnerEmailIgnoreCase(email);
  var taskRows=manager?tasks.findAll():tasks.findByOwnerEmailIgnoreCase(email);
  LocalDate today=LocalDate.now(); YearMonth thisMonth=YearMonth.from(today); double pipeline=dealRows.stream().mapToDouble(d->value(d.getValue())).sum();
  double won=dealRows.stream().filter(d->is(d.getStage(),"Won")).mapToDouble(d->value(d.getValue())).sum();
  long open=dealRows.stream().filter(d->!is(d.getStage(),"Won")&&!is(d.getStage(),"Lost")).count(); long wins=dealRows.stream().filter(d->is(d.getStage(),"Won")).count(); long closed=dealRows.stream().filter(d->is(d.getStage(),"Won")||is(d.getStage(),"Lost")).count();
  long due=taskRows.stream().filter(t->!Boolean.TRUE.equals(t.getDone())&&dueDate(t.getDate())!=null&&!dueDate(t.getDate()).isAfter(today)).count();
  long thisMonthCustomers=customerRows.stream().filter(c->c.getCreatedAt()!=null&&YearMonth.from(c.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()).equals(thisMonth)).count();
  var monthly=new ArrayList<MonthPoint>(); DateTimeFormatter fmt=DateTimeFormatter.ofPattern("MMM");
  for(int i=5;i>=0;i--){YearMonth m=thisMonth.minusMonths(i);double monthValue=dealRows.stream().filter(d->d.getCreatedAt()!=null&&YearMonth.from(d.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()).equals(m)).mapToDouble(d->value(d.getValue())).sum();long leadCount=leadRows.stream().filter(l->l.getCreatedAt()!=null&&YearMonth.from(l.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()).equals(m)).count();monthly.add(new MonthPoint(m.format(fmt),monthValue,leadCount));}
  Map<String,Long> sourceCounts=leadRows.stream().collect(Collectors.groupingBy(l->l.getSource()==null||l.getSource().isBlank()?"Other":l.getSource(),Collectors.counting()));
  List<SourceCount> sourceList=sourceCounts.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).map(e->new SourceCount(e.getKey(),e.getValue())).toList();
  return new DashboardReport(pipeline,won,customerRows.size(),thisMonthCustomers,open,taskRows.size(),due,taskRows.stream().filter(t->Boolean.TRUE.equals(t.getDone())).count(),leadRows.size(),closed==0?0:Math.round((double)wins*1000/closed)/10.0,dealRows.isEmpty()?0:pipeline/dealRows.size(),monthly,sourceList);
 }
 private static boolean is(String a,String b){return a!=null&&a.equalsIgnoreCase(b);} private static double value(Double d){return d==null?0:d;}
 private static LocalDate dueDate(String value){try{return value==null?null:LocalDate.parse(value.substring(0,Math.min(10,value.length())));}catch(RuntimeException e){return null;}}
 public record DashboardReport(double pipelineValue,double wonRevenue,long customerCount,long newCustomersThisMonth,long openDealCount,long taskCount,long dueTasks,long completedTaskCount,long leadCount,double winRate,double averageDealSize,List<MonthPoint> monthlyPipeline,List<SourceCount> leadSources){}
 public record MonthPoint(String month,double revenue,long leads){}
 public record SourceCount(String name,long value){}
}
