<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/WorkflowService.js'></script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_workflow = 0;

	//loading popup with jQuery magic!
	function loadPopup_workflow(){
		//loads popup only if it is disabled
		if(popupStatus_workflow==0){
			$("#backgroundPopup_workflow").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_workflow").fadeIn("slow");
			$("#popupContact_workflow").fadeIn("slow");
			popupStatus_workflow = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_workflow(){
		//disables popup only if it is enabled
		if(popupStatus_workflow==1){
			$("#backgroundPopup_workflow").fadeOut("slow");
			$("#popupContact_workflow").fadeOut("slow");
			popupStatus_workflow = 0;
		}
	}

	//centering popup
	function centerPopup_workflow(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_workflow").height();
		var popupWidth = $("#popupContact_workflow").width();
		//centering
		$("#popupContact_workflow").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_workflow").css({
			"height": windowHeight
		});
		
	}

	function findAll_workflow(p){
		/* alert(p.length);
		alert(p[0].imageId); */
	//alert(dwr.util.toDescriptiveString(p,3));
		
		//var tableData = eval( dwr.util.toDescriptiveString(p,3) );
		//var continents = arrayAsJSONText.parseJSON();
		//alert(tableData);
		//alert(tableData[0].id+tableData[0].name);

		 oTable = $('#compute-table').dataTable( {
	    	"sPaginationType": "full_numbers",
	    	"bDestroy": true,
	    	"bAutoWidth": false,
	    	"bDeferRender": true,
	    	"bJQueryUI": false,
	    	"bLengthChange": false,
	    	"iDisplayLength": 17,
	        "aaData": [
	        ],
	        "aoColumns": [
	            { "sTitle": "#" },
	            { "sTitle": "Workflow" },
	            { "sTitle": "User" },
	            { "sTitle": "Status" },
	            { "sTitle": "Start" },
	            { "sTitle": "Asset" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			oTable.fnAddData( [i+1,p[i].processName,p[i].user.email, p[i].processStatus, 
			                   dateFormat(p[i].startTime,"mmm dd yyyy HH:MM:ss")
			                   , p[i].assetDetails,
			                   '<img class="clickimg" title="Approve" alt="Approve" src=../images/approve.png onclick=approve_workflow(\''+p[i].processId+'\')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Reject" alt="Reject" src=../images/reject.png onclick=reject_workflow(\''+p[i].processId+'\')>' ] );
		}
		
		
	
	}

	var viewed_workflow = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_workflow").click(function(){
			viewed_workflow = -1;
			//centering with css
			centerPopup_workflow();
			//load popup
			loadPopup_workflow();
		});
	
		$("#popupbutton_workflowlist").click(function(){
			
				dwr.engine.beginBatch();
				WorkflowService.findAll(findAll_workflow);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_workflow").click(function(){
			viewed_workflow = -1;
			disablePopup_workflow();
		});
		//Click out event!
		$("#backgroundPopup_workflow").click(function(){
			viewed_workflow = -1;
			disablePopup_workflow();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_workflow==1){
				disablePopup_workflow();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_workflowlist").click();
		});
		
function submitForm_workflow(f){
	var workflow = {  id:viewed_workflow,name:null, url:null };
	  dwr.util.getValues(workflow);
	  
	  if(viewed_workflow == -1){
		  workflow.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  WorkflowService.saveOrUpdate(workflow,afterSave_workflow);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_workflow();
	  viewed_workflow=-1;
}
function cancelForm_workflow(f){

	var workflow = {  id:null,name:null, url:null };
	  dwr.util.setValues(workflow);
	  viewed_workflow = -1;
	  disablePopup_workflow();
}

function afterEdit_workflow(p){
	var workflow = eval(p);
	viewed_workflow=p.id;
	centerPopup_workflow();
	loadPopup_workflow();
	dwr.util.setValues(workflow);
}

function edit_workflow(id){
	WorkflowService.findById(id,afterEdit_workflow);
}

function remove_workflow(id){
	if(!disp_confirm('Workflow')){
		return false;
	}
	dwr.engine.beginBatch();
	WorkflowService.remove(id,afterSave_workflow);
	dwr.engine.endBatch();
}
function afterSave_workflow(){
	viewed_workflow = -1;
	$("#popupbutton_workflowlist").click();}
	
function approve_workflow(processId){
	//alert(processId);
	dwr.engine.beginBatch();
	WorkflowService.moveProcessInstance(processId,'Approve',afterSave_workflow);
	$.sticky('Process approve');
	dwr.engine.endBatch();
}

function reject_workflow(processId){
	dwr.engine.beginBatch();
	WorkflowService.moveProcessInstance(processId,'Reject',afterSave_workflow);
	$.sticky('Process reject');
	dwr.engine.endBatch();
}

</script>

<p class="dataTableHeader">Approval Control</p>
<div id="datatable-iaas-parent" class="infragrid2">
					<div id="datatable-iaas" >
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="compute-table">
							<thead><tr></tr></thead>
							<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
							</tfoot><tbody></tbody>
						</table>
						<div style="height: 50px;"></div>
						<table align="right" border="0" width="100%">
							<tr>
								<td width="70%">
								
								</td>
								<td width="15%">
									<!-- <div class="demo" id="popupbutton_workflow"><button>Request Approval</button></div> -->
								</td>
								<td width="15%">
									<div class="demo" id="popupbutton_workflowlist"><button>List Approvals</button></div>
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_workflow" >
		<div id="popupContact_workflow" class="popupContact" >
							<a  onclick="cancelForm_workflow();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form method="post" name="thisform">
								<p id="contactArea_workflow" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Platform/Image : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Size : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Key : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url"></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Base Infra : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Security Group : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_workflow_create">
											<!-- <button onclick="submitForm_workflow(this.form);return false;">Save</button>&nbsp;&nbsp;&nbsp;&nbsp; -->
											<button onclick="cancelForm_workflow(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_workflow" class="backgroundPopup" ></div>
	</div>				