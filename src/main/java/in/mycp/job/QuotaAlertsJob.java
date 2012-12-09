/*
 mycloudportal - Self Service Portal for the cloud.
 Copyright (C) 2012-2013 Mycloudportal Technologies Pvt Ltd

 This file is part of mycloudportal.

 mycloudportal is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 mycloudportal is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with mycloudportal.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.mycp.job;

import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.remote.AccountLogService;
import in.mycp.remote.ReportService;
import in.mycp.service.ApplicationContextService4Jobs;
import in.mycp.service.WorkflowImpl4Jbpm;
import in.mycp.utils.Commons;

import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GangadharJN
 * @author jangamgangadhar@gmail.com
 * 
 */
@Component("QuotaAlertsJob")
public class QuotaAlertsJob implements EventListener {
	private static final long serialVersionUID = 1L;
	/*@Autowired
	ReportService reportService;*/
	
	@Transactional
	public void notify(EventListenerExecution execution) {
		ApplicationContext appContext = ApplicationContextService4Jobs.getAppContext();

		try{
		ReportService reportService = (ReportService)appContext.getBean("reportService");
		WorkflowImpl4Jbpm workflowImpl4Jbpm = (WorkflowImpl4Jbpm)appContext.getBean("workflowImpl4Jbpm");
		AccountLogService accountLogService = (AccountLogService)appContext.getBean("accountLogService");
		List<Company> lstCompany = Company.findAllCompanys();
		for (Company company : lstCompany) {
			 Hibernate.initialize(company);
			//company = Company.findCompany(company.getId());
			//System.out.println("company : "+company.getName());
			try{
				long companyAssetCost = reportService.getAllAssetCosts("company", company.getId()).getTotalCost();
				if(company.getQuota()!=null && company.getQuota()>0){
					if(company.getQuota() <= companyAssetCost){
						throw new Exception("Company '"+company.getName()+"' "+Commons.QUOTA_EXCEED_MSG);
					}else if( company.getQuota()-companyAssetCost <= company.getMinBal() ){
						throw new Exception("Company '"+company.getName()+"' "+Commons.QUOTA_ABOUTTO_EXCEED_MSG);
					}
				}
				
				Set<Department> lstdeDepartments = company.getDepartments();
				for (Department department : lstdeDepartments) {
					//System.out.println("department : "+department.getName());
					long deptAssetCost = reportService.getAllAssetCosts("department", department.getId()).getTotalCost();
					if(department.getQuota() !=null){
					long minQuota = Math.round(0.1 * department.getQuota());
					if(department.getQuota()<=deptAssetCost){
						throw new Exception("Department '"+department.getName()+"' "+Commons.QUOTA_EXCEED_MSG);
					}else if(department.getQuota()>0 && (department.getQuota()-deptAssetCost <= minQuota))
						throw new Exception("Department '"+department.getName()+"' "+Commons.QUOTA_ABOUTTO_EXCEED_MSG);
					}
					Set<Project> lstProjects = department.getProjects();
					for (Project project : lstProjects) {
						//System.out.println("project : "+project.getName());
						long projAssetCost = reportService.getAllAssetCosts("project", project.getId()).getTotalCost();
						if(project.getQuota() !=null){
						long minQuota = Math.round(0.1 * project.getQuota());
						if(project.getQuota()<=projAssetCost)
							throw new Exception("Project '"+project.getName()+"' "+Commons.QUOTA_EXCEED_MSG);
						else if(project.getQuota()>0 && (project.getQuota()-projAssetCost <= minQuota))
							throw new Exception("Project '"+project.getName()+"' "+Commons.QUOTA_ABOUTTO_EXCEED_MSG);
						}
						
					}
					Set<User> stUsers = department.getUsers();
					for (User user : stUsers) {
						//System.out.println("user : "+user.getEmail());
						long userAssetCost = reportService.getAllAssetCosts("user", user.getId()).getTotalCost();
						if(user.getQuota()!=null){
						long minQuota = Math.round(0.1 * user.getQuota());
						if(user.getQuota()<=userAssetCost){
							throw new Exception("User '"+user.getEmail()+"' "+Commons.QUOTA_EXCEED_MSG);
						}else if(user.getQuota()>0 && user.getQuota()-userAssetCost <= minQuota)
							throw new Exception("User '"+user.getEmail()+"' "+Commons.QUOTA_ABOUTTO_EXCEED_MSG);
						}
					}
				}
			}catch(Exception ex){
				try{
				accountLogService.saveLogAndSendMail(ex.getMessage(), "Quota Regular Check Job", Commons.task_status.FAIL.ordinal(), "gangu96@yahoo.co.in");
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
}
