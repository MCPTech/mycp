
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%@ page import="java.util.*" %>
<%@ page import="org.springframework.security.authentication.BadCredentialsException" %>

<html xmlns:og="http://ogp.me/ns#">
	<head>
		<title>Cloud Portal - open source web portal for any cloud service</title>
        <meta name="author" content="Charudath Doddanakatte" />
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <meta name="description" content="MyCloudPortal is a generic self service portal for the cloud. MyCloudPortal provides 
        a single pane of glass for the user to Consume and Govern any service from any cloud. Add this portal layer 
        onto Amazon public cloud and Eucalyptus private cloud in its current state."/>
        <meta name="keywords" content="cloud portal"/>
        <meta id="MetaCopyright" name="COPYRIGHT" content="Copyright 2012 MyCloudPortal" />
        <meta name="ROBOTS" content="INDEX, FOLLOW"/>
        <link rel="image_src" href="/images/mycpSmall.PNG" type="image/x-icon" />
        <meta property="og:title" content="MyCloudPortal" />
        <meta property="og:type" content="website" />
        <meta property="og:image" content="http://mycloudportal.in/images/mycpSmall.PNG" />
        <meta property="og:url" content="http://mycloudportal.in" />
	
<jsp:include page="layoutheader.jsp"></jsp:include>

  	<div id="wrapper">
		<div class="leftMain">
			<div class="intro">
				
				<p>As an enterprise <a href="/cloud-management.jsp">cloud management solution </a> the idea of MyCloudPortal is to provide a single pane of glass for the user to
				<span>Consume</span> and <span>Govern</span> any service from any cloud. This cloud  management solution can be used as a <a href="/cloud-broker.jsp">cloud broker</a>, <a href="/cloud-broker.jsp">cloud gateway</a> or a cloud control panel.</p>
				<p>In its current state it enables users to consume, monitor and manage services from <b>Eucalyptus private cloud & AWS public cloud
				 (Amazon Web Services).
				</b></p>
			</div>
			<br></br>
			<br></br>
			<br></br>
			<br></br>
			
			<div >
			This is a white labelled version, you can change the look&feel the way you desire.
			
			</div>
		</div>

		<div class="rightMain">
			<div style="padding: 15px 0;">
				<a href="http://code.google.com/p/mycloudportal/" target="_blank"><img src="/images/opensource.png" style="float:left; margin-right:10px" height="48" width="48"></a>
				MyCloudPortal is <font style="font-weight: bold;">open source</font> and <font style="font-weight: bold;">free</font> to
				<a href="http://code.google.com/p/mycloudportal/" target="_blank"> download &amp; use.</a>
			</div>

			<div class="demoVideo">
				<a target="_blank" href="http://www.youtube.com/v/G4zwfKRqEYY?version=3&amp;feature=player_detailpage&amp;loop=1&amp;autoplay=1&amp;modestbranding=1&amp;rel=0&amp;theme=light">
					<img alt="video preview" src="/images/videos1.PNG">
				</a>
				<p>Watch the getting started Video</p>
			</div>

			<div class="rgtnForm">
				<p>Sign Up Here!</p>
				<form novalidate="novalidate" class="cmxform" id="thisform" method="post" name="thisform" action="/cloud-portal/signup">
					<p id="contactArea_signup" class="contactArea">
						<input id="id" name="id" type="hidden">
					</p>
					<table>
						  <tr>
							<td>Name : <br />
							<input name="name" id="name" size="30" maxlength="30" class="required" type="text"></td>
						  </tr>
						  <tr>
							<td>Email : <br />
							<input name="email" id="email" size="30" maxlength="40" class="email required" type="text"></td>
						  </tr>
						  <tr>
							<td>Password : <br />
							<input name="password" id="password" size="30" maxlength="40" class="required" minlength="6" type="password"></td>
						  </tr>
						  <tr>
							<td>Organization : <br />
							<input name="organization" id="organization" maxlength="40" size="30" class="required" type="text"></td>
						  </tr>
						  <tr>
							<td>Enter text in the box <br />
							<input id="captchaResp" name="captchaResp" maxlength="5" size="30" class="required" type="text">
							</td>
						  </tr>
						  <tr>
							<td><img  style="border:1px solid grey;" src="/jcaptcha.jsp" /></td>
						  </tr>
							<tr>
							    <td style="width: 100%; " colspan="2">
								    <%try{
										  String s = ((String)session.getAttribute("MYCP_SIGNUP_MSG"));
										  if(s!=null && !s.equals("") && !s.equals("null")){out.print(s);}
									  }catch(Exception e){}
									  %>
								  </td>
						  	</tr> 
						  <tr>
							<td style="width: 50%;">
								<div class="demo" id="popupbutton_signup_create">
									<input aria-disabled="false" role="button" class="ui-button ui-widget ui-state-default ui-corner-all" value="Sign Up" type="submit">&nbsp;&nbsp;&nbsp;&nbsp;
								</div>
							</td>
						  </tr>
					</table>
				</form>
			</div>
			<div class="newsfeatures">
			
			</div>
		</div>
		
		<div class="clr"></div>

	</div>

	<jsp:include page="layoutfooter.jsp"></jsp:include>