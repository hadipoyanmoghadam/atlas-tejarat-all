package branch.dpi.atlas.service.cm.handler.credits;


import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
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
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 11:43 AM
 */
public class CheckTxExist extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String origExist = "0";
        String exist = "0";
        String serviceType= msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String messageSequence = creditsMessage.getMessageSequence();
        boolean isReversed = creditsMessage.getPin().charAt(2) == '4';


        try {
            String sessionId;
            String msgID;
            List<String> serviceLogSet;

            serviceLogSet = ChannelFacadeNew.findServiceLogs(messageSequence, serviceType);
            if (serviceLogSet.size() > 0)  // Duplicate  message
            {
                exist = "1";
                sessionId = serviceLogSet.get(0);
                String message_id = serviceLogSet.get(1);
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
                    String origMessageSequence = creditsMessage.getOrigMessageData().substring(0, 12);
                    serviceLogSet = ChannelFacadeNew.findServiceLogs(origMessageSequence, serviceType);
                    if (serviceLogSet.size() > 0) {
                        String orig_pin = serviceLogSet.get(2);
                        String reverse_pin = creditsMessage.getPin();
                        if (orig_pin.equals(Constants.PIN_CREDITS_WITHDRAW_RESPONSE) && reverse_pin.equals(Constants.PIN_CREDITS_WITHDRAW_REVERSE)) {
                            origExist = "1";
                            sessionId = serviceLogSet.get(0);
                            String message_id = serviceLogSet.get(1);
                            int m;
                            try {
                                m = (Integer.parseInt(message_id.trim()) + 1);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                            }

                            if ((m % 2) == 0) m++;
                            msgID = m + "";

                        } else if (orig_pin.equals(Constants.PIN_CREDITS_DEPOSIT_RESPONSE) && reverse_pin.equals(Constants.PIN_CREDITS_DEPOSIT_REVERSE)) {
                            origExist = "1";
                            sessionId = serviceLogSet.get(0);
                            String message_id = serviceLogSet.get(1);
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
