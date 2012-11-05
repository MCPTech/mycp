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

import in.mycp.domain.AttachmentInfoP;
import in.mycp.domain.Infra;
import in.mycp.domain.VolumeInfoP;
import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xerox.amazonws.ec2.AttachmentInfo;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.VolumeInfo;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("volumeWorker")
public class VolumeWorker extends Worker {

	protected static Logger logger = Logger.getLogger(VolumeWorker.class);

	@Autowired
	AccountLogService accountLogService;

	@Async
	public void detachVolume(final Infra infra, final VolumeInfoP volumeInfoP,
			final String userId) {
		String threadName = Thread.currentThread().getName();

		try {
			logger.debug("threadName " + threadName + " started.");
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName(), Commons.task_name.VOLUME
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);
			Jec2 ec2 = getNewJce2(infra);

			List<AttachmentInfoP> attaches = AttachmentInfoP
					.findAttachmentInfoPsByVolumeIdEquals(
							volumeInfoP.getVolumeId()).getResultList();
			String instanceId = "";
			String device = "";
			if (attaches != null && attaches.size() > 0) {
				for (Iterator iterator = attaches.iterator(); iterator
						.hasNext();) {
					AttachmentInfoP attachmentInfoP = (AttachmentInfoP) iterator
							.next();
					instanceId = attachmentInfoP.getInstanceId();
					device = attachmentInfoP.getDevice();
				}
			}
			logger.info("detaching " + volumeInfoP.getVolumeId() + "  "
					+ instanceId + " " + device);
			AttachmentInfo attachmentInfo = ec2.detachVolume(
					volumeInfoP.getVolumeId(), instanceId, device, false);

			int START_SLEEP_TIME = 10000;
			String attachedStatus = "";

			long timeout = START_SLEEP_TIME * 100;
			long runDuration = 0;
			outer: while (!Commons.VOLUME_STATUS_AVAILABLE
					.equals(attachedStatus)) {
				runDuration = runDuration + START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break outer;
				}
				try {
					logger.info("Volume  " + attachmentInfo.getVolumeId()
							+ " still detaching; sleeping " + START_SLEEP_TIME
							+ "ms");
					Thread.sleep(START_SLEEP_TIME);

					List<String> params = new ArrayList<String>();
					List<VolumeInfo> volumes = ec2.describeVolumes(params);
					for (Iterator iterator = volumes.iterator(); iterator
							.hasNext();) {
						VolumeInfo volumeInfoLocal = (VolumeInfo) iterator
								.next();
						if (volumeInfoLocal.getVolumeId().equals(
								volumeInfoP.getVolumeId())) {
							if (volumeInfoLocal.getStatus().equals(
									Commons.VOLUME_STATUS_AVAILABLE)) {
								attachedStatus = volumeInfoLocal.getStatus();
								break outer;
							}
						}
					}
				} catch (Exception e) {

					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}

			if (Commons.VOLUME_STATUS_AVAILABLE.equals(attachedStatus)) {
				VolumeInfoP volumeInfoPLocal = VolumeInfoP
						.findVolumeInfoP(volumeInfoP.getId());
				volumeInfoPLocal.setInstanceId("");
				volumeInfoPLocal.setDevice("");
				volumeInfoPLocal.setStatus(Commons.VOLUME_STATUS_AVAILABLE);
				volumeInfoPLocal.setDetails("");
				// AttachmentInfoP.find
				attaches = AttachmentInfoP
						.findAttachmentInfoPsByVolumeIdEquals(
								volumeInfoPLocal.getVolumeId()).getResultList();

				if (attaches != null && attaches.size() > 0) {
					for (Iterator iterator = attaches.iterator(); iterator
							.hasNext();) {
						AttachmentInfoP attachmentInfoP = (AttachmentInfoP) iterator
								.next();
						attachmentInfoP.remove();
					}
				}
				volumeInfoPLocal = volumeInfoPLocal.merge();
				logger.info("Volume  " + attachmentInfo.getVolumeId()
						+ " detached.");
				accountLogService.saveLogAndSendMail(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ volumeInfoP.getName(), Commons.task_name.VOLUME
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.VOLUME.name(), Commons.task_status.FAIL
							.ordinal(), userId);
		}
	}// end of detachVolume

	@Async
	public void deleteVolume(final Infra infra, final VolumeInfoP volumeInfoP,
			final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName(), Commons.task_name.VOLUME
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);

			Jec2 ec2 = getNewJce2(infra);
			VolumeInfo volumeInfo = new VolumeInfo("", "", "", "", "",
					Calendar.getInstance());
			try {
				ec2.deleteVolume(volumeInfoP.getVolumeId());
			} catch (Exception e) {
				logger.error(e.getMessage());// e.printStackTrace();
			}

			boolean found = false;
			int START_SLEEP_TIME = 10000;
			long timeout = START_SLEEP_TIME * 100;
			long runDuration = 0;
			outer: while (volumeInfo != null) {
				runDuration = runDuration + START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break outer;
				}
				found = false;
				try {
					// if the delete went thorugh fine ,
					// either the volume gets listed below witha status of
					// deleted
					// or
					// the volume Id doesnt get listed at all.

					// when the vol gets listed
					List<String> params = new ArrayList<String>();
					List<VolumeInfo> volumes = ec2.describeVolumes(params);
					for (Iterator iterator = volumes.iterator(); iterator
							.hasNext();) {
						VolumeInfo volumeInfoLocal = (VolumeInfo) iterator
								.next();
						if (volumeInfoLocal.getVolumeId().equals(
								volumeInfoP.getVolumeId())) {
							found = true;
							if (volumeInfoLocal.getStatus().equals(
									Commons.VOLUME_STATUS_DELETED)) {
								volumeInfo = null;
								break outer;
							}
						}
					}

					// when volume doesnt get listed
					if (!found) {
						volumeInfo = null;
						break outer;
					}
					logger.info("Volume  " + volumeInfoP.getVolumeId()
							+ " still being removed; sleeping "
							+ START_SLEEP_TIME + "ms");
					Thread.sleep(START_SLEEP_TIME);
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error(e.getMessage());
					e.printStackTrace();
					volumeInfo = null;
				}
			}
			if (volumeInfo == null) {
				volumeInfoP.setStatus(Commons.VOLUME_STATUS_DELETED);
				volumeInfoP.merge();
				// volumeInfoP.remove();
				logger.info("Volume  " + volumeInfoP.getVolumeId()
						+ " deleted.");
				setAssetEndTime(VolumeInfoP
						.findVolumeInfoP(volumeInfoP.getId()).getAsset());

				accountLogService.saveLogAndSendMail(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ volumeInfoP.getName(), Commons.task_name.VOLUME
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.VOLUME.name(), Commons.task_status.FAIL
							.ordinal(), userId);
		}

	}// end of deleteVolume

	@Async
	public void attachVolume(final Infra infra, final VolumeInfoP volumeInfoP,
			final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName(), Commons.task_name.VOLUME
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);
			Jec2 ec2 = getNewJce2(infra);
			AttachmentInfo attachmentInfo = ec2.attachVolume(
					volumeInfoP.getVolumeId(), volumeInfoP.getInstanceId(),
					volumeInfoP.getDevice());

			int START_SLEEP_TIME = 10000;
			VolumeInfo volumeInfo = null;

			String attachedStatus = "";
			long timeout = START_SLEEP_TIME * 100;
			long runDuration = 0;

			outer: while (!Commons.VOLUME_STATUS_INUSE.equals(attachedStatus)) {
				runDuration = runDuration + START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break outer;
				}
				try {
					logger.info("Volume  " + attachmentInfo.getVolumeId()
							+ " still attaching; sleeping " + START_SLEEP_TIME
							+ "ms");
					Thread.sleep(START_SLEEP_TIME);

					List<String> params = new ArrayList<String>();
					List<VolumeInfo> volumes = ec2.describeVolumes(params);
					for (Iterator iterator = volumes.iterator(); iterator
							.hasNext();) {
						VolumeInfo volumeInfoLocal = (VolumeInfo) iterator
								.next();
						if (volumeInfoLocal.getVolumeId().equals(
								volumeInfoP.getVolumeId())) {
							if (volumeInfoLocal.getStatus().equals(
									Commons.VOLUME_STATUS_INUSE)) {
								volumeInfo = volumeInfoLocal;
								attachedStatus = volumeInfoLocal.getStatus();
								break outer;
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
			logger.info("Volume " + attachmentInfo.getVolumeId() + " attached.");

			if (Commons.VOLUME_STATUS_INUSE.equals(attachedStatus)) {
				VolumeInfoP localVolumeInfoP = VolumeInfoP
						.findVolumeInfoPsByVolumeIdEquals(
								volumeInfoP.getVolumeId()).getSingleResult();
				localVolumeInfoP.setStatus(volumeInfo.getStatus());
				localVolumeInfoP = localVolumeInfoP.merge();

				List<AttachmentInfo> attachments = volumeInfo
						.getAttachmentInfo();
				Set<AttachmentInfoP> attachments4Store = new HashSet<AttachmentInfoP>();

				for (Iterator iterator2 = attachments.iterator(); iterator2
						.hasNext();) {
					AttachmentInfo attachmentInfoLocal = (AttachmentInfo) iterator2
							.next();
					AttachmentInfoP attachmentInfoP = new AttachmentInfoP();
					attachmentInfoP.setAttachTime(attachmentInfoLocal
							.getAttachTime().getTime());
					attachmentInfoP.setDevice(attachmentInfoLocal.getDevice());
					attachmentInfoP.setInstanceId(attachmentInfoLocal
							.getInstanceId());
					attachmentInfoP.setVolumeId(attachmentInfoLocal
							.getVolumeId());
					attachmentInfoP.setStatus(attachmentInfoLocal.getStatus());
					attachmentInfoP.setVolumeInfo(localVolumeInfoP);
					attachmentInfoP = attachmentInfoP.merge();
					attachments4Store.add(attachmentInfoP);
				}
				localVolumeInfoP.setAttachmentInfoPs(attachments4Store);
				localVolumeInfoP = localVolumeInfoP.merge();

				accountLogService.saveLogAndSendMail(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ volumeInfoP.getName(), Commons.task_name.VOLUME
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);

			}
			logger.info("Volume metadata saved in DB for "
					+ attachmentInfo.getVolumeId() + "");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			accountLogService.saveLogAndSendMail(
					"Error in: "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.VOLUME.name(), Commons.task_status.FAIL
							.ordinal(), userId);
		}

	}// end of attachVolume

	@Async
	public void createVolume(final Infra infra, final VolumeInfoP volumeInfoP,
			final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName(), Commons.task_name.VOLUME
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);

			Jec2 ec2 = getNewJce2(infra);
			VolumeInfo volumeInfo = null;
			try {
				volumeInfo = ec2.createVolume("" + volumeInfoP.getSize(),
						volumeInfoP.getSnapshotId(), volumeInfoP.getZone());

			} catch (Exception e) {
				logger.error(e.getMessage());// e.printStackTrace();
			}

			volumeInfoP.setVolumeId(volumeInfo.getVolumeId());
			volumeInfoP.setCreateTime(volumeInfo.getCreateTime().getTime());
			logger.info("setting " + volumeInfo.getStatus());
			volumeInfoP.setStatus(volumeInfo.getStatus());

			VolumeInfoP volumeInfoPlocal = volumeInfoP.merge();

			int INSTANCE_START_SLEEP_TIME = 10000;
			long timeout = INSTANCE_START_SLEEP_TIME * 100;
			long runDuration = 0;
			while (!volumeInfo.getStatus().equals("available")) {
				runDuration = runDuration + INSTANCE_START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break;
				}
				try {
					logger.info("Volume  " + volumeInfo.getVolumeId()
							+ " still starting up; sleeping "
							+ INSTANCE_START_SLEEP_TIME + "ms");
					Thread.sleep(INSTANCE_START_SLEEP_TIME);
					volumeInfo = ec2
							.describeVolumes(
									Collections.singletonList(volumeInfo
											.getVolumeId())).get(0);
					logger.info(volumeInfo.getVolumeId() + "   "
							+ volumeInfo.getStatus());
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error(e.getMessage());// e.printStackTrace();
				}
			}

			if (volumeInfo.getStatus().equals(Commons.VOLUME_STATUS_AVAILABLE)) {
				logger.info("Volume  " + volumeInfo.getVolumeId()
						+ " now available !");
				logger.info("setting " + volumeInfo.getStatus());
				volumeInfoPlocal.setStatus(volumeInfo.getStatus());
				volumeInfoPlocal = volumeInfoPlocal.merge();
				setAssetStartTime(volumeInfoPlocal.getAsset());

				accountLogService.saveLogAndSendMail(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ volumeInfoP.getName(), Commons.task_name.VOLUME
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);

				logger.info("Done creating volume " + volumeInfo.getVolumeId());
			} else {
				volumeInfoPlocal.setStatus(Commons.VOLUME_STATUS_FAILED);
				volumeInfoPlocal = volumeInfoPlocal.merge();
				setAssetEndTime(volumeInfoPlocal.getAsset());
				throw new IllegalStateException("Failed to create new Volume "
						+ volumeInfo.getVolumeId());

			}

		} catch (Exception e) {
			logger.error(e);
			accountLogService.saveLogAndSendMail(
					"Error in : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ volumeInfoP.getName() + ", " + e.getMessage(),
					Commons.task_name.VOLUME.name(), Commons.task_status.FAIL
							.ordinal(), userId);

			try {
				VolumeInfoP volumeInfoPlocal = VolumeInfoP
						.findVolumeInfoP(volumeInfoP.getId());
				volumeInfoPlocal.setStatus(Commons.VOLUME_STATUS_FAILED);
				volumeInfoPlocal = volumeInfoPlocal.merge();
				setAssetEndTime(volumeInfoPlocal.getAsset());
			} catch (Exception e2) {
				logger.error(e);
			}

			Thread.currentThread().interrupt();
		}

	}// work

}
