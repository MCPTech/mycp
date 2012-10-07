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


import in.mycp.domain.AssetType;

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

@RemoteProxy(name="AssetTypeService")
public class AssetTypeService  {

	private static final Logger log = Logger.getLogger(AssetTypeService.class
			.getName());

	
	@RemoteMethod
	public void save(AssetType instance) {
		try {
			instance.persist();
			} catch (Exception e) {
			//e.printStackTrace();
				log.error(e.getMessage());
		}
	}// end of save(Infra

	@RemoteMethod
	public AssetType saveOrUpdate(AssetType instance) {
		try {
			return instance.merge();
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of saveOrUpdate(Infra

	@RemoteMethod
	public String remove(int id) {
		try {
			AssetType.findAssetType(id).remove();
			return "Removed AssetType "+id;
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return "Cannot Remove AssetType "+id+". look into logs.";
	}// end of method remove(int id

	@RemoteMethod
	public AssetType findById(int id) {
		try {
			return AssetType.findAssetType(id);
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findById(int id

	@RemoteMethod
	public List findAll() {
		try {
			return AssetType.findAllAssetTypes();
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}// end of method findAll

	
}// end of class AssetType

