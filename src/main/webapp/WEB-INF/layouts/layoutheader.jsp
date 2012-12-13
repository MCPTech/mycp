
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
	
	<script type='text/javascript' src='/dwr/interface/RealmService.js'></script>
	
	<link type="text/css" href="/js/jqueryplugins/sticky/sticky.full.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/home.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/vader/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />
	<link type="text/css" href="/styles/popup.css" rel="Stylesheet" />
	
	<link href="/styles/login_1.css" media="all" rel="stylesheet" type="text/css" />
	<link href="/styles/login_2.css" media="all" rel="stylesheet" type="text/css" />
</head>

<body>
	<div style="width: 100%;height: 80px;"></div>
  <h2 align="center">
	<img width="300px;" src="/images/mcp_logo.PNG">
  </h2>
<div id="divSignIn" class="page-width">
<div id="box-wrapper" class="login-box v2">
  <!-- login page div -->
  <div class="content  with-foot ">
	  <form name="formSignIn" id="formSignIn" action="/resources/j_spring_security_check" method="POST">
	    <input id="return_to" name="return_to" type="hidden" />
	
	    <dl>
	      <dt><label for="email">Email:</label></dt>
	      <dd><input id="j_username" type='text' name='j_username' maxlength="40"/></dd>
	
	      <dt><label for="password">Password:</label></dt>
	      <dd><input id="j_password" type='password' name='j_password'  maxlength="40" /></dd>
	      <dt style="color: red;">
			  <%try{
			  String s = ((BadCredentialsException)session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage();
			  out.print(s);
			  }catch(Exception e){}
			  %>
		  </dt>
	    </dl>
	
	    <div class="login-box-footer">
	      <div class="inner forgot-password">
	        <a id="lnkForgotPwd" href="#">forgot my password</a>
	      </div>
	      <div class="inner remember-me">
	        <!-- <input checked="checked" id="remember_me" name="remember_me" type="checkbox" value="1" />
	        <label for="remember_me">Keep me logged in</label> -->
	      </div>
	      <div class="inner">
	      	<input class="flatbutton green" type="submit" value="Sign In">
	      </div>
	    </div>
	</form>
  </div>
    <footer>
	    <div class="badge" id="divNoAcc">
	      Don't have an account? <a id="lnkSignUp" href="#">Sign up</a>
	    </div>
    </footer>
</div>
</div>

<!-- forgot password div -->
<div id="divForgotPwd" class="page-width">
	<div id="box-wrapper" class="small-box">
	  <div id="forgotPwd" class="content small-box-content "> 
		<form id="formRecoverPwd" name="formRecoverPwd">
		    <label for="mail">Enter your email address</label>
		    <div class="field">
		      <input class="required email" id="mail" name="mail" type="text" />
		    </div>
		    <label style="font-size: 11px;" id="lblRecoverErrMsg"></label>
		    <button type="button" class="flatbutton green" id="btnForgot" name="btnForgot">Reset password</button>
		</form>
	  </div>
	    <footer>
	          <div class="login-tip">
	            Already have an account? <a id="lnkSignIn1" href="#">Log in here</a>
	          </div>
	    </footer>
	</div>
</div>

<!-- sign up div -->
<div id="box-wrapper" class="login-box v2">
	<div id="divSignUp" class="content  with-foot">
		<form novalidate="novalidate" id="signupform" method="post" name="signupform" action="/cloud-portal/signup">
			<input id="id" name="id" type="hidden">
			<table border="0">
				  <tr>
					<td>Name :
					<input name="name" id="name" size="30" maxlength="30" class="required" type="text"></td>
				  </tr>
				  <tr>
					<td>Email : 
					<input name="email" id="email" size="30" maxlength="40" class="email required" type="text"></td>
				  </tr>
				  <tr>
					<td>Password : 
					<input name="password" id="password" size="30" maxlength="40" class="required" minlength="6" type="password"></td>
				  </tr>
				  <tr>
					<td>Organization : 
					<input name="organization" id="organization" maxlength="40" size="30" class="required" type="text"></td>
				  </tr>
				  <tr>
					<td>Enter text in the box 
					<input id="captchaResp" name="captchaResp" maxlength="5" size="30" class="required" type="text">
					</td>
				  </tr>
				  <tr>
					<td><img id="captchaImg" style="border:1px solid grey;" />
					<!-- <a href="#" id="lnkNewCaptcha" title="New Captcha"><img align="top" alt="" src="/images/sync.png"></a> --></td>
				  </tr>
					<tr>
					    <td style="width: 100%; " colspan="2"><label id="lblSignUpErrMsg"></label></td>
				  	</tr> 
				  <tr>
					<td>
						 <button type="button" style="width: 100px;height: 30px;" class="flatbutton green" id="btnSignUp" name="btnSignUp">Sign Up</button>
					</td>
				  </tr>
				  <tr>
				  	<td>
				  		Already have an account? <a id="lnkSignIn2" href="#">Log in here</a>
				  	</td>
				  </tr>
			</table>
		</form>
	</div>
</div>