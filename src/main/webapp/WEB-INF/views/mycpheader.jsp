<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="in.mycp.utils.Commons" %>
<%@ page import="in.mycp.domain.User" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
	<title>Open Source Self Service Portal for the Cloud</title>
    
    <link type="text/css" href="/styles/googlemenu.css" rel="stylesheet" />
    <script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
    
    <link type="text/css" href="/styles/vader/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />	
<link type="text/css" href="/styles/global.css" rel="Stylesheet" />
<link type="text/css" href="/styles/myccep.css" rel="Stylesheet" />
<link type="text/css" href="/styles/popup.css" rel="Stylesheet" />


<script type="text/javascript" src="/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="/js/jqueryplugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="/js/jqueryplugins/jquery.validate.js"></script>


<script type="text/javascript" src="/js/jqueryplugins/sticky/sticky.full.js"></script>
<link type="text/css" href="/js/jqueryplugins/sticky/sticky.full.css" rel="Stylesheet" />


<script type="text/javascript" src="/js/myccep.js"></script>

	<script type="text/javascript" src="/dwr/engine.js"></script>
		<script type="text/javascript" src="/dwr/util.js"></script>
		<script type="text/javascript" src="/dwr/interface/eucalyptusService.js"></script>
		<script type="text/javascript" src="/dwr/interface/CommonService.js"></script>
		<script type="text/javascript">
		
		$(document).ready(function() {
			CommonService.getCurrentSession(function(p){
				dwr.util.setValue('mysession', ' '+p.company+' '+p.email+' '+dateFormat(p.loggedInDate));
			});
		});
			</script>
			
</head>
<body>

<div class="container">
    <div id="content">
	<div class="menu">				


    <ul>
    	<li><a class="right" href="/cloud-portal/dash">Home</a>
	</li>
    
    <%
    String roles = Commons.getCurrentUserRolesNonDWR();
    
    if(roles.contains(Commons.ROLE.ROLE_ADMIN+"") || 
    		roles.contains(Commons.ROLE.ROLE_MANAGER+"") ||
    			roles.contains(Commons.ROLE.ROLE_SUPERADMIN+""))
    {
    %>
    
    	<li><a class="left dropdown" href="#">Setup<span class="arrow"></span></a>
		<ul class="width-2">
		    <li><a href="/enterprise/company">Account</a></li>
		    <li><a href="/enterprise/department">Department</a></li>
		    <li><a href="/enterprise/project">Project</a></li>
		    <li><a href="/realm/user">User</a></li>
		</ul>
		</li>
		<li><a class="right dropdown" href="#">Configuration<span class="arrow"></span></a>
		<ul class="width-2">
		    <li><a href="/config/infra">Cloud</a></li>
		    <li><a href="/config/zone">Availability Zone</a></li>
		    <li><a href="/config/assettype">Product Type</a></li>
		    <li><a href="/config/product">Product</a></li>

		</ul>
		</li>
        
      <%} %>
      
      	<li><a class="dropdown" href="#">Resource<span class="arrow"></span></a>
		<ul class="width-3">
		    <li><a href="/iaas/compute">Compute</a></li>
		    <li><a href="/iaas/volume">Volume</a></li>
		    <li><a href="/iaas/ipaddress">IP Address</a></li>
		    <li><a href="/iaas/secgroup">Security Groups</a></li>
		    <li><a href="/iaas/keys">Key Pairs</a></li>
		    <li><a href="/iaas/image">Images</a></li>
		    <li><a href="/iaas/snapshot">Snapshots</a></li>
		</ul>
		</li>
		
        
        <%
	    if(roles.contains("ROLE_ADMIN") || 
	    		roles.contains("ROLE_MANAGER") ||
	    			roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
         <li><a class="dropdown" href="#">Control<span class="arrow"></span></a>
			<ul class="width-3">
			    <li><a href="/workflow/processInstance">Workflows</a></li>
			    <li><a href="/log/session">Session Log</a></li>
			</ul>
		</li>
	  	<%} %>
	  	
	  	<li><a class="dropdown" href="#">Usage Reports<span class="arrow"></span></a>
			<ul class="width-3">
			    

        <%
	    if(roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
	    <li><a href="/reports/usageAll">All</a></li>
			
        <%} %>
        
        <%
	    if(roles.contains("ROLE_ADMIN") || 
	    		roles.contains("ROLE_MANAGER") ||
	    			roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
	    <li><a href="/reports/usageDept">Departments</a></li>
	    <li><a href="/reports/usageProj">Projects</a></li>
	
       <%} %>
       <li><a href="/reports/usageUser">Users</a></li>
            </ul>
		</li>
		<li><a class="right" href="/resources/j_spring_security_logout"><span>Logout</span></a></li>
    </ul>
	<div style=" color: grey;line-height: 30px;padding: 1px 20px;    
	   		text-align: right; cursor: pointer;display: block;  font-weight: bold; font-size: small;"> 
	   			
	   		<span id="mysession"> </span>
	   </div>    
  
</div><!-- <div class="menu"> -->
</div><!-- <div id="content"> -->
</div><!-- <div class="container">  -->




