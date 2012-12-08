package in.mycp.domain;

import javax.persistence.Column;
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
@RooJpaActiveRecord(versionField = "", table = "ip_permission_p", finders = { "findIpPermissionPsByGroupDescription", "findIpPermissionPsByProtocolEqualsAndToPortEqualsAndFromPortEquals", "findIpPermissionPsByGroupDescriptionAndProtocolEqualsAndFromPortEquals" })
public class IpPermissionP {

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static TypedQuery<in.mycp.domain.IpPermissionP> findIpPermissionPsByParams(GroupDescriptionP groupDescription, String protocol, String sourceIp, Integer sourcePort) {
        if (groupDescription == null) throw new IllegalArgumentException("The groupDescription argument is required");
        if (protocol == null || protocol.length() == 0) throw new IllegalArgumentException("The protocol argument is required");
        if (sourcePort == null) throw new IllegalArgumentException("The sourcePort argument is required");
        if (sourceIp == null) throw new IllegalArgumentException("The sourceIp argument is required");
        EntityManager em = entityManager();
        TypedQuery<IpPermissionP> q = em.createQuery("SELECT o FROM IpPermissionP AS o WHERE o.groupDescription = :groupDescription " + " AND o.protocol = :protocol  AND o.cidrIps = :sourceIp AND o.fromPort = :sourcePort", IpPermissionP.class);
        q.setParameter("groupDescription", groupDescription);
        q.setParameter("protocol", protocol);
        q.setParameter("sourceIp", sourceIp);
        q.setParameter("sourcePort", sourcePort);
        return q;
    }
}
