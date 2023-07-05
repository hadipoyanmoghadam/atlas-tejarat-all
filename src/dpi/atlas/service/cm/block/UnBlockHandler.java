package dpi.atlas.service.cm.block;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.log.Block;
import dpi.atlas.model.entity.log.RowOfBlock;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.service.seq.SequenceGenerator;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: Jun 9, 2020
 * Time: 13:25 PM
 */
public class UnBlockHandler implements Configurable {
    private static Log log = LogFactory.getLog(UnBlockHandler.class);

    boolean getFromCash;
    HostInterface hostInterface;

    private Connector connector;


    public String handleCFS(Block block, RowOfBlock blockRow, String blockNo) throws Exception {

        try {
            String sessionId = "";
            String userId = "";

            SequenceGenerator sg = SequenceGenerator.getInstance();
            long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
            try {
                sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
            } catch (ISOException e) {
                log.error("Can not zeropad sessionId  = '" + sessionId + "' in UnBlockHandler.HandleCFS() : " + e.getMessage());
            }
            try {
                userId = ISOUtil.zeropad(blockRow.getUserId().trim(), 6);
            } catch (ISOException e) {
                log.error("Can not zeropad userId  = '" + userId + "' in UnBlockHandler.HandleCFS() : " + e.getMessage());
            }

            CMCommand command = new CMCommand();
            Batch batch = ChannelFacadeNew.getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND, getFromCash);
            command.addParam(CFSConstants.CURRENT_BATCH, batch.getBatchPk().toString());
            command.addHeaderParam(Fields.MESSAGE_TYPE, TJCommand.CMD_FINANCIAL_GROUP_UNBLOCK);
            command.addHeaderParam(Fields.SERVICE_TYPE, Constants.BLOCK_SERVICE);
            command.addHeaderParam(Fields.COMMAND, TJCommand.CMD_FINANCIAL_GROUP_UNBLOCK);
            command.addHeaderParam(Fields.SESSION_ID, sessionId);
            command.addHeaderParam(Fields.MESSAGE_ID, "01");
            command.addParam(Fields.REQUEST_DATE,DateUtil.getSystemDate());
            command.addParam(Fields.REQUEST_TIME,DateUtil.getSystemTime());
            command.addParam(Fields.BRANCH_CODE, userId); //user set az brachCode
            command.addParam(Fields.MN_RRN, "");
            command.addParam(Fields.TERMINAL_ID, "");
            command.addParam(Fields.USER_ID, userId);
            command.addParam(Fields.AMOUNT, blockRow.getAmount());
            command.addParam(Fields.BLOCK_ROW, blockNo);
            command.addParam(Fields.ACCOUNT_STATUS, Constants.UNBLOCK_ACCOUNT_STATUS);
            command.setCommandName(TJCommand.CMD_FINANCIAL_GROUP_UNBLOCK);
            command.addParam(Fields.SRC_ACCOUNT, blockRow.getAccountNo().trim());
            command.addParam(Fields.DOCUMENT_DESCRIPTION, "" != blockRow.getUnblockDesc() ? blockRow.getUnblockDesc() : "blank");
            command.addParam(Fields.BLCK_DATE,block.getUnblockDate());
            command.addParam(Fields.BLCK_TIME,DateUtil.getSystemTime());


            CMMessage msg = new CMMessage();
            msg.setAttribute(CMMessage.REQUEST, command);

            String strReply = (String) connector.sendSyncText(command);

            if (strReply == null) {
                log.error("Reply :: null (TimeOut) , command = " + command.toString());
                return ActionCode.TIME_OUT;
            }

            // **** reply != null ******

            CMResultSet result = new CMResultSet(strReply);
            msg.setAttribute(CMMessage.RESPONSE, result);
            CMCommand command1 = new CMCommand(result.getRequest());
            msg.setAttribute(CMMessage.REQUEST, command1);
            String action_code = result.getHeaderField(Fields.ACTION_CODE);


            if (action_code == null) {
                return ActionCode.TIME_OUT;
            }

            if (log.isDebugEnabled())
                log.debug("************   action_code = " + action_code);

            return action_code;

        } catch (NotFoundException e) {
            log.error("Error in UnBlockHandler.HandleCFS()!!BatchPk= NOT FOUND!!!!!! ");
            return ActionCode.ACTIVE_BATCH_NOT_FOUND;
        } catch (Exception e) {
            log.error("Exception in BlockHandler.HandleCFS()!!::" + e);
            return ActionCode.TIME_OUT;
        }
    }


    public String handleFaragir(Block block, RowOfBlock blockRow, String blockNo) throws CMFault {

        String sessionId = "";
        String userId = "";
        String branchCode = "";
        SequenceGenerator sg = SequenceGenerator.getInstance();
        long sequenceNumber = sg.getNextNumberInSequence("BranchSessionId");
        try {
            sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
        } catch (ISOException e) {
            log.error("Can not zeropad sessionId  = '" + sessionId + "' in UnBlockHandler.HandelFaragir() : " + e.getMessage());
        }
        try {
            userId = ISOUtil.zeropad(blockRow.getUserId(), 6);
        } catch (ISOException e) {
            log.error("Can not zeropad userId  = '" + userId + "' in UnBlockHandler.HandelFaragir() : " + e.getMessage());
        }
        try {
            branchCode = ISOUtil.zeropad(blockRow.getBranchCode(), 5);
        } catch (ISOException e) {
            log.error("Can not zeropad branchCode  = '" + branchCode + "' in UnBlockHandler.HandelFaragir() : " + e.getMessage());
        }

        CMMessage msg = new CMMessage();
        msg.setAttribute(Fields.COMMAND, TJCommand.CMD_FINANCIAL_GROUP_UNBLOCK);
        msg.setAttribute(Fields.REFRENCE_NUMBER, "");
        msg.setAttribute(Fields.MESSAGE_ID, "01");
        msg.setAttribute(Fields.SESSION_ID, sessionId);
        msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.BLOCK_SERVICE);
        msg.setAttribute(Fields.AMOUNT, blockRow.getAmount());
        msg.setAttribute(Fields.BLOCK_ROW, blockNo);
        msg.setAttribute(Fields.BLCK_DATE,block.getUnblockDate());
        msg.setAttribute(Fields.BLCK_TIME, DateUtil.getSystemTime());
        msg.setAttribute(Fields.DESC, "" != blockRow.getUnblockDesc() ? blockRow.getUnblockDesc() : "blank");
        msg.setAttribute(Fields.USER_ID, userId);
        msg.setAttribute(Fields.BRANCH_CODE, branchCode);
        msg.setAttribute(Fields.ACCOUNT_NO, blockRow.getAccountNo());
        msg.setAttribute(Fields.ACCOUNT_STATUS, Constants.UNBLOCK_ACCOUNT_STATUS);

        String currentBatch = "";
        try {

            Batch batch = ChannelFacadeNew.getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND, getFromCash);
            currentBatch = batch.getBatchPk().toString();
        } catch (Exception e) {
            log.debug("error in BlockHandler.HandelFaragir()!!getBatch>>>" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACTIVE_BATCH_NOT_FOUND);
            return ActionCode.ACTIVE_BATCH_NOT_FOUND;

        }

        msg.setAttribute(CFSConstants.CURRENT_BATCH, currentBatch);


        HashMap inputParams = new HashMap();


        inputParams.put(Constants.SESSION_ID, "000000000000");

        String service = (String) msg.getAttribute(CMMessage.SERVICE_TYPE);

        String customerId = "";
        String customerPass = "";
        String client_id = "";

        inputParams.put(Constants.SERVICE, service);
        inputParams.put(Constants.CUSTOMER_ID, customerId);
        inputParams.put(Constants.CUSTOMER_PASS, customerPass);
        inputParams.put(Constants.CLIENT_ID, client_id);

        msg.setAttribute(Constants.INPUT_PARAMS, inputParams);

        String beanName = "tj.host.post.block";
        HostResultSet hrs;
        String actionCode;
        try {
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            Map holder = new HashMap();
            holder.put("hostInterface", hostInterface);
            //TODO it seems there is no need to holder, probably signature and body of host.post should be changed
            hrs = host.post(msg, holder, hostInterface);

        } catch (CMFault e) {
            int errorCode = Integer.parseInt(e.getMessage());
            actionCode = getActionCode(errorCode, msg.getAttributeAsString(Fields.COMMAND_NAME));
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, actionCode);

        } catch (Exception ef) {
            log.error(ef);
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
        }

        actionCode = (hrs.getDataHeaderField(Fields.ACTION_CODE) != null ? hrs.getDataHeaderField(Fields.ACTION_CODE) : ActionCode.TIME_OUT);

        return actionCode;
    }

    public String getActionCode(int errorCode, String commandName) throws CMFault {
        String actionCode = ActionCode.TIME_OUT;

        if (errorCode == HostException.CONNECTION_ERROR)
            actionCode = ActionCode.GENERAL_DATA_ERROR;
        else if (errorCode == HostException.EMPTY_RESULTSET)
            actionCode = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;
        else if (errorCode == HostException.UNKNOWN_ERROR)
            actionCode = ActionCode.SYSTEM_NOT_AVAILABLE;
        else if (errorCode == HostException.CONNECTION_TIMEOUT)
            actionCode = ActionCode.TIME_OUT;
        else if (errorCode == HostException.ACCOUNT_MISSING)
            actionCode = ActionCode.ACCOUNT_HAS_NO_CUST_INFO;

        return actionCode;

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {


        String host_agent_name = cfg.get("host-agent");
        HostAgent hostAgent = ChannelManagerEngine.getInstance().getHostAgent(host_agent_name);
        hostInterface = hostAgent.getHostInterface();

        String connector_name = cfg.get("cfs-connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);


        String getBatchFromCash = cfg.get("getBatchFromCash");
        getFromCash = getBatchFromCash.equals("yes");


    }
}
