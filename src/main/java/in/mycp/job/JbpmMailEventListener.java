/**
 * 
 */
package in.mycp.job;

import in.mycp.remote.AccountLogService;
import in.mycp.service.ApplicationContextService4Jobs;
import in.mycp.utils.Commons;
import in.mycp.web.MailDetailsDTO;

import java.util.Collection;

import javax.mail.Message;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.pvm.internal.email.impl.MailProducerImpl;
import org.jbpm.pvm.internal.email.impl.MailTemplate;
import org.jbpm.pvm.internal.email.impl.MailTemplateRegistry;
import org.jbpm.pvm.internal.email.spi.MailSession;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author GangadharJN
 * @author jangamgangadhar@gmail.com
 *
 */
@Component("JbpmMailEventListener")
public class JbpmMailEventListener implements EventListener {

	String mailTemplate;
	  public void notify(EventListenerExecution execution) {
		  System.out.println("JbpmMailEventListener Notified..");
		  try{
			  ApplicationContext appContext = ApplicationContextService4Jobs.getAppContext();
			  AccountLogService accountLogService = (AccountLogService)appContext.getBean("accountLogService");
			  MailDetailsDTO mailDetailsDTO = (MailDetailsDTO) execution.getVariable("mailDetailsDTO");
			  if(mailDetailsDTO != null){
				  if(StringUtils.isBlank(mailTemplate))	mailTemplate=mailDetailsDTO.getTemplateName();
					try {
						MailTemplateRegistry mailTemplateRegistry = EnvironmentImpl.getFromCurrent(MailTemplateRegistry.class);
						MailSession mailSession = EnvironmentImpl.getFromCurrent(MailSession.class);
						MailTemplate template = mailTemplateRegistry.getTemplate(mailTemplate);
						MailProducerImpl mailProducer = new MailProducerImpl();
						mailProducer.setTemplate(template);
						Collection<Message> emails = mailProducer.produce(execution);
						mailSession.send(emails);
					} catch (Exception e) {
						System.err.println("Couldn't send Mail : "+e.getMessage());
						accountLogService.saveLog(e.getMessage(), "Mail Send Job", Commons.task_status.FAIL.ordinal(), "gangu96@yahoo.co.in");
					}
					System.out.println("JbpmMailEventListener Ended..!");
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }

}
