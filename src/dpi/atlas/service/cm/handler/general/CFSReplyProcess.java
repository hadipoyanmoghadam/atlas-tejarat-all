package dpi.atlas.service.cm.handler.general;

import dpi.atlas.model.entity.log.SAFLog;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;


public class CFSReplyProcess extends TJServiceHandler implements Configurable {

    private Map convertToReversal = new HashMap();

    public CFSReplyProcess() {
        convertToReversal.put(TJCommand.CMD_CASH_REQUEST, TJCommand.CMD_CASH_REQUEST_REVERSAL);
        convertToReversal.put(TJCommand.CMD_CASH_WITHDRAWAL, TJCommand.CMD_CASH_WITHDRAWAL_REVERSAL);
        convertToReversal.put(TJCommand.CMD_LORO, TJCommand.CMD_LORO_REVERSAL);
        convertToReversal.put(TJCommand.CMD_FAST_CASH, TJCommand.CMD_FAST_CASH_REVERSAL);
        convertToReversal.put(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL, TJCommand.CMD_NETWORK_CASH_WITHDRAWAL_REVERSAL);
        convertToReversal.put(TJCommand.CMD_NETWORK_FAST_CASH_REVERSAL, TJCommand.CMD_NETWORK_FAST_CASH_REVERSAL);
        convertToReversal.put(TJCommand.CMD_SALE_REQUEST, TJCommand.CMD_SALE_REQUEST_REVERSAL);
        convertToReversal.put(TJCommand.CMD_SALE_WITH_CASH_REQUEST_REVERSAL, TJCommand.CMD_SALE_WITH_CASH_REQUEST_REVERSAL);
        convertToReversal.put(TJCommand.CMD_TRANSFER_FUND_REVERSAL, TJCommand.CMD_TRANSFER_FUND_REVERSAL);
    }


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand cmCommand = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        try {
            String reply = (String) holder.get("connector-reply");
            CMResultSet result = null;
            if (reply != null) {
                result = new CMResultSet(reply);
            } else {
                if (log.isInfoEnabled()) log.info("timeout expired...");
                result = new CMResultSet();
                result.setHeaderField(Fields.ACTION_CODE, ActionCode.TIME_OUT);
                result.setHeaderField(Fields.ACTION_MESSAGE, "no response from CFS whitin timeout");
//                putReversalInSAF(cmCommand);
            }

            if (log.isInfoEnabled()) log.info("result = " + result);

//            if (result.getHeaderField(Fields.ACTION_CODE).equals(ActionCode.SYSTEM_MALFUNCTION))
//                putReversalInSAF(cmCommand);

            msg.setAttribute(CMMessage.RESPONSE, result);

            if (result.getHeaderField(Fields.ACTION_CODE).startsWith("00"))
                msg.setAttribute(Fields.APPROVED, "0");
            else if (result.getHeaderField(Fields.ACTION_CODE).startsWith(ActionCode.ALREADY_REVERSED))
                msg.setAttribute(Fields.APPROVED, "0");
            else if (cmCommand.getCommandName().startsWith(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ) && result.getHeaderField(Fields.ACTION_CODE).startsWith(ActionCode.REVERSE_HAS_NO_ORIGINAL))
                msg.setAttribute(Fields.APPROVED, "0");
            else msg.setAttribute(Fields.APPROVED, "1");


            msg.setAttribute(Fields.COMMAND, cmCommand.getCommandName());

            msg.setAttribute(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);

            String destAccount = result.getHeaderField(Fields.DEST_ACCOUNT);

            if (destAccount != null && !destAccount.equals(""))
                msg.setAttribute(Fields.DEST_ACCOUNT, result.getHeaderField(Fields.DEST_ACCOUNT));
            else
                msg.setAttribute(Fields.DEST_ACCOUNT, ISOUtil.zeropad("0", 13));

            String current_batch = result.getHeaderField(CFSConstants.CURRENT_BATCH);

            if (current_batch != null)
                msg.setAttribute(CFSConstants.CURRENT_BATCH, current_batch);
            else
                msg.setAttribute(CFSConstants.CURRENT_BATCH, "00000"); //todo: change this to give current batch every times.....(this may be null if CFS return 'request' message if it is duplicate or etc.)
           msg.setAttribute("cfsSideDone", "APPROVED");
        } catch (Exception e) {
            log.error(e);
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, dpi.atlas.service.cm.iso2000.ActionCode.GENERAL_ERROR);
            result.setHeaderField(Params.ACTION_MESSAGE, e.getMessage());
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        }
    }

    private void putReversalInSAF(CMCommand cmCommand) throws SQLException {
        // Generate Reversal Message and store it if the transacation is financial
        String commandName = cmCommand.getCommandName();
        if (convertToReversal.containsKey(commandName)) {
            cmCommand.setCommandName((String) convertToReversal.get(commandName));
            cmCommand.addParam(Fields.FORCE_POST, "1");
            cmCommand.addHeaderParam(Fields.MESSAGE_ID, "3");

            SAFLog safLog = new SAFLog();
            safLog.setHandled(SAFLog.STATUS_UNHANDLED);
            safLog.setInsertDate(new java.sql.Timestamp(System.currentTimeMillis()));
            safLog.setMsgPriority(1);
            safLog.setMsgString(cmCommand.toString());
            safLog.setSAFName("TJ_SPW_REV");
            safLog.setSAFPriority(1);
            safLog.setTryCount(0);
            safLog.setWaitTime(new Integer(Constants.DEFAULT_SAF_WAIT_TIME).intValue());
//            switchFacade.insertSAFLog(safLog);
//            ChannelFacadeNew.insertSAFLog(safLog.getSAFName(),safLog.getMsgString(),safLog.getSAFPriority(),safLog.getMsgPriority(),safLog.getTryCount(),safLog.getHandled(),safLog.getWaitTime(),safLog.getSessionId(),safLog.getMessageId(),"",safLog.getActionCode());
            //TODO msgType must be improved
//            ChannelFacadeNew.insertSAFLog(safLog.getSAFName(),safLog.getMsgString(),safLog.getSAFPriority(),safLog.getMsgPriority(),safLog.getTryCount(),safLog.getHandled(),safLog.getWaitTime(),safLog.getSessionId(),safLog.getMessageId(),"8",safLog.getActionCode());
        }
    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }


}
