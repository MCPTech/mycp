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

package in.mycp.web;

import org.directwebremoting.annotations.DataTransferObject;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

@DataTransferObject
public class DashboardDTO {

	int instanceCount = 0;
	int volCount = 0;
	int keyPairCount = 0;
	int snapshotCount = 0;
	int ipCount = 0;
	int secGroupCount = 0;
	int imageCount = 0;
	String cloudName = "";
	
	int accounts = 0;	
	
	int departments	 = 0;
	int projects	 = 0;
	int users	 = 0;
	int clouds	 = 0;
	int products = 0;
	
	long computeCost = 0;
	long volumeCost = 0;
	long ipaddressCost = 0;
	long secgroupCost = 0;
	long snapshotCost = 0;
	long imageCost = 0;
	long keyCost = 0;
	long totalCost = 0;
	String currency="";
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public long getComputeCost() {
		return computeCost;
	}
	public void setComputeCost(long computeCost) {
		this.computeCost = computeCost;
	}
	public long getVolumeCost() {
		return volumeCost;
	}
	public void setVolumeCost(long volumeCost) {
		this.volumeCost = volumeCost;
	}
	public long getIpaddressCost() {
		return ipaddressCost;
	}
	public void setIpaddressCost(long ipaddressCost) {
		this.ipaddressCost = ipaddressCost;
	}
	public long getSecgroupCost() {
		return secgroupCost;
	}
	public void setSecgroupCost(long secgroupCost) {
		this.secgroupCost = secgroupCost;
	}
	public long getSnapshotCost() {
		return snapshotCost;
	}
	public void setSnapshotCost(long snapshotCost) {
		this.snapshotCost = snapshotCost;
	}
	public long getImageCost() {
		return imageCost;
	}
	public void setImageCost(long imageCost) {
		this.imageCost = imageCost;
	}
	public long getKeyCost() {
		return keyCost;
	}
	public void setKeyCost(long keyCost) {
		this.keyCost = keyCost;
	}
	public long getTotalCost() {
		
		
		totalCost = computeCost+ volumeCost+ ipaddressCost+ secgroupCost+ snapshotCost+imageCost+ keyCost;
		
		return totalCost;
	}
	public void setTotalCost(long totalCost) {
		//his.totalCost = totalCost;
	}
	
	public int getAccounts() {
		return accounts;
	}
	public void setAccounts(int accounts) {
		this.accounts = accounts;
	}
	public int getDepartments() {
		return departments;
	}
	public void setDepartments(int departments) {
		this.departments = departments;
	}
	public int getProjects() {
		return projects;
	}
	public void setProjects(int projects) {
		this.projects = projects;
	}
	public int getUsers() {
		return users;
	}
	public void setUsers(int users) {
		this.users = users;
	}
	public int getClouds() {
		return clouds;
	}
	public void setClouds(int clouds) {
		this.clouds = clouds;
	}
	public int getProducts() {
		return products;
	}
	public void setProducts(int products) {
		this.products = products;
	}
	
	public String getCloudName() {
		return cloudName;
	}
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}
	public int getInstanceCount() {
		return instanceCount;
	}
	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}
	public int getVolCount() {
		return volCount;
	}
	public void setVolCount(int volCount) {
		this.volCount = volCount;
	}
	public int getKeyPairCount() {
		return keyPairCount;
	}
	public void setKeyPairCount(int keyPairCount) {
		this.keyPairCount = keyPairCount;
	}
	public int getSnapshotCount() {
		return snapshotCount;
	}
	public void setSnapshotCount(int snapshotCount) {
		this.snapshotCount = snapshotCount;
	}
	public int getIpCount() {
		return ipCount;
	}
	public void setIpCount(int ipCount) {
		this.ipCount = ipCount;
	}
	public int getSecGroupCount() {
		return secGroupCount;
	}
	public void setSecGroupCount(int secGroupCount) {
		this.secGroupCount = secGroupCount;
	}
	public int getImageCount() {
		return imageCount;
	}
	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}
	
}
