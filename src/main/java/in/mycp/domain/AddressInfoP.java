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
@RooJpaActiveRecord(versionField = "", table = "address_info_p", finders = { "findAddressInfoPsByAsset", "findAddressInfoPsByInstanceIdEquals", "findAddressInfoPsByPublicIpEquals", "findAddressInfoPsByInstanceIdLike" })
public class AddressInfoP {

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

    public static List<in.mycp.domain.AddressInfoP> findAllAddressInfoPs() {
        return entityManager().createQuery("SELECT o FROM AddressInfoP o where  o.asset.active = 1", AddressInfoP.class).getResultList();
    }

    public static List<in.mycp.domain.AddressInfoP> findAllAddressInfoPs(int start, int max, String search) {
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM AddressInfoP o where  o.asset.active = 1", AddressInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM AddressInfoP o" + " where   o.asset.active = 1 and" + " (o.name like :search or o.instanceId like :search or o.publicIp like :search)", AddressInfoP.class);
            if (StringUtils.contains(search, " ")) {
                search = StringUtils.replaceChars(search, " ", "%");
            }
            q.setParameter("search", "%" + search + "%");
        }
        q.setFirstResult(start);
        q.setMaxResults(max);
        return q.getResultList();
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByUser(User user, int start, int max, String search) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user = :user and  o.asset.active = 1", AddressInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user = :user  and  o.asset.active = 1" + " and " + "( o.name like :search or o.instanceId like :search or o.publicIp like :search)", AddressInfoP.class);
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

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByCompany(Company company, int start, int max, String search) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        if (StringUtils.isBlank(search)) {
            q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1", AddressInfoP.class);
        } else {
            q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1 " + " and " + " (o.name like :search or o.instanceId like :search or o.publicIp like :search)", AddressInfoP.class);
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

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsBy(Infra infra, Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.user.department.company = :company and  o.asset.active = 1 " + " and o.asset.productCatalog.infra = :infra", AddressInfoP.class);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }
    
    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsBy(Infra infra, String  instanceId,String ip) {
        
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.instanceId like :instanceId and  o.asset.active = 1 " + " " +
        		"and o.asset.productCatalog.infra = :infra and o.publicIp like :ip", AddressInfoP.class);
       
        q.setParameter("instanceId", "%"+instanceId+"%");
        q.setParameter("ip", "%"+ip+"%");
        
        q.setParameter("infra", infra);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByInfra(Infra infra) {
        if (infra == null) throw new IllegalArgumentException("The infra argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = null;
        q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset.productCatalog.infra = :infra and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("infra", infra);
        return q;
    }

    public static Number findAddressInfoCountByCompany(Company company) {
        String queryStr = "SELECT COUNT(i.id) FROM AddressInfoP i  where i.asset.active = 1 ";
        if (company != null) {
            queryStr = queryStr + "  and i.asset.user.department.company = :company";
        }
        Query q = entityManager().createQuery(queryStr);
        if (company != null) {
            q.setParameter("company", company);
        }
        return (Number) q.getSingleResult();
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByPublicIpEqualsAndCompanyEquals(String publicIp, Company company) {
        if (publicIp == null || publicIp.length() == 0) throw new IllegalArgumentException("The publicIp argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.publicIp = :publicIp  and  o.asset.active = 1" + " and o.asset.user.department.company = :company", AddressInfoP.class);
        q.setParameter("publicIp", publicIp);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsBy(Infra infra, String publicIp, Company company) {
        if (publicIp == null || publicIp.length() == 0) throw new IllegalArgumentException("The publicIp argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.publicIp = :publicIp  and  o.asset.active = 1" + " and o.asset.user.department.company = :company " + " and o.asset.productCatalog.infra = :infra", AddressInfoP.class);
        q.setParameter("publicIp", publicIp);
        q.setParameter("company", company);
        q.setParameter("infra", infra);
        return q;
    }
    
    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsBy(Infra infra, String publicIp) {
        if (publicIp == null || publicIp.length() == 0) throw new IllegalArgumentException("The publicIp argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.publicIp = :publicIp  and  o.asset.active = 1" + " " +
        		" " + " and o.asset.productCatalog.infra = :infra", AddressInfoP.class);
        q.setParameter("publicIp", publicIp);
        
        q.setParameter("infra", infra);
        return q;
    }


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByAsset4Report(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("The asset argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset = :asset ", AddressInfoP.class);
        q.setParameter("asset", asset);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByAsset(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("The asset argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.asset = :asset and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("asset", asset);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByInstanceIdEquals(String instanceId) {
        if (instanceId == null || instanceId.length() == 0) throw new IllegalArgumentException("The instanceId argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.instanceId = :instanceId and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("instanceId", instanceId);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByInstanceIdLike(String instanceId) {
        if (instanceId == null || instanceId.length() == 0) throw new IllegalArgumentException("The instanceId argument is required");
        instanceId = instanceId.replace('*', '%');
        if (instanceId.charAt(0) != '%') {
            instanceId = "%" + instanceId;
        }
        if (instanceId.charAt(instanceId.length() - 1) != '%') {
            instanceId = instanceId + "%";
        }
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE LOWER(o.instanceId) LIKE LOWER(:instanceId) and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("instanceId", instanceId);
        return q;
    }

    public static TypedQuery<in.mycp.domain.AddressInfoP> findAddressInfoPsByPublicIpEquals(String publicIp) {
        if (publicIp == null || publicIp.length() == 0) throw new IllegalArgumentException("The publicIp argument is required");
        EntityManager em = entityManager();
        TypedQuery<AddressInfoP> q = em.createQuery("SELECT o FROM AddressInfoP AS o WHERE o.publicIp = :publicIp and  o.asset.active = 1", AddressInfoP.class);
        q.setParameter("publicIp", publicIp);
        return q;
    }
}
