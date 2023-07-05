package branch.dpi.atlas.service.cm.handler.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 4, 2017
 * Time: 2:20 PM
 */
public class ValidateMessage extends TJServiceHandler implements Configurable {
    ArrayList check4ValidationArray;
    ArrayList notCheck4ValidationArray;

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String check4Validation = configuration.get("checked");
        check4ValidationArray = CMUtil.tokenizString(check4Validation, ",");
        String notCheck4Validation = configuration.get("notChecked");
        notCheck4ValidationArray = CMUtil.tokenizString(notCheck4Validation, ",");

    }


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CreditsMessage creditsMessage;
        boolean isValid = true;
        String requestIsAccountBased = "1";
        creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = creditsMessage.getPin();
        String accountNo = creditsMessage.getAccountNo();
        if (check4ValidationArray.contains(pin)) {
            String cardNo = creditsMessage.getCardNo();
            if ((accountNo.equals(Constants.ZERO_ACCOUNT_NO))) {
                requestIsAccountBased = "0";
                if (cardNo != null && ISOUtil.isZero(cardNo))
                    isValid = false;
            }
            if (!isValid) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        } else if (!notCheck4ValidationArray.contains(pin)) {
            //other pin should have account_no. pin number 60125 is exceptional.
            if (accountNo.equals("") || accountNo == null || (accountNo.equals(Constants.ZERO_ACCOUNT_NO))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }
        }
        msg.setAttribute(Fields.REQUEST_IS_ACCOUNT_BASED, requestIsAccountBased);

    }
}
