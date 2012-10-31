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

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import in.mycp.utils.Commons;
import in.mycp.workers.ComputeWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.jbpm.pvm.internal.jobexecutor.GetNextDueDateCmd;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
@RemoteProxy(name = "InstanceP")
public class InstancePService {

	private static final Logger log = Logger.getLogger(InstancePService.class.getName());
	@Autowired
	WorkflowService workflowService;
	@Autowired
	ComputeWorker computeWorker;
	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

	@RemoteMethod
	public void requestCompute(InstanceP instance) {
		try {

			User currentUser = Commons.getCurrentUser();
			AssetType assetTypeComputeInstance = AssetType.findAssetTypesByNameEquals(Commons.ASSET_TYPE.ComputeInstance + "")
					.getSingleResult();

			long allAssetTotalCosts = reportService.getAllAssetCosts().getTotalCost();
			currentUser = User.findUser(currentUser.getId());
			Company company = currentUser.getProject().getDepartment().getCompany();
			Asset asset = Commons.getNewAsset(assetTypeComputeInstance, currentUser,instance.getProduct(), allAssetTotalCosts,company);
			instance.setAsset(asset);
			instance = instance.merge();
			Set<InstanceP> instances = new HashSet<InstanceP>();
			instances.add(instance);

			if (true == assetTypeComputeInstance.getWorkflowEnabled()) {
				Commons.createNewWorkflow(workflowService.createProcessInstance(Commons.PROCESS_DEFN.Compute_Request
						+ ""), instance.getId(), asset.getAssetType().getName());
				instance.setState(Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "");
				instance = instance.merge();
				accountLogService.saveLog("Compute with ID "+instance.getId()+" requested, workflow started, pending approval.", Commons.task_name.COMPUTE.name(), 
						Commons.task_status.SUCCESS.ordinal(),currentUser.getEmail());
			} else {
				accountLogService.saveLog("Compute with ID "+instance.getId()+" requested, workflow approved automatically.", Commons.task_name.COMPUTE.name(), 
						Commons.task_status.SUCCESS.ordinal(),currentUser.getEmail());
				instance.setState(Commons.REQUEST_STATUS.STARTING + "");
				instance = instance.merge();
				workflowApproved(instances);
			}
			log.info("end of requestCompute");
		} catch (Exception e) {
			log.error(e.getMessage());e.printStackTrace();
			Commons.setSessionMsg("Error while requestCompute Instance "+instance.getName()
					+"<br> Reason: "+e.getMessage());
			accountLogService.saveLog("Error in Compute request with ID "+instance.getId()+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), 
					Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
		}
	}// end of requestCompute(InstanceP

	@RemoteMethod
	public void updateCompute(InstanceP instance) {
		try {
			InstanceP localInstance = InstanceP.findInstanceP(instance.getId());
			if ((Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "").equals(localInstance.getState())) {
				instance.setState("" + Commons.WORKFLOW_STATUS.PENDING_APPROVAL);
			} 
			instance = instance.merge();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
	}// end of updateCompute(InstanceP

	/**
	 * This is called after workflow is approved. this triggers the compute
	 * instance creation in the backend infrastructure
	 * 
	 * 
	 * @param instance
	 */
	@RemoteMethod
	public void workflowApproved(Set<InstanceP> instances) {
			log.info("in createCompute");
			for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
				InstanceP instanceP = (InstanceP) iterator.next();
				try {
				computeWorker.createCompute(instanceP.getAsset().getProductCatalog().getInfra(), instanceP,Commons.getCurrentUser().getEmail());
				log.info("Scheduled ComputeCreateWorker for " + instanceP.getName());
				Commons.setSessionMsg("Scheduled Instance creation "+ instanceP.getId());
				accountLogService.saveLog("Workflow approved for compute with ID "+instanceP.getId()+", compute creation scheduled.", Commons.task_name.COMPUTE.name(),
						Commons.task_status.SUCCESS.ordinal(),Commons.getCurrentUser().getEmail());
				} catch (Exception e) {
					log.error(e.getMessage());//e.printStackTrace();
					Commons.setSessionMsg("Error while scheduling Instance creation "+instanceP.getId());
					accountLogService.saveLog("Error during Workflow approval for compute with ID "+instanceP.getId()+", "+e.getMessage(), 
							Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
				}
			}
			log.info("end of createCompute");
		
	}// end of createCompute(InstanceP

	

	@RemoteMethod
	public InstanceP saveOrUpdate(InstanceP instance) {
		try {
			InstanceP i = instance.merge();
			Commons.setSessionMsg("Saved Instance "+i.getId());
			//accountLogService.saveLog("Compute save with ID "+instance.getId(), Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal());
			return i;
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			Commons.setSessionMsg("Error while saving Instance "+instance.getName());
			//accountLogService.saveLog("Error in Compute save with ID "+instance.getId()+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal());
		}
		return null;
	}// end of saveOrUpdate(InstanceP

	@RemoteMethod
	public void remove(int id) {
		try {
			//terminateCompute(id);
			InstanceP.findInstanceP(id).remove();
			Commons.setSessionMsg("Removed Instance "+id);
			accountLogService.saveLog("Compute instance removed with ID "+id, Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal()
					,Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			Commons.setSessionMsg("Error while removing Instance "+id);
			accountLogService.saveLog("Error in Compute istance removal with ID "+id, Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal()
					,Commons.getCurrentUser().getEmail());
		}
	}// end of method remove(int id

	@RemoteMethod
	public InstanceP findById(int id) {
		try {
			InstanceP instance = InstanceP.findInstanceP(id);
			instance.setProduct(""+instance.getAsset().getProductCatalog().getId());
			return instance;
		} catch (Exception e) {
			log.error(e);//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<InstanceP> findAll(int start, int max,String search) {
		try {
			User user = Commons.getCurrentUser();
			//if role is MANAGER , show all VMs
			//if role is MYCP_ADMIN , show all VMs including System VMs
			//for everybody else, just show their own VMs
			if(user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER+"")
					|| user.getRole().getName().equals(Commons.ROLE.ROLE_ADMIN+"")){
				return InstanceP.findInstancePsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()),start, max,search).getResultList();
			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
				return InstanceP.findAllInstancePs(start, max,search);
			}else{
				return InstanceP.findInstancePsByUser(user,start, max,search).getResultList();
			}
			

		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<InstanceP> findAllWithSystemVms(int start, int max,String search) {
		try {
			return InstanceP.findAllInstancePs(start, max,search);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<InstanceP> findAll4Attach() {
		try {
			// return InstanceP.findAllInstancePs();
			List<InstanceP> instances = InstanceP.findAllInstancePs();
			List<InstanceP> instances2return = new ArrayList<InstanceP>();
			for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
				InstanceP instanceP = (InstanceP) iterator.next();
				try {
					AddressInfoP addressInfoP = AddressInfoP.findAddressInfoPsByInstanceIdLike(instanceP.getInstanceId()).getSingleResult();
					if (addressInfoP.getAssociated() != null || addressInfoP.getAssociated() == true) {
						continue;
					}
				} catch (Exception e) {
					log.error(e);//e.printStackTrace();
				}
				instances2return.add(instanceP);
			}
			return instances2return;

		} catch (Exception e) {
			log.error(e);//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public void terminateCompute(int id) {
		try {
		Commons.setAssetEndTime(InstanceP.findInstanceP(id).getAsset());
		computeWorker.terminateCompute(InstanceP.findInstanceP(id).getAsset().getProductCatalog().getInfra(), id,Commons.getCurrentUser().getEmail());
		accountLogService.saveLog("Compute instance terminated with ID "+id, Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal()
				,Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			accountLogService.saveLog("Error in Compute instance termination with ID "+id+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), 
					Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
		}
	}

	@RemoteMethod
	public void stopCompute(int id) {
		try {
			computeWorker.stopCompute(InstanceP.findInstanceP(id).getAsset().getProductCatalog().getInfra(), id,Commons.getCurrentUser().getEmail());	
			accountLogService.saveLog("Compute instance stopped with ID "+id, Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal()
					,Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			accountLogService.saveLog("Error while stopping Compute instance with ID "+id+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), 
					Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
		}
		
	}

	@RemoteMethod
	public void startCompute(int id) {
		try {
			computeWorker.startCompute(InstanceP.findInstanceP(id).getAsset().getProductCatalog().getInfra(), id,Commons.getCurrentUser().getEmail());	
			accountLogService.saveLog("Compute instance started with ID "+id, Commons.task_name.COMPUTE.name(), 
					Commons.task_status.SUCCESS.ordinal(),Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			accountLogService.saveLog("Error while starting Compute instance with ID "+id+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), 
					Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
		}
		
	}

	@RemoteMethod
	public void restartCompute(int id) {
		try {
			computeWorker.restartCompute(InstanceP.findInstanceP(id).getAsset().getProductCatalog().getInfra(), id,Commons.getCurrentUser().getEmail());
			accountLogService.saveLog("Compute instance restarted with ID "+id, Commons.task_name.COMPUTE.name(), 
					Commons.task_status.SUCCESS.ordinal(),Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			accountLogService.saveLog("Error while restarting Compute instance with ID "+id+", "+e.getMessage(), Commons.task_name.COMPUTE.name(), 
					Commons.task_status.FAIL.ordinal(),Commons.getCurrentUser().getEmail());
		}
		
	}

	@RemoteMethod
	public List<ProductCatalog> findProductType() {
		
		if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
			return ProductCatalog.findProductCatalogsByProductTypeEquals(Commons.ProductType.ComputeInstance.getName()).getResultList();
		}else{
			
			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(Commons.ProductType.ComputeInstance.getName(),
					Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
		}
		
		
	}

}// end of class InstancePController

