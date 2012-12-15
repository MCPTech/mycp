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
package in.mycp.workers;

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.remote.AccountLogService;
import in.mycp.remote.ReportService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xerox.amazonws.ec2.AddressInfo;
import com.xerox.amazonws.ec2.Jec2;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("ipAddressWorker")
public class IpAddressWorker extends Worker {

	@Autowired
	AccountLogService accountLogService;

	@Autowired
	ReportService reportService;
	
	protected static Logger logger = Logger.getLogger(IpAddressWorker.class);

	@Async
	public void allocateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		try {
			Jec2 ec2 = getNewJce2(infra);
			String newIpAddress = null;

			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);
			try {
				newIpAddress = ec2.allocateAddress();
				logger.info("got new Address " + newIpAddress);
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
				if (e.getMessage().indexOf("Permission denied while") > -1) {
					throw new Exception("Permission denied while trying to get address");
				} else if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
					throw new Exception("No Connectivity to Cloud");
				}
			}

			AddressInfoP addressInfoPLocal = null;
			try {
				addressInfoPLocal = AddressInfoP.findAddressInfoP(addressInfoP.getId());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			String address_str = null;

			int START_SLEEP_TIME = 10000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			while (address_str == null) {
				if (now > waitformaxcap) {
					throw new Exception("Got bored, Quitting.");
				}
				now = now + START_SLEEP_TIME;
				logger.info("Ipaddress " + newIpAddress + " still getting created; sleeping " + START_SLEEP_TIME + "ms");
				Thread.sleep(START_SLEEP_TIME);
				try {
					// address_str =
					// ec2.describeAddresses(Collections.singletonList(newIpAddress)).get(0).getPublicIp();
					List<AddressInfo> adrsses = ec2.describeAddresses(new ArrayList<String>());
					for (Iterator iterator = adrsses.iterator(); iterator.hasNext();) {
						AddressInfo addressInfo = (AddressInfo) iterator.next();
						if (newIpAddress.equals(addressInfo.getPublicIp()) && addressInfo.getInstanceId().startsWith("available")) {
							// euca logic
							address_str = addressInfo.getPublicIp();
							break;
						}

					}
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
					address_str = e.getMessage();
					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}else if (e.getMessage().indexOf("Got bored, Quitting") > -1) {
						throw new Exception("Got bored, Quitting");
					}
				}
			}
			
			//if there are any AddressInfoP objevcts hanging around in the MYCP DB for the same publicIP, make it inactive.
			List<AddressInfoP> addressInfoPsWithSameIP = AddressInfoP.findAddressInfoPsBy(infra, address_str).getResultList();
			for (Iterator iterator = addressInfoPsWithSameIP.iterator(); iterator.hasNext(); ) {
				AddressInfoP addressInfoP2 = (AddressInfoP) iterator.next();
				setAssetEndTime(addressInfoP2.getAsset());
			}
			
			
			
			if (address_str.equals(newIpAddress)) {
				addressInfoPLocal.setInstanceId("");
				addressInfoPLocal.setPublicIp(newIpAddress);
				addressInfoPLocal.setStatus(Commons.ipaddress_STATUS.available + "");
				addressInfoPLocal = addressInfoPLocal.merge();

				setAssetStartTime(addressInfoPLocal.getAsset());

				accountLogService.saveLogAndSendMail("Completed : "
						+ this.getClass().getName()
						+ " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName()
								.subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
						Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			}

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

			Jec2 ec2 = getNewJce2(infra);
			String ipToMatch = addressInfoP.getPublicIp();
			try {
				logger.info("releasing address " + addressInfoP.getPublicIp());
				ec2.releaseAddress(addressInfoP.getPublicIp());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
				if (e.getMessage().indexOf("Permission denied while trying to release address") > -1) {
					throw new Exception("Permission denied while trying to release address");
				} else if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
					throw new Exception("No Connectivity to Cloud");
				}
			}

			AddressInfoP addressInfoPLocal = null;
			try {
				addressInfoPLocal = AddressInfoP.findAddressInfoP(addressInfoP.getId());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}
			AddressInfo addressInfoLocal = new AddressInfo("", "");

			int START_SLEEP_TIME = 10000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			while (addressInfoLocal != null) {
				if (now > waitformaxcap) {
					throw new Exception("Got bored, Quitting.");
				}
				now = now + START_SLEEP_TIME;
				try {
					List<AddressInfo> adrsses = ec2.describeAddresses(new ArrayList<String>());
					for (Iterator iterator = adrsses.iterator(); iterator.hasNext();) {
						AddressInfo addressInfo = (AddressInfo) iterator.next();
						if (ipToMatch.equals(addressInfo.getPublicIp()) && addressInfo.getInstanceId().startsWith("nobody")) {
							// euca logic
							addressInfoLocal = null;
							break;
						}

					}

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " still getting released; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
					// addressInfoLocal = null;

					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}else if (e.getMessage().indexOf("Got bored, Quitting") > -1) {
						throw new Exception("Got bored, Quitting");
					}
				}
			}

			if (addressInfoLocal == null && addressInfoPLocal != null) {
				addressInfoPLocal.setStatus(Commons.ipaddress_STATUS.nobody + "");
				addressInfoPLocal.merge();
				setAssetEndTime(addressInfoPLocal.getAsset());
				accountLogService.saveLogAndSendMail("Completed : "
						+ this.getClass().getName()
						+ " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName()
								.subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
						Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

				// addressInfoPLocal.remove();

			} else {
				addressInfoPLocal.setStatus(Commons.ipaddress_STATUS.failed + "");
				addressInfoPLocal.setInstanceId("");
				addressInfoPLocal.merge();
				setAssetEndTime(addressInfoPLocal.getAsset());
			}

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
				a.setInstanceId("");
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

			Jec2 ec2 = getNewJce2(infra);
			// get the new addressInfoP and update it with the new instanceId
			// get the new addressInfoP and update it with status "from cloud"
			// get the old addressinfoP and update the instanceId with ""
			// get the old addressinfoP and update the status with "from cloud"
			// get the old instanceP and update the new ipaddress

			AddressInfoP addressInfoPNew = AddressInfoP.findAddressInfoP(addressInfoP.getId());
			InstanceP instancePNew = InstanceP.findInstancePsByInstanceIdEquals(addressInfoP.getInstanceId()).getSingleResult();
			List<AddressInfoP> addressInfoPOlds = null;
			
			if(instancePNew.getIpAddress()!=null && instancePNew.getIpAddress().length()>0){
				try {
					 addressInfoPOlds = AddressInfoP.findAddressInfoPsByPublicIpEquals(instancePNew.getIpAddress()).getResultList();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/*System.out.println("addressInfoPOld.getPublicIp() = "+addressInfoPOld.getPublicIp()+" "+addressInfoPOld.getInstanceId());
			System.out.println("addressInfoPNew.getPublicIp() = "+addressInfoPNew.getPublicIp()+" "+addressInfoPNew.getInstanceId());
			System.out.println("instancePNew.getIpAddress() = "+instancePNew.getIpAddress()+" "+instancePNew.getInstanceId());
			*/
			// here do the ec2.associate
			try {
				logger.info("associateAddress address " + addressInfoP.getPublicIp() + " to instance " + addressInfoP.getInstanceId());
				ec2.associateAddress(addressInfoP.getInstanceId(), addressInfoP.getPublicIp());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
				if (e.getMessage().indexOf("Permission denied while") > -1) {
					throw new Exception("Permission denied.");
				} else if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
					throw new Exception("No Connectivity to Cloud");
				}
			}
			boolean match = false;
			int START_SLEEP_TIME = 5000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			outer: while (!match) {
				try {
					if (now > waitformaxcap) {
						throw new Exception("Got bored, Quitting.");
					}
					now = now + START_SLEEP_TIME;
					// wait till teh job is done.
					List<AddressInfo> adrsses = ec2.describeAddresses(new ArrayList<String>());
					for (Iterator iterator = adrsses.iterator(); iterator.hasNext();) {
						AddressInfo addressInfo = (AddressInfo) iterator.next();
						
						System.out.println("addressInfoPNew.getPublicIp().equals(addressInfo.getPublicIp() = "+addressInfoPNew.getPublicIp()+" "+addressInfo.getPublicIp());
						
						if (addressInfoPNew.getPublicIp().equals(addressInfo.getPublicIp())) {
							// euca logic
							if (addressInfo.getInstanceId().startsWith(addressInfoPNew.getInstanceId())) {
								match = true;
								break outer;

							}
						}
					}

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " getting associated; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}else if (e.getMessage().indexOf("Got bored, Quitting") > -1) {
						throw new Exception("Got bored, Quitting");
					}
				}
			}

			// now loop throgh the ip addreses in cloud
			// and get the association details
			// then get the status of oldIp.

			List<AddressInfo> adrsses = ec2.describeAddresses(new ArrayList<String>());
			for (Iterator iterator = adrsses.iterator(); iterator.hasNext();) {
				AddressInfo addressInfo = (AddressInfo) iterator.next();
				//System.out.println("addressInfoPNew.getPublicIp().equals(addressInfo.getPublicIp() = "+addressInfoPNew.getPublicIp()+" "+addressInfo.getPublicIp());
				if (addressInfoPNew.getPublicIp().equals(addressInfo.getPublicIp())) {
					// euca logic
					//System.out.println(" addressInfo.getInstanceId().startsWith(addressInfoPNew.getInstanceId()) "+addressInfo.getInstanceId()+" "+addressInfoPNew.getInstanceId());
					if (addressInfo.getInstanceId().startsWith(addressInfoPNew.getInstanceId())) {
						// now the association is successfull
						// save this into MYCP.
						addressInfoPNew.setInstanceId(addressInfoP.getInstanceId());
						addressInfoPNew.setStatus(Commons.ipaddress_STATUS.associated + "");
						addressInfoPNew.merge();
						instancePNew.setIpAddress(addressInfoPNew.getPublicIp());
						instancePNew.setDnsName(addressInfoPNew.getPublicIp());
						instancePNew.merge();
						setAssetStartTime(instancePNew.getAsset());
					} 
				}
				
				//System.out.println("addressInfoPOld.getPublicIp().equals(addressInfo.getPublicIp()) = "+addressInfoPOld.getPublicIp()+" "+addressInfo.getPublicIp());
				for (Iterator iterator2 = addressInfoPOlds.iterator(); iterator2.hasNext(); ) {
					AddressInfoP addressInfoP2 = (AddressInfoP) iterator2.next();
					if (addressInfoP2.getPublicIp().equals(addressInfo.getPublicIp())) {
						// test whether the oldIp has been disassociated
						
						addressInfoP2.setStatus(Commons.ipaddress_STATUS.nobody + "");
						addressInfoP2.merge();
						setAssetEndTime(addressInfoP2.getAsset());
					}	
				}
				
				
			}

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
				a = a.merge();
				setAssetEndTime(a.getAsset());
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}// end of associateAddress

	@Async
	public void disassociateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		

		// get the old addressInfoP and update it with the instanceId=""
		// get the old addressInfoP and update it with status -="from cloud"
		// loop through the ec2 addreses
		// find the addressInfo for that old instanceId
		// find the addressInfoP for that addressInfo
		// and update the new addressInfoP with instanceId=new insatnceID
		// and update the new addressInfoP with status associated.
		// get the old instanceP and update the new ipaddress

		try {
			AddressInfoP addressInfoPOld = AddressInfoP.findAddressInfoP(addressInfoP.getId());
			String instanceId = getExactInstanceId(addressInfoPOld.getInstanceId()); 
			InstanceP instanceP = null;
			String productCatId = addressInfoP.getAsset().getProductCatalog().getId()+"";
			try {
				instanceP = InstanceP.findInstancePsByInstanceIdEquals(instanceId).getSingleResult();	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			// AddressInfoP addressInfoNew =
			// AddressInfoP.findAddressInfoPsByPublicIpEquals(instancePNew.getIpAddress()).getSingleResult();
			System.out.println("addressInfoPOld.getPublicIp() = "+addressInfoPOld.getPublicIp()+" "+addressInfoPOld.getInstanceId());
			System.out.println("instanceP.getIpAddress() = "+instanceP.getIpAddress()+" "+instanceP.getInstanceId());
			
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			
			Jec2 ec2 = getNewJce2(infra);

			try {
				ec2.disassociateAddress(addressInfoP.getPublicIp());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
				if (e.getMessage().indexOf("Permission denied while") > -1) {
					throw new Exception("Permission denied.");
				} else if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
					throw new Exception("No Connectivity to Cloud");
				}
			}
			
			//loop and find out if the disassocitaion job is complete 
			int START_SLEEP_TIME = 5000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			boolean done=false;
			outer: while (!done) {
				try {
					if (now > waitformaxcap) {
						throw new Exception("Got bored, Quitting.");
					}
					now = now + START_SLEEP_TIME;
			List<AddressInfo> adrsses1 = ec2.describeAddresses(new ArrayList<String>());
				for (Iterator iterator = adrsses1.iterator(); iterator.hasNext(); ) {
					AddressInfo addressInfo = (AddressInfo) iterator.next();
					if (addressInfoPOld.getPublicIp().equals(addressInfo.getPublicIp())) {
						// if teh address is already released/disassociated
						if (addressInfo.getInstanceId().startsWith(Commons.ipaddress_STATUS.nobody + "")
								|| addressInfo.getInstanceId().startsWith(Commons.ipaddress_STATUS.available + "")) {
							done = true;
							break outer;
						}
					}
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//end of looping and finding out if the disassociation job is complete 

			//now , start looping again to fin dthe status old IP and the new ip (which the instance got automatically reassigned to)
			boolean match = false;
			boolean found = false;
			START_SLEEP_TIME = 5000;
			waitformaxcap = START_SLEEP_TIME * 10;
			now = 0;
			outer: while (!match && !found) {
				try {
					if (now > waitformaxcap) {
						throw new Exception("Got bored, Quitting.");
					}
					now = now + START_SLEEP_TIME;
					// wait till teh job is done.
					List<AddressInfo> adrsses = ec2.describeAddresses(new ArrayList<String>());
					for (Iterator iterator = adrsses.iterator(); iterator.hasNext();) {
						AddressInfo addressInfo = (AddressInfo) iterator.next();
						//logger.info("addressInfoPOld.getPublicIp().equals(addressInfo.getPublicIp() = "+addressInfoPOld.getPublicIp()+" "+addressInfo.getPublicIp());
						//now, if you get the OLD ip , remove its association with the instance and make it available
						if (addressInfoPOld.getPublicIp().equals(addressInfo.getPublicIp())) {
							// if teh address is already released/disassociated, it will be in teh status available
							if (addressInfo.getInstanceId().startsWith(Commons.ipaddress_STATUS.available + "")) {
								match = true;
								addressInfoPOld.setStatus(Commons.ipaddress_STATUS.available + "");
								//setAssetEndTime(addressInfoPOld.getAsset());
								addressInfoPOld.setInstanceId("");
								addressInfoPOld.setReason("");
								addressInfoPOld.merge();

							}

							continue;
						} 
						
						
						//logger.info("associating back into the old/automatic IP = "+addressInfo.getInstanceId()+" "+addressInfoPOld.getInstanceId());
						
						// if some other automatic Ip is assigned ot the old instance, get that IP and store it in MYCP DB.
						
						if (addressInfo.getInstanceId().contains(instanceId)) {
							// now we get the new Ip address assigned
							found = true;

							// find the addressInfoP for that addressInfo
							// and update the new addressInfoP with
							// instanceId=new insatnceID
							// and update the new addressInfoP with status
							// associated.
							// get the old instanceP and update the new
							// ipaddress

							AddressInfoP addressInfoPNew = new AddressInfoP();//.findAddressInfoPsByPublicIpEquals(addressInfo.getPublicIp()).getSingleResult();
							addressInfoPNew.setInstanceId(addressInfo.getInstanceId());
							addressInfoPNew.setPublicIp(addressInfo.getPublicIp());
							
							addressInfoPNew.setStatus(Commons.ipaddress_STATUS.auto_assigned+ "");
							addressInfoPNew.setReason("auto assigned Ip after disassociation");
							
								AssetType assetType = AssetType.findAssetTypesByNameEquals(
										"" + Commons.ASSET_TYPE.IpAddress).getSingleResult();
								Company company = addressInfoPOld.getAsset().getUser().getDepartment().getCompany();
								Asset asset = Commons.getNewAsset4Worker(assetType, addressInfoPOld.getAsset().getUser() ,productCatId , reportService,company);
								asset.setProject(Project.findProject(addressInfoPOld.getProjectId()));
								addressInfoPNew.setAsset(asset);
							setAssetStartTime(addressInfoPNew.getAsset());
							addressInfoPNew.merge();

							instanceP.setIpAddress(addressInfoPNew.getPublicIp());
							instanceP.setDnsName(addressInfoPNew.getPublicIp());
							instanceP.merge();
							//after disaccosition , the asset still belongs to the user, cannot set the end time
							//setAssetEndTime(addressInfoPOld.getAsset());
						}
					}

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " getting disassociated; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}else if (e.getMessage().indexOf("Got bored, Quitting") > -1) {
						throw new Exception("Got bored, Quitting");
					}
				}
			}

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
			
			//lets leave the asset and mycp DB which ever state they are in. 
			/*try {
				AddressInfoP a = AddressInfoP.findAddressInfoP(addressInfoP.getId());
				a.setStatus(Commons.ipaddress_STATUS.failed + "");
				a = a.merge();
				setAssetEndTime(a.getAsset());
			} catch (Exception e2) {
				// TODO: handle exception
			}*/
		}

	}// enf disassociateAddress

}
