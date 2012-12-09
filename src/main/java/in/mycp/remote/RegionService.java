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
import in.mycp.domain.RegionP;
import in.mycp.utils.Commons;

import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RemoteProxy(name="RegionService")
public class RegionService  {

	private static final Logger log = Logger.getLogger(RegionService.class
			.getName());

	
	

	@RemoteMethod
	public RegionP saveOrUpdate(RegionP instance) {
		try {
			instance.setCompany(Company.findCompany(instance.getCompany().getId()));
			return instance.merge();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(Infra

	@RemoteMethod
	public String remove(int id) {
		try {
			RegionP.findRegionP(id).remove();
			return "Removed Region "+id;
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return "Cannot Remove Region "+id+". look into logs.";
	}// end of method remove(int id

	@RemoteMethod
	public RegionP findById(int id) {
		try {
			return RegionP.findRegionP(id);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<RegionP> findAll() {
		try {
			
			if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
				return RegionP.findAllRegionPs();
			}else{
				return RegionP.findRegionPsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	
}// end of class RegionP

