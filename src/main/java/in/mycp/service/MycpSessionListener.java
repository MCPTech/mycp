/**
 * 
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
