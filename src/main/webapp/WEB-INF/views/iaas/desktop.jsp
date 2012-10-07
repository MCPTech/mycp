<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>


<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_desktop = 0;

	//loading popup with jQuery magic!
	function loadPopup_desktop(){
		//loads popup only if it is disabled
		if(popupStatus_desktop==0){
			$("#backgroundPopup_desktop").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_desktop").fadeIn("slow");
			$("#popupContact_desktop").fadeIn("slow");
			popupStatus_desktop = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_desktop(){
		//disables popup only if it is enabled
		if(popupStatus_desktop==1){
			$("#backgroundPopup_desktop").fadeOut("slow");
			$("#popupContact_desktop").fadeOut("slow");
			popupStatus_desktop = 0;
		}
	}

	//centering popup
	function centerPopup_desktop(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_desktop").height();
		var popupWidth = $("#popupContact_desktop").width();
		//centering
		$("#popupContact_desktop").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_desktop").css({
			"height": windowHeight
		});
		
	}

	function findAll_desktop(p){
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
	            { "sTitle": "Insance Id" },
	            { "sTitle": "Image Id" },
	            { "sTitle": "DNS" },
	            { "sTitle": "Key" },
	            { "sTitle": "OS" },
	            { "sTitle": "State" },
	            { "sTitle": "Instance Type" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			oTable.fnAddData( [i+1,p[i].instanceId, p[i].imageId, p[i].dnsName,
			                   p[i].keyName, p[i].platform, p[i].state,
			                   p[i].instanceType,
			                   '<input type=button onclick=edit_desktop('+p[i].id+'); value=Edit />&nbsp;&nbsp;&nbsp;'+
			                   '<input type=button onclick=remove_desktop('+p[i].id+'); value=Remove />' ] );
		}
		
		
	
	}

	var viewed_desktop = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_desktop").click(function(){
			viewed_desktop = -1;
			//centering with css
			centerPopup_desktop();
			//load popup
			loadPopup_desktop();
		});
	
		$("#popupbutton_desktoplist").click(function(){
			
				dwr.engine.beginBatch();
				InstanceP.findAll(findAll_desktop);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_desktop").click(function(){
			viewed_desktop = -1;
			disablePopup_desktop();
		});
		//Click out event!
		$("#backgroundPopup_desktop").click(function(){
			viewed_desktop = -1;
			disablePopup_desktop();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_desktop==1){
				disablePopup_desktop();
			}
		});
		
		
		
		$(document).ready(function() {
			
		});
		
function submitForm_desktop(f){
	var instancep = {  id:viewed_desktop,name:null, url:null };
	  dwr.util.getValues(instancep);
	  
	  if(viewed_desktop == -1){
		  instancep.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  InstanceP.saveOrUpdate(instancep,afterSave_desktop);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_desktop();
	  viewed_desktop=-1;
}
function cancelForm_desktop(f){

	var instancep = {  id:null,name:null, url:null };
	  dwr.util.setValues(instancep);
	  viewed_desktop = -1;
	  disablePopup_desktop();
}

function afterEdit_desktop(p){
	var instancep = eval(p);
	viewed_desktop=p.id;
	centerPopup_desktop();
	loadPopup_desktop();
	dwr.util.setValues(instancep);
}

function edit_desktop(id){
	InstanceP.findById(id,afterEdit_desktop);
}

function remove_desktop(id){
	if(!disp_confirm('Desktop')){
		return false;
	}
	dwr.engine.beginBatch();
	InstanceP.remove(id,afterSave_desktop);
	dwr.engine.endBatch();
}
function afterSave_desktop(){
	viewed_desktop = -1;
	$("#popupbutton_desktoplist").click();}

</script>

<div id="datatable-iaas-parent" class="infragrid2">
					<div id="datatable-iaas" >
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="compute-table">
							<thead><tr></tr></thead>
							<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
							</tfoot><tbody></tbody>
						</table>
						
						<table align="right" border="0" width="100%">
							<tr>
								<td width="80%">
								<font color="green">IaaS -- Desktop</font>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_compute"><button>Request Compute</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_computelist"><button>List Compute</button></div>
								</td>
							</tr>
						</table>
						
						
				</div>
				</div>
				
	<div id="popupContactParent_desktop" >
		<div id="popupContact_desktop" class="popupContact" >
							<a  onclick="cancelForm_desktop();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form method="post" name="thisform">
								<p id="contactArea_desktop" class="contactArea" >
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
										<div class="demo" id="popupbutton_desktop_create">
											<button onclick="submitForm_desktop(this.form);return false;">Save</button>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_desktop(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_desktop" class="backgroundPopup" ></div>
	</div>				