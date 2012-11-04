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

import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.IpPermissionP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.utils.Commons;
import in.mycp.workers.SecurityGroupWorker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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

@RemoteProxy(name = "GroupDescriptionP")
public class SecurityGroupService {

	private static final Logger log = Logger
			.getLogger(SecurityGroupService.class.getName());

	@Autowired
	SecurityGroupWorker securityGroupWorker;

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
			try {
				String s = instance.getName();
				instance.setName(StringUtils.replace(s, " ", "_"));

			} catch (Exception e) {
			}
			// String companyName = Commons.getCurrentSession().getCompany();
			/*
			 * String pId="";String dId="";String cId=""; try{pId =
			 * Commons.getCurrentUser().getProject().getId()+"";}catch(Exception
			 * e){} try{dId =
			 * Commons.getCurrentUser().getProject().getDepartment
			 * ().getId()+"";}catch(Exception e){} try{cId =
			 * Commons.getCurrentUser
			 * ().getProject().getDepartment().getCompany()
			 * .getId()+"";}catch(Exception e){}
			 * 
			 * if(instance!=null && instance.getName()!=null &&
			 * instance.getName().indexOf("_"+companyName) <0){
			 * instance.setName(instance.getName()+"_"+pId+"_"+dId+"_"+cId); }
			 */
			// check unique name per infra
			try {
				if (GroupDescriptionP
						.findGroupDescriptionPsByNameEqualsAndCompanyEquals(
								instance.getName(),
								Commons.getCurrentUser().getProject()
										.getDepartment().getCompany())
						.getSingleResult().getId() > 0) {
					throw new Exception(
							"Security group with this name already exists for this account, Choose another name.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage().contains("returns more than one elements")
						|| e.getMessage().contains(
								"Security group with this name already exists")) {
					throw new Exception(
							"Security group with this name already exists for this account, Choose another name.");
				}
			}

			AssetType assetTypeSecurityGroup = AssetType
					.findAssetTypesByNameEquals(
							Commons.ASSET_TYPE.SecurityGroup + "")
					.getSingleResult();
			if (instance.getId() != null && instance.getId() > 0) {
				instance = GroupDescriptionP.findGroupDescriptionP(instance
						.getId());
			} else {
				User currentUser = Commons.getCurrentUser();
				long allAssetTotalCosts = reportService.getAllAssetCosts()
						.getTotalCost();
				currentUser = User.findUser(currentUser.getId());
				Company company = currentUser.getProject().getDepartment()
						.getCompany();
				Asset asset = Commons.getNewAsset(assetTypeSecurityGroup,
						currentUser, instance.getProduct(), allAssetTotalCosts,
						company);
				instance.setAsset(asset);

				if (true == assetTypeSecurityGroup.getWorkflowEnabled()) {
					instance.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL
							+ "");
					instance = instance.merge();
					
					accountLogService.saveLogAndSendMail("Security Group '"+instance.getName()+"' with ID "+instance.getId()+" requested, workflow started, pending approval.", Commons.task_name.SECURITYGROUP.name(), 
							Commons.task_status.SUCCESS.ordinal(),currentUser.getEmail());
					
					Commons.createNewWorkflow(
							workflowService
									.createProcessInstance(Commons.PROCESS_DEFN.SecGroup_Request
											+ ""), instance.getId(),
							assetTypeSecurityGroup.getName());

				} else {
					
					accountLogService.saveLogAndSendMail("Security Group '"+instance.getName()+"' with ID "+instance.getId()+" requested, workflow approved automatically.", Commons.task_name.SECURITYGROUP.name(), 
							Commons.task_status.SUCCESS.ordinal(),currentUser.getEmail());
					
					instance.setStatus(Commons.secgroup_STATUS.starting + "");
					instance = instance.merge();
					workflowApproved(instance);
				}
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()
					.contains(
							"Security group with this name already exists for this account")) {
				Commons.setSessionMsg("Security group with this name already exists for this account, Choose another name.");
			}
			log.error(e);
		}
		return null;
	}// end of saveOrUpdate(GroupDescriptionP

	@RemoteMethod
	public void workflowApproved(GroupDescriptionP instance) {
		try {
			instance.setStatus(Commons.secgroup_STATUS.starting + "");
			instance = instance.merge();
			securityGroupWorker.createSecurityGroup(instance.getAsset()
					.getProductCatalog().getInfra(), instance, Commons
					.getCurrentUser().getEmail());
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
			List<IpPermissionP> IpPermissionPs = IpPermissionP
					.findIpPermissionPsByGroupDescription(
							GroupDescriptionP.findGroupDescriptionP(id))
					.getResultList();
			for (Iterator iterator = IpPermissionPs.iterator(); iterator
					.hasNext();) {
				IpPermissionP ipPermissionP = (IpPermissionP) iterator.next();
				ipPermissionP.remove();
			}
			try {
				securityGroupWorker.deleteSecurityGroup(GroupDescriptionP
						.findGroupDescriptionP(id).getAsset()
						.getProductCatalog().getInfra(), GroupDescriptionP
						.findGroupDescriptionP(id), Commons.getCurrentUser()
						.getEmail());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}

			GroupDescriptionP g = GroupDescriptionP.findGroupDescriptionP(id);
			Commons.setAssetEndTime(g.getAsset());
			g.remove();
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
			GroupDescriptionP instance = GroupDescriptionP
					.findGroupDescriptionP(id);
			instance.setProduct(""
					+ instance.getAsset().getProductCatalog().getId());
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
			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				gds = GroupDescriptionP.findAllActiveGroupDescriptionPs();
			} else {
				gds = GroupDescriptionP.findActiveGroupDescriptionPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), 0, 100, "").getResultList();
			}
			for (Iterator iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator
						.next();

				List<IpPermissionP> ips = IpPermissionP
						.findIpPermissionPsByGroupDescription(groupDescriptionP)
						.getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2
							.next();
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
				gds = GroupDescriptionP.findActiveGroupDescriptionPsByUser(
						user, start, max, search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")
					|| user.getRole().getName()
							.equals(Commons.ROLE.ROLE_ADMIN + "")) {

				gds = GroupDescriptionP.findActiveGroupDescriptionPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			} else {
				gds = GroupDescriptionP.findAllActiveGroupDescriptionPs(start,
						max, search);
			}

			for (Iterator iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator
						.next();

				List<IpPermissionP> ips = IpPermissionP
						.findIpPermissionPsByGroupDescription(groupDescriptionP)
						.getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2
							.next();
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
				gds = GroupDescriptionP.findAllGroupDescriptionPsByUser(user,
						start, max, search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")
					|| user.getRole().getName()
							.equals(Commons.ROLE.ROLE_ADMIN + "")) {
				gds = GroupDescriptionP.findAllGroupDescriptionPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			} else {
				gds = GroupDescriptionP.findAllGroupDescriptionPs(start, max,
						search);
			}

			for (Iterator iterator = gds.iterator(); iterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator
						.next();

				List<IpPermissionP> ips = IpPermissionP
						.findIpPermissionPsByGroupDescription(groupDescriptionP)
						.getResultList();

				Set<IpPermissionP> hset = new HashSet<IpPermissionP>();

				for (Iterator iterator2 = ips.iterator(); iterator2.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator2
							.next();
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
	public List<ProductCatalog> findProductType() {
		if (Commons.getCurrentUser().getRole().getName()
				.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			return ProductCatalog.findProductCatalogsByProductTypeEquals(
					Commons.ProductType.SecurityGroup.getName())
					.getResultList();
		} else {
			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(
					Commons.ProductType.SecurityGroup.getName(),
					Company.findCompany(Commons.getCurrentSession()
							.getCompanyId())).getResultList();
		}
	}

}// end of class SecurityGroupService

