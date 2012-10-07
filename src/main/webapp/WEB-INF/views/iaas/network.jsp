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
	var popupStatus_network = 0;

	//loading popup with jQuery magic!
	function loadPopup_network(){
		//loads popup only if it is disabled
		if(popupStatus_network==0){
			$("#backgroundPopup_network").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_network").fadeIn("slow");
			$("#popupContact_network").fadeIn("slow");
			popupStatus_network = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_network(){
		//disables popup only if it is enabled
		if(popupStatus_network==1){
			$("#backgroundPopup_network").fadeOut("slow");
			$("#popupContact_network").fadeOut("slow");
			popupStatus_network = 0;
		}
	}

	//centering popup
	function centerPopup_network(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_network").height();
		var popupWidth = $("#popupContact_network").width();
		//centering
		$("#popupContact_network").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_network").css({
			"height": windowHeight
		});
		
	}

	function findAll_network(p){
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
			                   '<input class="clickimg" title="Edit" alt="edit" type=button onclick=edit_network('+p[i].id+'); value=Edit />&nbsp;&nbsp;&nbsp;'+
			                   '<input class="clickimg" title="Remove" alt="Remove" type=button onclick=remove_network('+p[i].id+'); value=Remove />' ] );
		}
		
		
	
	}

	var viewed_network = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_network").click(function(){
			viewed_network = -1;
			//centering with css
			centerPopup_network();
			//load popup
			loadPopup_network();
		});
	
		$("#popupbutton_networklist").click(function(){
			
				dwr.engine.beginBatch();
				InstanceP.findAll(findAll_network);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_network").click(function(){
			viewed_network = -1;
			disablePopup_network();
		});
		//Click out event!
		$("#backgroundPopup_network").click(function(){
			viewed_network = -1;
			disablePopup_network();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_network==1){
				disablePopup_network();
			}
		});
		
		
		
		$(document).ready(function() {
			
		});
		
function submitForm_network(f){
	var instancep = {  id:viewed_network,name:null, url:null };
	  dwr.util.getValues(instancep);
	  
	  if(viewed_network == -1){
		  instancep.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  InstanceP.saveOrUpdate(instancep,afterSave_network);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_network();
	  viewed_network=-1;
}
function cancelForm_network(f){

	var instancep = {  id:null,name:null, url:null };
	  dwr.util.setValues(instancep);
	  viewed_network = -1;
	  disablePopup_network();
}

function afterEdit_network(p){
	var instancep = eval(p);
	viewed_network=p.id;
	centerPopup_network();
	loadPopup_network();
	dwr.util.setValues(instancep);
}

function edit_network(id){
	InstanceP.findById(id,afterEdit_network);
}

function remove_network(id){
	if(!disp_confirm('Network')){
		return false;
	}
	dwr.engine.beginBatch();
	InstanceP.remove(id,afterSave_network);
	dwr.engine.endBatch();
}
function afterSave_network(){
	viewed_network = -1;
	$("#popupbutton_networklist").click();}

</script>

<div id="datatable-iaas-parent" class="infragrid2">
					<div id="datatable-iaas" >
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="compute-table">
							<thead><tr></tr></thead>
							<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
							</tfoot><tbody></tbody>
						</table>
						<div style="height: 50px;"></div>
						<table align="right">
							<tr>
								<td>
								<!-- <div class="demo" id="popupbutton_compute"><button>Request Compute</button></div> -->
								</td>
								<td>
								<!-- <div class="demo" id="popupbutton_computelist"><button>List Compute</button></div> -->
								</td>
							</tr>
						</table>
				</div>
				</div>
	<div id="popupContactParent_network" >
		<div id="popupContact_network" class="popupContact" >
							<a  onclick="cancelForm_network();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form method="post" name="thisform">
								<p id="contactArea_network" class="contactArea" >
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
										<div class="demo" id="popupbutton_network_create">
											<button onclick="submitForm_network(this.form);return false;">Save</button>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_network(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_network" class="backgroundPopup" ></div>
	</div>				