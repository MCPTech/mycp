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
package in.mycp.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.Deployment;
import org.jbpm.api.DeploymentQuery;
import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.ManagementService;
import org.jbpm.api.NewDeployment;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.history.HistoryActivityInstance;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.job.Job;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
@Service
@Transactional
public class WorkflowImpl4Jbpm  {
	
	Log log = LogFactory.getLog(WorkflowImpl4Jbpm.class);
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ExecutionService executionService;
	@Autowired
    private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ManagementService managementService;
	
	private List<String> processDefinitions;

    public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setExecutionService(ExecutionService executionService) {
		this.executionService = executionService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	
    public void setManagementService(ManagementService managementService) {
		this.managementService = managementService;
	}

	public void setProcessDefinitions(List<String> definitions) {
        this.processDefinitions = definitions;
        
        setupProcessDefinitions();
    }
	
	public Date findStartTime(String processInstanceId) {
    	
        try {
        	
        	return historyService
      			  .createHistoryProcessInstanceQuery()
      			  .processInstanceId(processInstanceId)
      			  .uniqueResult().getStartTime();
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        
        }
        return null;
	}
	
	public Date findEndTime(String processInstanceId) {
    	
        try {
        	
        	return historyService
      			  .createHistoryProcessInstanceQuery()
      			  .processInstanceId(processInstanceId)
      			  .uniqueResult().getEndTime();
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        
        }
        return null;
	}
	
	
	
    public ProcessInstance findProcessInstance(String processInstanceId) {
    	
        try {
        	
        	return executionService.findProcessInstanceById(processInstanceId);
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        }
        
        return null;
	}
	
	
    public void endProcessInstance(String processInstanceId) {
    	
        try {
        	executionService.endProcessInstance(processInstanceId, "Rejected");
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        
        }
	}
	
	
    public ProcessInstance createProcessInstance(String processDefnKey) {
        try {
        	ProcessInstance pi = executionService.startProcessInstanceByKey(processDefnKey);
        	return pi;
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        
        }
        return null;
	}
    
    public ProcessInstance createProcessInstance(String processDefnKey, Map<String, Object> variables) {
        try {
        	ProcessInstance pi = executionService.startProcessInstanceByKey(processDefnKey, variables);
        	return pi;
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        }
        return null;
	}
	
    public ProcessInstance moveProcessInstance(String processInstanceId,String transition) {
	    try {
	    	return executionService.signalExecutionById(processInstanceId, transition);
		}catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
		}
        return null;
	}
    
    public ProcessInstance moveProcessInstance(String processInstanceId,String transition, Map<String, Object> variables) {
        try {
        	return executionService.signalExecutionById(processInstanceId, transition, variables);
        }catch(org.jbpm.api.JbpmException jbmEx){
        	jbmEx.printStackTrace();
        	//: could not send email
        	
        	return executionService.signalExecutionById(processInstanceId, transition);
        	
    	}catch(Exception e){
    		log.error(e.getMessage());//e.printStackTrace();
    	}
        return null;
	}
	
    public void cleanupAllProcessDefinitions() {
    	
        try {
        	DeploymentQuery dq = repositoryService.createDeploymentQuery();
        	List<Deployment> deployments = dq.list();
        	for (Iterator iterator = deployments.iterator(); iterator.hasNext();) {
				Deployment deployment = (Deployment) iterator.next();
				
				repositoryService.deleteDeploymentCascade(deployment.getId());
			}
        }catch(Exception e){log.error(e.getMessage());//e.printStackTrace();
        }
	}

    public void testProcessDefinitions() {
		/*ProcessInstance pi = createProcessInstance("Image_Request");
		System.out.println("pi.getId() = " + pi.getId());
		System.out.println("pi = " + pi);*/
		//Image_Request.80048
		//Image_Request.80050
		
		ProcessInstance pi = findProcessInstance("Image_Request.80050");
		
		
		Set<String> activityNames = pi.findActiveActivityNames();
		for (Iterator iterator = activityNames.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			//System.out.println("before move activityNames = " + string);
		}
		
		pi = moveProcessInstance(pi.getId(), "Reject");
		
		activityNames = pi.findActiveActivityNames();
		for (Iterator iterator = activityNames.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			
		}
	}
	
	
    public void setupProcessDefinitions() {
    	
        try {
        	
        	PathMatchingResourcePatternResolver matchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        	Resource resource[]= matchingResourcePatternResolver.getResources("classpath*:jbpm/**/*.jpdl.xml");
        	
        	if(resource !=null && resource.length>0){
        		for (int i = 0; i < resource.length; i++) {
        			
        			NewDeployment deployment = repositoryService.createDeployment();
        			deployment.addResourceFromUrl(resource[i].getURL());
    	            deployment.deploy();
    				
    	            
    			}
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	log.info("IOException occurred: ", e);
            throw new RuntimeException("An error occured while trying to deploy a process definition", e);
        }
    }
	
	
	
	public List<Task> findPersonalTasks(String taskOwnerName) {
		return taskService.findPersonalTasks(taskOwnerName);
	}

	
	public void completeTask(String strTaskId, String outcome, Map<String, Object> variables) {
		taskService.completeTask(strTaskId, outcome, variables);
	}

	
	public Execution findExecutionById(String strExecutionId) {
		return executionService.findExecutionById(strExecutionId);
	}

	
	public List<HistoryActivityInstance> getProcessHistory(String jbpmProcessId) {
		if(StringUtils.isNotBlank(jbpmProcessId))
			return historyService.createHistoryActivityInstanceQuery().processInstanceId(jbpmProcessId).list();
		else
			return null;
	}

	
	public ProcessInstance startProcessInstanceByKey(String strProcessName, Map<String, Object> variables) {
		try{
			return executionService.startProcessInstanceByKey(strProcessName, variables);	
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return null;
	}

	
	public List<Task> findAllActiveTasks() {
		return taskService.createTaskQuery().list();
	}
	
	
	public Job findJobByProcessId(String jbpmProcessId){
		List<Job> jobs = managementService.createJobQuery().processInstanceId(jbpmProcessId).list();
		if(jobs != null && jobs.size()>0)
			return jobs.get(0);
		else
			return null;
	}
	
	
	public HistoryTask getTaskHistory(String executionId){
		return historyService.createHistoryTaskQuery().executionId(executionId).uniqueResult();
	}
	
	
	public void saveTask(Task task){
		taskService.saveTask(task);
	}
	
	
	public Task findActiveTaskByProcessId(String jbpmProcessId){
		List<Task> lstTasks = taskService.createTaskQuery().processInstanceId(jbpmProcessId).list();
		if(lstTasks != null && lstTasks.size()>0)
			return lstTasks.get(0);
		else
			return null;
	}
	
	
	public boolean isProcessInstanceRuning(String name) {
		boolean isRunning = false;
		List<Job> jobs =  managementService.createJobQuery().list();
		for (Iterator iterator = jobs.iterator(); iterator.hasNext();) {
			Job job = (Job) iterator.next();
			String pdId = job.getExecution().getProcessDefinitionId();
			if(pdId.startsWith(name)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
