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

import in.mycp.domain.AvailabilityZoneP;
import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.utils.Commons;

import java.util.ArrayList;
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
	
	@RemoteProxy(name = "ZoneService")
	public class ZoneService {

		private static final Logger log = Logger.getLogger(ZoneService.class
				.getName());
		
		@Autowired
		AccountLogService accountLogService;	
		
		@RemoteMethod
		public List<AvailabilityZoneP> findAll() {
			try {
				List<AvailabilityZoneP> az= new ArrayList<AvailabilityZoneP>();
				
				if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
					return AvailabilityZoneP.findAllAvailabilityZonePs();
				}else{
					return AvailabilityZoneP.findAllAvailabilityZonePsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()));
				}
				
			} catch (Exception e) {
				log.error(e.getMessage());e.printStackTrace();
			}
			return null;
		}// end of method findAll
		
		@RemoteMethod
		public List<AvailabilityZoneP> findBy(Infra infra) {
			try {
				List<AvailabilityZoneP> az= new ArrayList<AvailabilityZoneP>();
				
				if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
					return AvailabilityZoneP.findAllAvailabilityZonePsByInfra(infra);
				}else{
					return AvailabilityZoneP.findAllAvailabilityZonePsBy(infra,Company.findCompany(Commons.getCurrentSession().getCompanyId()));
				}
				
			} catch (Exception e) {
				log.error(e.getMessage());e.printStackTrace();
			}
			return null;
		}// end of method findAll
		
		
		@RemoteMethod
		public AvailabilityZoneP saveOrUpdate(AvailabilityZoneP instance) {
			try {
				//instance.setCompany(Company.findCompany(instance.getCompany().getId()));
				instance = instance.merge();
				
				accountLogService.saveLog("Zone created " + instance.getName()+", ",
						Commons.task_name.AVAILABILITYZONE.name(),
						Commons.task_status.SUCCESS.ordinal(),
						Commons.getCurrentUser().getEmail());
				
				return instance;
			} catch (Exception e) {
				log.error(e.getMessage());e.printStackTrace();
				
				accountLogService.saveLog("Error in Zone creation " + instance.getName()+", ",
						Commons.task_name.AVAILABILITYZONE.name(),
						Commons.task_status.FAIL.ordinal(),
						Commons.getCurrentUser().getEmail());
				
			}
			return null;
		}// end of saveOrUpdate(AvailabilityZoneP

		@RemoteMethod
		public String remove(int id) {
			try {
				AvailabilityZoneP z = AvailabilityZoneP.findAvailabilityZoneP(id);
				z.remove();
				accountLogService.saveLog("Zone removed " + z.getName()+", ",
						Commons.task_name.AVAILABILITYZONE.name(),
						Commons.task_status.SUCCESS.ordinal(),
						Commons.getCurrentUser().getEmail());
				return "Removed Availability Zone "+id;
			} catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
				accountLogService.saveLog("Error in Zone creation " + AvailabilityZoneP.findAvailabilityZoneP(id).getName()+", ",
						Commons.task_name.AVAILABILITYZONE.name(),
						Commons.task_status.FAIL.ordinal(),
						Commons.getCurrentUser().getEmail());
			}
			return "Cannot Remove Availability Zone "+id+". look into logs.";
		}// end of method remove(int id

		@RemoteMethod
		public AvailabilityZoneP findById(int id) {
			try {
				return AvailabilityZoneP.findAvailabilityZoneP(id);
			} catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
			return null;
		}// end of method findById(int id
}
