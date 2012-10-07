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

package in.mycp.workers;

import in.mycp.domain.Asset;
import in.mycp.domain.Infra;

import java.util.Date;

import org.jasypt.util.text.BasicTextEncryptor;

import com.xerox.amazonws.ec2.Jec2;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

public class Worker {
  
	public Jec2 getNewJce2(Infra infra) {
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("gothilla");
			String decAccessId = textEncryptor.decrypt(infra.getAccessId());
			String decSecretKey = textEncryptor.decrypt(infra.getSecretKey());
			
			if(infra.getServer().startsWith("ec2.amazonaws.com")){
				Jec2 ec2 = new Jec2(decAccessId, decSecretKey);
				return ec2;
			}else {
				Jec2 ec2 = new Jec2(decAccessId, decSecretKey, false,
						infra.getServer(), infra.getPort());
				ec2.setResourcePrefix(infra.getResourcePrefix());
				ec2.setSignatureVersion(infra.getSignatureVersion());
				return ec2;		
			}
		
	}
  
	public void setAssetEndTime(Asset a){
		//Asset a = instanceLocal.getAsset();
		a.setEndTime(new Date());
		a.setActive(false);
		a.merge();
	}
	
	
	public void setAssetStartTime(Asset a){
		//Asset a = instanceLocal.getAsset();
		a.setStartTime(new Date());
		a.setActive(true);
		a.merge();
	}
	
	
}