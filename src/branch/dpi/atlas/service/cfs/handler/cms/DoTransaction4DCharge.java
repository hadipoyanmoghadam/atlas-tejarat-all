package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2018/09/18 10:10:49 $
 */

public class DoTransaction4DCharge extends CFSHandlerBase implements Configurable {

    private String fromAccountField;
    private String amountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        HashMap extraData = new HashMap();

        String accountNo = msg.getAttributeAsString(fromAccountField);
        long amount = Long.parseLong((String) msg.getAttribute(amountField));
        String chargeType = msg.getAttributeAsString(Fields.DCHARGE_TYPE);

        Tx tx = new Tx();
        tx.setAmount(amount);
        tx.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));
        tx.setSrcAccountNo(accountNo);
        tx.setTxCode(msg.getAttributeAsString(Fields.MESSAGE_TYPE));
        tx.setCardNo(msg.getAttributeAsString(Fields.PAN));
        tx.setRRN((String) msg.getAttribute(Fields.MN_RRN));
        tx.setTxSequenceNumber("");
        tx.setFeeAmount(0);

        extraData.put(Fields.DCHARGE_TYPE, chargeType);

        try {
            CFSFacadeNew.txnDoDChargeTransaction(tx, extraData);
            if (chargeType.equalsIgnoreCase(Constants.DCHARGE_BALANCE))
                msg.setAttribute(Fields.AMOUNT,String.valueOf(tx.getLastTransLimit()));
            holder.put(Fields.CARD_BALANCE, tx.getSrcCardBalance());

        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.NO_ACCOUNT_OF_TYPE_REQUESTED, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
        } catch (ModelException e1) {
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, ActionCode.NOT_SUFFICIENT_FUNDS);
        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        fromAccountField = cfg.get(CFSConstants.FROM_ACCOUNT_FIELD);
        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((fromAccountField == null) || (fromAccountField.equals(""))) {
            log.fatal("From Account is not Specified");
            throw new ConfigurationException("From Account is not Specified");
        }
        if ((amountField == null) || (amountField.trim().equals(""))) {
            log.fatal("Amount Field is not Specified");
            throw new ConfigurationException("Amount Field is not Specified");
        }
    }
}
