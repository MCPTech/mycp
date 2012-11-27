<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
  <script type='text/javascript' src='/dwr/interface/KeyPairInfoP.js'></script>
  <script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
  
<script type="text/javascript">
/***************************/
//@Author: Adrian "yEnS" Mato Gondelle
//@website: www.yensdesign.com
//@email: yensamg@gmail.com
//@license: Feel free to use it, but keep this credits please!					
/***************************/
	var popupStatus_keys = 0;

	function loadPopup_keys(){
		if(popupStatus_keys==0){
			$("#backgroundPopup_keys").css({
				"opacity": "0.7"
			});
			$("#backgroundPopup_keys").fadeIn("slow");
			$("#popupContact_keys").fadeIn("slow");
			popupStatus_keys = 1;
		}
	}

	function disablePopup_keys(){
		if(popupStatus_keys==1){
			$("#backgroundPopup_keys").fadeOut("slow");
			$("#popupContact_keys").fadeOut("slow");
			popupStatus_keys = 0;
		}
	}

	function centerPopup_keys(){
		var windowWidth = document.documentElement.clientWidth;
		var windowHeight = document.documentElement.clientHeight;
		var popupHeight = $("#popupContact_keys").height();
		var popupWidth = $("#popupContact_keys").width();
		$("#popupContact_keys").css({
			"position": "absolute",
			"top": windowHeight/2-popupHeight/2,
			"left": windowWidth/2-popupWidth/2
		});
		
		$("#backgroundPopup_keys").css({
			"height": windowHeight
		});
		
	}
	
	var start = 0;
	var max = 17;
	
	function findAll_keys(p){
		
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
	            { "sTitle": "Name" },
	            { "sTitle": "Project" },
	            { "sTitle": "Fingerprint" },
	            { "sTitle": "Status" },
	            { "sTitle": "Key" },
	            { "sTitle": "Cloud" },
	            { "sTitle": "Actions" }
	           
	        ]
	    } );
		
		var i=0;
		for (i=0;i<p.length;i++)
		{
			var d = '<div style=\"display:none;\" id= keyMat'+p[i].id+'>'+p[i].keyMaterial+'</div>';
			var actions='<img class="clickimg" title="Remove" alt="Remove" src=../images/remove.png onclick=delete_keys('+p[i].id+')>&nbsp; &nbsp; '+
            '<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
            
			if('PENDING_APPROVAL' == p[i].status ){
            	p[i].status='<img title="pending approval" alt="pending approval" src=/images/pending.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
            }else if('starting' == p[i].status ){
            	p[i].status='<img title="starting" alt="starting" src=/images/preloader.gif>&nbsp;';
            	actions='';
            }else if('active' == p[i].status ){
            	p[i].status='<img title="active" alt="active" src=/images/running.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
            }else if('inactive' == p[i].status ){
            	p[i].status='<img title="inactive" alt="inactive" src=/images/waiting.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
            }else if('failed' == p[i].status ){
            	p[i].status='<img title="failed" alt="failed" src=/images/warning.png>&nbsp;';
            	actions='<img class="clickimg" title="Delete" alt="Delete" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
            }else if('APPROVAL_REJECTED' == p[i].status){
            	p[i].status='<img title="Approval Rejected" alt="Approval Rejected" src=/images/rejected.png>&nbsp;';
        		actions=
                	'<img class="clickimg" title="Delete" alt="Remove" src=../images/deny.png onclick=remove_keys('+p[i].id+')>';
        	}
			
			oTable.fnAddData( [start+i+1,p[i].keyName, p[i].asset.project.name, p[i].keyFingerprint,p[i].status,'<a href=\"#\" onClick=\"+showKeyMaterial('+p[i].id+')\">Download</a>'+d,
			                   p[i].asset.productCatalog.infra.name,
			                   actions ] );
		}
	}

	function showKeyMaterial(id){
		KeyPairInfoP.writeFileContentInResponse(id, function(data) {
		    dwr.engine.openInDownload(data);
		  });
		
		/* if (document.getElementById('keyMat'+id).style.display == 'none') {
			document.getElementById('keyMat'+id).style.display = 'block';
		}else{
			document.getElementById('keyMat'+id).style.display = 'none';
		} */
		//KeyPairInfoP.findById(id,showKey);
	}
	
	
	
	var viewed_keys = -1;	
	$(function(){
			$("#popupbutton_keys").click(function(){
				viewed_keys = -1;
				centerPopup_keys();
				loadPopup_keys();
			});
		
			
				
				
				$("#popupbutton_keyslist").click(function(){
					dwr.engine.beginBatch();
					start =0;
					$('#SearchField').val('');
					KeyPairInfoP.findAll(start,max,'',findAll_keys);
				  dwr.engine.endBatch();
			} );
			
			$("#popupbutton_previous").click(function(){
				if(start>16){
					start=start -17;
				}
				var text2Search = dwr.util.getValue("SearchField");
				KeyPairInfoP.findAll(start,max,text2Search,findAll_keys);
			} );
			
			$("#popupbutton_next").click(function(){
				start = start +17;
				var text2Search = dwr.util.getValue("SearchField");
				KeyPairInfoP.findAll(start,max,text2Search,findAll_keys);
			} );
			
			$("#popupbutton_search").click(function(){
				
				var text2Search = dwr.util.getValue("SearchField");
				start = 0;
				KeyPairInfoP.findAll(start,max,text2Search,findAll_keys);
				
			} );
				
			ProjectService.findAll(function(p){
				dwr.util.removeAllOptions('projectId');
				//dwr.util.addOptions('project', p, 'id', 'name');
				dwr.util.addOptions('projectId', p, 'id', function(p) {
					return p.name+' @ '+p.department.name;
				});
				//dwr.util.setValue(id, sel);
				
			});
				
			});
		
		$("#popupContactClose_keys").click(function(){
			viewed_keys = -1;
			disablePopup_keys();
		});
		$("#backgroundPopup_keys").click(function(){
			viewed_keys = -1;
			disablePopup_keys();
		});
		$(document).keypress(function(e){
			if(e.keyCode==27 && popupStatus_keys==1){
				disablePopup_keys();
			}
		});
		
		$(document).ready(function() {
			$("#popupbutton_keyslist").click();

			KeyPairInfoP.findProductType(function(p){
				//alert(dwr.util.toDescriptiveString(p,3));
  				dwr.util.removeAllOptions('product');
  				dwr.util.addOptions('product', p,'id','name');
  				//dwr.util.setValue(id, sel);
  			});
			
			$("#thisform").validate({
				 submitHandler: function(form) {
					 submitForm_keys(form);
					 return false;
				 }
				});

			
		});
		
	function submitForm_keys(f){
		var keyPairInfop = {  id:viewed_keys,keyName:null,product:null, projectId:null };
		  dwr.util.getValues(keyPairInfop);
		  if(viewed_keys == -1){
			  keyPairInfop.id  = null; 
		  }
		  dwr.engine.beginBatch();
		  KeyPairInfoP.saveOrUpdate(keyPairInfop,afterSave_keys);
		  dwr.engine.endBatch();
		  disablePopup_keys();
		  viewed_keys=-1;
	}
	function cancelForm_keys(f){
	
		var keyPairInfop = {  id:null,keyName:null ,product:null, projectId:null};
		  dwr.util.setValues(keyPairInfop);
		  viewed_keys = -1;
		  disablePopup_keys();
	}
	
	function afterEdit_keys(p){
		var keyPairInfop = eval(p);
		viewed_keys=p.id;
		centerPopup_keys();
		loadPopup_keys();
		dwr.util.setValues(keyPairInfop);
		dwr.util.setValue('product',p.product.id);
	}
	
	function edit_keys(id){
		KeyPairInfoP.findById(id,afterEdit_keys);
	}
	
	function remove_keys(id){
		if(!disp_confirm('Key')){
			return false;
		}
		dwr.engine.beginBatch();
		KeyPairInfoP.remove(id,afterSave_keys);
		dwr.engine.endBatch();
	}
	function afterSave_keys(p){
		if(p == null){
			CommonService.getSessionMsg(function(p){
				$.sticky(p);
			});
			return;
		}
		viewed_keys = -1;
		$("#popupbutton_keyslist").click();}
	
	function delete_keys(id){
		dwr.engine.beginBatch();
		KeyPairInfoP.deleteKeyPair(id,afterSave_keys);
		dwr.engine.endBatch();
		}
	
	</script>
<p class="dataTableHeader">Key Resource</p>

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
						<div style="height: 50px; padding-top: 20px;">
							<div class="demo" id="popupbutton_keys" style="float: left; padding-left: 10px;"><button>Request New Key</button></div>
							<div class="demo" id="popupbutton_keyslist" style="float: left; padding-left: 10px;"><button>List All Keys</button></div>
							
							<div style="width: 200px;float: right;"> 
								<div class="demo" id="popupbutton_previous" style="float: left;  width: 90px;"><button>Previous</button></div>
								<div class="demo" id="popupbutton_next" style="float: left; "><button>Next</button></div>
							</div>
						</div>
						
						<table align="right" border="0" width="100%">
							<tr>
								<td width="80%">
								
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_keys"><button>Request Key</button></div> -->
								</td>
								<td width="10%">
									<!-- <div class="demo" id="popupbutton_keyslist"><button>List Keys</button></div> -->
								</td>
							</tr>
						</table>
						
				</div>
				</div>
	<div id="popupContactParent_keys" >
		<div id="popupContact_keys" class="popupContact" >
							<a  onclick="cancelForm_keys();return false;" class="popupContactClose" style="cursor: pointer; text-decoration:none;">X</a>
							<h1>Keys</h1>
							<form class="cmxform" id="thisform" method="post" name="thisform">
								<p id="contactArea_keys" class="contactArea" >
								<input type="hidden" id="id" name="id">
								<table style="width: 100%;">
								  <tr>
								    <td style="width: 50%;">Name : </td>
								    <td style="width: 50%;"><input type="text" name="keyName" id="keyName" size="30" class="required"></td>
								  </tr>
								  <tr>
								    <td style="width: 50%;">Product : </td>
								    <td style="width: 50%;">
								    <select id="product" name="product" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 20%;">project : </td>
								    <td style="width: 80%;">
								    <select id="projectId" name="projectId" style="width: 205px;" class="required">
							    	</select>
							    	</td>
								  </tr>
								  <tr>
								    <td style="width: 50%;"></td>
								    <td style="width: 50%;">
								    <br><br>
										<div class="demo" id="popupbutton_keys_create">
											<input class="submit" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
											<button onclick="cancelForm_keys(this.form);return false;">Cancel</button>
										</div>
									</td>
								  </tr>
								</table>
								</p>
							</form>
						</div>
		<div id="backgroundPopup_keys" class="backgroundPopup" ></div>
	</div>				