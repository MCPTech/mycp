package in.mycp.domain;

import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "company", finders = { "findCompanysByNameEquals" })
public class Company {

    @Column(name = "quota")
    private Integer quota=0;
    
    @Column(name = "min_bal") 
    private Integer minBal=0;
    
    public static List<java.lang.String> findAllDistinctCurrency() {
        return entityManager().createQuery("SELECT DISTINCT c.currency FROM Company AS c", String.class).getResultList();
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
