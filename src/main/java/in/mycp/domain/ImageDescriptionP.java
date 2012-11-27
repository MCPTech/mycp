package in.mycp.domain;

import java.util.Iterator;
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
@RooJpaActiveRecord(versionField = "", table = "image_description_p", finders = { "findImageDescriptionPsByImageIdEquals", "findImageDescriptionPsByAsset" })
public class ImageDescriptionP {

    @Transient
    private String instanceIdForImgCreation;

    public String getInstanceIdForImgCreation() {
        return instanceIdForImgCreation;
    }

    public void setInstanceIdForImgCreation(String instanceIdForImgCreation) {
        this.instanceIdForImgCreation = instanceIdForImgCreation;
    }

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByUser(User user, int start, int max, String search) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user = :user", ImageDescriptionP.class);
        } else {
            q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user = :user " + " and " + " (o.name like :search or o.imageId like :search" + " or o.imageLocation like :search )", ImageDescriptionP.class);
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

    public static List<in.mycp.domain.ImageDescriptionP> findAllImageDescriptionPs(int start, int max, String search) {
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM ImageDescriptionP o ", ImageDescriptionP.class);
        } else {
            q = em.createQuery("SELECT o FROM ImageDescriptionP o " + " where " + " (o.name like :search or o.imageId like :search" + " or o.imageLocation like :search )", ImageDescriptionP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        List<ImageDescriptionP> is = q.getResultList();
        for (Iterator iterator = is.iterator(); iterator.hasNext(); ) {
            ImageDescriptionP imageDescriptionP = (ImageDescriptionP) iterator.next();
        }
        return q.getResultList();
    }

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByCompany(Company company, int start, int max, String search) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user.department.company = :company ", ImageDescriptionP.class);
        } else {
            q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user.department.company = :company " + " " + " and (o.name like :search or o.imageId like :search or o.imageLocation like :search) ", ImageDescriptionP.class);
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

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
        q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user.department.company = :company ", ImageDescriptionP.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByCompany(Infra infra, Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
/*<<<<<<< HEAD
        q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user.project.department.company = :company " + " and o.asset.productCatalog.infra = :infra ", ImageDescriptionP.class);
=======*/
        q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra ", ImageDescriptionP.class);

        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByInfra(Infra infra) {
        if (infra == null) throw new IllegalArgumentException("The infra argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = null;
        q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.asset.productCatalog.infra = :infra", ImageDescriptionP.class);
        q.setParameter("infra", infra);
        return q;
    }

    public static Number findImageDescriptionCount() {
        String queryStr = "SELECT COUNT(i.id) FROM ImageDescriptionP i ";
        Query q = entityManager().createQuery(queryStr);
        return (Number) q.getSingleResult();
    }

    public static Number findImageDescriptionCountByCompany(Company company, String status) {
        String queryStr = "SELECT COUNT(i.id) FROM ImageDescriptionP i where i.status = :status ";
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

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsByImageIdEqualsAndCompanyEquals(String imageId, Company company) {
        if (imageId == null || imageId.length() == 0) throw new IllegalArgumentException("The imageId argument is required");
        EntityManager em = entityManager();
        TypedQuery<ImageDescriptionP> q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.imageId = :imageId" + " and o.asset.user.department.company = :company", ImageDescriptionP.class);
        q.setParameter("imageId", imageId);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.ImageDescriptionP> findImageDescriptionPsBy(Infra infra, String imageId, Company company) {
        if (imageId == null || imageId.length() == 0) throw new IllegalArgumentException("The imageId argument is required");
        EntityManager em = entityManager();
/*<<<<<<< HEAD
        TypedQuery<ImageDescriptionP> q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.imageId = :imageId" + " and o.asset.user.project.department.company = :company " + " and o.asset.productCatalog.infra = :infra", ImageDescriptionP.class);
=======*/
        TypedQuery<ImageDescriptionP> q = em.createQuery("SELECT o FROM ImageDescriptionP AS o WHERE o.imageId = :imageId" + " and o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", ImageDescriptionP.class);

        q.setParameter("imageId", imageId);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
