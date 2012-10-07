package in.mycp.domain;

import javax.persistence.EntityManager;
import javax.persistence.Transient;
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
@RooJpaActiveRecord(versionField = "", table = "infra", finders = { "findInfrasByNameEquals", "findInfrasByRegion", "findInfrasByServerEquals" })
public class Infra {

    @Transient
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static TypedQuery<in.mycp.domain.Infra> findInfrasByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The Company argument is required");
        EntityManager em = entityManager();
        TypedQuery<Infra> q = em.createQuery("SELECT o FROM Infra AS o WHERE o.company = :company", Infra.class);
        q.setParameter("company", company);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
