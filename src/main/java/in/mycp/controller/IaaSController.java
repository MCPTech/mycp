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
package in.mycp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RequestMapping("/iaas")
@Controller
public class IaaSController  {


	@RequestMapping(produces = "text/html")
	public String main(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/main";
	}
	
	@RequestMapping(value="/compute", produces = "text/html")
	public String compute(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/compute";
	}
	
	@RequestMapping(value="/snapshot", produces = "text/html")
	public String snapshot(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/snapshot";
	}
	
	@RequestMapping(value="/desktop", produces = "text/html")
	public String desktop(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/desktop";
	}
	
	@RequestMapping(value="/image", produces = "text/html")
	public String image(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/image";
	}
	
	@RequestMapping(value="/ipaddress", produces = "text/html")
	public String ipaddress(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/ipaddress";
	}
	
	@RequestMapping(value="/keys", produces = "text/html")
	public String keys(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/keys";
	}
	
	@RequestMapping(value="/network", produces = "text/html")
	public String network(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/network";
	}
	
	@RequestMapping(value="/secgroup", produces = "text/html")
	public String secgroup(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/secgroup";
	}
	
	@RequestMapping(value="/volume", produces = "text/html")
	public String volume(HttpServletRequest req, HttpServletResponse resp) {
		return "iaas/volume";
	}
	
}
