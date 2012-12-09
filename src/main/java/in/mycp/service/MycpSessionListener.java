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

import in.mycp.domain.User;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author GangadharJN
 *
 */
public class MycpSessionListener implements HttpSessionActivationListener,HttpSessionListener  {

	@Override
	public void sessionWillPassivate(HttpSessionEvent se) {
		/*System.out.println("Session Above to Destroy.!");
		User user = (User)se.getSession().getAttribute("CurrentUser");
		System.out.println(user.getEmail());
		*/
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent se) {
		System.out.println(se.getSession().getAttribute("CurrentUser"));
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("Session Created..");
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("Session Destroying.!");
		/*User user = (User)se.getSession().getAttribute("CurrentUser");
		System.out.println(user.getEmail());
		System.out.println("Session Destroyed.!");*/
	}


}
