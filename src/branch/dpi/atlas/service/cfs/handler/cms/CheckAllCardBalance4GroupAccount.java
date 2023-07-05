package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class CheckAllCardBalance4GroupAccount extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            String accNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            List<CardAccount> cardAccount = CFSFacadeNew.getGroupCardList(accNo);
            if (cardAccount == null || cardAccount.equals("") || cardAccount.size() == 0)
                throw new NotFoundException(CFSFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));

            long amount;
            String chargeType = msg.getAttributeAsString(Fields.DCHARGE_TYPE).trim();
            if (chargeType.equalsIgnoreCase(Constants.DCHARGE_AMOUNT) || chargeType.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE)) {
                amount = Long.parseLong(msg.getAttributeAsString(Fields.AMOUNT));

                for (CardAccount card : cardAccount) {
                  if(card.getMaxTransLimit() <amount)
                      throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
                }

            }

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

    }


}
