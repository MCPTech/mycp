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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xerox.amazonws.ec2.AddressInfo;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

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
					}
				}
			}

			if (address_str.equals(newIpAddress)) {
				addressInfoPLocal.setInstanceId("available");
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
						if (ipToMatch.equals(addressInfo.getPublicIp()) && addressInfo.getInstanceId().equals("nobody")) {
							// euca logic
							addressInfoLocal = null;
							break;
						}

					}

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " still getting released; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
					addressInfoLocal = null;

					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}
				}
			}

			if (addressInfoLocal == null && addressInfoPLocal!=null) {
				setAssetEndTime(addressInfoPLocal.getAsset());
				accountLogService.saveLogAndSendMail("Completed : "
						+ this.getClass().getName()
						+ " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName()
								.subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
						Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

				addressInfoPLocal.remove();
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
			String instanceIdOrig = addressInfoP.getInstanceId();
			if (StringUtils.contains(instanceIdOrig, " ")) {
				instanceIdOrig = StringUtils.substringBefore(instanceIdOrig, " ");
			}

			InstanceP orig_compute = null;
			try {
				orig_compute = InstanceP.findInstancePsByInstanceIdEquals(instanceIdOrig).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			AddressInfoP addressInfoP4PublicIp = null;
			try {
				addressInfoP4PublicIp = AddressInfoP.findAddressInfoPsByPublicIpEquals(addressInfoP.getPublicIp()).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			AddressInfoP addressInfoP4InstanceId = null;
			try {
				addressInfoP4InstanceId = AddressInfoP.findAddressInfoPsByInstanceIdLike(instanceIdOrig).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			boolean match = false;
			int START_SLEEP_TIME = 5000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			outer: while (!match) {
				if (now > waitformaxcap) {
					throw new Exception("Got bored, Quitting.");
				}
				now = now + START_SLEEP_TIME;

				try {

					List<String> params = new ArrayList<String>();
					List<ReservationDescription> instances = ec2.describeInstances(params);
					for (ReservationDescription res : instances) {
						if (res.getInstances() != null) {
							HashSet<InstanceP> instancesP = new HashSet<InstanceP>();
							for (Instance inst : res.getInstances()) {
								logger.info(inst.getInstanceId() + " " + orig_compute.getInstanceId() + " " + inst.getDnsName()+ " " + inst.getIpAddress()  + " " + addressInfoP.getPublicIp());
								if (inst.getInstanceId().equals(orig_compute.getInstanceId()) && inst.getIpAddress().equals(addressInfoP.getPublicIp())) {
									match = true;
									break outer;
								}

							}// for (Instance inst : res.getInstances()) {
						}// if (res.getInstances() != null) {
					}// for (ReservationDescription res : instances) {

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " getting associated; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);

				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
					// addressInfoLocal=null;
					if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
						throw new Exception("No Connectivity to Cloud");
					}
				}
			}
			if (match == true) {
				try {
					orig_compute.setIpAddress(addressInfoP.getPublicIp());
					orig_compute.merge();
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}

				try {
					addressInfoP4PublicIp.setAssociated(true);
					addressInfoP4PublicIp.setInstanceId(orig_compute.getInstanceId());
					addressInfoP4PublicIp.setStatus(Commons.ipaddress_STATUS.associated + "");
					addressInfoP4PublicIp.merge();

				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}

				try {
					addressInfoP4InstanceId.setAssociated(false);
					addressInfoP4InstanceId.setInstanceId("somebody");
					addressInfoP4InstanceId.setStatus(Commons.ipaddress_STATUS.associated + "");
					addressInfoP4InstanceId.merge();
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}

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

	}// end of associateAddress

	@Async
	public void disassociateAddress(final Infra infra, final AddressInfoP addressInfoP, final String userId) {
		String threadName = Thread.currentThread().getName();

		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + addressInfoP.getName(),
					Commons.task_name.IPADDRESS.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			logger.debug("threadName " + threadName + " started for disassociateAddress");
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
			String instanceIdOrig = addressInfoP.getInstanceId();
			if (StringUtils.contains(instanceIdOrig, " ")) {
				instanceIdOrig = StringUtils.substringBefore(instanceIdOrig, " ");
			}

			InstanceP orig_compute = null;
			try {
				orig_compute = InstanceP.findInstancePsByInstanceIdEquals(instanceIdOrig).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			AddressInfoP addressInfoP4PublicIp = null;
			try {
				addressInfoP4PublicIp = AddressInfoP.findAddressInfoPsByPublicIpEquals(addressInfoP.getPublicIp()).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			String newIp = "";
			boolean match = false;
			int START_SLEEP_TIME = 5000;
			int waitformaxcap = START_SLEEP_TIME * 10;
			long now = 0;
			outer: while (!match) {
				if (now > waitformaxcap) {
					throw new Exception("Got bored, Quitting.");
				}
				now = now + START_SLEEP_TIME;

				try {

					List<String> params = new ArrayList<String>();
					List<ReservationDescription> instances = ec2.describeInstances(params);
					for (ReservationDescription res : instances) {
						if (res.getInstances() != null) {
							HashSet<InstanceP> instancesP = new HashSet<InstanceP>();
							for (Instance inst : res.getInstances()) {
								logger.info(inst.getInstanceId() + " " + orig_compute.getInstanceId() + " " + inst.getDnsName()+ " " + inst.getIpAddress() + " " + addressInfoP.getPublicIp());
								if (inst.getInstanceId().equals(orig_compute.getInstanceId()) && !inst.getIpAddress().equals(addressInfoP.getPublicIp())) {

									newIp = inst.getIpAddress();
									match = true;
									break outer;
								}

							}// for (Instance inst : res.getInstances()) {
						}// if (res.getInstances() != null) {
					}// for (ReservationDescription res : instances) {

					logger.info("Ipaddress " + addressInfoP.getPublicIp() + " getting disassociated; sleeping " + START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);

				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
					// addressInfoLocal=null;

				}
			}

			AddressInfoP addressInfoP4NewPublicIp = null;
			try {
				addressInfoP4NewPublicIp = AddressInfoP.findAddressInfoPsByPublicIpEquals(newIp).getSingleResult();
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			if (match == true) {
				try {
					orig_compute.setIpAddress(newIp);
					orig_compute.merge();
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}

				try {
					addressInfoP4PublicIp.setAssociated(false);
					addressInfoP4PublicIp.setInstanceId("available");
					addressInfoP4PublicIp.setStatus(Commons.ipaddress_STATUS.available + "");
					addressInfoP4PublicIp.merge();
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}

				try {
					addressInfoP4NewPublicIp.setAssociated(false);
					addressInfoP4NewPublicIp.setInstanceId(orig_compute.getInstanceId());
					addressInfoP4PublicIp.setStatus(Commons.ipaddress_STATUS.available + "");
					addressInfoP4NewPublicIp.merge();
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}
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

	}// enf disassociateAddress

}
