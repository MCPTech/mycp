<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript" src="/js/jqueryplugins/jquery.jcarousel.min.js"></script>
<script type='text/javascript' src='/dwr/interface/WorkflowService.js'></script>
<script type='text/javascript' src='/dwr/interface/DashboardService.js'></script>
<script type='text/javascript' src='/dwr/interface/ReportService.js'></script>
<script type='text/javascript' src='/dwr/interface/InfraService.js'></script>
<script type='text/javascript' src='/dwr/interface/ProductService.js'></script>
<script type='text/javascript' src='/dwr/interface/eucalyptusService.js'></script>


<script type="text/javascript">

	function findAll_workflow(p){
		
		 oTable = $('#workflow-table').dataTable( {
	    	"sPaginationType": "full_numbers",
	    	"bDestroy": true,
	    	"bFilter": false,
	    	"bAutoWidth": false,
	    	"bDeferRender": true,
	    	"bJQueryUI": false,
	    	"bLengthChange": false,
	    	"iDisplayLength": 7,
	        "aaData": [
	        ],
	        "aoColumns": [
	            { "sTitle": "#" },
	            { "sTitle": "Workflow" },
	            { "sTitle": "User" },
	            { "sTitle": "Status" },
	            { "sTitle": "Start" },
	            { "sTitle": "Asset" },
	            { "sTitle": "Actions" }
	        ]
	    } );
		var i=0;
		for (i=0;i<p.length;i++)
		{
			oTable.fnAddData( [i+1,p[i].processName,p[i].user.email, p[i].processStatus, 
			                   dateFormat(p[i].startTime,"mmm dd yyyy HH:MM:ss")
			                   , p[i].assetDetails,
			                   '<img class="clickimg" title="Approve" alt="Approve" src=../images/approve.png onclick=approve_workflow(\''+p[i].processId+'\')>&nbsp; &nbsp; &nbsp; '+
			                   '<img class="clickimg" title="Reject" alt="Reject" src=../images/reject.png onclick=reject_workflow(\''+p[i].processId+'\')>' ] );
		}
	}//end findAll_workflow
			
			function findAll_Asset(p){
								
				dwr.util.setValue('cloud', p.cloudName);
				dwr.util.setValue('compute', p.instanceCount);
				dwr.util.setValue('volume', p.volCount);
				dwr.util.setValue('ip', p.ipCount);
				dwr.util.setValue('secgroup', p.secGroupCount);
				dwr.util.setValue('Snapshot', p.snapshotCount);
				dwr.util.setValue('Image', p.imageCount);
				dwr.util.setValue('Key', p.keyPairCount);
				
				dwr.util.setValue('Accounts', p.accounts);
				dwr.util.setValue('Departments', p.departments);
				dwr.util.setValue('Projects', p.projects);
				dwr.util.setValue('Users', p.users);
				dwr.util.setValue('Clouds', p.clouds);
				dwr.util.setValue('Products', p.products);
			}//end findAll_Asset(p)
			
			function findAll_AssetCosts(p){
				
				dwr.util.setValue('computeCost', p.computeCost);
				dwr.util.setValue('volumeCost', p.volumeCost);
				dwr.util.setValue('ipaddressCost', p.ipaddressCost);
				dwr.util.setValue('secgroupCost', p.secgroupCost);
				dwr.util.setValue('snapshotCost', p.snapshotCost);
				dwr.util.setValue('imageCost', p.imageCost);
				dwr.util.setValue('keyCost', p.keyCost);
				dwr.util.setValue('totalCost', p.totalCost);
				dwr.util.setValue('totalCostCurrency', p.currency);
				
			}//end findAll_Asset(p)
			
			function findAll_infra(p){
				var oTable1 = $('#cloudhealth-table').dataTable( {
				   	"sPaginationType": "two_button",
				   	"bDestroy": true,
				   	"bFilter": false,
				   	"bAutoWidth": false,
				   	"bDeferRender": true,
				   	"bJQueryUI": false,
				   	"bLengthChange": false,
				   	"iDisplayLength": 5,
				       "aaData": [
				       ],
				       "aoColumns": [
				           { "sTitle": "Cloud" },
				           { "sTitle": "Server" },
				           { "sTitle": "Status" }
				       ]
				   } );
					var i=0;
					for (i=0;i<p.length;i++)
					{
						 if(p[i].status == 'loading'){
							 p[i].status =  '<div id=cloudStatus'+p[i].server+'><img title="Loading" alt=loading src=../images/preloader.gif></div>'; 
	                	 }
						 
						 p[i].server = '<a href="/config/infra">'+p[i].server+'</a>';
						 
						 oTable1.fnAddData( [	p[i].name,p[i].server, p[i].status
						                  ] );
					}
				//alert('calling updateCloudStatus ');
				updateCloudStatus(p);
			}//end findAll_infra
			
			function updateCloudStatus(p){
				var i=0;
				for (i=0;i<p.length;i++)
				{
					InfraService.getInfraStatusDWR(p[i].id,after_updateCloudStatus);
				}
			}//end updateCloudStatus
			
			function after_updateCloudStatus(s){
				
				var ip = s.substring(0, s.indexOf("="));
				var status = s.substring(s.indexOf("=")+1,s.length);
				//alert('ip = '+ip +'status = '+status);
				if(status == 'running'){
					dwr.util.setValue('cloudStatus'+ip,'<img title="running" alt=running src=../images/running.png>',{ escapeHtml:false });	
				}else if(status == 'unreachable'){
					dwr.util.setValue('cloudStatus'+ip,'<img title="warning" alt=warning src=../images/warning.png>',{ escapeHtml:false });	
				}else if(status == 'unknown'){
					dwr.util.setValue('cloudStatus'+ip,'<img title="unknown" alt=unknown src=../images/unknown.png>',{ escapeHtml:false });
				}
				
				
				
			}//end after_updateCloudStatus
			
			function findAll_product(p){
				//alert(dwr.util.toDescriptiveString(p,3));
				var i=0;
				for (i=0;i<p.length;i++)
				{
					//alert(p[i].productType);
					if(p[i].productType == 'Ip Address'){
						//alert(p[i].name+"<br>"+p[i].price+" "+p[i].currency);
						dwr.util.setValue('productIpAddress', p[i].name+"<br>"+p[i].price+" "+p[i].currency+"/Hr ",{ escapeHtml:false });
						//alert(dwr.util.getValue('productIpAddress'));
					}else if(p[i].productType == 'Security Group'){
						dwr.util.setValue('productSecurityGroup', p[i].name+'<br>'+p[i].price+' '+p[i].currency+"/Hr ",{ escapeHtml:false });
					}else if(p[i].productType == 'Volume'){
						dwr.util.setValue('productVolume', p[i].name+'<br>'+p[i].price+' '+p[i].currency+"/Hr ",{ escapeHtml:false });
					}else if(p[i].productType == 'Snapshot'){
						dwr.util.setValue('productSnapshot', p[i].name+'<br>'+p[i].price+' '+p[i].currency+"/Hr ",{ escapeHtml:false });
					}else if(p[i].productType == 'Key Pair'){
						dwr.util.setValue('productKeyPair', p[i].name+'<br>'+p[i].price+' '+p[i].currency+"/Hr ",{ escapeHtml:false });
					}else if(p[i].productType == 'Instance'){
						dwr.util.setValue('productCompute', p[i].name+'<br>'+p[i].price+' '+p[i].currency+"/Hr ",{ escapeHtml:false });
					}
				}
				
		        
			}//end findAll_product
			
			

			
		$(document).ready(function() {
			WorkflowService.findAll(findAll_workflow);
			DashboardService.getAllAssetCount(findAll_Asset);
			ReportService.getAllAssetCosts(findAll_AssetCosts);
			ProductService.findAll(findAll_product);
			InfraService.findAll4Dashboard(findAll_infra);
			
			jQuery('.jcarousalClass').jcarousel({
				auto: 6,
				scroll:0
		    });
		});
		
		function approve_workflow(processId){
			WorkflowService.moveProcessInstance(processId,'Approve',WorkflowService.findAll(findAll_workflow));
			$.sticky('Process approved');
		}

		function reject_workflow(processId){
			WorkflowService.moveProcessInstance(processId,'Reject',WorkflowService.findAll(findAll_workflow));
			$.sticky('Process rejected');
		}
		
</script>
<div class="grid">
					<div class="grid1">
					<!-- <span id="cloud"></span> -->
						<div class="productsDash">Current Consumption</div>
						<div style="height: 10px;"></div>
						<table width="100%">
						<tbody>
						<tr>
							<td width="50%"><a href="/iaas/compute">Compute</a> </td><td width="50%"><span id="compute"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/volume">Volume</a> </td><td width="50%"><span id="volume"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/ipaddress">Ip Address</a> </td><td width="50%"><span id="ip"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/secgroup">Sec Group</a> </td><td width="50%"><span id="secgroup"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/snapshot">Snapshot</a> </td><td width="50%"><span id="Snapshot"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/image">Image</a> </td><td width="50%"><span id="Image"></span></td></tr>
						<tr>
							<td width="50%"><a href="/iaas/keys">Key</a> </td><td width="50%"><span id="Key"></span></td></tr>
						</tbody>
						</table>
						
						<div style="height: 20px;"></div>
						<div class="productsDash">MyCP Overview</div>
						<div style="height: 10px;"></div>
						<table width="100%">
						<tbody>
						<tr>
							<td width="50%"><a href="/enterprise/company">Accounts</a> </td><td width="50%"><span id="Accounts"></span></td></tr>
						<tr>
							<td width="50%"><a href="/enterprise/department">Departments</a> </td><td width="50%"><span id="Departments"></span></td></tr>
						<tr>
							<td width="50%"><a href="/enterprise/project">Projects</a> </td><td width="50%"><span id="Projects"></span></td></tr>
						<tr>
							<td width="50%"><a href="/realm/user">Users</a> </td><td width="50%"><span id="Users"></span></td></tr>
						<tr>
							<td width="50%"><a href="/config/infra">Clouds</a> </td><td width="50%"><span id="Clouds"></span></td></tr>
						<tr>
							<td width="50%"><a href="/config/product">Products</a> </td><td width="50%"><span id="Products"></span></td></tr>
						</tbody>
						</table>
						
						
					</div>
					<div class="grid2">
						<div class="productsDash">Cloud Health</div>
						<div style="height: 10px;"></div>
						<div id="datatable-cloudhealth"  >
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="cloudhealth-table">
								<thead><tr></tr></thead>
								<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
								</tfoot><tbody></tbody>
							</table>
						</div>
						
						
						
						<div style="height: 20px;"></div>
						<div class="productsDash">Products</div>
						<div style="height: 10px;"></div>
						<div id="jcarousalDiv">
						
							<div class="jcarousalClass" >
							    <ul>
							        <li><a href="/iaas/ipaddress"> <img alt="" src="/images/ipaddress.png"><div id="productIpAddress"></div></a> </li>
							        <li> <a href="/iaas/secgroup"><img alt="" src="/images/firewall.png"><div id="productSecurityGroup"></div></a></li>
							        <li><a href="/iaas/volume"><img alt="" src="/images/volume.png"><div id="productVolume"></div></a></li>
							        <li> <a href="/iaas/snapshot"><img alt="" src="/images/storage.png"><div id="productSnapshot"></div></a></li>
							        <li> <a href="/iaas/compute"><img alt="" src="/images/compute.png"><div id="productCompute"></div></a></li>
							        <li> <a href="/iaas/keys"><img alt="" src="/images/key.png"><div id="productKeyPair"></div></a></li>
						       
							    </ul>
							</div>
						</div>
					</div>
					<div class="grid3">
						<div class="productsDash">Current Costs<span id="cloud_costs"></span></div>
						<div style="height: 10px;"></div>
						<table width="100%" >
						
						<tbody>
							<tr><td>Compute</td><td><span id="computeCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Volume</td><td><span id="volumeCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Ip Address</td><td><span id="ipaddressCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Sec Group</td><td><span id="secgroupCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Snapshot</td><td><span id="snapshotCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Image</td><td><span id="imageCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>Key</td><td><span id="keyCost"></span></td><td></td><td></td><td></td></tr>
							<tr><td>&nbsp;</td><td></td><td></td><td></td><td></td></tr>
							<tr><td>Total</td><td><span id="totalCost"></span></td><td><span id="totalCostCurrency"></span></td><td></td><td></td></tr>
						</tbody>
						</table>
					
					</div>
					<div id="datatable-iaas-parent" class="grid4">
						<div class="productsDash">Requests waiting for your Actions</div>
						<div style="height: 10px;"></div>
						
						<div id="datatable-iaas" >
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="workflow-table">
								<thead><tr></tr></thead>
								<tfoot><tr><th rowspan="1" colspan="5"></th></tr>
								</tfoot><tbody></tbody>
							</table>
						</div>
					</div>
				</div>