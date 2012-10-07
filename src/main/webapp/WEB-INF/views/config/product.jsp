<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type='text/javascript' src='/dwr/interface/ProductService.js'></script>
<script type='text/javascript' src='/dwr/interface/MeterMetricService.js'></script>
<script type='text/javascript' src='/dwr/interface/InfraService.js'></script>
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
	var popupStatus_product = 0;

	//loading popup with jQuery magic!
	function loadPopup_product(){
		//loads popup only if it is disabled
		if(popupStatus_product==0){
			$("#backgroundPopup_product").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_product").fadeIn("slow");
			$("#popupContact_product").fadeIn("slow");
			popupStatus_product = 1;
		}
	}

	//disabling popup with jQuery magic!
	function disablePopup_product(){
		//disables popup only if it is enabled
		if(popupStatus_product==1){
			$("#backgroundPopup_product").fadeOut("slow");
			$("#popupContact_product").fadeOut("slow");
			popupStatus_product = 0;
		}
	}

	//centering popup
	function centerPopup_product(){
		//request data for centering
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_product").height();
		var popupWidth = $("#popupContact_product").width();
		//centering
		$("#popupContact_product").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		//only need force for IE6
		
		$("#backgroundPopup_product").css({
			"height": windowHeight
		});
		
	}

	var isMycpAdmin = false;
	function findAll_product(p){
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
	            { "sTitle": "Price" },
	            { "sTitle": "Type" },
	            //{ "sTitle": "Meter Metric" },
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
			var options = '<img class="clickimg" title="Edit" alt="Edit" src=../images/edit.png onclick=edit_product('+p[i].id+')>&nbsp; &nbsp; &nbsp;';
			if(isMycpAdmin){
				options = options+
                '<img class="clickimg" title="Remove" alt="Remove" src=../images/deny.png onclick=remove_product('+p[i].id+')>';
			}
			oTable.fnAddData( [i+1,p[i].name, p[i].details, p[i].price+' '+p[i].currency,p[i].productType,
			                   //meterMetric,
			                   options ] );
		}
		
		
	
	}

	var viewed_product = -1;	
$(function(){
	
	
	//LOADING POPUP
		//Click the button event!
		$("#popupbutton_product").click(function(){
			viewed_product = -1;
			//centering with css
			centerPopup_product();
			//load popup
			loadPopup_product();
		});
	
		$("#popupbutton_productlist").click(function(){
			
				dwr.engine.beginBatch();
				ProductService.findAll(findAll_product);
			  dwr.engine.endBatch();
			  
		
		} );
		
		});
		
		
		
					
		//CLOSING POPUP
		//Click the x event!
		$("#popupContactClose_product").click(function(){
			viewed_product = -1;
			disablePopup_product();
		});
		//Click out event!
		$("#backgroundPopup_product").click(function(){
			viewed_product = -1;
			disablePopup_product();
		});
		//Press Escape event!
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_product==1){
				disablePopup_product();
			}
		});
		
		
		
		$(document).ready(function() {
			$("#popupbutton_productlist").click();

			$("#thisform").validate({
   				 submitHandler: function(form) {
   					submitForm_product(form);
   					 return false;
   				 }
   				});
              
			
			ProductService.findAllProductTypesAsString(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('productType');
  				dwr.util.addOptions('productType', p);
  				//dwr.util.setValue(id, sel);
  			});
			
			CompanyService.findAllDistinctCurrency(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('currency');
  				dwr.util.addOptions('currency', p);
  				//dwr.util.setValue(id, sel);
  			});
			
			InfraService.findAll(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('infra');
  				dwr.util.addOptions('infra', p, 'id', 'name');
  				//dwr.util.setValue(id, sel);
  			});
			
			$(document).ready(function() {
				CommonService.getCurrentSession(function(p){
					if(p.role != 'ROLE_SUPERADMIN'){
						//dwr.util.setValue('only4mycpadmin', '');	
					}else if(p.role == 'ROLE_SUPERADMIN'){
						isMycpAdmin = true; 
					}
					
				});
				});
			
             /*  MeterMetricService.findAll(function(p){
  				dwr.util.removeAllOptions('meterMetric');
  				dwr.util.addOptions('meterMetric', p, 'id', 'name');
  				//dwr.util.setValue(id, sel);
  			}); */
  			
  			
		});
		
function submitForm_product(f){
	var product = {  id:viewed_product,name:null, details:null, price:0, currency:null, productType:null,infra:{} };
	  dwr.util.getValues(product);
	  
	 // product.meterMetric.id=dwr.util.getValue("meterMetric");
	 product.infra.id=dwr.util.getValue("infra");
	  
	  if(viewed_product == -1){
		  product.id  = null; 
	  }
	  dwr.engine.beginBatch();
	  ProductService.saveOrUpdate(product,afterSave_product);
	 	//Permission.findById(3);
	 // Permission.findAll(); 
	  dwr.engine.endBatch();
	  disablePopup_product();
	  viewed_product=-1;
}
function cancelForm_product(f){

	var product = {  id:null,name:null, details:null, price:0, currency:null, productType:null,infra:{} };
	  dwr.util.setValues(product);
	  viewed_product = -1;
	  disablePopup_product();
}

function afterEdit_product(p){
	var product = eval(p);
	viewed_product=p.id;
	centerPopup_product();
	loadPopup_product();
	dwr.util.setValues(product);
	//dwr.util.setValue('meterMetric',p.meterMetric.id);
	dwr.util.setValue('infra',p.infra.id);
	dwr.util.setValue('productType',p.productType);
	dwr.util.setValue('currency',p.currency);
}

function edit_product(id){
	ProductService.findById(id,afterEdit_product);
}

function remove_product(id){
	if(!disp_confirm('Product')){
		return false;
	}
	dwr.engine.beginBatch();
	ProductService.remove(id,afterRemove_product);
	dwr.engine.endBatch();
}

function afterRemove_product(p){
	viewed_product = -1;
	$("#popupbutton_productlist").click();
	$.sticky(p);
	}
	
function afterSave_product(){
	viewed_product = -1;
	$("#popupbutton_productlist").click();}

</script>
<p class="dataTableHeader">Product Configuration</p>
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
									<span id="only4mycpadmin"><div class="demo" id="popupbutton_product"><button>New Product</button></div></span>
								</td>
								<td width="10%">
									<div class="demo" id="popupbutton_productlist"><button>List Products</button></div>
								</td>
							</tr>
						</table>
						
				</div>
				</div>
				
	<div id="popupContactParent_product" >
		<div id="popupContact_product" class="popupContact" >
							<a  onclick="cancelForm_product();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Product</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_product" class="contactArea" >
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
								    <td style="width: 50%;">Price (Rate/Hr) : </td>
								    <td style="width: 50%;"><input type="text" name="price" id="price" size="30" class="number"></td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Currency : </td>
								    <td style="width: 50%;"><select id="currency" name="currency" style="width: 205px;">
							    	</select></td>
								  </tr>
								   <!-- <tr>
								    <td style="width: 50%;">Meter Metric : </td>
								    <td style="width: 50%;">
								    <select id="meterMetric" name="meterMetric" style="width: 205px;">
							    	</select>
								    </td>
								  </tr> -->
								  <tr>
								    <td style="width: 50%;">Type : </td>
								    <td style="width: 50%;">
								    <select id="productType" name="productType" style="width: 205px;">
							    	</select>
								    </td>
								  </tr>
								  
								  <tr>
								    <td style="width: 50%;">Infra : </td>
								    <td style="width: 50%;">
								    <select id="infra" name="infra" style="width: 205px;" class="required">
							    	</select>
								    </td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_product_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_product(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_product" class="backgroundPopup" ></div>
	</div>				