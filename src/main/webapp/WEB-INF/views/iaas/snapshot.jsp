<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/SnapshotInfoP.js'></script>
<script type='text/javascript' src='/dwr/interface/VolumeInfoP.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
<script type='text/javascript' src='/dwr/interface/ProductService.js'></script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
	var popupStatus_backup = 0;
	function loadPopup_backup(){
		if(popupStatus_backup==0){
			$("#backgroundPopup_backup").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_backup").fadeIn("slow");
			$("#popupContact_backup").fadeIn("slow");
			popupStatus_backup = 1;
		}
	}

	function disablePopup_backup(){
		if(popupStatus_backup==1){
			$("#backgroundPopup_backup").fadeOut("slow");
			$("#popupContact_backup").fadeOut("slow");
			popupStatus_backup = 0;
		}
	}

	function centerPopup_backup(){
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_backup").height();
		var popupWidth = $("#popupContact_backup").width();
		$("#popupContact_backup").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		$("#backgroundPopup_backup").css({
			"height": windowHeight
		});
		
	}

	var start = 0;
	var max = 17;
	
	function findAll_backup(p){
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
	            { "sTitle": "Snapshot Id" },
	            { "sTitle": "Project" },
	            { "sTitle": "Volume Id" },
	            { "sTitle": "Start Time" },
	            { "sTitle": "Status" },
	            { "sTitle": "Progress" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var actions = '<img class="clickimg" title="Remove" alt="Remove" src=../images/remove.png onclick=delete_backup('+p[i].id+')>&nbsp; &nbsp; '+
            '<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_backup('+p[i].id+')>';
			if('PENDING_APPROVAL' == p[i].status ){
            	p[i].status='<img title="pending approval" alt="pending approval" src=/images/pending.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_backup('+p[i].id+')>';
            }else if('pending' == p[i].status ){
            	p[i].status='<img title="starting" alt="starting" src=/images/preloader.gif>&nbsp;';
            	actions='';
            }else if('completed' == p[i].status ){
            	p[i].status='<img title="completed" alt="completed" src=/images/running.png>&nbsp;';
            	actions='<img class="clickimg" title="Remove" alt="Remove" src=../images/remove.png onclick=delete_backup('+p[i].id+')>&nbsp; &nbsp; ';
            }else if('inactive' == p[i].status ){
            	p[i].status='<img title="inactive" alt="inactive" src=/images/unknown.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_backup('+p[i].id+')>';
            }else if('APPROVAL_REJECTED' == p[i].status){
            	p[i].status='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
        		actions=
                	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_backup('+p[i].id+')>';
        	}

			var projName = '';
			try{
				projName = p[i].asset.project.name;
			}catch(e){}
			
			oTable.fnAddData( [start+i+1,p[i].snapshotId, projName, p[i].volumeId, 
			                   dateFormat(p[i].startTime,"mmm dd yyyy HH:MM:ss"), p[i].status,p[i].progress, p[i].asset.productCatalog.infra.name,
			                   actions ] );
		}
		
		
	
	}

	var viewed_backup = -1;	
$(function(){
		$("#popupbutton_backup").click(function(){
			viewed_backup = -1;
			centerPopup_backup();
			loadPopup_backup();
		});
	
		$("#popupbutton_backuplist").click(function(){
				dwr.engine.beginBatch();
				start =0;
				$('#SearchField').val('');
				SnapshotInfoP.findAll(start,max,'',findAll_backup);
			  dwr.engine.endBatch();
		} );
		
		$("#popupbutton_previous").click(function(){
			if(start>16){
				start=start -17;
			}
			var text2Search = dwr.util.getValue("SearchField");
			SnapshotInfoP.findAll(start,max,text2Search,findAll_backup);
		} );
		
		$("#popupbutton_next").click(function(){
			start = start +17;
			var text2Search = dwr.util.getValue("SearchField");
			SnapshotInfoP.findAll(start,max,text2Search,findAll_backup);
		} );
		
		$("#popupbutton_search").click(function(){
			
			var text2Search = dwr.util.getValue("SearchField");
			start = 0;
			SnapshotInfoP.findAll(start,max,text2Search,findAll_backup);
			
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

	$("#popupContactClose_backup").click(function(){
			viewed_backup = -1;
			disablePopup_backup();
		});
		$("#backgroundPopup_backup").click(function(){
			viewed_backup = -1;
			disablePopup_backup();
		});
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_backup==1){
				disablePopup_backup();
			}
		});
		
		$(document).ready(function() {
			$("#popupbutton_backuplist").click();
			
			SnapshotInfoP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', {'-1':'Please Select'});
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_backup(form);
					 return false;
				 }
				});
		});
		
	function submitForm_backup(f){
		var snapshotInfop = {  id:viewed_backup,description:null, volumeId:null,product:null, projectId:null };
		  dwr.util.getValues(snapshotInfop);
		  if(viewed_backup == -1){
			  snapshotInfop.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  SnapshotInfoP.requestSnapshot(snapshotInfop,afterSave_backup);
		  dwr.engine.endBatch();
		  disablePopup_backup();
		  viewed_backup=-1;
		  CommonService.getSessionMsg(function(p){   $.sticky(p);  });
	}
	function cancelForm_backup(f){
	
		var snapshotInfop = {  id:null,description:null, volumeId:null,product:null, projectId:null };
		  dwr.util.setValues(snapshotInfop);
		  viewed_backup = -1;
		  disablePopup_backup();
	}
	
	function afterEdit_backup(p){
		var snapshotInfop = eval(p);
		viewed_backup=p.id;
		centerPopup_backup();
		loadPopup_backup();
		dwr.util.setValues(snapshotInfop);
	}
	
	function edit_backup(id){
		SnapshotInfoP.findById(id,afterEdit_backup);
	}
	
	function remove_backup(id){
		if(!disp_confirm('Snapshot')){
			return false;
		}
		dwr.engine.beginBatch();
		SnapshotInfoP.remove(id,afterSave_backup);
		dwr.engine.endBatch();
	}
	function afterSave_backup(){
		viewed_backup = -1;
		$("#popupbutton_backuplist").click();
	}
	
	function delete_backup(id){
		if(!disp_terminate('Snapshot')){
			return false;
		}
		dwr.engine.beginBatch();
		SnapshotInfoP.deleteSnapshot(id,afterdelete_backup);
		dwr.engine.endBatch();
	}
	
	function afterdelete_backup(){
		viewed_backup = -1;
		$("#popupbutton_backuplist").click();
		$.sticky("Snapshot scheduled to be removed.");
	}
	
	</script>
<p class="dataTableHeader">Snapshot Resource</p>
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
							<div class="demo" id="popupbutton_backup" style="float: left; padding-left: 10px;"><button>Request New Snapshot</button></div>
							<div class="demo" id="popupbutton_backuplist" style="float: left; padding-left: 10px;"><button>List All Snapshots</button></div>
							
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
									<!-- <div class="demo" id="popupbutton_backup"><button>Request Snapshot</button></div> -->
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_backuplist"><button>List Snapshots</button></div> -->
								</td>
							</tr>
						</table>
						
						
				</div>
				</div>
				
	<div id="popupContactParent_backup" >
		<div id="popupContact_backup" class="popupContact" >
							<a  onclick="cancelForm_backup();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Snapshot</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_backup" class="contactArea" >
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
								    <td style="width: 50%;">Description : </td>
								    <td style="width: 50%;"><input type="text" name="description" id="description" size="30" class="required"> </td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Volume : </td>
								    <td style="width: 50%;"><select id="volumeId" name="volumeId" style="width: 205px;" class="required"></td>
								  </tr>
								   
								  <tr>
								    <td style="width: 50%;">project : </td>
								    <td style="width: 50%;">
								    <select id="projectId" name="projectId" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_backup_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_backup(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_backup" class="backgroundPopup" ></div>
	</div>				
	
	<script>
	$("#product").change(function(){
		
		var productCatId = parseInt($("#product").val());
		ProductService.findById(productCatId, function(s){
			VolumeInfoP.findAll4List4Infra(s.infra,function(p){
				dwr.util.removeAllOptions('instance');
				dwr.util.addOptions('volumeId', p, 'volumeId', function(p) {
					if(p.volumeId !=null){
						return p.volumeId+' '+p.size+' '+p.name;
					}else {
						return null;
					}
				});
			});
			
			
		});
	} );
</script>
