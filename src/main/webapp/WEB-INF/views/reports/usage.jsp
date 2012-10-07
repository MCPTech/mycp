<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/ReportService.js'></script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_report = 0;

	//loading popup with jQuery magic!
	function loadPopup_report(){
		//loads popup only if it is disabled
		if(popupStatus_report==0){
			$("#backgroundPopup_report").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_report").fadeIn("slow");
			$("#popupContact_report").fadeIn("slow");
			popupStatus_report = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_report(){
		//disables popup only if it is enabled
		if(popupStatus_report==1){
			$("#backgroundPopup_report").fadeOut("slow");
			$("#popupContact_report").fadeOut("slow");
			popupStatus_report = 0;
		}
	}

	//centering popup
	function centerPopup_report(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_report").height();
		var popupWidth = $("#popupContact_report").width();
		//centering
		$("#popupContact_report").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_report").css({
			"height": windowHeight
		});
		
	}

	function findAll_report(p){
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
	            { "sTitle": "Name" },
	            { "sTitle": "Details" },
	            { "sTitle": "Start" },
	            { "sTitle": "End" },
	            { "sTitle": "Duration(Hr)" },
	            { "sTitle": "Rate" },
	            { "sTitle": "Cost" }
	            
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var endTime = '';
			if(p[i].endTime !=null){
				endTime = dateFormat(p[i].endTime,"mmm dd yyyy HH:MM:ss");
			}
			
			oTable.fnAddData( [i+1,p[i].assetTypeName,p[i].assetDetails, dateFormat(p[i].startTime,"mmm dd yyyy HH:MM:ss")
			                   ,endTime, p[i].duration,p[i].startRate+' '+p[i].currency,p[i].cost+' '+p[i].currency] );
			                   
		}
		
		
	
	}

	var viewed_report = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_report").click(function(){
			viewed_report = -1;
			//centering with css
			centerPopup_report();
			//load popup
			loadPopup_report();
		});
	
		$("#popupbutton_reportlist").click(function(){
			
				dwr.engine.beginBatch();
				ReportService.findAllShort(findAll_report);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_report").click(function(){
			viewed_report = -1;
			disablePopup_report();
		});
		//Click out event!
		$("#backgroundPopup_report").click(function(){
			viewed_report = -1;
			disablePopup_report();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_report==1){
				disablePopup_report();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_reportlist").click();
		});
		
function submitForm_report(f){
	var workflow = {  id:viewed_report,name:null, url:null };
	  dwr.util.getValues(workflow);
	  
	  if(viewed_report == -1){
		  workflow.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  ReportService.saveOrUpdate(workflow,afterSave_report);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_report();
	  viewed_report=-1;
}
function cancelForm_report(f){

	var workflow = {  id:null,name:null, url:null };
	  dwr.util.setValues(workflow);
	  viewed_report = -1;
	  disablePopup_report();
}

function afterEdit_report(p){
	var workflow = eval(p);
	viewed_report=p.id;
	centerPopup_report();
	loadPopup_report();
	dwr.util.setValues(workflow);
}

function edit_report(id){
	ReportService.findById(id,afterEdit_report);
}

function remove_report(id){
	if(!disp_confirm('Report')){
		return false;
	}
	dwr.engine.beginBatch();
	ReportService.remove(id,afterSave_report);
	dwr.engine.endBatch();
}
function afterSave_report(){
	viewed_report = -1;
	$("#popupbutton_reportlist").click();}

</script>

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
								<td width="80%">
								<font color="green">Reports -- Usage</font>
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_report"><button>Deploy Process</button></div> -->
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_reportlist"><button>Refresh Report</button></div> 
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_report" >
		<div id="popupContact_report" class="popupContact" >
							<a  onclick="cancelForm_report();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form method="post" name="thisform">
								<p id="contactArea_report" class="contactArea" >
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
										<div class="demo" id="popupbutton_report_create">
											<button onclick="submitForm_report(this.form);return false;">Save</button>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_report(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_report" class="backgroundPopup" ></div>
	</div>				