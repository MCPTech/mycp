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

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RequestMapping("/config")
@Controller
public class ConfigController  {

	@RequestMapping(value="/infra", produces = "text/html")
	public String infra(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/infra");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/infra";
	}

	@RequestMapping(value="/product", produces = "text/html")
	public String product(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/product");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/product";
	}


	@RequestMapping(value="/quotas", produces = "text/html")
	public String quotas(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/quotas");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/quotas";
	}

	@RequestMapping(value="/zone", produces = "text/html")
	public String zone(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/zone");
		return "config/zone";
	}

	@RequestMapping(value="/region", produces = "text/html")
	public String region(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/region");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/region";
	}

	@RequestMapping(value="/assettype", produces = "text/html")
	public String assettype(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/assettype");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/assettype";
	}

	@RequestMapping(value="/metermetric", produces = "text/html")
	public String metermetric(HttpServletRequest req, HttpServletResponse resp) {
		//System.out.println("config/metermetric");
		//ModelAndView modelandView = new ModelAndView("main");
		return "config/metermetric";
	}

}
