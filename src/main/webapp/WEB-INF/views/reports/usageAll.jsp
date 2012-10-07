<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type='text/javascript' src='/dwr/interface/ReportService.js'></script>
<script type="text/javascript">
		function findAll_infra(p){
			var totalCosts=0;
				var cellFuncs = [
				                 function(p) { return p.assetTypeName; },
				                 function(p) { return p.assetDetails; },
				                 function(p) { return p.user.email; },
				                 function(p) { return  dateFormat(p.startTime,"mmm dd yyyy HH:MM"); },
				                 function(p) { return dateFormat(p.endTime,"mmm dd yyyy HH:MM");},
				                 function(p) { return p.duration;},
				                 function(p) { return p.startRate;},
				                 function(p) { 
				                	 totalCosts=totalCosts+p.cost;
				                	 return p.cost;}
				               ];
				dwr.util.addRows( "resourceUsage",p,cellFuncs, { escapeHtml:false });
				dwr.util.setValue('totalCosts',totalCosts);//alert(totalCosts);
			}//end findAll_infra
			

						
	$(document).ready(function() {
			ReportService.findAssets4AllAccounts(findAll_infra);
		});					
						
</script>

<html><center>
						<div class="dataTableHeader">Usage of all Resources.</div>
<div class="infragrid2" >
<table align="center" width="95%" ><!-- just for border -->
<tr><td>
						<div style="width:10%; color: #d45500; float: right; font-weight: bold;">Total Cost: <span id="totalCosts"></span></div>
						<div style="height: 10px;"></div>
						<table align="center" width="100%"  id="resourceUsage" >
						<thead>
							<tr style="background-color: black;color: white;">
								<td>Type</td>
								<td>Details</td>
								<td>Owner</td>
								<td>Start</td>
								<td>End</td>
								<td>Duration (Hrs)</td>
								<td>Rate ( _ / Hr)</td>
								<td>Cost ( _ )</td>
								<td></td>
							</tr>
						</thead>
						<tbody>
							
						</tbody>
						</table>
</td></tr></table> <!-- end of just border table -->
</div>
</center></html>
