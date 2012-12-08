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
