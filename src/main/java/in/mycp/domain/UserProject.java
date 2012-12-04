package in.mycp.domain;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = UserProjectPK.class, versionField = "", table = "user_project")
@RooDbManaged(automaticallyDelete = true)
public class UserProject {
}
