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

package in.mycp.remote;

import in.mycp.domain.Company;
import in.mycp.domain.User;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "CompanyService")
public class CompanyService {

	private static final Logger log = Logger.getLogger(CompanyService.class.getName());

	@Autowired
	AccountLogService accountLogService;

	@RemoteMethod
	public void save(Company instance) {
		try {
			instance.persist();
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage());
		}
	}// end of save(Company

	@RemoteMethod
	public Company saveOrUpdate(Company instance) {
		try {

			if (instance.getId() != null && instance.getId() > 0) {
				Company company = findById(instance.getId());
				if (company.getQuota() != null && company.getQuota().intValue() != instance.getQuota().intValue()) {

					List<User> users = User.findManagersByCompany(company).getResultList();
					for (User user : users) {
						if (user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
							accountLogService.saveLogAndSendMail(
									"Company '" + instance.getName() + "' Quota updated from '" + company.getQuota() + "' to '" + instance.getQuota() + "'", "Company '"
											+ instance.getName() + "' Quota updated", 1, user.getEmail());
						}
					}

				}
			}
			return instance.merge();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of saveOrUpdate(Company

	@RemoteMethod
	public String remove(int id) {
		try {
			Company.findCompany(id).remove();
			return "Removed Company " + id;
		} catch (Exception e) {
			// System.out.println(" = "+e.getMessage());
			// e.printStackTrace();
			log.error(e.getMessage());
		}
		return "Error removing Company " + id + ". Look into logs.";
	}// end of method remove(int id

	@RemoteMethod
	public Company findById(int id) {
		try {
			return Company.findCompany(id);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<Company> findAll() {
		try {
			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return Company.findAllCompanys();

			} else {
				List<Company> comapnies = new ArrayList<Company>();
				comapnies.add(Company.findCompany(Commons.getCurrentSession().getCompanyId()));
				return comapnies;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<String> findAllDistinctCurrency() {
		try {
			return Company.findAllDistinctCurrency();

		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

}// end of class Company

