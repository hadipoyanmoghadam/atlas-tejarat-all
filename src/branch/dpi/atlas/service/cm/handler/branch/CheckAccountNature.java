package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.CheckDigitsUtils;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 13 2016
 * Time: 04:52 PM
 */
public class CheckAccountNature extends TJServiceHandler implements Configurable {

    ArrayList check_pay_code = new ArrayList();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String checkPayCode = configuration.get("checkPayCode");
        check_pay_code = CMUtil.tokenizString(checkPayCode, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside CheckAccountNature.doProcess(CMMessage msg, Map holder)");
        BranchMessage branchMsg;
        String pin="";
        String messageSeq="";
        try {

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

            branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            pin = branchMsg.getPin();
            messageSeq=branchMsg.getMessageSequence();

            if (pin.equalsIgnoreCase(Constants.PIN_DEPOSIT)) {


                String id1 = branchMsg.getId1().trim();
                String amount = branchMsg.getAmount();

                if (msg.hasAttribute(Constants.DEST_ACCOUNT_NATURE)) {

                    String destAccountNo = command.getParam(Fields.DEST_ACCOUNT);
                    if (destAccountNo.length() < 13)
                        destAccountNo = ISOUtil.zeropad(destAccountNo, 13);

                    int destAccountNature = Integer.parseInt(msg.getAttributeAsString(Constants.DEST_ACCOUNT_NATURE));
                    if ((destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN)
                            && (id1.trim().equals("") || !checkDigit(msg, destAccountNo, id1, amount))) {

                        msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                    }

                    if (destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN) {
                        String[] payCds = {id1, ""};
                        List patterns = ChannelFacadeNew.getPatternsList(destAccountNo);
                        boolean isMatched = CheckDigitsUtils.isMatchedByPattern(payCds, patterns);
                        if (isMatched) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID_BY_PATTERN);
                            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID_BY_PATTERN));
                        }
                    }
                    if (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME) {
                        if (id1==null || id1.trim().equals("") || ISOUtil.isZero(id1.trim())) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                        }
                        if(ChannelFacadeNew.checkId(destAccountNo,id1)){
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.BILL_PAYMENT_BEFORE_PAID);
                            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.BILL_PAYMENT_BEFORE_PAID));
                        }
                    }
                }
            }
        } catch (CMFault fe) {
            throw fe;
        } catch (ModelException e) {
            if (e.getMessage().equals(Constants.PAYMENT_CODE_IS_INVALID)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
            } else if (e.getMessage().equals(Constants.INVALID_NUMBER_OF_PAYCODES)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE));
            } else if (e.getMessage().equals(Constants.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
            }

        } catch (Exception e) {
            log.error("Exception"+pin+messageSeq);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    private boolean checkDigit(CMMessage msg, String destAccount, String PayCode, String amount) throws CMFault {
        String[] payCds = {PayCode, ""};
        boolean isCheckDigit = false;
        try {
            isCheckDigit = CheckDigitsUtils.isCheckDigit(destAccount, payCds, amount);
        } catch (SQLException e) {
            log.error("DB has encountered an exception during fething data from tbchkdigitalg...");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ISOException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (ModelException e) {
            if (e.getMessage().equals(Constants.NO_RELATED_ALG_EXIST)) {
                log.debug("Account number=" + destAccount + " is not permitted to be credit");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
            } else if (e.getMessage().equals(Constants.INVALID_NUMBER_OF_PAYCODES)) {
                log.error("Invalid number of payment code parameter");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE));
            }
        }
        return isCheckDigit;
    }
}