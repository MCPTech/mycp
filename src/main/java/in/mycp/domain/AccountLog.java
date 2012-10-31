package in.mycp.domain;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "account_log", finders = { "findAccountLogsByUserId", "findAccountLogsByUserIdAndTimeOfEntryGreaterThan", "findAccountLogsByUserIdAndTimeOfEntryBetween" })
public class AccountLog {
}
