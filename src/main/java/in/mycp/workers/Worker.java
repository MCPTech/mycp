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
package in.mycp.workers;

import in.mycp.domain.Asset;
import in.mycp.domain.Infra;
import in.mycp.utils.Commons;

import java.util.Date;
import java.util.logging.Level;

import org.jasypt.util.text.BasicTextEncryptor;

import com.vmware.vcloud.api.rest.schema.FirewallRuleProtocols;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.samples.FakeSSLSocketFactory;
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
	
	
	public VcloudClient getVcloudClient(Infra infra) {
		try {
			
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("gothilla");
		String decAccessId = textEncryptor.decrypt(infra.getAccessId());
		String decSecretKey = textEncryptor.decrypt(infra.getSecretKey());
			VcloudClient.setLogLevel(Level.SEVERE);
			VcloudClient vcloudClient = new VcloudClient("https://"+infra.getServer(), Version.V1_5);
			String login = decAccessId+"@"+infra.getVcloudAccountName();
			vcloudClient.registerScheme("https", infra.getPort().intValue(), FakeSSLSocketFactory.getInstance());
			vcloudClient.login(login, decSecretKey);
		return vcloudClient;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	} 
	
	
	public String getPrototcolAsString(FirewallRuleProtocols firewallRule){
		if(firewallRule.isAny()){
			return Commons.PROTOCOL_TYPE_ANY;
		}else if(firewallRule.isTcp() && !firewallRule.isIcmp() && !firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_TCP;
		}else if(!firewallRule.isTcp() && firewallRule.isIcmp() && !firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_ICMP;
		}else if(!firewallRule.isTcp() && !firewallRule.isIcmp() && firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_UDP;
		}else if(firewallRule.isTcp() && firewallRule.isIcmp() && !firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_TCP_ICMP;
		}else if(firewallRule.isTcp() && !firewallRule.isIcmp() && firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_TCP_UDP;
		}else if(!firewallRule.isTcp() && firewallRule.isIcmp() && firewallRule.isUdp()){
			return Commons.PROTOCOL_TYPE_UDP_ICMP;
		}else {
			return "";
		}
	}//getPrototcolAsString
	
  public FirewallRuleProtocols getFirewallRuleProtocols(String protocol){
	  FirewallRuleProtocols frp = new FirewallRuleProtocols();
	  
	  if(Commons.PROTOCOL_TYPE_ANY.equals(protocol)){
		  frp.setAny(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_TCP.equals(protocol)){
		  frp.setTcp(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_ICMP.equals(protocol)){
		  frp.setIcmp(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_UDP.equals(protocol)){
		  frp.setUdp(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_TCP_ICMP.equals(protocol)){
		  frp.setTcp(true);
		  frp.setIcmp(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_TCP_UDP.equals(protocol)){
		  frp.setTcp(true);
		  frp.setUdp(true);
		  return frp;
	  }else if(Commons.PROTOCOL_TYPE_UDP_ICMP.equals(protocol)){
		  frp.setUdp(true);
		  frp.setIcmp(true);
		  return frp;
	  }else {
		  return null;
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