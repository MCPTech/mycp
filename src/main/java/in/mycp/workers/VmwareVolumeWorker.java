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
import in.mycp.domain.InstanceP;
import in.mycp.domain.VolumeInfoP;
import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.ovf.CimString;
import com.vmware.vcloud.api.rest.schema.ovf.RASDType;
import com.vmware.vcloud.api.rest.schema.ovf.ResourceType;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.VirtualDisk;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@Component("vmwareVolumeWorker")
public class VmwareVolumeWorker extends Worker {

	protected static Logger logger = Logger.getLogger(VmwareVolumeWorker.class);

	@Autowired
	AccountLogService accountLogService;

	@Async
	public void deleteVolume(final Infra infra, final VolumeInfoP volumeInfoP, final String userId) {
		try {
			VcloudClient vcloudClient = getVcloudClient(infra);

			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + volumeInfoP.getName(),
					Commons.task_name.VOLUME.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			// find the instance to which this disk has to be attcahed
			InstanceP instance = InstanceP.findInstancePsByInstanceIdEquals(volumeInfoP.getInstanceId()).getSingleResult();
			// look up teh corresponding VM for this instance in vcloud
			String vmHref = instance.getVcloudVmHref();
			ReferenceType refType = new ReferenceType();
			refType.setHref(vmHref);
			VM vm = VM.getVMByReference(vcloudClient, refType);

			// add teh disk into this VM
			List<VirtualDisk> disks = vm.getDisks();
			boolean removed = false;
			for (int i = 0; i < disks.size(); i++) {
				
				String diskNameFromMycp = volumeInfoP.getVolumeId().substring(volumeInfoP.getVolumeId().indexOf("_")+1,volumeInfoP.getVolumeId().length());
				String diskNameFromCloud = disks.get(i).getItemResource().getElementName().getValue();
				//logger.info(" diskNameFromMycp = diskNameFromCloud ?"+diskNameFromMycp+" =  "+diskNameFromCloud );
				if (diskNameFromMycp.equals(diskNameFromCloud)){
					disks.remove(i);
					removed = true;
				}
			}

			if(removed){
				vm.updateDisks(disks).waitForTask(-1);
				volumeInfoP.setStatus(Commons.VOLUME_STATUS_DELETED);
				volumeInfoP.merge();
				// volumeInfoP.remove();
				logger.info("Volume  " + volumeInfoP.getVolumeId() + " deleted.");
				setAssetEndTime(VolumeInfoP.findVolumeInfoP(volumeInfoP.getId()).getAsset());

				accountLogService.saveLogAndSendMail("Complete : " + this.getClass().getName() + " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
						+ " for " + volumeInfoP.getName(), Commons.task_name.VOLUME.name(), Commons.task_status.SUCCESS.ordinal(), userId);
	
			}else {
				logger.error("Volume  " + volumeInfoP.getVolumeId() + "could not be found, cannot deleted.");
				accountLogService.saveLogAndSendMail("Complete : " + this.getClass().getName() + " : "
						+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
						+ " for " + volumeInfoP.getName(), Commons.task_name.VOLUME.name(), Commons.task_status.FAIL.ordinal(), userId);
	
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			volumeInfoP.setStatus(Commons.VOLUME_STATUS_FAILED);
			setAssetEndTime(VolumeInfoP.findVolumeInfoP(volumeInfoP.getId()).getAsset());
			volumeInfoP.merge();

			accountLogService.saveLogAndSendMail("Error in: " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + volumeInfoP.getName() + ", " + e.getMessage(), Commons.task_name.VOLUME.name(), Commons.task_status.FAIL.ordinal(), userId);
		}

	}// end of deleteVolume

	@Async
	public void createAndAttachVolume(final Infra infra, final VolumeInfoP volumeInfoP, final String userId) {
		VolumeInfoP volumeInfoPLocal = volumeInfoP.merge();

		try {
			VcloudClient vcloudClient = getVcloudClient(infra);

			accountLogService.saveLog(
					"Started : "
							+ this.getClass().getName()
							+ " : "
							+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0,
									Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_")) + " for " + volumeInfoP.getName(),
					Commons.task_name.VOLUME.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			// setup new disk info here
			String diskInMBytes = (volumeInfoP.getSize() * 1024) + "";
			//String diskName = "Hard disk";
			//String diskId = volumeInfoP.getInstanceId() + "_" + volumeInfoP.getId();
			CimString cimString = new CimString();
			Map<QName, String> cimAttributes = cimString.getOtherAttributes();

			cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "busSubType", "vcloud"), "lsilogic");
			cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "busType", "vcloud"), "6");
			cimAttributes.put(new QName("http://www.vmware.com/vcloud/v1.5", "capacity", "vcloud"), diskInMBytes);

			CimString setElementName = new CimString();
			setElementName.setValue("not used in vcloud");
			CimString setInstanceID = new CimString();
			setInstanceID.setValue("not used in vcloud");
			ResourceType setResourceType = new ResourceType();
			setResourceType.setValue("17");

			RASDType diskItemType = new RASDType();
			diskItemType.setElementName(setElementName);
			diskItemType.setInstanceID(setInstanceID);
			diskItemType.setResourceType(setResourceType);
			List<CimString> diskAttributes = diskItemType.getHostResource();
			diskAttributes.add(cimString);

			// find the instance to which this disk has to be attcahed
			InstanceP instance = InstanceP.findInstancePsByInstanceIdEquals(volumeInfoP.getInstanceId()).getSingleResult();
			// look up teh corresponding VM for this instance in vcloud
			String vmHref = instance.getVcloudVmHref();
			ReferenceType refType = new ReferenceType();
			refType.setHref(vmHref);
			VM vm = VM.getVMByReference(vcloudClient, refType);

			//cannot specify disk name in vcloud - refer http://communities.vmware.com/thread/319223
			//so do a stupid thing to save the name.
			Hashtable<String, String> existingDiskNames = new Hashtable<String, String>();
			List<VirtualDisk> disks = vm.getDisks();
			for (Iterator iterator = disks.iterator(); iterator.hasNext(); ) {
				VirtualDisk virtualDisk = (VirtualDisk) iterator.next();
				existingDiskNames.put(virtualDisk.getItemResource().getElementName().getValue(), virtualDisk.getItemResource().getElementName().getValue());
			}
			
			// add teh disk into this VM
			disks.add(new VirtualDisk(diskItemType));
			vm.updateDisks(disks).waitForTask(-1);
			
			String diskName ="";
			vm = VM.getVMByReference(vcloudClient, vm.getReference());
			for (Iterator iterator = vm.getDisks().iterator(); iterator.hasNext(); ) {
				VirtualDisk virtualDisk = (VirtualDisk) iterator.next();
				if(existingDiskNames.containsKey(virtualDisk.getItemResource().getElementName().getValue())){
					continue;
				}
				//if not caught in above loop, now i have the new disk.
				diskName=virtualDisk.getItemResource().getElementName().getValue();
				break;
			}
			

			// if the above is successfull , set up teh metadata for this disk
			// in mycp
			volumeInfoP.setVolumeId(volumeInfoP.getInstanceId()+"_"+diskName);
			volumeInfoP.setCreateTime(new Date());
			volumeInfoP.setStatus(Commons.VOLUME_STATUS_ATTACHED);
			// merge it and save the reference as a local variable since
			// volumeInfoP is final and cannot be saved back into the same ref
			volumeInfoPLocal = volumeInfoP.merge();
			// if all is successfull, start teh asset timing for metering
			setAssetStartTime(volumeInfoPLocal.getAsset());

			accountLogService.saveLogAndSendMail("Complete : " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + volumeInfoPLocal.getName(), Commons.task_name.VOLUME.name(), Commons.task_status.SUCCESS.ordinal(), userId);

			logger.info("Done creating volume " + volumeInfoPLocal.getVolumeId());

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			volumeInfoPLocal.setStatus(Commons.VOLUME_STATUS_FAILED);
			volumeInfoPLocal = volumeInfoPLocal.merge();
			setAssetEndTime(volumeInfoPLocal.getAsset());
			accountLogService.saveLogAndSendMail("Error in : " + this.getClass().getName() + " : "
					+ Thread.currentThread().getStackTrace()[1].getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1].getMethodName().indexOf("_"))
					+ " for " + volumeInfoP.getName() + ", " + e.getMessage(), Commons.task_name.VOLUME.name(), Commons.task_status.FAIL.ordinal(), userId);
		}
	}// work

}
