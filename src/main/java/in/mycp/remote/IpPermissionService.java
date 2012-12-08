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

import in.mycp.domain.AssetType;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.IpPermissionP;
import in.mycp.utils.Commons;
import in.mycp.workers.SecurityGroupWorker;

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
@RemoteProxy(name = "IpPermissionP")
public class IpPermissionService {

	private static final Logger log = Logger.getLogger(IpPermissionService.class.getName());

	@Autowired
	SecurityGroupWorker securityGroupWorker;
	
	@Autowired
	WorkflowService workflowService;
	

	public void save(IpPermissionP instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			log.error(e);e.printStackTrace();
		}
	}// end of save(IpPermissionP

	@RemoteMethod
	public IpPermissionP saveOrUpdate(IpPermissionP instance) {
		try {
			instance = instance.merge();
			AssetType assetTypeSecurityGroup = AssetType.findAssetTypesByNameEquals(Commons.ASSET_TYPE.SecurityGroup + "")
					.getSingleResult();
			if(!assetTypeSecurityGroup.getWorkflowEnabled() || (instance.getGroupDescription() !=null 
					&& instance.getGroupDescription().getStatus().equals(Commons.secgroup_STATUS.active+""))){
				//workflowApproved(instance);
			}
			Commons.setSessionMsg("Scheduling Ip permission Save");
			return instance;
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip permission Save");
			log.error(e);e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(IpPermissionP

	@RemoteMethod
	public void workflowApproved(IpPermissionP instance) {
		try {
			securityGroupWorker.authorizeSecurityGroupIngress(instance.getGroupDescription().getAsset().getProductCatalog().getInfra(),
					instance,Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e);e.printStackTrace();
		}
	}// end of createCompute(InstanceP
	
	@RemoteMethod
	public void remove(int id) {
		try {

			securityGroupWorker.revokeSecurityGroupIngress(IpPermissionP.findIpPermissionP(id).getGroupDescription().getAsset()
					.getProductCatalog().getInfra(), IpPermissionP.findIpPermissionP(id),Commons.getCurrentUser().getEmail());
			IpPermissionP.findIpPermissionP(id).remove();
			Commons.setSessionMsg("Scheduling Ip permission remove");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip permission remove");
			log.error(e);e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public IpPermissionP findById(int id) {
		try {

			return IpPermissionP.findIpPermissionP(id);
		} catch (Exception e) {
			log.error(e);//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<IpPermissionP> findAll() {
		try {
			return IpPermissionP.findAllIpPermissionPs();
		} catch (Exception e) {
			log.error(e);//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<IpPermissionP> findBySecurityGroup(GroupDescriptionP groupDescriptionP) {
		try {
			return IpPermissionP.findIpPermissionPsByGroupDescription(groupDescriptionP).getResultList();
		} catch (Exception e) {
			log.error(e);e.printStackTrace();
		}
		return null;
	}// end of method findById(int id
}// end of class IpPermissionPController

