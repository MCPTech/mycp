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

package in.mycp.remote;

import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.User;
import in.mycp.utils.Commons;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RemoteProxy(name = "DepartmentService")
public class DepartmentService {

	private static final Logger log = Logger.getLogger(DepartmentService.class
			.getName());
	@Autowired
	AccountLogService accountLogService;
	
	

	@RemoteMethod
	public Department saveOrUpdate(Department instance) {
		try {
			
			instance.setCompany(Company.findCompany(instance.getCompany().getId()));
			//instance.setManager(Manager.findManager(instance.getManager().getId()));
			//instance.setQuota(Quota.findQuota(instance.getQuota().getId()));
			accountLogService.saveLog("Department " + instance.getName()+" created, ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			if(instance.getId()!=null && instance.getId()>0){
				Department department = findById(instance.getId());
				if(department.getQuota()!=null && department.getQuota().intValue() != instance.getQuota().intValue()){
					
					List<User> users = User.findManagersByCompany(department.getCompany()).getResultList();
					for (User user : users) {
						if(user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER+"")){
							accountLogService.saveLogAndSendMail("Department '"+instance.getName()+"' Quota updated from '"+department.getQuota()+
									"' to '"+instance.getQuota()+"'", "Department '"+instance.getName()+"' Quota updated", 1, user.getEmail());
						}
					}
					
					
					
					
				}
			}
			return instance.merge();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			accountLogService.saveLog("Error in Department " + instance.getName()+" creation, ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
		}
		return null;
	}// end of saveOrUpdate(Department

	@RemoteMethod
	public String remove(int id) {
		try {
			Department d = Department.findDepartment(id);
			d.remove();
			accountLogService.saveLog("Department " + d.getName()+" removed, ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			return "Department removed "+id;
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			accountLogService.saveLog("Error in Department " + Department.findDepartment(id).getName()+" removal, ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
		}
		return "Error removing Department "+id+". Look into logs.";
	}// end of method remove(int id

	@RemoteMethod
	public Department findById(int id) {
		try {
			return Department.findDepartment(id);
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<Department> findAll() {
		try {
			if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
				return Department.findAllDepartments();
			}else{
				return Department.findDepartmentsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll
	
	@RemoteMethod
	public List<Department> findDepartmentsByCompany(Company company) {
		try {
			company = Company.findCompany(company.getId());
			return Department.findDepartmentsByCompany(company).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

}// end of class Department

