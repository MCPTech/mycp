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
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.utils.Commons;
import in.mycp.workers.IpAddressWorker;

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
@RemoteProxy(name = "AddressInfoP")
public class AddressInfoPService {

	private static final Logger log = Logger
			.getLogger(AddressInfoPService.class.getName());

	@Autowired
	IpAddressWorker ipAddressWorker;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public AddressInfoP saveOrUpdate(AddressInfoP instance) {
		try {
			// if for update only
			if (instance != null && instance.getId() != null
					&& instance.getId() > 0) {
				AddressInfoP instance_local = AddressInfoP
						.findAddressInfoP(instance.getId());
				instance_local.setReason(instance.getReason());
				instance_local.setName(instance.getName());
				return instance_local.merge();
			}
			String productId = instance.getProduct();
			instance = instance.merge();
			AssetType assetType = AssetType.findAssetTypesByNameEquals(
					"" + Commons.ASSET_TYPE.IpAddress).getSingleResult();
			User currentUser = Commons.getCurrentUser();
			long allAssetTotalCosts = reportService.getAllAssetCosts()
					.getTotalCost();
			currentUser = User.findUser(currentUser.getId());
			Company company = currentUser.getProject().getDepartment()
					.getCompany();
			Asset asset = Commons.getNewAsset(assetType, currentUser,
					productId, allAssetTotalCosts, company);
			asset.setActive(false);
			instance.setAsset(asset);
			instance = instance.merge();

			if (true == assetType.getWorkflowEnabled()) {
				accountLogService
						.saveLog(
								"Ip Address with ID "
										+ instance.getId()
										+ " requested, workflow started, pending approval.",
								Commons.task_name.IPADDRESS.name(),
								Commons.task_status.SUCCESS.ordinal(),
								currentUser.getEmail());

				Commons.createNewWorkflow(
						workflowService
								.createProcessInstance(Commons.PROCESS_DEFN.IpAddress_Request
										+ ""), instance.getId(), asset
								.getAssetType().getName());
				instance.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL
						+ "");
				instance = instance.merge();
			} else {
				accountLogService
						.saveLog(
								"Ip Address with ID "
										+ instance.getId()
										+ " requested, workflow approved automatically.",
								Commons.task_name.IPADDRESS.name(),
								Commons.task_status.SUCCESS.ordinal(),
								currentUser.getEmail());

				instance.setStatus(Commons.ipaddress_STATUS.starting + "");
				instance = instance.merge();
				workflowApproved(instance);
			}
			Commons.setSessionMsg("Ip Address saved");
			return instance;
		} catch (Exception e) {
			// e.printStackTrace();
			Commons.setSessionMsg("Error while saving Instance "
					+ instance.getName() + "<br> Reason: " + e.getMessage());
			log.error(e);
		}
		return null;
	}// end of saveOrUpdate(AddressInfoP

	/**
	 * This is called after workflow is approved. this triggers the ip address
	 * creation in the backend infrastructure
	 * 
	 * 
	 * @param instance
	 */
	public void workflowApproved(AddressInfoP instance) {
		log.info("Workflow approved for " + instance.getId() + " "
				+ instance.getName());
		instance.setStatus(Commons.ipaddress_STATUS.starting + "");
		instance = instance.merge();
		allocateAddress(instance.getId());
	}

	@RemoteMethod
	public void remove(int id) {
		try {
			// releaseAddress(id);
			Commons.setAssetEndTime(AddressInfoP.findAddressInfoP(id)
					.getAsset());
			AddressInfoP.findAddressInfoP(id).remove();
			Commons.setSessionMsg("Scheduled Ip Address remove");
		} catch (Exception e) {
			// e.printStackTrace();
			Commons.setSessionMsg("Error while Scheduling Ip Address remove");
			log.error(e);
		}
	}// end of method remove(int id

	@RemoteMethod
	public AddressInfoP findById(int id) {
		try {
			AddressInfoP instance = AddressInfoP.findAddressInfoP(id);
			instance.setProduct(""
					+ instance.getAsset().getProductCatalog().getId());
			return instance;

		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e);
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<AddressInfoP> findAll(int start, int max, String search) {
		try {

			User user = Commons.getCurrentUser();
			List<AddressInfoP> allAddresses = null;
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				allAddresses = AddressInfoP.findAddressInfoPsByUser(user,
						start, max, search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")
					|| user.getRole().getName()
							.equals(Commons.ROLE.ROLE_ADMIN + "")) {
				allAddresses = AddressInfoP.findAddressInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			} else {
				allAddresses = AddressInfoP.findAllAddressInfoPs(start, max,
						search);
			}

			return allAddresses;
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e);
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public void associateAddress(AddressInfoP instance) {
		try {

			log.info(instance.getInstanceId() + "   " + instance.getPublicIp());
			String orig_instanceIdFromJS = instance.getInstanceId();
			String orig_publicIpFromJS = instance.getPublicIp();
			instance = AddressInfoP.findAddressInfoPsByInstanceIdLike(
					instance.getInstanceId()).getSingleResult();

			// setit back to clean instance ID because it is in format
			// 'i-595E09AE (eucalyptus)'
			instance.setInstanceId(orig_instanceIdFromJS);
			instance.setPublicIp(orig_publicIpFromJS);

			ipAddressWorker.associateAddress(
					AddressInfoP.findAddressInfoP(instance.getId()).getAsset()
							.getProductCatalog().getInfra(), instance, Commons
							.getCurrentUser().getEmail());
			Commons.setSessionMsg("Scheduling Ip Address association");
			// AddressInfoP.findAddressInfoP(id).remove();
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip Address association");
			e.printStackTrace();
			log.error(e);
		}
	}// end of method associate(int id

	@RemoteMethod
	public void disassociateAddress(int id) {
		try {
			AddressInfoP instance = AddressInfoP.findAddressInfoP(id);
			ipAddressWorker.disassociateAddress(
					AddressInfoP.findAddressInfoP(instance.getId()).getAsset()
							.getProductCatalog().getInfra(), instance, Commons
							.getCurrentUser().getEmail());
			Commons.setSessionMsg("Scheduling Ip Address disassociation");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip Address disassociation");
			e.printStackTrace();
			log.error(e);
		}
	}// end of method disassociateAddress(int id

	@RemoteMethod
	public void allocateAddress(int id) {
		try {

			AddressInfoP adressInfoP = AddressInfoP.findAddressInfoP(id);
			log.info("Calling allocate address for Workflow approved for "
					+ adressInfoP.getId() + " " + adressInfoP.getName());
			ipAddressWorker.allocateAddress(
					AddressInfoP.findAddressInfoP(adressInfoP.getId())
							.getAsset().getProductCatalog().getInfra(),
					adressInfoP, Commons.getCurrentUser().getEmail());
			Commons.setSessionMsg("Scheduling Ip Address allocate");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip Address allocate");
			e.printStackTrace();
			log.error(e);
		}
	}// end of method allocateAddress

	@RemoteMethod
	public void releaseAddress(int id) {
		try {
			AddressInfoP adressInfoP = AddressInfoP.findAddressInfoP(id);
			log.info("releasing IP adress " + adressInfoP.getPublicIp());
			if (adressInfoP.getInstanceId() != null
					&& adressInfoP.getInstanceId().startsWith("available")) {
				ipAddressWorker.releaseAddress(adressInfoP.getAsset()
						.getProductCatalog().getInfra(), adressInfoP, Commons
						.getCurrentUser().getEmail());
			} else {
				log.error("Cant release addresses not marked as available.");
			}
			Commons.setSessionMsg("Scheduling Ip Address release");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Ip Address release");
			e.printStackTrace();
			log.error(e);
		}
	}// end of method allocateAddress

	@RemoteMethod
	public List<ProductCatalog> findProductType() {
		if (Commons.getCurrentUser().getRole().getName()
				.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			return ProductCatalog.findProductCatalogsByProductTypeEquals(
					Commons.ProductType.IpAddress.getName()).getResultList();
		} else {

			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(
					Commons.ProductType.IpAddress.getName(),
					Company.findCompany(Commons.getCurrentSession()
							.getCompanyId())).getResultList();
		}

	}

}// end of class AddressInfoPController

