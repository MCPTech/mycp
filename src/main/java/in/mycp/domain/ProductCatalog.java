package in.mycp.domain;

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
@RooJpaActiveRecord(versionField = "", table = "product_catalog", finders = { "findProductCatalogsByNameEquals", "findProductCatalogsByProductTypeEquals", "findProductCatalogsByInfra" })
public class ProductCatalog {

    public static TypedQuery<in.mycp.domain.ProductCatalog> findProductCatalogsByCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("The company argument is required");
        EntityManager em = entityManager();
        TypedQuery<ProductCatalog> q = em.createQuery("SELECT o FROM ProductCatalog AS o WHERE o.infra.company = :company", ProductCatalog.class);
        q.setParameter("company", company);
        return q;
    }

    public static TypedQuery<in.mycp.domain.ProductCatalog> findProductCatalogsByProductTypeAndCompany(String productType, Company company) {
        if (productType == null || productType.length() == 0) throw new IllegalArgumentException("The productType argument is required");
        EntityManager em = ProductCatalog.entityManager();
        TypedQuery<ProductCatalog> q = em.createQuery("SELECT o FROM ProductCatalog AS o WHERE o.productType = :productType " + " and o.infra.company = :company", ProductCatalog.class);
        q.setParameter("productType", productType);
        q.setParameter("company", company);
        return q;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
