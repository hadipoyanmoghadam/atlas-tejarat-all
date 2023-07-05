package branch.dpi.atlas.service.cfs.handler.tourist;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 29, 2017
 * Time: 01:33 PM
 */

public class CheckDefineTouristCard extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String cardNo = msg.getAttributeAsString(Fields.PAN);
        try {
            List<String> cardInfo = CFSFacadeNew.ExistTouristCardInDB(cardNo);
            if (cardInfo.size() > 0) {
                String account_no = cardInfo.get(0);
                if (account_no != null && !account_no.equalsIgnoreCase("")) {
                    account_no = account_no.trim();
                    if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                            msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_CHARGE)) {
                        long maxTransLimit = Long.parseLong(cardInfo.get(1));
                        String status = cardInfo.get(2);
                        if (maxTransLimit == CFSConstants.IGNORE_MAX_TRANS_LIMIT) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                            throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                        } else if (status.equalsIgnoreCase("0")) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
                        } else {
                            msg.setAttribute(Fields.DEST_ACCOUNT, account_no);
                            msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, account_no);
                        }
                    } else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                            msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_DISCHARGE)) {
                        long amount = Long.parseLong((String) msg.getAttribute(Fields.AMOUNT));
                        long maxTransLimit = Long.parseLong(cardInfo.get(1));
                        String status = cardInfo.get(2);
                        if (maxTransLimit == CFSConstants.IGNORE_MAX_TRANS_LIMIT) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                            throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                        } else if (maxTransLimit < amount) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.NOT_SUFFICIENT_FUNDS);
                            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
                        } else if (status.equalsIgnoreCase("0")) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
                        } else {
                            msg.setAttribute(Fields.SRC_ACCOUNT, account_no);
                        }
                    } else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                            msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_CARD_REVOKE)) {
                        long maxTransLimit = Long.parseLong(cardInfo.get(1));
                        String status = cardInfo.get(2);
                        if (maxTransLimit == CFSConstants.IGNORE_MAX_TRANS_LIMIT) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                            throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                        } else if (status.equalsIgnoreCase("0")) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
                        } else {

                            msg.setAttribute(Fields.AMOUNT,String.valueOf(maxTransLimit));
                            msg.setAttribute(Fields.SRC_ACCOUNT, account_no);
                        }
                    }
                } else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                    throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
                }
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
            }
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
    }

}
