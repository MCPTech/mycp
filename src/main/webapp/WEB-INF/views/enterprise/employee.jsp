<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/EmployeeService.js'></script>
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
	var popupStatus_employee = 0;

	//loading popup with jQuery magic!
	function loadPopup_employee(){
		//loads popup only if it is disabled
		if(popupStatus_employee==0){
			$("#backgroundPopup_employee").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_employee").fadeIn("slow");
			$("#popupContact_employee").fadeIn("slow");
			popupStatus_employee = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_employee(){
		//disables popup only if it is enabled
		if(popupStatus_employee==1){
			$("#backgroundPopup_employee").fadeOut("slow");
			$("#popupContact_employee").fadeOut("slow");
			popupStatus_employee = 0;
		}
	}

	//centering popup
	function centerPopup_employee(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_employee").height();
		var popupWidth = $("#popupContact_employee").width();
		//centering
		$("#popupContact_employee").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_employee").css({
			"height": windowHeight
		});
		
	}

	function findAll_employee(p){
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
	            { "sTitle": "First Name" },
	            { "sTitle": "Last Name" },
	            { "sTitle": "Designation" },
	            { "sTitle": "Email" },
	            { "sTitle": "Phone" },
	            { "sTitle": "Manager" },
	            { "sTitle": "Quota" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		var manager='';
		var quota='';
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			if(p[i].manager !=null){
				manager=p[i].manager.firstname+' '+p[i].manager.lastname;
			}
			
			if(p[i].quota !=null){
				quota = p[i].quota.name;
			}
			
			
			oTable.fnAddData( [i+1,p[i].firstname, p[i].lastname, p[i].designation,
			                   p[i].email,p[i].phone,manager,quota,
			                   '<img alt="Edit" src=../images/edit.png onclick=edit_employee('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img alt="Remove" src=../images/deny.png onclick=remove_employee('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_employee = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_employee").click(function(){
			viewed_employee = -1;
			//centering with css
			centerPopup_employee();
			//load popup
			loadPopup_employee();
		});
	
		$("#popupbutton_employeelist").click(function(){
			
				dwr.engine.beginBatch();
				EmployeeService.findAll(findAll_employee);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_employee").click(function(){
			viewed_employee = -1;
			disablePopup_employee();
		});
		//Click out event!
		$("#backgroundPopup_employee").click(function(){
			viewed_employee = -1;
			disablePopup_employee();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_employee==1){
				disablePopup_employee();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_employeelist").click();
              
              $("#thisform").validate({
 				 submitHandler: function(form) {
 					submitForm_employee(form);
 					 return false;
 				 }
 				});
              
              QuotaService.findAll(function(p){
  				dwr.util.removeAllOptions('quota');
  				dwr.util.addOptions('quota', p, 'id', 'name');
  				//dwr.util.setValue(id, sel);
  				
  			});
              
              ManagerService.findAll(function(p){
  				dwr.util.removeAllOptions('manager');
  				dwr.util.addOptions('manager', p, 'id', 'firstname');
  				//dwr.util.setValue(id, sel);
  				
  			});
              
		});
		
function submitForm_employee(f){
     
	var employee = {  id:viewed_employee,firstname:null, lastname:null,designation:null,email:null,phone:null,manager:{}, quota:{}};
	  dwr.util.getValues(employee);
	  
	  employee.manager.id= dwr.util.getValue("manager");
	  employee.quota.id= dwr.util.getValue("quota");
	  
	  if(viewed_employee == -1){
		  employee.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  EmployeeService.saveOrUpdate(employee,afterSave_employee);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_employee();
	  viewed_employee=-1;
}
function cancelForm_employee(f){

	var employee = {  id:null,firstname:null, lastname:null,designation:null,email:null,phone:null,manager:{}, quota:{}};
	  dwr.util.setValues(employee);
	  viewed_employee = -1;
	  disablePopup_employee();
}

function afterEdit_employee(p){
	var employee = eval(p);
	viewed_employee=p.id;
	centerPopup_employee();
	loadPopup_employee();
	dwr.util.setValues(employee);
	
	dwr.util.setValue('manager',p.manager.id);
	dwr.util.setValue('quota',p.quota.id);
}

function edit_employee(id){
	EmployeeService.findById(id,afterEdit_employee);
}

function remove_employee(id){
	if(!disp_confirm('Employee')){
		return false;
	}
	dwr.engine.beginBatch();
	EmployeeService.remove(id,afterSave_employee);
	dwr.engine.endBatch();
}
function afterSave_employee(){
	viewed_employee = -1;
	$("#popupbutton_employeelist").click();}

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
								<font color="green">Enterprise -- Employee</font>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_employee"><button>New Employee</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_employeelist"><button>List Employee</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_employee" >
		<div id="popupContact_employee" class="popupContact" >
							<a  onclick="cancelForm_employee();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Employee</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_employee" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">First Name : </td>
								    <td style="width: 50%;"><input type="text" name="firstname" id="firstname" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Last Name : </td>
								    <td style="width: 50%;"><input type="text" name="lastname" id="lastname" size="30" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Designation : </td>
								    <td style="width: 50%;"><input type="text" name="designation" id="designation" size="30"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Email : </td>
								    <td style="width: 50%;"><input type="text" name="email" id="email" size="30" class="email"></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Phone : </td>
								    <td style="width: 50%;"><input type="text" name="phone" id="phone" size="30" class="number"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Manager : </td>
								    <td style="width: 50%;">
								    <select id="manager" name="manager" style="width: 205px;">
							    	</select>
								    <!-- <input type="text" name="manager" id="manager" size="30"> -->
								    </td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Quota : </td>
								    <td style="width: 50%;"><select id="quota" name="quota" style="width: 205px;">
							    	</select></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_employee_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_employee(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_employee" class="backgroundPopup" ></div>
	</div>				