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
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 11, 2015
 * Time: 1:04:03 PM
 */
public class CheckTxExist extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String exist;
        String sessionId;
        String msgID;
        List<String> cmsLogSet;
        String rrn;

        try {
            rrn = msg.getAttributeAsString(Fields.RRN);
            String serviceType= (String) msg.getAttribute(CMMessage.SERVICE_TYPE);

            cmsLogSet = ChannelFacadeNew.findCMSMessageLog(rrn);
            if (cmsLogSet.size() > 0) {
                if(cmsLogSet.get(2)!=null && cmsLogSet.get(2).trim().equalsIgnoreCase(serviceType)) {
                    // Duplicate  message
                    exist = "1";
                    sessionId = cmsLogSet.get(0);
                    String message_id = cmsLogSet.get(1);
                    int m;
                    try {
                        m = (Integer.parseInt(message_id.trim()) + 1);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(":::Inside CheckTxExist >> ERROR : " + e.getMessage()); //todo: throw it
                    }

                    if ((m % 2) == 0) m++;
                    msgID = m + "";
                }else {
                    exist = "0";
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
                exist = "0";
                msgID = "1";
                SequenceGenerator sg = SequenceGenerator.getInstance();
                long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
                try {
                    sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                } catch (ISOException e) {
                    throw new CMFault(CMFault.FAULT_EXTERNAL);
                }
            }
            msg.setAttribute(Fields.EXIST, exist);
            msg.setAttribute(Fields.SESSION_ID, sessionId.trim());
            msg.setAttribute(Fields.MESSAGE_ID, msgID);

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Params.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }
    }
}