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
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.SnapshotInfoP;
import in.mycp.domain.User;
import in.mycp.domain.VolumeInfoP;
import in.mycp.utils.Commons;
import in.mycp.workers.SnapshotWorker;

import java.util.ArrayList;
import java.util.Iterator;
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

@RemoteProxy(name = "SnapshotInfoP")
public class SnapshotService {

	private static final Logger log = Logger.getLogger(SnapshotService.class
			.getName());

	@Autowired
	WorkflowService workflowService;

	@Autowired
	SnapshotWorker snapshotWorker;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;
	
	@Autowired
	VolumeService volumeService;

	@RemoteMethod
	public void save(SnapshotInfoP instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
	}// end of save(SnapshotInfoP

	@RemoteMethod
	public SnapshotInfoP saveOrUpdate(SnapshotInfoP instance) {
		try {
			return requestSnapshot(instance);
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(SnapshotInfoP

	@RemoteMethod
	public void remove(int id) {
		try {
			deleteSnapshot(id);
			Asset a = SnapshotInfoP.findSnapshotInfoP(id).getAsset();
			if(a!=null && a.getEndTime() !=null){
				Commons.setAssetEndTime(a);
			}
			
			//SnapshotInfoP.findSnapshotInfoP(id).remove();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public SnapshotInfoP findById(int id) {
		try {
			return SnapshotInfoP.findSnapshotInfoP(id);
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<SnapshotInfoP> findAll(int start, int max, String search) {
		try {

			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				return SnapshotInfoP.findSnapshotInfoPsByUser(user, start, max,
						search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")) {
				return SnapshotInfoP.findSnapshotInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			}
			return SnapshotInfoP.findAllSnapshotInfoPs(start, max, search);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public SnapshotInfoP requestSnapshot(SnapshotInfoP snapshotInfoP) {
		try {

			AssetType assetType = AssetType.findAssetTypesByNameEquals(
					"" + Commons.ASSET_TYPE.VolumeSnapshot).getSingleResult();
			User currentUser = Commons.getCurrentUser();
			
			currentUser = User.findUser(currentUser.getId());
			Company company = currentUser.getDepartment().getCompany();
			Asset asset = Commons.getNewAsset(assetType, currentUser,snapshotInfoP.getProduct(), reportService,company);
			asset.setProject(Project.findProject(snapshotInfoP.getProjectId()));
			snapshotInfoP.setAsset(asset);
			snapshotInfoP = snapshotInfoP.merge();
			VolumeInfoP volumeInfoP = volumeService.findById( Integer.parseInt(snapshotInfoP.getVolumeId()) );
			if (true == assetType.getWorkflowEnabled()) {
				accountLogService
						.saveLogAndSendMail(
								"Snapshot for volume '"+volumeInfoP.getName()+"("+volumeInfoP.getId()+")' with ID "
										+ snapshotInfoP.getId()
										+ " requested, workflow started, pending approval.",
								Commons.task_name.SNAPSHOT.name(),
								Commons.task_status.SUCCESS.ordinal(),
								currentUser.getEmail());

				Commons.createNewWorkflow(
						workflowService
								.createProcessInstance(Commons.PROCESS_DEFN.Snapshot_Request
										+ ""), snapshotInfoP.getId(), asset
								.getAssetType().getName());
				snapshotInfoP
						.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL
								+ "");
				snapshotInfoP = snapshotInfoP.merge();
			} else {
				accountLogService
						.saveLogAndSendMail(
								"Snapshot for volume '"+volumeInfoP.getName()+"("+volumeInfoP.getId()+")' with ID "
										+ snapshotInfoP.getId()
										+ " requested, workflow approved automatically.",
								Commons.task_name.SNAPSHOT.name(),
								Commons.task_status.SUCCESS.ordinal(),
								currentUser.getEmail());
				snapshotInfoP.setStatus(Commons.SNAPSHOT_STATUS.pending + "");
				snapshotInfoP = snapshotInfoP.merge();
				workflowApproved(snapshotInfoP);
			}
			Commons.setSessionMsg("Scheduling Snapshot request");
			log.info("end of requestSnapshot");
			return snapshotInfoP;
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Snapshot request: "
					+ e.getMessage());
			log.error(e.getMessage());// 
			e.printStackTrace();
		}
		return null;
	}// end of requestSnapshot(SnapshotInfoP

	public void workflowApproved(SnapshotInfoP instance) {
		try {
			instance.setStatus(Commons.SNAPSHOT_STATUS.pending + "");
			instance = instance.merge();
			snapshotWorker.createSnapshot(instance.getAsset()
					.getProductCatalog().getInfra(), instance, Commons
					.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}

	}

	@RemoteMethod
	public void deleteSnapshot(int id) {
		try {
			SnapshotInfoP snapshotInfoP = SnapshotInfoP.findSnapshotInfoP(id);
			Commons.setAssetEndTime(snapshotInfoP.getAsset());
			snapshotWorker.deleteSnapshot(snapshotInfoP.getAsset()
					.getProductCatalog().getInfra(), snapshotInfoP, Commons
					.getCurrentUser().getEmail());
			Commons.setSessionMsg("Scheduling Snapshot remove");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Snapshot remove");
			log.error(e.getMessage());// e.printStackTrace();
		}
	}

	@RemoteMethod
	public List<ProductCatalog> findProductType() {
		//do some circus to rempve vcloud specific snapshot product types
		List<ProductCatalog> pcs2return = new ArrayList<ProductCatalog>();
		List<ProductCatalog> pcs = null;
		
		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED ||  Commons.getCurrentUser().getRole().getName()
				.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			pcs =  ProductCatalog.findProductCatalogsByProductTypeEquals(
					Commons.ProductType.VolumeSnapshot.getName())
					.getResultList();
		} else {
			pcs =  ProductCatalog.findProductCatalogsByProductTypeAndCompany(
					Commons.ProductType.VolumeSnapshot.getName(),
					Company.findCompany(Commons.getCurrentSession()
							.getCompanyId())).getResultList();
		}
		
		for (Iterator iterator = pcs.iterator(); iterator.hasNext(); ) {
			ProductCatalog productCatalog = (ProductCatalog) iterator.next();
			if(productCatalog.getInfra().getInfraType().getId() == Commons.INFRA_TYPE_AWS 
					|| productCatalog.getInfra().getInfraType().getId() == Commons.INFRA_TYPE_EUCA){
				pcs2return.add(productCatalog);
			}
		}
		return pcs2return;

	}

}// end of class SnapshotInfoPController

