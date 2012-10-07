<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/MeterMetricService.js'></script>
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_metermetric = 0;

	//loading popup with jQuery magic!
	function loadPopup_metermetric(){
		//loads popup only if it is disabled
		if(popupStatus_metermetric==0){
			$("#backgroundPopup_metermetric").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_metermetric").fadeIn("slow");
			$("#popupContact_metermetric").fadeIn("slow");
			popupStatus_metermetric = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_metermetric(){
		//disables popup only if it is enabled
		if(popupStatus_metermetric==1){
			$("#backgroundPopup_metermetric").fadeOut("slow");
			$("#popupContact_metermetric").fadeOut("slow");
			popupStatus_metermetric = 0;
		}
	}

	//centering popup
	function centerPopup_metermetric(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_metermetric").height();
		var popupWidth = $("#popupContact_metermetric").width();
		//centering
		$("#popupContact_metermetric").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_metermetric").css({
			"height": windowHeight
		});
		
	}

	function findAll_metermetric(p){
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
	            { "sTitle": "Datatype" },
	            { "sTitle": "Description" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			oTable.fnAddData( [i+1,p[i].name, p[i].datatype, p[i].description,
			                   '<img alt="Edit" src=../images/edit.png onclick=edit_metermetric('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img alt="Remove" src=../images/deny.png onclick=remove_metermetric('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_metermetric = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_metermetric").click(function(){
			viewed_metermetric = -1;
			//centering with css
			centerPopup_metermetric();
			//load popup
			loadPopup_metermetric();
		});
	
		$("#popupbutton_metermetriclist").click(function(){
			
				dwr.engine.beginBatch();
				MeterMetricService.findAll(findAll_metermetric);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_metermetric").click(function(){
			viewed_metermetric = -1;
			disablePopup_metermetric();
		});
		//Click out event!
		$("#backgroundPopup_metermetric").click(function(){
			viewed_metermetric = -1;
			disablePopup_metermetric();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_metermetric==1){
				disablePopup_metermetric();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_metermetriclist").click();
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_metermetric(form);
					 return false;
				 }
				});
		});
		
function submitForm_metermetric(f){
	var metermetric = {  id:viewed_metermetric,name:null, datatype:null , description:null};
	  dwr.util.getValues(metermetric);
	  
	  if(viewed_metermetric == -1){
		  metermetric.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  MeterMetricService.saveOrUpdate(metermetric,afterSave_metermetric);
	  dwr.engine.endBatch();
	  disablePopup_metermetric();
	  viewed_metermetric=-1;
}
function cancelForm_metermetric(f){

	var metermetric = {  id:null,name:null, datatype:null , description:null };
	  dwr.util.setValues(metermetric);
	  viewed_metermetric = -1;
	  disablePopup_metermetric();
}

function afterEdit_metermetric(p){
	var metermetric = eval(p);
	viewed_metermetric=p.id;
	centerPopup_metermetric();
	loadPopup_metermetric();
	dwr.util.setValues(metermetric);
}

function edit_metermetric(id){
	MeterMetricService.findById(id,afterEdit_metermetric);
}

function remove_metermetric(id){
	if(!disp_confirm('Meter Metric')){
		return false;
	}
	dwr.engine.beginBatch();
	MeterMetricService.remove(id,afterSave_metermetric);
	dwr.engine.endBatch();
}
function afterSave_metermetric(){
	viewed_metermetric = -1;
	$("#popupbutton_metermetriclist").click();}

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
								<font color="green">Config -- Meter Metric</font>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_metermetric"><button>New Meter Metric</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_metermetriclist"><button>List Meter Metric</button></div>
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_metermetric" >
		<div id="popupContact_metermetric" class="popupContact" >
							<a  onclick="cancelForm_metermetric();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Meter Metric</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_metermetric" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>

								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								    <tr>
								    <td style="width: 50%;">Description : </td>
								    <td style="width: 50%;"><input type="text" name="description" id="description" size="30" ></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Datatype : </td>
								    <td style="width: 50%;"><input type="text" name="datatype" id="datatype" size="30" ></td>
								  </tr>
								
								  
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_metermetric_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_metermetric(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_metermetric" class="backgroundPopup" ></div>
	</div>				