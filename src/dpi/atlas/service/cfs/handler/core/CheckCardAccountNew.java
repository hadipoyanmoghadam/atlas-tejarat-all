package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.6 $ $Date: 2007/10/30 14:09:50 $
 */

public class CheckCardAccountNew extends CFSHandlerBase implements Configurable {

  protected String accountField;

  public CheckCardAccountNew() {
    accountField = Fields.SRC_ACCOUNT;
  }

  public void doProcess(CMMessage msg, Map holder) throws CFSFault {

    String accNo = msg.getAttributeAsString(accountField);
    accNo = "000" + accNo;//Todo: chech this laetr,to remove this

    String cardNo = msg.getAttributeAsString(Fields.PAN);
    CardAccount cardAccount;
    try {
      cardAccount = CFSFacadeNew.getCardAccountWithTransLimit(accNo, cardNo);
      if (cardAccount.getStatus() != CFSConstants.ACC_STATUS_ACTIVE)
        throw new CFSFault(CFSFault.FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));
    } catch (NotFoundException e) {
      if (log.isDebugEnabled()) log.debug(e);
      throw new CFSFault(CFSFault.FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));
    } catch (SQLException e) {
      log.error(e);
      throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
     } catch (CFSFault e) {
            throw e;
    } catch (Exception e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
    }

    if (log.isInfoEnabled()) log.info("cardAccount.getMaxTransLimit() = " + cardAccount.getMaxTransLimit());

    holder.put(Fields.MAX_TRANS_LIMIT, cardAccount.getMaxTransLimit());
      //group_card
      int row = cardAccount.getSeries();
      if (row == 0 )
          msg.setAttribute(Fields.CARD_TYPE, CFSConstants.NORMAL_CARD);
      else if (row == CFSConstants.GROUP_CARD_PARENT_ROW)
          msg.setAttribute(Fields.CARD_TYPE, CFSConstants.GROUP_CARD_PARENT);
      else if (row >= 2)
          msg.setAttribute(Fields.CARD_TYPE, CFSConstants.GROUP_CARD_CHILD);
      else{
          msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_ROW);
          throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_ROW));
      }

      //Add SMS REGISTER Field to CMMessage for Group card sms
      msg.setAttribute(Fields.SMS_REGISTER, cardAccount.getSmsRegister());
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
