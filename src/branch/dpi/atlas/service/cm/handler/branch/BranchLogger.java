package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.seq.SequenceGenerator;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;

import java.util.Map;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

/**
 * User: H.Ghayoumi
 * Date: Apr 11, 2013
 * Time: 1:04:35 PM
 */
public class BranchLogger extends TJServiceHandler {
    private static Log log = LogFactory.getLog(BranchLogger.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String pin = branchMsg.getPin();
        String messageSeq = branchMsg.getMessageSequence();
        String branchCode = branchMsg.getBranchCode();
        String userId = branchMsg.getUserId();
        String reqDate = branchMsg.getRequestDate();
        String reqTime = branchMsg.getRequestTime();
        String txDate = (branchMsg.getTxDate() == null) ? reqDate : branchMsg.getTxDate();
        String txTime = (branchMsg.getTxTime() == null) ? reqTime : branchMsg.getTxTime();
        String accounNo = branchMsg.getAccountNo();
        String cardNo = (branchMsg.getCardNo() == null) ? "" : branchMsg.getCardNo();
        String txString = branchMsg.getTxString();
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
        if(branchMsg.getTerminalId()!=null)
            terminalId=branchMsg.getTerminalId();

        try {
            ChannelFacadeNew.insertBranchLog(sessionId, pin, messageId, messageSeq, branchCode, userId, reqDate,
                    reqTime, txDate, txTime, accounNo, cardNo, actionCode, txString, duration,terminalId);
        } catch (SQLException e) {
            log.error("Inside of branchLogger :: SQLException = " + e);
            msg.setAttribute(Fields.ACTION_CODE,ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }

    }

}



