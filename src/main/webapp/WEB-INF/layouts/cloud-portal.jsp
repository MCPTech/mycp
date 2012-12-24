
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

<script>
$(function(){
	$("#divSignUp").hide();
	$("#divForgotPwd").hide();
});

$("#lnkSignUp").click(function(){
	$("#signupform")[0].reset();
	$("#divSignIn").hide();
	$("#divNoAcc").hide();
	$("#divForgotPwd").hide();
	$("#divSignUp").show();
	$("#captchaImg").attr("src", "/jcaptcha.jsp");
});

$("#lnkSignIn1").click(function(){
	$("#formSignIn")[0].reset();
	$("#divSignIn").show();
	$("#divNoAcc").show();
	$("#divForgotPwd").hide();
	$("#divSignUp").hide();
	$("#lblRecoverErrMsg").html('');
	$("#lblSignUpErrMsg").html('');
});

$("#lnkSignIn2").click(function(){
	$("#formSignIn")[0].reset();
	$("#divSignIn").show();
	$("#divNoAcc").show();
	$("#divForgotPwd").hide();
	$("#divSignUp").hide();
	$("#lblRecoverErrMsg").html('');
	$("#lblSignUpErrMsg").html('');
});

$("#lnkForgotPwd").click(function(){
	$("#formRecoverPwd")[0].reset();
	$("#divSignIn").hide();
	$("#divNoAcc").hide();
	$("#divForgotPwd").show();
	$("#divSignUp").hide();
});

$("#btnForgot").click( function(){
	
	$.post('/cloud-portal/recoverPwd', $("#formRecoverPwd").serialize(),
		function(data){
			$("#lblRecoverErrMsg").html(data);
		}
	);
});

$("#btnSignUp").click( function(){
	$.post('/cloud-portal/validateSignup', $("#signupform").serialize(),
		function(data){
			$("#lblSignUpErrMsg").html(data);
			if(data =='') {
				$('#signupform').submit();
			}
		}
	).complete(function() {  });
});

$("#lnkNewCaptcha").click(function(){
	$("#captchaImg").attr("src", "/jcaptcha.jsp");
});

</script>
