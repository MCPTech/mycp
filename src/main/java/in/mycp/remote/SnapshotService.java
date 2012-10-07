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
import in.mycp.domain.ImageDescriptionP;
import in.mycp.domain.Infra;
import in.mycp.domain.KeyPairInfoP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.SnapshotInfoP;
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import in.mycp.utils.Commons;
import in.mycp.workers.SnapshotWorker;

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

	private static final Logger log = Logger.getLogger(SnapshotService.class.getName());

	@Autowired
	WorkflowService workflowService;

	@Autowired
	SnapshotWorker snapshotWorker;

	@RemoteMethod
	public void save(SnapshotInfoP instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
	}// end of save(SnapshotInfoP

	@RemoteMethod
	public SnapshotInfoP saveOrUpdate(SnapshotInfoP instance) {
		try {
			return requestSnapshot(instance);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(SnapshotInfoP

	@RemoteMethod
	public void remove(int id) {
		try {
			deleteSnapshot(id);
			SnapshotInfoP.findSnapshotInfoP(id).remove();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public SnapshotInfoP findById(int id) {
		try {
			return SnapshotInfoP.findSnapshotInfoP(id);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<SnapshotInfoP> findAll(int start,int  max,String search) {
		try {
			
			User user = Commons.getCurrentUser();
			if(user.getRole().getName().equals(Commons.ROLE.ROLE_USER+"")){
				return SnapshotInfoP.findSnapshotInfoPsByUser(user, start,  max, search).getResultList();
			}else if (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "") || user.getRole().getName().equals(Commons.ROLE.ROLE_ADMIN+"")){
				return SnapshotInfoP.findSnapshotInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession().getCompanyId()), start,  max, search).getResultList();
			}
			return SnapshotInfoP.findAllSnapshotInfoPs(start,  max, search);
		} catch (Exception e) {
			log.error(e);//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public SnapshotInfoP requestSnapshot(SnapshotInfoP snapshotInfoP) {
		try {

			AssetType assetTypeSnapshot = AssetType.findAssetTypesByNameEquals("" + Commons.ASSET_TYPE.VolumeSnapshot).getSingleResult();
			User currentUser = Commons.getCurrentUser();
			Asset asset = Commons.getNewAsset(assetTypeSnapshot, currentUser,snapshotInfoP.getProduct());
			snapshotInfoP.setAsset(asset);
			snapshotInfoP = snapshotInfoP.merge();
			if (true == assetTypeSnapshot.getWorkflowEnabled()) {
				Commons.createNewWorkflow(workflowService.createProcessInstance(Commons.PROCESS_DEFN.Snapshot_Request
						+ ""), snapshotInfoP.getId(), asset.getAssetType().getName());
				snapshotInfoP.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL+"");
				snapshotInfoP = snapshotInfoP.merge();
			} else {
				snapshotInfoP.setStatus(Commons.SNAPSHOT_STATUS.pending+"");
				snapshotInfoP = snapshotInfoP.merge();
				workflowApproved(snapshotInfoP);
			}
			Commons.setSessionMsg("Scheduling Snapshot request");
			log.info("end of requestSnapshot");
			return snapshotInfoP;
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Snapshot request");
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of requestSnapshot(SnapshotInfoP

	public void workflowApproved(SnapshotInfoP instance) {
		try {
			instance.setStatus(Commons.SNAPSHOT_STATUS.pending+"");
			instance = instance.merge();
			snapshotWorker.createSnapshot(instance.getAsset().getProductCatalog().getInfra(), instance);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}

	}

	@RemoteMethod
	public void deleteSnapshot(int id) {
		try {
			SnapshotInfoP snapshotInfoP = SnapshotInfoP.findSnapshotInfoP(id);
			Commons.setAssetEndTime(snapshotInfoP.getAsset());
			snapshotWorker.deleteSnapshot( snapshotInfoP.getAsset().getProductCatalog().getInfra(), snapshotInfoP);
			Commons.setSessionMsg("Scheduling Snapshot remove");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Snapshot remove");
			log.error(e.getMessage());//e.printStackTrace();
		}
	}
	
	@RemoteMethod
	public List<ProductCatalog> findProductType() {

		if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
			return ProductCatalog.findProductCatalogsByProductTypeEquals(Commons.ProductType.VolumeSnapshot.getName()).getResultList();
		}else{
			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(Commons.ProductType.VolumeSnapshot.getName(),
					Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
		}
		
	}
	

}// end of class SnapshotInfoPController

