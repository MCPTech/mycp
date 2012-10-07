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

package in.mycp.service;

import in.mycp.domain.Role;
import in.mycp.remote.InfraService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */

public class MycpAuthService extends AbstractUserDetailsAuthenticationProvider {
	private static final Logger log = Logger.getLogger(AbstractUserDetailsAuthenticationProvider.class
			.getName());

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
			//System.out.println(" = additionalAuthenticationChecks ");
	}


	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		String password = (String) authentication.getCredentials();
		if (StringUtils.isBlank(password)) {
			throw new BadCredentialsException("Please enter password");
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		in.mycp.domain.User mycpUser =null;
		try {
			ShaPasswordEncoder passEncoder = new ShaPasswordEncoder(256);
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
			}
			
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());//e.printStackTrace();
			throw new BadCredentialsException("Invalid username or password");
		} catch (EntityNotFoundException e) {
			log.error(e.getMessage());//e.printStackTrace();
			throw new BadCredentialsException("Invalid user");
		} catch (NonUniqueResultException e) {
			throw new BadCredentialsException("Non-unique user, contact administrator");
		}catch(Exception e){
			throw new BadCredentialsException("Invalid username or password");
		}

		return new User(mycpUser.getEmail(), mycpUser.getPassword(), mycpUser.getActive(), // enabled
				true, // account not expired
				true, // credentials not expired
				true, // account not locked
				authorities);
	}
}
