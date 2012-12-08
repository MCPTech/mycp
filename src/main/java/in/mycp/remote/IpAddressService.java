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



import in.mycp.domain.IpAddressP;

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

@RemoteProxy(name = "IpAddressP")
public class IpAddressService {

    private static final Logger log = Logger.getLogger(IpAddressService.class.getName());

    
    // not used as of now, 
    //ip address work is done by addressinfopservice
	/*
	
    	@RemoteMethod
		public void save(IpAddressP instance){
			try{
				instance.persist();
			}catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
		}//end of save(IpAddressP
		
    	@RemoteMethod
		public IpAddressP saveOrUpdate(IpAddressP instance){
			try{
				if(instance.getId()>0){
					IpAddressP instance_local = IpAddressP.findIpAddressP(instance.getId());
					
				}
				return instance.merge();
			}catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
			return null;
		}//end of saveOrUpdate(IpAddressP
		
    	@RemoteMethod
		public void remove(int id){
			try{
				IpAddressP.findIpAddressP(id).remove();
			}catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
		}//end of method remove(int id
		
    	@RemoteMethod
		public IpAddressP findById(int id){
			try{
				return IpAddressP.findIpAddressP(id);
			}catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
			return null;
		}//end of method findById(int id

    	@RemoteMethod
		public List findAll(){
			try{
				return IpAddressP.findAllIpAddressPs();
				}catch (Exception e) {
				log.error(e.getMessage());//e.printStackTrace();
			}
			return null;
		}//end of method findAll
*/   }//end of class IpAddressPController
   
   

