<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/DepartmentService.js'></script>
<script type='text/javascript' src='/dwr/interface/CompanyService.js'></script>
<script type='text/javascript' src='/dwr/interface/ManagerService.js'></script>
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
	var popupStatus_department = 0;

	//loading popup with jQuery magic!
	function loadPopup_department(){
		//loads popup only if it is disabled
		if(popupStatus_department==0){
			$("#backgroundPopup_department").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_department").fadeIn("slow");
			$("#popupContact_department").fadeIn("slow");
			popupStatus_department = 1;
			
			
		}
	}
	
	//disabling popup with jQuery magic!
	function disablePopup_department(){
		//disables popup only if it is enabled
		if(popupStatus_department==1){
			$("#backgroundPopup_department").fadeOut("slow");
			$("#popupContact_department").fadeOut("slow");
			popupStatus_department = 0;
		}
	}

	//centering popup
	function centerPopup_department(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_department").height();
		var popupWidth = $("#popupContact_department").width();
		//centering
		$("#popupContact_department").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_department").css({
			"height": windowHeight
		});
		
	}

	function findAll_department(p){
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
	            //{ "sTitle": "Manager" },
	            { "sTitle": "Company" },
	            //{ "sTitle": "Quota" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		var manager='';
		var company='';
		var quota='';
			
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			/* if(p[i].manager !=null){
				manager=p[i].manager.firstname+' '+p[i].manager.lastname;
			} */
			if(p[i].company !=null){
				company = p[i].company.name;
			}
			/* if(p[i].quota !=null){
				quota = p[i].quota.name;
			} */
			
			oTable.fnAddData( [i+1,p[i].name,  company,
			                   '<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_department('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_department('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_department = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_department").click(function(){
			viewed_department = -1;
			//centering with css
			centerPopup_department();
			//load popup
			loadPopup_department();
			
		});
	
		$("#popupbutton_departmentlist").click(function(){
			
				dwr.engine.beginBatch();
				DepartmentService.findAll(findAll_department);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_department").click(function(){
			viewed_department = -1;
			disablePopup_department();
		});
		//Click out event!
		$("#backgroundPopup_department").click(function(){
			viewed_department = -1;
			disablePopup_department();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_department==1){
				disablePopup_department();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_departmentlist").click();
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_department(form);
					 return false;
				 }
				});
			
			CompanyService.findAll(function(p){
				dwr.util.removeAllOptions('company');
				dwr.util.addOptions('company', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
			});
			/* ManagerService.findAll(function(p){
				dwr.util.removeAllOptions('manager');
				dwr.util.addOptions('manager', p, 'id', 'firstname');
				//dwr.util.setValue(id, sel);
				
			});
			QuotaService.findAll(function(p){
				dwr.util.removeAllOptions('quota');
				dwr.util.addOptions('quota', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
				
			}); */
		});
		
function submitForm_department(f){

	var department = {  id:viewed_department,name:null, company:{}};
	  dwr.util.getValues(department);
	  
	  department.company.id= dwr.util.getValue("company");
	  /* department.quota.id= dwr.util.getValue("quota");
	  department.manager.id= dwr.util.getValue("manager"); */
	  
	  if(viewed_department == -1){
		  department.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  DepartmentService.saveOrUpdate(department,afterSave_department);
	  dwr.engine.endBatch();
	  disablePopup_department();
	  viewed_department=-1;
}
function cancelForm_department(f){

	var department = {  id:null,name:null, company:null };
	  dwr.util.setValues(department);
	  viewed_department = -1;
	  disablePopup_department();
}

function afterEdit_department(p){
	var department = eval(p);
	viewed_department=p.id;
	centerPopup_department();
	loadPopup_department();
	dwr.util.setValues(department);
	
	dwr.util.setValue('company',p.company.id);
	/* dwr.util.setValue('manager',p.manager.id);
	dwr.util.setValue('quota',p.quota.id); */
}

function edit_department(id){
	DepartmentService.findById(id,afterEdit_department);
}

function remove_department(id){
	if(!disp_confirm('Department')){
		return false;
	}
	dwr.engine.beginBatch();
	DepartmentService.remove(id,afterRemove_department);
	dwr.engine.endBatch();
}

function afterRemove_department(p){
	viewed_department = -1;
	$("#popupbutton_departmentlist").click();
	$.sticky(p);
	}


function afterSave_department(){
	viewed_department = -1;
	$("#popupbutton_departmentlist").click();}

</script>
<p class="dataTableHeader">Department Setup</p>
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
									<div class="demo" id="popupbutton_department"><button>New Department</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_departmentlist"><button>List Departments</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_department" >
		<div id="popupContact_department" class="popupContact" >
							<a  onclick="cancelForm_department();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Department</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_department" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Company : </td>
								    <td style="width: 50%;">
								    <select id="company" name="company" style="width: 205px;" class="required">
							    	</select>
								    <!-- <input type="text" name="company" id="company" size="30"> -->
								    </td>
								  </tr>
								 <!--  <tr>
								    <td style="width: 50%;">Manager : </td>
								    <td style="width: 50%;">
								    <select id="manager" name="manager" style="width: 205px;">
							    	</select>
								    <input type="text" name="manager" id="manager" size="30">
								    </td>
								  </tr> -->
								  <!-- 
								  <tr>
								    <td style="width: 50%;">Quota : </td>
								    <td style="width: 50%;">
								    <select id="quota" name="quota" style="width: 205px;">
							    	</select>
								    <input type="text" name="quota" id="quota" size="30">
								    </td>
								  </tr> -->
								   
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_department_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_department(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_department" class="backgroundPopup" ></div>
	</div>				