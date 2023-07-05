package branch.dpi.atlas.service.cm.handler.saf;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.entity.log.SAFLog;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.quartz.BaseQuartzJob;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.FormatException;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.*;

/**
 * SAF2Job class
 *
 * @author <a href="mailto:Behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.2 $ $Date: 2019/24/07 13:36:08 $
 */
public class SAF2Job extends BaseQuartzJob {
    private static Log log = LogFactory.getLog(SAF2Job.class);

    ArrayList ACTIONCODE_TO_BE_CONSIDERED_ARRAY = new ArrayList();
    ArrayList ACTIONCODE_TO_BE_CONSIDERED_AFTER_MAXTRYCOUNT_ARRAY = new ArrayList();
    private int maxTryCount = Integer.MAX_VALUE;
    private int pickNum = Integer.MAX_VALUE;
    private int ERROR_HANDLED = 99;
    boolean init_flag = false;
    private Configuration cfg;

    int sendToCore = SOURCE_CORE;
    static final int Defualt_CORE = 0;
    static final int SOURCE_CORE = 1;
    static final int DESTINATION_CORE = 2;
    static final int Exception_SAFPriority = 4;

    BR_SAFLogHandler safLogHandler;
    boolean getFromCash;

    public int getPickNum() {
        return pickNum;
    }

    public void setPickNum(int pickNum) {
        this.pickNum = pickNum;
    }

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    }
    public void doWork() throws Exception {

        try {
            Collection col = findAllSAFLogByPriority();
            log.debug("SAF Unhandled Size = " + col.size());

            String returned_actionCode = "";
            int i = 1;
            for (Iterator it = col.iterator(); it.hasNext() && (i <= pickNum); i++) {
                SAFLog safLogMsg = (SAFLog) it.next();
                safLogMsg.setStartTime(System.currentTimeMillis());

                try {
                    if (safLogMsg.getSAFName().trim().equals(Constants.REVERSE_SAF_NAME)) {
                        returned_actionCode = handle(safLogMsg, Constants.SRC_HOST_ID);
                        safLogMsg.setActionCode(returned_actionCode);
                        if (returned_actionCode.startsWith("00") || returned_actionCode.startsWith(ActionCode.ALREADY_REVERSED) )
                            safLogMsg.setHandled(SAFLog.STATUS_HANDLED);
                        sendToCore = Defualt_CORE;

                    } else if (safLogMsg.getSAFName().trim().equals(Constants.FT_SAF_NAME) || safLogMsg.getSAFName().trim().equals(Constants.FTR_SAF_NAME)) {
                        returned_actionCode = handle(safLogMsg, Constants.DEST_HOST_ID);
                        safLogMsg.setActionCode(returned_actionCode);
                        if (returned_actionCode.startsWith("00"))
                            safLogMsg.setHandled(SAFLog.STATUS_HANDLED);
                        sendToCore = Defualt_CORE;
                    }

                    if (returned_actionCode.startsWith("*"))
                        safLogMsg.setHandled(SAFLog.STATUS_UNDELIVERED);

                } catch (Exception e) {
                    log.error("Exception in safLog>>>>" + e.getMessage() + "Session_id::" + safLogMsg.getSessionId());
                    safLogMsg.setMsgPriority(safLogMsg.getMsgPriority() + 100);
                    safLogMsg.setSAFPriority(Exception_SAFPriority);
                }
                if (!safLogMsg.isHandledNow()) {
                    determineSafLogHandle(safLogMsg);
                }
                safLogMsg.incTryCount();
                safLogMsg.setLastTryDate(new Timestamp(System.currentTimeMillis()));
                updateSAFLog(safLogMsg);

            }
        } catch (Exception e) {
            log.fatal("***Exception in safLog>>can not fetch record!!!**" + e.getMessage());
            throw new ModelException(e);
        }
    }

    private void determineSafLogHandle(SAFLog safLogMsg) {
        try {
            int maxtry = -1;
            if (safLogMsg.getMaxTryCount() != 0) {
                if (safLogMsg.getTryCount() >= safLogMsg.getMaxTryCount())
                    maxtry = safLogMsg.getMaxTryCount();
            } else if (safLogMsg.getTryCount() >= maxTryCount)
                maxtry = maxTryCount;

            if (maxtry != -1) {
                if (log.isInfoEnabled()) log.info("Max try count has been reached: MaxTryCount=" + maxtry);
                if (sendToCore == SOURCE_CORE)
                    safLogMsg.setHandled(SAFLog.STATUS_NOTAPPROVED);
                else
                    safLogMsg.setHandled(SAFLog.STATUS_UNDELIVERED);
            }
        } catch (Exception e) {
            log.error("error in determineSafLogHandle" + e.getMessage());
        }
    }

    public String handle(SAFLog saf_msg, String hostName) throws Exception {

        CMCommand command = new CMCommand(saf_msg.getMsgString());
        if (command.getParam(hostName).equals(Constants.HOST_ID_CFS))
            return safLogHandler.handleCFS(saf_msg);
        else if (command.getParam(hostName).equals(Constants.HOST_ID_FARAGIR)) {
            CMMessage msg = prepareInputCMMessage(command, saf_msg);
            return safLogHandler.handleFaragir(msg);
        } else
            return "";
    }

    private CMMessage prepareInputCMMessage(CMCommand command, SAFLog saf_msg) throws Exception {
        CMMessage msg = new CMMessage();
        msg.setAttribute(Fields.SESSION_ID, command.getHeaderParam(Fields.SESSION_ID));
        msg.setAttribute(Fields.PIN,command.getParam(Fields.PIN));
        msg.setAttribute(CMMessage.SERVICE_TYPE, command.getHeaderParam(Fields.SERVICE_TYPE));
        msg.setAttribute(Constants.SRC_HOST_ID, command.getParam(Constants.SRC_HOST_ID));
        msg.setAttribute(Constants.DEST_HOST_ID, command.getParam(Constants.DEST_HOST_ID));
        String destAccNo = command.getParam(Fields.DEST_ACCOUNT);
        if (destAccNo != null && !destAccNo.equals("")) {
            if (destAccNo.length() > 13)
                destAccNo = destAccNo.substring(0, 13);
        }
        command.addParam(Fields.DEST_ACCOUNT, destAccNo);
        if (command.getParam(Fields.DOCUMENT_DESCRIPTION) == null)
            command.addParam(Fields.DOCUMENT_DESCRIPTION, "");
        String currentBatch = "";
        try {
            Batch batch = ChannelFacadeNew.getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND, getFromCash);
            currentBatch = batch.getBatchPk().toString();
        } catch (Exception e) {
            log.debug("error in getBatch>>>" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACTIVE_BATCH_NOT_FOUND);
            saf_msg.setActionCode(ActionCode.ACTIVE_BATCH_NOT_FOUND);
            throw e;
        }
        command.addParam(CFSConstants.CURRENT_BATCH, currentBatch);
        msg.setAttribute(CFSConstants.CURRENT_BATCH, currentBatch);
        msg.setAttribute(CMMessage.REQUEST, command);

        return msg;
    }

    private void updateSAFLog(SAFLog safLog) throws FormatException, SQLException {

        try {

            ChannelFacadeNew.updateSafLogBR(safLog);

        } catch (SQLException e) {
            log.error("error in updateSAFLog>>" + e);
            throw e;
        }
    }

    private Collection findAllSAFLogByPriority() throws SQLException {
        List col = new ArrayList();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
            if (part <= 0)
                part = CFSConstants.PARTITION_SIZE + part;

//            int partMinusOne = (part - 1);
//            if (partMinusOne <= 0) {
//                partMinusOne = CFSConstants.PARTITION_SIZE + partMinusOne;
//            }
//
//            int partMinusTwo = (part - 2);
//            if (partMinusTwo <= 0) {
//                partMinusTwo = CFSConstants.PARTITION_SIZE + partMinusTwo;
//            }
//
//            int partMinusThree = (part - 3);
//            if (partMinusThree <= 0) {
//                partMinusThree = CFSConstants.PARTITION_SIZE + partMinusThree;
//            }
//
//            int partMinusFour = (part - 4);
//            if (partMinusFour <= 0) {
//                partMinusFour = CFSConstants.PARTITION_SIZE + partMinusFour;
//            }

//            String hql = "select * from tbSAFLogBR where partno in (" + part + ", " + partMinusOne + ", " + partMinusTwo + ", " + partMinusThree + ", " + partMinusFour + ") and ( HANDLED = 0 or HANDLED = 3 ) order by TRYCOUNT,SAFPRIORITY,MSGPRIORITY,SEQUENCER fetch first " + pickNum + " rows only   with ur";
            String hql = "select * from tbSAFLogBR where partno="+ part + " and (SAFNAME='TJ_BR_FT' or SAFNAME='TJ_BR_REV') and ( HANDLED = 0 or HANDLED = 3 ) order by TRYCOUNT,SAFPRIORITY,MSGPRIORITY,SEQUENCER fetch first " + pickNum + " rows only   with ur";

            connection = ChannelFacadeNew.getDbConnectionPool().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            while (resultSet.next()) {
                SAFLog safLog = new SAFLog();
                safLog.setSequencer(resultSet.getLong("SEQUENCER"));
                safLog.setPartNo(new Integer(resultSet.getInt("PARTNO")));
                safLog.setSAFName(resultSet.getString("SAFNAME"));
                safLog.setInsertDate(resultSet.getTimestamp("INSERTDATE"));
                safLog.setMsgString(resultSet.getString("MSGSTRING"));
                safLog.setSAFPriority(resultSet.getInt("SAFPRIORITY"));
                safLog.setMsgPriority(resultSet.getInt("MSGPRIORITY"));
                safLog.setTryCount(resultSet.getInt("TRYCOUNT"));
                safLog.setLastTryDate(resultSet.getTimestamp("LASTTRYDATE"));
                safLog.setHandled(resultSet.getInt("HANDLED"));
                safLog.setWaitTime(resultSet.getInt("HOST_ID"));
                safLog.setSessionId(resultSet.getString("SESSION_ID"));
                safLog.setMessageId(resultSet.getString("MESSAGE_ID"));
                col.add(safLog);
            }

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            ChannelFacadeNew.getDbConnectionPool().returnConnection(connection);

        }

        return col;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        this.cfg = cfg;

        maxTryCount = Integer.parseInt(cfg.get("max-try-count"));
        pickNum = Integer.parseInt(cfg.get("pick-num"));
        String getBatchFromCash = cfg.get("getBatchFromCash");
        getFromCash = getBatchFromCash.equals("yes");

        String actioncodeToBeConsidered = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED);
        ACTIONCODE_TO_BE_CONSIDERED_ARRAY = CMUtil.tokenizString(actioncodeToBeConsidered, ",");

        String actioncodeToBeConsideredAfterMaxTryCount = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED_AFTER_MAXTRYCOUNT);
        ACTIONCODE_TO_BE_CONSIDERED_AFTER_MAXTRYCOUNT_ARRAY = CMUtil.tokenizString(actioncodeToBeConsideredAfterMaxTryCount, ",");


        safLogHandler = new BR_SAFLogHandler();
        safLogHandler.setConfiguration(cfg);
    }

}
