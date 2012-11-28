<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/VolumeInfoP.js'></script>
<script type='text/javascript' src='/dwr/interface/ZoneService.js'></script>
<script type='text/javascript' src='/dwr/interface/InstanceP.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
  
	var popupStatus_volume = 0;

 function loadPopup_volume(popup,backgroundPopup){
 		if(popupStatus_volume==0){
 			backgroundPopup.css({	"opacity": "0.7"		});
 			backgroundPopup.fadeIn("slow");
 			popup.fadeIn("slow");
 			popupStatus_volume = 1;
 		}
 	}

 function disablePopup_volume(popup,backgroundPopup){
 		if(popupStatus_volume==1){
 			backgroundPopup.fadeOut("slow");
 			popup.fadeOut("slow");
 			popupStatus_volume = 0;
 		}
 	}
 	
 function centerPopup_volume(popup,backgroundPopup){
	 
 		var windowWidth = document.documentElement.clientWidth;
 		var windowHeight = document.documentElement.clientHeight;
 		var popupHeight = popup.height();
 		var popupWidth = popup.width();
 		popup.css({
 			"position": "absolute",
 			"top": windowHeight/2-popupHeight/2,
 			"left": windowWidth/2-popupWidth/2
 		});
 		backgroundPopup.css({	"height": windowHeight	});
 	}

 
	var start = 0;
	var max = 17;
	

	function findAll_volume(p){
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
	            { "sTitle": "Volume Id" },
	            { "sTitle": "Size(GB)" },
	            { "sTitle": "Create Time" },
	            { "sTitle": "Status" },
	            { "sTitle": "Details" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			var actions=
				'<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_volume('+p[i].id+')>&nbsp;&nbsp;'+
            	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_volume('+p[i].id+')>';
            	
            	if('available'==p[i].status){
            		p[i].status='<img title="available" alt="available" src=/images/available.png>&nbsp;';
            		
            		actions=
	            		'<img class="clickimg" title="Remove" alt="Remove" src=../images/remove.png onclick=delete_volume('+p[i].id+')>&nbsp;&nbsp;'+
	    				'<img class="clickimg" title="Attach" alt="Attach" src=../images/attach.png onclick=selectAttach_volume('+p[i].id+')>&nbsp;&nbsp;';
                	
            	}else if('in-use'==p[i].status ||
            			'attached'==p[i].status){
            		p[i].status='<img  title="in-use" alt="in-use" src=/images/running.png>&nbsp;';
            		
            		actions=
            			'<img class="clickimg" title="Detach" alt="Detach" src=../images/detach.png onclick=detach_volume('+p[i].id+')>&nbsp;&nbsp;';
            	}else if('deleted'==p[i].status){
            		p[i].status='<img  title="deleted" alt="deleted" src=/images/terminated.png>&nbsp;';
            		actions=
                    	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_volume('+p[i].id+')>';
            	}else if('creating'==p[i].status){
            		p[i].status='<img  title="Starting" alt="Starting" src=/images/preloader.gif>&nbsp;';
            		actions='';
            	}else if('PENDING_APPROVAL' == p[i].status && p[i].volumeId == null){
            		p[i].status='<img title="Pending Approval" alt="Pending Approval" src=/images/pending.png>&nbsp;';
            		actions=
                    	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_volume('+p[i].id+')>';
            	}else if('FAILED' == p[i].status){
            		p[i].status='<img title="failed" alt="failed" src=/images/failed.png>&nbsp;';
            		actions=
                    	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_volume('+p[i].id+')>';
            	}else if('APPROVAL_REJECTED' == p[i].status){
            		p[i].status='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
            		actions=
                    	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_volume('+p[i].id+')>';
            	}
            	
            	
            	
				
			oTable.fnAddData( [start+i+1,p[i].name,p[i].asset.project.name,p[i].volumeId, p[i].size+' (GB)',
			                   dateFormat(p[i].createTime,"mmm dd yyyy HH:MM:ss"),p[i].status,p[i].details,p[i].asset.productCatalog.infra.name,
			                  actions ] );
		}
	}

	var viewed_volume = -1;	
$(function(){
		$("#popupbutton_volume").click(function(){
			viewed_volume = -1;
			centerPopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
			//loadPopup_volume();
			loadPopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		});
	
		
		$("#popupbutton_volumelist").click(function(){
				dwr.engine.beginBatch();
				start =0;
				$('#SearchField').val('');
				VolumeInfoP.findAllWithAttachInfo(start,max,'',findAll_volume);
			  dwr.engine.endBatch();
		} );
		
		$("#popupbutton_previous").click(function(){
			if(start>16){
				start=start -17;
			}
			var text2Search = dwr.util.getValue("SearchField");
			VolumeInfoP.findAllWithAttachInfo(start,max,text2Search,findAll_volume);
		} );
		
		$("#popupbutton_next").click(function(){
			start = start +17;
			var text2Search = dwr.util.getValue("SearchField");
			VolumeInfoP.findAllWithAttachInfo(start,max,text2Search,findAll_volume);
		} );
		
		$("#popupbutton_search").click(function(){
			
			var text2Search = dwr.util.getValue("SearchField");
			start = 0;
			VolumeInfoP.findAllWithAttachInfo(start,max,text2Search,findAll_volume);
			
		} );
		
		});

	$("#popupContactClose_volume").click(function(){
			viewed_volume = -1;
			disablePopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		});
		
		$("#backgroundPopup_volume").click(function(){
			viewed_volume = -1;
			disablePopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		});
		
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_volume==1){
				disablePopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_volumelist").click();
			
			
			ZoneService.findAll(function(p){
				dwr.util.removeAllOptions('zone');
				dwr.util.addOptions('zone', p, 'name', 'name');
				//dwr.util.setValue(id, sel);
			});
			
			VolumeInfoP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			InstanceP.findAll(0,100,'',function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
				dwr.util.removeAllOptions('instanceId');
				dwr.util.addOptions('instanceId', p, 'instanceId', function(p) {
					return p.name+' '+p.instanceId+' '+p.dnsName;
				});
				//dwr.util.setValue(id, sel);
			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_volume(form);
					 return false;
				 }
				});
			
			$("#thisform_attach").validate({
				 submitHandler: function(form) {
					 submitForm_volume_attach(form);
					 return false;
				 }
				});
			
			ProjectService.findAll(function(p){
				dwr.util.removeAllOptions('projectId');
				//dwr.util.addOptions('project', p, 'id', 'name');
				dwr.util.addOptions('projectId', p, 'id', function(p) {
					return p.name+' @ '+p.department.name;
				});
				//dwr.util.setValue(id, sel);
				
			});
		});
		
	function submitForm_volume(f){
		CommonService.getSessionMsg(function(p){   $.sticky(p);  });
		var volumeinfop = {  id:viewed_volume,name:null, size:null, zone:null,product:null, projectId:null };
		  dwr.util.getValues(volumeinfop);
		  volumeinfop.projectId=dwr.util.getValue("projectId");
		  volumeinfop.zone=dwr.util.getValue("zone");
		  if(viewed_volume == -1){
			  volumeinfop.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  VolumeInfoP.saveOrUpdate(volumeinfop,afterSave_volume);
		  dwr.engine.endBatch();
		  disablePopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		  viewed_volume=-1;
	}
	
	function submitForm_volume_attach(f){
		var volumeinfop = {  id:viewed_volume,volumeId:null, device:null, instanceId:null, projectId:null };
		  dwr.util.getValues(volumeinfop);
		  //volumeinfop.zone=dwr.util.getValue("zone");
		  if(viewed_volume == -1){
			  volumeinfop.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  VolumeInfoP.attachVolume(volumeinfop,afterSave_volume_attach);
		 	//Permission.findById(3);
		 // Permission.findAll(); 
		  dwr.engine.endBatch();
		  disablePopup_volume($("#popupContact_volume_attach"),$("#backgroundPopup_volume_attach"));
		  viewed_volume=-1;
	}
	
	
	function cancelForm_volume_attach(f){
		
		var volumeinfop = {  id:null,volumeId:null, device:null, instanceId:null};
		  dwr.util.setValues(volumeinfop);
		  viewed_volume = -1;
		  disablePopup_volume($("#popupContact_volume_attach"),$("#backgroundPopup_volume_attach"));
	}
	
	function cancelForm_volume(f){
	
		var volumeinfop = {  id:null,name:null, size:null, zone:null,product:null };
		  dwr.util.setValues(volumeinfop);
		  viewed_volume = -1;
		  disablePopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
	}
	
	function afterEdit_volume(p){
		var volumeinfop = eval(p);
		viewed_volume=p.id;
		centerPopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		loadPopup_volume($("#popupContact_volume"),$("#backgroundPopup_volume"));
		dwr.util.setValues(volumeinfop);
		dwr.util.setValue('zone',volumeinfop.zone.name);
		dwr.util.setValue('product',volumeinfop.product.id);
		
	}
	
	function afterSelectAttach_volume(p){
		//alert(dwr.util.toDescriptiveString(p,3));
		var volumeinfop = eval(p);
		viewed_volume=p.id;
		
		centerPopup_volume($("#popupContact_volume_attach"),$("#backgroundPopup_volume_attach"));
		loadPopup_volume($("#popupContact_volume_attach"),$("#backgroundPopup_volume_attach"));
		dwr.util.setValues(volumeinfop);
		dwr.util.setValue('product',volumeinfop.instanceId);
		 
	}
	
	
	function edit_volume(id){
		VolumeInfoP.findById(id,afterEdit_volume);
	}
	
	function remove_volume(id){
		if(!disp_confirm('Volume')){
			return false;
		}
		dwr.engine.beginBatch();
		VolumeInfoP.remove(id,afterSave_volume);
		dwr.engine.endBatch();
	}
	function afterSave_volume(){
		viewed_volume = -1;
		$("#popupbutton_volumelist").click();
		}
	
	function afterSave_volume_attach(){
		viewed_volume = -1;
		$("#popupbutton_volumelist").click();
		$.sticky('<b>Volume scheduled to be attached.</b><p>');
		}
	
	function delete_volume(id){
		dwr.engine.beginBatch();
		VolumeInfoP.deleteVolume(id,afterDelete_volume);
		dwr.engine.endBatch();
	}
	
	function afterDelete_volume(){
		viewed_volume = -1;
		$("#popupbutton_volumelist").click();
		$.sticky('<b>Volume scheduled to be removed.</b><p>');
		}
	
	function detach_volume(id){
		//alert(id);
		dwr.engine.beginBatch();
		VolumeInfoP.detachVolume(id,afterDetach_volume);
		dwr.engine.endBatch();
	}
	
	function afterDetach_volume(){
		viewed_volume = -1;
		$("#popupbutton_volumelist").click();
		$.sticky('<b>Volume scheduled to be detached.</b><p>');
		}
	
	function selectAttach_volume(id){
		VolumeInfoP.findById(id,afterSelectAttach_volume);
	}
	

	
	
</script>
<p class="dataTableHeader">Volume Resource</p>
				<div style="width: 300px;float: right;"> 
						<div style="float: left; padding-top: 5px; width: 170px;"> <input type="text" name="SearchField" id="SearchField"  ></div>
						 
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
							<div class="demo" id="popupbutton_volume" style="float: left; padding-left: 10px;"><button>Request New Volume</button></div>
							<div class="demo" id="popupbutton_volumelist" style="float: left; padding-left: 10px;"><button>List All Volumes</button></div>
							
							<div style="width: 200px;float: right;"> 
								<div class="demo" id="popupbutton_previous" style="float: left;  width: 90px;"><button>Previous</button></div>
								<div class="demo" id="popupbutton_next" style="float: left; "><button>Next</button></div>
							</div>
						
						</div>
						<table align="right" border="0" width="100%">
							<tr>
								<td width="80%">
								
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_volume"><button>Request Volume</button></div> -->
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_volumelist"><button>List Volumes</button></div> -->
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_volume" >
		<div id="popupContact_volume" class="popupContact" >
							<a  onclick="cancelForm_volume();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Volume</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_volume" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								<tr>
								    <td style="width: 50%;">Product : </td>
								    <td style="width: 50%;">
								    <select id="product" name="product" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Size(GB) : </td>
								    <td style="width: 50%;"><input type="text" name="size" id="size" size="30" class="required number"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Zone : </td>
								    <td style="width: 50%;"> 
								    <select id="zone" name="zone" style="width: 205px;" class="required">
							    	</select></td>
								  </tr>
								   
								  <tr>
								    <td style="width: 20%;">project : </td>
								    <td style="width: 80%;">
								    <select id="projectId" name="projectId" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_volume_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_volume(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_volume" class="backgroundPopup" ></div>
		
		<div id="popupContact_volume_attach" class="popupContact" >
							<a  onclick="cancelForm_volume_attach();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Volume Attach</h1>
							<form class="cmxform" id="thisform_attach" method="post" name="thisform_attach">
								<p id="contactArea_volume" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Volume : </td>
								    <td style="width: 50%;"><input type="text" name="volumeId" id="volumeId" size="30" class="required" readonly="readonly"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Device : </td>
								    <td style="width: 50%;"><input type="text" name="device" id="device" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Instance : </td>
								    <td style="width: 50%;"> 
									    <select id="instanceId" name="instanceId" style="width: 205px;" class="required">
								    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_volume_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
										<button onclick="cancelForm_volume_attach(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_volume_attach" class="backgroundPopup" ></div>
	</div>				