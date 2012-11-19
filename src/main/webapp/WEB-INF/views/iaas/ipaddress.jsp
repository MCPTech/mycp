
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
  <script type='text/javascript' src='/dwr/interface/AddressInfoP.js'></script>
  <script type='text/javascript' src='/dwr/interface/InstanceP.js'></script>
  <script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
  
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
	var popupStatus_ipaddress = 0;

function loadPopup_ipaddress(popup,backgroundPopup){
		if(popupStatus_ipaddress==0){
			backgroundPopup.css({
				"opacity": "0.7"
			});
			backgroundPopup.fadeIn("slow");
			popup.fadeIn("slow");
			popupStatus_ipaddress = 1;
		}
	}

function disablePopup_ipaddress(popup,backgroundPopup){
		if(popupStatus_ipaddress==1){
			backgroundPopup.fadeOut("slow");
			popup.fadeOut("slow");
			popupStatus_ipaddress = 0;
		}
	}
	
function centerPopup_ipaddress(popup,backgroundPopup){
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

	function findAll_ipaddress(p){
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
	            { "sTitle": "Instance Id" },
	            { "sTitle": "Public IP" },
	            { "sTitle": "Status" },
	            { "sTitle": "Reason" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var actions='<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_ipaddress('+p[i].id+')>&nbsp;&nbsp;'+
            '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_ipaddress('+p[i].id+')>';
            if('PENDING_APPROVAL' == p[i].status ){
            	p[i].status='<img title="pending approval" alt="pending approval" src=/images/pending.png>&nbsp;';
            	actions='<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_ipaddress('+p[i].id+')>';
            }else if('starting' == p[i].status ){
            	p[i].status='<img title="starting" alt="starting" src=/images/preloader.gif>&nbsp;';
            	actions='';
            }else if('available' == p[i].status ){
            	p[i].status='<img title="available" alt="available" src=/images/available.png>&nbsp;';
            	actions='<img class="clickimg" title="release" alt="release" src=../images/release.png onclick=release_ipaddress('+p[i].id+')>&nbsp;&nbsp;'+
				'<img class="clickimg" title="associate" alt="associate" src=../images/associate.png onclick=associate_ipaddress('+p[i].id+')>&nbsp; &nbsp;';
            }else if('associated' == p[i].status ){
            	p[i].status='<img title="associated" alt="associated" src=/images/running.png>&nbsp;';
				actions='<img class="clickimg" title="disassociate" alt="disassociate" src=../images/disassociate.png onclick=disassociate_ipaddress('+p[i].id+')>&nbsp;&nbsp;';
            }else if('failed' == p[i].status ){
            	p[i].status='<img title="failed" alt="failed" src=/images/warning.png>&nbsp;';
            	actions='<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_ipaddress('+p[i].id+')>';
            }else if('APPROVAL_REJECTED' == p[i].status){
            	p[i].status='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
        		actions=
                	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_ipaddress('+p[i].id+')>';
        	}else{
            	p[i].status='<img title="unknown" alt="unknown" src=/images/unknown.png>&nbsp;';
				actions='';
            }
            
/*             
            if (p[i].associated !=null && p[i].associated=='1'){
            	p[i].status='<img title="associated" alt="associated" src=/images/running.png>&nbsp;';
            	//alert(p[i].instanceId.toLowerCase());
            	//alert(p[i].instanceId.toLowerCase().indexOf("available"));
				actions='<img class="clickimg" title="disassociate" alt="disassociate" src=../images/disassociate.png onclick=disassociate_ipaddress('+p[i].id+')>&nbsp;&nbsp;';
			}else if(p[i].instanceId !=null && (p[i].instanceId.toLowerCase().indexOf("available") >=0)){
				p[i].status='<img title="available" alt="available" src=/images/available.png>&nbsp;';
				actions='<img class="clickimg" title="release" alt="release" src=../images/release.png onclick=release_ipaddress('+p[i].id+')>&nbsp;&nbsp;'+
						'<img class="clickimg" title="associate" alt="associate" src=../images/associate.png onclick=associate_ipaddress('+p[i].id+')>&nbsp; &nbsp;';

			}else if(p[i].instanceId !=null && (p[i].instanceId.toLowerCase().indexOf("nobody") >=0)){
				actions='<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_ipaddress('+p[i].id+')>&nbsp;';
			}
 */
			oTable.fnAddData( [start+i+1,p[i].name,p[i].project,p[i].instanceId, p[i].publicIp,p[i].status,p[i].reason,p[i].asset.productCatalog.infra.name,
			                   actions ] );
		}
		
	}

	var viewed_ipaddress = -1;	
$(function(){
		$("#popupbutton_ipaddress").click(function(){
			viewed_ipaddress = -1;
			centerPopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
			loadPopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
		});
	
	
		
		$("#popupbutton_ipaddresslist").click(function(){
				
				start =0;
				$('#SearchField').val('');
				AddressInfoP.findAll(start,max,'',findAll_ipaddress);
			  
		} );
		
		$("#popupbutton_previous").click(function(){
			if(start>16){
				start=start -17;
			}
			var text2Search = dwr.util.getValue("SearchField");
			AddressInfoP.findAll(start,max,text2Search,findAll_ipaddress);
		} );
		
		$("#popupbutton_next").click(function(){
			start = start +17;
		
			var text2Search = dwr.util.getValue("SearchField");
			AddressInfoP.findAll(start,max,text2Search,findAll_ipaddress);
		} );
		
		$("#popupbutton_search").click(function(){
			
			var text2Search = dwr.util.getValue("SearchField");
			start = 0;
			AddressInfoP.findAll(start,max,text2Search,findAll_ipaddress);
			
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_ipaddress").click(function(){
			viewed_ipaddress = -1;
			//disablePopup_ipaddress();
			disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
		});
		$("#popupContactClose_ipaddress_associate").click(function(){
			viewed_ipaddress = -1;
			//disablePopup_ipaddress();
			disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
		});
		//Click out event!
		$("#backgroundPopup_ipaddress").click(function(){
			viewed_ipaddress = -1;
			//disablePopup_ipaddress();
			disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_ipaddress==1){
				//disablePopup_ipaddress();
				disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_ipaddresslist").click();
			
			InstanceP.findAll4Attach(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
				dwr.util.removeAllOptions('instanceId');
				dwr.util.addOptions('instanceId', p, 'instanceId', function(p) {
					return p.name+' '+p.instanceId+' '+p.dnsName;
				});
				//dwr.util.setValue(id, sel);
			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_ipaddress(form);
					 return false;
				 }
				});
			
			$("#thisform_associate").validate({
				 submitHandler: function(form) {
					 submitForm_ipaddress_associate(form);
					 return false;
				 }
				});
			
			AddressInfoP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			ProjectService.findAll(function(p){
				dwr.util.removeAllOptions('project');
				//dwr.util.addOptions('project', p, 'id', 'name');
				dwr.util.addOptions('project', p, 'id', function(p) {
					return p.name+' @ '+p.department.name;
				});
				//dwr.util.setValue(id, sel);
				
			});
		});
		
		function submitForm_ipaddress(f){
			var addressInfop = {  id:viewed_ipaddress,name:null, reason:null,product:null, project:{} };
			  dwr.util.getValues(addressInfop);
			  addressInfop.project.id=dwr.util.getValue("project");
			  //alert(dwr.util.getValue("product"));
			  //addressInfop.product = dwr.util.getValue("product");
			  if(viewed_ipaddress == -1){
				  addressInfop.id  = null; 
			  }
			  //alert(addressInfop.name);
			  dwr.engine.beginBatch();
			  AddressInfoP.saveOrUpdate(addressInfop,afterSave_ipaddress);
			 	//Permission.findById(3);
			 // Permission.findAll(); 
			  dwr.engine.endBatch();
			  //disablePopup_ipaddress();
			  disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
			  viewed_ipaddress=-1;
		}
		
		function submitForm_ipaddress_associate(f){
			var addressInfop = {  id:viewed_ipaddress,name:null, instanceId:null,publicIp:null, project:{} };
			  dwr.util.getValues(addressInfop);
			  addressInfop.project.id=dwr.util.getValue("project");
			  //alert(addressInfop.instanceId);
			  //alert(addressInfop.publicIp);
			  if(viewed_ipaddress == -1){
				  addressInfop.id  = null; 
			  }
			  
			  dwr.engine.beginBatch();
			  AddressInfoP.associateAddress(addressInfop,afterSave_ipaddress);
			 	//Permission.findById(3);
			 // Permission.findAll(); 
			  dwr.engine.endBatch();
			  //disablePopup_ipaddress();
			  disablePopup_ipaddress($("#popupContact_ipaddress_associate"),$("#backgroundPopup_ipaddress_associate"));
			  viewed_ipaddress=-1;
		}
		
function cancelForm_ipaddress(f){

	var addressInfop = {  id:null,name:null, reason:null,product:null };
	  dwr.util.setValues(addressInfop);
	  viewed_ipaddress = -1;
	  //disablePopup_ipaddress();
	  disablePopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
}

function cancelForm_ipaddress_associate(f){

	var addressInfop = {  id:null,name:null, instanceId:null,publicIp:null };
	  dwr.util.setValues(addressInfop);
	  viewed_ipaddress = -1;
	  //disablePopup_ipaddress();
	  disablePopup_ipaddress($("#popupContact_ipaddress_associate"),$("#backgroundPopup_ipaddress_associate"));
}



function afterEdit_ipaddress(p){
	var addressInfop = eval(p);
	viewed_ipaddress=p.id;
	//centerPopup_ipaddress();
	centerPopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
	//loadPopup_ipaddress();
	loadPopup_ipaddress($("#popupContact_ipaddress"),$("#backgroundPopup_ipaddress"));
	dwr.util.setValues(addressInfop);
	dwr.util.setValue('product',p.product.id);
}

function afterEdit_ipaddress_associate(p){
	var addressInfop = eval(p);
	viewed_ipaddress=p.id;
	centerPopup_ipaddress($("#popupContact_ipaddress_associate"),$("#backgroundPopup_ipaddress_associate"));
	loadPopup_ipaddress($("#popupContact_ipaddress_associate"),$("#backgroundPopup_ipaddress_associate"));
	dwr.util.setValues(addressInfop);
	dwr.util.setValue('instanceId',p.instanceId);
	
}


function edit_ipaddress(id){
	AddressInfoP.findById(id,afterEdit_ipaddress);
}

function remove_ipaddress(id){
	if(!disp_confirm('IP')){
		return false;
	}
	dwr.engine.beginBatch();
	AddressInfoP.remove(id,afterRemove_ipaddress);
	dwr.engine.endBatch();
}

function afterRemove_ipaddress(){
	viewed_ipaddress = -1;
	$("#popupbutton_ipaddresslist").click();
	CommonService.getSessionMsg(function(p){   $.sticky(p);  });
	}
	
	
function afterSave_ipaddress(){
	viewed_ipaddress = -1;
	$("#popupbutton_ipaddresslist").click();
	CommonService.getSessionMsg(function(p){   $.sticky(p);  });
	}
	
	function associate_ipaddress(id){
		AddressInfoP.findById(id,afterEdit_ipaddress_associate);
	}
	
	
	function disassociate_ipaddress(id){
		AddressInfoP.disassociateAddress(id,afterDisassociate_ipaddress);
	}
	
	function release_ipaddress(id){
		AddressInfoP.releaseAddress(id,afterRelease_ipaddress);
	}
	function afterRelease_ipaddress(){
		CommonService.getSessionMsg(function(p){   $.sticky(p);  });
		//AddressInfoP.findById(id,afterEdit_ipaddress);
	}
	
	function afterDisassociate_ipaddress(){
		CommonService.getSessionMsg(function(p){   $.sticky(p);  });
		//AddressInfoP.findById(id,afterEdit_ipaddress);
	}


</script>
<p class="dataTableHeader">IP Resource</p>
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
						<div style="height: 50px;">
							<div class="demo" id="popupbutton_ipaddress" style="float: left; padding-left: 10px;"><button>Request New Ip</button></div>
							<div class="demo" id="popupbutton_ipaddresslist" style="float: left; padding-left: 10px;"><button>List All Ips</button></div>
							
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
									<!-- <div class="demo" id="popupbutton_ipaddress"><button>Request IP</button></div> -->
								</td>
								<td width="15%">
									<!-- <div class="demo" id="popupbutton_ipaddresslist"><button>List IPs</button></div> -->
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_ipaddress" >
		<div id="popupContact_ipaddress" class="popupContact" >
							<a  onclick="cancelForm_ipaddress();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>IP Address</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_ipaddress" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Reason : </td>
								    <td style="width: 50%;"><input type="text" name="reason" id="reason" size="30" ></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Product : </td>
								    <td style="width: 50%;">
								    <select id="product" name="product" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">project : </td>
								    <td style="width: 80%;">
								    <select id="project" name="project" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_ipaddress_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_ipaddress(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_ipaddress" class="backgroundPopup" ></div>
		
		
		<div id="popupContact_ipaddress_associate" class="popupContact" >
							<a  onclick="cancelForm_ipaddress_associate();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>IP Address Associate</h1>
							<form class="cmxform" id="thisform_associate" method="post" name="thisform_associate">
								<p id="contactArea_ipaddress" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">IP : </td>
								    <td style="width: 50%;"><input type="text" name="publicIp" id="publicIp" size="30" class="required" readonly="readonly"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Instance : </td>
								    <td style="width: 50%;"><select id="instanceId" name="instanceId" style="width: 205px;" class="required">
							    	</select></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_ipaddress_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_ipaddress_associate(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_ipaddress_associate" class="backgroundPopup" ></div>
	</div>				