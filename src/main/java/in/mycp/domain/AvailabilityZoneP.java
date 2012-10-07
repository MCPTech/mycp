package in.mycp.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "availability_zone_p", finders = { "findAvailabilityZonePsByNameEquals" })
public class AvailabilityZoneP {

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static List<in.mycp.domain.AvailabilityZoneP> findAllAvailabilityZonePsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The Company argument is required");
        EntityManager em = entityManager();
        TypedQuery<in.mycp.domain.AvailabilityZoneP> q = em.createQuery("SELECT o FROM AvailabilityZoneP o" + " where o.infraId.company = :company", in.mycp.domain.AvailabilityZoneP.class);
        q.setParameter("company", company);
        return q.getResultList();
    }

    public static List<in.mycp.domain.AvailabilityZoneP> findAllAvailabilityZonePs() {
        return entityManager().createQuery("SELECT o FROM AvailabilityZoneP o", AvailabilityZoneP.class).getResultList();
    }
}
