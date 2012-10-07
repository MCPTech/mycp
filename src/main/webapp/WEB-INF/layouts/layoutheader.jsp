
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.security.authentication.BadCredentialsException" %>
	
	<script type="text/javascript" src="/dwr/engine.js"></script>
	<script type="text/javascript" src="/dwr/util.js"></script>
	<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="/js/jquery-ui-1.8.16.custom.min.js"></script>	
	<script type="text/javascript" src="/js/jqueryplugins/jquery.validate.js"></script>
	<script type="text/javascript" src="/js/jqueryplugins/sticky/sticky.full.js"></script>
	<script type="text/javascript" src="/js/myccep.js"></script>
	<script type="text/javascript" src="/js/jqueryplugins/nospam.js"></script>
	<script type="text/javascript" src="/dwr/interface/SignupService.js"></script>
	<script type='text/javascript' src='/dwr/interface/RealmService.js'></script>
	
	
	
	<link type="text/css" href="/js/jqueryplugins/sticky/sticky.full.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/home.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/vader/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/popup.css" rel="Stylesheet" />
	
	<script type="text/javascript">
	$(function() {
		$( "input:button", ".demo" ).button();
		$('a.email').nospam();
		$("#thisform").validate({
			 submitHandler: function(form) {
				form.submit();
				return true;
			 }
			});
	});
	</script>


</head>

<body>


	<div class="band">
		<div id="header">
			<a href="/cloud-portal">
				<div class="logo">
					My Cloud Portal<sup>beta</sup><br />
					<span>any service any cloud</span>
					<div style="padding-top: 15px;"><marquee>
					<font color="green" size="1.5px">
					
					</font>
					</marquee></div>
				</div>
			</a>
			<div class="login">
			
			<form name="f" action="/resources/j_spring_security_check" method="POST">
				<table  >
				  <tr >
				    <td style="width: 30%;">Email  </td>
				    <td style="width: 30%;">Password  </td>
					<td style="width: 30%; "></td>				    
				  </tr>
				  
				  <tr>
				    <td style="width: 30%;">
				    	<input id="j_username" type='text' name='j_username' maxlength="40" style="width:200px" />
				    </td>
				    <td style="width: 30%;">
				    	<input id="j_password" type='password' name='j_password'  maxlength="40" style="width:200px" />
				    </td>
				    <td style="width: 30%;">
				    	<input class="login-button" type="submit" value="Sign In">
				    </td>
				  </tr>
				  <tr >
				  
				  <td colspan="3" style="color: red;">
					  <%try{
					  String s = ((BadCredentialsException)session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage();
					  out.print(s);
					  }catch(Exception e){}
					  %>
				  </td>
				  </tr>
				  <tr>
				  	<td colspan="3" style="height: 6px;"></td>
				  </tr>
				  <tr>
				  <td colspan="3" style="color: red;">
						  
				  </td>
				  </tr>
				  
				</table>
			</form>
			</div>
			
		</div>
	</div>