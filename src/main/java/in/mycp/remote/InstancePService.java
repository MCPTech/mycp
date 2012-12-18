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

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import in.mycp.utils.Commons;
import in.mycp.workers.ComputeWorker;
import in.mycp.workers.VmwareComputeWorker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
@RemoteProxy(name = "InstanceP")
public class InstancePService {

	private static final Logger log = Logger.getLogger(InstancePService.class.getName());
	@Autowired
	WorkflowService workflowService;
	@Autowired
	ComputeWorker computeWorker;
	@Autowired
	VmwareComputeWorker vmwareComputeWorker;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

	@RemoteMethod
	public void requestCompute(InstanceP instance) {
		try {
			Infra i = ProductCatalog.findProductCatalog(Integer.parseInt(instance.getProduct())).getInfra();
			User currentUser = Commons.getCurrentUser();
			currentUser = User.findUser(currentUser.getId());
			Department department = currentUser.getDepartment();
			Company company = department.getCompany();
			AssetType assetType = AssetType.findAssetTypesByNameEquals(Commons.ASSET_TYPE.ComputeInstance + "").getSingleResult();

			Asset asset = Commons.getNewAsset(assetType, currentUser, instance.getProduct(), reportService, company);
			asset.setProject(Project.findProject(instance.getProjectId()));
			instance.setAsset(asset);
			instance = instance.merge();
			Set<InstanceP> instances = new HashSet<InstanceP>();
			instances.add(instance);

			if (true == assetType.getWorkflowEnabled()) {
				Commons.createNewWorkflow(workflowService.createProcessInstance(Commons.PROCESS_DEFN.Compute_Request + ""), instance.getId(), asset.getAssetType()
						.getName());
				instance.setState(Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "");
				instance = instance.merge();
				accountLogService.saveLog("Compute '" + instance.getName() + "' with ID " + instance.getId() + " requested, workflow started, pending approval.",
						Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal(), currentUser.getEmail());
				Commons.setSessionMsg("requestCompute Instance " + instance.getName() + " scheduled");
			} else {
				accountLogService.saveLog("Compute '" + instance.getName() + "' with ID " + instance.getId() + " requested, workflow approved automatically.",
						Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal(), currentUser.getEmail());
				instance.setState(Commons.REQUEST_STATUS.STARTING + "");
				instance = instance.merge();
				Commons.setSessionMsg("requestCompute Instance " + instance.getName() + " workflow aproved");
				workflowApproved(instances);
			}
			log.info("end of requestCompute");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Commons.setSessionMsg("Error while requestCompute Instance " + instance.getName() + "<br> Reason: " + e.getMessage());
			accountLogService.saveLogAndSendMail("Error in Compute '" + instance.getName() + "' request with ID " + (instance.getId() != null ? instance.getId() : 0)
					+ ", " + e.getMessage(), Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
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
			log.error(e.getMessage());// e.printStackTrace();
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
				Infra infra = instanceP.getAsset().getProductCatalog().getInfra();
				if (infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
					computeWorker.createCompute(infra, instanceP, Commons.getCurrentUser().getEmail());
				} else {
					vmwareComputeWorker.createCompute(infra, instanceP, Commons.getCurrentUser().getEmail());
				}

				instanceP.setState(Commons.REQUEST_STATUS.STARTING + "");
				instanceP = instanceP.merge();
				log.info("Scheduled ComputeCreateWorker for " + instanceP.getName());
				Commons.setSessionMsg("Scheduled Instance creation " + instanceP.getId());
				accountLogService.saveLog("Workflow approved for compute '" + instanceP.getName() + "' with ID " + instanceP.getId() + ", compute creation scheduled.",
						Commons.task_name.COMPUTE.name(), Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
				Commons.setSessionMsg("Error while scheduling Instance creation " + instanceP.getId());
				accountLogService.saveLog(
						"Error during Workflow approval for compute '" + instanceP.getName() + "' with ID " + instanceP.getId() + ", " + e.getMessage(),
						Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
			}
		}
		log.info("end of createCompute");

	}// end of createCompute(InstanceP

	@RemoteMethod
	public InstanceP saveOrUpdate(InstanceP instance) {
		try {
			InstanceP i = instance.merge();
			Commons.setSessionMsg("Saved Instance " + i.getId());
			// accountLogService.saveLogAndSendMail("Compute save with ID "+instance.getId(),
			// Commons.task_name.COMPUTE.name(),
			// Commons.task_status.FAIL.ordinal());
			return i;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
			Commons.setSessionMsg("Error while saving Instance " + instance.getName());
			// accountLogService.saveLogAndSendMail("Error in Compute save with ID "+instance.getId()+", "+e.getMessage(),
			// Commons.task_name.COMPUTE.name(),
			// Commons.task_status.FAIL.ordinal());
		}
		return null;
	}// end of saveOrUpdate(InstanceP

	@RemoteMethod
	public void remove(int id) {
		InstanceP instance = InstanceP.findInstanceP(id);
		String instanceName = instance.getName();
		try {
			// terminateCompute(id);
			// if instance is still in pending approval status , allow him to
			// remove.
			if (instance.getState() != null && instance.getState().equals(Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "")) {
				Workflow workflow = Workflow.findWorkflowsBy(instance.getId(),Commons.ASSET_TYPE.ComputeInstance+"");
				workflowService.endProcessInstance(workflow.getProcessId());
				instance.remove();
			} else {

				List<AddressInfoP> addresses = AddressInfoP.findAddressInfoPsByPublicIpEquals(instance.getIpAddress()).getResultList();
				for (Iterator iterator = addresses.iterator(); iterator.hasNext();) {
					try {
						AddressInfoP addressInfoP = (AddressInfoP) iterator.next();
						addressInfoP.remove();
					} catch (Exception e) {
						log.error("Error while trying to remove the associated IP address of Instance " + instance.getName() + " with ID " + instance.getInstanceId());
						// TODO: handle exception
					}
				}

				Asset a = instance.getAsset();
				if (a != null && a.getEndTime() != null) {
					Commons.setAssetEndTime(a);
				}
			}
			// instance.remove();
			Commons.setSessionMsg("Removed Instance " + id);
			accountLogService.saveLog("Compute instance '" + instanceName + "' removed with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Commons.setSessionMsg("Error while removing Instance " + id);
			accountLogService.saveLog("Error in Compute instance '" + instanceName + "' removal with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
		}
	}// end of method remove(int id

	@RemoteMethod
	public InstanceP findById(int id) {
		try {
			InstanceP instance = InstanceP.findInstanceP(id);
			instance.setProduct("" + instance.getAsset().getProductCatalog().getId());
			return instance;
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<InstanceP> findAll(int start, int max, String search) {
		try {
			User user = Commons.getCurrentUser();
			// if role is MANAGER , show all VMs
			// if role is MYCP_ADMIN , show all VMs including System VMs
			// for everybody else, just show their own VMs
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
				return InstanceP.findInstancePsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()), start, max, search).getResultList();
			} else if (user.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return InstanceP.findAllInstancePs(start, max, search);
			} else {
				return InstanceP.findInstancePsByUser(user, start, max, search).getResultList();
			}

		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<InstanceP> findInstances4VolAttachBy(Infra i) {
		try {
			User user = Commons.getCurrentUser();
			// if role is MANAGER , show all VMs
			// if role is MYCP_ADMIN , show all VMs including System VMs
			// for everybody else, just show their own VMs
			if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.EDITION_ENABLED == Commons.HOSTED_EDITION_ENABLED) {
				// if super admin, show all sec groups across all accounts in
				// teh same cloud
				if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
					return InstanceP.findInstancePsByInfra(i).getResultList();
				} else if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
					return InstanceP.findInstancePsBy(i, Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
				} else {
					return InstanceP.findInstancePsBy(i, Company.findCompany(Commons.getCurrentSession().getCompanyId()), user).getResultList();
				}
			} else if (Commons.EDITION_ENABLED == Commons.PRIVATE_CLOUD_EDITION_ENABLED) {
				return InstanceP.findInstancePsByInfra(i).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<InstanceP> findAllWithSystemVms(int start, int max, String search) {
		try {
			return InstanceP.findAllInstancePs(start, max, search);
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<InstanceP> findAll4Attach(AddressInfoP a) {
		try {
			// return InstanceP.findAllInstancePs();
			AddressInfoP aLocal = AddressInfoP.findAddressInfoP(a.getId());
			Infra infra = aLocal.getAsset().getProductCatalog().getInfra();
			List<InstanceP> instances = null;

			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				instances = InstanceP.findInstancePsByInfra(infra).getResultList();
			} else if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
				instances = InstanceP.findInstancePsBy(infra, Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
			} else if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				instances = InstanceP.findInstancePsBy(infra, Company.findCompany(Commons.getCurrentSession().getCompanyId()), Commons.getCurrentUser()).getResultList();
			}
			List<InstanceP> instances2return = new ArrayList<InstanceP>();
			for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
				InstanceP instanceP = (InstanceP) iterator.next();
				try {
					AddressInfoP addressInfoP = AddressInfoP.findAddressInfoPsByInstanceIdLike(instanceP.getInstanceId()).getSingleResult();
					System.out.println("addressInfoP.getStatus() = " + addressInfoP.getStatus() + "  " + addressInfoP.getPublicIp());
					// get instances which are auto_assigned, skip the rest
					// which is usually in status associated.
					if (addressInfoP.getStatus().equals(Commons.ipaddress_STATUS.associated + "")) {
						continue;
					}
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
				instances2return.add(instanceP);
			}
			return instances2return;

		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public void terminateCompute(int id) {
		InstanceP instanceP = InstanceP.findInstanceP(id);
		try {

			Infra infra = instanceP.getAsset().getProductCatalog().getInfra();
			if (infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
				computeWorker.terminateCompute(instanceP.getAsset().getProductCatalog().getInfra(), id, Commons.getCurrentUser().getEmail());
			} else {
				vmwareComputeWorker.terminateCompute(infra, instanceP.getId(), Commons.getCurrentUser().getEmail());
			}
			instanceP.setState(Commons.REQUEST_STATUS.TERMINATING + "");
			instanceP = instanceP.merge();
			Commons.setAssetEndTime(instanceP.getAsset());
			Commons.setSessionMsg("Terminate for Instance " + instanceP.getName() + " scheduled");
			accountLogService.saveLog("Compute instance '" + instanceP.getName() + "' terminated with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			Commons.setSessionMsg("Error while scheduling termination for Instance " + instanceP.getName());
			accountLogService.saveLog("Error in Compute instance '" + instanceP.getName() + "' termination with ID " + id + ", " + e.getMessage(),
					Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
		}
	}

	@RemoteMethod
	public void stopCompute(int id) {
		InstanceP instanceP = InstanceP.findInstanceP(id);
		try {
			Infra infra = instanceP.getAsset().getProductCatalog().getInfra();

			if (infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
				computeWorker.stopCompute(instanceP.getAsset().getProductCatalog().getInfra(), id, Commons.getCurrentUser().getEmail());
			} else {
				vmwareComputeWorker.stopCompute(infra, instanceP.getId(), Commons.getCurrentUser().getEmail());
			}
			instanceP.setState(Commons.REQUEST_STATUS.STOPPING + "");
			instanceP = instanceP.merge();
			Commons.setSessionMsg("Stop for Instance " + instanceP.getName() + " scheduled");
			accountLogService.saveLog("Compute instance '" + instanceP.getName() + "' stopped with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			Commons.setSessionMsg("Error in Stopping Instance " + instanceP.getName());
			accountLogService.saveLog("Error while stopping Compute instance '" + instanceP.getName() + "' with ID " + id + ", " + e.getMessage(),
					Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
		}

	}

	@RemoteMethod
	public void startCompute(int id) {
		InstanceP instanceP = InstanceP.findInstanceP(id);
		try {
			Infra infra = instanceP.getAsset().getProductCatalog().getInfra();

			if (infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
				computeWorker.startCompute(instanceP.getAsset().getProductCatalog().getInfra(), id, Commons.getCurrentUser().getEmail());
			} else {
				vmwareComputeWorker.startCompute(infra, instanceP.getId(), Commons.getCurrentUser().getEmail());
			}
			instanceP.setState(Commons.REQUEST_STATUS.STARTING + "");
			instanceP = instanceP.merge();
			Commons.setSessionMsg("Start for Instance " + instanceP.getName() + " scheduled");
			accountLogService.saveLog("Compute instance '" + instanceP.getName() + "' started with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			Commons.setSessionMsg("Error in Starting Instance " + instanceP.getName());
			accountLogService.saveLog("Error while starting Compute instance '" + instanceP.getName() + "' with ID " + id + ", " + e.getMessage(),
					Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
		}
	}

	@RemoteMethod
	public void restartCompute(int id) {
		InstanceP instanceP = InstanceP.findInstanceP(id);
		try {
			Infra infra = instanceP.getAsset().getProductCatalog().getInfra();

			if (infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
				computeWorker.restartCompute(instanceP.getAsset().getProductCatalog().getInfra(), id, Commons.getCurrentUser().getEmail());
			} else {
				vmwareComputeWorker.restartCompute(infra, instanceP.getId(), Commons.getCurrentUser().getEmail());
			}
			instanceP.setState(Commons.REQUEST_STATUS.RESTARTING + "");
			instanceP = instanceP.merge();
			Commons.setSessionMsg("Restart for Instance " + instanceP.getName() + " scheduled");
			accountLogService.saveLog("Compute instance '" + instanceP.getName() + "' restarted with ID " + id, Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			Commons.setSessionMsg("Erorr in restarting Instance " + instanceP.getName());
			accountLogService.saveLog("Error while restarting Compute instance '" + instanceP.getName() + "' with ID " + id + ", " + e.getMessage(),
					Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL.ordinal(), Commons.getCurrentUser().getEmail());
		}
	}

	@RemoteMethod
	public List<ProductCatalog> findProductType() {

		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			return ProductCatalog.findProductCatalogsByProductTypeEquals(Commons.ProductType.ComputeInstance.getName()).getResultList();
		} else {

			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(Commons.ProductType.ComputeInstance.getName(),
					Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
		}

	}

}// end of class InstancePController

