package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.service.seq.SequenceGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 10, 2015
 * Time: 2:24:03 PM
 */
public class CMSLogger extends TJServiceHandler {
    private static Log log = LogFactory.getLog(CMSLogger.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String actionCode = "";
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
        String messageId = msg.getAttributeAsString(Fields.MESSAGE_ID);
        String rrn = msg.getAttributeAsString(Fields.RRN);
        String branchCode = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        String txString = msg.getAttributeAsString(CMMessage.REQUEST_STR);
        String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String cardNo = msg.getAttributeAsString(Fields.PAN);
        String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
        String hostId = "0";
        if (msg.hasAttribute(Fields.HOST_ID))
            hostId = msg.getAttributeAsString(Fields.HOST_ID);

        try {

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
                txString = msg.getAttributeAsString(CMMessage.RESPONSE);
            } else
                msg.setAttribute(Fields.REQUEST_INSERT_TIME, String.valueOf(currentTime));

            ChannelFacadeNew.insertCMSLog(messageId, cardNo, txString, actionCode, messageType, rrn, sessionId, duration, branchCode,accountNo,hostId);

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Params.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }
    }

}



