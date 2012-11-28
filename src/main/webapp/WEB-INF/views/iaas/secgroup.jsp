<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/GroupDescriptionP.js'></script>
<script type='text/javascript' src='/dwr/interface/IpPermissionP.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
<script type="text/javascript">
	function clearTable(tableID) {
		$(document.getElementById(tableID)).find("tr:gt(0)").remove();	
	}

	function addRowWithData(tableID,id,protocol,fromPort,toPort,cidrIps) {
		var table = document.getElementById('secTable');

			var rowData = '<tr valign="top">'+
					'<input type="hidden" name="ids" id="ids" value=\''+id+'\'>'+	  
					'<td style="width: 20%;"><input type="text" name="protocol" id="protocol" size="20" value=\''+protocol+'\' class="required"></td>'+
				  '<td style="width: 20%;"><input type="text" name="fromPort" id="fromPort" size="20" value=\''+fromPort+'\' class="required number"></td>'+
				  '<td style="width: 20%;"><input type="text" name="toPort" id="toPort" size="20" value=\''+toPort+'\' class="required number"></td>'+
				  '<td style="width: 20%;"><input type="text" name="cidrIps" id="cidrIps" size="20" value=\''+cidrIps+'\' ></td>'+
				  '<td style="width: 5%;">'+
						'<a title="Remove Rule" onclick="deleteRow(this,'+id+')"><img src=../images/minus.png></a>'+
				  '</td>'+
			  '</tr>';
			  
		    $('#'+tableID+' tr:last').after(rowData);
		}
	
	function addRow(tableID) {
	var table = document.getElementById('secTable');

		var rowData = '<tr valign="top">'+
				'<input type="hidden" name="ids" id="ids" value=0>'+
			  '<td style="width: 20%;"><input type="text" name="protocol" id="protocol" size="20" class="required"></td>'+
			  '<td style="width: 20%;"><input type="text" name="fromPort" id="fromPort" size="20" class="required number"></td>'+
			  '<td style="width: 20%;"><input type="text" name="toPort" id="toPort" size="20" class="required number"></td>'+
			  '<td style="width: 20%;"><input type="text" name="cidrIps" id="cidrIps" size="20" ></td>'+
			  '<td style="width: 5%;">'+
					'<a title="Remove Rule" onclick="deleteRow(this,null)"><img src=../images/minus.png></a>'+
			  '</td>'+
		  '</tr>';
		  
	    $('#'+tableID+' tr:last').after(rowData);
	}
	
	function deleteRow(row,id) {
	    try {
	    	$(row).parents('tr').first().remove();
	    	if(id !=null){
	    		IpPermissionP.remove(id);
	    	}
	    }catch(e) {
	        alert(e);
	    }
	}
</script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
	var popupStatus_secgroup = 0;

	function loadPopup_secgroup(){
		if(popupStatus_secgroup==0){
			$("#backgroundPopup_secgroup").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_secgroup").fadeIn("slow");
			$("#popupContact_secgroup").fadeIn("slow");
			popupStatus_secgroup = 1;
		}
	}

	function disablePopup_secgroup(){
		if(popupStatus_secgroup==1){
			$("#backgroundPopup_secgroup").fadeOut("slow");
			$("#popupContact_secgroup").fadeOut("slow");
			popupStatus_secgroup = 0;
		}
	}

	function centerPopup_secgroup(){
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_secgroup").height();
		var popupWidth = $("#popupContact_secgroup").width();
		$("#popupContact_secgroup").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		
		$("#backgroundPopup_secgroup").css({
			"height": windowHeight
		});
		
	}

	var start = 0;
	var max = 17;
	var index=0;
	
	function findAll_secgroup(p){
		
		//alert(dwr.util.toDescriptiveString(p,3));

		 oTable = $('#compute-table').dataTable( {
	    	"sPaginationType": "full_numbers",
	    	"bDestroy": true,
	    	"bAutoWidth": false,
	    	"bDeferRender": true,
	    	 "aaSorting": [],
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
	            { "sTitle": "Description" },
	            { "sTitle": "Owner" },
	            { "sTitle": "Status" },
	            { "sTitle": "Protocol" },
	            { "sTitle": "From" },
	            { "sTitle": "To" },
	            { "sTitle": "CIDR" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			try{
			var status = '';
			if(p[i].status !=null && p[i].status == 'active'){
				status = '<img  title="running" alt="running" src=../images/running.png >';
			}else if(p[i].status !=null && p[i].status == 'starting'){
				status = '<img  title="starting" alt="starting" src=../images/preloader.gif >';
			}else if(p[i].status !=null && p[i].status == 'PENDING_APPROVAL'){
				status = '<img  title="pending approval" alt="pending approval" src=../images/pending.png >';
			}else if('APPROVAL_REJECTED' == p[i].status){
            	status='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
        		
        	}else{
				status = '<img title="unknown" alt="unknown" src=../images/unknown.png >';
			}
			var projName = '';
			try{
				projName = p[i].asset.project.name;
			}catch(e){}
			oTable.fnAddData( [i+1,p[i].name, projName, p[i].descripton, p[i].owner,status,
			                   '', '', '',
			                   '',p[i].asset.productCatalog.infra.name,
			                   '<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_secgroup('+p[i].id+')>&nbsp;&nbsp;&nbsp;'+
			                   '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_secgroup('+p[i].id+')>' ] );
			
			if(p[i].ipPermissionPs!=null && p[i].ipPermissionPs.length>0){
				var j=0;
				for (j=0;j<p[i].ipPermissionPs.length;j++)
				{
					oTable.fnAddData( ['','', '', '', '','',
					                   p[i].ipPermissionPs[j].protocol, p[i].ipPermissionPs[j].fromPort, p[i].ipPermissionPs[j].toPort,
					                   p[i].ipPermissionPs[j].cidrIps,'',
					                   '' ] );
				}//for j
			}//if
		}catch(e){alert(e);}
		}//for i
	
	}

	var viewed_secgroup = -1;	
$(function(){
	
	
		$("#popupbutton_secgroup").click(function(){
			
			viewed_secgroup = -1;
			centerPopup_secgroup();
			loadPopup_secgroup();
				var groupDescriptionp = {  id:null,name:null, url:null };
			  	dwr.util.setValues(groupDescriptionp);
			  	clearTable('secTable');
			  	addRow('secTable');
			  
		});
	
		
			$("#popupbutton_secgrouplist").click(function(){
				
					dwr.engine.beginBatch();
					start =0;
					$('#SearchField').val('');
					GroupDescriptionP.findAll4Edit(start,max,'',findAll_secgroup);
				  dwr.engine.endBatch();
			} );
			
			$("#popupbutton_previous").click(function(){
				if(start>16){
					start=start -17;
				}
				var text2Search = dwr.util.getValue("SearchField");
				GroupDescriptionP.findAll4Edit(start,max,text2Search,findAll_secgroup);
			} );
			
			$("#popupbutton_next").click(function(){
				start = start +17;
				var text2Search = dwr.util.getValue("SearchField");
				GroupDescriptionP.findAll4Edit(start,max,text2Search,findAll_secgroup);
			} );
			
			$("#popupbutton_search").click(function(){
				
				var text2Search = dwr.util.getValue("SearchField");
				start = 0;
				GroupDescriptionP.findAll4Edit(start,max,text2Search,findAll_secgroup);
				
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

		$("#popupContactClose_secgroup").click(function(){
			viewed_secgroup = -1;
			disablePopup_secgroup();
		});
		$("#backgroundPopup_secgroup").click(function(){
			viewed_secgroup = -1;
			disablePopup_secgroup();
		});
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_secgroup==1){
				disablePopup_secgroup();
			}
		});
		
		$(document).ready(function() {
			$("#popupbutton_secgrouplist").click();
			
			GroupDescriptionP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_secgroup(form);
					 return false;
				 }
				});
		});
		
	function submitForm_secgroup(f){
		CommonService.getSessionMsg(function(p){   $.sticky(p);  });
		var groupDescriptionp = {  id:viewed_secgroup,name:null, descripton:null,owner:null,ipPermissionPs:[],product:null, projectId:null };
	     dwr.util.getValues(groupDescriptionp); 
		  if(viewed_secgroup == -1){
			  groupDescriptionp.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  	GroupDescriptionP.saveOrUpdate(groupDescriptionp,function(p) {
				if(p == null){
					CommonService.getSessionMsg(function(p){
						$.sticky(p);
					});
					return;
				}
		  		var ids = $('input:hidden[name=ids]');
		  		 var fromPortData = $('input:text[name=fromPort]');
		  	     var toPortData = $('input:text[name=toPort]');
		  	     var protocolData = $('input:text[name=protocol]');
		  	     var cidrIpsData = $('input:text[name=cidrIps]');
		  	     var j=0;
		  	     var rules=new Array();
		  		for (j=0;j<fromPortData.length;j++)
		  		{
		  			//alert(ids[j].value);
		  			var ipPermissionP = {id:ids[j].value, fromPort:fromPortData[j].value,toPort:toPortData[j].value, 
		  					protocol:protocolData[j].value,cidrIps:cidrIpsData[j].value,
		  					groupDescription:{id:p.id}};
		  			IpPermissionP.saveOrUpdate(ipPermissionP,afterSave_secgroup);
		  		}
			});
		  dwr.engine.endBatch();
		  disablePopup_secgroup();
		  viewed_secgroup=-1;
	}
	function cancelForm_secgroup(f){
	
		var groupDescriptionp = {  id:null,name:null, descripton:null,owner:null,ipPermissionPs:[],product:null, projectId:null };
		  dwr.util.setValues(groupDescriptionp);
		  viewed_secgroup = -1;
		  disablePopup_secgroup();
	}
	
	function afterEdit_secgroup(p){
		viewed_secgroup=p.id;
		centerPopup_secgroup();
		loadPopup_secgroup();
		dwr.util.setValues(p);
		dwr.util.setValue('product',p.product.id);
		clearTable('secTable');
		IpPermissionP.findBySecurityGroup(p,function(p) {
			for (j=0;j<p.length;j++)
			{
				addRowWithData('secTable',p[j].id,p[j].protocol,p[j].fromPort,p[j].toPort,p[j].cidrIps);
				//addRow('secTable');//alert(p[j].protocol);
			}
		});
		//if(p.ipPermissionPs!=null && p.ipPermissionPs.length>0){
			//var j=0;
			//for (j=0;j<p.ipPermissionPs.length;j++)
			//{
				//alert(p.ipPermissionPs[j].protocol);
				//oTable.fnAddData( ['','', '', '',
				                  // p.ipPermissionPs[j].protocol, p.ipPermissionPs[j].fromPort, p.ipPermissionPs[j].toPort,
				                   //p.ipPermissionPs[j].cidrIps,
				                  // '' ] );
			//}//for j
		//}//if
		
		
	}
	
	function edit_secgroup(id){
		GroupDescriptionP.findById(id,afterEdit_secgroup);
	}
	
	function remove_secgroup(id){
		if(!disp_confirm('Group')){
			return false;
		}
		dwr.engine.beginBatch();
		GroupDescriptionP.remove(id,afterSave_secgroup);
		dwr.engine.endBatch();
	}
	function afterSave_secgroup(){
		viewed_secgroup = -1;
		$("#popupbutton_secgrouplist").click();}
	
	</script>
<p class="dataTableHeader">Security Group Resource</p>
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
						<div style="height: 50px; padding-top: 20px;">
							<div class="demo" id="popupbutton_secgroup" style="float: left; padding-left: 10px;"><button>Request New Group</button></div>
							<div class="demo" id="popupbutton_secgrouplist" style="float: left; padding-left: 10px;"><button>List All Groups</button></div>
							
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
									<!-- <div class="demo" id="popupbutton_secgroup"><button>Create Group</button></div> -->
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_secgrouplist"><button>List Groups</button></div> -->
								</td>
							</tr>
						</table>
						
						
				</div>
				</div>
				
	<div id="popupContactParent_secgroup" >
	
		<div id="popupContact_secgroup" class="popupContact" style="width: 70%">
							<a  onclick="cancelForm_secgroup();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Security Group</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_secgroup" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table  style="width: 100%;" border="0">
								<tr>
								    <td style="width: 20%;">Name : </td>
								    <td style="width: 20%;">Description : </td>
								    <td style="width: 20%;">Owner : </td>
								    <td style="width: 20%;">Product : </td>
								    <td style="width: 20%;">Project : </td>
								</tr>
								 
								<tr valign="top">
								    <td><input type="text" name="name" id="name" size="20" class="required"></td>
								    <td><input type="text" name="descripton" id="descripton" size="20" class="required"></td>
								    <td><input type="text" name="owner" id="owner" size="20" class="required"> </td>
								    <td><select id="product" name="product" style="width: 185px;" class="required"></select>
								    </td>
								    <td>
									    <select id="projectId" name="projectId" style="width: 185px;" class="required">
								    	</select>
							    	</td>
								</tr>
								</table>
								<br>
								<table  style="width: 100%;">
									<tr>
										  <td>
											  <table style="width: 100%;" id="secTable">
												  <tr>
													  <td style="width: 20%;">Protocol : </td>
													  <td style="width: 20%;">From Port : </td>
													  <td style="width: 20%;">To Port : </td>
													  <td style="width: 20%;">CIDR : </td>
													  <td style="width: 5%;">
													  	<a title="Add New Rule" onclick="addRow('secTable');return false;"> <img src="../images/add_row.png"/> </a>
													  </td>
													  <td style="width: 15%;"></td>
												  </tr>
											  </table>
										  </td>
									</tr>
									<tr>
									  	<td align="right">
									    <br><br>
											<div class="demo" id="popupbutton_secgroup_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
												<button onclick="cancelForm_secgroup(this.form);return false;">Cancel</button>
											</div>
										</td>
										
									</tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_secgroup" class="backgroundPopup" ></div>
	</div>				