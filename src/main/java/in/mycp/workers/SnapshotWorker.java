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

import in.mycp.domain.Infra;
import in.mycp.domain.SnapshotInfoP;
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

import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.SnapshotInfo;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("snapshotWorker")
public class SnapshotWorker extends Worker {

	@Autowired
	AccountLogService accountLogService;

	protected static Logger logger = Logger.getLogger(SnapshotWorker.class);

	@Async
	public void createSnapshot(final Infra infra, final SnapshotInfoP snapshot,
			final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ snapshot.getId(), Commons.task_name.SNAPSHOT
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);
			
			Jec2 ec2 = getNewJce2(infra);
			SnapshotInfo snapshotInfo = null;
			try {
				snapshotInfo = ec2.createSnapshot(snapshot.getVolumeId(),
						snapshot.getDescription());

			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
				if (e.getMessage().indexOf("Number of retries exceeded") > -1) {
					throw new Exception("No Connectivity to Cloud");
				}
			}

			SnapshotInfoP snapshot_local = SnapshotInfoP
					.findSnapshotInfoP(snapshot.getId());
			snapshot_local.setId(snapshot.getId());
			snapshot_local.setVolumeId(snapshot.getVolumeId());
			snapshot_local.setStartTime(snapshotInfo.getStartTime().getTime());
			snapshot_local.setProgress(snapshotInfo.getProgress());
			snapshot_local.setStatus(snapshotInfo.getStatus());
			snapshot_local.setSnapshotId(snapshotInfo.getSnapshotId());
			snapshot_local = snapshot_local.merge();

			int START_SLEEP_TIME = 10000;
			long timeout = START_SLEEP_TIME * 100;
			long runDuration = 0;
			while (!"completed".equals(snapshotInfo.getStatus())) {
				runDuration = runDuration + START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break;
				}
				logger.info("SnapShot  " + snapshotInfo.getSnapshotId()
						+ " still getting created; sleeping "
						+ START_SLEEP_TIME + "ms");
				Thread.sleep(START_SLEEP_TIME);

				snapshotInfo = ec2
						.describeSnapshots(
								Collections.singletonList(snapshotInfo
										.getSnapshotId())).get(0);
				logger.info("Snapshot " + snapshotInfo.getSnapshotId()
						+ " progress = " + snapshotInfo.getProgress());

				snapshot_local.setProgress(snapshotInfo.getProgress());
				snapshot_local.setStatus(snapshotInfo.getStatus());
				snapshot_local = snapshot_local.merge();
			}

			if ("completed".equals(snapshotInfo.getStatus())) {

				snapshot_local.setDescription(snapshotInfo.getDescription());
				snapshot_local.setProgress(snapshotInfo.getProgress());
				snapshot_local.setVolumeId(snapshot.getVolumeId());
				snapshot_local.setStartTime(snapshotInfo.getStartTime()
						.getTime());
				snapshot_local.setSnapshotId(snapshotInfo.getSnapshotId());
				snapshot_local.setStatus(snapshotInfo.getStatus());
				snapshot_local.setOwnerId(snapshotInfo.getOwnerId());
				snapshot_local.setVolumeSize(snapshotInfo.getVolumeSize());
				snapshot_local.setOwnerAlias(snapshotInfo.getOwnerAlias());

				snapshot_local = snapshot_local.merge();

				setAssetStartTime(snapshot_local.getAsset());

				logger.info("SnapShot  " + snapshotInfo.getSnapshotId()
						+ " created");
				accountLogService.saveLog(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ snapshot_local.getId(),
						Commons.task_name.SNAPSHOT.name(),
						Commons.task_status.SUCCESS.ordinal(), userId);
			}
		} catch (Exception e) {
			SnapshotInfoP s = SnapshotInfoP.findSnapshotInfoP(snapshot.getId());
			s.setStatus(Commons.SNAPSHOT_STATUS.inactive + "");
			s.merge();
			logger.error(e);// e.printStackTrace();

			accountLogService.saveLog(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ snapshot.getId() + ", " + e.getMessage(),
					Commons.task_name.SNAPSHOT.name(), Commons.task_status.FAIL
							.ordinal(), userId);
		}

	}

	@Async
	public void deleteSnapshot(final Infra infra, final SnapshotInfoP snapshot,
			final String userId) {
		try {
			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ snapshot.getId(), Commons.task_name.SNAPSHOT
							.name(), Commons.task_status.SUCCESS.ordinal(),
					userId);
			Jec2 ec2 = getNewJce2(infra);
			try {
				ec2.deleteSnapshot(snapshot.getSnapshotId());
			} catch (Exception e) {
				logger.error(e);// e.printStackTrace();
			}

			int START_SLEEP_TIME = 10000;
			long timeout = START_SLEEP_TIME * 100;
			long runDuration = 0;
			boolean found = true;
			outer: while (found) {
				logger.info("SnapShot " + snapshot.getSnapshotId()
						+ " still getting deleted; sleeping "
						+ START_SLEEP_TIME + "ms");
				Thread.sleep(START_SLEEP_TIME);
				runDuration = runDuration + START_SLEEP_TIME;
				if (runDuration > timeout) {
					logger.info("Tried enough.Am bored, quitting.");
					break outer;
				}
				try {
					List<String> params = new ArrayList<String>();
					List<SnapshotInfo> snapshots = ec2
							.describeSnapshots(params);
					found = false;
					for (Iterator iterator = snapshots.iterator(); iterator
							.hasNext();) {
						SnapshotInfo snapshotInfo = (SnapshotInfo) iterator
								.next();
						logger.info("snapshotInfo.getSnapshotId() "
								+ snapshotInfo.getSnapshotId()
								+ " snapshot.getSnapshotId() = "
								+ snapshot.getSnapshotId());
						if (snapshot.getSnapshotId().equals(
								snapshotInfo.getSnapshotId())) {
							found = true;
						}
					}
					// check if the snapshot is deleted
					if (!found) {
						break outer;
					}
				} catch (Exception e) {
					logger.error(e);// e.printStackTrace();
				}
			}
			logger.info("snapshotInfo deleteSnapshot  out of while");
			if (!found) {
				SnapshotInfoP snapshot_local = SnapshotInfoP
						.findSnapshotInfoP(snapshot.getId());
				snapshot_local.setStatus(Commons.SNAPSHOT_STATUS.inactive + "");
				snapshot_local = snapshot_local.merge();

				setAssetEndTime(snapshot_local.getAsset());
				accountLogService.saveLog(
						"Complete : "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ snapshot.getId(), Commons.task_name.SNAPSHOT
								.name(), Commons.task_status.SUCCESS.ordinal(),
						userId);

			}

		} catch (Exception e) {
			logger.error(e);// e.printStackTrace();

			accountLogService.saveLog(
					"Error in "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1]
									.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
											.getMethodName().indexOf("_")) + " for "
							+ snapshot.getId() + ", " + e.getMessage(),
					Commons.task_name.SNAPSHOT.name(), Commons.task_status.FAIL
							.ordinal(), userId);
		}

	}
}
