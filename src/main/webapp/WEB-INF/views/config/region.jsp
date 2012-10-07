<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/RegionService.js'></script>
<script type='text/javascript' src='/dwr/interface/CompanyService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_region = 0;

	//loading popup with jQuery magic!
	function loadPopup_region(){
		//loads popup only if it is disabled
		if(popupStatus_region==0){
			$("#backgroundPopup_region").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_region").fadeIn("slow");
			$("#popupContact_region").fadeIn("slow");
			popupStatus_region = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_region(){
		//disables popup only if it is enabled
		if(popupStatus_region==1){
			$("#backgroundPopup_region").fadeOut("slow");
			$("#popupContact_region").fadeOut("slow");
			popupStatus_region = 0;
		}
	}

	//centering popup
	function centerPopup_region(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_region").height();
		var popupWidth = $("#popupContact_region").width();
		//centering
		$("#popupContact_region").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_region").css({
			"height": windowHeight
		});
		
	}
	var isMycpAdmin = false;
	function findAll_region(p){
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
	            { "sTitle": "Url" },
	            { "sTitle": "Company" },
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
			
			var options = '';
			if(isMycpAdmin){
				options = '<img alt="Edit" src=../images/edit.png onclick=edit_region('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
                '<img alt="Remove" src=../images/deny.png onclick=remove_region('+p[i].id+')>';
			}
			
			oTable.fnAddData( [i+1,p[i].name, p[i].details,p[i].url,comp,
			                   options ] );
		}
		
		
	
	}

	var viewed_region = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_region").click(function(){
			viewed_region = -1;
			//centering with css
			centerPopup_region();
			//load popup
			loadPopup_region();
		});
	
		$("#popupbutton_regionlist").click(function(){
			
				dwr.engine.beginBatch();
				RegionService.findAll(findAll_region);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_region").click(function(){
			viewed_region = -1;
			disablePopup_region();
		});
		//Click out event!
		$("#backgroundPopup_region").click(function(){
			viewed_region = -1;
			disablePopup_region();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_region==1){
				disablePopup_region();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_regionlist").click();
	
			$("#thisform").validate({
			 submitHandler: function(form) {
				 submitForm_region(form);
				 return false;
			 }
			});
         
			
			CompanyService.findAll(function(p){
				dwr.util.removeAllOptions('company');
				dwr.util.addOptions('company', p, 'id', 'name');
			});
			
			$(document).ready(function() {
				CommonService.getCurrentSession(function(p){
					if(p.role != 'ROLE_SUPERADMIN'){
						dwr.util.setValue('only4mycpadmin', '');	
					}else{
						isMycpAdmin = true; 
					}
					
				});
				});
			
		});
		
		
		
function submitForm_region(f){
	 
	var region = {  id:viewed_region,name:null, details:null, url:null, company:{} };
	  dwr.util.getValues(region);
	  
	  region.company.id= dwr.util.getValue("company");
	  
	  if(viewed_region == -1){
		  region.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  RegionService.saveOrUpdate(region,afterSave_region);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_region();
	  viewed_region=-1;
}
function cancelForm_region(f){

	var region = {  id:null,name:null, details:null, url:null, company:{} };
	  dwr.util.setValues(region);
	  viewed_region = -1;
	  disablePopup_region();
}

function afterEdit_region(p){
	var region = eval(p);
	viewed_region=p.id;
	centerPopup_region();
	loadPopup_region();
	dwr.util.setValues(region);
	dwr.util.setValue('company',p.company.id);
}

function edit_region(id){
	RegionService.findById(id,afterEdit_region);
}

function remove_region(id){
	if(!disp_confirm('Region')){
		return false;
	}
	dwr.engine.beginBatch();
	RegionService.remove(id,afterRemove_region);
	dwr.engine.endBatch();
}

function afterRemove_region(p){
		viewed_region = -1;
		$("#popupbutton_regionlist").click();
		$.sticky(p);
	}
	
function afterSave_region(){
	viewed_region = -1;
	$("#popupbutton_regionlist").click();}

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
								<font color="green">Config -- Region</font>
								</td>
								<td width="10%">
									<span id="only4mycpadmin"><div class="demo" id="popupbutton_region"><button>New Region</button></div></span>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_regionlist"><button>List Region</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_region" >
		<div id="popupContact_region" class="popupContact" >
							<a  onclick="cancelForm_region();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Region</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_region" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								  
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Details : </td>
								    <td style="width: 50%;"><input type="text" name="details" id="details" size="30"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Url : </td>
								    <td style="width: 50%;"><input type="text" name="url" id="url" size="30"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Company : </td>
								    <td style="width: 50%;">
								    <select id="company" name="company" style="width: 205px;" class="required">
							    	</select>
								    <!-- <input type="text" name="company" id="company" size="30"> -->
								    </td>
								  </tr> 
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_region_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_region(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_region" class="backgroundPopup" ></div>
	</div>				