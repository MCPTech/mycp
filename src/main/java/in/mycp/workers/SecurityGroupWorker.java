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
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xerox.amazonws.ec2.GroupDescription;
import com.xerox.amazonws.ec2.Jec2;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@Component("securityGroupWorker")
public class SecurityGroupWorker extends Worker {

	protected static Logger logger = Logger.getLogger(SecurityGroupWorker.class);

	@Async
	public void createSecurityGroup(final Infra infra, final GroupDescriptionP securityGroup) {
		try {
			Jec2 ec2 = getNewJce2(infra);
			GroupDescriptionP groupLocal = GroupDescriptionP.findGroupDescriptionP(securityGroup.getId());
			try {
				ec2.createSecurityGroup(groupLocal.getName(), groupLocal.getDescripton());
			} catch (Exception e) {
				logger.error(e);//e.printStackTrace();
				if(e.getMessage().indexOf("Number of retries exceeded") > -1){
					throw new Exception("No Connectivity to Cloud");
				}
			}

			List<GroupDescription> groupDescs = ec2.describeSecurityGroups(new ArrayList<String>());
			int START_SLEEP_TIME = 5000;
			long timeout = START_SLEEP_TIME *100;
			long runDuration=0;
			boolean created = false;
			outer: while (true) {
				logger.info("Security group " + securityGroup.getId() + " being created.");
				Thread.sleep(START_SLEEP_TIME);
				runDuration = runDuration+START_SLEEP_TIME;
				if(runDuration > timeout){
					logger.info("Tried enough.Am bored, quitting.");
					break outer;
				}
				
				for (Iterator iterator = groupDescs.iterator(); iterator.hasNext();) {
					GroupDescription groupDescription = (GroupDescription) iterator.next();
					logger.info(groupDescription);
					if (groupLocal.getName().equals(groupDescription.getName())) {
						logger.info("Security group " + groupDescription.getName() + " created.");
						
						created = true;
						break outer;
					}
				}
			}
			if(created){
				groupLocal.setStatus(Commons.secgroup_STATUS.active+"");
				groupLocal = groupLocal.merge();
				List<IpPermissionP> localPerms = IpPermissionP.findIpPermissionPsByGroupDescription(groupLocal).getResultList();
				
				for (Iterator iterator = localPerms.iterator(); iterator.hasNext();) {
					IpPermissionP ipPermissionP = (IpPermissionP) iterator.next();
					logger.info("Creating IpPermission Ingress for " + ipPermissionP.getProtocol()+" "+ipPermissionP.getFromPort()+" "+ipPermissionP.getToPort());
					try {
						authorizeSecurityGroupIngress(infra, ipPermissionP);	
					} catch (Exception e) {
						logger.error(e);
					}
				}
				
				setAssetStartTime(groupLocal.getAsset());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());e.printStackTrace();
		}
	}

	@Async
	public void authorizeSecurityGroupIngress(final Infra infra, final IpPermissionP ipPermissionP) {
		try {
			Jec2 ec2 = getNewJce2(infra);

			ec2.authorizeSecurityGroupIngress(ipPermissionP.getGroupDescription().getName(), ipPermissionP.getProtocol(),
					ipPermissionP.getFromPort(), ipPermissionP.getToPort(), ipPermissionP.getCidrIps());
		} catch (Exception e) {
			logger.error(e.getMessage());e.printStackTrace();
		}
	}

	@Async
	public void revokeSecurityGroupIngress(final Infra infra, final IpPermissionP ipPermissionP) {
		try {
			Jec2 ec2 = getNewJce2(infra);
			try {
				ec2.revokeSecurityGroupIngress(ipPermissionP.getGroupDescription().getName(), ipPermissionP.getProtocol(),
						ipPermissionP.getFromPort(), ipPermissionP.getToPort(), ipPermissionP.getCidrIps());
			} catch (Exception e) {
				logger.error(e.getMessage());//e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());//e.printStackTrace();
		}
	}

	@Async
	public void deleteSecurityGroup(final Infra infra, final GroupDescriptionP securityGroup) {
		try {
			Jec2 ec2 = getNewJce2(infra);
			ec2.deleteSecurityGroup(securityGroup.getName());
			setAssetEndTime(GroupDescriptionP.findGroupDescriptionP(securityGroup.getId()).getAsset());
		} catch (Exception e) {
			logger.error(e.getMessage());//e.printStackTrace();
		}
	}

}