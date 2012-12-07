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

import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
/**
 * use this as a template for creating further workers
 * like ComputeCreateWorker, StorageCreateWorker etc
 * 
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
 
@Component("asyncWorker")
public class AsyncWorker  {

	protected static Logger logger = Logger.getLogger(AsyncWorker.class);
	
	@Async
	public void work() {
		String threadName = Thread.currentThread().getName();
		try {
	         logger.info(threadName+" Started.");
	         //System.out.println(threadName+" Started.");
	         
	         while(1==1){
	         Thread.sleep(10);
	         logger.info(threadName+" doing-----.");
	        // System.out.println(threadName+" doing-----.");
	         
	         }
	         
	         
	        }catch (InterruptedException e) {
	        	logger.info(threadName+" Interrupted.");
	        	//System.out.println(threadName+" Interrupted.");
	            Thread.currentThread().interrupt();
	        }
		
	}//work
	
}
