//My Cloud Portal - Self Service Portal for the cloud.
//This file is part of My Cloud Portal.
//
//My Cloud Portal is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, version 3 of the License.
//
//My Cloud Portal is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with My Cloud Portal.  If not, see <http://www.gnu.org/licenses/>.

package in.mycp.controller;

import in.mycp.domain.AccountLog;
import in.mycp.domain.Asset;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.remote.AccountLogService;
import in.mycp.remote.ReportService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RequestMapping("/log")
@Controller
public class AccountLogController {

	private static final Logger log = Logger.getLogger(AccountLogController.class.getName());

	@Autowired
	AccountLogService accountLogService;

	@RequestMapping(value = "/account", produces = "text/html")
	public String session(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("sessionLogList", accountLogService.getLast24HoursLog());
		return "log/accountLog";
	}

}//end of class
