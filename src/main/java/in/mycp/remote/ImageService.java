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
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import in.mycp.utils.Commons;
import in.mycp.workers.ImageWorker;

import java.util.List;

import javax.persistence.TypedQuery;

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

@RemoteProxy(name = "ImageDescriptionP")
public class ImageService {

	private static final Logger log = Logger.getLogger(ImageService.class
			.getName());
	@Autowired
	WorkflowService workflowService;
	@Autowired
	ImageWorker imageWorker;
	@Autowired
	ReportService reportService;

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public void save(ImageDescriptionP instance) {
		try {
			instance.persist();

		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}// end of save(ImageDescriptionP

	@RemoteMethod
	public ImageDescriptionP saveOrUpdate(ImageDescriptionP instance) {
		try {
			return requestImage(instance);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of saveOrUpdate(ImageDescriptionP

	@RemoteMethod
	public void remove(int id) {
		try {
			ImageDescriptionP.findImageDescriptionP(id).remove();
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}// end of method remove(int id

	@RemoteMethod
	public ImageDescriptionP findById(int id) {
		try {
			return ImageDescriptionP.findImageDescriptionP(id);
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<ImageDescriptionP> findAll4List(int firstResult, int maxResults) {
		log.info("findAll4List");
		try {
			User user = Commons.getCurrentUser();
			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return ImageDescriptionP.findAllImageDescriptionPs(firstResult,
						maxResults, "");
			} else {
				return ImageDescriptionP.findImageDescriptionPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), firstResult, maxResults, "")
						.getResultList();
			}

		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll4List

	@RemoteMethod
	public List<ImageDescriptionP> findAll(int start, int max, String search) {
		try {
			User user = Commons.getCurrentUser();

			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return ImageDescriptionP.findAllImageDescriptionPs(start, max,
						search);
			} else {
				return ImageDescriptionP.findImageDescriptionPsByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			}

			/*
			 * if(user.getRole().getName().equals(Commons.ROLE.ROLE_USER+"")){
			 * return
			 * ImageDescriptionP.findImageDescriptionPsByUser(user,start,max
			 * ,search).getResultList(); }else if
			 * (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")
			 * || user.getRole().getName().equals(Commons.ROLE.ROLE_ADMIN+"")){
			 * return ImageDescriptionP.findImageDescriptionPsByCompany(
			 * Company.
			 * findCompany(Commons.getCurrentSession().getCompanyId()),start
			 * ,max,search).getResultList(); } return
			 * ImageDescriptionP.findAllImageDescriptionPs(start,max,search);
			 */
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public ImageDescriptionP requestImage(ImageDescriptionP instance) {
		try {
			String instanceIdForImgCreation = instance
					.getInstanceIdForImgCreation();
			AssetType assetTypeImageDescription = AssetType
					.findAssetTypesByNameEquals(
							"" + Commons.ASSET_TYPE.ComputeImage)
					.getSingleResult();
			User currentUser = Commons.getCurrentUser();

			long allAssetTotalCosts = reportService.getAllAssetCosts()
					.getTotalCost();
			currentUser = User.findUser(currentUser.getId());
			Company company = currentUser.getDepartment()
					.getCompany();
			Asset asset = Commons.getNewAsset(assetTypeImageDescription,
					currentUser, "", allAssetTotalCosts, company);
			instance.setAsset(asset);
			instance = instance.merge();
			if (true == assetTypeImageDescription.getWorkflowEnabled()) {

				accountLogService.saveLog("Image '"+instance.getName()+"' with ID " + instance.getId()
						+ " requested, workflow started, pending approval.",
						Commons.task_name.IMAGE.name(),
						Commons.task_status.SUCCESS.ordinal(),
						currentUser.getEmail());

				Workflow workflow = Commons
						.createNewWorkflow(
								workflowService
										.createProcessInstance(Commons.PROCESS_DEFN.Image_Request
												+ ""), instance.getId(), asset
										.getAssetType().getName());
			} else {

				accountLogService.saveLog("Image '"+instance.getName()+"' with ID " + instance.getId()
						+ " requested, workflow approved automatically.",
						Commons.task_name.IMAGE.name(),
						Commons.task_status.SUCCESS.ordinal(),
						currentUser.getEmail());

				instance.setInstanceIdForImgCreation(instanceIdForImgCreation);
				workflowApproved(instance);
			}
			log.info("end of requestImage");
			return instance;
		} catch (Exception e) {
			Commons.setSessionMsg("Error while requestImage, Instance "
					+ instance.getName() + "<br> Reason: " + e.getMessage());
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of requestSnapshot(SnapshotInfoP

	public void workflowApproved(ImageDescriptionP instance) {
		try {
			imageWorker.createImage(instance.getAsset().getProductCatalog()
					.getInfra(), instance, Commons.getCurrentUser().getEmail());
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
	}
		
	@RemoteMethod
	public List<ImageDescriptionP> findAll(Infra infra, int start, int max, String search) {
		try {
			User user = Commons.getCurrentUser();

			if (user.getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return ImageDescriptionP.findAllImageDescriptionPs(infra, start, max,
						search);
			} else {
				return ImageDescriptionP.findImageDescriptionPsByCompany(infra,
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId()), start, max, search)
						.getResultList();
			}
		} catch (Exception e) {
			log.error(e);// e.printStackTrace();
		}
		return null;
	}// end of method findAll

}// end of class ImageDescriptionPController

