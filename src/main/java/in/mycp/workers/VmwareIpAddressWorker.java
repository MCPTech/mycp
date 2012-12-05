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

package in.mycp.workers;

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vmware.vcloud.api.rest.schema.IpAddressesType;
import com.vmware.vcloud.api.rest.schema.NatPortForwardingRuleType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.OrgNetworkType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.OrgNetwork;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrgNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.xerox.amazonws.ec2.AddressInfo;
import com.xerox.amazonws.ec2.Jec2;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("vmwareIpAddressWorker")
public class VmwareIpAddressWorker extends Worker {

	@Autowired
	AccountLogService accountLogService;

	protected static Logger logger = Logger.getLogger(VmwareIpAddressWorker.class);

	@Async
	public void allocateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);
			
			VcloudClient  vcloudClient = getVcloudClient(infra);
			ArrayList<Object> externalIpsFromCloud = new ArrayList<Object>();
			ArrayList<Object> natRulesFromCloud = new ArrayList<Object>();
			Hashtable<String, NatRuleType> usedIps = new Hashtable<String, NatRuleType>();
			String allocatedPublicIp=null;
			
			//get all external IPs from cloud
			Collection<ReferenceType> orgRefs =vcloudClient.getOrgRefs();
			for (Iterator iterator = orgRefs.iterator(); iterator.hasNext();) {
				ReferenceType orgRefType = (ReferenceType) iterator.next();
				Organization o = Organization.getOrganizationByReference(vcloudClient, orgRefType);
				Collection<ReferenceType> networkRefs = o.getNetworkRefs();
				for (Iterator iterator2 = networkRefs.iterator(); iterator2.hasNext();) {
					ReferenceType networkRefType = (ReferenceType) iterator2.next();
					OrgNetwork oNetwork = OrgNetwork.getOrgNetworkByReference(vcloudClient, networkRefType);
					OrgNetworkType oNetworkType = oNetwork.getResource();
					IpAddressesType ipAddressesType = oNetworkType.getAllowedExternalIpAddresses();
					List<String> ipAddresses = ipAddressesType.getIpAddress();
					for (Iterator iterator3 = ipAddresses.iterator(); iterator3.hasNext();) {
						String ip = (String) iterator3.next();
						externalIpsFromCloud.add(ip);
						
					}
				}
			}
			
			//get all current mappings from cloud
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);
					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								if (jaxbElement.getValue() instanceof NatServiceType) {
									NatServiceType natService = (NatServiceType) jaxbElement.getValue();
									List<NatRuleType> natRules = natService.getNatRule();
									NatRuleType natRuleTypeToBeRemoved = null;
									for (Iterator iterator = natRules.iterator(); iterator.hasNext();) {
										NatRuleType natRuleType = (NatRuleType) iterator.next();
										if (natRuleType.getPortForwardingRule() != null) {
											natRulesFromCloud.add(natRuleType);
										}
									}
								}
							}
						}
					}
				}
			}
				
			
			
			//put all usedIps into a hashtable
			boolean ipUsed = true;
			for (Iterator iterator = externalIpsFromCloud.iterator(); iterator.hasNext(); ) {
				String ipFromCloud = (String) iterator.next();
				for (Iterator natIterator = natRulesFromCloud.iterator(); natIterator.hasNext(); ) {
					NatRuleType natRuleType = (NatRuleType) natIterator.next();
					logger.info("ipFromCloud = "+ipFromCloud+" natRuleType.getPortForwardingRule().getExternalIpAddress() = "+natRuleType.getPortForwardingRule().getExternalIpAddress());
					if(ipFromCloud.equals(natRuleType.getPortForwardingRule().getExternalIpAddress())){
						usedIps.put(ipFromCloud, natRuleType);
					}
				}
			}
			
			//check what is a free Ip in the cloud and assign it
			outer :  for (Iterator iterator = externalIpsFromCloud.iterator(); iterator.hasNext(); ) {
				String ipFromCloud = (String) iterator.next();
				if(!usedIps.containsKey(ipFromCloud)){
					
					List<AddressInfoP> addressesInMycp = AddressInfoP.findAddressInfoPsByPublicIpEquals(ipFromCloud).getResultList();
					//check if there are no AddressInfoPs assigned with this IP
					if(addressesInMycp.size()==0){
						allocatedPublicIp = ipFromCloud;
						break outer;	
					}else{
						//even if there are some AddressInfoPs assigned with this IP, make sure they are dead and gone
						for (Iterator iterator2 = addressesInMycp.iterator(); iterator2.hasNext(); ) {
							AddressInfoP addressInfoP2 = (AddressInfoP) iterator2.next();
							if(addressInfoP2.getAssociated() !=null && 
									addressInfoP2.getAssociated() == false && addressInfoP2.getStatus() !=null && 
									!addressInfoP2.getStatus().equals(Commons.ipaddress_STATUS.available+"")
									&& !addressInfoP2.getStatus().equals(Commons.ipaddress_STATUS.associated+"")
									&& !addressInfoP2.getStatus().equals(Commons.ipaddress_STATUS.starting+"")){
								allocatedPublicIp = ipFromCloud;
								break outer;
							}
						}	
					}
					
					
				}
			}
			
			
			
			if(allocatedPublicIp !=null){
				addressInfoP.setPublicIp(allocatedPublicIp);
				addressInfoP.setAssociated(false);
				addressInfoP.setStatus(Commons.ipaddress_STATUS.available+"");
				addressInfoP.merge();
				setAssetStartTime(addressInfoP.getAsset());
				
				accountLogService.saveLogAndSendMail("Completed : "
						+ this.getClass().getName()
						+ " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName()
								.subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
						Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);
				
			}else{
				addressInfoP.setPublicIp("");
				addressInfoP.setAssociated(false);
				addressInfoP.setStatus(Commons.ipaddress_STATUS.failed+"");
				addressInfoP.merge();
				setAssetEndTime(addressInfoP.getAsset());
				throw new Exception("No more free IPs in the cloud "+infra.getName());
			}
			

		} catch (Exception e) {
			logger.error(e); e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.FAIL.ordinal(), userId);
			try {
				AddressInfoP a = AddressInfoP.findAddressInfoP(addressInfoP.getId());
				a.setStatus(Commons.ipaddress_STATUS.failed + "");
				a = a.merge();
				setAssetEndTime(a.getAsset());
			} catch (Exception e2) {
				// TODO: handle exception
			}

		}

	}// end allocateAddress

	@Async
	public void releaseAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {

		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);
			
			VcloudClient  vcloudClient = getVcloudClient(infra);
			String ipToMatch = addressInfoP.getPublicIp();
			
			//check if we can release this address
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);
					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								if (jaxbElement.getValue() instanceof NatServiceType) {
									NatServiceType natService = (NatServiceType) jaxbElement.getValue();
									List<NatRuleType> natRules = natService.getNatRule();
									NatRuleType natRuleTypeToBeRemoved = null;
									for (Iterator iterator = natRules.iterator(); iterator.hasNext();) {
										NatRuleType natRuleType = (NatRuleType) iterator.next();
										if (natRuleType.getPortForwardingRule() != null) {
											if(natRuleType.getPortForwardingRule().getExternalIpAddress().equals(ipToMatch)){
												throw new Exception("Nat Rule associated with Private Ip "+natRuleType.getPortForwardingRule().getInternalIpAddress());
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			//set the public Ip opf the associated Instance to null
			/*InstanceP instance = InstanceP.findInstancePsByInstanceIdEquals(addressInfoP.getInstanceId()).getSingleResult();
			instance.setIpAddress("");*/
			
			//then update the addresinfoP
			addressInfoP.setAssociated(false);
			addressInfoP.setStatus(Commons.ipaddress_STATUS.released+"");
			setAssetEndTime(addressInfoP.getAsset());
			addressInfoP.merge();
			
			//finally delete it.
			AddressInfoP.findAddressInfoP(addressInfoP.getId()).remove();
			
			accountLogService.saveLogAndSendMail("Completed : "
					+ this.getClass().getName()
					+ " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName()
							.subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			
		} catch (Exception e) {
			logger.error(e);e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.FAIL.ordinal(), userId);
			try {
				AddressInfoP a = AddressInfoP.findAddressInfoP(addressInfoP.getId());
				a.setStatus(Commons.ipaddress_STATUS.failed + "");
				a = a.merge();
				setAssetEndTime(a.getAsset());
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}// end of releaseAddress
	
	
	
	
	
	
	@Async
	public void associateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			InstanceP instanceP = InstanceP.findInstancePsByInstanceIdEquals(addressInfoP.getInstanceId()).getSingleResult();
			String privateIp = instanceP.getPrivateIpAddress();
			String publicIp = addressInfoP.getPublicIp();

			VcloudClient vcloudClient = getVcloudClient(infra);
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);
					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								if (jaxbElement.getValue() instanceof NatServiceType) {
									NatServiceType natService = (NatServiceType) jaxbElement.getValue();

									NatRuleType nr1 = new NatRuleType();
									NatPortForwardingRuleType portfwd = new NatPortForwardingRuleType();
									portfwd.setExternalIpAddress(publicIp);
									portfwd.setExternalPort(-1);
									portfwd.setInternalIpAddress(privateIp);
									portfwd.setInternalPort(-1);
									portfwd.setProtocol("TCP_UDP");

									nr1.setPortForwardingRule(portfwd);
									nr1.setDescription(addressInfoP.getName());
									natService.getNatRule().add(nr1);

									adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);

								}
							}
						}
					}
				}
			}

			// update AddressInfoP
			AddressInfoP addressInfoPLocal = addressInfoP.merge();
			addressInfoPLocal.setAssociated(true);
			addressInfoPLocal.setInstanceId(instanceP.getInstanceId());
			addressInfoPLocal.setStatus(Commons.ipaddress_STATUS.associated + "");
			setAssetStartTime(addressInfoPLocal.getAsset());
			addressInfoPLocal = addressInfoPLocal.merge();
			// update teh associated instanceP object
			instanceP.setIpAddress(publicIp);
			instanceP.merge();

			accountLogService.saveLogAndSendMail("Completed : " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + addressInfoP.getName(), Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.FAIL.ordinal(), userId);
			/*try {
				AddressInfoP a = AddressInfoP.findAddressInfoP(addressInfoP.getId());
				a.setStatus(Commons.ipaddress_STATUS.failed + "");
				a.setAssociated(false);
				a = a.merge();
				setAssetEndTime(a.getAsset());
				InstanceP i = InstanceP.findInstancePsByInstanceIdEquals(a.getInstanceId()).getSingleResult();
				i.setIpAddress("");
				i.merge();
			} catch (Exception e2) {
				e2.printStackTrace();
			}*/
		}

	}// end of associateAddress

	@Async
	public void disassociateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			InstanceP instanceP = InstanceP.findInstancePsByInstanceIdEquals(addressInfoP.getInstanceId()).getSingleResult();
			String privateIp = instanceP.getPrivateIpAddress();
			String publicIp = addressInfoP.getPublicIp();

			VcloudClient vcloudClient = getVcloudClient(infra);
			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					AdminOrgNetwork adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);
					if (adminOrgNetwork.getResource().getConfiguration() != null) {
						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								if (jaxbElement.getValue() instanceof NatServiceType) {
									NatServiceType natService = (NatServiceType) jaxbElement.getValue();
									List<NatRuleType> natRules = natService.getNatRule();
									NatRuleType natRuleTypeToBeRemoved = null;
									breakHere: for (Iterator iterator = natRules.iterator(); iterator.hasNext();) {
										NatRuleType natRuleType = (NatRuleType) iterator.next();
										if (natRuleType.getPortForwardingRule() != null) {
											if (natRuleType.getPortForwardingRule().getExternalIpAddress().equals(publicIp)) {
												logger.info(" removing " + publicIp + " entry ");
												natRuleTypeToBeRemoved = natRuleType;
												break breakHere;
											}
										}
									}

									natService.getNatRule().remove(natRuleTypeToBeRemoved);
									adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);
								}
							}
						}
					}
				}
			}

			// update AddressInfoP
			AddressInfoP addressInfoPLocal = addressInfoP.merge();
			addressInfoPLocal.setAssociated(false);
			addressInfoPLocal.setInstanceId("");
			addressInfoPLocal.setStatus(Commons.ipaddress_STATUS.available + "");
			addressInfoPLocal.setInstanceId(Commons.ipaddress_STATUS.available + "");
			setAssetEndTime(addressInfoPLocal.getAsset());
			addressInfoPLocal = addressInfoPLocal.merge();
			// update teh associated instanceP object
			instanceP.setIpAddress("");
			instanceP.merge();

			accountLogService.saveLogAndSendMail("Completed : " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + addressInfoP.getName(), Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.FAIL.ordinal(), userId);
			try {
				AddressInfoP a = AddressInfoP.findAddressInfoP(addressInfoP.getId());
				a.setStatus(Commons.ipaddress_STATUS.failed + "");
				a.setAssociated(false);
				a = a.merge();
				setAssetEndTime(a.getAsset());
				InstanceP i = InstanceP.findInstancePsByInstanceIdEquals(a.getInstanceId()).getSingleResult();
				i.setIpAddress("");
				i.merge();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}// end of disassociateAddress

}
