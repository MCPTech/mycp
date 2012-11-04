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

// Generated Dec 29, 2011 2:51:05 PM by Charudath

import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.AttachmentInfoP;
import in.mycp.domain.Company;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.domain.VolumeInfoP;
import in.mycp.utils.Commons;
import in.mycp.workers.VolumeWorker;

import java.util.Date;
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

@RemoteProxy(name = "VolumeInfoP")
public class VolumeService {

	private static final Logger log = Logger.getLogger(VolumeService.class
			.getName());

	@Autowired
	VolumeWorker volumeWorker;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public VolumeInfoP requestVolume(VolumeInfoP volume) {
		try {

			AssetType assetTypeVolume = AssetType.findAssetTypesByNameEquals(
					"Volume").getSingleResult();
			User currentUser = Commons.getCurrentUser();
			long allAssetTotalCosts = reportService.getAllAssetCosts()
					.getTotalCost();
			currentUser = User.findUser(currentUser.getId());
			Company company = currentUser.getProject().getDepartment()
					.getCompany();
			Asset asset = Commons.getNewAsset(assetTypeVolume, currentUser,
					volume.getProduct(), allAssetTotalCosts, company);
			volume.setAsset(asset);
			volume = volume.merge();
			if (true == assetTypeVolume.getWorkflowEnabled()) {
				Commons.createNewWorkflow(
						workflowService
								.createProcessInstance(Commons.PROCESS_DEFN.Volume_Request
										+ ""), volume.getId(), asset
								.getAssetType().getName());
				volume.setStatus(Commons.WORKFLOW_STATUS.PENDING_APPROVAL + "");
				volume = volume.merge();

				accountLogService.saveLogAndSendMail("Volume '"+volume.getName()+"' with ID " + volume.getId()
						+ " requested, workflow started, pending approval.",
						Commons.task_name.VOLUME.name(),
						Commons.task_status.SUCCESS.ordinal(),
						currentUser.getEmail());

			} else {
				accountLogService.saveLogAndSendMail("Volume '"+volume.getName()+"' with ID " + volume.getId()
						+ " requested, workflow approved automatically.",
						Commons.task_name.VOLUME.name(),
						Commons.task_status.SUCCESS.ordinal(),
						currentUser.getEmail());
				volume.setStatus(Commons.VOLUME_STATUS_CREATING + "");
				volume = volume.merge();
				workflowApproved(volume);
			}
			Commons.setSessionMsg("Scheduling Volume create");
			log.info("end of requestVolume");
			return volume;
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Volume create: "
					+ e.getMessage());
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}// end of requestVolume(VolumeInfoP

	public void workflowApproved(VolumeInfoP volume) {
		volume = VolumeInfoP.findVolumeInfoP(volume.getId());
		volume.getAsset().setStartTime(new Date());
		volume.setStatus(Commons.VOLUME_STATUS_CREATING + "");
		volume = volume.merge();
		createVolume(volume);
	}

	@RemoteMethod
	public void deleteVolume(int id) {
		try {
			VolumeInfoP volumeInfoP = VolumeInfoP.findVolumeInfoP(id);
			Commons.setAssetEndTime(volumeInfoP.getAsset());
			volumeWorker.deleteVolume(volumeInfoP.getAsset()
					.getProductCatalog().getInfra(), volumeInfoP, Commons
					.getCurrentUser().getEmail());
			Commons.setSessionMsg("Scheduling Volume remove");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Volume remove");
			log.error(e);// e.printStackTrace();
		}

	}// end of deleteVolume(VolumeInfoP

	@RemoteMethod
	public void attachVolume(VolumeInfoP volume) {
		try {
			// volume = VolumeInfoP.findVolumeInfoP(volume.getId());
			log.info("calling Worker for " + volume);
			// TODO - whats a better way to find the infra object
			volumeWorker.attachVolume(
					VolumeInfoP.findVolumeInfoP(volume.getId()).getAsset()
							.getProductCatalog().getInfra(), volume, Commons
							.getCurrentUser().getEmail());
			log.info("scheduled Worker for " + volume);
			Commons.setSessionMsg("Scheduling Volume attach");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Volume attach");
			log.error(e);
			e.printStackTrace();
		}
	}// end of deleteVolume(VolumeInfoP

	@RemoteMethod
	public void detachVolume(int id) {
		try {
			VolumeInfoP volume = VolumeInfoP.findVolumeInfoP(id);
			log.info("calling detachVolume Worker for " + volume.getSize()
					+ ", " + volume.getSnapshotId() + ", " + volume.getZone());
			volumeWorker.detachVolume(volume.getAsset().getProductCatalog()
					.getInfra(), volume, Commons.getCurrentUser().getEmail());
			log.info("scheduled detachVolume Worker for " + volume.getSize()
					+ ", " + volume.getSnapshotId() + ", " + volume.getZone());
			Commons.setSessionMsg("Scheduling Volume detach");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Volume detach");
			log.error(e);// e.printStackTrace();
		}
	}// end of deleteVolume(VolumeInfoP

	@RemoteMethod
	public void createVolume(VolumeInfoP volume) {
		try {
			log.info("calling Worker for " + volume.getSize() + ", "
					+ volume.getSnapshotId() + ", " + volume.getZone());
			volumeWorker.createVolume(volume.getAsset().getProductCatalog()
					.getInfra(), volume, Commons.getCurrentUser().getEmail());
			log.info("scheduled Worker for " + volume.getSize() + ", "
					+ volume.getSnapshotId() + ", " + volume.getZone());
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}// end of createVolume(VolumeInfoP

	@RemoteMethod
	public VolumeInfoP saveOrUpdate(VolumeInfoP instance) {
		try {
			return requestVolume(instance);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(VolumeInfoP

	@RemoteMethod
	public void remove(int id) {
		try {
			// deleteVolume(id);
			VolumeInfoP.findVolumeInfoP(id).remove();
			Commons.setSessionMsg("Scheduling Volume remove");
		} catch (Exception e) {
			Commons.setSessionMsg("Error while Scheduling Volume remove");
			log.error(e);// e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public VolumeInfoP findById(int id) {
		try {
			VolumeInfoP instance = VolumeInfoP.findVolumeInfoP(id);
			instance.setProduct(""
					+ instance.getAsset().getProductCatalog().getId());
			return instance;
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<VolumeInfoP> findAll4List() {
		try {
			User user = Commons.getCurrentUser();
			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return VolumeInfoP.findAllVolumeInfoPs();
			} else {
				return VolumeInfoP.findVolumeInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), 0, 100, "").getResultList();
			}
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll4List

	@RemoteMethod
	public List<VolumeInfoP> findAll(int start, int max, String search) {
		try {

			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				return VolumeInfoP.findVolumeInfoPsByUser(user, start, max,
						search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")
					|| user.getRole().getName()
							.equals(Commons.ROLE.ROLE_ADMIN + "")) {
				return VolumeInfoP.findVolumeInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			}
			return VolumeInfoP.findAllVolumeInfoPs(start, max, search);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<VolumeInfoP> findAllWithAttachInfo(int start, int max,
			String search) {
		try {
			List<VolumeInfoP> volumes = null;
			User user = Commons.getCurrentUser();
			if (user.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
				volumes = VolumeInfoP.findVolumeInfoPsByUser(user, start, max,
						search).getResultList();
			} else if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_MANAGER + "")
					|| user.getRole().getName()
							.equals(Commons.ROLE.ROLE_ADMIN + "")) {
				volumes = VolumeInfoP.findVolumeInfoPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			} else {
				volumes = VolumeInfoP.findAllVolumeInfoPs(start, max, search);
			}

			for (Iterator iterator = volumes.iterator(); iterator.hasNext();) {
				VolumeInfoP volumeInfoP = (VolumeInfoP) iterator.next();
				if (volumeInfoP.getVolumeId() == null
						|| volumeInfoP.getVolumeId().equals("")) {
					continue;
				}
				List<AttachmentInfoP> attaches = AttachmentInfoP
						.findAttachmentInfoPsByVolumeIdEquals(
								volumeInfoP.getVolumeId()).getResultList();
				if (attaches != null && attaches.size() > 0) {
					for (Iterator iterator2 = attaches.iterator(); iterator2
							.hasNext();) {
						AttachmentInfoP attachmentInfoP = (AttachmentInfoP) iterator2
								.next();
						volumeInfoP.setDetails(attachmentInfoP.getStatus()
								+ " @ " + attachmentInfoP.getAttachTime()
								+ " to " + attachmentInfoP.getInstanceId());
					}
				}
			}
			return volumes;

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
					Commons.ProductType.Volume.getName()).getResultList();
		} else {

			return ProductCatalog.findProductCatalogsByProductTypeAndCompany(
					Commons.ProductType.Volume.getName(),
					Company.findCompany(Commons.getCurrentSession()
							.getCompanyId())).getResultList();
		}

	}

}// end of class VolumeInfoPController

