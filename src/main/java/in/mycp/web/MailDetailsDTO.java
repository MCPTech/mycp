/**
 * 
 */
package in.mycp.web;

import java.io.Serializable;

/**
 * @author GangadharJN
 * 
 */
public class MailDetailsDTO implements Serializable{

	private String toName;
	private String to;
	private String subject;
	private String bodyText;
	private String templateName;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

}
