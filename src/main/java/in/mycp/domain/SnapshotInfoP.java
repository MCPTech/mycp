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
@RooJpaActiveRecord(versionField = "", table = "snapshot_info_p", finders = { "findSnapshotInfoPsBySnapshotIdEquals", "findSnapshotInfoPsByAsset" })
public class SnapshotInfoP {

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

    public static List<in.mycp.domain.SnapshotInfoP> findAllSnapshotInfoPs(int start, int max, String search) {
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM SnapshotInfoP o where  o.asset.active = 1", SnapshotInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM SnapshotInfoP o " + " where   o.asset.active = 1 and " + " (o.snapshotId like :search or o.volumeId like :search)", SnapshotInfoP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        return q.getResultList();
    }

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsByUser(User user, int start, int max, String search) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user = :user and  o.asset.active = 1", SnapshotInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user = :user  and  o.asset.active = 1" + " and " + " (o.snapshotId like :search or o.volumeId like :search)", SnapshotInfoP.class);
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

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsByCompany(Company company, int start, int max, String search) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1", SnapshotInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user.department.company = :company  and  o.asset.active = 1" + " and " + " ( o.snapshotId like :search or o.volumeId like :search)", SnapshotInfoP.class);
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

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1", SnapshotInfoP.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsBy(Infra infra, Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1 " + " and o.asset.productCatalog.infra = :infra", SnapshotInfoP.class);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsByInfra(Infra infra) {
        if (infra == null) throw new IllegalArgumentException("The infra argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = null;
        q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset.productCatalog.infra = :infra and  o.asset.active = 1", SnapshotInfoP.class);
        q.setParameter("infra", infra);
        return q;
    }

    public static Number findSnapshotInfoCountByCompany(Company company, String status) {
        String queryStr = "SELECT COUNT(i.id) FROM SnapshotInfoP i where i.status = :status and  i.asset.active = 1 ";
        if (company != null) {
            queryStr = queryStr + "  and i.asset.user.department.company = :company ";
        }
        Query q = entityManager().createQuery(queryStr);
        q.setParameter("status", status);
        if (company != null) {
            q.setParameter("company", company);
        }
        return (Number) q.getSingleResult();
    }

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsBySnapshotIdEqualsAndCompanyEquals(String snapshotId, Company company) {
        if (snapshotId == null || snapshotId.length() == 0) throw new IllegalArgumentException("The snapshotId argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.snapshotId = :snapshotId and  o.asset.active = 1" + " and o.asset.user.department.company = :company", SnapshotInfoP.class);
        q.setParameter("snapshotId", snapshotId);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.SnapshotInfoP> findSnapshotInfoPsBy(Infra infra, String snapshotId, Company company) {
        if (snapshotId == null || snapshotId.length() == 0) throw new IllegalArgumentException("The snapshotId argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.snapshotId = :snapshotId and  o.asset.active = 1" + " and o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", SnapshotInfoP.class);
        q.setParameter("snapshotId", snapshotId);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    public static TypedQuery<SnapshotInfoP> findSnapshotInfoPsByAsset(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("The asset argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.asset = :asset and  o.asset.active = 1", SnapshotInfoP.class);
        q.setParameter("asset", asset);
        return q;
    }
    
    public static TypedQuery<SnapshotInfoP> findSnapshotInfoPsBySnapshotIdEquals(String snapshotId) {
        if (snapshotId == null || snapshotId.length() == 0) throw new IllegalArgumentException("The snapshotId argument is required");
        EntityManager em = entityManager();
        TypedQuery<SnapshotInfoP> q = em.createQuery("SELECT o FROM SnapshotInfoP AS o WHERE o.snapshotId = :snapshotId and  o.asset.active = 1", SnapshotInfoP.class);
        q.setParameter("snapshotId", snapshotId);
        return q;
    }
}
