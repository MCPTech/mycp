<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript" src="/js/jqueryplugins/jquery.ui.autocomplete.js"></script>
<script type='text/javascript' src='/dwr/interface/InstanceP.js'></script>
<script type='text/javascript' src='/dwr/interface/KeyPairInfoP.js'></script>
<script type='text/javascript' src='/dwr/interface/ImageDescriptionP.js'></script>
<script type='text/javascript' src='/dwr/interface/GroupDescriptionP.js'></script>
<script type='text/javascript' src='/dwr/interface/ProductService.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_compute = 0;

	//loading popup with jQuery magic!
	function loadPopup_compute(){
		//loads popup only if it is disabled
		if(popupStatus_compute==0){
			$("#backgroundPopup_compute").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_compute").fadeIn("slow");
			$("#popupContact_compute").fadeIn("slow");
			popupStatus_compute = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_compute(){
		//disables popup only if it is enabled
		if(popupStatus_compute==1){
			$("#backgroundPopup_compute").fadeOut("slow");
			$("#popupContact_compute").fadeOut("slow");
			popupStatus_compute = 0;
		}
	}

	//centering popup
	function centerPopup_compute(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_compute").height();
		var popupWidth = $("#popupContact_compute").width();
		//centering
		$("#popupContact_compute").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_compute").css({
			"height": windowHeight
		});
		
	}

	var start = 0;
	var max = 17;
	
	function findAll_compute(p){
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
	        "fnDrawCallback": function() {
                $('.dataTables_paginate').css("display", "none");
                $('.dataTables_length').css("display", "none");
                $('.dataTables_filter').css("display", "none");
                $('.dataTables_info').css("display", "none");
    		},
	        "aoColumns": [
	            { "sTitle": "#" },
	            { "sTitle": "Name" },
	            { "sTitle": "Project" },
	            { "sTitle": "Id" },
	            { "sTitle": "Image" },
	            { "sTitle": "DNS" },
	            { "sTitle": "Key" },
	            { "sTitle": "Security" },
	            { "sTitle": "OS" },
	            { "sTitle": "State" },
	            { "sTitle": "Instance Type" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var state = '';
			if(p[i].state !=null)
				{state = p[i].state;}
			
			var actions =
				'<img class="clickimg" title="Edit" alt="Edit" src=/images/edit.png onclick=edit_compute('+p[i].id+')>&nbsp; '+
                '<img class="clickimg" title="Delete" alt="Remove" src=/images/deny.png onclick=remove_compute('+p[i].id+')>'
            
            if('running' == state || 'RUNNING' == state || 'POWERED_ON' == state){
				state='<img  title="Running" alt="Running" src=../images/running.png>&nbsp;';
				//Euca 2.0 does not impl stop and reboot
				//actions='<img alt="Edit" src=../images/stop.png onclick=stop_compute('+p[i].id+')>&nbsp; '+
                //'<img alt="Edit" src=../images/restart.png onclick=restart_compute('+p[i].id+')>&nbsp; '+
                actions='<img class="clickimg" title="terminate" alt="terminate" src=/images/terminate.png onclick=terminate_compute('+p[i].id+')>&nbsp; ';
                //'<img class="clickimg" title="Delete" alt="Remove" src=/images/deny.png onclick=remove_compute('+p[i].id+')>';
			}else if('STOPPED' == state  || 'POWERED_OFF' == state){
				actions='<img class="clickimg" title="start" alt="start" src=/images/start.png onclick=start_compute('+p[i].id+')>&nbsp; '+
				'<img class="clickimg" title="Delete" alt="Remove" src=/images/deny.png onclick=remove_compute('+p[i].id+')>';
			}else if('TERMINATED' == state || 'terminated' == state){
				state='<img  title="Terminated" alt="Terminated" src=/images/terminated.png>&nbsp;'; 
				actions =
	                '<img class="clickimg" title="Delete" alt="Delete" src=/images/deny.png onclick=remove_compute('+p[i].id+')>'
			}else if('PENDING_APPROVAL' == state && p[i].instanceId != null ){
				state='<img  title="Pending Approval" alt="Pending Approval" src=/images/pending.png>&nbsp;';
				actions =''; 
			}else if('PENDING_APPROVAL' == state && p[i].instanceId == null){
				state='<img class="clickimg" title="Pending Approval" alt="Pending Approval" src=/images/pending.png>&nbsp;';
				 actions =
		                '<img class="clickimg" title="Delete" alt="Delete" src=/images/deny.png onclick=remove_compute('+p[i].id+')>'
			}else if('STARTING' == state || 'pending' == state){
				state='<img  title="Starting" alt="Starting" src=/images/preloader.gif>&nbsp;';
				actions='';
			}else if('FAILED' == state ){
				state='<img  title="Failed" alt="Failed" src=/images/failed.png>&nbsp;';
				actions ='<img class="clickimg" title="Delete" alt="Remove" src=/images/deny.png onclick=remove_compute('+p[i].id+')>'
	            
			}else if('APPROVAL_REJECTED' == state){
				state='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
        		actions=
                	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_compute('+p[i].id+')>';
        	}else{
			//	if('STARTING' == state || 'RESTARTING' == state || 
			//  'TERMINATING' == state  || 'SHUTTING_DOWN' == state){
				//disable all actions here
				actions ='<img class="clickimg" title="Delete" alt="Remove" src=/images/deny.png onclick=remove_compute('+p[i].id+')>'
			}
                
			//alert(state);
        	var projName = '';
			try{
				projName = p[i].asset.project.name;
			}catch(e){}
			oTable.fnAddData( [start+i+1,p[i].name, projName, p[i].instanceId, p[i].imageId, p[i].dnsName,
			                   p[i].keyName,p[i].groupName, p[i].platform, state,
			                   p[i].instanceType,p[i].asset.productCatalog.infra.name,
			                   actions ] );
		}
	
	}

	var viewed_compute = -1;	
$(function(){

	$("#popupbutton_compute").click(function(){
			viewed_compute = -1;
			centerPopup_compute();
			loadPopup_compute();
		});
	
	
		$("#popupbutton_computelist").click(function(){
				dwr.engine.beginBatch();
				start =0;
				$('#SearchField').val('');
				InstanceP.findAll(start,max,'',findAll_compute);
			  dwr.engine.endBatch();
		} );
		
		$("#popupbutton_previous").click(function(){
			if(start>16){
				start=start -17;
			}
			var text2Search = dwr.util.getValue("SearchField");
			InstanceP.findAll(start,max,text2Search,findAll_compute);
		} );
		
		$("#popupbutton_next").click(function(){
			start = start +17;
			//alert(start+'   '+max);
			var text2Search = dwr.util.getValue("SearchField");
			InstanceP.findAll(start,max,text2Search,findAll_compute);
		} );
		
		$("#popupbutton_search").click(function(){
			
			var text2Search = dwr.util.getValue("SearchField");
			start = 0;
			InstanceP.findAll(start,max,text2Search,findAll_compute);
			
		} );
		
		ProjectService.findAll(function(p){
			dwr.util.removeAllOptions('projectId');
			//dwr.util.addOptions('project', p, 'id', 'name');
			dwr.util.addOptions('projectId', p, 'id', function(p) {
				return p.name+' @ '+p.department.name;
			});
			//dwr.util.setValue(id, sel);
			
		});
		
		});
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_compute").click(function(){
			viewed_compute = -1;
			disablePopup_compute();
		});
		//Click out event!
		$("#backgroundPopup_compute").click(function(){
			viewed_compute = -1;
			disablePopup_compute();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_compute==1){
				disablePopup_compute();
			}
		});
		
		
		$(document).ready(function() {
			$("#popupbutton_computelist").click();
			
			InstanceP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			KeyPairInfoP.findAll4List(function(p){
				dwr.util.removeAllOptions('keyName');
				dwr.util.addOptions('keyName', p, 'id', 'keyName');
				//dwr.util.setValue(id, sel);
			});
			/*
			ImageDescriptionP.findAll4List(0,100,function(p){
				
				dwr.util.removeAllOptions('imageId');
				dwr.util.addOptions('imageId', p, 'imageId', function(p) {
					return p.imageId+' @ '+p.imageLocation;
				});
				
			});
			*/
			GroupDescriptionP.findAll4List(function(p){
				dwr.util.removeAllOptions('groupName');
				dwr.util.addOptions('groupName', p, 'name', 'name');
				//dwr.util.setValue(id, sel);
			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_compute(form);
					 return false;
				 }
				});
			
			//$("#imageId").autocomplete("my_autocomplete_backend.php", { minChars:1 });
			
			
			jQuery('#imageId').autocomplete({
			    source : function(request, response) {
			    	var text2Search = $('#imageId').val() ;
			    	ImageDescriptionP.findAll(0,100,text2Search,  function(data) {
			                var arrayOfData = [];
			                for(i = 0;i < data.length;i++){
			                    arrayOfData.push(data[i].imageId+','+data[i].name+','+data[i].imageLocation);
			                }
			                response(arrayOfData);
			            
			        });
			    }
			});
			
			
			
			

			
			
		});
		
		
		
function submitForm_compute(f){
	
	var instancep = {  id:viewed_compute,name:null, reason:null, imageId:null, instanceType:null, keyName:null,groupName:null,product:null, projectId:null };
	  dwr.util.getValues(instancep);
	  var imageStr = dwr.util.getValue("imageId");
	  if(imageStr.indexOf(',')>0){
		  instancep.imageId=imageStr.substring(0,imageStr.indexOf(','));  
	  }
	  
	  if(viewed_compute == -1){
		  instancep.id  = null; 
	  }
	  instancep.keyName=dwr.util.getText("keyName");
	  
	  dwr.engine.beginBatch();
	  if(viewed_compute >0){
		  InstanceP.updateCompute(instancep,afterSave_compute);
	  }else{
		  InstanceP.requestCompute(instancep,afterSave_compute);  
	  }
	  dwr.engine.endBatch();
	  disablePopup_compute();
	  viewed_compute=-1;
}

function cancelForm_compute(f){
	var instancep = {  id:null,name:null, reason:null, imageId:null, instanceType:null, keyName:null,groupName:null,product:null};
	  dwr.util.setValues(instancep);
	  viewed_compute = -1;
	  disablePopup_compute();
}

function afterEdit_compute(p){
	//var instancep = {  id:viewed_compute,name:p.name, reason:p.reason, imageId:p.imageId, 
			//instanceType:p.instanceType, keyName:p.keyName,groupName:p.groupName };
	//var instancep = {  id:viewed_compute,name:null, reason:null, imageId:null, instanceType:null, keyName:null,groupName:null,product:null };
	var instancep = eval(p);
	viewed_compute=p.id;
	centerPopup_compute();
	loadPopup_compute();
	dwr.util.setValues(instancep);
	dwr.util.setValue('keyName',p.keyName);
	dwr.util.setValue('product',p.product.id);
	dwr.util.setValue('imageId',p.imageId);
	dwr.util.setValue('groupName',p.groupName);
	
	//keyName product imageId groupName
}

function edit_compute(id){
	//alert('edit '+id);
	viewed_compute=id;
	InstanceP.findById(id,afterEdit_compute);
}

function remove_compute(id){
	if(!disp_confirm('Server')){
		return false;
	}

	dwr.engine.beginBatch();
	InstanceP.remove(id,afterRemove_compute);
	dwr.engine.endBatch();
}

function afterRemove_compute(){
	viewed_compute = -1;
	$("#popupbutton_computelist").click();
		CommonService.getSessionMsg(function(p){
			$.sticky(p);
		});
		
	}
	
function afterSave_compute(){
	CommonService.getSessionMsg(function(p){
		$.sticky(p);
	});
	viewed_compute = -1;
	$("#popupbutton_computelist").click();
	}

	function terminate_compute(id){
		if(!disp_terminate('Server')){
			return false;
		}
			dwr.engine.beginBatch();
			InstanceP.terminateCompute(id,afterTerminate_compute);
			dwr.engine.endBatch();
		}
	
	function afterTerminate_compute(){
		viewed_compute = -1;
		$("#popupbutton_computelist").click();
		$.sticky("Server scheduled for Termination.");
		}
	
	function stop_compute(id){
			dwr.engine.beginBatch();
			InstanceP.stopCompute(id,afterSave_compute);
			dwr.engine.endBatch();
		}
	
	function restart_compute(id){
			dwr.engine.beginBatch();
			InstanceP.restartCompute(id,afterSave_compute);
			dwr.engine.endBatch();
		}
	
	function start_compute(id){
			dwr.engine.beginBatch();
			InstanceP.startCompute(id,afterSave_compute);
			dwr.engine.endBatch();
		}


</script>   
<p class="dataTableHeader">Compute Resource</p>
					<div style="width: 300px;float: right;"> 
						<div style="float: left; padding-top: 5px; width: 170px;"> <input type="text" name="SearchField" id="SearchField" ></div>
						 
						<div class="demo" id="popupbutton_search" style="float: left; padding-bottom: 10px;"><button>Search</button></div>
					
					</div>
			<div id="datatable-iaas-parent" class="infragrid2">
					<div id="datatable-iaas" >
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="compute-table">
							<thead><tr></tr></thead>
							<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
							</tfoot><tbody></tbody>
						</table>
						<div style="height: 50px;padding-top: 20px;">
							<div class="demo" id="popupbutton_compute" style="float: left; padding-left: 10px;"><button>Request New Compute</button></div>
							<div class="demo" id="popupbutton_computelist" style="float: left; padding-left: 10px;"><button>List All Compute</button></div>
							
							<div style="width: 200px;float: right;"> 
								<div class="demo" id="popupbutton_previous" style="float: left;  width: 90px;"><button>Previous</button></div>
								<div class="demo" id="popupbutton_next" style="float: left; "><button>Next</button></div>
							</div>
						
						</div>
						
						<table align="right" border="0" width="100%">
							<tr>
								<td width="70%">
								
								</td>
								<td width="15%">
									<!-- <div class="demo" id="popupbutton_compute"><button>Request Compute</button></div> -->
								</td>
								<td width="15%">
									<!-- <div class="demo" id="popupbutton_computelist"><button>List Compute</button></div> -->
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_compute" >
		<div id="popupContact_compute" class="popupContact" >
							<a  onclick="cancelForm_compute();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_compute" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								
								<tr>
								    <td style="width: 20%;">Product : </td>
								    <td style="width: 80%;">
								    <select id="product" name="product" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  
								  <tr>
								    <td style="width: 20%;">Name : </td>
								    <td style="width: 80%;"><input type="text" name="name" id="name" size="58" maxlength="90" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Reason : </td>
								    <td style="width: 80%;"><input type="text" name="reason" id="reason" maxlength="90" size="58"></td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Image : </td>
								    <td style="width: 80%;">
								    <input type="text" id="imageId" size="58" class="required">
								    
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Type : </td>
								    <td style="width: 80%;">
								    <select id="instanceType" name="instanceType" style="width: 385px;" class="required">
								    	<option value="m1.small">m1.small</option>
								    	<option value="m1.large">m1.large</option>
								    	<option value="m1.xlarge">m1.xlarge</option>
								    	<option value="c1.medium">c1.medium </option>
								    	<option value="c1.xlarge">c1.xlarge</option>
							    	</select>
							    	</td>
								  </tr>
								  
								  <tr>
								    <td style="width: 20%;">Key : </td>
								    <td style="width: 80%;">
								    <select id="keyName" name="keyName" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								   <!-- <tr>
								    <td style="width: 50%;">Base Infra : </td>
								    <td style="width: 50%;"><input type="text" name="hypervisor" id="hypervisor" size="60"></td>
								  </tr> -->
								  <tr>
								    <td style="width: 20%;">Security Group : </td>
								    <td style="width: 80%;">
								    <select id="groupName" name="groupName" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">project : </td>
								    <td style="width: 80%;">
								    <select id="projectId" name="projectId" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_compute_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_compute(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_compute" class="backgroundPopup" ></div>
	</div>	



