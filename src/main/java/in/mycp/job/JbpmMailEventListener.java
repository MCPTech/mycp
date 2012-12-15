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

package in.mycp.job;

import in.mycp.remote.AccountLogService;
import in.mycp.service.ApplicationContextService4Jobs;
import in.mycp.utils.Commons;
import in.mycp.web.MailDetailsDTO;

import java.util.Collection;

import javax.mail.Message;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Logger logger = Logger.getLogger(JbpmMailEventListener.class);

	private String mailTemplate;

	public void notify(EventListenerExecution execution) {
		try {
			ApplicationContext appContext = ApplicationContextService4Jobs.getAppContext();
			AccountLogService accountLogService = (AccountLogService) appContext.getBean("accountLogService");
			MailDetailsDTO mailDetailsDTO = (MailDetailsDTO) execution.getVariable("mailDetailsDTO");
			if (mailDetailsDTO != null) {
				if (StringUtils.isBlank(mailTemplate))
					mailTemplate = mailDetailsDTO.getTemplateName();
				try {
					MailTemplateRegistry mailTemplateRegistry = EnvironmentImpl.getFromCurrent(MailTemplateRegistry.class);
					MailSession mailSession = EnvironmentImpl.getFromCurrent(MailSession.class);
					MailTemplate template = mailTemplateRegistry.getTemplate(mailTemplate);
					MailProducerImpl mailProducer = new MailProducerImpl();
					mailProducer.setTemplate(template);
					Collection<Message> emails = mailProducer.produce(execution);
					mailSession.send(emails);
				} catch (Exception e) {
					logger.error("Couldn't send Mail : " + e.getMessage());
					//accountLogService.saveLog(e.getMessage(), "Mail Send Job", Commons.task_status.FAIL.ordinal(), mailDetailsDTO.getTo());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mailTemplate = null;
		}
	}
}
