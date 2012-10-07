package in.mycp.domain;

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
@RooJpaActiveRecord(versionField = "", table = "project", finders = { "findProjectsByDepartment" })
public class Project {

    public static TypedQuery<in.mycp.domain.Project> findProjectsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The department argument is required");
        EntityManager em = entityManager();
        TypedQuery<Project> q = em.createQuery("SELECT o FROM Project AS o WHERE o.department.company = :company", Project.class);
        q.setParameter("company", company);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
