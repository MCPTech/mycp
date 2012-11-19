<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/RealmService.js'></script>
<!-- <script type='text/javascript' src='/dwr/interface/ManagerService.js'></script>
<script type='text/javascript' src='/dwr/interface/QuotaService.js'></script> -->
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
<script type='text/javascript' src='/dwr/interface/DepartmentService.js'></script>

<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
//SETTING UP OUR POPUP
	//0 means disabled; 1 means enabled;
	var popupStatus_user = 0;

	//loading popup with jQuery magic!
	function loadPopup_user(){
		//loads popup only if it is disabled
		if(popupStatus_user==0){
			$("#backgroundPopup_user").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_user").fadeIn("slow");
			$("#popupContact_user").fadeIn("slow");
			popupStatus_user = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_user(){
		//disables popup only if it is enabled
		if(popupStatus_user==1){
			$("#backgroundPopup_user").fadeOut("slow");
			$("#popupContact_user").fadeOut("slow");
			popupStatus_user = 0;
		}
	}

	//centering popup
	function centerPopup_user(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_user").height();
		var popupWidth = $("#popupContact_user").width();
		//centering
		$("#popupContact_user").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_user").css({
			"height": windowHeight
		});
		
	}

	function findAll_user(p){
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
	            { "sTitle": "Email" },
	            { "sTitle": "Registered Date" },
	            { "sTitle": "Status" },
	            { "sTitle": "Role" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			if('1' == p[i].active){
				p[i].active='<img title="active" alt="active" src=/images/running.png>&nbsp;';
			}else{
				p[i].active='<img title="disabled" alt="disabled" src=/images/waiting.png>&nbsp;';
			}
			oTable.fnAddData( [i+1,p[i].email, dateFormat(p[i].registereddate), p[i].active,
			                   p[i].role.name,
			                   '<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_user('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_user('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_user = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_user").click(function(){
			viewed_user = -1;
			//centering with css
			centerPopup_user();
			//load popup
			loadPopup_user();
			dwr.util.setValue('active',true);
			
		});
	
		$("#popupbutton_userlist").click(function(){
			
				dwr.engine.beginBatch();
				RealmService.findAll(findAll_user);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_user").click(function(){
			viewed_user = -1;
			disablePopup_user();
		});
		//Click out event!
		$("#backgroundPopup_user").click(function(){
			viewed_user = -1;
			disablePopup_user();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_user==1){
				disablePopup_user();
			}
		});
		
		$(document).ready(function() {
			$("#popupbutton_userlist").click();

			RealmService.findAllRoles(function(p){
				dwr.util.removeAllOptions('role');
				dwr.util.addOptions('role', p, 'id', 'name');
			});

		/* 	QuotaService.findAll(function(p){
	  				dwr.util.removeAllOptions('quota');
	  				dwr.util.addOptions('quota', p, 'id', 'name');
	  			});
	              
	              ManagerService.findAll(function(p){
	  				dwr.util.removeAllOptions('manager');
	  				dwr.util.addOptions('manager', ["Please select ..."]);
	  				dwr.util.addOptions('manager', p, 'id', 'firstname');
	  			}); */
			
	  			ProjectService.findAll(function(p){
					dwr.util.removeAllOptions('projects');
					//dwr.util.addOptions('project', p, 'id', 'name');
					dwr.util.addOptions('projects', p, 'id', function(p) {
						return p.name+' @ '+p.department.name;
					});
					//dwr.util.setValue(id, sel);
					
				});
	  			
	  			DepartmentService.findAll(function(p){
					dwr.util.removeAllOptions('department');
					//dwr.util.addOptions('department', p, 'id', 'name');
					dwr.util.addOptions('department', p, 'id', function(p) {
						return p.name;
					});
					//dwr.util.setValue(id, sel);
					
				});
	  			
	  			$( '#email').blur( function() {
	  				RealmService.emailExists(this.value, function(p) {
	  					//alert(p);
	  					if(p){
	  						$( '#email' ).select();
	  						$( '#email' ).val('Email already taken.Choose another.');
		  					//alert('Exists');
		  				}else{
		  					//alert('ok');
		  				}
	  				})});
	  			
		
	  			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_user(form);
					 return false;
				 }
				});
		});
		
	function submitForm_user(f){
		//alert(f);
		var user = {  id:viewed_user,email:null, password:null,active:null,role:{},firstName:null, lastName:null,designation:null,phone:null, projects:{}, department:{}};
		  dwr.util.getValues(user);
		  user.role.id= dwr.util.getValue("role");
		  //user.projects=dwr.util.getValue("projects");
		  //user.projects[1].id=dwr.util.getValue("project")[1];
		  user.department.id=dwr.util.getValue("department");
		 // alert(user.role.id);
		  /* try{
			if('Please select ...' == dwr.util.getValue("manager")){
				user.manager.id=-1;
			}else{
			  user.manager.id= dwr.util.getValue("manager");
			}
		  	}catch(e){}
		  try{user.quota.id= dwr.util.getValue("quota");}catch(e){} */
		  if(viewed_user == -1){
			  user.id  = null; 
		  }
		  
		  dwr.engine.beginBatch();
		  RealmService.saveOrUpdate(user,afterSave_user);
		  dwr.engine.endBatch();
		  
		  disablePopup_user();
		  viewed_user=-1;
	}
	function cancelForm_user(f){
	
		var user = {  id:null,email:null, password:null,active:null,role:{},firstName:null, lastName:null,designation:null,phone:null, projects:{}, department:{}};
		  dwr.util.setValues(user);
		  viewed_user = -1;
		  disablePopup_user();
	}
	
	function afterEdit_user(p){
		var user = eval(p);
		viewed_user=p.id;
		centerPopup_user();
		loadPopup_user();
		dwr.util.setValues(user);
		//alert(user.role.id);
		$('#email').attr("readonly", true); 
		dwr.util.setValue('role',user.role.id);
		dwr.util.setValue('department',user.department.id);
		if(user.projects !=null){
			dwr.util.setValue('project',user.project.id);
		}
		/* try{dwr.util.setValue('manager',user.manager.id);}catch(e){}
		try{dwr.util.setValue('quota',user.quota.id);}catch(e){} */
	}
	
	function edit_user(id){
		RealmService.findById(id,afterEdit_user);
	}
	
	function remove_user(id){
		if(!disp_confirm('User')){
			return false;
		}
		dwr.engine.beginBatch();
		RealmService.remove(id,afterSave_user);
		dwr.engine.endBatch();
	}
	function afterSave_user(){
		viewed_user = -1;
		$("#popupbutton_userlist").click();}

</script>
<p class="dataTableHeader">User Setup</p>
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
									<div class="demo" id="popupbutton_user"><button>New User</button></div>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_userlist"><button>List Users</button></div>
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_user" >
		<div id="popupContact_user" class="popupContact" >
							<a  onclick="cancelForm_user();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>User</h1>
							<form  class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_user" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Email : </td>
								    <td style="width: 50%;"><input type="text" name="email" id="email" size="30" class="email required"></td>
								    
								  </tr>
								  <tr>
								    <td style="width: 50%;">First Name : </td>
								    <td style="width: 50%;"><input type="text" name="firstName" id="firstName" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Last Name : </td>
								    <td style="width: 50%;"><input type="text" name="lastName" id="lastName" size="30" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Password : </td>
								    <td style="width: 50%;"><input type="password" name="password" id="password" size="30" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Active? : </td>
								    <td style="width: 50%;"><input type="checkbox" name="active" id="active"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Role : </td>
								    <td style="width: 50%;"><select id="role" name="role" style="width: 205px;" class="required"></select></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Designation : </td>
								    <td style="width: 50%;"><input type="text" name="designation" id="designation" size="30"></td>
								  </tr>
								  
								   <tr>
								    <td style="width: 50%;">Phone : </td>
								    <td style="width: 50%;"><input type="text" name="phone" id="phone" size="30" class="number"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Project : </td>
								    <td style="width: 50%;"> 
								    <select id="projects" name="projects" style="width: 205px;" class="required" multiple>
							    	</select></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Department : </td>
								    <td style="width: 50%;"> 
								    <select id="department" name="department" style="width: 205px;" class="required">
							    	</select></td>
								  </tr>
								  <!--  <tr>
								    <td style="width: 50%;">Manager : </td>
								    <td style="width: 50%;">
								    <select id="manager" name="manager" style="width: 205px;">
							    	</select>
								    </td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Quota : </td>
								    <td style="width: 50%;"><select id="quota" name="quota" style="width: 205px;">
							    	</select></td>
								  </tr> -->
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_user_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_user(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_user" class="backgroundPopup" ></div>
	</div>				