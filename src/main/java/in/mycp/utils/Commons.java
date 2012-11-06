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

package in.mycp.utils;

import in.mycp.domain.AccountLog;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.domain.Workflow;
import in.mycp.domain.AccountLogTypeDTO;
import in.mycp.web.MycpSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContextFactory;
import org.jbpm.api.ProcessInstance;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class Commons {
	
	private static final Logger log = Logger.getLogger(Commons.class.getName());

	static public enum ASSET_TYPE {
		IpAddress, SecurityGroup, IpPermission, Volume, VolumeSnapshot, ComputeImage, ComputeInstance, KeyPair, addressInfo
	}
	
	static public enum ProductType {
		IpAddress("Ip Address"), SecurityGroup("Security Group"), Volume("Volume"), VolumeSnapshot("Snapshot"), ComputeImage("Image"), ComputeInstance(
				"Instance"), KeyPair("Key Pair");

		private String name;

		private ProductType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	static public enum EUCA_STATUS {
		running, unknown, unreachable,error
	}

	static public enum REQUEST_STATUS {
		running, STARTING, SHUTTING_DOWN, RESTARTING, STOPPING, STOPPED, TERMINATING, TERMINATED, FAILED
	}

	static public enum PROCESS_DEFN {
		Compute_Request, Image_Request, IpAddress_Request, Keys_Request, SecGroup_Request, Snapshot_Request, Volume_Request
	}

	static public enum SNAPSHOT_STATUS {
		completed, pending, inactive
	}

	static public enum ipaddress_STATUS {
		starting,available,associated,failed
	}

	static public enum keypair_STATUS {
		starting,active, inactive,failed
	}

	static public enum secgroup_STATUS {
		starting,active, inactive,failed
	}

	static public enum image_STATUS {
		active, inactive
	}
	
	static public enum sync_status {
		running,failed,success
	}
	
	static public enum task_name {
		SIGNIN, LOGOUT,COMPUTE, IPADDRESS, VOLUME, SECURITYGROUP, KEYPAIR,IMAGE, SNAPSHOT, PROJECT, DEPARTMENT,USER, CLOUD,AVAILABILITYZONE,PRODUCT,PRODUCTTYPE,WORKFLOW,SYNC
	}
	
	static public enum task_status {
		FAIL, SUCCESS
	}

	public static String VOLUME_STATUS_AVAILABLE = "available";
	public static String VOLUME_STATUS_INUSE = "in-use";
	public static String VOLUME_STATUS_ATTACHED = "attached";
	public static String VOLUME_STATUS_CREATING = "creating";
	public static String VOLUME_STATUS_DELETED = "deleted";
	public static String VOLUME_STATUS_FAILED = "FAILED";

	public static int ROLE_USER_INTVAL = 0;
	public static int ROLE_ADMIN_INTVAL = 3;
	public static int ROLE_MANAGER_INTVAL = 6;
	public static int ROLE_SUPERADMIN_INTVAL = 9;

	static public enum ROLE {
		ROLE_USER, ROLE_ADMIN, ROLE_MANAGER, ROLE_SUPERADMIN
	}

	static public enum WORKFLOW_STATUS {
		PENDING_APPROVAL, ABORTED, APPROVED, APPROVAL_REJECTED, END, NO_WORKFLOW
	}
	
	static public enum WORKFLOW_TRANSITION {
		Reject,Approve
	}
	
	public static String QUOTA_EXCEED_MSG = "Quota Exceeded.";
	
	public static int OS_EDITION_ENABLED=1;
	public static int HOSTED_EDITION_ENABLED=2;
	public static int SP_EDITION_ENABLED=3;
	
	public static int EDITION_ENABLED=HOSTED_EDITION_ENABLED;
	

	public static List getAllJbpmProcDefNames() {
		// public static final String JBPM_PROC_DEF_NAME_FVC_BILL = "fvc bill";
		Field[] allFields = Commons.class.getFields();
		List<String> fieldList = new ArrayList<String>();
		for (int i = 0; i < allFields.length; i++) {
			Field eachField = allFields[i];
			if (eachField.getName().startsWith("JBPM_PROC_DEF_NAME")) {
				try {
					fieldList.add((String) eachField.get(Commons.class));
				} catch (Exception e) {
				}
			}
		}
		return fieldList;
	}

	public static Asset getNewAsset(AssetType at, User currentUser, String productCatalogId, long allAssetTotalCosts, Company company) throws Exception {
		Asset asset = new Asset();
		asset.setActive(true);
		
		System.out.println(company);
		if(company.getQuota()>0 && (company.getQuota()-allAssetTotalCosts <= company.getMinBal())){
			throw new Exception(QUOTA_EXCEED_MSG);
		}
		asset.setStartTime(new Date());
		asset.setAssetType(at);
		asset.setDetails("from mycp");
		asset.setUser(currentUser);
		asset.setActive(false);
		if (!StringUtils.isEmpty(productCatalogId)) {
			ProductCatalog pc = ProductCatalog.findProductCatalog(Integer.parseInt(productCatalogId));
			asset.setStartRate(pc.getPrice());
			asset.setProductCatalog(pc);
		}

		return asset.merge();
	}

	public static Asset getNewAsset(AssetType at, User currentUser, ProductCatalog productCatalog) {
		Asset asset = new Asset();
		asset.setActive(false);

		asset.setStartTime(new Date());
		asset.setAssetType(at);
		asset.setDetails("from mycp");
		asset.setUser(currentUser);
		asset.setProductCatalog(productCatalog);
		asset.setStartRate(productCatalog.getPrice());
		return asset.merge();
	}

	public static Workflow createNewWorkflow(ProcessInstance pi, Integer assetId, String assetType) {

		Workflow workflow = new Workflow();
		workflow.setProcessId(pi.getId());
		String activityName = "";
		Set<String> activityNames = pi.findActiveActivityNames();
		for (Iterator iterator = activityNames.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			activityName = string;
			break;
		}
		workflow.setStatus(activityName);
		workflow.setAssetId(assetId);
		workflow.setAssetType(assetType);
		workflow.setUser(getCurrentUser());
		return workflow.merge();
	}

	public static User getCurrentUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		HttpSession session = null;
		Object obj = null;
		try {
			session = WebContextFactory.get().getSession();
			setSessionAttribute("MYCP_SIGNUP_MSG", ""); 
			obj = session.getAttribute("CurrentUser");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// if this is after the first call from DWR
		if (obj != null) {
			return (User) obj;
			// if this is teh first call from DWR
		} else if (obj == null && session != null) {
			User currentUser = User.findUsersByEmailEquals(securityContext.getAuthentication().getName()).getSingleResult();
			session.setAttribute("CurrentUser", currentUser);
			return currentUser;
			// for non DWR calls
		} else {
			//System.out.println("securityContext.getAuthentication().getName() = " + securityContext.getAuthentication().getName());
			User currentUser = User.findUsersByEmailEquals(securityContext.getAuthentication().getName()).getSingleResult();
			return currentUser;
		}
	}

	public static String getCurrentUserRolesNonDWR() {
		SecurityContext securityContext = SecurityContextHolder.getContext();

		return securityContext.getAuthentication().getAuthorities().toString();

	}

	public static MycpSession getCurrentSession() {
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			User user = User.findUsersByEmailEquals(securityContext.getAuthentication().getName()).getSingleResult();
			MycpSession mysession = new MycpSession();
			mysession.setEmail(user.getEmail());
			mysession.setFirstName("");
			mysession.setLastName("");
			try {
				mysession.setCompany(user.getProject().getDepartment().getCompany().getName());
				mysession.setCompanyId(user.getProject().getDepartment().getCompany().getId());
			} catch (Exception e) {
				// e.printStackTrace();
			}

			mysession.setLoggedInDate(user.getLoggedInDate());
			mysession.setRole(user.getRole().getName());
			return mysession;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setSessionAttribute(String key, String val) {
		try {
			HttpSession session = WebContextFactory.get().getSession();
			session.setAttribute(key, val);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public static void setSessionMsg(String msg) {
		try {
			HttpSession session = WebContextFactory.get().getSession();
			session.setAttribute("session_msg", msg);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public static String getSessionMsg() {
		try {
			HttpSession session = WebContextFactory.get().getSession();
			return (String)session.getAttribute("session_msg");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "";
	}
	
	
	public static void setAssetEndTime(Asset a){
		//Asset a = instanceLocal.getAsset();
		a.setEndTime(new Date());
		a.setActive(false);
		a.merge();
	}
	
	
	public static void setAssetStartTime(Asset a){
		//Asset a = instanceLocal.getAsset();
		a.setStartTime(new Date());
		a.setActive(true);
		a.merge();
	}

	public static List<AccountLogTypeDTO> getAllAccountLogTypes(){
		
		List<AccountLogTypeDTO> list = new ArrayList<AccountLogTypeDTO>();
			list.add(new AccountLogTypeDTO(1, "last 24 Hours"));
			list.add(new AccountLogTypeDTO(2, "last 7 days"));
			int acctLogTypeId = 3;
			DateTime dt = new DateTime();
			
			for(int i =0;i<6;i++ ){
				list.add(new AccountLogTypeDTO(acctLogTypeId, dt.minusMonths(i).monthOfYear().getAsText()));
				acctLogTypeId++;
			}
		return list;
	}
	
	public static DateTime getDateTimeFromMonthName(String monthName){
		
		DateTime dt = new DateTime();
		for(int i =0;i<7;i++ ){
			if(monthName.equals(dt.minusMonths(i).monthOfYear().getAsText())){
				return dt.minusMonths(i);
			}
		}
		return null;
	}

	
}
