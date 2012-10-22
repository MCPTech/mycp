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

import in.mycp.utils.Commons;
import in.mycp.web.MycpSession;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RemoteProxy(name = "CommonService")
public class CommonService {

	private static final Logger log = Logger.getLogger(AddressInfoPService.class.getName());

	@RemoteMethod
	public MycpSession getCurrentSession() {
		try {
			HttpSession session = WebContextFactory.get().getSession();
			Object obj = session.getAttribute("CurrentSession");
			if (obj != null) {
				return (MycpSession) obj;
			} else {
				MycpSession mycpsession = Commons.getCurrentSession();
				session.setAttribute("CurrentSession", mycpsession);
				return mycpsession;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			CommonService.setSessionMsg(e.getMessage());
		}
		return null;
	}// end of getCurrentUser
	
	@RemoteMethod
	public static void setSessionMsg(String msg) {
		Commons.setSessionMsg(msg);
	}
	
	@RemoteMethod
	public static String getSessionMsg() {
		return Commons.getSessionMsg();
	}
	
}
