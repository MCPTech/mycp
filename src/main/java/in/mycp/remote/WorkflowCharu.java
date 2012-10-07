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

package in.mycp.remote;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
@RemoteProxy(name = "workflowCharu")
public class WorkflowCharu {

	private static final Logger log = Logger.getLogger(WorkflowCharu.class
			.getName());

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");

	// TODO - put in workflow service here

	/*
	 * public List<ProcessDefinition> listAllProcessDefinitions() throws
	 * IOException { try { List<ProcessDefinition> allProcDefns =
	 * workflowService.listAllProcDefns(); return allProcDefns; } catch
	 * (Exception e) { e.printStackTrace(); } return null; }
	 * 
	 * 
	 * public List<Workflow> listAllWorkflows() throws ServletException,
	 * IOException { try { List<Workflow> workflows =
	 * WorkflowHomeVar.findAllActive(); if(workflows == null || workflows.size()
	 * ==0){ return null; } for (Iterator iterator = workflows.iterator();
	 * iterator.hasNext();) { Workflow workflow = (Workflow) iterator.next();
	 * WorkflowDetail wd =
	 * workflowService.getProcessInstanceDetails(workflow.getProcessId());
	 * if(!wd.isRunning()){ wd.setTransitions(""); }
	 * workflow.setWorkflowDetail(wd); } return workflows; } catch (Exception e)
	 * { e.printStackTrace(); } return null; }
	 * 
	 * public ProcessInstance start(String pname) throws IOException { try {
	 * return workflowService.startProcess(pname); } catch (Exception e) {
	 * e.printStackTrace(); } return null; }
	 * 
	 * public void next(String workflowId, String transitionName) throws
	 * IOException { try { Workflow wf =
	 * WorkflowHomeVar.findById(Integer.parseInt(workflowId));
	 * workflowService.moveWorkflow( ""+wf.getProcessId().intValue(),
	 * transitionName, "taskDescription", "userId"); WorkflowDetail wfd=
	 * workflowService.getProcessInstanceDetails(wf.getProcessId().intValue());
	 * if(!wfd.isRunning() && transitionName.startsWith("Approve")){
	 * wf.setStatus(""+Constants.WORKFLOW_STATUS.APPROVED); }else
	 * if(!wfd.isRunning() && !transitionName.startsWith("Approve")){
	 * wf.setStatus(""+Constants.WORKFLOW_STATUS.APPROVAL_REJECTED); }else{
	 * wf.setStatus(""+Constants.WORKFLOW_STATUS.PENDING_APPROVAL); }
	 * 
	 * wf = WorkflowHomeVar.saveOrUpdate(wf); //check if this workflow is
	 * approved if(!wfd.isRunning() && transitionName.startsWith("Approve")){
	 * //get the asset associated with this workflow
	 * 
	 * Asset asset = wf.getAsset(); //dunno why , but the aboev does nto get me
	 * the assettype and other objects asset =
	 * AssetHomeVar.findById(asset.getId()); InfraConfig infraConfig = new
	 * InfraConfig(); System.out.println(wf.getAsset().getId()); try{ Set
	 * infraHasProductCatalogs =
	 * asset.getProductCatalog().getInfraHasProductCatalogs(); for (Iterator
	 * iterator = infraHasProductCatalogs.iterator(); iterator .hasNext();) {
	 * InfraHasProductCatalog infraHasProductCatalog = (InfraHasProductCatalog)
	 * iterator.next(); infraConfig =
	 * infraHasProductCatalog.getInfra().getInfraConfig(); break;
	 * 
	 * } }catch(Exception e1){e1.printStackTrace();}
	 * 
	 * if(asset.getAssetType().getName().equals(""+Constants.ASSET_TYPE.
	 * ComputeReservation)){ Set set =
	 * wf.getAsset().getReservationDescriptionPs(); for (Iterator iterator =
	 * set.iterator(); iterator.hasNext();) { ReservationDescriptionP
	 * reservationDescriptionP = (ReservationDescriptionP) iterator.next(); Set
	 * instances = reservationDescriptionP.getInstancePs();
	 * InstancePHomeVar.createCompute(instances, infraConfig); }
	 * 
	 * } }
	 * 
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * 
	 * 
	 * public void end(String worflowId) throws IOException { try { Workflow wf
	 * = WorkflowHomeVar.findById(Integer.parseInt(worflowId));
	 * wf.setStatus(""+Constants.WORKFLOW_STATUS.END);
	 * workflowService.endWorkflow(""+wf.getProcessId().intValue());
	 * WorkflowHomeVar.save(wf); } catch (Exception e) { e.printStackTrace(); }
	 * }
	 * 
	 * public void abort(String worflowId) throws IOException { try { Workflow
	 * wf = WorkflowHomeVar.findById(Integer.parseInt(worflowId));
	 * wf.setStatus(""+Constants.WORKFLOW_STATUS.ABORTED);
	 * workflowService.endWorkflow(""+wf.getProcessId().intValue());
	 * WorkflowHomeVar.save(wf); } catch (Exception e) { e.printStackTrace(); }
	 * }
	 */

}// end of class WorkflowController

