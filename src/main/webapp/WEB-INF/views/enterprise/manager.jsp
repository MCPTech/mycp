<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/ManagerService.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_manager = 0;

	//loading popup with jQuery magic!
	function loadPopup_manager(){
		//loads popup only if it is disabled
		if(popupStatus_manager==0){
			$("#backgroundPopup_manager").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_manager").fadeIn("slow");
			$("#popupContact_manager").fadeIn("slow");
			popupStatus_manager = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_manager(){
		//disables popup only if it is enabled
		if(popupStatus_manager==1){
			$("#backgroundPopup_manager").fadeOut("slow");
			$("#popupContact_manager").fadeOut("slow");
			popupStatus_manager = 0;
		}
	}

	//centering popup
	function centerPopup_manager(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_manager").height();
		var popupWidth = $("#popupContact_manager").width();
		//centering
		$("#popupContact_manager").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_manager").css({
			"height": windowHeight
		});
		
	}

	function findAll_manager(p){
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
	            { "sTitle": "Project" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			oTable.fnAddData( [i+1,p[i].firstname, p[i].lastname, p[i].designation,
			                   p[i].email,p[i].phone,p[i].project.name,
			                   '<img alt="Edit" src=../images/edit.png onclick=edit_manager('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img alt="Remove" src=../images/deny.png onclick=remove_manager('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_manager = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_manager").click(function(){
			viewed_manager = -1;
			//centering with css
			centerPopup_manager();
			//load popup
			loadPopup_manager();
		});
	
		$("#popupbutton_managerlist").click(function(){
			
				dwr.engine.beginBatch();
				ManagerService.findAll(findAll_manager);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_manager").click(function(){
			viewed_manager = -1;
			disablePopup_manager();
		});
		//Click out event!
		$("#backgroundPopup_manager").click(function(){
			viewed_manager = -1;
			disablePopup_manager();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_manager==1){
				disablePopup_manager();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_managerlist").click();
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_manager(form);
					 return false;
				 }
				});
			
			ProjectService.findAll(function(p){
				dwr.util.removeAllOptions('project');
				dwr.util.addOptions('project', p, 'id', 'name');
				//dwr.util.setValue(id, sel);
				
			});
			
		});
		
function submitForm_manager(f){

    
    
	var manager = {  id:viewed_manager,firstname:null, lastname:null , designation:null , email:null , phone:null , project:{} };
	  dwr.util.getValues(manager);
	  
	  if(viewed_manager == -1){
		  manager.id  = null; 
	  }
	  manager.project.id=dwr.util.getValue("project");
	  dwr.engine.beginBatch();
	  ManagerService.saveOrUpdate(manager,afterSave_manager);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_manager();
	  viewed_manager=-1;
}
function cancelForm_manager(f){

	var manager = {  id:null,firstname:null, lastname:null , designation:null , email:null , phone:null, project:{}};
	  dwr.util.setValues(manager);
	  viewed_manager = -1;
	  disablePopup_manager();
}

function afterEdit_manager(p){
	var manager = eval(p);
	viewed_manager=p.id;
	centerPopup_manager();
	loadPopup_manager();
	dwr.util.setValues(manager);
	dwr.util.setValue('project',p.project.id);
}

function edit_manager(id){
	ManagerService.findById(id,afterEdit_manager);
}

function remove_manager(id){
	if(!disp_confirm('Manager')){
		return false;
	}
	dwr.engine.beginBatch();
	ManagerService.remove(id,afterSave_manager);
	dwr.engine.endBatch();
}
function afterSave_manager(){
	viewed_manager = -1;
	$("#popupbutton_managerlist").click();}

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
								<font color="green">Enterprise -- Manager</font>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_manager"><button>New Manager</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_managerlist"><button>List Manager</button></div>
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_manager" >
		<div id="popupContact_manager" class="popupContact" >
							<a  onclick="cancelForm_manager();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Manager</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_manager" class="contactArea" >
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
								    <td style="width: 50%;"><input type="text" name="designation" id="designation" size="30" ></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Email : </td>
								    <td style="width: 50%;"><input type="text" name="email" id="email" size="30"  class="email"></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Phone : </td>
								    <td style="width: 50%;"><input type="text" name="phone" id="phone" size="30"  class="number"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Project : </td>
								    <td style="width: 50%;"> 
								    <select id="project" name="project" style="width: 205px;">
							    	</select></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_manager_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_manager(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_manager" class="backgroundPopup" ></div>
	</div>				