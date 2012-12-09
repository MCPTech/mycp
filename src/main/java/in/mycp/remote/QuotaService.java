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



import in.mycp.domain.MeterMetric;
import in.mycp.domain.Quota;

import java.util.Date;
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

@RemoteProxy(name="QuotaService")
public class QuotaService  {

	private static final Logger log = Logger.getLogger(QuotaService.class
			.getName());

	
	@RemoteMethod
	public void save(Quota instance) {
		try {
			instance.persist();
			} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
	}// end of save(Infra

	@RemoteMethod
	public Quota saveOrUpdate(Quota instance) {
		try {
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			//formatter.parse(instance.getStartdate());
			
			//formatter.getCalendar().getTime();
			
			MeterMetric mm = MeterMetric.findMeterMetric(instance.getMeterMetric().getId());
			instance.setMeterMetric(mm);
			
			/*Quota q = new Quota();
			q.setEnddate(instance.getEnddate());
			q.setStartdate(instance.getStartdate());
			q.setLimit(instance.getLimit());
			q.setName(instance.getName());
			q.setMeterMetric(mm);
			System.out.println("saveOrUpdate");
			
			Quota q1 = new Quota();
			q1.setName("ggggggg");
			q1.setEnddate(new Date());*/
			return instance.merge();   
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(Infra

	@RemoteMethod
	public void remove(int id) {
		try {
			Quota.findQuota(id).remove();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public Quota findById(int id) {
		try {
			return Quota.findQuota(id);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List findAll() {
		try {
			return Quota.findAllQuotas();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	
}// end of class Quota

