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

import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.domain.ProductCatalog;
import in.mycp.utils.Commons;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.samples.FakeSSLSocketFactory;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.RegionInfo;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "InfraService")
public class InfraService {

	private static final Logger log = Logger.getLogger(InfraService.class
			.getName());

	@Autowired
	EucalyptusService eucalyptusService;
	
	@Autowired
	VmwareService vmwareService;

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public Infra syncDataFromEuca(String instanceId) {
		Infra instance = Infra.findInfra(new Integer(instanceId));
		try {

			accountLogService.saveLog(
					"Start : Synchronizing "+instance.getName()
							, Commons.task_name.SYNC
							.name(), Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			if (instance.getSyncInProgress() != null
					&& instance.getSyncInProgress()) {
				log.error("Synchronizing is in progress, Cannot start another one now. Wait till the current one gets done.");
				accountLogService.saveLog(
						"Synchronizing is in progress, Cannot start another one now. Wait till the current one gets done. "
								, Commons.task_name.SYNC
								.name(), Commons.task_status.FAIL.ordinal(),
						Commons.getCurrentUser().getEmail());
				return null;
			}
			instance.setSyncDate(new Date());
			instance.setSyncInProgress(true);
			instance.setSyncstatus(Commons.sync_status.running.ordinal());
			instance.merge();

			if(instance.getInfraType().getId() == Commons.INFRA_TYPE_VCLOUD){
				vmwareService.syncDataFromVcloud(instance);
			}else {
				eucalyptusService.syncDataFromEuca(instance);
			}
			
			
			
			instance.setSyncInProgress(false);
			instance.setSyncstatus(Commons.sync_status.success.ordinal());
			instance.merge();
			
			accountLogService.saveLogAndSendMail(
					"Synchronizing complete for "+instance.getName()
							, Commons.task_name.SYNC
							.name(), Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Synchronizing failed.Error follows.");
			log.error(e);
			accountLogService.saveLogAndSendMail(
					"Synchronizing failed for "+Infra.findInfra(new Integer(instanceId)).getName()+". Error : "+e.getMessage()
							, Commons.task_name.SYNC
							.name(), Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
			try {
				instance.setSyncDate(new Date());
				instance.setSyncInProgress(false);
				instance.setSyncstatus(Commons.sync_status.failed.ordinal());
				instance.merge();

			} catch (Exception e2) {
				log.error(e2);
			}

		}
		return null;
	}// end of saveOrUpdate(Infra

	@RemoteMethod
	public Infra saveOrUpdate(Infra instance) {
		try {
			String logCreated=" created ";
			
			// instance.setRegion(RegionP.findRegionP(instance.getRegion().getId()));
			instance.setCompany(Company.findCompany(instance.getCompany()
					.getId()));
			
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword("gothilla");
			String encAccessId = textEncryptor.encrypt(instance.getAccessId());
			String encSecretKey = textEncryptor
					.encrypt(instance.getSecretKey());

			if (instance.getId() == null || instance.getId() < 1) {
				instance.setAccessId(encAccessId);
				instance.setSecretKey(encSecretKey);
			} else {
				logCreated=" updated ";
				// avoid double encryption
				Infra local = Infra.findInfra(instance.getId());
				if (!local.getSecretKey().equals(instance.getSecretKey())) {
					instance.setSecretKey(encSecretKey);
				}

				if (!local.getAccessId().equals(instance.getAccessId())) {
					instance.setAccessId(encAccessId);
				}

			}
			instance = instance.merge();
			accountLogService.saveLog("Cloud " + instance.getName()
					+ logCreated, Commons.task_name.CLOUD.name(),
					Commons.task_status.SUCCESS.ordinal(), Commons
							.getCurrentUser().getEmail());
			// now create all products supported by this infra
			createAllProducts(instance);
			return instance;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			accountLogService.saveLog("Cloud creation failed " + instance.getName()
					+ " ", Commons.task_name.CLOUD.name(),
					Commons.task_status.FAIL.ordinal(), Commons
							.getCurrentUser().getEmail());
		}
		return null;
	}// end of saveOrUpdate(Infra

	public void createAllProducts(Infra i) {
		if (ProductCatalog.findProductCatalogsByInfra(i).getResultList() != null
				&& ProductCatalog.findProductCatalogsByInfra(i).getResultList()
						.size() > 0) {
			// products already exist, may when the cloud is updated .
			// dont create fresh products in this case
			return;
		}
		ProductCatalog pc = new ProductCatalog();
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.ComputeInstance + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.ComputeInstance.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.IpAddress + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.IpAddress.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.KeyPair + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.KeyPair.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.SecurityGroup + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.SecurityGroup.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.Volume + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.Volume.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.VolumeSnapshot + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.VolumeSnapshot.getName());
		pc.merge();
		pc = new ProductCatalog();
		pc.setId(0);
		pc.setInfra(i);
		pc.setCurrency(i.getCompany().getCurrency());
		pc.setName(Commons.ProductType.ComputeImage + " @ " + i.getName());
		pc.setPrice(10);
		pc.setProductType(Commons.ProductType.ComputeImage.getName());
		pc.merge();

		accountLogService.saveLog("Automatic Products created for the Cloud "
				+ i.getName() + ", ", Commons.task_name.CLOUD.name(),
				Commons.task_status.SUCCESS.ordinal(), Commons.getCurrentUser()
						.getEmail());
	}

	@RemoteMethod
	public String remove(int id) {
		Infra i = null;
		try {
			i = Infra.findInfra(id);
			List<ProductCatalog> products = ProductCatalog
					.findProductCatalogsByInfra(i).getResultList();
			if (products != null && products.size() > 0) {
				for (Iterator iterator = products.iterator(); iterator
						.hasNext();) {
					ProductCatalog productCatalog = (ProductCatalog) iterator
							.next();
					try {
						productCatalog.remove();
					} catch (Exception e) {
						log.error(" While removing the cloud, could not remove the product "
								+ productCatalog.getName()
								+ " "
								+ "associated.please remove them manually");
					}

				}
			}

			i.remove();
			accountLogService.saveLog("Cloud " + i.getName() + " removed, ",
					Commons.task_name.CLOUD.name(), Commons.task_status.SUCCESS
							.ordinal(), Commons.getCurrentUser().getEmail());
			return "Removed Infra " +i.getName();
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
			accountLogService.saveLog("Error in Cloud removal "
					+ i.getName() + ", ",
					Commons.task_name.CLOUD.name(), Commons.task_status.FAIL
							.ordinal(), Commons.getCurrentUser().getEmail());
		}
		return "Cannot Remove Infra " + Infra.findInfra(id).getName() + ". look into logs.";
	}// end of method remove(int id

	@RemoteMethod
	public Infra findById(int id) {
		try {
			return Infra.findInfra(id);
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<Infra> findAll() {
		try {
			if (Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return Infra.findAllInfras();
			} else {
				return Infra.findInfrasByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId())).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<Infra> findAll4Dashboard() {
		try {
			List<Infra> infras = null;
			if (Commons.EDITION_ENABLED== Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				infras = Infra.findAllInfras();
			} else {
				infras = Infra.findInfrasByCompany(
						Company.findCompany(Commons.getCurrentSession()
								.getCompanyId())).getResultList();
			}
			for (Iterator iterator = infras.iterator(); iterator.hasNext();) {
				Infra infra = (Infra) iterator.next();
				infra.setStatus("loading");
			}

			return infras;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

	public String getInfraStatus(Infra infra) {
		String status = Commons.EUCA_STATUS.unknown + "";
		try {
			//System.out.println(" in infra getInfraStatus for  "+infra.getName());
			
			// first , just try to reach and ping the server, then try
			// connecting
			try {
				InetAddress byIpAsName = InetAddress.getByName(infra
						.getServer());
				SocketAddress sockaddr = new InetSocketAddress(byIpAsName,
						infra.getPort());
				Socket theSock = new Socket();
				theSock.connect(sockaddr, 2000);
			} catch (Exception e) {
				// log.error(e.getMessage());//e.printStackTrace();
				//e.printStackTrace();
				log.info(e.getMessage());
				status = Commons.EUCA_STATUS.unreachable + "";
				throw new Exception("Cant even open socket to server "
						+ infra.getServer() + ".wont try to connect!");
			}

			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword("gothilla");
			String decAccessId = textEncryptor.decrypt(infra.getAccessId());
			String decSecretKey = textEncryptor.decrypt(infra.getSecretKey());

			if(infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || 
					infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA){
				Jec2 ec2 = new Jec2(decAccessId, decSecretKey, false,
						infra.getServer(), infra.getPort());
				ec2.setResourcePrefix(infra.getResourcePrefix());
				ec2.setSignatureVersion(infra.getSignatureVersion());
				ec2.setMaxRetries(1);
				List params = new ArrayList<String>();
				List<RegionInfo> regions = ec2.describeRegions(params);
				for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
					RegionInfo regionInfo = (RegionInfo) iterator.next();
					if (regionInfo != null) {
						status = Commons.EUCA_STATUS.running + "";
					} else if (regionInfo == null) {
						status = Commons.EUCA_STATUS.unknown + "";
					}
					break;
				}
			}else if(infra.getInfraType().getId() == Commons.INFRA_TYPE_VCLOUD){
				VcloudClient.setLogLevel(Level.SEVERE);
				VcloudClient vcloudClient = new VcloudClient("https://"+infra.getServer(), Version.V1_5);
				String login = decAccessId+"@"+infra.getVcloudAccountName();
				vcloudClient.registerScheme("https", infra.getPort().intValue(), FakeSSLSocketFactory.getInstance());
				vcloudClient.login(login, decSecretKey);
				Collection<ReferenceType> orgRefs = vcloudClient.getOrgRefs();
				for (Iterator iterator = orgRefs.iterator(); iterator.hasNext();) {
					ReferenceType orgRefType = (ReferenceType) iterator.next();
					if(orgRefType !=null){
						status = Commons.EUCA_STATUS.running + "";
					}else {
						status = Commons.EUCA_STATUS.unknown + "";
					}
					break;
				}
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			status = Commons.EUCA_STATUS.unknown + "";
		}
		return status;
	}

	@RemoteMethod
	public String getInfraStatusDWR(int infraId) {
		Infra infra = Infra.findInfra(infraId);
		String status = Commons.EUCA_STATUS.unknown + "";
		try {
			// first , just try to reach and ping the server, then try
			// connecting
			try {
				InetAddress byIpAsName = InetAddress.getByName(infra
						.getServer());
				SocketAddress sockaddr = new InetSocketAddress(byIpAsName,
						infra.getPort());
				Socket theSock = new Socket();
				theSock.connect(sockaddr, 4000);
			} catch (Exception e) {
				// log.error(e.getMessage());
				//e.printStackTrace();
				log.error(e);
				status = Commons.EUCA_STATUS.unreachable + "";
				log.info("Cant even open socket to server " + infra.getServer()
						+ ".wont try to connect!");
				throw new Exception("Cant even open socket to server "
						+ infra.getServer() + ".wont try to connect!");
			}

			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword("gothilla");
			String decAccessId = textEncryptor.decrypt(infra.getAccessId());
			String decSecretKey = textEncryptor.decrypt(infra.getSecretKey());

			if(infra.getInfraType().getId() == Commons.INFRA_TYPE_AWS || 
					infra.getInfraType().getId() == Commons.INFRA_TYPE_EUCA){
				Jec2 ec2 = new Jec2(decAccessId, decSecretKey, false,
						infra.getServer(), infra.getPort());
				if (infra.getServer() != null
						&& !infra.getServer().contains("ec2.amazonaws.com")) {
					ec2.setResourcePrefix(infra.getResourcePrefix());
					ec2.setSignatureVersion(infra.getSignatureVersion());
					ec2.setMaxRetries(1);
				}
	
				List params = new ArrayList<String>();
	
				List<RegionInfo> regions = ec2.describeRegions(params);
				for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
					RegionInfo regionInfo = (RegionInfo) iterator.next();
					if (regionInfo != null) {
						status = Commons.EUCA_STATUS.running + "";
					} else if (regionInfo == null) {
						status = Commons.EUCA_STATUS.error + "";
					}
					break;
				}
			}else if(infra.getInfraType().getId() == Commons.INFRA_TYPE_VCLOUD){
				
				VcloudClient.setLogLevel(Level.SEVERE);
				VcloudClient vcloudClient = new VcloudClient("https://"+infra.getServer(), Version.V1_5);
				
				String login = decAccessId+"@"+infra.getVcloudAccountName();
				vcloudClient.registerScheme("https", infra.getPort().intValue(), FakeSSLSocketFactory.getInstance());
				vcloudClient.login(login, decSecretKey);
				Collection<ReferenceType> orgRefs = vcloudClient.getOrgRefs();
				for (Iterator iterator = orgRefs.iterator(); iterator.hasNext();) {
					ReferenceType orgRefType = (ReferenceType) iterator.next();
					if(orgRefType !=null){
						status = Commons.EUCA_STATUS.running + "";
					}else {
						status = Commons.EUCA_STATUS.unknown + "";
					}
					break;
				}
				
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			// e.printStackTrace();
			log.error(e);
			if (e.getMessage() != null
					&& e.getMessage().indexOf("Client error") > -1) {
				status = Commons.EUCA_STATUS.running + "";
			} else if (e.getMessage() != null
					&& e.getMessage().indexOf("Cant even open socket") > -1) {
				status = Commons.EUCA_STATUS.unreachable + "";
			} else {
				status = Commons.EUCA_STATUS.unknown + "";
			}
		}
		return infra.getServer() + "=" + status;
	}

}// end of class InfraController

