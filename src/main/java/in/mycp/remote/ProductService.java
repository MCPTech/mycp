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

import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.domain.ProductCatalog;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */


@RemoteProxy(name = "ProductService")
public class ProductService {

	private static final Logger log = Logger.getLogger(ProductService.class.getName());

	@Autowired
	AccountLogService accountLogService;
	
	@RemoteMethod
	public ProductCatalog saveOrUpdate(ProductCatalog instance) {
		try {
			// MeterMetric mm =
			// MeterMetric.findMeterMetric(instance.getMeterMetric().getId());
			// instance.setMeterMetric(mm);
			instance.setInfra(Infra.findInfra(instance.getInfra().getId()));

			accountLogService.saveLog("Product created " + instance.getName()+", ",
					Commons.task_name.PRODUCT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			return instance.merge();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			accountLogService.saveLog("Error in Product creation " + instance.getName()+", ",
					Commons.task_name.PRODUCT.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
			
		}
		return null;
	}// end of saveOrUpdate(

	@RemoteMethod
	public String remove(int id) {
		try {
			ProductCatalog p = ProductCatalog.findProductCatalog(id);
					p.remove();
			accountLogService.saveLog("Product removed " + p.getName()+", ",
					Commons.task_name.DEPARTMENT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			return "Removed Product "+id;
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			accountLogService.saveLog("Error in Product creation " + ProductCatalog.findProductCatalog(id).getName()+", ",
					Commons.task_name.PRODUCT.name(),
					Commons.task_status.FAIL.ordinal(),
					Commons.getCurrentUser().getEmail());
			return "Error while removing Product "+id+". Check logs.";
		}
	}// end of method remove(int id

	@RemoteMethod
	public ProductCatalog findById(int id) {
		try {
			return ProductCatalog.findProductCatalog(id);
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List<ProductCatalog> findAll() {
		try {
			
			if(Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN+"")){
				return ProductCatalog.findAllProductCatalogs();
			}else{
				return ProductCatalog.findProductCatalogsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	@RemoteMethod
	public List<String> findAllProductTypesAsString() {
		try {
			List<String> productTypes = new ArrayList<String>();
			for (Commons.ProductType d : Commons.ProductType.values()) {
				// System.out.println(" = "+d.getName()+"  "+d);
				productTypes.add(d.getName());
			}
			return productTypes;
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}// end of method findAll

	public List<ProductCatalog> findByType(String type) {
		try {
			return ProductCatalog.findProductCatalogsByProductTypeEquals(type).getResultList();
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
		}
		return null;
	}

}// end of class ProductCatalog

