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
import in.mycp.domain.Project;
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

@RemoteProxy(name = "ProjectService")
public class ProjectService {

	private static final Logger log = Logger.getLogger(ProjectService.class
			.getName());
	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public void save(Project instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
	}// end of save(Project

	@RemoteMethod
	public Project saveOrUpdate(Project instance) {
		try {

			// instance.setCompany(Company.findCompany(instance.getCompany().getId()));
			instance.setDepartment(Department.findDepartment(instance
					.getDepartment().getId()));
			// instance.setQuota(Quota.findQuota(instance.getQuota().getId()));

			accountLogService.saveLog(
					"Project " + instance.getName() + " created, ",
					Commons.task_name.PROJECT.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons
							.getCurrentUser().getEmail());
			if(instance.getId()!=null && instance.getId()>0){
				Project project = findById(instance.getId());
				if(project.getQuota()!=null && project.getQuota().intValue() != instance.getQuota().intValue()){
					
					List<User> users = User.findManagersByCompany(project.getDepartment().getCompany()).getResultList();
					for (User user : users) {
						if(user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER+"")){
							accountLogService.saveLogAndSendMail("project '"+instance.getName()+"' Quota updated from '"+project.getQuota()+
									"' to '"+instance.getQuota()+"'", "project '"+instance.getName()+"' Quota updated", 1, user.getEmail());
						}
					}
					
					
					
				}
			}
			
			return instance.merge();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
			accountLogService.saveLog(
					"Error in Project " + instance.getName() + " creation, ",
					Commons.task_name.PROJECT.name(),
					Commons.task_status.FAIL.ordinal(), Commons
							.getCurrentUser().getEmail());
		}
		return null;
	}// end of saveOrUpdate(Project

	@RemoteMethod
	public String remove(int id) {
		try {
			Project p = Project.findProject(id);
			p.remove();

			accountLogService.saveLog(
					"Project " + p.getName() + " removed, ",
					Commons.task_name.PROJECT.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons
							.getCurrentUser().getEmail());
			return "Removed Project " + id;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
			accountLogService.saveLog(
					"Error in Project " + Project.findProject(id).getName() + " removal, ",
					Commons.task_name.PROJECT.name(),
					Commons.task_status.FAIL.ordinal(), Commons
							.getCurrentUser().getEmail());
			return "Error while removing Project " + id;
		}
	}// end of method remove(int id

	@RemoteMethod
	public Project findById(int id) {
		try {
			return Project.findProject(id);
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<Project> findAll() {
		try {
			if (Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return Project.findAllProjects();
			} else {
				return Project.findProjectsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId())).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

}// end of class Project

