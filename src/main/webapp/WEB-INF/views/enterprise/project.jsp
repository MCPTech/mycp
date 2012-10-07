<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
<script type='text/javascript' src='/dwr/interface/DepartmentService.js'></script>
<script type='text/javascript' src='/dwr/interface/CompanyService.js'></script>
<script type='text/javascript' src='/dwr/interface/QuotaService.js'></script>


<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_project = 0;

	//loading popup with jQuery magic!
	function loadPopup_project(){
		//loads popup only if it is disabled
		if(popupStatus_project==0){
			$("#backgroundPopup_project").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_project").fadeIn("slow");
			$("#popupContact_project").fadeIn("slow");
			popupStatus_project = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_project(){
		//disables popup only if it is enabled
		if(popupStatus_project==1){
			$("#backgroundPopup_project").fadeOut("slow");
			$("#popupContact_project").fadeOut("slow");
			popupStatus_project = 0;
		}
	}

	//centering popup
	function centerPopup_project(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_project").height();
		var popupWidth = $("#popupContact_project").width();
		//centering
		$("#popupContact_project").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_project").css({
			"height": windowHeight
		});
		
	}

	function findAll_project(p){
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
	            //{ "sTitle": "Company" },
	            { "sTitle": "Department" },
	            //{ "sTitle": "Quota" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		var department='';
		var company='';
		var quota='';
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			if(p[i].department !=null){
				department=p[i].department.name;
			}
/* 			if(p[i].company !=null){
				company = p[i].company.name;
			}
 */			/* if(p[i].quota !=null){
				quota = p[i].quota.name;
			} */
			
			oTable.fnAddData( [i+1,p[i].name, p[i].details, 
			                   department,
			                   '<img class="clickimg" title="Edit"  alt="Edit" src=../images/edit.png onclick=edit_project('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Remove"  alt="Remove" src=../images/deny.png onclick=remove_project('+p[i].id+')>' ] );
		}
	}

	var viewed_project = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_project").click(function(){
			viewed_project = -1;
			//centering with css
			centerPopup_project();
			//load popup
			loadPopup_project();
		});
	
		$("#popupbutton_projectlist").click(function(){
			
				dwr.engine.beginBatch();
				ProjectService.findAll(findAll_project);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_project").click(function(){
			viewed_project = -1;
			disablePopup_project();
		});
		//Click out event!
		$("#backgroundPopup_project").click(function(){
			viewed_project = -1;
			disablePopup_project();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_project==1){
				disablePopup_project();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_projectlist").click();
           
           $("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_project(form);
					 return false;
				 }
				});
			
           
			/* CompanyService.findAll(function(p){
				dwr.util.removeAllOptions('company');
				dwr.util.addOptions('company', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
			}); */
			DepartmentService.findAll(function(p){
				dwr.util.removeAllOptions('department');
				//dwr.util.addOptions('department', p, 'id', 'name');
				dwr.util.addOptions('department', p, 'id', function(p) {
					return p.name+' @ '+p.company.name;
				});
				//dwr.util.setValue(id, sel);
				
			});
			/* QuotaService.findAll(function(p){
				dwr.util.removeAllOptions('quota');
				dwr.util.addOptions('quota', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
				
			}); */
			
		});
		
function submitForm_project(f){
	  
      
	//var project = {  id:viewed_project,name:null, details:null, company:{}, department:{}, quota:{} };
	var project = {  id:viewed_project,name:null, details:null, department:{} };
	  dwr.util.getValues(project);
	  
	  project.department.id= dwr.util.getValue("department");
	  //project.company.id= dwr.util.getValue("company");
	  //project.quota.id= dwr.util.getValue("quota");
	  
	  if(viewed_project == -1){
		  project.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  ProjectService.saveOrUpdate(project,afterSave_project);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_project();
	  viewed_project=-1;
}
function cancelForm_project(f){

	var project = {  id:null,name:null, details:null,  department:{} };
	  dwr.util.setValues(project);
	  viewed_project = -1;
	  disablePopup_project();
}

function afterEdit_project(p){
	var project = eval(p);
	viewed_project=p.id;
	centerPopup_project();
	loadPopup_project();
	dwr.util.setValues(project);
	//dwr.util.setValue('company',p.company.id);
	dwr.util.setValue('department',p.department.id);
	//dwr.util.setValue('quota',p.quota.id);
    
    
}

function edit_project(id){
	ProjectService.findById(id,afterEdit_project);
}

function remove_project(id){
	if(!disp_confirm('Project')){
		return false;
	}
	dwr.engine.beginBatch();
	ProjectService.remove(id,afterSave_project);
	dwr.engine.endBatch();
}
function afterSave_project(){
	viewed_project = -1;
	$("#popupbutton_projectlist").click();}

</script>
<p class="dataTableHeader">Project Setup</p>
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
									<div class="demo" id="popupbutton_project"><button>New Project</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_projectlist"><button>List Projects</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_project" >
		<div id="popupContact_project" class="popupContact" >
							<a  onclick="cancelForm_project();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Project</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_project" class="contactArea" >
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
								  <!-- <tr>
								    <td style="width: 50%;">Company : </td>
								    <td style="width: 50%;"><select id="company" name="company" style="width: 205px;">
							    	</select>
							    	</td>
								  </tr>
								   -->
								  <tr>
								    <td style="width: 50%;">Department : </td>
								    <td style="width: 50%;"><select id="department" name="department" style="width: 205px;" class="required">
							    	</select></td>
								  </tr>
								   <!-- <tr>
								    <td style="width: 50%;">Quota : </td>
								    <td style="width: 50%;"><select id="quota" name="quota" style="width: 205px;">
							    	</select></td>
								  </tr>
								   -->
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_project_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											
											<button onclick="cancelForm_project(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_project" class="backgroundPopup" ></div>
	</div>				