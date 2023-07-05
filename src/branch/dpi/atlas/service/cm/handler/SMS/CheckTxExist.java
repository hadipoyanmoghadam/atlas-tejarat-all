package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.seq.SequenceGenerator;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: Nov 17, 2019
 * Time: 2:31 PM
 */

public class CheckTxExist extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String origExist = "0";
        String exist = "0";
        String serviceType= msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        SMSMessage smsMessage=(SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String messageSequence = smsMessage.getMessageSequence();
        boolean isReversed =false;

        try {
            String sessionId;
            String msgID;
            List<String> branchLogSet;

            branchLogSet = ChannelFacadeNew.findServiceLogs(messageSequence, serviceType);
            if (branchLogSet.size() > 0)  // Duplicate  message
            {
                exist = "1";
                sessionId = branchLogSet.get(0);
                String message_id = branchLogSet.get(1);
                int m;
                try {
                    m = (Integer.parseInt(message_id.trim()) + 1);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                }

                if ((m % 2) == 0) m++;
                msgID = m + "";

            } else {
                exist = "0";
                if (isReversed) {
                    String origMessageSequence = smsMessage.getOrigMessageData().substring(0, 12);
                   branchLogSet = ChannelFacadeNew.findServiceLogs(origMessageSequence, serviceType);
                    if (branchLogSet.size() > 0) {
                        String orig_pin = branchLogSet.get(2);
                        String reverse_pin = smsMessage.getPin();
                        if (orig_pin.equals(Constants.PIN_WITHDRAW_RESPONSE) && reverse_pin.equals(Constants.PIN_WITHDRAW_REVERSE)) {
                            origExist = "1";
//                            extractSessionId();
                            sessionId = branchLogSet.get(0);
                            String message_id = branchLogSet.get(1);
                            int m;
                            try {
                                m = (Integer.parseInt(message_id.trim()) + 1);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                            }

                            if ((m % 2) == 0) m++;
                            msgID = m + "";

                        } else if (orig_pin.equals(Constants.PIN_DEPOSIT_RESPONSE) && reverse_pin.equals(Constants.PIN_DEPOSIT_REVERSE)) {
                            origExist = "1";
//                            extractSessionId();
                            sessionId = branchLogSet.get(0);
                            String message_id = branchLogSet.get(1);
                            int m;
                            try {
                                m = (Integer.parseInt(message_id.trim()) + 1);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                            }

                            if ((m % 2) == 0) m++;
                            msgID = m + "";

                        } else if (orig_pin.equals(Constants.PIN_DEPOSIT_GIFTCARD_RESPONSE) && reverse_pin.equals(Constants.PIN_DEPOSIT_GIFTCARD_REVERSE)) {
                            origExist = "1";
//                            extractSessionId();
                            sessionId = branchLogSet.get(0);
                            String message_id = branchLogSet.get(1);
                            int m;
                            try {
                                m = (Integer.parseInt(message_id.trim()) + 1);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                            }

                            if ((m % 2) == 0) m++;
                            msgID = m + "";


                        } else {
                            origExist = "0";
//                            generateSessionId();
                            msgID = "1";
                            SequenceGenerator sg = SequenceGenerator.getInstance();
                            long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
                            try {
                                sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                            } catch (ISOException e) {
                                throw new CMFault(CMFault.FAULT_EXTERNAL);
                            }
                        }

                    } else {
                        origExist = "0";
//                        generateSessionId();
                        msgID = "1";
                        SequenceGenerator sg = SequenceGenerator.getInstance();
                        long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
                        try {
                            sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                        } catch (ISOException e) {
                            throw new CMFault(CMFault.FAULT_EXTERNAL);
                        }
                    }
                } else {
//                    generateSessionId();
                    msgID = "1";
                    SequenceGenerator sg = SequenceGenerator.getInstance();
                    long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
                    try {
                        sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                    } catch (ISOException e) {
                        throw new CMFault(CMFault.FAULT_EXTERNAL);
                    }
                }
            }
            msg.setAttribute(Fields.BRANCH_TX_EXIST, exist);
            msg.setAttribute(Fields.BRANCH_ORIG_EXIST, origExist);
            msg.setAttribute(Fields.SESSION_ID, sessionId.trim());
            msg.setAttribute(Fields.MESSAGE_ID, msgID);
            msg.setAttribute(Fields.ISREVERSED, isReversed);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }
    }

}
