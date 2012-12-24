<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="format" %>

<script type='text/javascript' src='/dwr/interface/ReportService.js'></script>
<script type='text/javascript' src='/dwr/interface/CompanyService.js'></script>
<script type='text/javascript' src='/dwr/interface/DepartmentService.js'></script>
<script type='text/javascript' src='/dwr/interface/ProjectService.js'></script>
<script type='text/javascript' src='/dwr/interface/RealmService.js'></script>

<script>

$(function(){
	$('#dataTable').dataTable( {
		"sPaginationType" : "full_numbers",
			"bDestroy": true,
	    	"bAutoWidth": false,
	    	"bDeferRender": true,
	    	"bJQueryUI": false,
	    	"bLengthChange": false,
	    	"iDisplayLength": 25
	}); 
	$('#filterAsset').val('${FILTER}');
});

function filterResult(){
	document.forms["frmAssignAsset"].submit();
}

function autocomp(c){
	//alert(c[0].id);
	var compAvailableTags = [];
	for (i=0;i<c.length;i++){
		compAvailableTags.push({value: c[i].name, id: c[i].id});
	}
	$(".autocompleteComp").autocomplete({
		source: compAvailableTags,
		select: function(event1, ui) {
			showUpdateImgae(this.id);
			$("#companyHiden").val(ui.item.id);
			deptByCompany({id: ui.item.id});
		}
	});
}


function showUpdateImgae(currFieldId){
	//remove all update images
	$('.updateRecord').each(function() {$(this).html('');});
	var assetId = currFieldId.split('_')[1];
	//add image for the current row
	$("#updateRecord_"+assetId).html('<img src="/images/update.png">');
}

function deptByCompany(companyJsonId){
	DepartmentService.findDepartmentsByCompany(companyJsonId, function(d){
		var deptAvailableTags = [];
		for (i=0;i<d.length;i++){
			deptAvailableTags.push({value: d[i].name, id: d[i].id});
		}
		$(".autocompleteDept").autocomplete({source: deptAvailableTags
			,select: function(event2, ui2) { 
				showUpdateImgae(this.id);
				$("#departmentHiden").val(ui2.item.id);
				projByDept({id: ui2.item.id});
			}
		});
	});
}

function projByDept(deptJsonId){
	ProjectService.findProjectsByDepartment(deptJsonId, function(p){
		var projAvailableTags = [];
		for (i=0;i<p.length;i++){
			projAvailableTags.push({value: p[i].name, id: p[i].id});
		}
		$(".autocompleteProj").autocomplete({source: projAvailableTags
			,select: function(event3, ui3){
				showUpdateImgae(this.id);
				$("#projectHiden").val(ui3.item.id);
				RealmService.findUsersByDepartment(deptJsonId, function(u){
						var availableTags = [];
						for (i=0;i<u.length;i++){
							availableTags.push({value: u[i].email, id: u[i].id});
						}
						$(".autocompleteUser").autocomplete({source: availableTags
						,select: function(event4, ui4){
							showUpdateImgae(this.id);
							$("#userHiden").val(ui4.item.id);
						}
						});
				});
			}
		});
	}); 
}

function updateRecord(assetId){
	try{
		ReportService.updateAsset(assetId, $("#companyHiden").val(), $("#departmentHiden").val(), $("#projectHiden").val(), $("#userHiden").val());
		$.sticky('Asset assigned.!'  );
	}catch(ex){$.sticky('Failed to assign the Asset.!');}
	$('.updateRecord').each(function() {$(this).html('');});
}

function findAll(p){
	$('#dataTable').dataTable( {
		"sScrollX": "100%",
		"sScrollY" : 500,
		"bJQueryUI" : false,
		"sPaginationType" : "full_numbers"
	});
}

</script>

<p class="dataTableHeader">Asset Assignment</p>

<table align="center" width="98%"  id="sessionLog" border="0">
	<tr >
		<td width="82%" align="right">Filter:</td>
		<td width="17%" align="right">
			<form action="assignAsset" name="frmAssignAsset" method="post">
				<select onchange="filterResult()" id="filterAsset" name="filterAsset" style="width: 200px;" >
					<option value="">Unassigned</option>
					<option value="-1">All</option>
		    	</select>
	    	</form>
    	</td>
	</tr>
</table><br>

<div id="datatable-iaas-parent" class="infragrid2">
		<div id="datatable-iaas" >
			<table cellpadding="0" cellspacing="5" border="0" class="display" id="dataTable">
				<thead>
					<tr>
						<th>AssetType</th>
						<th>Infra</th>
						<th>Name</th>
						<th>Company</th>
						<th>Department</th>
						<th>Project</th>
						<th>User</th>
						<th></th>
					</tr>
				</thead>
				<tbody align="center">
					<c:forEach var="asset" items="${ASSET_LIST}">
						<tr>
							<td>${asset.assetType.name }</td>
							<td>${asset.productCatalog.infra.name}</td>
							<td>${asset.name}</td>
							<td><input type="text" name="company" id="company_${asset.id}" class="autocompleteComp" value="${asset.user.department.company.name}"/>
							<%-- <input type="hidden" name="companyHiden" id="companyHiden${asset.id}"/> --%>
							</td>
							<td><input type="text" name="department" id="department_${asset.id}" class="autocompleteDept" value="${asset.user.department.name}" onclick="deptByCompany({id: '${asset.user.department.company.id}'}); "/>
							<%-- <input type="hidden" name="departmentHiden" id="departmentHiden${asset.id}"/></td> --%>
							<td><input type="text" name="project" id="project_${asset.id}" class="autocompleteProj" value="${asset.project.name}" onclick="projByDept({id: '${asset.user.department.id}'}); "/>
							<%-- <input type="hidden" name="projectHiden" id="projectHiden${asset.id}"/></td> --%>
							<td width="5%"><input type="text" name="user" id="user_${asset.id}" class="autocompleteUser" value="${asset.user.email}"/>
							<%-- <input type="hidden" name="userHiden" id="userHiden${asset.id}"/></td> --%>
							<td width="3%"><a href="#" title="Update" class="updateRecord" name="updateRecord" id="updateRecord_${asset.id}" onclick="updateRecord('${asset.id}');"> </a>
							
							 <input type="hidden" name="companyHiden" id="companyHiden"/>
							 <input type="hidden" name="departmentHiden" id="departmentHiden"/>
							 <input type="hidden" name="projectHiden" id="projectHiden"/>
							 <input type="hidden" name="userHiden" id="userHiden"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div style="height: 50px;"></div>
			<table align="right" border="0" width="100%">
				<tr>
					<td width="80%">
					
					</td>
				</tr>
			</table>
			
	</div>
</div>