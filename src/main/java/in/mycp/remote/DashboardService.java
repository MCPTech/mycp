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

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.ImageDescriptionP;
import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.domain.KeyPairInfoP;
import in.mycp.domain.ProductCatalog;
import in.mycp.domain.Project;
import in.mycp.domain.SnapshotInfoP;
import in.mycp.domain.User;
import in.mycp.domain.VolumeInfoP;
import in.mycp.utils.Commons;
import in.mycp.web.DashboardDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@RemoteProxy(name = "DashboardService")
public class DashboardService {

	private static final Logger log = Logger.getLogger(DashboardService.class.getName());
	
	@RemoteMethod
	public DashboardDTO getAllAssetCount() {
		try {
			DashboardDTO dto = new DashboardDTO();

			//TODO - work this out later on how to get the infra
			List<Infra> infras = Infra.findAllInfras();
			for (Iterator iterator = infras.iterator(); iterator.hasNext();) {
				Infra infra = (Infra) iterator.next();
				dto.setCloudName(infra.getName());
				break;
			}
			
			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				dto.setInstanceCount(InstanceP.findInstanceCountByCompany(null, Commons.REQUEST_STATUS.running + "").intValue());
				dto.setVolCount(VolumeInfoP.findVolumeInfoCountByCompany(null, Commons.VOLUME_STATUS_AVAILABLE).intValue());
				dto.setKeyPairCount(KeyPairInfoP.findKeyPairInfoCountByCompany(null, Commons.keypair_STATUS.active+ "").intValue());
				dto.setSnapshotCount(SnapshotInfoP.findSnapshotInfoCountByCompany(null, Commons.SNAPSHOT_STATUS.completed + "").intValue());
				dto.setIpCount(AddressInfoP.findAddressInfoCountByCompany(null).intValue());
				dto.setSecGroupCount(GroupDescriptionP.findGroupDescriptionCountByCompany(null, Commons.secgroup_STATUS.active+ "")
						.intValue());
				dto.setImageCount(ImageDescriptionP.findImageDescriptionCountByCompany(null, Commons.image_STATUS.active + "")
						.intValue());
				
				//TODO - improve this code for perf
				dto.setAccounts(Company.findAllCompanys().size());
				dto.setDepartments(Department.findAllDepartments().size());
				dto.setProjects(Project.findAllProjects().size());
				dto.setUsers(User.findAllUsers().size());
				dto.setClouds(Infra.findAllInfras().size());
				dto.setProducts(ProductCatalog.findAllProductCatalogs().size());
				
			} else {
				Company c = Company.findCompany(Commons.getCurrentSession().getCompanyId());
				dto.setInstanceCount(InstanceP.findInstanceCountByCompany(c,
						Commons.REQUEST_STATUS.running + "").intValue());
				dto.setVolCount(VolumeInfoP.findVolumeInfoCountByCompany(c,
						Commons.VOLUME_STATUS_AVAILABLE).intValue());
				dto.setKeyPairCount(KeyPairInfoP.findKeyPairInfoCountByCompany(
						c, Commons.keypair_STATUS.active + "").intValue());
				dto.setSnapshotCount(SnapshotInfoP.findSnapshotInfoCountByCompany(
						c, Commons.SNAPSHOT_STATUS.completed + "").intValue());
				dto.setIpCount(AddressInfoP.findAddressInfoCountByCompany(c).intValue());
				dto.setSecGroupCount(GroupDescriptionP.findGroupDescriptionCountByCompany(
						c, Commons.secgroup_STATUS.active + "").intValue());
				dto.setImageCount(ImageDescriptionP.findImageDescriptionCountByCompany(
						c, Commons.image_STATUS.active+ "").intValue());

				//TODO - improve this code for perf
				dto.setAccounts(1);
				dto.setDepartments(Department.findDepartmentsByCompany(c).getResultList().size());
				dto.setProjects(Project.findProjectsByCompany(c).getResultList().size());
				dto.setUsers(User.findUsersByCompany(c).getResultList().size());
				dto.setClouds(Infra.findInfrasByCompany(c).getResultList().size());
				dto.setProducts(ProductCatalog.findProductCatalogsByCompany(c).getResultList().size());
				
			}
			return dto;

		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method

	@RemoteMethod
	public List<Company> findAll() {
		try {
			if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
				return Company.findAllCompanys();
			} else {
				List<Company> comapnies = new ArrayList<Company>();
				comapnies.add(Company.findCompany(Commons.getCurrentSession().getCompanyId()));
				return comapnies;
			}

		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

}// end of class Company

