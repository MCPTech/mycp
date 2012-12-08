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
    <%
    String roles = Commons.getCurrentUserRolesNonDWR();
    boolean HOSTED_EDITION_ENABLED = false;
    boolean SERVICE_PROVIDER_EDITION_ENABLED = false;
    boolean PRIVATE_CLOUD_EDITION_ENABLED = false;
    
	 if(Commons.EDITION_ENABLED == Commons.HOSTED_EDITION_ENABLED){
		 HOSTED_EDITION_ENABLED = true;
	    }else if(Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED){
	    	SERVICE_PROVIDER_EDITION_ENABLED=true;
	    }else if(Commons.EDITION_ENABLED == Commons.PRIVATE_CLOUD_EDITION_ENABLED){
	    	PRIVATE_CLOUD_EDITION_ENABLED=true;
	    }
	 
	 /*
	 
	 Menu logic: 
		 SP edition:
		 if super_admin role and SP edition, then allow him to all permissions across all menu.
		 if manager role and SP edition, then remove Account, ALL Configuration menu.
		 if user role and SP edition,  then remove ALL setup menu,Configuration menu, reports, All,depts, projects.
		 Hosted edition:
		 if super_admin role and Hosted edition, then allow him  all permissions across all menu.
		 if manager role and Hosted edition, then allow him all permissions across all menu except 4except reports-All and data level restraints .
		 if user role and Hosted edition,  then remove ALL setup menu,Configuration menu, reports, All,depts, projects.
		 Private Cloud edition:
		 if super_admin role and Private Cloud  edition, then allow him  all permissions across all menu except Account creation
		 if manager role and Private Cloud  edition, then allow him all permissions across all menu except Account, reports-All and data level restraints .
		 if user role and Private Cloud  edition,  then remove ALL setup menu,Configuration menu, reports, All,depts, projects.
	 
	 */
	 
	 %>
	 
	<div class="menu">				
		<%if(HOSTED_EDITION_ENABLED){ %>
			<%@ include file="hostedmenu.jsp" %>
		<%}else if(SERVICE_PROVIDER_EDITION_ENABLED){ %>
			<%@ include file="serviceprovidermenu.jsp" %>
		<%}else if(PRIVATE_CLOUD_EDITION_ENABLED){ %>
			<%@ include file="privatecloudmenu.jsp" %>
		<%} %>  
	</div><!-- <div class="menu"> -->
	</div><!-- <div id="content"> -->
	</div><!-- <div class="container">  -->




