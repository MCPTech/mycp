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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vmware.vcloud.api.rest.schema.InstantiateVAppTemplateParamsType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.QueryResultVAppRecordType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.VirtualNetworkCard;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.UndeployPowerActionType;
import com.vmware.vcloud.sdk.constants.VappStatus;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;
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

@Component("vmwareComputeWorker")
public class VmwareComputeWorker extends Worker {
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
			
		VcloudClient vcloudClient = getVcloudClient(infra);
		InstanceP currentCompute = InstanceP.findInstanceP(instancePId);
		String instanceHref = currentCompute.getInstanceId();
		ReferenceType vappRefType = new ReferenceType();
		vappRefType.setHref(instanceHref);
		Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRefType);
			
			currentCompute.setState(Commons.REQUEST_STATUS.RESTARTING+ "");
			currentCompute = currentCompute.merge();
				
		vapp.reboot().waitForTask(-1);

			if(Vapp.getVappByReference(vcloudClient, vapp.getReference()).getVappStatus() == VappStatus.POWERED_ON){
				currentCompute.setState(Commons.REQUEST_STATUS.running+ "");
				currentCompute = currentCompute.merge();
				accountLogService.saveLogAndSendMail(
						"Complete: "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ currentCompute.getName(),
						Commons.task_name.COMPUTE.name(),
						Commons.task_status.SUCCESS.ordinal(), userId);
				
			} else {
				currentCompute.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(currentCompute.getAsset());
				currentCompute.merge();
				accountLogService.saveLogAndSendMail(
						"Error in: "
								+ this.getClass().getName()
								+ " : "
								+ Thread.currentThread().getStackTrace()[1]
										.getMethodName().subSequence(0, Thread.currentThread().getStackTrace()[1]
												.getMethodName().indexOf("_")) + " for "
								+ currentCompute.getName(),
						Commons.task_name.COMPUTE.name(),
						Commons.task_status.FAIL.ordinal(), userId);
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
			
			VcloudClient vcloudClient = getVcloudClient(infra);
			InstanceP currentCompute = InstanceP.findInstanceP(instancePId);
			String instanceHref = currentCompute.getInstanceId();
			ReferenceType vappRefType = new ReferenceType();
			vappRefType.setHref(instanceHref);
			Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRefType);
				
				currentCompute.setState(Commons.REQUEST_STATUS.STARTING+ "");
				currentCompute = currentCompute.merge();
			
			vapp.deploy(true, 0, false).waitForTask(-1);

			if(Vapp.getVappByReference(vcloudClient, vapp.getReference()).getVappStatus() == VappStatus.POWERED_ON){
				currentCompute.setState(Commons.REQUEST_STATUS.running+ "");
				currentCompute = currentCompute.merge();
			

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
				logger.error(e2);
				e2.printStackTrace();
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


			VcloudClient vcloudClient = getVcloudClient(infra);
			InstanceP currentCompute = InstanceP.findInstanceP(instancePId);
			String instanceHref = currentCompute.getInstanceId();
			ReferenceType vappRefType = new ReferenceType();
			vappRefType.setHref(instanceHref);
			Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRefType);
				
				currentCompute.setState(Commons.REQUEST_STATUS.SHUTTING_DOWN+ "");
				currentCompute = currentCompute.merge();
			
				vapp.undeploy(UndeployPowerActionType.SHUTDOWN).waitForTask(-1);
			
			if(Vapp.getVappByReference(vcloudClient, vapp.getReference()).getVappStatus() == VappStatus.POWERED_OFF){
			
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
			
			VcloudClient vcloudClient = getVcloudClient(infra);
			InstanceP currentCompute = InstanceP.findInstanceP(instancePId);
			String instanceHref = currentCompute.getInstanceId();
			ReferenceType vappRefType = new ReferenceType();
			vappRefType.setHref(instanceHref);
			Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRefType);
				
			currentCompute.setState(Commons.REQUEST_STATUS.TERMINATING
					+ "");
			currentCompute.merge();
			
				try {
					vapp.undeploy(UndeployPowerActionType.SHUTDOWN).waitForTask(-1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Vapp.getVappByReference(vcloudClient, vapp.getReference()).delete().waitForTask(-1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//test whether you can get access to this vapp, if not then delete is successfull.
				
				try {
					if(Vapp.getVappByReference(vcloudClient, vapp.getReference()).getVappStatus() == VappStatus.POWERED_OFF){
						logger.info("Still not deleted "+vapp.getReference().getName());
					}
				} catch (VCloudException e) {
					if (e.getMessage()!=null && e.getMessage().contains("No access to entity")){
						
					
				InstanceP instanceP = InstanceP
						.findInstancePsByInstanceIdEquals(instancePId+"")
						.getSingleResult();
				
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
					} catch (Exception e1) {
						e1.printStackTrace();
						logger.error(e1);
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
			VcloudClient vcloudClient = getVcloudClient(infra);
			
			String vappTemplateHref =instance.getImageId();
			String vappName = instance.getName()+" (VAPP) ";
			String vmNameLocal =instance.getName()+" (VM) ";
			//String orgName ="mycloudportal";
			String vAppNetworkName=instance.getGroupName();
			String fenceMode="bridged";
			boolean deployOnInstantiate = true;
			boolean powerOnOnInstantiate = true;
			
			ReferenceType vappTemplateRefType = new ReferenceType();
			vappTemplateRefType.setHref(vappTemplateHref);
			VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(vcloudClient, vappTemplateRefType);
			/*String imageName = instance.getImageId();
			String keypairName = instance.getKeyName();
			String groupName = instance.getGroupName();
			String instanceType = instance.getInstanceType();*/

			logger.info("Launching " + infra.getCompany().getName()
					+ " instance " + instance.getId() + " for image: "
					+ vappTemplate.getResource().getName() +" with ID "+vappTemplate.getResource().getId());
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

			Vdc vdc = Vdc.getVdcByReference(vcloudClient, vappTemplate.getVdcReference()) ;
			
			NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
			if (vdc.getAvailableNetworkRefs().size() == 0) {
				logger.info("No Networks in vdc to instantiate the vapp");
				//System.exit(0);
				throw new Exception("No Networks in vdc to instantiate the vapp "+vappName);
			}
			
			
			networkConfigurationType.setParentNetwork(vdc.getAvailableNetworkRefByName(vAppNetworkName));
			networkConfigurationType.setFenceMode(FenceModeValuesType.fromValue(fenceMode).value());
			
			
			VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
			vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
			vAppNetworkConfigurationType.setNetworkName(vAppNetworkName);

			
			NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
			MsgType networkInfo = new MsgType();
			networkConfigSectionType.setInfo(networkInfo);
			List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
			vAppNetworkConfigs.add(vAppNetworkConfigurationType);

			InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
			List<JAXBElement<? extends SectionType>> sections = instantiationParamsType.getSection();
			sections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));

			InstantiateVAppTemplateParamsType instVappTemplParamsType = new InstantiateVAppTemplateParamsType();
			instVappTemplParamsType.setName(vappName);
			instVappTemplParamsType.setSource(vappTemplate.getReference());
			
			instVappTemplParamsType.setDeploy(deployOnInstantiate);
			instVappTemplParamsType.setPowerOn(powerOnOnInstantiate);
			
			instVappTemplParamsType.setInstantiationParams(instantiationParamsType);

			Vapp newVapp = vdc.instantiateVappTemplate(instVappTemplParamsType);
			
			instanceLocal = InstanceP.findInstanceP(instance.getId());
			
			instanceLocal.setInstanceId(newVapp.getResource().getHref());
			instanceLocal.setLaunchTime(new Date());
			instanceLocal.setState(Commons.REQUEST_STATUS.STARTING + "");
			instanceLocal = instanceLocal.merge();
			
			List<Task> ts =  newVapp.getTasks();
			int taskComplete =1;
			while (taskComplete < 100){
				
				try {
					for (Iterator iterator = ts.iterator(); iterator.hasNext(); ) {
						Task task = (Task) iterator.next();
						logger.info("Task Progress = "+task.getProgress(vcloudClient, task.getReference()).intValue()+" % ");
						taskComplete = task.getProgress(vcloudClient, task.getReference()).intValue();
						task.waitForTask(10000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//end while

			logger.info("Vapp Status = "+Vapp.getVappByReference(vcloudClient, newVapp.getReference()).getVappStatus().toString());
			if(Vapp.getVappByReference(vcloudClient, newVapp.getReference()).getVappStatus() != VappStatus.POWERED_ON){
				instanceLocal.setState(Commons.REQUEST_STATUS.FAILED + "");
				setAssetEndTime(instanceLocal.getAsset());
				instanceLocal = instanceLocal.merge();
				throw new Exception("Vapp is not powered on "+Vapp.getVappByReference(vcloudClient, newVapp.getReference()).getVappStatus().toString());
			}

			String href = newVapp.getReference().getHref(); 
			Date vappCreationDate=null;
			 RecordResult<QueryResultVAppRecordType> vappResult = vcloudClient
	                    .getQueryService().queryRecords(QueryRecordType.VAPP);
	          for (QueryResultVAppRecordType vappRecord : vappResult.getRecords()) {
	        	  if(vappRecord.getHref().equals(href)){
	        		  vappCreationDate=new Date(vappRecord.getCreationDate().getYear(), vappRecord.getCreationDate().getMonth(), vappRecord.getCreationDate().getDay(), vappRecord.getCreationDate().getHour(), vappRecord.getCreationDate().getMinute());
	        	  }
	          }
	        
	          String ipAddress ="";
	          
			List<VM> vms = newVapp.getChildrenVms();
			for (Iterator iterator2 = vms.iterator(); iterator2.hasNext(); ) {
				try {
					VM vm = (VM) iterator2.next();
					String vmId = vm.getResource().getHref();
					String vmName = vm.getResource().getName() +" ("+newVapp.getResource().getName()+")";
					boolean isDeployed = vm.isDeployed();
					int noOfCpus = vm.getCpu().getNoOfCpus();
					int memorySizeGB = vm.getMemory().getMemorySize().intValue()/1024;
					String vmStatus = vm.getVMStatus().name();
					
					logger.info("Name ="+vm.getResource().getName()+" isDeployed = " 
							+ vm.isDeployed() + " , NoOfCpus = "
									+ vm.getCpu().getNoOfCpus() + " , Memory Size= " + vm.getMemory().getMemorySize()
									+ " , Status= " + vm.getVMStatus());
						
							HashMap<Integer, String>  ipAddreses = vm.getIpAddressesById();
									Set<Integer> keys = ipAddreses.keySet();
							for (Iterator iterator25 = keys.iterator(); iterator25.hasNext();) {
								Integer integer = (Integer) iterator25.next();
								System.out.println("ipAddreses.get(integer) "+ipAddreses.get(integer));
								ipAddress=ipAddress+ipAddreses.get(integer)+",";
							}
							
						ipAddress = StringUtils.removeEnd(ipAddress, ",");
							
							List<VirtualNetworkCard> cards = vm.getNetworkCards();
							for (Iterator iterator3 = cards.iterator(); iterator3.hasNext();) {
								VirtualNetworkCard virtualNetworkCard = (VirtualNetworkCard) iterator3.next();
								System.out.println("virtualNetworkCard.getIpAddress() = "+virtualNetworkCard.getIpAddress());
							}
							
							String platformVendor = "";
							String platformVersion ="";
							
							try {
								if(vm.getPlatformSection() !=null){
									System.out.println("vm.getPlatformSection().getVendor() = "+vm.getPlatformSection().getVendor().getValue());
									System.out.println("vm.getPlatformSection().getVersion() = "+vm.getPlatformSection().getVersion().getValue());
									platformVendor = vm.getPlatformSection().getVendor().getValue();
									platformVersion = vm.getPlatformSection().getVersion().getValue();
								}	
							} catch (Exception e) {
								//e.printStackTrace();
							}
							
							String osName = "";
							String osVersion ="";
							try {
								if(vm.getOperatingSystemSection() !=null){
									System.out.println("vm.getOperatingSystemSection().getDescription() = "+vm.getOperatingSystemSection().getDescription().getValue());
									System.out.println("vm.getOperatingSystemSection().getVersion() = "+vm.getOperatingSystemSection().getVersion());
									osName=vm.getOperatingSystemSection().getDescription().getValue();
									osVersion = vm.getOperatingSystemSection().getVersion();
								}	
							} catch (Exception e) {
								//e.printStackTrace();
							}
							
							List<VirtualDisk> vdisks = vm.getDisks();
							int diskSizeGB = 0;
							String instanceType="";
							try {
							for (Iterator iterator5 = vdisks.iterator(); iterator5.hasNext();) {
								VirtualDisk virtualDisk = (VirtualDisk) iterator5.next();
								if (virtualDisk.isHardDisk()) {
									System.out.println("Disk for " + vm.getReference().getName() + " , virtualDisk.getHardDiskBusType() = "
											+ virtualDisk.getHardDiskBusType() + " , virtualDisk.getHardDiskSize() = " + virtualDisk.getHardDiskSize());
									diskSizeGB = diskSizeGB +(virtualDisk.getHardDiskSize().intValue()/1024);
								}
							}
							
							} catch (Exception e) {
								// TODO: handle exception
							}	
							instanceType = "vCloud (RAM "+memorySizeGB+" GB, CPU "+noOfCpus+" , HD "+diskSizeGB+" GB)";
							
							
							instanceLocal.setImageId(newVapp.getResource().getName());
							instanceLocal.setDnsName(ipAddress);
							instanceLocal.setState(vmStatus);
							instanceLocal.setKeyName("no_key_vmware");
							instanceLocal.setInstanceType(instanceType);
							instanceLocal.setPlatform(osName);
							instanceLocal.setPrivateDnsName(ipAddress);
							instanceLocal.setLaunchTime(vappCreationDate);
							instanceLocal.setStateCode(vmStatus);
							instanceLocal.setPrivateIpAddress(ipAddress);
							instanceLocal.setIpAddress(ipAddress);
							instanceLocal.setArchitecture(osName);
							instanceLocal.setVirtualizationType(platformVendor);
							instanceLocal.setState(Commons.REQUEST_STATUS.running + "");
							instanceLocal = instanceLocal.merge();

					logger.info("\t" + vmId + "\t" + vmId + "\t" + vmStatus + "\t"  
							+ vappCreationDate + "\t " + memorySizeGB+" GB "+noOfCpus+" CPUs " + osName);
					
				}catch(Exception e){e.printStackTrace();}
				}//end for

			

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
						+ " and assigning ip " + ipAddress);
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
			
		}

	}// work

}
