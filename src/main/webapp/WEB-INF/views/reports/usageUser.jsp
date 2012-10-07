<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="in.mycp.domain.*" %>

<html><center> 						
						
						<div class="dataTableHeader">Resource Usage per User.</div>
						
	  						
<table align="center" width="95%" ><!-- just for border -->
<tr><td>
	  						
							<table  align="center" width="100%"  id="resourceUsage" >
							<thead>
								<tr style="background-color: black;color: white;">
									
									<td>Type</td>
									<td>Details</td>
									<td>Project</td>
									<td>Start</td>
									<td>End</td>
									<td>Duration (Hrs)</td>
									<td>Rate (<%=request.getAttribute("currency") %>/Hr)</td>
									<td>Cost (<%=request.getAttribute("currency") %>)</td>
									
								</tr>
							</thead>
							<tbody>
<% 
Hashtable<String, List> userHash = (Hashtable)request.getAttribute("userHash");
Enumeration userHashKeys = userHash.keys();
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh.mm");
while( userHashKeys.hasMoreElements() ) {
  Object userName = userHashKeys.nextElement();
  List userList = (List)userHash.get(userName);
  //System.out.println(" = "+deptName);
  long totalCost=0;
  if(userList.size()<1){
	  //continue;
  }
  %>
							<tr style="height: 30px;">
								<td colspan="8"><div style="width:25%; color: #d45500; font-weight: bold; float: left;"><%=userName %></div></td>
							</tr>		
  <%
  for (Iterator iterator = userList.iterator(); iterator.hasNext();) {
		Asset asset = (Asset) iterator.next();
		totalCost = totalCost+asset.getCost();
		String projName = "";
		try{projName = asset.getUser().getProject().getName();}catch(Exception e){}
%>
							<tr>
								<td></td>
								<td><%=asset.getAssetType().getName() %> - <%=asset.getAssetDetails() %></td>
								<td><%=projName %></td>
								<td><%=formatter.format( asset.getStartTime()) %></td>
								<td><%
								if(asset.getEndTime() !=null){
									out.println(formatter.format( asset.getEndTime()));
								}else{
									out.println("-");
								}
								%></td>
								<td><%=asset.getDuration() %></td>
								<td><%=asset.getStartRate() %></td>
								<td><%=asset.getCost() %></td>
							</tr>
<%
  }//end of for looop deptList.iterator()
%>
							<tr>
								<td></td>
								<td colspan="7"> <hr></td>
								
							</tr>	
							<tr>
								
								<td colspan="7"> </td>
								<td colspan="1"><div style="color: #d45500; font-weight: bold; text-align: left;">Total : <%=totalCost %>&nbsp;&nbsp;<%=request.getAttribute("currency") %></div></td>
							</tr>	
						<tr>
								<td></td>
								<td colspan="7"> <hr></td>
								
							</tr>		
						
<%
}//end of while loop deptHashKeys.hasMoreElements()
%>
				
						</tbody>
						</table>
</td></tr></table> <!-- end of just border table -->						
		
</center></html>