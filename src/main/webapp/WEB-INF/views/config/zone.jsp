<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/ZoneService.js'></script>
<script type='text/javascript' src='/dwr/interface/InfraService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_zone = 0;

	//loading popup with jQuery magic!
	function loadPopup_zone(){
		//loads popup only if it is disabled
		if(popupStatus_zone==0){
			$("#backgroundPopup_zone").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_zone").fadeIn("slow");
			$("#popupContact_zone").fadeIn("slow");
			popupStatus_zone = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_zone(){
		//disables popup only if it is enabled
		if(popupStatus_zone==1){
			$("#backgroundPopup_zone").fadeOut("slow");
			$("#popupContact_zone").fadeOut("slow");
			popupStatus_zone = 0;
		}
	}

	//centering popup
	function centerPopup_zone(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_zone").height();
		var popupWidth = $("#popupContact_zone").width();
		//centering
		$("#popupContact_zone").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_zone").css({
			"height": windowHeight
		});
		
	}
	var isMycpAdmin = false;
	function findAll_zone(p){
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
	            { "sTitle": "Region" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var comp = '';
			if(p[i].company !=null){
				comp = p[i].company.name;
			}
			
			var options = '<img alt="Edit" src=../images/edit.png onclick=edit_zone('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
                '<img alt="Remove" src=../images/deny.png onclick=remove_zone('+p[i].id+')>';
			
			
			oTable.fnAddData( [i+1,p[i].name, p[i].messages,p[i].regionName,p[i].infraId.name,
			                   options ] );
		}
		
		
	
	}

	var viewed_zone = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_zone").click(function(){
			viewed_zone = -1;
			//centering with css
			centerPopup_zone();
			//load popup
			loadPopup_zone();
		});
	
		$("#popupbutton_zonelist").click(function(){
			
				dwr.engine.beginBatch();
				ZoneService.findAll(findAll_zone);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_zone").click(function(){
			viewed_zone = -1;
			disablePopup_zone();
		});
		//Click out event!
		$("#backgroundPopup_zone").click(function(){
			viewed_zone = -1;
			disablePopup_zone();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_zone==1){
				disablePopup_zone();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_zonelist").click();
	
			$("#thisform").validate({
			 submitHandler: function(form) {
				 submitForm_zone(form);
				 return false;
			 }
			});
         
			
			InfraService.findAll(function(p){
				dwr.util.removeAllOptions('infraId');
				dwr.util.addOptions('infraId', p, 'id', 'name');
			});
			

			
		});
		
		
		
function submitForm_zone(f){
	
		var zone = {  id:viewed_zone,name:null, messages:null, regionName:null, infraId:{} };
		  dwr.util.getValues(zone);
		  
		  zone.infraId.id= dwr.util.getValue("infraId");
		  
		  if(viewed_zone == -1){
			  zone.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  ZoneService.saveOrUpdate(zone,afterSave_zone);
		  dwr.engine.endBatch();
		  disablePopup_zone();
		  viewed_zone=-1;
	}
	
	function cancelForm_zone(f){
	
		var zone = {  id:null,name:null, messages:null, regionName:null, infraId:{} };
		  dwr.util.setValues(zone);
		  viewed_zone = -1;
		  disablePopup_zone();
	}
	
	function afterEdit_zone(p){
		var zone = eval(p);
		viewed_zone=p.id;
		centerPopup_zone();
		loadPopup_zone();
		dwr.util.setValues(zone);
		dwr.util.setValue('infraId',p.infraId.id);
	}
	
	function edit_zone(id){
		ZoneService.findById(id,afterEdit_zone);
	}
	
	function remove_zone(id){
		if(!disp_confirm('Zone')){
			return false;
		}
		dwr.engine.beginBatch();
		ZoneService.remove(id,afterRemove_zone);
		dwr.engine.endBatch();
	}
	
	function afterRemove_zone(p){
			viewed_zone = -1;
			$("#popupbutton_zonelist").click();
			$.sticky(p);
		}
		
	function afterSave_zone(){
		viewed_zone = -1;
		$("#popupbutton_zonelist").click();}

</script>
<p class="dataTableHeader">Zone Configuration</p>
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
									<div class="demo" id="popupbutton_zone"><button>Configure New Zone</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_zonelist"><button>List All Zones</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_zone" >
		<div id="popupContact_zone" class="popupContact" >
							<a  onclick="cancelForm_zone();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Availability Zone</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_zone" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								  
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Details : </td>
								    <td style="width: 50%;"><input type="text" name="messages" id="messages" size="30"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Region : </td>
								    <td style="width: 50%;"><input type="text" name="regionName" id="regionName" size="30"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Cloud : </td>
								    <td style="width: 50%;">
								    <select id="infraId" name="infraId" style="width: 205px;" class="required">
							    	</select>
								    </td>
								  </tr> 
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_zone_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_zone(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_zone" class="backgroundPopup" ></div>
	</div>				