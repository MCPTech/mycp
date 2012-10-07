<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/AssetTypeService.js'></script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_assettype = 0;

	//loading popup with jQuery magic!
	function loadPopup_assettype(){
		//loads popup only if it is disabled
		if(popupStatus_assettype==0){
			$("#backgroundPopup_assettype").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_assettype").fadeIn("slow");
			$("#popupContact_assettype").fadeIn("slow");
			popupStatus_assettype = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_assettype(){
		//disables popup only if it is enabled
		if(popupStatus_assettype==1){
			$("#backgroundPopup_assettype").fadeOut("slow");
			$("#popupContact_assettype").fadeOut("slow");
			popupStatus_assettype = 0;
		}
	}

	//centering popup
	function centerPopup_assettype(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_assettype").height();
		var popupWidth = $("#popupContact_assettype").width();
		//centering
		$("#popupContact_assettype").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_assettype").css({
			"height": windowHeight
		});
		
	}
	var issuperadmin = false;
	function findAll_assettype(p){
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
	            { "sTitle": "Workflow?" },
	            { "sTitle": "Billable?" },
	            { "sTitle": "Description" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var options = ' ';
			if(issuperadmin){
				options = '<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_assettype('+p[i].id+')>&nbsp; &nbsp; &nbsp;'+
                '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_assettype('+p[i].id+')>';
			}
			oTable.fnAddData( [i+1,p[i].name, p[i].workflowEnabled,p[i].billable,p[i].description,
			                   options ] );
		}
		
		  
	
	}

	var viewed_assettype = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_assettype").click(function(){
			viewed_assettype = -1;
			//centering with css
			centerPopup_assettype();
			//load popup
			loadPopup_assettype();
		});
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_assettype").click(function(){
			viewed_assettype = -1;
			disablePopup_assettype();
		});
		//Click out event!
		$("#backgroundPopup_assettype").click(function(){
			viewed_assettype = -1;
			disablePopup_assettype();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_assettype==1){
				disablePopup_assettype();
			}
		});
		
		
		
		$(document).ready(function() {
			
			$("#popupbutton_assettypelist").click(function(){
				AssetTypeService.findAll(findAll_assettype);
			} );
			});
		
			  $("#thisform").validate({
					 submitHandler: function(form) {
						 submitForm_assettype(form);
						 return false;
					 }
					});
			  
			  $(document).ready(function() {
					CommonService.getCurrentSession(function(p){
						if(p.role != 'ROLE_SUPERADMIN'){
							dwr.util.setValue('only4superadmin', '');	
						}else{
							issuperadmin = true; 
						}
						//after finding out who is logged in , call the listing function 
						$("#popupbutton_assettypelist").click();
						
						
					});
					});
		});
		
function submitForm_assettype(f){
	var assettype = {  id:viewed_assettype,name:null,workflowEnabled:false,billable:true, description:null };
	  dwr.util.getValues(assettype);
	  
	  if(viewed_assettype == -1){
		  assettype.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  AssetTypeService.saveOrUpdate(assettype,afterSave_assettype);
	 
	  dwr.engine.endBatch();
	  disablePopup_assettype();
	  viewed_assettype=-1;
}
function cancelForm_assettype(f){

	var assettype = {  id:null,name:null,workflowEnabled:false,billable:false, description:null };
	  dwr.util.setValues(assettype);
	  viewed_assettype = -1;
	  disablePopup_assettype();
}

function afterEdit_assettype(p){
	var assettype = eval(p);
	viewed_assettype=p.id;
	centerPopup_assettype();
	loadPopup_assettype();
	dwr.util.setValues(assettype);
}

function edit_assettype(id){
	AssetTypeService.findById(id,afterEdit_assettype);
}

function remove_assettype(id){
	if(!disp_confirm('Product Type')){
		return false;
	}
	dwr.engine.beginBatch();
	AssetTypeService.remove(id,afterRemove_assettype);
	dwr.engine.endBatch();
}

function afterRemove_assettype(p){
	viewed_assettype = -1;
	$("#popupbutton_assettypelist").click();
	$.sticky(p);
	}
	
function afterSave_assettype(){
	viewed_assettype = -1;
	$("#popupbutton_assettypelist").click();}

</script>
<p class="dataTableHeader">Product Type Configuration</p>
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
								
								</td>
								<td width="10%">
									<span id="only4superadmin"><div class="demo" id="popupbutton_assettype"><button>New Product Type</button></div></span>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_assettypelist"><button>List Product Types</button></div>
								</td>
							</tr>
						</table>
						
						
				</div>
				</div>
				
	<div id="popupContactParent_assettype" >
		<div id="popupContact_assettype" class="popupContact" >
							<a  onclick="cancelForm_assettype();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Product Type</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_assettype" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								 
								  <tr>
								    <td style="width: 50%;">Description : </td>
								    <td style="width: 50%;"><input type="text" name="description" id="description" size="30"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Workflow ? </td>
								    <td style="width: 50%;"><input type="checkbox" name="workflowEnabled" id="workflowEnabled"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Billable ? </td>
								    <td style="width: 50%;"><input type="checkbox" name="billable" id="billable"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_assettype_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_assettype(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_assettype" class="backgroundPopup" ></div>
	</div>				