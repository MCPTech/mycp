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
@RooJpaActiveRecord(versionField = "", table = "volume_info_p", finders = { "findVolumeInfoPsByVolumeIdEquals", "findVolumeInfoPsByAsset" })
public class VolumeInfoP {

    @Transient
    private String details;

    @Transient
    public String product;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public static List<in.mycp.domain.VolumeInfoP> findAllVolumeInfoPs() {
        return entityManager().createQuery("SELECT o FROM VolumeInfoP o", VolumeInfoP.class).getResultList();
    }

    public static List<in.mycp.domain.VolumeInfoP> findAllVolumeInfoPs(int start, int max, String search) {
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM VolumeInfoP o", VolumeInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM VolumeInfoP o " + " where " + " (o.name like :search or o.volumeId like :search)", VolumeInfoP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        return q.getResultList();
    }

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsByUser(User user, int start, int max, String search) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user = :user", VolumeInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user = :user" + " and " + " (o.name like :search or o.volumeId like :search)", VolumeInfoP.class);
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

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsByCompany(Company company, int start, int max, String search) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user.department.company = :company", VolumeInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user.department.company = :company" + " and " + " (o.name like :search or o.volumeId like :search)", VolumeInfoP.class);
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

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user.department.company = :company", VolumeInfoP.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsBy(Infra infra, Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", VolumeInfoP.class);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsByInfra(Infra infra) {
        if (infra == null) throw new IllegalArgumentException("The infra argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = null;
        q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.asset.productCatalog.infra = :infra", VolumeInfoP.class);
        q.setParameter("infra", infra);
        return q;
    }

    public static Number findVolumeInfoCountByCompany(Company company, String status) {
        String queryStr = "SELECT COUNT(i.id) FROM VolumeInfoP i where i.status = :status ";
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

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsByVolumeIdEqualsAndCompanyEquals(String volumeId, Company company) {
        if (volumeId == null || volumeId.length() == 0) throw new IllegalArgumentException("The volumeId argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.volumeId = :volumeId " + " and o.asset.user.department.company = :company", VolumeInfoP.class);
        q.setParameter("volumeId", volumeId);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.VolumeInfoP> findVolumeInfoPsBy(Infra infra, String volumeId, Company company) {
        if (volumeId == null || volumeId.length() == 0) throw new IllegalArgumentException("The volumeId argument is required");
        EntityManager em = entityManager();
        TypedQuery<VolumeInfoP> q = em.createQuery("SELECT o FROM VolumeInfoP AS o WHERE o.volumeId = :volumeId " + " and o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra ", VolumeInfoP.class);
        q.setParameter("volumeId", volumeId);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
