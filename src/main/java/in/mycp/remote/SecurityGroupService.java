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

import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.Infra;
import in.mycp.domain.IpPermissionP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.utils.Commons;
import in.mycp.workers.SecurityGroupWorker;
import in.mycp.workers.VmwareSecurityGroupWorker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "GroupDescriptionP")
public class SecurityGroupService {

	private static final Logger log = Logger.getLogger(SecurityGroupService.class.getName());

	@Autowired
	SecurityGroupWorker securityGroupWorker;

	@Autowired
	VmwareSecurityGroupWorker vmwareSecurityGroupWorker;
	
	@Autowired
	WorkflowService workflowService;

	@Autowired
	IpPermissionService ipPermissionService;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;
	
	

	@RemoteMethod
	public GroupDescriptionP saveOrUpdate(GroupDescriptionP instance) {
		try {
			
			
			//for update , use different logic
			if (instance.getId() != null && instance.getId() > 0) {
				return updateSecurityGroup(instance);
			}
			
			try {
				String s = instance.getName();
				instance.setName(StringUtils.replace(s, " ", "_"));
				instance.setName(instance.getName() + "_" + Commons.getCurrentSession().getCompany());
			} catch (Exception e) {
			}
			
			// check unique name per infra
			try {
				
					
				if (GroupDescriptionP.findGroupDescriptionPsByNameEqualsAndCompanyEquals(instance.getName(), Commons.getCurrentUser().getDepartment().getCompany())
						.getSingleResult().getId() > 0) {
					throw new Exception("Security group with this name already exists for this account, Choose another name.");
				}
			} catch (EmptyResultDataAccessException ersde) {
				// do nothing 
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage().contains("Security group with this name already exists")) {
					throw new Exception("Security group with this name already exists for this account, Choose another name.");
				}
			}

			AssetType assetType = AssetType.findAssetTypesByNameEquals(Commons.ASSET_TYPE.SecurityGroup + "").getSingleResult();
			if (instance.getId() != null && instance.getId() > 0) {
				instance = GroupDescriptionP.findGroupDescriptionP(instance.getId());
			} else {
				User currentUser = Commons.getCurrentUser();

				currentUser = User.findUser(currentUser.getId());
				Company company = currentUser.getDepartment().getCompany();
				Asset asset = Commons.getNewAsset(assetType, currentUser, instance.getProduct(), reportService, company);
				asset.setProject(Project.findProject(instance.getProjectId()));
				instance.setAsset(asset);

				if (true == assetType.getWorkflowEnabled()) {
					instance.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "");
					instance = instance.merge();

					accountLogService.saveLog("Security Group '" + instance.getName() + "' with ID " + instance.getName()
							+ " requested, workflow started, pending approval.", Commons.task_name.SECURITYGROUP.name(), Commons.task_status.SUCCESS.ordinal(),
							currentUser.getEmail());

					Commons.createNewWorkflow(workflowService.createProcessInstance(Commons.PROCESS_DEFN.SecGroup_Request + ""), instance.getId(), assetType.getName());

				} else {

					accountLogService.saveLog("Security Group '" + instance.getName() + "' with ID " + instance.getName()
							+ " requested, workflow approved automatically.", Commons.task_name.SECURITYGROUP.name(), Commons.task_status.SUCCESS.ordinal(),
							currentUser.getEmail());

					instance.setStatus(Commons.secgroup_STATUS.starting + "");
					instance = instance.merge();
					workflowApproved(instance);
				}
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("Security group with this name already exists for this account")) {
				Commons.setSessionMsg("Security group with this name already exists for this account, Choose another name.");
			}
			log.error(e);
		}
		return null;
	}// end of saveOrUpdate(GroupDescriptionP

	
	
	public GroupDescriptionP updateSecurityGroup(GroupDescriptionP instance) {
		GroupDescriptionP local = GroupDescriptionP.findGroupDescriptionP(instance.getId());
		try {
			
			Infra infra = local.getAsset().getProductCatalog().getInfra();
			if(infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA){
				securityGroupWorker.createSecurityGroup(infra, local, Commons.getCurrentUser().getEmail());
			}else if(infra.getInfraType().getId() == Commons.INFRA_TYPE_VCLOUD){
				vmwareSecurityGroupWorker.createOrUpdateFirewallRules(infra, local, Commons.getCurrentUser().getEmail());
			}
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return local;
	}// end of createCompute(InstanceP

	
	@RemoteMethod
	public void workflowApproved(GroupDescriptionP instance) {
		try {
			instance.setStatus(Commons.secgroup_STATUS.starting + "");
			instance = instance.merge();
			securityGroupWorker.createSecurityGroup(instance.getAsset().getProductCatalog().getInfra(), instance, Commons.getCurrentUser().getEmail());
			Set<IpPermissionP> perms = instance.getIpPermissionPs();
			for (Iterator iterator = perms.iterator(); iterator.hasNext();) {
				IpPermissionP ipPermissionP = (IpPermissionP) iterator.next();
				ipPermissionService.workflowApproved(ipPermissionP);
			}
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}// end of createCompute(InstanceP

	@RemoteMethod
	public void remove(int id) {
		try {
			List<IpPermissionP> IpPermissionPs = IpPermissionP.findIpPermissionPsByGroupDescription(GroupDescriptionP.findGroupDescriptionP(id)).getResultList();
			for (Iterator iterator = IpPermissionPs.iterator(); iterator.hasNext();) {
				IpPermissionP ipPermissionP = (IpPermissionP) iterator.next();
				ipPermissionP.remove();
			}
			try {
				securityGroupWorker.deleteSecurityGroup(GroupDescriptionP.findGroupDescriptionP(id).getAsset().getProductCatalog().getInfra(),
						GroupDescriptionP.findGroupDescriptionP(id), Commons.getCurrentUser().getEmail());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}

			GroupDescriptionP g = GroupDescriptionP.findGroupDescriptionP(id);
			g.setStatus(Commons.secgroup_STATUS.inactive+""); 
			g.merge();
			Asset a = g.getAsset();
			a.setActive(false);
			a.merge();
			if (a != null && a.getEndTime() != null) {
				Commons.setAssetEndTime(a);
			}

			// g.remove();
			/*
			 * g.setStatus(Commons.secgroup_STATUS.inactive+""); g.merge();
			 */
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public GroupDescriptionP findById(int id) {
		try {
			GroupDescriptionP instance = GroupDescriptionP.findGroupDescriptionP(id);
			instance.setProduct("" + instance.getAsset().getProductCatalog().getId());
			return instance;
			// return GroupDescriptionP.findGroupDescriptionP(id);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<GroupDescriptionP> findAll4List() {
		try {
			List<GroupDescriptionP> gds = null;
			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				gds = GroupDescriptionP.findAllActiveGroupDescriptionPs();
			} else {
				gds = GroupDescriptionP.findActiveGroupDescriptionPsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()), 0, 100, "")
						.getResultList();
			}
			for (Iterator iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator.next();

				List<IpPermissionP> ips = IpPermissionP.findIpPermissionPsByGroupDescription(groupDescriptionP).getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2.next();
					hset.add(ipPermissionP);
				}

				groupDescriptionP.setIpPermissionPs(hset);

			}

			return gds;
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll4List

	@RemoteMethod
	public List<GroupDescriptionP> findAll(int start, int max, String search) {
		try {
			List<GroupDescriptionP> gds = null;

			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				gds = GroupDescriptionP.findActiveGroupDescriptionPsByUser(user, start, max, search).getResultList();
			} else if (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {

				gds = GroupDescriptionP.findActiveGroupDescriptionPsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()), start, max, search)
						.getResultList();
			} else {
				gds = GroupDescriptionP.findAllActiveGroupDescriptionPs(start, max, search);
			}

			for (Iterator iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator.next();

				List<IpPermissionP> ips = IpPermissionP.findIpPermissionPsByGroupDescription(groupDescriptionP).getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2.next();
					hset.add(ipPermissionP);
				}

				groupDescriptionP.setIpPermissionPs(hset);

			}

			return gds;
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List findAll4Edit(int start, int max, String search) {
		try {
			List<GroupDescriptionP> gds = null;

			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				gds = GroupDescriptionP.findAllGroupDescriptionPsByUser(user, start, max, search).getResultList();
			} else if (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
				gds = GroupDescriptionP.findAllGroupDescriptionPsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId()), start, max, search)
						.getResultList();
			} else {
				gds = GroupDescriptionP.findAllGroupDescriptionPs(start, max, search);
			}

			for (Iterator<GroupDescriptionP> iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator.next();

				List<IpPermissionP> ips = IpPermissionP.findIpPermissionPsByGroupDescription(groupDescriptionP).getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2.next();
					hset.add(ipPermissionP);
				}

				groupDescriptionP.setIpPermissionPs(hset);

			}

			return gds;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<ProductCatalog> findProductType() {

		// do some circus to get the vcloud keypair product type out of the
		// returned list.

		List<ProductCatalog> pcs2return = new ArrayList<ProductCatalog>();
		List<ProductCatalog> pcs = null;
		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			pcs = ProductCatalog.findProductCatalogsByProductTypeEquals(Commons.ProductType.SecurityGroup.getName()).getResultList();
		} else {
			pcs = ProductCatalog.findProductCatalogsByProductTypeAndCompany(Commons.ProductType.SecurityGroup.getName(),
					Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
		}

		for (Iterator iterator = pcs.iterator(); iterator.hasNext();) {
			ProductCatalog productCatalog = (ProductCatalog) iterator.next();
			if (productCatalog.getInfra().getInfraType().getId() == Commons.INFRA_TYPE_AWS || productCatalog.getInfra().getInfraType().getId() == Commons.INFRA_TYPE_EUCA) {
				pcs2return.add(productCatalog);
			}
		}
		return pcs2return;

	}

	@RemoteMethod
	public List<in.mycp.domain.GroupDescriptionP> findActiveGroupDescriptionPsByInfra(Infra infra) {

		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.EDITION_ENABLED == Commons.HOSTED_EDITION_ENABLED) {
			// if super admin, show all sec groups across all accounts in teh
			// same cloud
			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return GroupDescriptionP.findActiveGroupDescriptionPsByInfra(infra).getResultList();
			} else if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")
					|| Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				if(infra.getInfraType().getId() == Commons.INFRA_TYPE_VCLOUD){
					return GroupDescriptionP.findActiveGroupDescriptionPsByInfra(infra).getResultList();
				}else{
					return GroupDescriptionP.findActiveGroupDescriptionPsBy(infra, Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();	
				}
				
			}
		} else if (Commons.EDITION_ENABLED == Commons.PRIVATE_CLOUD_EDITION_ENABLED) {
			return GroupDescriptionP.findActiveGroupDescriptionPsByInfra(infra).getResultList();
		}
		return null;
	}

}// end of class SecurityGroupService

