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

import in.mycp.domain.AccountLog;
import in.mycp.domain.AccountLogTypeDTO;
import in.mycp.domain.Company;
import in.mycp.domain.User;
import in.mycp.service.WorkflowImpl4Jbpm;
import in.mycp.utils.Commons;
import in.mycp.web.MailDetailsDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.jbpm.api.ProcessInstance;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Assets - Asset details, AssetType, AssetUser, Start , end , rate , Cost,-
 * order by User
 * 
 * Company - Asset details, AssetType, AssetUser, Start , end , rate , Cost,
 * 
 * Department - Asset details, AssetType, AssetUser, Start , end , rate ,Cost,
 * 
 * Project - Asset details, AssetType, AssetUser, Start , end , rate , Cost,
 * 
 * User - Asset details, AssetType, Start , end , rate , Cost,
 * 
 * 
 * 
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 * 
 */

@RemoteProxy(name = "AccountLogService")
public class AccountLogService {

	private static final Logger log = Logger.getLogger(AccountLogService.class.getName());

	@Autowired
	private WorkflowImpl4Jbpm workflowImpl4Jbpm;

	@RemoteMethod
	public List<AccountLogTypeDTO> getAllAccountLogTypes() {
		return Commons.getAllAccountLogTypes();
	}
	
	@RemoteMethod
	public List<AccountLog> getLog(String logType) {
		System.out.println("logType = "+logType);
		return null;
	}

	public List<User> findUsers4Role(User curentUser) {
		if (curentUser.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER + "")) {
			Company c = curentUser.getDepartment().getCompany();
			return User.findUsersByCompany(c).getResultList();
		} else if (curentUser.getRole().getName().equals(Commons.ROLE.ROLE_USER + "")) {
			List<User> singleUser = new ArrayList<User>();
			singleUser.add(curentUser);
			return singleUser;
		} else if (curentUser.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			List<User> superAdminUsers = new ArrayList<User>();
			//add super admin too into this list
			superAdminUsers.add(curentUser);
			List<Company> companies = Company.findAllCompanys();
			for (Iterator iterator = companies.iterator(); iterator.hasNext();) {
				try {
					Company company2 = (Company) iterator.next();
					superAdminUsers.addAll(User.findUsersByCompany(company2).getResultList());
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
			return superAdminUsers;

		}

		return new ArrayList<User>();
	}

	public List<AccountLog> getLast24HoursLog() {
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(1);
			List<AccountLog> accountLogs = new ArrayList<AccountLog>();
			User curentUser = Commons.getCurrentUser();

			List<User> users = findUsers4Role(curentUser);

			for (User user : users) {

				try {
					List<AccountLog> als = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(user, yesterday.toDate()).getResultList();
					accountLogs.addAll(als);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}

			}
			return accountLogs;
		}
		return null;

	}

	public List<AccountLog> getLast7DaysLog() {
		if (Commons.getCurrentSession() != null) {
			DateTime day7th = new DateTime().minusDays(7);
			List<AccountLog> accountLogs = new ArrayList<AccountLog>();
			User curentUser = Commons.getCurrentUser();

			List<User> users = findUsers4Role(curentUser);
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				try {
					List<AccountLog> als = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(Commons.getCurrentUser(), day7th.toDate()).getResultList();
					accountLogs.addAll(als);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}

			}
			return accountLogs;
		}
		return null;

	}

	public List<AccountLog> getLog4Month(String monthName) {
		if (Commons.getCurrentSession() != null) {
			DateTime dt = Commons.getDateTimeFromMonthName(monthName);
			DateTime monthStart = new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.dayOfMonth().getMinimumValue(), 0, 0, 0, 0);
			DateTime monthEnd = new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.dayOfMonth().getMaximumValue(), 0, 0, 0, 0);

			List<AccountLog> accountLogs = new ArrayList<AccountLog>();
			User curentUser = Commons.getCurrentUser();

			List<User> users = findUsers4Role(curentUser);
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				try {
					List<AccountLog> als = AccountLog.findAccountLogsByUserIdAndTimeOfEntryBetween(Commons.getCurrentUser(), monthStart.toDate(), monthEnd.toDate())
							.getResultList();
					accountLogs.addAll(als);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}

			}
			return accountLogs;

		}
		return null;
	}// end getLog4Month

	/*
	 * COMPUTE, IPADDRESS, VOLUME, SECURITYGROUP, KEYPAIR,IMAGE, SNAPSHOT - mail
	 * notification has to be sent
	 */
	public void saveLogAndSendMail(String message, String task, int status, String emailId) {
		saveLog(message, task, status, emailId);
		try {
			MailDetailsDTO mailDetailsDTO = new MailDetailsDTO();
			mailDetailsDTO.setTemplateName("RegularMailTemplate");
			mailDetailsDTO.setTo(emailId);
			mailDetailsDTO.setSubject(task);
			mailDetailsDTO.setBodyText(message);
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("mailDetailsDTO", mailDetailsDTO);
			ProcessInstance instance = workflowImpl4Jbpm.startProcessInstanceByKey("Mail4Users", variables);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}// end saveLogAfterLogout

	@RemoteMethod
	public void saveLog(String message, String task, int status, String emailId) {
		try {
			if (StringUtils.isNotBlank(emailId)) {
				AccountLog acctLog = new AccountLog();
				if (message != null && message.length() > 243) {
					acctLog.setDetails(message.substring(0, 243));
				} else {
					acctLog.setDetails(message);
				}

				acctLog.setStatus((int) status);
				acctLog.setTask(task);
				acctLog.setTimeOfEntry(new Date());
				acctLog.setUserId(User.findUsersByEmailEquals(emailId).getSingleResult());
				acctLog.merge();
			}
		} catch (Exception e) {
			 e.printStackTrace();
			log.error(e.getMessage());
		}
	}// end saveLogAfterLogout

}// end of class AccountLogService

