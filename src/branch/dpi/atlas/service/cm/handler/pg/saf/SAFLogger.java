package branch.dpi.atlas.service.cm.handler.pg.saf;

import branch.dpi.atlas.service.cm.handler.saf.SAFLoggerBase;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.common.CMConstants;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.general.HostGenerateRefNo;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Nasiri .
 * Date: April 3, 2022
 * Time: 04:02 PM
 */
public class SAFLogger extends SAFLoggerBase {

    private static Log log = LogFactory.getLog(SAFLogger.class);
    private Integer waitTime = Integer.valueOf(Constants.DEFAULT_SAF_WAIT_TIME);
    private String safName;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            CMCommand cmCommand = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String commandName = msg.getAttributeAsString(CMMessage.COMMAND);
            cmCommand.addHeaderParam(CMMessage.COMMAND, commandName);

            String messageType = cmCommand.getCommandName();


            String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
            String msgId = msg.getAttributeAsString(Fields.MESSAGE_ID);
            String actionCode = Constants.REQUEST_ACTION_CODE;
            String srcAccount = cmCommand.getParam(Fields.SRC_ACCOUNT);
            String destAccount = cmCommand.getParam(Fields.DEST_ACCOUNT);
            String hostId = cmCommand.getParam(Constants.DEST_HOST_ID);
            String channelId = cmCommand.getParam(Fields.TERMINAL_ID);

            if (safName.equals("TJ_STK_REV")) {
                messageType = TJCommand.CMD_DEPOSIT_STOCK_REVERSE_PG;
                cmCommand.addHeaderParam(Fields.MESSAGE_TYPE, messageType);
                cmCommand.addParam(Fields.PIN, Constants.STOCK_DEPOSIT_REVERSE_MSG_TYPE);
                cmCommand.setCommandName(TJCommand.CMD_DEPOSIT_STOCK_REVERSE_PG);
                cmCommand.addHeaderParam(Fields.COMMAND, TJCommand.CMD_DEPOSIT_STOCK_REVERSE_PG);
                String origMessageData = cmCommand.getParam(Fields.MN_RRN) + cmCommand.getParam(Fields.DATE) + cmCommand.getParam(Fields.TIME);
                cmCommand.addParam(Fields.ORIG_MESSAGE_DATA, origMessageData);
                cmCommand.addParam(Fields.ISREVERSED, "1");

                hostId = cmCommand.getParam(Constants.SRC_HOST_ID);


                HostGenerateRefNo hostGenerateRefNo = new HostGenerateRefNo();
                hostGenerateRefNo.doProcess(msg, holder);

                String sgbTxCode;
                try {
                    sgbTxCode = ChannelFacadeNew.getTxTypeSgbTxCode(messageType);
                } catch (NotFoundException e) {
                    log.error("SGB Tx Code does not found for tx " + messageType);
                    sgbTxCode = CMConstants.SGB_TX_CODE_UNDEFINED;
                }
                if (sgbTxCode == null || "".equals(sgbTxCode.trim())) {
                    log.error("SGB Tx Code is null for tx " + messageType);
                    sgbTxCode = CMConstants.SGB_TX_CODE_UNDEFINED;
                }

                if (log.isDebugEnabled()) log.debug("sgbTxCode =" + sgbTxCode);
                cmCommand.addParam(Fields.OPERATION_CODE, sgbTxCode);
                cmCommand.addParam(Fields.SGB_TX_CODE, sgbTxCode.trim());

                if (msgId != null && !msgId.equals("")) {
                    if (((Integer.parseInt(msgId) % 2) != 0))
                        msgId = String.valueOf(Integer.parseInt(msgId) + 2);
                    else
                        msgId = String.valueOf(Integer.parseInt(msgId) + 1);

                    msgId = ISOUtil.padleft(msgId, 2, '0');

                    cmCommand.addHeaderParam(Fields.MESSAGE_ID, msgId);
                }
            }

            String msgString = cmCommand.toString();

            if (sessionId.equalsIgnoreCase("") || msgId.equalsIgnoreCase("")) {
                log.error(" !!!!!!!!!Warning ::: sessionId  =>" + sessionId + " -- msgId =>" + msgId + "<  -- msgString =" + msgString);
            }

            ChannelFacadeNew.insertSAFLogBranch(safName, msgString, Integer.parseInt(hostId), sessionId, msgId, messageType, actionCode, channelId, srcAccount, destAccount);

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.FUND_TRANSFER_MUST_BE_REVERSED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.FUND_TRANSFER_MUST_BE_REVERSED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        safName = cfg.get("safName");
        if (safName == null) {
            ConfigurationException ce = new ConfigurationException("No safe name found referred by 'safName' property, name");
            log.error(ce);
            throw ce;
        }


    }

}

