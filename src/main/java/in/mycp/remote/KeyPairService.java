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
import in.mycp.domain.Infra;
import in.mycp.domain.KeyPairInfoP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.utils.Commons;
import in.mycp.workers.KeyPairWorker;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "KeyPairInfoP")
public class KeyPairService {

	private static final Logger log = Logger.getLogger(KeyPairService.class
			.getName());

	@Autowired
	KeyPairWorker keyPairWorker;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public void save(KeyPairInfoP instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}// end of save(KeyPairInfoP

	@RemoteMethod
	public FileTransfer writeFileContentInResponse(int id) throws IOException {
		return new FileTransfer("private.key", "application/key", KeyPairInfoP
				.findKeyPairInfoP(id).getKeyMaterial().getBytes());
	}

	@RemoteMethod
	public KeyPairInfoP saveOrUpdate(KeyPairInfoP instance) {
		try {

			// check unique name per infra
			try {
				if (KeyPairInfoP
						.findKeyPairInfoPsByKeyNameEqualsAndCompanyEquals(
								instance.getKeyName(),
								Commons.getCurrentUser().getDepartment().getCompany())
						.getSingleResult().getId() > 0) {
					throw new Exception(
							"Key with this name already exists for this account, Choose another name.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage().contains("returns more than one elements")
						|| e.getMessage()
								.contains(
										"Key with this name already exists for this account")) {
					throw new Exception(
							"Key with this name already exists for this account, Choose another name.");
				}
			}

			AssetType assetType = AssetType.findAssetTypesByNameEquals(
					"KeyPair").getSingleResult();
			if (instance.getId() != null && instance.getId() > 0) {
			} else {
				User currentUser = Commons.getCurrentUser();
				currentUser = User.findUser(currentUser.getId());
				
				Company company = currentUser.getDepartment().getCompany();
				Asset asset = Commons.getNewAsset(assetType, currentUser,instance.getProduct(), reportService,company);
				asset.setProject(Project.findProject(instance.getProjectId()));
				instance.setAsset(asset);
				instance = instance.merge();
				if (true == assetType.getWorkflowEnabled()) {

					accountLogService
							.saveLogAndSendMail(
									"KeyPair '"+instance.getKeyName()+"' with ID "
											+ instance.getId()
											+ " requested, workflow started, pending approval.",
									Commons.task_name.KEYPAIR.name(),
									Commons.task_status.SUCCESS.ordinal(),
									currentUser.getEmail());

					Commons.createNewWorkflow(
							workflowService
									.createProcessInstance(Commons.PROCESS_DEFN.Keys_Request
											+ ""), instance.getId(), asset
									.getAssetType().getName());
					instance.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL
							+ "");
					instance = instance.merge();
				} else {

					accountLogService
							.saveLogAndSendMail(
									"KeyPair '"+instance.getKeyName()+"' with ID "
											+ instance.getId()
											+ " requested, workflow approved automatically.",
									Commons.task_name.KEYPAIR.name(),
									Commons.task_status.SUCCESS.ordinal(),
									currentUser.getEmail());

					instance.setStatus(Commons.keypair_STATUS.starting + "");
					instance = instance.merge();
					workflowApproved(instance);
				}
			}
			Commons.setSessionMsg("Key Saved");
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			if (e.getMessage().contains("Key with this name already exists")
					|| e.getMessage().contains(Commons.QUOTA_EXCEED_MSG)) {
				Commons.setSessionMsg(e.getMessage());
			}

		}
		return null;
	}// end of saveOrUpdate(KeyPairInfoP

	@RemoteMethod
	public void workflowApproved(KeyPairInfoP instance) {
		try {
			instance.setStatus(Commons.keypair_STATUS.starting + "");
			instance = instance.merge();
			createKeyPair(instance.getKeyName());
			Commons.setSessionMsg("Scheduled Key creation");
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
			Commons.setSessionMsg("Error while scheduling Key creation");
		}
	}

	@RemoteMethod
	public void remove(int id) {
		try {
			deleteKeyPair(id);
			KeyPairInfoP.findKeyPairInfoP(id).remove();
			Commons.setSessionMsg("Scheduled Key removal");
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
			Commons.setSessionMsg("Error during Scheduled Key removal");
		}
	}// end of method remove(int id

	@RemoteMethod
	public KeyPairInfoP findById(int id) {
		try {

			KeyPairInfoP instance = KeyPairInfoP.findKeyPairInfoP(id);
			instance.setProduct(""
					+ instance.getAsset().getProductCatalog().getId());
			return instance;
			// return KeyPairInfoP.findKeyPairInfoP(id);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<KeyPairInfoP> findAll4List() {
		try {

			User user = Commons.getCurrentUser();
			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return KeyPairInfoP.findAllKeyPairInfoPs();
			} else {
				return KeyPairInfoP.findKeyPairInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), 0, 100, "").getResultList();
			}
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll4List

	@RemoteMethod
	public List<KeyPairInfoP> findAll(int start, int max, String search) {
		try {

			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				return KeyPairInfoP.findKeyPairInfoPsByUser(user, start, max,
						search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")) {
				return KeyPairInfoP.findKeyPairInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			}

			return KeyPairInfoP.findAllKeyPairInfoPs(start, max, search);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();

		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public void deleteKeyPair(int id) {
		try {
			KeyPairInfoP key = KeyPairInfoP.findKeyPairInfoP(id);
			Commons.setAssetEndTime(key.getAsset());
			KeyPairInfoP keyPairInfoP = KeyPairInfoP
					.findKeyPairInfoPsByKeyNameEquals(key.getKeyName())
					.getSingleResult();
			keyPairWorker.deleteKeyPair(keyPairInfoP.getAsset()
					.getProductCatalog().getInfra(), keyPairInfoP, Commons
					.getCurrentUser().getEmail());

		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}

	@RemoteMethod
	public void createKeyPair(String name) {
		try {
			KeyPairInfoP keyPairInfoP = KeyPairInfoP
					.findKeyPairInfoPsByKeyNameEquals(name).getSingleResult();
			keyPairWorker.createKeyPair(keyPairInfoP.getAsset()
					.getProductCatalog().getInfra(), keyPairInfoP, Commons
					.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}

	@RemoteMethod
	public List<ProductCatalog> findProductType() {
		if (Commons.getCurrentUser().getRole().getName()
				.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			return ProductCatalog.findProductCatalogsByProductTypeEquals(
					Commons.ProductType.KeyPair.getName()).getResultList();
		} else {
			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(
					Commons.ProductType.KeyPair.getName(),
					Company.findCompany(Commons.getCurrentSession()
							.getCompanyId())).getResultList();
		}
	}
	
	@RemoteMethod
	public List<KeyPairInfoP> findKeyPairInfoPsByInfra(Infra infra) {
		List<KeyPairInfoP> infoPs = KeyPairInfoP.findKeyPairInfoPsByInfra(infra).getResultList();
		System.out.println(infoPs);
		return infoPs;
    }

}// end of class KeyPairInfoPController

