<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/QuotaService.js'></script>
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
	var popupStatus_quota = 0;

	//loading popup with jQuery magic!
	function loadPopup_quota(){
		//loads popup only if it is disabled
		if(popupStatus_quota==0){
			$("#backgroundPopup_quota").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_quota").fadeIn("slow");
			$("#popupContact_quota").fadeIn("slow");
			popupStatus_quota = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_quota(){
		//disables popup only if it is enabled
		if(popupStatus_quota==1){
			$("#backgroundPopup_quota").fadeOut("slow");
			$("#popupContact_quota").fadeOut("slow");
			popupStatus_quota = 0;
		}
	}

	//centering popup
	function centerPopup_quota(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_quota").height();
		var popupWidth = $("#popupContact_quota").width();
		//centering
		$("#popupContact_quota").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_quota").css({
			"height": windowHeight
		});
		
	}

	function findAll_quota(p){
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
	            { "sTitle": "Limit" },
	            { "sTitle": "Start" },
	            { "sTitle": "End" },
	            { "sTitle": "Meter Metric" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		var meterMetric='';
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			if(p[i].meterMetric !=null){
				meterMetric=p[i].meterMetric.name;
			}
			
			oTable.fnAddData( [i+1,p[i].name, p[i].quotalimit, dateFormat(p[i].startdate),
			                   dateFormat(p[i].enddate),p[i].meterMetric.name,
			                   '<img alt="Edit" src=../images/edit.png onclick=edit_quota('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img alt="Remove" src=../images/deny.png onclick=remove_quota'+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_quota = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_quota").click(function(){
			viewed_quota = -1;
			//centering with css
			centerPopup_quota();
			//load popup
			loadPopup_quota();
		});
	
		$("#popupbutton_quotalist").click(function(){
			
				dwr.engine.beginBatch();
				QuotaService.findAll(findAll_quota);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_quota").click(function(){
			viewed_quota = -1;
			disablePopup_quota();
		});
		//Click out event!
		$("#backgroundPopup_quota").click(function(){
			viewed_quota = -1;
			disablePopup_quota();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_quota==1){
				disablePopup_quota();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_quotalist").click();
			
			MeterMetricService.findAll(function(p){
				dwr.util.removeAllOptions('meterMetric');
				dwr.util.addOptions('meterMetric', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_quota(form);
					 return false;
				 }
				});
			
			$( "#startdate" ).datepicker();
			$( "#enddate" ).datepicker();
			
		});
		
function submitForm_quota(f){
	  
      try{
	var quota = {  id:viewed_quota,name:null, quotalimit:null,  meterMetric:{}};
	  dwr.util.getValues(quota);

	  quota.startdate = Date.parse(dwr.util.getValue("startdate"));
	  quota.enddate = Date.parse(dwr.util.getValue("enddate"));
	  quota.meterMetric.id=dwr.util.getValue("meterMetric");
	  
	  if(viewed_quota == -1){
		  quota.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  	QuotaService.saveOrUpdate(quota,afterSave_quota);
	  dwr.engine.endBatch();
	  
	  disablePopup_quota();
	  viewed_quota=-1;
      }catch(e){alert(e);}
}
function cancelForm_quota(f){

	var quota = {  id:null,name:null, quotalimit:null, startdate:null,enddate:null,meterMetric:null };
	  dwr.util.setValues(quota);
	  viewed_quota = -1;
	  disablePopup_quota();
}

function afterEdit_quota(p){
	var quota = eval(p);
	p.startdate=dateFormat(p.startdate);
	p.enddate=dateFormat(p.enddate);
	viewed_quota=p.id;
	centerPopup_quota();
	loadPopup_quota();
	dwr.util.setValues(quota);
	dwr.util.setValue('meterMetric',p.meterMetric.id);
	
}

function edit_quota(id){
	QuotaService.findById(id,afterEdit_quota);
}

function remove_quota(id){
	if(!disp_confirm('Quota')){
		return false;
	}
	dwr.engine.beginBatch();
	QuotaService.remove(id,afterSave_quota);
	dwr.engine.endBatch();
}
function afterSave_quota(){
	viewed_quota = -1;
	$("#popupbutton_quotalist").click();}

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
								<font color="green">Config -- Quota</font>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_quota"><button>New Quota</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_quotalist"><button>List Quota</button></div>
								</td>
							</tr>
						</table>
				</div>
				</div>
				
	<div id="popupContactParent_quota" >
		<div id="popupContact_quota" class="popupContact" >
							<a  onclick="cancelForm_quota();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Quota</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_quota" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Limit : </td>
								    <td style="width: 50%;"><input type="text" name="quotalimit" id="quotalimit" size="30" class="number"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Start : </td>
								    <td style="width: 50%;"><input type="text" name="startdate" id="startdate" size="30" ></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">End : </td>
								    <td style="width: 50%;"><input type="text" name="enddate" id="enddate" size="30" ></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Meter Metric : </td>
								    <td style="width: 50%;">
								    <select id="meterMetric" name="meterMetric" style="width: 205px;">
							    	</select>
								    </td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_quota_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_quota(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_quota" class="backgroundPopup" ></div>
	</div>				