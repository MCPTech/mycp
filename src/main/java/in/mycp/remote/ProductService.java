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

package in.mycp.remote;

import in.mycp.domain.Company;
import in.mycp.domain.Infra;
import in.mycp.domain.ProductCatalog;
import in.mycp.utils.Commons;

import java.util.ArrayList;
import java.util.Iterator;
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
			String s = "Product created ";
			if(instance.getId()>0){
				s = "Product updated ";
			}
			
			//check if this is runniing in SP edition , then we allow only one product per type per infra.
			if(Commons.EDITION_ENABLED == Commons.SERVICE_PROVIDER_EDITION_ENABLED){
				List<ProductCatalog> existingPcs =ProductCatalog.findProductCatalogsByInfra(instance.getInfra()).getResultList();
				for (Iterator iterator = existingPcs.iterator(); iterator.hasNext(); ) {
					ProductCatalog productCatalog = (ProductCatalog) iterator.next();
					if(productCatalog.getProductType()!=null && 
							productCatalog.getProductType().equals(instance.getProductType())){
						throw new Exception(" MyCP: While running Service Provider Edition, you are not allowed to create 2 products of the same type on same infra/cloud");
					}
				}
			}
			
			
			
			// MeterMetric mm =
			// MeterMetric.findMeterMetric(instance.getMeterMetric().getId());
			// instance.setMeterMetric(mm);
			instance.setInfra(Infra.findInfra(instance.getInfra().getId()));
			instance = instance.merge();
			
			accountLogService.saveLog( s+ instance.getName()+", ",
					Commons.task_name.PRODUCT.name(),
					Commons.task_status.SUCCESS.ordinal(),
					Commons.getCurrentUser().getEmail());
			
			return instance;
		} catch (Exception e) {
			log.error(e.getMessage());//e.printStackTrace();
			accountLogService.saveLog("Error in Product creation/updating " + instance.getName()+", ",
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
	public List<ProductCatalog> findAll4Dashboard() {
		try {
			List<Infra> infras = null;
			if (Commons.EDITION_ENABLED== Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				System.out.println("ProductCatalog.findAllProductCatalogs().size() = "+ProductCatalog.findAllProductCatalogs().size());
				return ProductCatalog.findAllProductCatalogs();
			} else {
				return ProductCatalog.findProductCatalogsByCompany(Company.findCompany(Commons.getCurrentSession().getCompanyId())).getResultList();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll
	
	@RemoteMethod
	public List<ProductCatalog> findAll() {
		try {
			
			if (Commons.EDITION_ENABLED== Commons.SERVICE_PROVIDER_EDITION_ENABLED || Commons.getCurrentUser().getRole().getName()
					.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
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

