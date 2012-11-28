<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
  <script type='text/javascript' src='/dwr/interface/ImageDescriptionP.js'></script>
<script type='text/javascript' src='/dwr/interface/InstanceP.js'></script>

<script type='text/javascript' src='/dwr/interface/KeyPairInfoP.js'></script>
<script type='text/javascript' src='/dwr/interface/GroupDescriptionP.js'></script>
<script type='text/javascript' src='/dwr/interface/ProductService.js'></script>
 
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
	var popupStatus_compute = 0;

function loadPopup_compute(){
		if(popupStatus_compute==0){
			$("#backgroundPopup_compute").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_compute").fadeIn("slow");
			$("#popupContact_compute").fadeIn("slow");
			popupStatus_compute = 1;
		}
	}

	function disablePopup_compute(){
		if(popupStatus_compute==1){
			$("#backgroundPopup_compute").fadeOut("slow");
			$("#popupContact_compute").fadeOut("slow");
			popupStatus_compute = 0;
		}
	}

	function centerPopup_compute(){
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_compute").height();
		var popupWidth = $("#popupContact_compute").width();
		$("#popupContact_compute").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		
		$("#backgroundPopup_compute").css({
			"height": windowHeight
		});
		
	}
	function create_server(imageId,infraId){
		
		viewed_compute = -1;
		centerPopup_compute();
		loadPopup_compute();
		dwr.util.setValue('imageId',imageId);
		
		InstanceP.findProductType(function(p){
				dwr.util.removeAllOptions('product');
				for (var key in p) {
					   var obj = p[key];
					   var infra = obj['infra'];
					   
					   if(infra['id'] == infraId){
						   var a = {};
						   a[obj['id']] = obj['name'];
							dwr.util.addOptions('product',  a);	
						}
				}
			});
		
		KeyPairInfoP.findAll4List(function(p){
			dwr.util.removeAllOptions('keyName');
			//dwr.util.addOptions('keyName', p, 'id', 'keyName');
			for (var key in p) {
				   var keypair = p[key];
				   var asset = keypair['asset'];
				   var productCatalog = asset['productCatalog'];
				   var infra = productCatalog['infra'];
				   if(infra['id'] == infraId){
					   var a = {};
					   a[keypair['id']] = keypair['keyName'];
						dwr.util.addOptions('keyName',  a);	
					}
			}
			
		});
		
		GroupDescriptionP.findAll4List(function(p){
			dwr.util.removeAllOptions('groupName');
			//dwr.util.addOptions('groupName', p, 'name', 'name');
			for (var key in p) {
				   var secGroup = p[key];
				   //alert(dwr.util.toDescriptiveString(secGroup,2));
				   var asset = secGroup['asset'];
				   var productCatalog = asset['productCatalog'];
				   var infra = productCatalog['infra'];
				   
				   if(infra['id'] == infraId){
					   
					   var a = {};
					   a[secGroup['name']] = secGroup['name'];
						dwr.util.addOptions('groupName',  a);	
					}
			}
			
		});
		
	}
	

	var viewed_image = -1;	
	var start = 0;
	var max = 17;
		
	function findAll_image(p){
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
	        "fnDrawCallback": function() {
                $('.dataTables_paginate').css("display", "none");
                $('.dataTables_length').css("display", "none");
                $('.dataTables_filter').css("display", "none");
                $('.dataTables_info').css("display", "none");
    		},
	        "aoColumns": [
	            { "sTitle": "#" },
	            { "sTitle": "Image Name" },
	            { "sTitle": "Image Id" },
	            { "sTitle": "Owner" },
	            { "sTitle": "State" },
	            { "sTitle": "Public" },
	            { "sTitle": "Architecture" },
	            { "sTitle": "Platform" },
	            { "sTitle": "Root Device" },
	            { "sTitle": "Image Type" },
	            { "sTitle": "Location" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		//oTable.fnClearTable();
		
		var i=0;
		
		for (i=0;i<p.length;i++)
		{
			//TODO - if project name is added into the list , then we need use the below code. 
			var projName = '';
				try{
					projName = p[i].asset.project.name;
				}catch(e){}
				
			oTable.fnAddData( [start+i+1,p[i].name, p[i].imageId, 
			                   p[i].imageOwnerId, p[i].imageState, p[i].isPublic,
			                   p[i].architecture,p[i].platform,p[i].rootDeviceName,p[i].imageType,p[i].imageLocation,
			                   p[i].asset.productCatalog.infra.name,
			                   '<img class="clickimg" title="create server"  alt="create server" src="/images/createServer.png" onclick=create_server("'+p[i].imageId+'","'+p[i].asset.productCatalog.infra.id+'")>&nbsp; &nbsp; &nbsp; '
			                   //'<img class="clickimg" title="Remove"  alt="Remove" src=../images/deny.png onclick=remove_image('+p[i].id+')>' 
			                   ] );
		}
	}
	
$(function(){
		$("#popupbutton_image").click(function(p){
			viewed_image = -1;
			centerPopup_image();
			loadPopup_image();
			
		});
	
			$("#popupbutton_imagelist").click(function(){
					dwr.engine.beginBatch();
					start =0;
					$('#SearchField').val('');
					ImageDescriptionP.findAll(start,max,'',findAll_image);
				  dwr.engine.endBatch();
			} );
			
			$("#popupbutton_previous").click(function(){
				if(start>16){
					start=start -17;
				}
				var text2Search = dwr.util.getValue("SearchField");
				ImageDescriptionP.findAll(start,max,text2Search,findAll_image);
			} );
			
			$("#popupbutton_next").click(function(){
				start = start +17;
				var text2Search = dwr.util.getValue("SearchField");
				ImageDescriptionP.findAll(start,max,text2Search,findAll_image);
			} );
			
			$("#popupbutton_search").click(function(){
				
				var text2Search = dwr.util.getValue("SearchField");
				start = 0;
				ImageDescriptionP.findAll(start,max,text2Search,findAll_image);
				
			} );
			
			
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_imagelist").click();
			
			
			
			
			InstanceP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			KeyPairInfoP.findAll4List(function(p){
				dwr.util.removeAllOptions('keyName');
				dwr.util.addOptions('keyName', p, 'id', 'keyName');
				//dwr.util.setValue(id, sel);
			});
			
			GroupDescriptionP.findAll4List(function(p){
				dwr.util.removeAllOptions('groupName');
				dwr.util.addOptions('groupName', p, 'name', 'name');
				//dwr.util.setValue(id, sel);
			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_compute(form);
					 return false;
				 }
				});
			
			
			jQuery('#imageId').autocomplete({
			    source : function(request, response) {
			    	var text2Search = $('#imageId').val() ;
			    	ImageDescriptionP.findAll(0,100,text2Search,  function(data) {
			                var arrayOfData = [];
			                for(i = 0;i < data.length;i++){
			                	 arrayOfData.push(data[i].imageId+','+data[i].name+','+data[i].imageLocation);
			                }
			                response(arrayOfData);
			            
			        });
			    }
			});
			
		});
		
		function submitForm_compute(f){
			
			var instancep = {  id:viewed_compute,name:null, reason:null, imageId:null, instanceType:null, keyName:null,groupName:null,product:null };
			  dwr.util.getValues(instancep);
			  var imageStr = dwr.util.getValue("imageId");
			  if(imageStr.indexOf(',')>0){
				  instancep.imageId=imageStr.substring(0,imageStr.indexOf(','));  
			  }
			  
			  
			  if(viewed_compute == -1){
				  instancep.id  = null; 
			  }
			  instancep.keyName=dwr.util.getText("keyName");
			  if(viewed_compute >0){
				  InstanceP.updateCompute(instancep,afterSave_compute);
			  }else{
				  InstanceP.requestCompute(instancep,afterSave_compute);  
			  }
			 
			  disablePopup_compute();
			  viewed_compute=-1;
		}
		
		function afterSave_compute(){
			$.sticky('Compute creation scheduled.Check Approvals.');
			}
		
		function cancelForm_compute(f){
			var instancep = {  id:null,name:null, reason:null, imageId:null, instanceType:null, keyName:null,groupName:null,product:null};
			  dwr.util.setValues(instancep);
			  viewed_compute = -1;
			  disablePopup_compute();
		}
		
function submitForm_image(f){
	var imageDescriptionp = {  id:viewed_image,name:null, description:null };
	  dwr.util.getValues(imageDescriptionp);
	  imageDescriptionp.instanceIdForImgCreation=dwr.util.getValue("instance");
	  //alert(imageDescriptionp.instanceIdForImgCreation);
	  if(viewed_image == -1){
		  imageDescriptionp.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  ImageDescriptionP.saveOrUpdate(imageDescriptionp,afterSave_image);
	  dwr.engine.endBatch();
	  disablePopup_image();
	  viewed_image=-1;
}
function cancelForm_image(f){

	var imageDescriptionp = {  id:null,name:null, description:null };
	  dwr.util.setValues(imageDescriptionp);
	  viewed_image = -1;
	  disablePopup_image();
}

function afterEdit_image(p){
	var imageDescriptionp = eval(p);
	viewed_image=p.id;
	centerPopup_image();
	loadPopup_image();
	dwr.util.setValues(imageDescriptionp);
}

function edit_image(id){
	ImageDescriptionP.findById(id,afterEdit_image);
}

	function remove_image(id){
		if(!disp_confirm('Image')){
			return false;
		}
		dwr.engine.beginBatch();
		ImageDescriptionP.remove(id,afterSave_image);
		dwr.engine.endBatch();
	}
	
	function afterSave_image(){
		viewed_image = -1;
		$("#popupbutton_imagelist").click();}

</script>
<div class="dataTableHeader">Image Resource</div>
					<div style="width: 300px;float: right;"> 
						<div style="float: left; padding-top: 5px; width: 170px;"> <input type="text" name="SearchField" id="SearchField"  ></div>
						 
						<div class="demo" id="popupbutton_search" style="float: left; padding-bottom: 10px;"><button>Search</button></div>
					
					</div>
		<div id="datatable-iaas-parent" class="infragrid2">
					
					<div id="datatable-iaas" >
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="compute-table">
							<thead><tr></tr></thead>
							<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
							</tfoot><tbody></tbody>
						</table>
						<div style="height: 50px; padding-top: 10px;">
							<div class="demo" id="popupbutton_imagelist" style="float: left; padding-left: 10px;"><button>List All Images</button></div>
							<div style="width: 200px;float: right;"> 
								<div class="demo" id="popupbutton_previous" style="float: left;  width: 90px;"><button>Previous</button></div>
								<div class="demo" id="popupbutton_next" style="float: left; "><button>Next</button></div>
							</div>
						
						<table align="right" border="0" width="100%">
							<tr>
								<td width="80%">
								<font color="green"></font>
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_image"><button>Request Image</button></div> -->
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_imagelist"><button>List Images</button></div> -->
								</td>
							</tr>
						</table>
						
						
						
				</div>
				</div>
				
	<div id="popupContactParent_compute" >
		<div id="popupContact_compute" class="popupContact" >
							<a  onclick="cancelForm_compute();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Request Compute</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_compute" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								
								<tr>
								    <td style="width: 20%;">Product : </td>
								    <td style="width: 80%;">
								    <select id="product" name="product" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  
								  <tr>
								    <td style="width: 20%;">Name : </td>
								    <td style="width: 80%;"><input type="text" name="name" id="name" size="58" maxlength="90" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Reason : </td>
								    <td style="width: 80%;"><input type="text" name="reason" id="reason" maxlength="90" size="58"></td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Image : </td>
								    <td style="width: 80%;">
								    <input type="text" id="imageId" size="58" class="required">
								    
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">Type : </td>
								    <td style="width: 80%;">
								    <select id="instanceType" name="instanceType" style="width: 385px;" class="required">
								    	<option value="m1.small">m1.small</option>
								    	<option value="m1.large">m1.large</option>
								    	<option value="m1.xlarge">m1.xlarge</option>
								    	<option value="c1.medium">c1.medium</option>
								    	<option value="c1.xlarge">c1.xlarge</option>
							    	</select>
							    	</td>
								  </tr>
								  
								  <tr>
								    <td style="width: 20%;">Key : </td>
								    <td style="width: 80%;">
								    <select id="keyName" name="keyName" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								   <!-- <tr>
								    <td style="width: 50%;">Base Infra : </td>
								    <td style="width: 50%;"><input type="text" name="hypervisor" id="hypervisor" size="60"></td>
								  </tr> -->
								  <tr>
								    <td style="width: 20%;">Security Group : </td>
								    <td style="width: 80%;">
								    <select id="groupName" name="groupName" style="width: 385px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  
								  
								  
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_compute_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_compute(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_compute" class="backgroundPopup" ></div>
	</div>					