package in.mycp.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "instance_p", finders = { "findInstancePsByInstanceIdEquals", "findInstancePsByKeyNameNotEquals", "findInstancePsByReservationDescription", "findInstancePsByAsset" })
public class InstanceP {

    @Transient
    public String product;

    @Transient
    public int projectId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public static List<in.mycp.domain.InstanceP> findAllInstancePs() {
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = em.createQuery("SELECT o FROM InstanceP o", InstanceP.class);
        return q.getResultList();
    }

    public static List<in.mycp.domain.InstanceP> findAllInstancePs(int start, int max, String search) {
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM InstanceP o", InstanceP.class);
        } else {
            q = em.createQuery("SELECT o FROM InstanceP o " + " where " + " (o.name like :search or o.imageId like :search or o.instanceId like :search)", InstanceP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        return q.getResultList();
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsByUser(User user, int start, int max, String search) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user = :user", InstanceP.class);
        } else {
            q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user = :user " + " and " + " (o.name like :search or o.imageId like :search or o.instanceId like :search)", InstanceP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        q.setParameter("user", user);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsByCompany(Company company, int start, int max, String search) {
        if (company == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user.department.company = :company", InstanceP.class);
        } else {
            q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user.department.company = :company " + " and " + " (o.name like :search or o.imageId like :search or o.instanceId like :search)", InstanceP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user.department.company = :company", InstanceP.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsBy(Infra infra, Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", InstanceP.class);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsBy(Infra infra, Company company, User user) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra and o.asset.user =:user", InstanceP.class);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        q.setParameter("user", user);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsByInfra(Infra infra) {
        if (infra == null) throw new IllegalArgumentException("The infra argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = null;
        q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.asset.productCatalog.infra = :infra", InstanceP.class);
        q.setParameter("infra", infra);
        return q;
    }

    public static Number findInstanceCountByCompany(Company company, String status) {
        String queryStr = "SELECT COUNT(i.id) FROM InstanceP i where i.state = :status ";
        if (company != null) {
            queryStr = queryStr + "  and i.asset.user.department.company = :company";
        }
        Query q = entityManager().createQuery(queryStr);
        q.setParameter("status", status);
        if (company != null) {
            q.setParameter("company", company);
        }
        return (Number) q.getSingleResult();
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsByInstanceIdEqualsAndCompanyEquals(String instanceId, Company company) {
        if (instanceId == null || instanceId.length() == 0) throw new IllegalArgumentException("The instanceId argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.instanceId = :instanceId " + " " + " and o.asset.user.department.company = :company", InstanceP.class);
        q.setParameter("instanceId", instanceId);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.InstanceP> findInstancePsBy(Infra infra, String instanceId, Company company) {
        if (instanceId == null || instanceId.length() == 0) throw new IllegalArgumentException("The instanceId argument is required");
        EntityManager em = entityManager();
        TypedQuery<InstanceP> q = em.createQuery("SELECT o FROM InstanceP AS o WHERE o.instanceId = :instanceId " + " and o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", InstanceP.class);
        q.setParameter("instanceId", instanceId);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
