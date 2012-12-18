<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="in.mycp.domain.*" %>
<%@ page import="in.mycp.utils.*" %>
<html><center>

<script type='text/javascript' src='/dwr/interface/AccountLogService.js'></script>

<script type="text/javascript">
function filterResult(p){
	var Index = p.options[p.selectedIndex].text; 
	$("#filterName").val(Index);
	document.forms["filterLog"].submit();
	}

$(document).ready(function() {
	
	AccountLogService.getAllAccountLogTypes(function(p){
		dwr.util.removeAllOptions('accountLogType');
		dwr.util.addOptions('accountLogType', p, 'id', 'name');
		//alert('${filter}');
		dwr.util.setValue('accountLogType', '${filter}');
	});
	
	
	
});
</script>


 						
						
<div class="dataTableHeader">Audit log.	
</div>

						
	  						
<table align="center" width="95%" ><!-- just for border -->
<tr><td>

							<table align="center" width="100%"  id="sessionLog" >
							<thead>
								<tr >
									<td width="80%" align="right">Filter:</td>
									<td width="20%" align="right">
									<form action="/log/filterLog" name="filterLog">
										<select onchange="filterResult(this)" id="accountLogType" name="accountLogType" style="width: 200px;" >
								    	</select>
								    	<input type="hidden" name="filterName" id="filterName">
								    	</form>
							    	</td>
								</tr>
							</thead>
							<tbody>
							</tbody></table>
							
	  						
							<table align="center" width="100%"  id="sessionLog" >
							<thead>
								<tr style="background-color: black;color: white;">
									<td width="10%">User</td>
									<td width="10%">Task</td>
									<td width="60%">Message</td>
									<td width="10%">Date</td>
									<td width="10%">Status</td>
								</tr>
							</thead>
							<tbody>
							
<% 
List<AccountLog> accountLogList = (List)request.getAttribute("sessionLogList");
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh.mm");
  int i =1;
	for (Iterator iterator = accountLogList.iterator(); iterator.hasNext();) {
		AccountLog accountLog = (AccountLog) iterator.next();
		i++;
		if(i%2 ==0){
%>
			<tr style="background-color: white;">
<%
		}else{
			
%>
			<tr>
<%
		}//end of else
%>
			<td width="10%">
				<%=accountLog.getUserId().getEmail() %> 
				
			</td>
			
			<td width="10%">
				
				<%=accountLog.getTask() %> 
				
			</td>
			
			<td width="60%">
				 
				 
				<%=accountLog.getDetails() %>.
			</td>
			<td width="10%">
				<%=formatter.format(accountLog.getTimeOfEntry()) %> 
				
			</td>
			<td width="10%">
				<% 
				if( accountLog.getStatus().intValue() == Commons.task_status.SUCCESS.ordinal())
				{
				%>
				<font color="green">
					<%=Commons.task_status.SUCCESS.name()%>
				</font> 
				<% 
				}else{
				%>
				<font color="red">
					<%=Commons.task_status.FAIL.name()%>
				</font>
				<% 
				}
				%>
			</tr>
<%
  }//end of for looop deptList.iterator()
%>
							
	
		</tbody>
		</table>			
						
</td></tr></table> <!-- end of just border table -->						
		
</center></html>