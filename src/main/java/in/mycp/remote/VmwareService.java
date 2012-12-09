
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

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.ImageDescriptionP;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.IpPermissionP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.User;
import in.mycp.domain.VolumeInfoP;
import in.mycp.utils.Commons;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vcloud.api.rest.schema.FirewallRuleProtocols;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.IpAddressesType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.OrgNetworkType;
import com.vmware.vcloud.api.rest.schema.QueryResultCatalogRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultOrgNetworkRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultVAppRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultVMRecordType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Catalog;
import com.vmware.vcloud.sdk.CatalogItem;
import com.vmware.vcloud.sdk.OrgNetwork;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.VirtualNetworkCard;
import com.vmware.vcloud.sdk.admin.AdminOrgNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;
import com.vmware.vcloud.sdk.samples.FakeSSLSocketFactory;
import com.xerox.amazonws.ec2.KeyPairInfo;
import com.xerox.amazonws.ec2.SnapshotInfo;

/**
 * Remote service to be used by DWR for vmware setup,cleanup and sync
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "vmwareService")
public class VmwareService {

	private static Log logger = LogFactory.getLog(VmwareService.class);

	public VcloudClient getVcloudClient(Infra infra) {
		try {
			
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("gothilla");
		String decAccessId = textEncryptor.decrypt(infra.getAccessId());
		String decSecretKey = textEncryptor.decrypt(infra.getSecretKey());
			VcloudClient.setLogLevel(Level.SEVERE);
			VcloudClient vcloudClient = new VcloudClient("https://"+infra.getServer(), Version.V1_5);
			String login = decAccessId+"@"+infra.getVcloudAccountName();
			vcloudClient.registerScheme("https", infra.getPort().intValue(), FakeSSLSocketFactory.getInstance());
			vcloudClient.login(login, decSecretKey);
		return vcloudClient;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getPrototcolAsString(FirewallRuleProtocols firewallRuleProtocol){
		try{
			
			boolean any = false;
			boolean icmp = false;
			boolean tcp = false;
			boolean udp = false;
			
			try {if(firewallRuleProtocol.isAny()){any = true;}} catch (Exception e) {}
			try {if(firewallRuleProtocol.isIcmp()){icmp = true;}} catch (Exception e) {}
			try {if(firewallRuleProtocol.isTcp()){tcp = true;}} catch (Exception e) {}
			try {if(firewallRuleProtocol.isUdp()){udp = true;}} catch (Exception e) {}
			
			
		if(any){
			return Commons.PROTOCOL_TYPE_ANY;
		}else if(
				tcp && !icmp && !udp ){
			return Commons.PROTOCOL_TYPE_TCP;
		}else if(!tcp  && icmp && !udp){
			return Commons.PROTOCOL_TYPE_ICMP;
		}else if(!tcp  && !icmp && udp){
			return Commons.PROTOCOL_TYPE_UDP;
		}else if(tcp  && icmp && !udp){
			return Commons.PROTOCOL_TYPE_TCP_ICMP;
		}else if(tcp  && !icmp && udp){
			return Commons.PROTOCOL_TYPE_TCP_UDP;
		}else if(!tcp  && icmp && udp){
			return Commons.PROTOCOL_TYPE_UDP_ICMP;
		}else {
			return "";
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}//getPrototcolAsString
		
	

	@RemoteMethod
	public void syncDataFromVcloud(Infra infra) throws Exception {

		User currentUser = null;
		Company company = null;
		currentUser = Commons.getCurrentUser();
		company = Company.findCompany(Commons.getCurrentSession().getCompanyId());

		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			logger.info("SP Edition Enabled and Current user is Super Admin, synching everything cloud<-->mycp");
		} else if (company != null && Commons.EDITION_ENABLED == Commons.PRIVATE_CLOUD_EDITION_ENABLED
				&& (currentUser.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "") )) {
			logger.info("Private Edition Enabled and Current user is Account Manager, synching account related cloud<-->mycp");
		}else if (company != null && Commons.EDITION_ENABLED == Commons.HOSTED_EDITION_ENABLED
				&& (currentUser.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "") )) {
			logger.info("Hosted Edition Enabled and Current user is Account Manager, synching account related cloud<-->mycp");
		} else {
			if(Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED){
				throw new Exception("You cannot sync if you are not super admin");
			}else if(Commons.EDITION_ENABLED == Commons.PRIVATE_CLOUD_EDITION_ENABLED){
				throw new Exception("You cannot sync if you are not account manager");
			}else if(Commons.EDITION_ENABLED == Commons.HOSTED_EDITION_ENABLED){
				throw new Exception("You cannot sync if you are not account manager");
			}else{
				throw new Exception("You cannot sync. What edition of mycp are you running?");
			}
		}

		AssetType assetTypeIpAddress = AssetType.findAssetTypesByNameEquals("IpAddress").getSingleResult();
		AssetType assetTypeSecurityGroup = AssetType.findAssetTypesByNameEquals("SecurityGroup").getSingleResult();
		AssetType assetTypeVolume = AssetType.findAssetTypesByNameEquals("Volume").getSingleResult();
		AssetType assetTypeVolumeSnapshot = AssetType.findAssetTypesByNameEquals("VolumeSnapshot").getSingleResult();
		AssetType assetTypeComputeImage = AssetType.findAssetTypesByNameEquals("ComputeImage").getSingleResult();
		AssetType assetTypeComputeInstance = AssetType.findAssetTypesByNameEquals("ComputeInstance").getSingleResult();
		AssetType assetTypeKeyPair = AssetType.findAssetTypesByNameEquals("KeyPair").getSingleResult();

		ProductCatalog ipaddressProduct = null;
		ProductCatalog secGroupProduct = null;
		ProductCatalog volumeProduct = null;
		ProductCatalog snapshotProduct = null;
		ProductCatalog computeProduct = null;
		ProductCatalog imageProduct = null;
		ProductCatalog keypairProduct = null;

		Set<ProductCatalog> products = infra.getProductCatalogs();
		if (products != null && products.size() > 0) {

		} else {
			logger.error("Please set up products for this Cloud before synchronizing.");
			return;
		}
		for (Iterator iterator = products.iterator(); iterator.hasNext();) {
			ProductCatalog productCatalog = (ProductCatalog) iterator.next();
			if (productCatalog.getProductType().equals(Commons.ProductType.ComputeImage.getName())) {
				imageProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.ComputeInstance.getName())) {
				computeProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.IpAddress.getName())) {
				ipaddressProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.KeyPair.getName())) {
				keypairProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.SecurityGroup.getName())) {
				secGroupProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.Volume.getName())) {
				volumeProduct = productCatalog;
			} else if (productCatalog.getProductType().equals(Commons.ProductType.VolumeSnapshot.getName())) {
				snapshotProduct = productCatalog;
			}
		}

		if (ipaddressProduct == null) {
			logger.error("Please set up ipaddress Product for this Cloud before synchronizing.");
			return;
		} else if (secGroupProduct == null) {
			logger.error("Please set up Security Group Product for this Cloud before synchronizing.");
			return;
		} else if (volumeProduct == null) {
			logger.error("Please set up Volume Product for this Cloud before synchronizing.");
			return;
		} else if (snapshotProduct == null) {
			logger.error("Please set up Snapshot Product for this Cloud before synchronizing.");
			return;
		} else if (computeProduct == null) {
			logger.error("Please set up Compute Product for this Cloud before synchronizing.");
			return;
		} else if (imageProduct == null) {
			logger.error("Please set up Image Product for this Cloud before synchronizing.");
			return;
		} else if (keypairProduct == null) {
			logger.error("Please set up Key Pair Product for this Cloud before synchronizing.");
			return;
		}

		Date start = new Date();
		logger.info("Connect Start:" + new Date());
		VcloudClient vcloudClient = getVcloudClient(infra);
		//String ownerId = "";
		List<String> params = new ArrayList<String>();

		syncGroupDescription( params,vcloudClient,infra,assetTypeSecurityGroup,currentUser,secGroupProduct,company,start);
		
		syncIpAddress(params,vcloudClient,infra,assetTypeIpAddress, currentUser,ipaddressProduct, company,start);
		syncVolumes(params,vcloudClient,infra,assetTypeVolume,currentUser,volumeProduct,company,start);
		//no snapshots in vcloud
		Hashtable<String, SnapshotInfo> snapshotsFromCloud = new Hashtable<String, SnapshotInfo>();
		
		syncImages(params,vcloudClient,infra,assetTypeComputeImage,currentUser,imageProduct, company,start); 
		
		syncInstances(params,vcloudClient,infra,assetTypeComputeInstance,currentUser,computeProduct,company,start);
		
		//no keypair in vcloud
		Hashtable<String, KeyPairInfo> keysFromCloud = new Hashtable<String, KeyPairInfo>();

	}// end of sync

	
	public void syncGroupDescription(List<String> params,VcloudClient vcloudClient,Infra infra,AssetType assetTypeSecurityGroup,
			User currentUser,ProductCatalog secGroupProduct, Company company,Date start){
		try {
			params = new ArrayList<String>();
			logger.info("Available Security groups @" + (new Date().getTime() - start.getTime()) / 1000 + " S");
			Hashtable<String, AdminOrgNetwork> adminOrgNetworksFromCloud = new Hashtable<String, AdminOrgNetwork>();
			
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				GroupDescriptionP descriptionP = null;
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);
					adminOrgNetworksFromCloud.put(adminOrgNetwork.getReference().getName(), adminOrgNetwork);
					//System.out.println("adminOrgNetwork.getReference().getName() = "+adminOrgNetwork.getReference().getName());
					
					try {
						List<GroupDescriptionP> groups = GroupDescriptionP.findGroupDescriptionPsBy(infra, adminOrgNetwork.getReference().getName(), 
								adminOrgNetwork.getReference().getHref()).getResultList();
						inner: for (Iterator iterator2 = groups.iterator(); iterator2.hasNext();) {
							GroupDescriptionP groupDescriptionP = (GroupDescriptionP) iterator2.next();
							// check if this security group is for this cloud or for
							// some other in teh same account.
							// if same , then we do an update below.if not, we will
							// create new.
							if (groupDescriptionP.getAsset().getProductCatalog().getInfra().getId() == infra.getId()) {
								descriptionP = groupDescriptionP;
								break inner;
							}
						}

					} catch (Exception e) {
						logger.error(e.getMessage());
						descriptionP = null;
						e.printStackTrace();
					}

					if (descriptionP != null) {
						descriptionP.setDescripton(adminOrgNetwork.getReference().getHref());
						//descriptionP.setOwner(groupDescription.getOwner());
					} else {
						descriptionP = new GroupDescriptionP();
						descriptionP.setName(adminOrgNetwork.getReference().getName());
						descriptionP.setDescripton(adminOrgNetwork.getReference().getHref());
						//descriptionP.setOwner(groupDescription.getOwner());

						Asset asset = Commons.getNewAsset(assetTypeSecurityGroup, currentUser, secGroupProduct);
						descriptionP.setStatus(Commons.secgroup_STATUS.active + "");
						descriptionP.setAsset(asset);
					}

					// needed for other work in snapshot import
					//ownerId = groupDescription.getOwner();

					descriptionP = descriptionP.merge();
					
					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								
								//FirewallServiceType 
								if (jaxbElement.getValue() instanceof FirewallServiceType) {
									FirewallServiceType firewallService = (FirewallServiceType) jaxbElement.getValue();
									Set<IpPermissionP> ipPermissionPs = new HashSet<IpPermissionP>();
									for (FirewallRuleType firewallRule : firewallService.getFirewallRule()) {
										try{
											logger.info(firewallRule.getDescription() +" "+ firewallRule.getPolicy() +" "+ firewallRule.getSourceIp() +" "+ 
													firewallRule.getDestinationIp() +" "+firewallRule.getDirection()+" "+getPrototcolAsString(firewallRule.getProtocols()));
											
											IpPermissionP ipPermissionP = null;
											try {
												ipPermissionP = IpPermissionP.findIpPermissionPsByParams(descriptionP, getPrototcolAsString(firewallRule.getProtocols()), 
														firewallRule.getSourceIp(), firewallRule.getSourcePort(),
														firewallRule.getDirection(),firewallRule.getPolicy()).getSingleResult();
											} catch (Exception e) {
												e.printStackTrace();
												logger.error(e.getMessage());
											}

											if (ipPermissionP != null) {
												// do not create a new object
											} else {
												ipPermissionP = new IpPermissionP();
											}
											

											ipPermissionP.setProtocol(getPrototcolAsString(firewallRule.getProtocols()));
											ipPermissionP.setVcloudName(firewallRule.getDescription());
											ipPermissionP.setVcloudPolicy(firewallRule.getPolicy());
											ipPermissionP.setCidrIps(firewallRule.getSourceIp());
											ipPermissionP.setFromPort(firewallRule.getSourcePort());
											ipPermissionP.setVcloudDestinationIp(firewallRule.getDestinationIp());
											ipPermissionP.setToPort(firewallRule.getPort());
											ipPermissionP.setVcloudDirection(firewallRule.getDirection());
											ipPermissionP.setVcloudEnabled(firewallRule.isIsEnabled());
											
											descriptionP = descriptionP.merge();
											ipPermissionP.setGroupDescription(descriptionP);
											ipPermissionP = ipPermissionP.merge();
											if (descriptionP.getIpPermissionPs() != null) {
												descriptionP.getIpPermissionPs().add(ipPermissionP);
											} else {
												Set<IpPermissionP> ipPermissionPsNew = new HashSet<IpPermissionP>();
												ipPermissionPsNew.add(ipPermissionP);
												descriptionP.setIpPermissionPs(ipPermissionPsNew);
											}

											descriptionP = descriptionP.merge();
										}catch(Exception e){
											logger.error(e.getMessage());
											e.printStackTrace();
										}

									}//for (FirewallRuleType firewallRule : firewallService.getFirewallRule())
								}//if (jaxbElement.getValue() instanceof FirewallServiceType) 
							}//for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
						}//if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null)
						}//if (adminOrgNetwork.getResource().getConfiguration() != null)
				}//for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs())
			}//for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs())
			
			
			// clean up the security groups which are just hanging around just
			// getting created for the last 1 hour
			List<GroupDescriptionP> secGroups = null;

			if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				secGroups = GroupDescriptionP.findActiveGroupDescriptionPsByInfra(infra).getResultList();
			} else if (company != null && Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED) {
				secGroups = GroupDescriptionP.findActiveGroupDescriptionPsBy(infra, company).getResultList();
			} else {
				throw new Exception("You cannot sync if" + "1. you are not Super Admin and running a SP edition of mycloudportal."
						+ "2. you are Super Admin but running any other edition of mycloudportal");
			}

			for (Iterator secGroupiterator = secGroups.iterator(); secGroupiterator.hasNext();) {
				GroupDescriptionP groupDescriptionP = (GroupDescriptionP) secGroupiterator.next();
				try {
					if (groupDescriptionP.getStatus().equals(Commons.secgroup_STATUS.starting + "")
							&& (new Date().getTime() - groupDescriptionP.getAsset().getStartTime().getTime() > (1000 * 60 * 60))) {
						groupDescriptionP.getAsset().setEndTime(groupDescriptionP.getAsset().getStartTime());
						groupDescriptionP.setStatus(Commons.secgroup_STATUS.failed + "");
						groupDescriptionP.merge();
						continue;
					}

					/*
					 * get all groups from mycp db loop and find out if they
					 * exist in cloud if not remove them
					 */
					if (Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED){
						//remove assets in mycp only if the edition running is NOT SERVICE PROVIDER 
						//adminOrgNetworksFromCloud.put(adminOrgNetwork.getReference().getName(), adminOrgNetwork);
						
						if (adminOrgNetworksFromCloud.containsKey(groupDescriptionP.getName())
								&& adminOrgNetworksFromCloud.get(groupDescriptionP.getName()).getReference().getHref().equals(groupDescriptionP.getDescripton())) {
	
						} else {
							logger.info("removing groupDescriptionP " + groupDescriptionP.getName() + ", description " + groupDescriptionP.getDescripton()
									+ " in mycp since it does not have a corresponding entry in the cloud");
							groupDescriptionP.remove();
							continue;
						}
					}
				} catch (Exception e) {
					 e.printStackTrace();
					logger.error(e);
				}

			}//for (Iterator secGroupiterator = secGroups.iterator(); secGroupiterator.hasNext();)
			
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	public void syncIpAddress(List<String> params,VcloudClient vcloudClient,Infra infra,AssetType assetTypeIpAddress, 
			User currentUser,ProductCatalog ipaddressProduct, Company company,Date start){
		/*
		 * IP Address import
		 * 
		 * 1. get all IPs from the cloud. 2. loop thro them and compare them
		 * against the currentUser's address assets. - this is the first loop 3.
		 * clean up all address in mycp database if they are hanging in state
		 * "starting" for more than 60 mins 4. vclean up all ip addresses in
		 * mycp whose ownership has changed in the cloud
		 */

		try {
			//List<AddressInfo> addressInfos = ec2.describeAddresses(params);
			Hashtable<String, String> IpNobody = new Hashtable<String, String>();
			Hashtable<String, String> IpAll = new Hashtable<String, String>();
			
			Hashtable<String, String> externalIps = new Hashtable<String, String>();
			Hashtable<String, String> mappedIps = new Hashtable<String, String>();
			
			RecordResult<QueryResultOrgNetworkRecordType> vcResult = 
					vcloudClient.getQueryService().queryRecords(QueryRecordType.ORGNETWORK);
			
			List<ReferenceType> orgNetworkRefTypes = vcResult.getReferenceResult().getReferences();
			for (Iterator iterator = orgNetworkRefTypes.iterator(); iterator.hasNext(); ) {
				ReferenceType orgNetRefType = (ReferenceType) iterator.next();
				OrgNetwork oNetwork = OrgNetwork.getOrgNetworkByReference(vcloudClient, orgNetRefType);
				//System.out.println("oNetwork.getReference().getName() = "+oNetwork.getReference().getName());
				//OrgNetwork oNetwork = OrgNetwork.getOrgNetworkByReference(h.vcloudClient, networkRefType);
					OrgNetworkType oNetworkType = oNetwork.getResource();

					// external ip adddress
					IpAddressesType ipAddressesType = oNetworkType.getAllowedExternalIpAddresses();
					List<String> ipAddresses = ipAddressesType.getIpAddress();
					//System.out.println("\t\t Allowed External Ip addresses");
					for (Iterator iterator3 = ipAddresses.iterator(); iterator3.hasNext();) {
						String ip = (String) iterator3.next();
						logger.info("\t\t\t Ip address = " + ip);
						//externalIpsArray.add(ip);
						externalIps.put(ip, ip);
						
					}
			}
			
			
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);

					//System.out.println("adminOrgNetwork.getReference().getName() = "+adminOrgNetwork.getReference().getName());

					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {

							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								//NatServiceType	
								if (jaxbElement.getValue() instanceof NatServiceType) {
									NatServiceType natServiceType = (NatServiceType) jaxbElement.getValue();
									List<NatRuleType> natRules = natServiceType.getNatRule();
									for (Iterator iterator2 = natRules.iterator(); iterator2.hasNext(); ) {
										NatRuleType natRuleType = (NatRuleType) iterator2.next();
										try {
										
										logger.info("NAT mapping Rule = "+natRuleType.getOneToOneBasicRule().getMappingMode()+" "+natRuleType.getOneToOneBasicRule().getExternalIpAddress()
												+" --> "+natRuleType.getOneToOneBasicRule().getInternalIpAddress());
										mappedIps.put(natRuleType.getOneToOneBasicRule().getExternalIpAddress(), natRuleType.getOneToOneBasicRule().getInternalIpAddress());
										} catch (Exception e) {
											// TODO: handle exception
										}
										
										try {
											logger.info("Port fwding rule: "+natRuleType.getPortForwardingRule().getExternalIpAddress()+" "+natRuleType.getPortForwardingRule().getExternalPort()
													+" "+natRuleType.getPortForwardingRule().getInternalIpAddress()+" "
													+ natRuleType.getPortForwardingRule().getInternalPort() +" "
													+ natRuleType.getPortForwardingRule().getProtocol());
											
											
											mappedIps.put(natRuleType.getPortForwardingRule().getExternalIpAddress(),natRuleType.getPortForwardingRule().getInternalIpAddress());
													
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
									
								}
							}
						}
					}
				}
			}
			

			logger.info("Importing available addresses. start @ " + (new Date().getTime() - start.getTime()) / 1000 + " S");
			Enumeration<String> extIps = externalIps.keys();
			while (extIps.hasMoreElements()) {
				String extIp = (String) extIps.nextElement();
				/*System.out.println("extIp = "+extIp);
				System.out.println("mappedIps = "+mappedIps);
				System.out.println("mappedIps.get(extIp) = "+mappedIps.get(extIp));*/
				
				if(mappedIps.get(extIp) == null){
					IpNobody.put(extIp, "nobody");
					// import only for superuser , for everybody else , just
					// skip the import process for free IPs.
					if (!currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
						continue;
					}// if NOT super user
				}else{
					IpAll.put(extIp, mappedIps.get(extIp));
				}
				
				AddressInfoP addressInfoP = null;
				try {
					addressInfoP = AddressInfoP.findAddressInfoPsBy(infra, extIp, company).getSingleResult();

				} catch (Exception e) {
					logger.error(e.getMessage());//
					e.printStackTrace();
				}
				if (addressInfoP == null) {
					addressInfoP = new AddressInfoP();
					Asset asset = Commons.getNewAsset(assetTypeIpAddress, currentUser, ipaddressProduct);
					addressInfoP.setAsset(asset);
					addressInfoP.setStatus(Commons.ipaddress_STATUS.available + "");
					addressInfoP = addressInfoP.merge();
				}

				addressInfoP.setInstanceId(mappedIps.get(extIp));
				addressInfoP.setPublicIp(extIp);
				addressInfoP = addressInfoP.merge();
				
			}//while (extIps.hasMoreElements())
			
			// clean up all address in mycp database if they are hanging in
			// state "starting" for more than 60 mins
			// //clean up the ip adresses in mycp which are not in a
			// corresponding state in the cloud
			List<AddressInfoP> addresses = null;

			if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				addresses = AddressInfoP.findAddressInfoPsByInfra(infra).getResultList();
			} else if (company != null && Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED) {
				addresses = AddressInfoP.findAddressInfoPsBy(infra, company).getResultList();
			} else {
				throw new Exception("You cannot sync if" + "1. you are not Super Admin and running a SP edition of mycloudportal."
						+ "2. you are Super Admin but running any other edition of mycloudportal");
			}

			for (Iterator iterator = addresses.iterator(); iterator.hasNext();) {
				AddressInfoP addressInfoP = (AddressInfoP) iterator.next();
				try {
					if (addressInfoP.getStatus().equals(Commons.ipaddress_STATUS.starting + "")
							&& (new Date().getTime() - addressInfoP.getAsset().getStartTime().getTime() > (1000 * 60 * 60))) {
						addressInfoP.getAsset().setEndTime(addressInfoP.getAsset().getStartTime());
						addressInfoP.setStatus(Commons.ipaddress_STATUS.failed + "");
						addressInfoP.merge();
						continue;
					}

					// remove those ip address for which the ownership has changed
					if (Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED){
						//remove assets in mycp only if the edition running is NOT SERVICE PROVIDER 
						if (addressInfoP.getPublicIp() != null && addressInfoP.getInstanceId() != null && IpAll.containsKey(addressInfoP.getPublicIp())
								&& !addressInfoP.getInstanceId().equals(IpAll.get(addressInfoP.getPublicIp()))) {
							logger.info("removing ip address in MYCP for those whose ownership has changed in the cloud " + addressInfoP.getName() + " "
									+ addressInfoP.getPublicIp() + " " + addressInfoP.getInstanceId());
							addressInfoP.remove();
							continue;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void syncVolumes(List<String> params,VcloudClient vcloudClient,Infra infra,AssetType assetTypeVolume, 
			User currentUser,ProductCatalog volumeProduct, Company company,Date start) {


		Hashtable<String, VirtualDisk> volumesFromCloud = new Hashtable<String, VirtualDisk>();

		try {

			params = new ArrayList<String>();
			//List<VolumeInfo> volumes = ec2.describeVolumes(params);
			logger.info("Available Volumes");
			RecordResult<QueryResultVMRecordType> vcResult = vcloudClient.getQueryService().queryRecords(QueryRecordType.VM);
			List<ReferenceType> vmRefTypes = vcResult.getReferenceResult().getReferences();
			logger.info("vmRefTypes.size() = "+vmRefTypes.size());
			for (Iterator iterator = vmRefTypes.iterator(); iterator.hasNext(); ) {
				try {
					ReferenceType vmRefType = (ReferenceType) iterator.next();
					logger.info("vmRefType.getName() = "+vmRefType.getName());
					VM vm = VM.getVMByReference(vcloudClient, vmRefType);
					String href = vm.getParentVappReference().getHref(); 
					Date vappCreationDate=null;
					 RecordResult<QueryResultVAppRecordType> vappResult = vcloudClient
			                    .getQueryService().queryRecords(QueryRecordType.VAPP);
			          for (QueryResultVAppRecordType vappRecord : vappResult.getRecords()) {
			        	  if(vappRecord.getHref().equals(href)){
			        		  vappCreationDate=new Date(vappRecord.getCreationDate().getYear(), vappRecord.getCreationDate().getMonth(), vappRecord.getCreationDate().getDay(), vappRecord.getCreationDate().getHour(), vappRecord.getCreationDate().getMinute());
			        	  }
			          }
	          
					List<VirtualDisk> vdisks = vm.getDisks();
					for (Iterator iterator5 = vdisks.iterator(); iterator5.hasNext();) {
						VirtualDisk virtualDisk = (VirtualDisk) iterator5.next();
						if (virtualDisk.isHardDisk()) {
							String volId ="vcloud-"+vm.getReference().getName()+"-"+virtualDisk.getItemResource().getInstanceID().getValue();
							
							volumesFromCloud.put(volId, virtualDisk);
							
							logger.info("Disk for " + vm.getReference().getName() + " , virtualDisk.getHardDiskBusType() = "
									+ virtualDisk.getHardDiskBusType() + " , virtualDisk.getHardDiskSize() = " + virtualDisk.getHardDiskSize());
							
							/*System.out.println("virtualDisk.getItemResource().getAddressOnParent() = "+virtualDisk.getItemResource().getAddressOnParent());
							System.out.println("virtualDisk.getItemResource().getElementName().getValue() = "+virtualDisk.getItemResource().getElementName().getValue());
							
							System.out.println("virtualDisk.getItemResource().getInstanceID().getValue() = "+virtualDisk.getItemResource().getInstanceID().getValue());*/
							
							VolumeInfoP volumeInfoP = null;
							try {
								volumeInfoP = VolumeInfoP.findVolumeInfoPsBy(infra, volId, company).getSingleResult();
							} catch (Exception e) {
								logger.error(e.getMessage());//
								e.printStackTrace();
							}
							if (volumeInfoP != null) {

							} else {
								volumeInfoP = new VolumeInfoP();
								Asset asset = Commons.getNewAsset(assetTypeVolume, currentUser, volumeProduct);

								volumeInfoP.setAsset(asset);

							}
							volumeInfoP.setSize(virtualDisk.getHardDiskSize().intValue()/1024);
							volumeInfoP.setVolumeId(volId);
							//in vcloud , cannot get volume creation time , hence adding the Vapp creation time to which this volume belongs
							volumeInfoP.setCreateTime(vappCreationDate);
							volumeInfoP.setZone("vcloud_no_zone");
							volumeInfoP.setStatus(Commons.VOLUME_STATUS_ATTACHED);
							volumeInfoP.setSnapshotId("vcloud_no_snapshot");
							volumeInfoP.setDevice(virtualDisk.getItemResource().getElementName().getValue());
							volumeInfoP.setInstanceId(vm.getResource().getId());
							volumeInfoP = volumeInfoP.merge();
							
							
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					
					//e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
		List<VolumeInfoP> vols = null;

		if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			vols = VolumeInfoP.findVolumeInfoPsByInfra(infra).getResultList();
		} else if (company != null && Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED) {
			vols = VolumeInfoP.findVolumeInfoPsBy(infra, company).getResultList();
		} else {
			throw new Exception("You cannot sync if" + "1. you are not Super Admin and running a SP edition of mycloudportal."
					+ "2. you are Super Admin but running any other edition of mycloudportal");
		}

		for (Iterator volIterator = vols.iterator(); volIterator.hasNext();) {
			VolumeInfoP volumeInfo2 = (VolumeInfoP) volIterator.next();
			try {
				if (volumeInfo2.getStatus().equals(Commons.VOLUME_STATUS_CREATING)
						&& (new Date().getTime() - volumeInfo2.getAsset().getStartTime().getTime() > (1000 * 60 * 60))) {
					volumeInfo2.getAsset().setEndTime(volumeInfo2.getAsset().getStartTime());
					volumeInfo2.setStatus(Commons.VOLUME_STATUS_FAILED);
					volumeInfo2.merge();
					continue;
				}
				
				if (Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED){
					//remove assets in mycp only if the edition running is NOT SERVICE PROVIDER 
					if (volumesFromCloud.containsKey(volumeInfo2.getVolumeId()) && 
							(volumesFromCloud.get(volumeInfo2.getVolumeId()).getHardDiskSize().intValue()/1024) == (volumeInfo2.getSize())) {
	
					} else {
						logger.info("removing volumeInfo " + volumeInfo2.getVolumeId() + ", size " + volumeInfo2.getSize()
								+ " in mycp since it does not have a corresponding entry in the cloud");
						volumeInfo2.remove();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}	
	}catch(Exception e){
	e.printStackTrace();			
	}
	}//end of method
	
	public void syncImages(List<String> params,VcloudClient vcloudClient,Infra infra,AssetType assetTypeComputeImage,
			User currentUser,ProductCatalog imageProduct, Company company,Date start) {

		//images are templates in vcloud
		Hashtable<String, VappTemplate> imagesFromCloud = new Hashtable<String, VappTemplate>();

		try {
			
			//List<ImageDescription> images = ec2.describeImages(params);
			
			logger.info("Listing Available Images......");
			int imageCount = 0;
			RecordResult<QueryResultCatalogRecordType> catResult =vcloudClient.getQueryService().queryRecords(QueryRecordType.CATALOG);
			List<ReferenceType> catRefTypes  =  catResult.getReferenceResult().getReferences();
			for (Iterator iterator = catRefTypes.iterator(); iterator.hasNext(); ) {
				ReferenceType catRefType = (ReferenceType) iterator.next();
				//System.out.println("catRefType.getName() = "+catRefType.getName());
				String catalogName = catRefType.getName();
				Catalog cat = Catalog.getCatalogByReference(vcloudClient, catRefType);
				//System.out.println("cat.getResource().isIsPublished() = "+cat.getResource().isIsPublished());
				boolean isCatalogPublic = cat.getResource().isIsPublished();
				Collection<ReferenceType> catItemRefs = cat.getCatalogItemReferences();
				for (Iterator iterator2 = catItemRefs.iterator(); iterator2.hasNext(); ) {
					try{
						ReferenceType catItemRef = (ReferenceType) iterator2.next();
						CatalogItem catItem = CatalogItem.getCatalogItemByReference(vcloudClient, catItemRef);
						if(catItem.getEntityReference().getType().indexOf("vAppTemplate+xml") == -1){
							continue;
						}
						VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(vcloudClient, catItem.getEntityReference());
						String imageId = vappTemplate.getResource().getId();
						imagesFromCloud.put(imageId, vappTemplate);
						logger.info(vappTemplate.getResource().getId() + "\t" + vappTemplate.getResource().getName() + "\t" + vappTemplate.getOwner().getName());
						
						ImageDescriptionP imageDescriptionP = null;
	
						try {
							imageDescriptionP = ImageDescriptionP.findImageDescriptionPsBy(infra,vappTemplate.getResource().getId(), company).getSingleResult();
						} catch (Exception e) {
							logger.error(e.getMessage());//
							e.printStackTrace();
						}
						if (imageDescriptionP != null) {
	
						} else {
							imageDescriptionP = new ImageDescriptionP();
							Asset asset = Commons.getNewAsset(assetTypeComputeImage, currentUser, imageProduct);
							imageDescriptionP.setAsset(asset);
							imageDescriptionP = imageDescriptionP.merge();
						}
	
						imageDescriptionP.setImageId(imageId);
	
						imageDescriptionP.setImageLocation(vappTemplate.getResource().getHref());
						imageDescriptionP.setImageOwnerId(vappTemplate.getOwner().getName());
						imageDescriptionP.setImageState(vappTemplate.getVappTemplateStatus().name());
						imageDescriptionP.setIsPublic(isCatalogPublic);
						
						//imageDescriptionP.setProductCodes(prodCodes_str);
						//imageDescriptionP.setArchitecture(img.getArchitecture());
						//imageDescriptionP.setImageType(img.getImageType());
						//imageDescriptionP.setKernelId(img.getKernelId());
						//imageDescriptionP.setRamdiskId(img.getRamdiskId());
						//imageDescriptionP.setPlatform(img.getPlatform());
						//imageDescriptionP.setReason(img.getReason());
						imageDescriptionP.setImageOwnerAlias(vappTemplate.getOwner().getName());
	
						imageDescriptionP.setName(vappTemplate.getResource().getName());
						imageDescriptionP.setDescription(vappTemplate.getResource().getDescription());
						//imageDescriptionP.setRootDeviceType(img.getRootDeviceType());
						//imageDescriptionP.setRootDeviceName(img.getRootDeviceName());
						//imageDescriptionP.setVirtualizationType(img.getVirtualizationType());
	
						imageDescriptionP.merge();
					}catch(Exception e){
					e.printStackTrace();
					}
					
					/*System.out.println("name =   " +  vappTemplate.getReference().getName()+" , \t  isVM= " 
							+ vappTemplate.isVm() + " , status="
									+ vappTemplate.getVappTemplateStatus()+" , Size(GB)  "+(vappTemplate.getVappTemplateSize().longValue()/1024));
					
					System.out.println("vappTemplate.getResource().getId() = "+vappTemplate.getResource().getId());
					System.out.println("vappTemplate.getResource().getName() = "+vappTemplate.getResource().getName());
					System.out.println("vappTemplate.getResource().getType() = "+vappTemplate.getResource().getType());
					System.out.println("vappTemplate.getReference().getHref() = "+vappTemplate.getReference().getHref());
					System.out.println("vappTemplate.getVappTemplateStatus() = "+vappTemplate.getVappTemplateStatus());
					System.out.println("vappTemplate.getOwner().getId() = "+vappTemplate.getOwner().getId());
					System.out.println("vappTemplate.getOwner().getName() = "+vappTemplate.getOwner().getName());
					
					System.out.println("\n +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ \n");*/
				}
				
			}
			
			// now clean up the images in mycp db which do not exist in
			// the cloud.
			List<ImageDescriptionP> imagesInMycp = null;

			if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				imagesInMycp = ImageDescriptionP.findImageDescriptionPsByInfra(infra).getResultList();
			} else if (company != null && Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED) {
				imagesInMycp = ImageDescriptionP.findImageDescriptionPsByCompany(infra, company).getResultList();
			} else {
				throw new Exception("You cannot sync if" + "1. you are not Super Admin and running a SP edition of mycloudportal."
						+ "2. you are Super Admin but running any other edition of mycloudportal");
			}

			if (Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED){
				//remove assets in mycp only if the edition running is NOT SERVICE PROVIDER 
				for (Iterator iterator = imagesInMycp.iterator(); iterator.hasNext();) {
				ImageDescriptionP imageDescriptionP = (ImageDescriptionP) iterator.next();
				try {
					/*System.out.println("imagesFromCloud = "+imagesFromCloud);
					System.out.println("imageDescriptionP = "+imageDescriptionP);
					System.out.println("imageDescriptionP.getImageId() = "+imageDescriptionP.getImageId());
					System.out.println("imagesFromCloud.containsKey(imageDescriptionP.getImageId()) = "+imagesFromCloud.containsKey(imageDescriptionP.getImageId()));*/
					if (imageDescriptionP.getImageId()!=null && imagesFromCloud.containsKey(imageDescriptionP.getImageId())
							&& imagesFromCloud.get(imageDescriptionP.getImageId()).getResource().getHref().equals(imageDescriptionP.getImageLocation())) {
				
					} else {
						logger.info("removing imageDescriptionP " + imageDescriptionP.getImageId() + ", location " + imageDescriptionP.getImageLocation()
								+ " in mycp since it does not have a corresponding entry in the cloud");
							imageDescriptionP.remove();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end syncImages
	
	public void syncInstances(List<String> params,VcloudClient vcloudClient,Infra infra,AssetType assetTypeComputeInstance,
			User currentUser,ProductCatalog computeProduct, Company company,Date start) {
		

		Hashtable<String, VM> instancesFromCloud = new Hashtable<String, VM>();

		try {
			params = new ArrayList<String>();
			//List<ReservationDescription> instances = ec2.describeInstances(params);
			logger.info("Listing Instances/VMs");
			String instanceId = "";
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
			Date now = new Date();
			
			
			
			RecordResult<QueryResultVAppRecordType> vappResults = 
					vcloudClient.getQueryService().queryRecords(QueryRecordType.VAPP);
			List<ReferenceType> vappRefTypes  =  vappResults.getReferenceResult().getReferences();
			for (Iterator iterator = vappRefTypes.iterator(); iterator.hasNext(); ) {
				ReferenceType vappRefType = (ReferenceType) iterator.next();
				Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRefType);
				
				String href = vapp.getReference().getHref(); 
				Date vappCreationDate=null;
				 RecordResult<QueryResultVAppRecordType> vappResult = vcloudClient
		                    .getQueryService().queryRecords(QueryRecordType.VAPP);
		          for (QueryResultVAppRecordType vappRecord : vappResult.getRecords()) {
		        	  if(vappRecord.getHref().equals(href)){
		        		  vappCreationDate=new Date(vappRecord.getCreationDate().getYear(), vappRecord.getCreationDate().getMonth(), vappRecord.getCreationDate().getDay(), vappRecord.getCreationDate().getHour(), vappRecord.getCreationDate().getMinute());
		        	  }
		          }
		          
				List<VM> vms = vapp.getChildrenVms();
				for (Iterator iterator2 = vms.iterator(); iterator2.hasNext(); ) {
					try {
						VM vm = (VM) iterator2.next();
						String vmId = vm.getResource().getId();
						String vmName = vm.getResource().getName() +" ("+vapp.getResource().getName()+")";
						boolean isDeployed = vm.isDeployed();
						//int noOfCpus = vm.getCpu().getNoOfCpus();
						
						double noOfCpus = vm.getCpu().getNoOfCpus();
						double d1 = new BigDecimal(vm.getMemory().getMemorySize().toString()).doubleValue();
						double d2 = new BigDecimal("1024").doubleValue();
						
						double memorySizeGB = d1/d2;
						//int memorySizeGB = vm.getMemory().getMemorySize().intValue()/1024;
						String vmStatus = vm.getVMStatus().name();
						
						logger.info("Name ="+vm.getResource().getName()+" isDeployed = " 
								+ vm.isDeployed() + " , NoOfCpus = "
										+ vm.getCpu().getNoOfCpus() + " , Memory Size= " + vm.getMemory().getMemorySize()
										+ " , Status= " + vm.getVMStatus());
							String ipAddress ="";
							logger.info("looping vm.getIpAddressesById()");
								HashMap<Integer, String>  ipAddreses = vm.getIpAddressesById();
										Set<Integer> keys = ipAddreses.keySet();
								for (Iterator iterator25 = keys.iterator(); iterator25.hasNext();) {
									Integer integer = (Integer) iterator25.next();
									logger.info("ipAddreses.get(integer) "+ipAddreses.get(integer));
									ipAddress=ipAddress+ipAddreses.get(integer)+",";
								}
								
							ipAddress = StringUtils.removeEnd(ipAddress, ",");
							logger.info("looping vm.getNetworkCards()");
								List<VirtualNetworkCard> cards = vm.getNetworkCards();
								for (Iterator iterator3 = cards.iterator(); iterator3.hasNext();) {
									try {
										VirtualNetworkCard virtualNetworkCard = (VirtualNetworkCard) iterator3
												.next();
										logger.info("virtualNetworkCard.getIpAddress() = "
												+ virtualNetworkCard
														.getIpAddress());
									} catch (Exception e) {
										logger.error(e.getMessage());
									}
								}
								
								String platformVendor = "";
								String platformVersion ="";
								
								try {
									if(vm.getPlatformSection() !=null){
										logger.info("vm.getPlatformSection Vendor Version = "+vm.getPlatformSection().getVendor().getValue()+" "
												+vm.getPlatformSection().getVersion().getValue());
										
										platformVendor = vm.getPlatformSection().getVendor().getValue();
										platformVersion = vm.getPlatformSection().getVersion().getValue();
									}	
								} catch (Exception e) {
									logger.error(e.getMessage());
									//e.printStackTrace();
								}
								
								String osName = "";
								String osVersion ="";
								try {
									if(vm.getOperatingSystemSection() !=null){
										logger.info(" getOperatingSystemSection "+vm.getOperatingSystemSection().getDescription().getValue()+" "
												+vm.getOperatingSystemSection().getVersion());

										osName=vm.getOperatingSystemSection().getDescription().getValue();
										osVersion = vm.getOperatingSystemSection().getVersion();
									}	
								} catch (Exception e) {
									logger.error(e.getMessage());//e.printStackTrace();
								}
								
								List<VirtualDisk> vdisks = vm.getDisks();
								double diskSizeGB = 0;
								String instanceType="";
								try {
								for (Iterator iterator5 = vdisks.iterator(); iterator5.hasNext();) {
									VirtualDisk virtualDisk = (VirtualDisk) iterator5.next();
									if (virtualDisk.isHardDisk()) {
										logger.info("Disk for " + vm.getReference().getName() + " , virtualDisk.getHardDiskBusType() = "
												+ virtualDisk.getHardDiskBusType() + " , virtualDisk.getHardDiskSize() = " + virtualDisk.getHardDiskSize());
										diskSizeGB = diskSizeGB +(virtualDisk.getHardDiskSize().intValue()/1024);
									}
								}
								
								} catch (Exception e) {
									logger.error(e.getMessage());// TODO: handle exception
								}	
								instanceType = "vCloud (RAM "+memorySizeGB+" GB, CPU "+noOfCpus+" , HD "+diskSizeGB+" GB)";
								
						instancesFromCloud.put(vmId, vm);

						logger.info("\t" + vmId + "\t" + vmId + "\t" + vmStatus + "\t"  
								+ vappCreationDate + "\t " + memorySizeGB+" GB "+noOfCpus+" CPUs " + osName);

						InstanceP instanceP = null;
						try {
							instanceP = InstanceP.findInstancePsBy(infra, vmId, company).getSingleResult();
						} catch (Exception e) {
							logger.error(e.getMessage());//
							e.printStackTrace();
						}

						if (instanceP != null) {
							//while syncing , I cant get the image from tehvapp, so if the sync is working for an existing vapp inside mycp, keep the image relation.
							instanceP.setImage(instanceP.getImage());
							instanceP.setName(instanceP.getName());
						} else {
							instanceP = new InstanceP();
							Asset asset = Commons.getNewAsset(assetTypeComputeInstance, currentUser, computeProduct);
							instanceP.setAsset(asset);
						}

						instanceP.setInstanceId(vmId);
						//TODO charu - get the href of vapp template here
						//cant get this refer http://communities.vmware.com/message/2153649#2153649
						
						try {
							//comment this off because of the the above reason for now
							//instanceP.setImage(ImageDescriptionP.findImageDescriptionPsByImageIdEquals(vapp.getResource().getId()).getSingleResult());
						
						} catch (Exception e) {
							logger.error(e.getMessage());
							e.printStackTrace();
						}
						instanceP.setDnsName(ipAddress);
						instanceP.setState(vmStatus);
						instanceP.setKeyName("no_key_vmware");
						instanceP.setInstanceType(instanceType);
						instanceP.setPlatform(osName);
						instanceP.setPrivateDnsName(ipAddress);
						//instanceP.setReason(inst.getReason());
						//instanceP.setLaunchIndex(inst.getLaunchIndex());

						
						//instanceP.setProductCodes(prodCodes_str);
						instanceP.setLaunchTime(vappCreationDate);
						//instanceP.setAvailabilityZone(inst.getAvailabilityZone());
						//instanceP.setKernelId(inst.getKernelId());
						//instanceP.setRamdiskId(inst.getRamdiskId());
						instanceP.setStateCode(vmStatus);
						// instanceP.setMonitoring(inst.get)
						//instanceP.setSubnetId(inst.getSubnetId());
						//instanceP.setVpcId(inst.getVpcId());
						instanceP.setPrivateIpAddress(ipAddress);
						instanceP.setIpAddress(ipAddress);
						instanceP.setArchitecture(osName);
						//instanceP.setRootDeviceType(inst.getRootDeviceType());
						//instanceP.setRootDeviceName(inst.getRootDeviceName());
						//instanceP.setInstanceLifecycle(inst.getInstanceLifecycle());
						//instanceP.setSpotInstanceRequestId(inst.getSpotInstanceRequestId());
						instanceP.setVirtualizationType(platformVendor);
						// instanceP.setState(Commons.REQUEST_STATUS.running+"");
						// instanceP.setClientToken(inst.get)

						// instanceP.setReservationDescription(reservationDescriptionP);

						instanceP.setVcloudVmHref(vm.getReference().getHref());
						instanceP.setVcloudVappHref(vapp.getReference().getHref());
						
						instanceP.setVcloudCpu(noOfCpus);
						instanceP.setVcloudRamGb(memorySizeGB);
						instanceP.setVcloudDiskGb(diskSizeGB);
						
						instanceP = instanceP.merge();

						
						
						
					} catch (Exception e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
					
				}//for (Iterator iterator2 = vms.iterator(); iterator2.hasNext(); )
				
			} // for (Iterator iterator = vappRefTypes.iterator(); iterator.hasNext(); )
			
			
			List<InstanceP> insts = null;

			if (Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED && currentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				insts = InstanceP.findInstancePsByInfra(infra).getResultList();
			} else if (company != null && Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED) {
				insts = InstanceP.findInstancePsBy(infra, company).getResultList();
			} else {
				throw new Exception("You cannot sync if" + "1. you are not Super Admin and running a SP edition of mycloudportal."
						+ "2. you are Super Admin but running any other edition of mycloudportal");
			}

			for (Iterator iterator = insts.iterator(); iterator.hasNext();) {
				InstanceP instanceP2 = (InstanceP) iterator.next();
				//instances which are pending approval need not be compared.
				if(instanceP2.getInstanceId() == null){
					continue;
				}
				
				try {
					if (instanceP2.getState().equals(Commons.REQUEST_STATUS.STARTING + "")
							&& (new Date().getTime() - instanceP2.getAsset().getStartTime().getTime() > (1000 * 60 * 60 * 3))) {
						instanceP2.getAsset().setEndTime(instanceP2.getAsset().getStartTime());
						instanceP2.setState(Commons.REQUEST_STATUS.FAILED + "");
						instanceP2.merge();
					}
					
					if (Commons.EDITION_ENABLED != Commons.SERVICE_PROVIDER_EDITION_ENABLED){
						//remove assets in mycp only if the edition running is NOT SERVICE PROVIDER
						
						
						if (instancesFromCloud.containsKey(instanceP2.getInstanceId())) {
	
						} else {
							try {
								
							logger.info("removing instanceP " + instanceP2.getInstanceId() + ", image " + instanceP2.getImage().getImageId()
									+ " in mycp since it does not have a corresponding entry in the cloud");
							
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
							
							instanceP2.remove();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}//end syncInstances

}// end of class

