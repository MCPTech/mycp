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

	
	public List<AccountLog> getTodaysLog(){
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(30);
			
			System.out.println(yesterday.toDate());
			//
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	
	public List<AccountLog> getCurrentWeeksLog(){
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(2);
			
			System.out.println(yesterday.toDate());
			//
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	
	public List<AccountLog> getCurrentMonthsLog(){
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(2);
			
			System.out.println(yesterday.toDate());
			//
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	
	public List<AccountLog> getLog4Month(DateTime d){
		
		if (Commons.getCurrentSession() != null) {
			DateTime yesterday = new DateTime().minusDays(2);
			
			System.out.println(yesterday.toDate());
			//
			List<AccountLog> accountLogs = AccountLog.findAccountLogsByUserIdAndTimeOfEntryGreaterThan(
					Commons.getCurrentUser(), yesterday.toDate()).getResultList();
			
			return accountLogs;
		}
		return null;

	}
	
	public void saveLog(String message,String task,Short status){
		try{
			
			AccountLog acctLog = new AccountLog();
			acctLog.setDetails(message);
			acctLog.setStatus(status);
			acctLog.setTask(task);
			acctLog.setTimeOfEntry(new Date());
			acctLog.setUserId(Commons.getCurrentUser());
			acctLog.merge();
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
	}//end saveLog
	
	
		
}// end of class AccountLogService

