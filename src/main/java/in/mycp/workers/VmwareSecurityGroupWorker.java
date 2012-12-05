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

import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.Infra;
import in.mycp.domain.IpPermissionP;
import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vmware.vcloud.api.rest.schema.FirewallRuleProtocols;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrgNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.xerox.amazonws.ec2.Jec2;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("vmwareSecurityGroupWorker")
public class VmwareSecurityGroupWorker extends Worker {

	@Autowired
	AccountLogService accountLogService;

	protected static Logger logger = Logger.getLogger(VmwareSecurityGroupWorker.class);

	@Async
	public void createOrUpdateFirewallRules(final Infra infra, final GroupDescriptionP securityGroup, final String userId) {
		GroupDescriptionP securityGroupLocal = null;
		ArrayList<FirewallRuleType> firewallRulesInCloud =null;
		FirewallServiceType firewallService = null;
		AdminOrgNetwork adminOrgNetwork = null;
		try {
			securityGroupLocal = GroupDescriptionP.findGroupDescriptionP(securityGroup.getId());

			VcloudClient vcloudClient = getVcloudClient(infra);
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + securityGroup.getName(),
					Commons.task_name.SECURITYGROUP.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			for (ReferenceType adminOrgRef : vcloudClient.getVcloudAdmin().getAdminOrgRefs()) {
				AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(vcloudClient, adminOrgRef);
				for (ReferenceType adminOrgNetworkRef : adminOrg.getAdminOrgNetworkRefs()) {
					adminOrgNetwork = AdminOrgNetwork.getOrgNetworkByReference(vcloudClient, adminOrgNetworkRef);

					if (securityGroupLocal.getDescripton() != null && securityGroupLocal.getDescripton().equals(adminOrgNetwork.getReference().getHref())
							&& adminOrgNetwork.getResource().getConfiguration() != null) {

						firewallRulesInCloud = new ArrayList<FirewallRuleType>();
						

						if (adminOrgNetwork.getResource().getConfiguration().getFeatures() != null) {
							for (JAXBElement<? extends NetworkServiceType> jaxbElement : adminOrgNetwork.getResource().getConfiguration().getFeatures()
									.getNetworkService()) {
								if (jaxbElement.getValue() instanceof FirewallServiceType) {
									firewallService = (FirewallServiceType) jaxbElement.getValue();

									// first, loop throw the firewall rules on cloud and keep it aside for reference.
									// then , remove all firewall rules in the cloud
									// last, start creating firewall rules as per MYCP database.

									for (FirewallRuleType firewallRule : firewallService.getFirewallRule()) {
										firewallRulesInCloud.add(firewallRule);
									}

									// removing all firewall rules from cloud
									firewallService.getFirewallRule().addAll(new ArrayList<FirewallRuleType>());
									adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);
									logger.info("cleaned up all firewallrules in " + infra.getName());

									// now, loop thro the ippermissions in MYCP
									// and recreate those as firwallrules in
									// cloud
									Set<IpPermissionP> ipPermissions = securityGroupLocal.getIpPermissionPs();
									for (Iterator iterator = ipPermissions.iterator(); iterator.hasNext();) {
										IpPermissionP ipPermissionP = (IpPermissionP) iterator.next();

										FirewallRuleType frt = new FirewallRuleType();
										frt.setIsEnabled(ipPermissionP.getVcloudEnabled());
										frt.setDirection(ipPermissionP.getVcloudDirection());
										//this is name
										frt.setDescription(ipPermissionP.getVcloudName());
										frt.setDestinationIp(ipPermissionP.getVcloudDestinationIp());
										frt.setSourceIp(ipPermissionP.getVcloudSourceIp());
										frt.setPort(ipPermissionP.getVcloudDestinationPort());
										frt.setSourcePort(ipPermissionP.getVcloudSourcePort());
										FirewallRuleProtocols frp = new FirewallRuleProtocols();
											frp.setAny(getFirewallRuleProtocols(ipPermissionP.getProtocol()).isAny());
											frp.setIcmp(getFirewallRuleProtocols(ipPermissionP.getProtocol()).isIcmp());
											frp.setTcp(getFirewallRuleProtocols(ipPermissionP.getProtocol()).isTcp());
											frp.setUdp(getFirewallRuleProtocols(ipPermissionP.getProtocol()).isUdp());
										frt.setProtocols(frp);
										firewallService.getFirewallRule().add(frt);
									}

									adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);
								}

							}
						}
					}
				}
			}

			accountLogService.saveLogAndSendMail("Complete : " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + securityGroup.getName(), Commons.task_name.SECURITYGROUP.name(), Commons.task_status.SUCCESS.ordinal(), userId);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();

			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + securityGroup.getName() + ", " + e.getMessage(),
					Commons.task_name.SECURITYGROUP.name(), Commons.task_status.FAIL.ordinal(), userId);
			
			try {
				//first , remove all 
					try {
						firewallService.getFirewallRule().addAll(new ArrayList<FirewallRuleType>());
						adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
					// second, restore it back to original state
				if(firewallRulesInCloud != null && firewallService != null && adminOrgNetwork != null){
					for (Iterator iterator = firewallRulesInCloud.iterator(); iterator.hasNext(); ) {
						FirewallRuleType firewallRuleType = (FirewallRuleType) iterator.next();
						firewallService.getFirewallRule().add(firewallRuleType);
					}
					adminOrgNetwork.updateOrgNetwork(adminOrgNetwork.getResource()).waitForTask(-1);
				}//if
			} catch (Exception e2) {
				e2.printStackTrace();
		}
	}
	}
}

	

