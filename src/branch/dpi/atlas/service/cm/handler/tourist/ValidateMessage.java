package branch.dpi.atlas.service.cm.handler.tourist;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
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
 * Date: May 2, 2017
 * Time: 9:45 PM
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

        TouristMessage touristMessage;
        touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = touristMessage.getPin();
        String cardNo = touristMessage.getCardNo();
        String requestIsAccountBased = "0";
        boolean isValid = true;

        if (check4ValidationArray.contains(pin)) {
            String account_no = touristMessage.getAccountNo();
            if ((cardNo.equals(Constants.ZERO_CARD_NO))) {
                requestIsAccountBased = "1";
                if (account_no == null || ISOUtil.isZero(account_no))
                    isValid = false;
            }
            if (!isValid) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }
        } else if (!notCheck4ValidationArray.contains(pin)) {
            //other pin should have card_no.
            if (cardNo.equals("") || cardNo == null || ISOUtil.isZero(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
            }
        }
        msg.setAttribute(Fields.REQUEST_IS_ACCOUNT_BASED, requestIsAccountBased);

        if (pin.equals(Constants.PIN_TOURIST_CHARGE)) {
            if (touristMessage.getAccountNo() == null || touristMessage.getAccountNo().equals("") ||
                    (ISOUtil.isZero(touristMessage.getAccountNo()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }
            if (touristMessage.getAmount() == null || touristMessage.getAmount().equals("") ||
                    (ISOUtil.isZero(touristMessage.getAmount()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_AMOUNT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_AMOUNT));
            }
            if (touristMessage.getTransDate() == null || touristMessage.getTransDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getTransDate())) || touristMessage.getTransTime() == null ||
                    touristMessage.getTransTime().trim().equals("") || (ISOUtil.isZero(touristMessage.getTransTime())) ||
                    touristMessage.getDocumentNo() == null || touristMessage.getDocumentNo().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getDocumentNo()))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_TOURIST_DISCHARGE)) {
            if (touristMessage.getAccountNo().equals("") || touristMessage.getAccountNo() == null ||
                    (ISOUtil.isZero(touristMessage.getAccountNo()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_INVALID));
            }
            if (touristMessage.getAmount().equals("") || touristMessage.getAmount() == null ||
                    (ISOUtil.isZero(touristMessage.getAmount()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_AMOUNT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_AMOUNT));
            }
            if (touristMessage.getTransDate() == null || touristMessage.getTransDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getTransDate())) || touristMessage.getTransTime() == null ||
                    touristMessage.getTransTime().trim().equals("") || (ISOUtil.isZero(touristMessage.getTransTime())) ||
                    touristMessage.getDocumentNo() == null || touristMessage.getDocumentNo().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getDocumentNo()))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        if (pin.equals(Constants.PIN_TOURIST_REVOKE)) {
            if (touristMessage.getAccountNo().equals("") || touristMessage.getAccountNo() == null ||
                    (ISOUtil.isZero(touristMessage.getAccountNo()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_INVALID));
            }
            if (touristMessage.getTransDate() == null || touristMessage.getTransDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getTransDate())) || touristMessage.getTransTime() == null ||
                    touristMessage.getTransTime().trim().equals("") || (ISOUtil.isZero(touristMessage.getTransTime())) ||
                    touristMessage.getDocumentNo() == null || touristMessage.getDocumentNo().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getDocumentNo()))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_TOURIST_SUMMARY_REPORT)) {
            if (touristMessage.getFromDate() == null || touristMessage.getFromDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getFromDate()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (touristMessage.getToDate() == null || touristMessage.getToDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getToDate()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (touristMessage.getTransactionType() == null || touristMessage.getTransactionType().trim().equals("") ||
                    (!touristMessage.getTransactionType().trim().equalsIgnoreCase(Constants.DEPOSIT_TRANSACTION) &&
                            !touristMessage.getTransactionType().trim().equalsIgnoreCase(Constants.WITHDRAW_TRANSACTION))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

        }
        if (pin.equals(Constants.PIN_TOURIST_FUND_TRANSFER)) {
            if (touristMessage.getSrcAccountNo() == null || touristMessage.getSrcAccountNo().equals("") ||
                    (ISOUtil.isZero(touristMessage.getSrcAccountNo()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }
            if (touristMessage.getDestAccountNo() == null || touristMessage.getDestAccountNo().equals("") ||
                    (ISOUtil.isZero(touristMessage.getDestAccountNo()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_INVALID));
            }
            if (touristMessage.getAmount() == null || touristMessage.getAmount().equals("") ||
                    (ISOUtil.isZero(touristMessage.getAmount()))) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_AMOUNT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_AMOUNT));
            }
            if (touristMessage.getTransDate() == null || touristMessage.getTransDate().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getTransDate())) || touristMessage.getTransTime() == null ||
                    touristMessage.getTransTime().trim().equals("") || (ISOUtil.isZero(touristMessage.getTransTime())) ||
                    touristMessage.getDocumentNo() == null || touristMessage.getDocumentNo().trim().equals("") ||
                    (ISOUtil.isZero(touristMessage.getDocumentNo()))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            msg.setAttribute(Fields.REQUEST_IS_ACCOUNT_BASED, "1");
        }
    }
}
