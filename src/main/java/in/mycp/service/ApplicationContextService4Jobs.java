package in.mycp.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author : GangadharJN
 * 
 */
public class ApplicationContextService4Jobs implements ApplicationContextAware {

	private static ApplicationContext appContext;

	public ApplicationContextService4Jobs() {
	}

	public void afterPropertiesSet() throws Exception {
		appContext = getAppContext();
	}

	public static ApplicationContext getAppContext() {
		return appContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		if (appContext == null) {
			appContext = applicationContext;
		}
	}
}
