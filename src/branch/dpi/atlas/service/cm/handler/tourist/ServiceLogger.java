package branch.dpi.atlas.service.cm.handler.tourist;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.seq.SequenceGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 09:27 AM
 */
public class ServiceLogger extends TJServiceHandler {
    private static Log log = LogFactory.getLog(ServiceLogger.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        TouristMessage touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String pin = touristMessage.getPin();
        String messageSeq = touristMessage.getMessageSequence();
        String branchCode = touristMessage.getBranchCode();
        String userId = touristMessage.getUserId();
        String txDate = touristMessage.getRequestDate();
        String txTime = touristMessage.getRequestTime();
        String cardNo = touristMessage.getCardNo();
        String accountNo = (touristMessage.getAccountNo() == null) ? "" : touristMessage.getAccountNo();
        String txString = touristMessage.getTxString();
        String actionCode = "";
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String messageId = msg.getAttributeAsString(Fields.MESSAGE_ID);

        if ((sessionId == null || "".equals(sessionId))) {
            SequenceGenerator sg = SequenceGenerator.getInstance();
            long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
            try {
                sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                if (messageId == null || messageId.equals(""))
                    messageId = "1";
            } catch (ISOException e) {
                log.error("EEROR :: ISOUtil.zeropad(sessionId )");
                throw new CMFault(CMFault.FAULT_EXTERNAL);
            }
        }
        if (msg.hasAttribute(Fields.ACTION_CODE))
            actionCode = (String) msg.getAttribute(Fields.ACTION_CODE);

        if (messageId.length() == 1)
            messageId = "0" + messageId;
        msg.setAttribute(Fields.MESSAGE_ID, messageId);

        long currentTime = System.currentTimeMillis();
        long duration = 0;
        if (msg.hasAttribute(Fields.REQUEST_INSERT_TIME)) {
            duration = currentTime - Long.parseLong(msg.getAttributeAsString(Fields.REQUEST_INSERT_TIME));
            pin = String.valueOf(Integer.parseInt(msg.getAttributeAsString(Fields.PIN)) + 1);
            txString = msg.getAttributeAsString(CMMessage.RESPONSE);
        } else
            msg.setAttribute(Fields.REQUEST_INSERT_TIME, String.valueOf(currentTime));

        String terminalId="";
        if(touristMessage.getTerminalId()!=null)
            terminalId=touristMessage.getTerminalId();
        try {
            ChannelFacadeNew.insertServiceLog(sessionId, pin, messageId, messageSeq, branchCode, userId, txDate,
                    txTime, accountNo, cardNo, actionCode, txString, duration, terminalId);
        } catch (SQLException e) {
            log.error("Inside of branchLogger :: SQLException = " + e);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }

    }

}



