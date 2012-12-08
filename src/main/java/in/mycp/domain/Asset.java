package in.mycp.domain;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "asset", finders = { "findAssetsByUserAndAssetType" })
public class Asset {

    protected static Logger logger = Logger.getLogger(Asset.class);

    @Transient
    public String currency;

    @Transient
    public String assetDetails;

    @Transient
    public String assetTypeName;

    @Transient
    public long duration;

    @Transient
    public long cost;

    public static TypedQuery<in.mycp.domain.Asset> findAssets4Report4User(int userId, AssetType assetType, boolean billable, boolean active) {
        try {
            if (assetType == null) throw new IllegalArgumentException("The assetType argument is required");
            EntityManager em = Asset.entityManager();
            TypedQuery<Asset> q = null;
            if (userId == 0) {
            	//AND (o.active = :active or o.endTime is not null)
                q = em.createQuery("SELECT o FROM Asset AS o WHERE o.assetType = :assetType" + "  ", Asset.class);
            } else {
            	//AND (o.active = :active or o.endTime is not null)
                q = em.createQuery("SELECT o FROM Asset AS o WHERE o.user.id = :userId AND o.assetType = :assetType" + "  ", Asset.class);
                q.setParameter("userId", userId);
            }
            q.setParameter("assetType", assetType);
           // q.setParameter("active", active);
            return q;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public static TypedQuery<in.mycp.domain.Asset> findAssets4Report4Company(int companyId, AssetType assetType, boolean billable, boolean active) {
        try {
            if (assetType == null) throw new IllegalArgumentException("The assetType argument is required");
            EntityManager em = Asset.entityManager();
            TypedQuery<Asset> q = null;
            //AND (o.active = :active or o.endTime is not null)
            q = em.createQuery("SELECT o FROM Asset AS o WHERE o.user.department.company.id = :companyId AND o.assetType = :assetType" + "  ", Asset.class);
            q.setParameter("companyId", companyId);
            q.setParameter("assetType", assetType);
           // q.setParameter("active", active);
            //System.out.println("findAssets4Report4Company q.getResultList().size() = "+q.getResultList().size());
            return q;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public static TypedQuery<in.mycp.domain.Asset> findAssets4Report4Department(int departmentId, AssetType assetType, boolean billable, boolean active) {
        try {
            if (assetType == null) throw new IllegalArgumentException("The assetType argument is required");
            EntityManager em = Asset.entityManager();
            TypedQuery<Asset> q = null;
            //AND (o.active = :active or o.endTime is not null)
            q = em.createQuery("SELECT o FROM Asset AS o WHERE o.user.department.id = :departmentId AND o.assetType = :assetType" + "  ", Asset.class);
            q.setParameter("departmentId", departmentId);
            q.setParameter("assetType", assetType);
            //q.setParameter("active", active);
           // System.out.println("findAssets4Report4Department q.getResultList().size() = "+q.getResultList().size());
            return q;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public static TypedQuery<in.mycp.domain.Asset> findAssets4Report4Project(int projectId, AssetType assetType, boolean billable, boolean active) {
        try {
            if (assetType == null) throw new IllegalArgumentException("The assetType argument is required");
            EntityManager em = Asset.entityManager();
            TypedQuery<Asset> q = null;
            //AND (o.active = :active or o.endTime is not null)
            q = em.createQuery("SELECT o FROM Asset o join o.user.projects ps WHERE ps.id = :projectId AND o.assetType = :assetType" + "  ", Asset.class);
            q.setParameter("projectId", projectId);
            q.setParameter("assetType", assetType);
           // q.setParameter("active", active);
            return q;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public void deactivate() {
        this.setActive(false);
        this.setEndTime(new Date());
        this.merge();
    }

    public String getAssetDetails() {
        return StringUtils.replace(assetDetails, "null", "");
    }

    public void setAssetDetails(String assetDetails) {
        this.assetDetails = assetDetails;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String toString() {
        return "";
    }
}
