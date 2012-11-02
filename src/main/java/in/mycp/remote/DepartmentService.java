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
import in.mycp.domain.Department;
import in.mycp.domain.InstanceP;
import in.mycp.domain.Manager;
import in.mycp.domain.Quota;
import in.mycp.domain.User;
import in.mycp.utils.Commons;
import in.mycp.web.MycpSession;

import java.util.List;

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
			accountLogService.saveLog("Department created " + instance.getName()+", ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			return instance.merge();
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			accountLogService.saveLog("Error in Department creation " + instance.getName()+", ",
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
			accountLogService.saveLog("Department removed " + d.getName()+", ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			return "Department removed "+id;
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			accountLogService.saveLog("Error in Department removal " + Department.findDepartment(id).getName()+", ",
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

}// end of class Department

