/*
 mycloudportal - Self Service Portal for the cloud.
 Copyright (C) 2012-2013 Mycloudportal Technologies Pvt Ltd

 This file is part of mycloudportal.

 mycloudportal is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 mycloudportal is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with mycloudportal.  If not, see <http://www.gnu.org/licenses/>.
 */
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
			/*if(authentication !=null)
			accountLogService.saveLog("User logged out",
					Commons.task_name.LOGOUT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					authentication.getName());*/
		} catch (Exception e) {
			logger.error(e.getMessage());
			//e.printStackTrace();
		}
		super.onLogoutSuccess(request, response, authentication);

	}
	
}