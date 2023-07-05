package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.Cmparam;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.CheckDigitsUtils;
import dpi.atlas.util.Constants;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.jpos.iso.ISOUtil;
import java.util.Map;
import java.util.Vector;

/**
 * User: F.Heidari
 * Date: July 15 2019
 * Time: 10:41 AM
 */
public class ValidationOfVerhoef extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside ValidationOfVerhoef.doProcess()");
        BranchMessage branchMsg = null;
        String pin = "";
        String accountNumber = "";
        boolean validated=false;
        try {

            branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            pin = branchMsg.getPin();
            String trxID = branchMsg.getId_num().trim().substring(0, 13);
            String amount_verhoef = branchMsg.getAmount_verhoef();
            accountNumber = ISOUtil.unPadLeft(branchMsg.getAccountNo(), '0');
            String code = branchMsg.getId_num();


            if (pin.equalsIgnoreCase(Constants.PIN_IsValid_Verhoef)) {
                boolean isMatched = CheckDigitsUtils.isValidVerhoef(trxID, accountNumber, amount_verhoef, code);
                if (!isMatched) {
                    branchMsg.setIsvalid_verhoef("0");
                } else {
                    branchMsg.setIsvalid_verhoef("1");
                }
            }
            if (pin.equalsIgnoreCase(Constants.PIN_IsValid_ExpenseVerhoef)) {
                Vector verhoeafACC = ChannelFacadeNew.getCMParam("VerhoefAcc");
                if (verhoeafACC == null || verhoeafACC.size() == 0)
                    throw new NotFoundException("verhoeafACC Not Found. ");
                for (Object verhoefAccountNo : verhoeafACC) {
                    Cmparam param = (Cmparam) verhoefAccountNo;
                    if (accountNumber.equals(param.getDescription().trim())) {
                        //this algorithm is special for "meli palayesh"
                        boolean isMatched = CheckDigitsUtils.isValidVerhoefSpecial(trxID, accountNumber, amount_verhoef, code);
                        if (!isMatched) {
                            branchMsg.setIsvalid_verhoef("0");
                        } else {
                            branchMsg.setIsvalid_verhoef("1");
                        }

                        validated = true;
                    }
                }

                if (validated == false) {


                    String dueDate = branchMsg.getDate_verhoef();
                    String chequeNumber = branchMsg.getCheque_number();
                    if (chequeNumber != null && !chequeNumber.trim().equalsIgnoreCase("") && chequeNumber.trim().length() == 16) {
                        chequeNumber = chequeNumber.trim();
                        chequeNumber = chequeNumber.substring(chequeNumber.length() - 11);
                        boolean isMatched = CheckDigitsUtils.isValidExpenseVerhoef(trxID, accountNumber, amount_verhoef, code, chequeNumber, dueDate);
                        if (!isMatched) {
                            branchMsg.setIsvalid_verhoef("0");
                        } else {
                            branchMsg.setIsvalid_verhoef("1");
                        }
                    } else
                        branchMsg.setIsvalid_verhoef("0");
                }
            }


        } catch (CheckDigitException e) {
            log.error("CheckDigitException::" + pin + "::" + accountNumber);
            branchMsg.setIsvalid_verhoef("0");
        } catch (Exception e) {
            log.error("Exception::" + pin + "::" + accountNumber);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}

