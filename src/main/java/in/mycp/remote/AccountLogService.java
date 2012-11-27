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

package in.mycp.remote;

import in.mycp.domain.AccountLog;
import in.mycp.domain.AccountLogTypeDTO;
import in.mycp.domain.User;
import in.mycp.utils.Commons;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.joda.time.DateTime;

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
	
	@RemoteMethod
	public List<AccountLogTypeDTO> getAllAccountLogTypes(){
		return Commons.getAllAccountLogTypes();
	}

	
	public List<AccountLog> getLast24HoursLog(){
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(1);
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	
	public List<AccountLog> getLast7DaysLog(){
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(7);
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	

	
	public List<AccountLog> getLog4Month(String monthName){
		if (Commons.getCurrentSession() != null) {
			DateTime dt = Commons.getDateTimeFromMonthName(monthName);
			DateTime monthStart = new DateTime(dt.getYear(), dt.getMonthOfYear(), 
					dt.dayOfMonth().getMinimumValue(), 0, 0, 0, 0);
			DateTime monthEnd = new DateTime(dt.getYear(), dt.getMonthOfYear(), 
					dt.dayOfMonth().getMaximumValue(), 0, 0, 0, 0);
			
			return AccountLog.findAccountLogsByUserIdAndTimeOfEntryBetween(
					Commons.getCurrentUser(), monthStart.toDate(), monthEnd.toDate()).getResultList();
		}
		return null;
	}//end getLog4Month
		
	/*
	 * COMPUTE, IPADDRESS, VOLUME, SECURITYGROUP, KEYPAIR,IMAGE, SNAPSHOT - mail notification has to be sent
	 * TODO mail send logic Gangadhar
	 */
	public void saveLogAndSendMail(String message,String task,int status,String emailId){
		saveLog(message, task, status, emailId);
	}//end saveLogAfterLogout
	
	public void saveLog(String message,String task,int status,String emailId){
		try{
			AccountLog acctLog = new AccountLog();
			acctLog.setDetails(message);
			acctLog.setStatus((int)status);
			acctLog.setTask(task);
			acctLog.setTimeOfEntry(new Date());
			acctLog.setUserId(User.findUsersByEmailEquals(emailId).getSingleResult());
			acctLog.merge();
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}//end saveLogAfterLogout
		
}// end of class AccountLogService

