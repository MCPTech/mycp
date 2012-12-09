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

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/*import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;*/

@RemoteProxy(name = "SignupService")
public class SignupService {

    private static final Logger log = Logger.getLogger(SignupService.class.getName());

    @Autowired
    ShaPasswordEncoder passwordEncoder;

   /* @Autowired
    DefaultManageableImageCaptchaService imageCaptchaService;*/

    @Autowired
    private transient MailSender mailTemplate;

    
}
