package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;


import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 23, 2013
 * Time: 1:59:02 PM
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

        BranchMessage branchMsg;
        boolean isValid = true;
        String requestIsAccountBased = "1";
        branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = branchMsg.getPin();
        String accountNo = branchMsg.getAccountNo();
        if (check4ValidationArray.contains(pin)) {
            String cardNo = branchMsg.getCardNo();
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


        if (pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS) && (branchMsg.getAccountStatus().equals("4") || branchMsg.getAccountStatus().equals("5"))) {
            if (ISOUtil.isZero(branchMsg.getAmount()) || ISOUtil.isZero(branchMsg.getBlockRow())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_STATEMENT)) {
            if (ISOUtil.isZero(branchMsg.getFromDate()) || ISOUtil.isZero(branchMsg.getToDate())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_DEPOSIT_GIFTCARD)) {
            if (branchMsg.getNationalCode()==null || branchMsg.getNationalCode().trim().equals("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.MELLICODE_INVALID);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.MELLICODE_INVALID));
            }
        }

        if (pin.equals(Constants.PIN_DISCHARGE_GIFTCARD)) {
            if (ISOUtil.isZero(branchMsg.getCardNo()) || !branchMsg.getCardNo().startsWith(Constants.BANKE_TEJARAT_BIN_NEW)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }
        }

        if (pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS_SIMIN) && (branchMsg.getAccountStatus().equals("6") || branchMsg.getAccountStatus().equals("5")
                ||branchMsg.getAccountStatus().equals("8") || branchMsg.getAccountStatus().equals("7"))) {
            if (ISOUtil.isZero(branchMsg.getAmount())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (branchMsg.getBlockRow()==null ||
                    branchMsg.getBlockRow().trim().equalsIgnoreCase("") ||
                    ISOUtil.isZero(branchMsg.getBlockRow())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_CHANGE_STATUS_CBI)) {
            if (ISOUtil.isZero(branchMsg.getAmount())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (branchMsg.getBlockRow()==null ||
                    branchMsg.getBlockRow().trim().equalsIgnoreCase("") ||
                    ISOUtil.isZero(branchMsg.getBlockRow())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_REMOVE_ROW)) {
            if ((branchMsg.getNationalCode()==null || branchMsg.getNationalCode().trim().equalsIgnoreCase("")) &&
                    (branchMsg.getExt_IdNumber()==null || branchMsg.getExt_IdNumber().trim().equalsIgnoreCase(""))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        if (pin.equals(Constants.PIN_UPDATE_ROW)) {
            if (branchMsg.getOldNationalCode()==null || branchMsg.getOldNationalCode().trim().equalsIgnoreCase("") ||
                    ISOUtil.isZero(branchMsg.getOldNationalCode())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (branchMsg.getOldNationalCode().trim().equalsIgnoreCase(Constants.FOREIGNERS_NATIONAL_CODE) &&
                    (branchMsg.getOld_ext_IdNumber()==null || branchMsg.getOld_ext_IdNumber().trim().equalsIgnoreCase("") ||
                            ISOUtil.isZero(branchMsg.getOld_ext_IdNumber()))){
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if(branchMsg.getOwnerIndex()==null || branchMsg.getOwnerIndex().trim().equalsIgnoreCase("")
                    ||branchMsg.getOwnerIndex().equalsIgnoreCase("00")){
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }

        if (pin.equals(Constants.PIN_REMOVE_DOC_SIMIN) && (!branchMsg.getRequestType().equals("1") && !branchMsg.getRequestType().equals("2"))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        }

        if (pin.equals(Constants.PIN_REMITTANCE_INQUIRY) || pin.equals(Constants.PIN_WITHDRAW_REMITTANCE)) {
            if (branchMsg.getNationalCode() == null || branchMsg.getNationalCode().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setNationalCode(branchMsg.getNationalCode().trim());
        }
        if (pin.equals(Constants.PIN_REGISTER_PAYA_REQUEST)) {
            if (branchMsg.getDocumentNo() == null || branchMsg.getDocumentNo().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDocumentNo(branchMsg.getDocumentNo().trim());

            if (branchMsg.getDueDate() == null || branchMsg.getDueDate().trim().equalsIgnoreCase("") || branchMsg.getDueDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDueDate(branchMsg.getDueDate().trim());

            if (branchMsg.getBankSend() == null || branchMsg.getBankSend().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setBankSend(branchMsg.getBankSend().trim());

            if (branchMsg.getBankRecv() == null || branchMsg.getBankRecv().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setBankRecv(branchMsg.getBankRecv().trim());

            if (branchMsg.getAmount() == null || branchMsg.getAmount().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setAmount(branchMsg.getAmount().trim());

            if (branchMsg.getSourceIban() == null || branchMsg.getSourceIban().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setSourceIban(branchMsg.getSourceIban().trim());

            if (branchMsg.getDestIban() == null || branchMsg.getDestIban().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDestIban(branchMsg.getDestIban().trim());

            if (branchMsg.getSenderName() == null || branchMsg.getSenderName().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setSenderName(branchMsg.getSenderName().trim());

            if (branchMsg.getMeliCode() == null || branchMsg.getMeliCode().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setMeliCode(branchMsg.getMeliCode().trim());

            if (branchMsg.getReciverName() == null || branchMsg.getReciverName().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setReciverName(branchMsg.getReciverName().trim());

            if (branchMsg.getSenderShahab() == null || branchMsg.getSenderShahab().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setSenderShahab(branchMsg.getSenderShahab().trim());

            if (branchMsg.getReason() == null || branchMsg.getReason().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setReason(branchMsg.getReason().trim());
        }
        if (pin.equals(Constants.PIN_SEND_PAYA)) {
            if (branchMsg.getOperationCode() == null || branchMsg.getOperationCode().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setOperationCode(branchMsg.getOperationCode().trim());

            if (branchMsg.getTransDate() == null || branchMsg.getTransDate().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setTransDate(branchMsg.getTransDate().trim());

            if (branchMsg.getDueDate() == null || branchMsg.getDueDate().trim().equalsIgnoreCase("") || branchMsg.getDueDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDueDate(branchMsg.getDueDate().trim());

            if (branchMsg.getSerial() == null || branchMsg.getSerial().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setSerial(branchMsg.getSerial().trim());

            if (branchMsg.getTransTime() == null || branchMsg.getTransTime().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setTransTime(branchMsg.getTransTime().trim());

            if (branchMsg.getPayaDescription() == null || branchMsg.getPayaDescription().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setPayaDescription(branchMsg.getPayaDescription().trim());
        }
        if (pin.equals(Constants.PIN_GET_PAYA) || pin.equals(Constants.PIN_INACTIVE_PAYA) || pin.equals(Constants.PIN_DELETE_PAYA)) {

            if (branchMsg.getDueDate() == null || branchMsg.getDueDate().trim().equalsIgnoreCase("") || branchMsg.getDueDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDueDate(branchMsg.getDueDate().trim());

            if (branchMsg.getDocumentNo() == null || branchMsg.getDocumentNo().trim().equalsIgnoreCase("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDocumentNo(branchMsg.getDocumentNo().trim());
        }
        if (pin.equals(Constants.PIN_GET_IN_DUE_DATE_PAYA)) {

            if (branchMsg.getDueDate() == null || branchMsg.getDueDate().trim().equalsIgnoreCase("") || branchMsg.getDueDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setDueDate(branchMsg.getDueDate().trim());
        }

        if (pin.equals(Constants.PIN_REPORT_PAYA)) {

            if (branchMsg.getFromDate() == null || branchMsg.getFromDate().trim().equalsIgnoreCase("") || branchMsg.getFromDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setFromDate(branchMsg.getFromDate().trim());

            if (branchMsg.getToDate() == null || branchMsg.getToDate().trim().equalsIgnoreCase("") || branchMsg.getToDate().trim().length()!=8) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            } else
                branchMsg.setToDate(branchMsg.getToDate().trim());
        }
        if (pin.equals(Constants.PIN_WITHDRAW_WAGE)) {
            if (!branchMsg.getOpType().equalsIgnoreCase(Fields.RTGS_WAGE) && !branchMsg.getOpType().equalsIgnoreCase(Fields.ACH_WAGE)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (branchMsg.getOpType().equalsIgnoreCase(Fields.ACH_WAGE)) {
                if (!branchMsg.getGroupType().equalsIgnoreCase(Fields.SINGLE_ACH) && !branchMsg.getGroupType().equalsIgnoreCase(Fields.GROUP_ACH)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (branchMsg.getGroupType().equalsIgnoreCase(Fields.GROUP_ACH) &&
                        (branchMsg.getGroupNo() == null || ISOUtil.isZero(branchMsg.getGroupNo().trim()) ||
                                !StringUtils.isNumeric(branchMsg.getGroupNo().trim()))) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }else{
                    branchMsg.setGroupNo(branchMsg.getGroupNo().trim());
                }
            }
            if (ISOUtil.isZero(branchMsg.getAmount())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        if (pin.equals(Constants.PIN_DEPOSIT_GIFTCARD) || pin.equals(Constants.PIN_DEPOSIT_GIFTCARD_REVERSE)) {
            int len=branchMsg.getNationalCode().length();
            if (len!=10 && len!=11 && len!=12 && len!=15) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.MELLICODE_INVALID);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.MELLICODE_INVALID));
            }
        }
        if (pin.equals(Constants.PIN_CREATE_NAC) || pin.equals(Constants.PIN_CREATE_NAC_BATCH) || pin.equals(Constants.PIN_CREATE_SIMIN_NAC)
                || pin.equals(Constants.PIN_CREATE_NAC_ATM)) {
            if (accountNo.compareTo(Constants.START_OMID_RANGE) >= 0 && (accountNo.compareTo(Constants.END_OMID_RANGE) <= 0)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }
        }
    }
}
