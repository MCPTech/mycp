//My Cloud Portal - Self Service Portal for the cloud.
//This file is part of My Cloud Portal.
//
//My Cloud Portal is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, version 3 of the License.
//
//My Cloud Portal is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with My Cloud Portal.  If not, see <http://www.gnu.org/licenses/>.

package in.mycp.remote;

import in.mycp.domain.Company;
import in.mycp.domain.Project;
import in.mycp.domain.Role;
import in.mycp.domain.User;
import in.mycp.utils.Commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "RealmService")
public class RealmService {

	private static final Logger log = Logger.getLogger(RealmService.class
			.getName());

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
	@Autowired
	AccountLogService accountLogService;

	@Autowired
	ShaPasswordEncoder passwordEncoder;

	@RemoteMethod
	public boolean emailExists(String email) {
		try {
			User user = User.findUsersByEmailEquals(email).getSingleResult();

			if (user != null) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
			return false;
		}
	}

	@RemoteMethod
	public User saveOrUpdate(User instance) {
		try {
			if (instance != null && StringUtils.isBlank(instance.getPassword())) {
				throw new Exception("Password cannot be empty");
			}
			//to update the user projects
			Set<Project> stProjects = instance.getProjects();
			for (Iterator iterator = stProjects.iterator(); iterator.hasNext();) {
				Project project = (Project) iterator.next();
				project = Project.findProject(project.getId());
				project.getUsers().add(instance);
			}
			User localUser = User.findUser(instance.getId());
			if (localUser == null) {
				instance.setRegistereddate(new Date());
				instance.setPassword(passwordEncoder.encodePassword(instance.getPassword(), instance.getEmail()));
			} else {
				instance.setRegistereddate(localUser.getRegistereddate());
				if (!localUser.getPassword().equals(instance.getPassword())) {
					instance.setPassword(passwordEncoder.encodePassword(instance.getPassword(), instance.getEmail()));
				}
			}
			//instance.setProject(Project.findProject(instance.getProject().getId()));
			//instance.setManager(Manager.findManager(instance.getManager().getId()));
			//instance.setQuota(Quota.findQuota(instance.getQuota().getId()));
			// ShaPasswordEncoder passEncoder = new ShaPasswordEncoder(256);
			accountLogService.saveLog("User " + instance.getEmail()+" created, ",
					Commons.task_name.USER.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			return instance.merge();
		} catch (Exception e) {
			log.error(e.getMessage());e.printStackTrace();
			accountLogService.saveLog("Error in User " + instance.getEmail()+" creation, "+e.getMessage(),
					Commons.task_name.USER.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
		}
		return null;
	}// end of saveOrUpdate(User

	@RemoteMethod
	public void remove(int id) {
		try {
			User u = User.findUser(id);
			u.remove();
			
			accountLogService.saveLog("User " + u.getEmail()+" removed, ",
					Commons.task_name.USER.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			accountLogService.saveLog("Error in User " + User.findUser(id).getEmail()+" removal, "+e.getMessage(),
					Commons.task_name.USER.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
		}
	}// end of method remove(int id

	@RemoteMethod
	public User findById(int id) {
		try {
			User user = User.findUser(id);
			user.getProjects().size();//without this user page will not work properly
			return user;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<User> findAll() {
		try {

			if (Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return User.findAllUsers();
			} else {
				List<User> list = User.findAllUsers();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					User user = (User) iterator.next();
					user.getProjects().size();//without this user page will not work properly
				}
				return User.findUsersByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId())).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<Role> findAllRoles() {
		try {
			// System.out.println(" = "+Role.findAllRoles().size());
			return Role.findAllRoles();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

}// end of class UserController

