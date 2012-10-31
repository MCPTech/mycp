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

package in.mycp.controller;

import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Infra;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.Role;
import in.mycp.domain.User;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RequestMapping("/cloud-portal")
@Controller
public class LoginController {
	protected static Logger logger = Logger.getLogger(LoginController.class);


	 @Autowired
	 	ShaPasswordEncoder passwordEncoder;
	 
	 @Autowired
	 	private transient MailSender mailTemplate;
	 
	@RequestMapping(produces = "text/html")
	public String main(HttpServletRequest req, HttpServletResponse resp) {
		logger.info("login "+req.getQueryString()+" {}{}{} "+req.getRequestURL()+" {}{}{} "+req.getRemoteAddr());
		//HttpSession s = req.getSession(true);
		return "mycplogin";
	}

	@RequestMapping(value="/signup", produces = "text/html")
	public String signup(HttpServletRequest req, HttpServletResponse resp) {
		try {
				String name = req.getParameter("name");
				String email = req.getParameter("email");
				String password = req.getParameter("password");
				String organization = req.getParameter("organization");
				String captchaResp = req.getParameter("captchaResp");
			
            boolean captchaPassed = false;
            try {
                HttpSession session = req.getSession();
                String check = (String) session.getAttribute("captcha");
                if (captchaResp.equalsIgnoreCase(check)) {
                    captchaPassed = true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (captchaPassed) {
            	//continue
            } else {
                logger.error("captcha failed ");
                req.getSession().setAttribute("MYCP_SIGNUP_MSG", "<font style=\"color: red;\"> Cannot create User.Captcha failed.</font>");
                return "mycplogin";
            }
            if (StringUtils.isBlank(password)) {
                throw new Exception("Password cannot be empty");
            }
            if (StringUtils.isBlank(email)) {
                throw new Exception("Email cannot be empty");
            }
            boolean exists = false;
            try {
                User u = User.findUsersByEmailEquals(email).getSingleResult();
                if (u != null && u.getId() > 0) {
                    exists = true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            
            if (exists) {
            	 req.getSession().setAttribute("MYCP_SIGNUP_MSG", "<font style=\"color: red; \"> User " + email + " exists. Choose a different email Id please.</font>");
            	 return "mycplogin";
            }
            
            User user = new User();
	            user.setFirstName(name);
	            user.setEmail(email);
	            user.setActive(true);
	            user.setRole(Role.findRolesByNameEquals(Commons.ROLE.ROLE_MANAGER + "").getSingleResult());
	            user.setRegistereddate(new Date());
	            user.setPassword(passwordEncoder.encodePassword(password, email));
            Company c = new Company();
	            c.setName(organization);
	            c.setCurrency("INR");
	            c = c.merge();
            Department d = new Department();
	            d.setCompany(c);
	            d.setName("Dept - " + c.getName());
	            d = d.merge();
            Project p = new Project();
	            p.setDepartment(d);
	            p.setName("Proj @ " + d.getName());
	            p = p.merge();
            user.setProject(p);
            user = user.merge();
            Infra infra = new Infra();
	            infra.setName(c.getName() + " Euca Setup");
	            infra.setAccessId("change it");
	            infra.setSecretKey("change it");
	            infra.setServer("change it");
	            infra.setCompany(c);
	            infra.setDetails("");
	            infra.setPort(8773);
	            infra.setResourcePrefix("/services/Eucalyptus");
	            infra.setSignatureVersion(1);
	            infra.setZone("");
	            infra = infra.merge();
            createAllProducts(infra);
            try {
            	final String username = user.getFirstName();
            	final String useremail = user.getEmail();
            	new Thread(new Runnable() {
            		  public void run() {
                        	/*sendMessage("mycloudportal@gmail.com", "Activation of MyCP test drive",useremail , "\n\nDear "+username+", \n" +
                        			" Thank you for your interest in My Cloud Portal, the open source self service portal for the cloud.\n" +
                        			" You can now configure your Eucalyptus private cloud access details at http://www.mycloudportal.in/config/infra \n" +
                        			" and start the sync process to import your data from eucalyptus cloud into my cloud portal." +
                        			" After you configure your account, you can\n" +
                        			" \t 1. Go to Setup, edit the currency, create your users, projects and departments.\n" +
                        			" \t 2. Go to Resources, start requesting and consuming infrastructure services.\n" +
                        			" \t 3. Usage reports menu will give you the metering data of your comsumption.\n" +
                        			"\n MyCP is open source and free to download and use as a standalone installation too. Its a simple standards compliant \n" +
                        			" java web application backed by MySQL.\n" +
                        			" If you are a developer, please think about participating in the project at http://code.google.com/p/mycloudportal\n" +
                        			"\n\nRegards\n" +
                        			"MyCP Team\n" +
                        			"www.mycloudportal.in\n");*/
            		  }
            		}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
            req.getSession().setAttribute("MYCP_SIGNUP_MSG", "<font style=\"color: green;\"> User " + user.getEmail() + " created.Please Sign In now.</font>");
            if(authenticate(email,password)){
            	if(user.getRole().getName().equals(Commons.ROLE.ROLE_USER+"")){
    				return "cloud-portal/userdash";
    			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER+"")){
    				return "cloud-portal/managerdash";
    			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_ADMIN+"")){
    				return "cloud-portal/admindash";
    			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
    				return "cloud-portal/superadmindash";
    			}
            	
            		
            }
        } catch (Exception e) {
        	e.printStackTrace();
            req.getSession().setAttribute("MYCP_SIGNUP_MSG", "<font style=\"color: red;\"> Cannot create User.Please try later.</font>");
            logger.error(e);
        }
		 return "mycplogin";
	}

   @Autowired
   AuthenticationManager authenticationManager;
	
	 public boolean authenticate(String username, String password) {
	        	/*List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    		in.mycp.domain.User mycpUser =null;*/
	    		try {
	    			/*ShaPasswordEncoder passEncoder = new ShaPasswordEncoder(256);
	    			String encodedPass = passEncoder.encodePassword(password, username);
	    			mycpUser = in.mycp.domain.User
	    					.findUsersByEmailEqualsAndPasswordEqualsAndActiveNot(username, encodedPass, false).getSingleResult();
	    			mycpUser.setLoggedInDate(new Date());
	    			mycpUser = mycpUser.merge();
	    			List<Role> roles = Role.findRolesByIntvalLessThan(mycpUser.getRole().getIntval()+1).getResultList();
	    			//everybody gets role_user
	    			//authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
	    			for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
	    				Role role = (Role) iterator.next();
	    				authorities.add(new GrantedAuthorityImpl(role.getName()));
	    			}*/
	    			
	    			UsernamePasswordAuthenticationToken usernameAndPassword = 
	    	                new UsernamePasswordAuthenticationToken(
	    	                		username, password);
	    			
	    			Authentication auth = authenticationManager.authenticate(usernameAndPassword);
		            SecurityContextHolder.getContext().setAuthentication(auth);
	    			return true;
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		} 
	    		return false;
	    }
	
    public void createAllProducts(Infra i) {
        ProductCatalog pc = new ProductCatalog();
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.ComputeInstance + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.ComputeInstance.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.IpAddress + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.IpAddress.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.KeyPair + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.KeyPair.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.SecurityGroup + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.SecurityGroup.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.Volume + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.Volume.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.VolumeSnapshot + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.VolumeSnapshot.getName());
	        pc.merge();
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.ComputeImage + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.ComputeImage.getName());
	        pc.merge();
    }
   
    public void sendMessage(String mailFrom, String subject, String mailTo, String message) {
        org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setSubject(subject);
        mailMessage.setTo(mailTo);
        mailMessage.setText(message);
        mailTemplate.send(mailMessage);
    }

	
	@RequestMapping(value="/dash", produces = "text/html")
	public String dashboard(HttpServletRequest req, HttpServletResponse resp) {
		logger.info("cloud-portal/dash");
		try {
			User user = Commons.getCurrentUser();
			if(user.getRole().getName().equals(Commons.ROLE.ROLE_USER+"")){
				return "cloud-portal/userdash";
			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_MANAGER+"")){
				return "cloud-portal/managerdash";
			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_ADMIN+"")){
				return "cloud-portal/admindash";
			}else if(user.getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
				return "cloud-portal/superadmindash";
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.info(e.getMessage());
		}
		
		//cant figure out the user role .
		return "mycplogin";
	}
	
	
	
}
