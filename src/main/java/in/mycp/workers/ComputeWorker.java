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
import in.mycp.domain.AssetType;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.ProductCatalog;
import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("computeWorker")
public class ComputeWorker extends Worker {
	@Autowired
	AccountLogService accountLogService;

	protected static Logger logger = Logger.getLogger(ComputeWorker.class);

	@Async
	public void restartCompute(final Infra infra, final int instancePId,
			final String userId) {
		try {

			logger.info("restartCompute " + infra.getCompany().getName()
					+ " instance : " + instancePId);
			accountLogService.saveLog(
					"Started: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for " + InstanceP.findInstanceP(instancePId).getName(),
					Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), userId);
			Jec2 ec2 = getNewJce2(infra);
			List<String> instanceIds = new ArrayList<String>();
			instanceIds.add(InstanceP.findInstanceP(instancePId)
					.getInstanceId());

			ec2.rebootInstances(instanceIds);

			for (Iterator iterator = instanceIds.iterator(); iterator.hasNext();) {
				String instanceId = (String) iterator.next();

				int SLEEP_TIME = 5000;
				int preparationSleepTime = 0;
				ReservationDescription reservationDescription = ec2
						.describeInstances(
								Collections.singletonList(instanceId)).get(0);
				Instance instanceEc2 = reservationDescription.getInstances()
						.get(0);
				InstanceP instanceP = InstanceP
						.findInstancePsByInstanceIdEquals(instanceId)
						.getSingleResult();

				while (!instanceEc2.isRunning() && !instanceEc2.isTerminated()) {
					try {
						instanceP.setState(Commons.REQUEST_STATUS.RESTARTING
								+ "");
						instanceP.merge();
						logger.info("Instance " + instanceEc2.getInstanceId()
								+ " still rebooting; sleeping " + SLEEP_TIME
								+ "ms");
						Thread.sleep(SLEEP_TIME);
						reservationDescription = ec2.describeInstances(
								Collections.singletonList(instanceEc2
										.getInstanceId())).get(0);
						instanceEc2 = reservationDescription.getInstances()
								.get(0);
					} catch (Exception e) {
						logger.error(e.getMessage());
						// e.printStackTrace();
					}
				}
				logger.info("out of while loop, instanceEc2.isRunning() "
						+ instanceEc2.isRunning());

				if (instanceEc2.isRunning()) {
					logger.info("EC2 instance is now running");
					if (preparationSleepTime > 0) {
						logger.info("Sleeping "
								+ preparationSleepTime
								+ "ms allowing instance services to start up properly.");
						Thread.sleep(preparationSleepTime);
						logger.info("Instance prepared - proceeding");
					}
					instanceP.setState(Commons.REQUEST_STATUS.running + "");
					instanceP.merge();
					accountLogService.saveLogAndSendMail(
							"Complete: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.SUCCESS.ordinal(), userId);
				} else {
					instanceP.setState(Commons.REQUEST_STATUS.FAILED + "");
					setAssetEndTime(instanceP.getAsset());
					instanceP.merge();
					accountLogService.saveLogAndSendMail(
							"Error in: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());// e.printStackTrace();
			accountLogService
					.saveLog(
							"Error in "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instancePId + ", " + e.getMessage(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);
		}

	}// end rebootInstances

	@Async
	public void startCompute(final Infra infra, final int instancePId,
			final String userId) {
		InstanceP instanceP = null;
		try {
			logger.info("startCompute " + infra.getCompany().getName()
					+ " instance : " + instancePId);
			accountLogService.saveLog(
					"Started: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for " + InstanceP.findInstanceP(instancePId).getName(),
					Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), userId);
			Jec2 ec2 = getNewJce2(infra);
			List<String> instanceIds = new ArrayList<String>();
			instanceIds.add(InstanceP.findInstanceP(instancePId)
					.getInstanceId());

			ec2.startInstances(instanceIds);

			for (Iterator iterator = instanceIds.iterator(); iterator.hasNext();) {
				String instanceId = (String) iterator.next();

				int SLEEP_TIME = 5000;
				int preparationSleepTime = 0;
				ReservationDescription reservationDescription = ec2
						.describeInstances(
								Collections.singletonList(instanceId)).get(0);
				Instance instanceEc2 = reservationDescription.getInstances()
						.get(0);
				instanceP = InstanceP.findInstancePsByInstanceIdEquals(
						instanceId).getSingleResult();

				int INSTANCE_START_SLEEP_TIME = 5000;
				long timeout = INSTANCE_START_SLEEP_TIME * 100;
				long runDuration = 0;
				while (!instanceEc2.isRunning() && !instanceEc2.isTerminated()) {
					runDuration = runDuration + INSTANCE_START_SLEEP_TIME;
					if (runDuration > timeout) {
						logger.info("Tried enough.Am bored, quitting.");
						break;
					}

					try {
						instanceP
								.setState(Commons.REQUEST_STATUS.STARTING + "");
						instanceP.merge();
						logger.info("Instance " + instanceEc2.getInstanceId()
								+ " still booting; sleeping " + SLEEP_TIME
								+ "ms");
						Thread.sleep(SLEEP_TIME);
						reservationDescription = ec2.describeInstances(
								Collections.singletonList(instanceEc2
										.getInstanceId())).get(0);
						instanceEc2 = reservationDescription.getInstances()
								.get(0);
					} catch (Exception e) {
						logger.error(e.getMessage());
						// e.printStackTrace();
					}
				}
				logger.info("out of while loop, instanceEc2.isRunning() "
						+ instanceEc2.isRunning());

				if (instanceEc2.isRunning()) {
					logger.info("EC2 instance is now running");
					if (preparationSleepTime > 0) {
						logger.info("Sleeping "
								+ preparationSleepTime
								+ "ms allowing instance services to start up properly.");
						Thread.sleep(preparationSleepTime);
						logger.info("Instance prepared - proceeding");
					}
					instanceP.setState(Commons.REQUEST_STATUS.running + "");
					instanceP.merge();

					accountLogService.saveLogAndSendMail(
							"Complete: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.SUCCESS.ordinal(), userId);

				} else {
					instanceP.setState(Commons.REQUEST_STATUS.FAILED + "");
					setAssetEndTime(instanceP.getAsset());
					instanceP.merge();
					accountLogService.saveLogAndSendMail(
							"Error in "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ InstanceP.findInstanceP(instancePId).getName(), Commons.task_name.COMPUTE
									.name(),
							Commons.task_status.FAIL.ordinal(), userId);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());// e.printStackTrace();

			accountLogService
					.saveLog(
							"Error in "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instancePId + ", " + e.getMessage(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);
			try {
				InstanceP instance1 = InstanceP.findInstanceP(instancePId);
				instance1.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instance1.getAsset());
				instance1.merge();
			} catch (Exception e2) {
				logger.error(e);
				e.printStackTrace();
			}

		}

	}// end startInstances

	@Async
	public void stopCompute(final Infra infra, final int instancePId,
			final String userId) {
		InstanceP instanceP = null;
		try {

			logger.info("stopCompute " + infra.getCompany().getName()
					+ " instance: " + instancePId);
			accountLogService.saveLog(
					"Started: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for " + InstanceP.findInstanceP(instancePId).getName(),
					Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), userId);
			Jec2 ec2 = getNewJce2(infra);
			List<String> instanceIds = new ArrayList<String>();
			instanceIds.add(InstanceP.findInstanceP(instancePId)
					.getInstanceId());
			ec2.stopInstances(instanceIds, true);

			for (Iterator iterator = instanceIds.iterator(); iterator.hasNext();) {
				String instanceId = (String) iterator.next();

				int SLEEP_TIME = 10000;
				int preparationSleepTime = 0;
				ReservationDescription reservationDescription = ec2
						.describeInstances(
								Collections.singletonList(instanceId)).get(0);
				Instance instanceEc2 = reservationDescription.getInstances()
						.get(0);
				instanceP = InstanceP.findInstancePsByInstanceIdEquals(
						instanceId).getSingleResult();
				int INSTANCE_START_SLEEP_TIME = 5000;
				long timeout = INSTANCE_START_SLEEP_TIME * 100;
				long runDuration = 0;

				while (instanceEc2.isRunning() || instanceEc2.isShuttingDown()) {
					runDuration = runDuration + INSTANCE_START_SLEEP_TIME;
					if (runDuration > timeout) {
						logger.info("Tried enough.Am bored, quitting, is this not and EBS backed instance?");
						break;
					}
					try {
						instanceP.setState(Commons.REQUEST_STATUS.STOPPING
								+ "");
						instanceP.merge();
						logger.info("Instance " + instanceEc2.getInstanceId()
								+ " still shutting down; sleeping "
								+ SLEEP_TIME + "ms");
						Thread.sleep(SLEEP_TIME);
						reservationDescription = ec2.describeInstances(
								Collections.singletonList(instanceEc2
										.getInstanceId())).get(0);
						instanceEc2 = reservationDescription.getInstances()
								.get(0);
					} catch (Exception e) {
						logger.error(e.getMessage());
						// e.printStackTrace();
					}
				}
				logger.info("out of while loop, instanceEc2.isRunning() "
						+ instanceEc2.isRunning());

				if (!instanceEc2.isRunning()) {
					logger.info("EC2 instance is now stopped");
					instanceP.setState(Commons.REQUEST_STATUS.STOPPED + "");
					instanceP.merge();
					accountLogService.saveLogAndSendMail(
							"Complete: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.SUCCESS.ordinal(), userId);
				}else{
					logger.info("EC2 instance is still running");
					instanceP.setState(Commons.REQUEST_STATUS.running + "");
					instanceP.merge();
					instanceP.getAsset().setEndTime(null);
					accountLogService.saveLogAndSendMail(
							"Complete: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());// e.printStackTrace();

			accountLogService
					.saveLog(
							"Error in "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instancePId + ", " + e.getMessage(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);
			try {
				InstanceP instance1 = InstanceP.findInstanceP(instancePId);
				instance1.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instance1.getAsset());
				instance1.merge();
			} catch (Exception e2) {
				logger.error(e);
				e.printStackTrace();
			}
		}

	}// end stopInstances

	@Async
	public void terminateCompute(final Infra infra, final int instancePId,
			final String userId) {

		try {

			logger.info("terminateCompute " + infra.getCompany().getName()
					+ " instance : " + instancePId);
			accountLogService.saveLog(
					"Started: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for " + InstanceP.findInstanceP(instancePId).getName(),
					Commons.task_name.COMPUTE.name(),
					Commons.task_status.SUCCESS.ordinal(), userId);
			Jec2 ec2 = getNewJce2(infra);
			List<String> instanceIds = new ArrayList<String>();
			instanceIds.add(InstanceP.findInstanceP(instancePId)
					.getInstanceId());
			try {
				ec2.terminateInstances(instanceIds);
			} catch (Exception e) {
				logger.error(e.getMessage());// e.printStackTrace();
			}

			for (Iterator iterator = instanceIds.iterator(); iterator.hasNext();) {
				String instanceId = (String) iterator.next();

				int SLEEP_TIME = 5000;
				int preparationSleepTime = 0;
				ReservationDescription reservationDescription = null;
				try {
					reservationDescription = ec2.describeInstances(
							Collections.singletonList(instanceId)).get(0);

				} catch (Exception e) {
					if (e.getMessage().contains("Index: 0, Size: 0")) {
						throw new Exception(
								"this instance is no more in the backend infra");
					}
				}
				Instance instanceEc2 = reservationDescription.getInstances()
						.get(0);
				/*InstanceP instanceP = InstanceP
						.findInstancePsByInstanceIdEquals(instanceId)
						.getSingleResult();*/
				InstanceP instanceP = InstanceP.findInstanceP(instancePId);
				
				int INSTANCE_START_SLEEP_TIME = 5000;
				long timeout = INSTANCE_START_SLEEP_TIME * 100;
				long runDuration = 0;
				while (!instanceEc2.isTerminated()) {
					runDuration = runDuration + INSTANCE_START_SLEEP_TIME;
					if (runDuration > timeout) {
						logger.info("Tried enough.Am bored, quitting.");
						break;
					}
					try {
						instanceP.setState(Commons.REQUEST_STATUS.TERMINATING
								+ "");
						instanceP.merge();
						logger.info("Instance " + instanceEc2.getInstanceId()
								+ " pending termination; sleeping "
								+ SLEEP_TIME + "ms");
						Thread.sleep(SLEEP_TIME);
						reservationDescription = ec2.describeInstances(
								Collections.singletonList(instanceEc2
										.getInstanceId())).get(0);
						instanceEc2 = reservationDescription.getInstances()
								.get(0);
					} catch (Exception e) {
						logger.error(e.getMessage());
						// e.printStackTrace();
					}
				}
				logger.info("out of while loop, instanceEc2.isTerminated() "
						+ instanceEc2.isTerminated());

				if (instanceEc2.isTerminated()) {
					logger.info("EC2 instance is now Terminated");
					if (preparationSleepTime > 0) {
						logger.info("Sleeping "
								+ preparationSleepTime
								+ "ms allowing instance services to start up properly.");
						Thread.sleep(preparationSleepTime);
						logger.info("Instance prepared - proceeding");
					}
					instanceP.setState(Commons.REQUEST_STATUS.TERMINATED + "");
					instanceP.merge();

					setAssetEndTime(instanceP.getAsset());

					try {
						AddressInfoP a = AddressInfoP
								.findAddressInfoPsByInstanceIdEquals(
										instanceP.getInstanceId())
								.getSingleResult();
						setAssetEndTime(a.getAsset());
						a.remove();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}

					accountLogService.saveLogAndSendMail(
							"Complete: "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instanceP.getName(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.SUCCESS.ordinal(), userId);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage()); // e.printStackTrace();

			accountLogService
					.saveLog(
							"Error in "
									+ this.getClass().getName()
									+ " : "
									+ Thread.currentThread().getStackTrace()[1]
											.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
													.getMethodName().indexOf("_")) + " for "
									+ instancePId + ", " + e.getMessage(),
							Commons.task_name.COMPUTE.name(),
							Commons.task_status.FAIL.ordinal(), userId);

			try {
				InstanceP instance1 = InstanceP.findInstanceP(instancePId);
				instance1.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instance1.getAsset());
				instance1.merge();
			} catch (Exception e2) {
				logger.error(e2);
				e2.printStackTrace();
			}

		}

	}// end terminateInstances

	@Async
	public void createCompute(final Infra infra, final InstanceP instance,
			final String userId) {

		InstanceP instanceLocal = null;
		try {
			//String imageName = instance.getImageId();
			String imageName = instance.getImage().getImageId();
			String keypairName = instance.getKeyName();
			String groupName = instance.getGroupName();
			String instanceType = instance.getInstanceType();

			logger.info("Launching " + infra.getCompany().getName()
					+ " instance " + instance.getId() + " for image: "
					+ imageName);
			accountLogService.saveLog(
					"Started: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ instance.getName(), Commons.task_name.COMPUTE
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);

			Jec2 ec2 = getNewJce2(infra);

			LaunchConfiguration launchConfiguration = new LaunchConfiguration(
					imageName);
			launchConfiguration.setKeyName(keypairName);
			launchConfiguration.setSecurityGroup(Collections
					.singletonList(groupName));
			launchConfiguration.setInstanceType(InstanceType
					.getTypeFromString(instanceType));
			launchConfiguration.setMinCount(1);
			launchConfiguration.setMaxCount(1);

			ReservationDescription reservationDescription = ec2.runInstances(launchConfiguration);
			instanceLocal = InstanceP.findInstanceP(instance.getId());

			Instance instanceEc2 = reservationDescription.getInstances().get(0);
			instanceLocal.setInstanceId(instanceEc2.getInstanceId());
			instanceLocal.setDnsName(instanceEc2.getDnsName());
			instanceLocal.setLaunchTime(instanceEc2.getLaunchTime().getTime());
			instanceLocal.setKernelId(instanceEc2.getKernelId());
			instanceLocal.setRamdiskId(instanceEc2.getRamdiskId());
			instanceLocal.setPlatform(instanceEc2.getPlatform());
			instanceLocal.setState(Commons.REQUEST_STATUS.STARTING + "");

			instanceLocal = instanceLocal.merge();

			int INSTANCE_START_SLEEP_TIME = 5000;
			int preparationSleepTime = 0;
			long timeout = INSTANCE_START_SLEEP_TIME * 400;
			long runDuration = 0;
			while (!instanceEc2.isRunning() && !instanceEc2.isTerminated()) {
				runDuration = runDuration + INSTANCE_START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break;
				}
				try {
					logger.info("Instance " + instanceEc2.getInstanceId()
							+ " still starting up; sleeping "
							+ INSTANCE_START_SLEEP_TIME + "ms");
					Thread.sleep(INSTANCE_START_SLEEP_TIME);
					reservationDescription = ec2.describeInstances(
							Collections.singletonList(instanceEc2
									.getInstanceId())).get(0);
					instanceEc2 = reservationDescription.getInstances().get(0);
				} catch (Exception e) {
					logger.error(e.getMessage());
					// e.printStackTrace();
				}
			}
			logger.info("out of while loop, instanceEc2.isRunning() "
					+ instanceEc2.isRunning());

			if (instanceEc2.isRunning()) {
				logger.info("EC2 instance is now running");
				if (preparationSleepTime > 0) {
					logger.info("Sleeping "
							+ preparationSleepTime
							+ "ms allowing instance services to start up properly.");
					Thread.sleep(preparationSleepTime);
					logger.info("Instance prepared - proceeding");
				}

				instanceLocal.setInstanceId(instanceEc2.getInstanceId());
				instanceLocal.setDnsName(instanceEc2.getDnsName());
				instanceLocal.setLaunchTime(instanceEc2.getLaunchTime()
						.getTime());
				instanceLocal.setKernelId(instanceEc2.getKernelId());
				instanceLocal.setRamdiskId(instanceEc2.getRamdiskId());
				instanceLocal.setPlatform(instanceEc2.getPlatform());
				instanceLocal.setState(Commons.REQUEST_STATUS.running + "");

				instanceLocal = instanceLocal.merge();

				/*
				 * AddressInfoP addressInfoP = new AddressInfoP();
				 * addressInfoP.setInstanceId(instanceLocal.getInstanceId());
				 * addressInfoP.setName("Ip for " + instanceLocal.getName());
				 * addressInfoP.setPublicIp(instanceLocal.getDnsName());
				 * addressInfoP.setStatus(Commons.ipaddress_STATUS.associated +
				 * ""); addressInfoP = addressInfoP.merge();
				 */

				// create an addressInfo object for this compute's IP.
				try {
					AddressInfoP a = new AddressInfoP();

					ProductCatalog pc = null;
					// Set<ProductCatalog> products = ;
					List products = ProductCatalog.findProductCatalogsByInfra(
							infra).getResultList();
					for (Iterator iterator = products.iterator(); iterator
							.hasNext();) {
						ProductCatalog productCatalog = (ProductCatalog) iterator
								.next();
						if (productCatalog.getProductType().equals(
								Commons.ProductType.IpAddress.getName())) {
							pc = productCatalog;
						}
					}
					/*
					 * ProductCatalog pc =
					 * ProductCatalog.findProductCatalogsByInfra(infra)
					 * 
					 * .findProductCatalogsByProductTypeAndCompany(Commons.
					 * ProductType.IpAddress.getName(),
					 * instanceLocal.getAsset().
					 * getUser().getProject().getDepartment
					 * ().getCompany()).getSingleResult();
					 */

					a.setAsset(Commons.getNewAsset(
							AssetType.findAssetTypesByNameEquals(
									Commons.ProductType.IpAddress + "")
									.getSingleResult(), instanceLocal
									.getAsset().getUser(), pc));
					a.setAssociated(true);
					a.setInstanceId(instanceLocal.getInstanceId());
					a.setName("Ip for " + instanceLocal.getName());
					a.setPublicIp(instanceLocal.getDnsName());
					a.setStatus(Commons.ipaddress_STATUS.associated + "");
					a.setReason("Automatic Ip addres assigned");
					setAssetStartTime(a.getAsset());
					a.merge();
				} catch (Exception e) {
					Commons.setSessionMsg("Error while createrCompute, Instance "
							+ instance.getName()
							+ "<br> Reason: "
							+ e.getMessage());
					e.printStackTrace();
					logger.error(e);
				}

				setAssetStartTime(instanceLocal.getAsset());

				logger.info("Done creating " + instance.getName()
						+ " and assigning ip " + instanceEc2.getDnsName());
				accountLogService.saveLogAndSendMail(
						"Complete: "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ instance.getName(), Commons.task_name.COMPUTE
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);
			} else {

				instanceLocal.setInstanceId(instanceEc2.getInstanceId());
				instanceLocal.setDnsName(instanceEc2.getDnsName());
				instanceLocal.setLaunchTime(instanceEc2.getLaunchTime()
						.getTime());
				instanceLocal.setKernelId(instanceEc2.getKernelId());
				instanceLocal.setRamdiskId(instanceEc2.getRamdiskId());
				instanceLocal.setPlatform(instanceEc2.getPlatform());
				instanceLocal.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instanceLocal.getAsset());
				instanceLocal = instanceLocal.merge();

				setAssetEndTime(instanceLocal.getAsset());

				throw new IllegalStateException(
						"Failed to start a new instance");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error while creating instance");

			accountLogService.saveLogAndSendMail(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ instance.getName() + ", " + e.getMessage(),
					Commons.task_name.COMPUTE.name(), Commons.task_status.FAIL
							.ordinal(), userId);
			try {
				InstanceP instance1 = InstanceP.findInstanceP(instance.getId());
				instance1.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instance1.getAsset());
				instance1.merge();

				setAssetEndTime(instance1.getAsset());

			} catch (Exception e2) {
				logger.error(e);
				e.printStackTrace();
			}
			Thread.currentThread().interrupt();
		}

	}// work

}
