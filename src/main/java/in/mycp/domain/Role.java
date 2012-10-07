package in.mycp.domain;

import in.mycp.utils.Commons;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "role", finders = { "findRolesByIntvalLessThan", "findRolesByNameEquals" })
public class Role {

    public static List<in.mycp.domain.Role> findAllRoles() {
        if (Commons.getCurrentUser().getRole().getName().equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
            return entityManager().createQuery("SELECT o FROM Role o", Role.class).getResultList();
        } else {
            return entityManager().createQuery("SELECT o FROM Role o where o.name != 'ROLE_SUPERADMIN' ", Role.class).getResultList();
        }
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
