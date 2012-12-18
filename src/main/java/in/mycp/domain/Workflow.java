package in.mycp.domain;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
@RooJpaActiveRecord(versionField = "", table = "workflow", finders = { "findWorkflowsByProcessIdEquals", "findWorkflowsByAssetIdEquals", "findWorkflowsByAssetTypeEquals", "findWorkflowsByUser" })
public class Workflow {

    @Transient
    private Date startTime;

    @Transient
    private String processName;

    @Transient
    private String processStatus;

    @Transient
    private String assetDetails;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getAssetDetails() {
        return assetDetails;
    }

    public void setAssetDetails(String assetDetails) {
        this.assetDetails = assetDetails;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public static TypedQuery<in.mycp.domain.Workflow> findWorkflowsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<Workflow> q = em.createQuery("SELECT o FROM Workflow AS o WHERE o.user.department.company = :company", Workflow.class);
        q.setParameter("company", company);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    public static Workflow findWorkflowsBy(Integer assetId,String assetType) {
        if (assetId == null) throw new IllegalArgumentException("The assetId argument is required");
        EntityManager em = entityManager();
        TypedQuery<Workflow> q = em.createQuery("SELECT o FROM Workflow AS o WHERE o.assetId = :assetId and o.assetType = :assetType", Workflow.class);
        q.setParameter("assetId", assetId);
        q.setParameter("assetType", assetType);
        List<Workflow> wfs = q.getResultList();
        for (Iterator iterator = wfs.iterator(); iterator.hasNext(); ) {
			Workflow workflow = (Workflow) iterator.next();
			return workflow;
		}
        
        return null;
    }
    
}
