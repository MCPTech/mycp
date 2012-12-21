package in.mycp.persistence;

import in.mycp.domain.User;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.Infra;
import in.mycp.domain.InfraType;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.Role;

import in.mycp.remote.ProductService;
import in.mycp.utils.Commons;

import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ganesan
 * @DATE 12/07/2012
 * This is a service class to handle persisting of Domain objects transactionaly. 
 **/
@Component
@Transactional 
public class MycpPersistenceServiceImpl {
	/* (non-Javadoc)
	 * @see in.mycp.service.MycpSignupService#signup(javax.servlet.http.HttpServletRequest)
	 * 
	 */
	 @Autowired
	 	ShaPasswordEncoder passwordEncoder;
	 @Autowired
	 	ProductService productService;

	 @Transactional ( readOnly = false , propagation = Propagation.REQUIRES_NEW )	
	public User signup(HttpServletRequest req) throws Exception{
		// TODO Auto-generated method stub
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String organization = req.getParameter("organization");
		String captchaResp = req.getParameter("captchaResp");

        User user = new User();
        user.setFirstName(name);
        user.setEmail(email);
        user.setActive(true);
        user.setRole(Role.findRolesByNameEquals(Commons.ROLE.ROLE_MANAGER + "").getSingleResult());
        user.setRegistereddate(new Date());
        user.setPassword(passwordEncoder.encodePassword(password, email));
        
        Company c = new Company();
        c.setName(organization);
        c.setCurrency(Commons.CURRENCY_DEFAULT);
        c = c.merge();
        
        Department d = new Department();
        d.setCompany(c);
        d.setName("Dept - " + c.getName());
        d = d.merge();
        
        Project p = new Project();
        p.setDepartment(d);
        p.setName("Proj @ " + d.getName());
        p = p.merge();
        
        if(user.getProjects() == null){
        	user.setProjects(new HashSet<Project>());
        }
        user.getProjects().add(p);
        user.setDepartment(d);
        user = user.merge();
    
        if(p.getUsers() == null){
        	p.setUsers(new HashSet<User>());
        }
        p.getUsers().add(user);
        p.merge();
        if(Commons.EDITION_ENABLED==Commons.HOSTED_EDITION_ENABLED){
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
            infra.setInfraType(InfraType.findInfraType(Commons.INFRA_TYPE_EUCA));
            infra = infra.merge();
            createAllProducts(infra);
        }
        return user;	
	}

	@Transactional ( readOnly = false , propagation = Propagation.REQUIRED )
    public void createAllProducts(Infra i) {
        ProductCatalog pc = new ProductCatalog();
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.ComputeInstance + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.ComputeInstance.getName());
	        //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.IpAddress + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.IpAddress.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.KeyPair + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.KeyPair.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.SecurityGroup + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.SecurityGroup.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.Volume + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.Volume.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.VolumeSnapshot + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.VolumeSnapshot.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
        pc = new ProductCatalog();
	        pc.setId(0);
	        pc.setInfra(i);
	        pc.setCurrency(i.getCompany().getCurrency());
	        pc.setName(Commons.ProductType.ComputeImage + " @ " + i.getName());
	        pc.setPrice(10);
	        pc.setProductType(Commons.ProductType.ComputeImage.getName());
	      //pc.merge();
	        productService.saveOrUpdate(pc);
    }
}
