package in.mycp.service;

import in.mycp.remote.AccountLogService;
import in.mycp.utils.Commons;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class MycpLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Autowired
	AccountLogService accountLogService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		setDefaultTargetUrl("/cloud-portal");
		try {
			//authentication object will be null when session has timed out
			if(authentication !=null)
			accountLogService.saveLog("User logged out",
					Commons.task_name.LOGOUT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					authentication.getName());
		} catch (Exception e) {
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
		super.onLogoutSuccess(request, response, authentication);

	}
	
}