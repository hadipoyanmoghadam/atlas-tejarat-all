package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@yahoo.com">SH BEHNAZ</a>
 * @version $Revision: 1.6 $ $Date: 2013/04/23 14:09:50 $
 */

public class getCardAccount extends CFSHandlerBase implements Configurable {

  protected String accountField;

  public getCardAccount() {
    accountField = Fields.SRC_ACCOUNT;
  }

  public void doProcess(CMMessage msg, Map holder) throws CFSFault {

    String cardNo = msg.getAttributeAsString(Fields.PAN);
    CardAccount cardAccount;
    try {
      cardAccount = CFSFacadeNew.getCardAccountWithTransLimit(cardNo);

    } catch (NotFoundException e) {
      if (log.isDebugEnabled()) log.debug(e);
      throw new CFSFault(CFSFault.FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD, ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD);
    } catch (SQLException e) {
      log.error(e);
      throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR,ActionCode.DATABASE_ERROR);
    }

    if (log.isInfoEnabled()) log.info("cardAccount.getAccountNo() = " + cardAccount.getAccountNo());

    msg.setAttribute(accountField,cardAccount.getAccountNo());
    holder.put(Fields.MAX_TRANS_LIMIT, cardAccount.getMaxTransLimit());
  }

  public void setConfiguration(Configuration cfg) throws ConfigurationException {
    super.setConfiguration(cfg);
    accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
    if ((accountField == null) || (accountField.trim().equals(""))) {
      if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
      accountField = Fields.SRC_ACCOUNT;
    }
  }
}