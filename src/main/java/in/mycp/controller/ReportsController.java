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

import in.mycp.domain.Asset;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Project;
import in.mycp.domain.User;
import in.mycp.remote.ReportService;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RequestMapping("/reports")
@Controller
public class ReportsController {

	private static final Logger log = Logger.getLogger(ReportsController.class.getName());

	@Autowired
	ReportService reportService;

	@RequestMapping(value = "/usage", produces = "text/html")
	public String usage(HttpServletRequest req, HttpServletResponse resp) {
		return "reports/usage";
	}

	@RequestMapping(value = "/usageAll", produces = "text/html")
	public String usageAll(HttpServletRequest req, HttpServletResponse resp) {
		return "reports/usageAll";
	}

	@RequestMapping(value = "/usageDept", produces = "text/html")
	public String usageDept(HttpServletRequest req, HttpServletResponse resp) {
		
		Hashtable<String, List> deptHash = new Hashtable<String, List>();
		if (Commons.getCurrentSession() != null && Commons.getCurrentSession().getCompanyId() > 0) {
			Company c = Company.findCompany(Commons.getCurrentSession().getCompanyId());
			req.setAttribute("currency", c.getCurrency());
			Set<Department> depts = c.getDepartments();
			
			for (Iterator iterator = depts.iterator(); iterator.hasNext();) {
				Department department = (Department) iterator.next();
				deptHash.put(department.getName(), reportService.findAssets4Department(department.getId()));
			}
		} else {
			List<Company> comps = Company.findAllCompanys();
			req.setAttribute("currency", "All");
			for (Iterator iterator = comps.iterator(); iterator.hasNext();) {
				Company company = (Company) iterator.next();
				Set<Department> depts = company.getDepartments();
				for (Iterator iterator1 = depts.iterator(); iterator1.hasNext();) {
					Department department = (Department) iterator1.next();
					deptHash.put(department.getName() + " @ " + department.getCompany().getName(),
							reportService.findAssets4Department(department.getId()));
				}
			}
		}
		req.setAttribute("deptHash", deptHash);

		return "reports/usageDept";
	}

	@RequestMapping(value = "/usageProj", produces = "text/html")
	public String usageProj(HttpServletRequest req, HttpServletResponse resp) {
		Hashtable<String, List> projHash = new Hashtable<String, List>();
		if (Commons.getCurrentSession() != null && Commons.getCurrentSession().getCompanyId() > 0) {
			Company c = Company.findCompany(Commons.getCurrentSession().getCompanyId());
			req.setAttribute("currency", c.getCurrency());
			
			Set<Department> depts = c.getDepartments();
			for (Iterator iterator = depts.iterator(); iterator.hasNext();) {
				Department department = (Department) iterator.next();
				Set<Project> projs = department.getProjects();
				for (Iterator iterator2 = projs.iterator(); iterator2.hasNext();) {
					Project project = (Project) iterator2.next();
					projHash.put(project.getName(),reportService.findAssets4Project(project.getId()));
					/*projHash.put(project.getName() + " @ " + department.getName() + " - " + Commons.getCurrentSession().getCompany(),
							reportService.findAssets4Project(project.getId()));*/
				}
			}
		} else {
			req.setAttribute("currency", "All");
			List<Company> comps = Company.findAllCompanys();
			for (Iterator iterator = comps.iterator(); iterator.hasNext();) {
				Company company = (Company) iterator.next();
				Set<Department> depts = company.getDepartments();
				for (Iterator iterator1 = depts.iterator(); iterator1.hasNext();) {
					Department department = (Department) iterator1.next();
					Set<Project> projs = department.getProjects();
					for (Iterator iterator2 = projs.iterator(); iterator2.hasNext();) {
						Project project = (Project) iterator2.next();
						projHash.put(project.getName() + " @ " + department.getName() + " - " + company.getName(),
								reportService.findAssets4Project(project.getId()));
					}
				}
			}
		}
		req.setAttribute("projHash", projHash);
		return "reports/usageProj";
	}

	@RequestMapping(value = "/usageUser", produces = "text/html")
	public String usageUser(HttpServletRequest req, HttpServletResponse resp) {
		Hashtable<String, List> userHash = new Hashtable<String, List>();
		Hashtable<String, String> userSummaryHash = new Hashtable<String, String>();
		if (Commons.getCurrentSession() != null && Commons.getCurrentSession().getCompanyId() > 0) {
			List<User> users = new ArrayList<User>();
			Company c = Company.findCompany(Commons.getCurrentSession().getCompanyId());
			req.setAttribute("currency", c.getCurrency());
			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")
					|| Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "") ) {
				
				
				users = User.findUsersByCompany(c).getResultList();
			}else{
				users.add(User.findUser(Commons.getCurrentUser().getId()));
			}
		
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				//userSummaryHash.put(user.getEmail(), getCost(reportService.findAssets4User(user.getId())) + "");
				userHash.put(user.getEmail(), reportService.findAssets4User(user.getId()));
			}
		} else {
			List<User> users = User.findAllUsers();
			req.setAttribute("currency", "All");
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				//userSummaryHash.put(user.getEmail(), getCost(reportService.findAssets4User(user.getId())) + "");
				userHash.put(user.getEmail(), reportService.findAssets4User(user.getId()));
			}
		}
		req.setAttribute("userHash", userHash);
		//req.setAttribute("userSummaryHash", userSummaryHash);
		return "reports/usageUser";
	}

	public long getCost(List<Asset> assets) {

		/*
		 * 
		 * <div style="color: #15ADFF;">Resource Usage per User summary.</div>
		 * <div style="height: 30px;"></div>
		 * 
		 * <table align="center" width="30%" border="1" bordercolor="grey"><!--
		 * just for border --> <tr><td>
		 * 
		 * <table align="center" width="50%" id="resourceUsage" > <thead> <tr
		 * style="background-color: grey ;"> <td>User</td> <td>Costs</td>
		 * <td></td> </tr> </thead> <tbody>
		 * 
		 * 
		 * 
		 * <% Hashtable<String, String> userSummaryHash =
		 * (Hashtable)request.getAttribute("userSummaryHash"); Enumeration
		 * userSummaryHashKeys = userSummaryHash.keys(); while(
		 * userSummaryHashKeys.hasMoreElements() ) { String userNameStr =
		 * (String)userSummaryHashKeys.nextElement(); String totalCostStr =
		 * (String)userSummaryHash.get(userNameStr);
		 * 
		 * %>
		 * 
		 * 
		 * 
		 * <tr > <td><%=userNameStr %></td> <td><%=totalCostStr %></td>
		 * <td></td> </tr>
		 * 
		 * <% } %> </tbody> </table> </td></tr></table> <!-- end of just border
		 * table -->
		 */
		long totalCost = 0;
		for (Iterator iterator = assets.iterator(); iterator.hasNext();) {
			Asset asset = (Asset) iterator.next();
			totalCost = totalCost + asset.getCost();
		}
		return totalCost;
	}

}
