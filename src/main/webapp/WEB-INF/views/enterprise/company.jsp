<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
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
	var popupStatus_company = 0;

	//loading popup with jQuery magic!
	function loadPopup_company(){
		//loads popup only if it is disabled
		if(popupStatus_company==0){
			$("#backgroundPopup_company").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_company").fadeIn("slow");
			$("#popupContact_company").fadeIn("slow");
			popupStatus_company = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_company(){
		//disables popup only if it is enabled
		if(popupStatus_company==1){
			$("#backgroundPopup_company").fadeOut("slow");
			$("#popupContact_company").fadeOut("slow");
			popupStatus_company = 0;
		}
	}

	//centering popup
	function centerPopup_company(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_company").height();
		var popupWidth = $("#popupContact_company").width();
		//centering
		$("#popupContact_company").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_company").css({
			"height": windowHeight
		});
		
	}

	function findAll_company(p){
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
	            { "sTitle": "Address" },
	            { "sTitle": "City" },
	            { "sTitle": "Country" },
	            { "sTitle": "Phone" },
	            { "sTitle": "Email" },
	            { "sTitle": "Currency" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			
			oTable.fnAddData( [i+1,p[i].name, p[i].address, p[i].city,
			                   p[i].country,p[i].phone,p[i].email,p[i].currency,
			                   '<img class="clickimg" title="Edit"  alt="Edit" src=../images/edit.png onclick=edit_company('+p[i].id+')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Remove"  alt="Remove" src=../images/deny.png onclick=remove_company('+p[i].id+')>' ] );
		}
		
		
	
	}

	var viewed_company = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_company").click(function(){
			viewed_company = -1;
			//centering with css
			centerPopup_company();
			//load popup
			loadPopup_company();
		});
	
		$("#popupbutton_companylist").click(function(){
			
				dwr.engine.beginBatch();
				CompanyService.findAll(findAll_company);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_company").click(function(){
			viewed_company = -1;
			disablePopup_company();
		});
		//Click out event!
		$("#backgroundPopup_company").click(function(){
			viewed_company = -1;
			disablePopup_company();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_company==1){
				disablePopup_company();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_companylist").click();
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_company(this.form);
					 return false;
				 }
				});
			
			
			$(document).ready(function() {
				CommonService.getCurrentSession(function(p){
					if(p.role != 'ROLE_SUPERADMIN'){
						dwr.util.setValue('only4superadmin', '');	
					}
					
				});
				});
			
		});
		
function submitForm_company(f){
	
	var company = {  id:viewed_company,name:null,address:null,city:null,currency:null, country:null, phone:null, email:null};
	  dwr.util.getValues(company);
	  
	  if(viewed_company == -1){
		  company.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  CompanyService.saveOrUpdate(company,afterSave_company);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_company();
	  viewed_company=-1;
}
function cancelForm_company(f){

	var company = {  id:null,name:null };
	  dwr.util.setValues(company);
	  viewed_company = -1;
	  disablePopup_company();
}

function afterEdit_company(p){
	var company = eval(p);
	viewed_company=p.id;
	centerPopup_company();
	loadPopup_company();
	dwr.util.setValues(company);
}

function edit_company(id){
	CompanyService.findById(id,afterEdit_company);
}

function remove_company(id){
	if(!disp_confirm('Account')){
		return false;
	}
	dwr.engine.beginBatch();
	CompanyService.remove(id,afterRemove_company);
	dwr.engine.endBatch();
}
function afterRemove_company(p){
	viewed_company = -1;
	$("#popupbutton_companylist").click();
	$.sticky(p);
	}
function afterSave_company(){
	viewed_company = -1;
	$("#popupbutton_companylist").click();
	}

</script>
<p class="dataTableHeader">Account Setup</p>
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
										<span id="only4superadmin">	<div class="demo" id="popupbutton_company"><button>New Account</button></div></span>
									</td>
									<td width="10%">
										<div class="demo" id="popupbutton_companylist"><button>List Accounts</button></div>
									</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_company" >
		<div id="popupContact_company" class="popupContact" >
							<a  onclick="cancelForm_company();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Account</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
							
								<p id="contactArea_company" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="name" id="name" size="30" class="required"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Address : </td>
								    <td style="width: 50%;"><input type="text" name="address" id="address" size="30"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">City : </td>
								    <td style="width: 50%;"><input type="text" name="city" id="city" size="30"></td>
								  </tr>
								  
								 
								   <tr>
								    <td style="width: 50%;">Country : </td>
								    <td style="width: 50%;"><input type="text" name="country" id="country" size="30"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Phone : </td>
								    <td style="width: 50%;"><input type="text" name="phone" id="phone" size="30"  class="number"></td>
								  </tr>
								   <tr>
								    <td style="width: 50%;">Email : </td>
								    <td style="width: 50%;"><input type="text" name="email" id="email" size="30"  class="email"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Currency Code : </td>
								    <td style="width: 50%;"><input type="text"  name="currency" id="currency" size="30" maxlength="3" minlength="3" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_company_create">
										<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											
											<button onclick="cancelForm_company(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							
							</form>
						</div>
		<div id="backgroundPopup_company" class="backgroundPopup" ></div>
	</div>				