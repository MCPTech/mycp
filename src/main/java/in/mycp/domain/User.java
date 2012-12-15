package in.mycp.domain;

import in.mycp.utils.Commons;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "user", finders = { "findUsersByEmailEquals", "findUsersByEmailEqualsAndPasswordEqualsAndActiveNot", "findUsersByProject" })
public class User {

    public static TypedQuery<in.mycp.domain.User> findUsersByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.department.company = :company", User.class);
        q.setParameter("company", company);
        return q;
    }
    
    public static TypedQuery<in.mycp.domain.User> findManagersByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.department.company = :company and o.role = "+Commons.ROLE_MANAGER_INTVAL, User.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.User> findUsersByDepartment(Department department) {
        if (department == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<User> q = em.createQuery("SELECT o FROM User AS o WHERE o.department = :department", User.class);
        q.setParameter("department", department);
        return q;
    }

    public String toString() {
        return "";
    }
}
