package branch.dpi.atlas.service.cm.job;

import branch.dpi.atlas.util.FarsiUtil;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.quartz.BaseQuartzJob;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.general.RefNoGeneratorSharing;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import hit.db2.Db2DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

/**
 * Created by R.Nasiri
 * Date: Jan 16, 2021
 * Time: 02:54 PM
 */
public class FeeSharingJob extends BaseQuartzJob {
    private static Log log = LogFactory.getLog(FeeSharingJob.class);
    protected Map<String, String> branchRTGSAccounts = new HashMap<String, String>();
    protected Map<String, String> branchACHAccounts = new HashMap<String, String>();
    protected String achOpCode;
    protected String rtgsOpCode;
    protected int blockSize = 100;
    static Map<Integer, Long> batchMap = new HashMap<Integer, Long>();
    Db2DataSource faraDataSource = null;
    private String driver = "hit.db2.Db2Driver";
    private String url = "";
    private String user = "";
    private String password = "";

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String size = (cfg.get("blockSize"));
        if (size != null && !size.trim().equalsIgnoreCase(""))
            blockSize = Integer.parseInt(size);
        url = cfg.get("url");
        faraDataSource = (Db2DataSource) AtlasModel.getInstance().getBean("SGBConfig");
        user = faraDataSource.getUser();
        password = faraDataSource.getPassword();
        String ip = faraDataSource.getServerName();
        String rdbname = faraDataSource.getDatabaseName();
        String port = String.valueOf(faraDataSource.getPortNumber());
        url = "jdbc:db2://" + ip + ":" + port + ";rdbname=" + rdbname + ";ccsid=1252";
    }

    public static Map<Integer, Long> getBatchMap() {
        return batchMap;
    }

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection selectConnection = null;
        Connection connection = null;
        try {
            log.error("Start FeeSharing job at " + DateUtil.getSystemTime());

            selectConnection = ChannelFacadeNew.getDbConnectionPool().getConnection();
            connection = ChannelFacadeNew.getDbConnectionPool().getConnection();

            readFara(connection);
            doSharing(connection, selectConnection);
            doReverse(connection, selectConnection);


            log.error("FeeSharingJob Done at " + DateUtil.getSystemTime());
        } catch (Exception e) {
            log.error("FeeSharingJob Done with Error at " + DateUtil.getSystemTime());
            log.error("Exception in FeeSharingJob_mainRoutine>> " + e.getMessage());
            try {
                connection.rollback();
                selectConnection.rollback();
            } catch (SQLException e1) {
                log.error("Exception in FeeSharingJob_rollback>> " + e.getMessage());
            }
        } finally {
            try {
                ChannelFacadeNew.getDbConnectionPool().returnConnection(connection);
                ChannelFacadeNew.getDbConnectionPool().returnConnection(selectConnection);
            } catch (Exception e) {
                log.error("Exception in FeeSharingJob_finally>> " + e.getMessage());
            }
        }
    }

    public void readFara(Connection connection) throws Exception {

        Connection faraConnection = null;
        Connection selectFaraConnection = null;
        Statement selectStm = null;
        ResultSet selectRs = null;
        PreparedStatement feeLog_ps = null;
        PreparedStatement sgbFeeLog_ps = null;
        String src_account_no = "";
        String dest_account_no = "";
        long amount = 0;
        long origAmount = 0;
        long logId = 0;
        String txSrc = "";
        String branchCode = "";
        String txDate = "";
        String txTime = "";
        String createDate = "";
        String createTime = "";
        String transType = "";
        String isReverse = "";
        String isReversed = "";
        String trackCode = "";
        String fileName = "";
        String destIBAN = "";
        String branch_docNo = "";
        long count = 0;
        long rowProcess = 0;

        try {

            log.error("readFara() Start:: " + DateUtil.getSystemTime());

            faraConnection = createConnection();
            selectFaraConnection = createConnection();

            selectStm = selectFaraConnection.createStatement();

            String sql = "Insert into tb_fee_log " +
                    "(SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,TX_SRC,BRANCH_CODE,FEE_AMOUNT,TX_DATE,TX_TIME,CREATION_DATE," +
                    "CREATION_TIME,TRANSACTION_TYPE,HOSTID,IS_REVERSAL_TXN, " +
                    "AMOUNT,ISREVERSED,TRACK_CODE,FILE_NAME,DEST_IBAN,BRANCH_DOCNO" +
                    ") values(?,?,?,?,?,?,?,?,?,?,'3',?,?,?,?,?,?,?)";
            feeLog_ps = connection.prepareStatement(sql);
            sql = "update SGBSUSA.tb_fee_Log set is_done = '1' where log_id = ? ";
            sgbFeeLog_ps = faraConnection.prepareStatement(sql);


            String selectString = "select LOG_ID,SRLACCS,SRLACCD,TX_SRC,INBRANCH,FEE_AMOUNT,TRANSDATE,ACTTIME," +
                    "CREATDATE,CREATTIME,TRANSACTION_TYPE,IS_REVERSAL_TXN," +
                    "AMOUNT,ISREVERSED,TRACK_CODE,FILE_NAME,DEST_IBAN,SERIAL" +
                    " from sgbsusa.tb_fee_log where IS_DONE='0' with ur";
            selectRs = selectStm.executeQuery(selectString);
            while (selectRs.next()) {
                count++;

                logId = selectRs.getLong("LOG_ID");
                src_account_no = selectRs.getString("SRLACCS");
                src_account_no = ISOUtil.padleft(src_account_no, 13, '0');
                dest_account_no = selectRs.getString("SRLACCD");
                dest_account_no = ISOUtil.padleft(dest_account_no, 13, '0');
                txSrc = selectRs.getString("TX_SRC");
                branchCode = selectRs.getString("INBRANCH");
                branchCode = ISOUtil.padleft(branchCode, 5, '0');
                amount = selectRs.getLong("FEE_AMOUNT");
                txDate = selectRs.getString("TRANSDATE");
                txTime = selectRs.getString("ACTTIME");
                txTime = ISOUtil.padleft(txTime.substring(0, txTime.length() - 2), 6, '0');
                createDate = selectRs.getString("CREATDATE");
                createTime = selectRs.getString("CREATTIME");
                createTime = ISOUtil.padleft(createTime.substring(0, createTime.length() - 2), 6, '0');
                transType = selectRs.getString("TRANSACTION_TYPE");
                isReverse = selectRs.getString("IS_REVERSAL_TXN");
                origAmount = selectRs.getLong("AMOUNT");
                isReversed = selectRs.getString("ISREVERSED");
                trackCode = selectRs.getString("TRACK_CODE");
                fileName = selectRs.getString("FILE_NAME");
                destIBAN = selectRs.getString("DEST_IBAN");
                branch_docNo = selectRs.getString("SERIAL");
                branch_docNo = ISOUtil.padleft(branch_docNo, 7, '0');

                saveFeeLogFromFARA(feeLog_ps, src_account_no, dest_account_no, txSrc, amount, branchCode, txDate, txTime,
                        createDate, createTime, transType, isReverse, origAmount, isReversed, trackCode, fileName, destIBAN, branch_docNo);


                sgbFeeLog_ps.setLong(1, logId);
                sgbFeeLog_ps.addBatch();

                if (count == blockSize) {
                    try {
                        feeLog_ps.executeBatch();
                        sgbFeeLog_ps.executeBatch();
                        feeLog_ps.clearBatch();
                        sgbFeeLog_ps.clearBatch();
                        connection.commit();
                        faraConnection.commit();
                        rowProcess += count;
                        count = 0;
                        log.error("Row insert::" + rowProcess + " records from FARAGIR. Last Log_ID::" + logId);
                    } catch (BatchUpdateException e) {
                        log.error("Exception1 in readFara()>> " + count + " records from FARAGIR. Last Log_ID::" + logId);
                        log.error("Exception1 in readFara()>> " + e.getNextException());
                        feeLog_ps.clearBatch();
                        sgbFeeLog_ps.clearBatch();
                        connection.rollback();
                        faraConnection.rollback();
                        count = 0;
                    } catch (Exception e) {
                        log.error("Exception2 in readFara()>> " + e.getMessage());
                        feeLog_ps.clearBatch();
                        sgbFeeLog_ps.clearBatch();
                        connection.rollback();
                        faraConnection.rollback();
                        count = 0;
                    }
                }
            }

            selectFaraConnection.commit();

            if (count > 0) {
                try {
                    feeLog_ps.executeBatch();
                    sgbFeeLog_ps.executeBatch();
                    feeLog_ps.clearBatch();
                    sgbFeeLog_ps.clearBatch();
                    connection.commit();
                    faraConnection.commit();
                    rowProcess += count;
                    log.error("Row insert::" + rowProcess + " from FARAGIR. Last Log_ID::" + logId);
                } catch (BatchUpdateException e) {
                    log.error("Exception3 in readFara()>> " + count + " records from FARAGIR. Last Log_ID::" + logId);
                    log.error("Exception3 in readFara()>> " + e.getNextException());
                    feeLog_ps.clearBatch();
                    sgbFeeLog_ps.clearBatch();
                    connection.rollback();
                    faraConnection.rollback();
                } catch (Exception e) {
                    log.error("Exception4 in readFara()>> " + e.getMessage());
                    feeLog_ps.clearBatch();
                    sgbFeeLog_ps.clearBatch();
                    connection.rollback();
                    faraConnection.rollback();
                }
            }
            log.error("readFara() Done at " + DateUtil.getSystemTime());
        } catch (Exception e) {
            log.error("Exception in readFara()>> " + e.getMessage());
            throw e;
        } finally {
            try {

                if (selectRs != null)
                    selectRs.close();
                if (selectStm != null)
                    selectStm.close();
                if (feeLog_ps != null)
                    feeLog_ps.close();
                if (sgbFeeLog_ps != null)
                    sgbFeeLog_ps.close();
                if (selectFaraConnection != null)
                    selectFaraConnection.close();
                if (faraConnection != null)
                    faraConnection.close();
            } catch (Exception e) {
                log.error("Exception in FeeSharingJob_finally>> " + e.getMessage());
            }
        }
    }

    public void doSharing(Connection connection, Connection selectConnection) throws Exception {

        Statement selectCmStm = null;
        ResultSet selectCmRs = null;
        Statement summerySt = null;
        ResultSet summeryRs = null;
        PreparedStatement cfstx_ps = null;
        PreparedStatement fee_log_ps = null;
        PreparedStatement sequenceSelect = null;
        PreparedStatement sequenceUpdate = null;
        Statement updateBranch = null;
        long sequence = 0;
        String src_account_no = "";
        String dest_account_no = "";
        String branchDocNo = "";
        String branchCode = "";
        long amount = 0;
        long logId = 0;
        String refNo = "";
        String session_id = "";
        String[] accountPercent = null;
        String branchRTGSAccount = "";
        String branchACHAccount = "";
        long percent = 0;
        long batchPk = 0;
        String operationCode = "";
        long count = 0;
        long rowProcess = 0;

        try {

            log.error("doSharing() Start at :: " + DateUtil.getSystemTime());

            selectCmStm = selectConnection.createStatement();
            summerySt = selectConnection.createStatement();

            String sql = "Insert into tbCFSTX " +
                    "(TX_PK,PARTNO,TX_SEQ,TX_CODE,TX_SRC," +
                    "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                    "CREATION_DATE,CREATION_TIME," +
                    "SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                    "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED," +
                    "IS_REVERSAL_TXN,SRC_ACC_BALANCE, " +
                    "SGB_ACTION_CODE,LAST_TRANS_LIMIT,DEST_ACC_BALANCE,SESSION_ID, " +
                    "HOSTCODE,EXTRA_INFO,DESC" +
                    ",CARD_NO,ACQUIRER,FEE_AMOUNT,TX_SEQUENCE_NUMBER,RRN,TERMINAL_ID" +
                    ",SGB_BRANCH_ID,BRANCH_DOCNO,TOTAL_DEST_ACCOUNT" +
                    ") values(?,?,?,?,'" + Constants.TXN_SRC_FEE + "'," +
                    "?,'364',?,?,?,?,'',?,?,?,?,0,0,'0',0,?,-1000000000000000000,0,?,'22',?,?" +
                    ",'" + Constants.BANKE_TEJARAT_BIN_NEW + "','" + Constants.BANKE_TEJARAT_BIN_NEW + "',0,'0','','',?,?,?)";
            cfstx_ps = connection.prepareStatement(sql);
            sql = "update tb_fee_Log set is_done = '1' where log_id = ? ";
            fee_log_ps = connection.prepareStatement(sql);
            sql = "select Sequencer from tbSequence where name = '" + Constants.FEE_SHARING_SESSION_ID + "' with ur";
            sequenceSelect = connection.prepareStatement(sql);
            sql = "update tbSequence set SEQUENCER = ?  where name = '" + Constants.FEE_SHARING_SESSION_ID + "' ";
            sequenceUpdate = connection.prepareStatement(sql);

            String extraInfo = "";
            byte[] descByte = {124, 65, 87, 88, 95};
            try {
                String s = new String(descByte, "ISO-8859-1");
                extraInfo = FarsiUtil.convertWindows1256(descByte);
            } catch (UnsupportedEncodingException e) {
                extraInfo = "";
            }

            Map<String, String> feeInfo = getFeeInfo(connection);
            getBranchFeeAccount(connection);
            getOpCode(connection, Constants.BRANCH_CHANNEL);
            List<String> feeBranch = getFeeBranch(connection);

            sequence = getSessionId(sequenceSelect);
            RefNoGeneratorSharing generator = RefNoGeneratorSharing.getInstance();


            String summeryString = "select TRANSACTION_TYPE,branch_code,max(LOG_ID)as max, count(*) as count " +
                    "from tb_fee_log where IS_DONE='0' and IS_REVERSAL_TXN='F' and tx_src='" + Constants.BRANCH + "' group by TRANSACTION_TYPE,branch_code with ur";
            summeryRs = summerySt.executeQuery(summeryString);
            List<InfoClass> infoList = new ArrayList<InfoClass>();
            while (summeryRs.next()) {
                InfoClass infoClass = new InfoClass(Constants.BRANCH, summeryRs.getString("TRANSACTION_TYPE"),
                        summeryRs.getString("branch_code"), summeryRs.getLong("max"), summeryRs.getInt("count"));
                infoList.add(infoClass);
            }
            selectConnection.commit();

            outerLoop:
            for (InfoClass info : infoList) {

                if (feeBranch != null && feeBranch.size() > 0 && feeBranch.contains(info.getBranchCode())) {

                    if (feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()) == null) {
                        log.error("ERROR1:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.1::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }
                    accountPercent = feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()).split(",");
                    if (accountPercent == null || accountPercent.length == 0
                            || accountPercent[0] == null || accountPercent[0].trim().equalsIgnoreCase("")
                            || accountPercent[1] == null || accountPercent[1].trim().equalsIgnoreCase("")) {
                        log.error("ERROR2:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.2::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",") == null) {
                        log.error("ERROR3:: information about CentralBank account for TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.3::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }
                    String[] cbiPercent = feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",");
                    if (cbiPercent == null || cbiPercent.length == 0
                            || cbiPercent[0] == null || cbiPercent[0].trim().equalsIgnoreCase("")
                            || cbiPercent[1] == null || cbiPercent[1].trim().equalsIgnoreCase("")) {
                        log.error("ERROR4:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.4::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS)) {
                        branchRTGSAccount = branchRTGSAccounts.get(info.getBranchCode().trim());
                        if (branchRTGSAccount == null || branchRTGSAccount.trim().equalsIgnoreCase("")) {
                            log.error("ERROR5:: branch RTGS account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                    " does not exis");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.5::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        }

                    } else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH)) {
                        branchACHAccount = branchACHAccounts.get(info.getBranchCode().trim());
                        if (branchACHAccount == null || branchACHAccount.trim().equalsIgnoreCase("")) {
                            log.error("ERROR6:: branch ACH account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                    " does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.6::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        }
                    } else {
                        log.error("ERROR7:: branch account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.7::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH))
                        if (achOpCode == null || achOpCode.trim().equalsIgnoreCase("")) {
                            log.error("ERROR8:: Branch ACH OpCode does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.8::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        } else
                            operationCode = achOpCode.trim();
                    else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                        if (rtgsOpCode == null || rtgsOpCode.trim().equalsIgnoreCase("")) {
                            log.error("ERROR9:: Branch RTGS OpCode does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.9::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        } else
                            operationCode = rtgsOpCode.trim();
                    else {
                        log.error("ERROR10:: TransactionType=" + info.getTransactionType() + " does not have OpCode then All records related to it do not process");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.10::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    rowProcess = 0;
                    count = 0;

                    String selectString = "select LOG_ID,DEST_ACCOUNT_NO,BRANCH_CODE,FEE_AMOUNT,BRANCH_DOCNO from tb_fee_log where " +
                            "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                            "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId() + "  with ur";
                    selectCmRs = selectCmStm.executeQuery(selectString);
                    while (selectCmRs.next()) {
                        count++;

                        session_id = ISOUtil.zeropad(String.valueOf(++sequence), 12);
                        amount = selectCmRs.getLong("FEE_AMOUNT");
                        src_account_no = selectCmRs.getString("DEST_ACCOUNT_NO");
                        branchDocNo = selectCmRs.getString("BRANCH_DOCNO");
                        long sharingAmount = 0;


                        // Calculate the finance
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        dest_account_no = accountPercent[0];
                        percent = Long.valueOf(accountPercent[1].trim());
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        // Calculate the CentralBank share
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        dest_account_no = cbiPercent[0];
                        percent = Long.valueOf(cbiPercent[1].trim());
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        // Calculate the Branch share
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                            dest_account_no = branchRTGSAccount;
                        else
                            dest_account_no = branchACHAccount;

                        percent = 100 - (Long.valueOf(cbiPercent[1].trim()) + Long.valueOf(accountPercent[1].trim()));
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        logId = selectCmRs.getLong("LOG_ID");
                        updateFeeLog(fee_log_ps,logId);

                        if (count == blockSize) {
                            try {
                                cfstx_ps.executeBatch();
                                rowProcess += count;
                                cfstx_ps.clearBatch();
                                log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                        " for TX_SRC=" + info.getTxSrc() +
                                        " for Branch_Code=" + info.getBranchCode() +
                                        " and TransactionType=" + info.getTransactionType());
                                count = 0;
                            } catch (BatchUpdateException ex) {
                                log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode() +
                                         "Row process::" + (rowProcess + count) +
                                        " End of process this type of transaction and rollBack all ");
                                log.error(ex.getMessage());
                                log.error(ex.getNextException());
                                connection.rollback();
                                cfstx_ps.clearBatch();
                                continue outerLoop;
                            } catch (SQLException ex) {
                                log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode() +
                                         "Row process::" + (rowProcess + count) +
                                        " End of process this type of transaction and rollBack all ");
                                log.error(ex.getMessage());
                                connection.rollback();
                                cfstx_ps.clearBatch();
                                continue outerLoop;
                            }
                        }
                    }

                    selectConnection.commit();

                    try {
                        if (count > 0) {
                            cfstx_ps.executeBatch();
                            rowProcess += count;
                            count = 0;
                            log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                    " for TX_SRC=" + info.getTxSrc() +
                                    " for Branch_Code=" + info.getBranchCode() +
                                    " and TransactionType=" + info.getTransactionType());
                            cfstx_ps.clearBatch();
                        }


                        sequenceUpdate.setLong(1, sequence);
                        sequenceUpdate.execute();
                        connection.commit();

                        log.error("Deposit Document was inserted::" +
                                " for TX_SRC=" + info.getTxSrc() +
                                " for Branch_Code=" + info.getBranchCode() +
                                " and TransactionType=" + info.getTransactionType());

                    } catch (BatchUpdateException ex) {
                        log.error("No2.Error in process record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        log.error(ex.getNextException());
                        connection.rollback();
                        cfstx_ps.clearBatch();
                        continue;
                    } catch (SQLException ex) {
                        log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        connection.rollback();
                        cfstx_ps.clearBatch();
                        continue;
                    }
                } else {
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("branch_Code=" + info.getBranchCode() +
                                " and transaction_type=" + info.getTransactionType() +
                                " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("Error in update record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
            }

            //Process EBNK Transaction

            getOpCode(connection, Constants.EBNK_CHANNEL);

            summeryString = "select tx_src,TRANSACTION_TYPE,max(LOG_ID)as max, count(*) as count " +
                    "from tb_fee_log where IS_DONE='0' and IS_REVERSAL_TXN='F' and tx_src<>'" + Constants.BRANCH + "' group by tx_src,TRANSACTION_TYPE with ur";
            summeryRs = summerySt.executeQuery(summeryString);
            infoList.clear();
            while (summeryRs.next()) {
                InfoClass infoClass = new InfoClass(summeryRs.getString("tx_src"), summeryRs.getString("TRANSACTION_TYPE"),
                        "", summeryRs.getLong("max"), summeryRs.getInt("count"));
                infoList.add(infoClass);
            }
            selectConnection.commit();
            outerLoop:
            for (InfoClass info : infoList) {

                if (feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()) == null) {
                    log.error("ERROR1:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.1::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
                accountPercent = feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()).split(",");
                if (accountPercent == null || accountPercent.length == 0
                        || accountPercent[0] == null || accountPercent[0].trim().equalsIgnoreCase("")
                        || accountPercent[1] == null || accountPercent[1].trim().equalsIgnoreCase("")) {
                    log.error("ERROR2:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.2::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                if (feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",") == null) {
                    log.error("ERROR3:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.3::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
                String[] cbiPercent = feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",");
                if (cbiPercent == null || cbiPercent.length == 0
                        || cbiPercent[0] == null || cbiPercent[0].trim().equalsIgnoreCase("")
                        || cbiPercent[1] == null || cbiPercent[1].trim().equalsIgnoreCase("")) {
                    log.error("ERROR4:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.4::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH))
                    if (achOpCode == null || achOpCode.trim().equalsIgnoreCase("")) {
                        log.error("ERROR5:: ACH OpCode does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                    "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.5::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    } else
                        operationCode = achOpCode.trim();
                else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                    if (rtgsOpCode == null || rtgsOpCode.trim().equalsIgnoreCase("")) {
                        log.error("ERROR5:: RTGS OpCode does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                    "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.5::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    } else
                        operationCode = rtgsOpCode.trim();
                else {
                    log.error("ERROR6:: TransactionType=" + info.getTransactionType() + " does not have OpCode");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and " +
                                "TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.6::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                rowProcess = 0;
                count = 0;

                String selectString = "select LOG_ID,DEST_ACCOUNT_NO,BRANCH_CODE,FEE_AMOUNT,BRANCH_DOCNO from tb_fee_log where " +
                        "IS_DONE='0' and IS_REVERSAL_TXN='F' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                        "and LOG_ID<=" + info.getMaxLogId() + " with ur";
                selectCmRs = selectCmStm.executeQuery(selectString);
                while (selectCmRs.next()) {
                    count++;

                    session_id = ISOUtil.zeropad(String.valueOf(++sequence), 12);
                    src_account_no = selectCmRs.getString("DEST_ACCOUNT_NO");
                    amount = selectCmRs.getLong("FEE_AMOUNT");
                    branchDocNo = selectCmRs.getString("BRANCH_DOCNO");
                    branchCode = selectCmRs.getString("BRANCH_CODE");
                    long sharingAmount = 0;

                    // Calculate the financial share
                    refNo = generator.generateRefNo("FE");
                    batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                    dest_account_no = accountPercent[0];
                    percent = Long.valueOf(accountPercent[1].trim());
                    sharingAmount = (long) ((double) percent * amount / 100);

                    saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT,
                            sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                            batchPk, new Timestamp(System.currentTimeMillis()),
                            DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                            dest_account_no, branchCode, branchDocNo);

                    // Calculate the CentralBank share
                    refNo = generator.generateRefNo("FE");
                    batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                    dest_account_no = cbiPercent[0];
                    percent = Long.valueOf(cbiPercent[1].trim());
                    sharingAmount = (long) ((double) percent * amount / 100);

                    saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT,
                            sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                            batchPk, new Timestamp(System.currentTimeMillis()),
                            DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                            dest_account_no, branchCode, branchDocNo);


                    logId = selectCmRs.getLong("LOG_ID");
                    updateFeeLog(fee_log_ps,logId);

                    if (count == blockSize) {
                        try {
                            cfstx_ps.executeBatch();
                            rowProcess += count;
                            log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                    " for TX_SRC=" + info.getTxSrc() +
                                    " and TransactionType=" + info.getTransactionType());
                            cfstx_ps.clearBatch();
                            count = 0;
                        } catch (BatchUpdateException ex) {
                            log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode() +
                                    "Row process::" + (rowProcess + count) +
                                    " End of process this type of transaction and rollBack all ");
                            log.error(ex.getMessage());
                            log.error(ex.getNextException());
                            connection.rollback();
                            cfstx_ps.clearBatch();
                            continue outerLoop;
                        } catch (SQLException ex) {
                            log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode() +
                                     "Row process::" + (rowProcess + count) +
                                    " End of process this type of transaction and rollBack all ");
                            log.error(ex.getMessage());
                            connection.rollback();
                            cfstx_ps.clearBatch();
                            continue outerLoop;
                        }
                    }
                }

                selectConnection.commit();

                try {
                    if (count > 0) {
                        cfstx_ps.executeBatch();
                        rowProcess += count;
                        count = 0;
                        log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                " for TX_SRC=" + info.getTxSrc() +
                                " and TransactionType=" + info.getTransactionType());
                        cfstx_ps.clearBatch();
                    }

                    sequenceUpdate.setLong(1, sequence);
                    sequenceUpdate.execute();

                    connection.commit();

                    log.error("Deposit Document was inserted::" +
                            " for TX_SRC=" + info.getTxSrc() +
                            " and TransactionType=" + info.getTransactionType());

                } catch (BatchUpdateException ex) {
                    log.error("Error in process record for tx_src=" + info.getTxSrc() +
                            " and transaction_type=" + info.getTransactionType());
                    log.error(ex.getMessage());
                    log.error(ex.getNextException());
                    connection.rollback();
                    continue;
                } catch (SQLException ex) {
                    log.error("Error in process record for tx_src=" + info.getTxSrc() +
                            " and transaction_type=" + info.getTransactionType());
                    log.error(ex.getMessage());
                    connection.rollback();
                    continue;
                }
            }

            log.error("doSharing() Done at " + DateUtil.getSystemTime());
        } catch (Exception e) {
            log.error("Exception in doSharing()>> " + e.getMessage());
            throw e;
        } finally {
            try {

                if (selectCmRs != null)
                    selectCmRs.close();
                if (selectCmStm != null)
                    selectCmStm.close();
                if (summeryRs != null)
                    summeryRs.close();
                if (summerySt != null)
                    summerySt.close();
                if (cfstx_ps != null)
                    cfstx_ps.close();
                if (fee_log_ps != null)
                    fee_log_ps.close();
                if (sequenceSelect != null)
                    sequenceSelect.close();
                if (sequenceUpdate != null)
                    sequenceUpdate.close();
            } catch (Exception e) {
                log.error("Exception in doSharing_finally>> " + e.getMessage());
            }
        }
    }

    public void doReverse(Connection connection, Connection selectConnection) throws Exception {

        Statement selectCmStm = null;
        ResultSet selectCmRs = null;
        Statement summerySt = null;
        ResultSet summeryRs = null;
        PreparedStatement cfstx_ps = null;
        PreparedStatement fee_log_ps = null;
        PreparedStatement sequenceSelect = null;
        PreparedStatement sequenceUpdate = null;
        Statement updateBranch = null;
        long sequence = 0;
        String src_account_no = "";
        String dest_account_no = "";
        String branchDocNo = "";
        String branchCode = "";
        long amount = 0;
        long logId = 0;
        String refNo = "";
        String session_id = "";
        String[] accountPercent = null;
        String branchRTGSAccount = "";
        String branchACHAccount = "";
        long percent = 0;
        long batchPk = 0;
        String operationCode = "";
        long count = 0;
        long rowProcess = 0;

        try {

            log.error("doReverse() Start at :: " + DateUtil.getSystemTime());

            selectCmStm = selectConnection.createStatement();
            summerySt = selectConnection.createStatement();

            String sql = "Insert into tbCFSTX " +
                    "(TX_PK,PARTNO,TX_SEQ,TX_CODE,TX_SRC," +
                    "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                    "CREATION_DATE,CREATION_TIME," +
                    "SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                    "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED," +
                    "IS_REVERSAL_TXN,SRC_ACC_BALANCE, " +
                    "SGB_ACTION_CODE,LAST_TRANS_LIMIT,DEST_ACC_BALANCE,SESSION_ID, " +
                    "HOSTCODE,EXTRA_INFO,DESC" +
                    ",CARD_NO,ACQUIRER,FEE_AMOUNT,TX_SEQUENCE_NUMBER,RRN,TERMINAL_ID" +
                    ",SGB_BRANCH_ID,BRANCH_DOCNO,TOTAL_DEST_ACCOUNT" +
                    ") values(?,?,?,?,'" + Constants.TXN_SRC_FEE + "'," +
                    "?,'364',?,?,?,?,'',?,?,?,?,0,0,'0',0,?,-1000000000000000000,0,?,'22',?,?" +
                    ",'" + Constants.BANKE_TEJARAT_BIN_NEW + "','" + Constants.BANKE_TEJARAT_BIN_NEW + "',0,'0','','',?,?,?)";
            cfstx_ps = connection.prepareStatement(sql);
            sql = "update tb_fee_Log set is_done = '1' where log_id = ? ";
            fee_log_ps = connection.prepareStatement(sql);
            sql = "select Sequencer from tbSequence where name = '" + Constants.FEE_SHARING_SESSION_ID + "' with ur";
            sequenceSelect = connection.prepareStatement(sql);
            sql = "update tbSequence set SEQUENCER = ?  where name = '" + Constants.FEE_SHARING_SESSION_ID + "' ";
            sequenceUpdate = connection.prepareStatement(sql);

            String extraInfo = "";
            byte[] descByte = {40, 39, 78, 66, 124, 58, 124, 65, 87, 88, 95};
            try {
                String s = new String(descByte, "ISO-8859-1");
                extraInfo = FarsiUtil.convertWindows1256(descByte);
            } catch (UnsupportedEncodingException e) {
                extraInfo = "";
            }

            Map<String, String> feeInfo = getFeeInfo(connection);
            getBranchFeeAccount(connection);
            getReverseOpCode(connection, Constants.BRANCH_CHANNEL);
            List<String> feeBranch = getFeeBranch(connection);

            sequence = getSessionId(sequenceSelect);
            RefNoGeneratorSharing generator = RefNoGeneratorSharing.getInstance();


            String summeryString = "select TRANSACTION_TYPE,branch_code,max(LOG_ID)as max, count(*) as count " +
                    "from tb_fee_log where IS_DONE='0' and IS_REVERSAL_TXN='T' and tx_src='" + Constants.BRANCH + "' group by TRANSACTION_TYPE,branch_code with ur";
            summeryRs = summerySt.executeQuery(summeryString);
            List<InfoClass> infoList = new ArrayList<InfoClass>();
            while (summeryRs.next()) {
                InfoClass infoClass = new InfoClass(Constants.BRANCH, summeryRs.getString("TRANSACTION_TYPE"),
                        summeryRs.getString("branch_code"), summeryRs.getLong("max"), summeryRs.getInt("count"));
                infoList.add(infoClass);
            }
            selectConnection.commit();

            outerLoop:
            for (InfoClass info : infoList) {

                if (feeBranch != null && feeBranch.size() > 0 && feeBranch.contains(info.getBranchCode())) {

                    if (feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()) == null) {
                        log.error("ERROR1:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.1::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }
                    accountPercent = feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()).split(",");
                    if (accountPercent == null || accountPercent.length == 0
                            || accountPercent[0] == null || accountPercent[0].trim().equalsIgnoreCase("")
                            || accountPercent[1] == null || accountPercent[1].trim().equalsIgnoreCase("")) {
                        log.error("ERROR2:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.2::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",") == null) {
                        log.error("ERROR3:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.3::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }
                    String[] cbiPercent = feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",");
                    if (cbiPercent == null || cbiPercent.length == 0
                            || cbiPercent[0] == null || cbiPercent[0].trim().equalsIgnoreCase("")
                            || cbiPercent[1] == null || cbiPercent[1].trim().equalsIgnoreCase("")) {
                        log.error("ERROR4:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.4::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS)) {
                        branchRTGSAccount = branchRTGSAccounts.get(info.getBranchCode().trim());
                        if (branchRTGSAccount == null || branchRTGSAccount.trim().equalsIgnoreCase("")) {
                            log.error("ERROR5:: branch RTGS account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                    " does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.5::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        }

                    } else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH)) {
                        branchACHAccount = branchACHAccounts.get(info.getBranchCode().trim());
                        if (branchACHAccount == null || branchACHAccount.trim().equalsIgnoreCase("")) {
                            log.error("ERROR6:: branch ACH account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                    " does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.6::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        }
                    } else {
                        log.error("ERROR7:: branch account for branchCode=" + info.getBranchCode() + " and TransactionType=" + info.getTransactionType() +
                                " does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.7::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH))
                        if (achOpCode == null || achOpCode.trim().equalsIgnoreCase("")) {
                            log.error("ERROR8:: Branch ACH OpCode does not exist.");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.8::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        } else
                            operationCode = achOpCode.trim();
                    else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                        if (rtgsOpCode == null || rtgsOpCode.trim().equalsIgnoreCase("")) {
                            log.error("ERROR9:: Branch RTGS OpCode does not exist");
                            try {
                                String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                        "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                        "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                                updateBranch = connection.createStatement();
                                updateBranch.executeUpdate(updateSql);
                                updateBranch.close();
                                connection.commit();
                                log.error("branch_Code=" + info.getBranchCode() +
                                        " and TX_SRC=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " was ignored.");
                                continue;
                            } catch (SQLException ex) {
                                log.error("No.9::Error in update record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                continue;
                            }
                        } else
                            operationCode = rtgsOpCode.trim();
                    else {
                        log.error("ERROR10:: TransactionType=" + info.getTransactionType() + " does not have OpCode");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                    "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("branch_Code=" + info.getBranchCode() +
                                    " and TX_SRC=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.10::Error in update record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    }

                    rowProcess = 0;
                    count = 0;

                    String selectString = "select LOG_ID,DEST_ACCOUNT_NO,BRANCH_CODE,FEE_AMOUNT,BRANCH_DOCNO from tb_fee_log where " +
                            "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                            "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId() + " with ur";
                    selectCmRs = selectCmStm.executeQuery(selectString);
                    while (selectCmRs.next()) {
                        count++;

                        session_id = ISOUtil.zeropad(String.valueOf(++sequence), 12);
                        dest_account_no = selectCmRs.getString("DEST_ACCOUNT_NO");
                        branchDocNo = selectCmRs.getString("BRANCH_DOCNO");
                        amount = selectCmRs.getLong("FEE_AMOUNT");
                        long sharingAmount = amount;
                        // Calculate the financial share
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        src_account_no = accountPercent[0];
                        percent = Long.valueOf(accountPercent[1].trim());
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT_REVERSE,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        // Calculate the CentralBank share
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        src_account_no = cbiPercent[0];
                        percent = Long.valueOf(cbiPercent[1].trim());
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT_REVERSE,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        // Calculate the Branch share
                        refNo = generator.generateRefNo("FE");
                        batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                        if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                            src_account_no = branchRTGSAccount;
                        else
                            src_account_no = branchACHAccount;

                        percent = 100 - (Long.valueOf(cbiPercent[1].trim()) + Long.valueOf(accountPercent[1].trim()));
                        sharingAmount = (long) ((double) percent * amount / 100);

                        saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT_REVERSE,
                                sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                batchPk, new Timestamp(System.currentTimeMillis()),
                                DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                                dest_account_no, info.getBranchCode(), branchDocNo);

                        logId = selectCmRs.getLong("LOG_ID");
                        updateFeeLog(fee_log_ps,logId);

                        if (count == blockSize) {
                            try {
                                cfstx_ps.executeBatch();
                                rowProcess += count;
                                cfstx_ps.clearBatch();
                                log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                        " for TX_SRC=" + info.getTxSrc() +
                                        " for Branch_Code=" + info.getBranchCode() +
                                        " and TransactionType=" + info.getTransactionType());
                                count = 0;
                            } catch (BatchUpdateException ex) {
                                log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode() +
                                         "Row process::" + (rowProcess + count) +
                                        " End of process this type of transaction and rollBack all ");
                                log.error(ex.getMessage());
                                log.error(ex.getNextException());
                                connection.rollback();
                                cfstx_ps.clearBatch();
                                continue outerLoop;
                            } catch (SQLException ex) {
                                log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                        " and transaction_type=" + info.getTransactionType() +
                                        " and branch_Code=" + info.getBranchCode());
                                log.error(ex.getMessage());
                                connection.rollback();
                                cfstx_ps.clearBatch();
                                continue outerLoop;
                            }
                        }
                    }

                    selectConnection.commit();

                    try {
                        if (count > 0) {
                            cfstx_ps.executeBatch();
                            rowProcess += count;
                            count = 0;
                            log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                    " for TX_SRC=" + info.getTxSrc() +
                                    " for Branch_Code=" + info.getBranchCode() +
                                    " and TransactionType=" + info.getTransactionType());
                            cfstx_ps.clearBatch();
                        }

                        sequenceUpdate.setLong(1, sequence);
                        sequenceUpdate.execute();
                        connection.commit();

                        log.error("Deposit Document was inserted::" +
                                " for TX_SRC=" + info.getTxSrc() +
                                " for Branch_Code=" + info.getBranchCode() +
                                " and TransactionType=" + info.getTransactionType());

                    } catch (BatchUpdateException ex) {
                        log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        log.error(ex.getNextException());
                        connection.rollback();
                        cfstx_ps.clearBatch();
                        continue;
                    } catch (SQLException ex) {
                        log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        connection.rollback();
                        cfstx_ps.clearBatch();
                        continue;
                    }
                } else {
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                                "and BRANCH_CODE='" + info.getBranchCode() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("branch_Code=" + info.getBranchCode() +
                                " and transaction_type=" + info.getTransactionType() +
                                " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("Error in update record for tx_src=" + info.getTxSrc() +
                                " and transaction_type=" + info.getTransactionType() +
                                " and branch_Code=" + info.getBranchCode());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
            }

            //Process EBNK Transaction

            getReverseOpCode(connection, Constants.EBNK_CHANNEL);

            summeryString = "select tx_src,TRANSACTION_TYPE,max(LOG_ID)as max, count(*) as count " +
                    "from tb_fee_log where IS_DONE='0' and IS_REVERSAL_TXN='T' and tx_src<>'" + Constants.BRANCH + "' group by tx_src,TRANSACTION_TYPE with ur";
            summeryRs = summerySt.executeQuery(summeryString);
            infoList.clear();
            while (summeryRs.next()) {
                InfoClass infoClass = new InfoClass(summeryRs.getString("tx_src"), summeryRs.getString("TRANSACTION_TYPE"),
                        "", summeryRs.getLong("max"), summeryRs.getInt("count"));
                infoList.add(infoClass);
            }
            selectConnection.commit();
            outerLoop:
            for (InfoClass info : infoList) {

                if (feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()) == null) {
                    log.error("ERROR1:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.1::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
                accountPercent = feeInfo.get(info.getTxSrc().trim() + "," + info.getTransactionType().trim()).split(",");
                if (accountPercent == null || accountPercent.length == 0
                        || accountPercent[0] == null || accountPercent[0].trim().equalsIgnoreCase("")
                        || accountPercent[1] == null || accountPercent[1].trim().equalsIgnoreCase("")) {
                    log.error("ERROR2:: information about TX_SRC=" + info.getTxSrc() + " and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.2::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                if (feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",") == null) {
                    log.error("ERROR3:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.3::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }
                String[] cbiPercent = feeInfo.get(Constants.CBI + "," + info.getTransactionType().trim()).split(",");
                if (cbiPercent == null || cbiPercent.length == 0
                        || cbiPercent[0] == null || cbiPercent[0].trim().equalsIgnoreCase("")
                        || cbiPercent[1] == null || cbiPercent[1].trim().equalsIgnoreCase("")) {
                    log.error("ERROR4:: information about CentralBank account and TransactionType=" + info.getTransactionType() +
                            " does not exist");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.4::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                if (info.getTransactionType().trim().equalsIgnoreCase(Constants.ACH))
                    if (achOpCode == null || achOpCode.trim().equalsIgnoreCase("")) {
                        log.error("ERROR5:: ACH OpCode does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                    "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.5::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    } else
                        operationCode = achOpCode.trim();
                else if (info.getTransactionType().trim().equalsIgnoreCase(Constants.RTGS))
                    if (rtgsOpCode == null || rtgsOpCode.trim().equalsIgnoreCase("")) {
                        log.error("ERROR6:: RTGS OpCode does not exist");
                        try {
                            String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                    "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                    "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                            updateBranch = connection.createStatement();
                            updateBranch.executeUpdate(updateSql);
                            updateBranch.close();
                            connection.commit();
                            log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                            continue;
                        } catch (SQLException ex) {
                            log.error("No.6::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                            log.error(ex.getMessage());
                            connection.rollback();
                            continue;
                        }
                    } else
                        operationCode = rtgsOpCode.trim();
                else {
                    log.error("ERROR7:: TransactionType=" + info.getTransactionType() + " does not have OpCode");
                    try {
                        String updateSql = "update tb_fee_Log set is_done = 'I' where " +
                                "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() +
                                "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' and LOG_ID<=" + info.getMaxLogId();
                        updateBranch = connection.createStatement();
                        updateBranch.executeUpdate(updateSql);
                        updateBranch.close();
                        connection.commit();
                        log.error("TX_SRC=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType() + " was ignored.");
                        continue;
                    } catch (SQLException ex) {
                        log.error("No.7::Error in update record for tx_src=" + info.getTxSrc() + " and transaction_type=" + info.getTransactionType());
                        log.error(ex.getMessage());
                        connection.rollback();
                        continue;
                    }
                }

                rowProcess = 0;
                count = 0;

                String selectString = "select LOG_ID,DEST_ACCOUNT_NO,BRANCH_CODE,FEE_AMOUNT,BRANCH_DOCNO from tb_fee_log where " +
                        "IS_DONE='0' and IS_REVERSAL_TXN='T' and TX_SRC='" + info.getTxSrc() + "' and TRANSACTION_TYPE='" + info.getTransactionType() + "' " +
                        "and LOG_ID<=" + info.getMaxLogId() + " with ur";
                selectCmRs = selectCmStm.executeQuery(selectString);
                while (selectCmRs.next()) {
                    count++;

                    session_id = ISOUtil.zeropad(String.valueOf(++sequence), 12);
                    dest_account_no = selectCmRs.getString("DEST_ACCOUNT_NO");
                    branchDocNo = selectCmRs.getString("BRANCH_DOCNO");
                    branchCode = selectCmRs.getString("BRANCH_CODE");
                    amount = selectCmRs.getLong("FEE_AMOUNT");

                    long sharingAmount = 0;

                    // Calculate the financial share
                    refNo = generator.generateRefNo("FE");
                    batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                    src_account_no = accountPercent[0];
                    percent = Long.valueOf(accountPercent[1].trim());
                    sharingAmount = (long) ((double) percent * amount / 100);

                    saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT_REVERSE,
                            sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                            batchPk, new Timestamp(System.currentTimeMillis()),
                            DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                            dest_account_no, branchCode, branchDocNo);

                    // Calculate the CentralBank share
                    refNo = generator.generateRefNo("FE");
                    batchPk = getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND);
                    src_account_no = cbiPercent[0];
                    percent = Long.valueOf(cbiPercent[1].trim());
                    sharingAmount = (long) ((double) percent * amount / 100);

                    saveTx(cfstx_ps, refNo, "1", TJCommand.SHARING_FT_REVERSE,
                            sharingAmount, src_account_no, dest_account_no, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                            batchPk, new Timestamp(System.currentTimeMillis()),
                            DateUtil.getSystemDate(), DateUtil.getSystemTime(), operationCode, session_id, extraInfo,
                            dest_account_no, branchCode, branchDocNo);

                    logId = selectCmRs.getLong("LOG_ID");
                    updateFeeLog(fee_log_ps,logId);

                    if (count == blockSize) {
                        try {
                            cfstx_ps.executeBatch();
                            rowProcess += count;
                            log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                    " for TX_SRC=" + info.getTxSrc() +
                                    " and TransactionType=" + info.getTransactionType());
                            cfstx_ps.clearBatch();
                            count = 0;
                        } catch (BatchUpdateException ex) {
                            log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode() +
                                     "Row process::" + (rowProcess + count) +
                                    " End of process this type of transaction and rollBack all ");
                            log.error(ex.getMessage());
                            log.error(ex.getNextException());
                            connection.rollback();
                            cfstx_ps.clearBatch();
                            continue outerLoop;
                        } catch (SQLException ex) {
                            log.error("Error in process record for tx_src=" + info.getTxSrc() +
                                    " and transaction_type=" + info.getTransactionType() +
                                    " and branch_Code=" + info.getBranchCode() +
                                     "Row process::" + (rowProcess + count) +
                                    " End of process this type of transaction and rollBack all ");
                            log.error(ex.getMessage());
                            connection.rollback();
                            cfstx_ps.clearBatch();
                            continue outerLoop;
                        }
                    }
                }

                selectConnection.commit();

                try {
                    if (count > 0) {
                        cfstx_ps.executeBatch();
                        rowProcess += count;
                        count = 0;
                        log.error("Row process::" + rowProcess + " of  " + info.getCountTransaction() +
                                " for TX_SRC=" + info.getTxSrc() +
                                " and TransactionType=" + info.getTransactionType());
                        cfstx_ps.clearBatch();
                    }

                    sequenceUpdate.setLong(1, sequence);
                    sequenceUpdate.execute();

                    connection.commit();
                    log.error("Deposit Document was inserted::" +
                            " for TX_SRC=" + info.getTxSrc() +
                            " and TransactionType=" + info.getTransactionType());

                } catch (BatchUpdateException ex) {
                    log.error("Error in process record for tx_src=" + info.getTxSrc() +
                            " and transaction_type=" + info.getTransactionType());
                    log.error(ex.getMessage());
                    log.error(ex.getNextException());
                    connection.rollback();
                    continue;
                } catch (SQLException ex) {
                    log.error("Error in process record for tx_src=" + info.getTxSrc() +
                            " and transaction_type=" + info.getTransactionType());
                    log.error(ex.getMessage());
                    connection.rollback();
                    continue;
                }
            }

            log.error("doReverse() Done at " + DateUtil.getSystemTime());
        } catch (Exception e) {
            log.error("Exception in doReverse()>> " + e.getMessage());
            throw e;
        } finally {
            try {

                if (selectCmRs != null)
                    selectCmRs.close();
                if (selectCmStm != null)
                    selectCmStm.close();
                if (summeryRs != null)
                    summeryRs.close();
                if (summerySt != null)
                    summerySt.close();
                if (cfstx_ps != null)
                    cfstx_ps.close();
                if (fee_log_ps != null)
                    fee_log_ps.close();
                if (sequenceSelect != null)
                    sequenceSelect.close();
                if (sequenceUpdate != null)
                    sequenceUpdate.close();
            } catch (Exception e) {
                log.error("Exception in doReverse_finally>> " + e.getMessage());
            }
        }
    }


    public Map<String, String> getFeeInfo(Connection connection) throws SQLException {
        Map<String, String> feeInfoMap = new HashMap<String, String>();
        Statement statement = null;
        ResultSet resultSet = null;
        String ownerName = "";
        String transactionType = "";
        String accountNo = "";
        int percent = 0;
        try {
            String query = "select * from tbfeeinfo with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ownerName = resultSet.getString("OWNER_NAME");
                transactionType = resultSet.getString("TRANSACTION_TYPE");
                accountNo = resultSet.getString("ACCOUNT_NO");
                percent = resultSet.getInt("PERCENT");
                feeInfoMap.put(ownerName + "," + transactionType, accountNo + "," + percent);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in getFeeInfo():: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return feeInfoMap;
    }

    public List<String> getFeeBranch(Connection connection) throws SQLException {
        List<String> feeBranch = new ArrayList<String>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "select branch_code from tbfeeBranch with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                feeBranch.add(resultSet.getString("branch_code"));
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in getFeeBranch():: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return feeBranch;
    }

    public void getBranchFeeAccount(Connection connection) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        String brRTGSAccount = "";
        String brACHAccountNo = "";
        String branchCode = "";
        try {
            String query = "select BRANCH_CODE,BR_ACH_FEE_ACC,BR_RTGS_FEE_ACC from tbbranch with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                branchCode = resultSet.getString("BRANCH_CODE");
                brRTGSAccount = resultSet.getString("BR_RTGS_FEE_ACC");
                brACHAccountNo = resultSet.getString("BR_ACH_FEE_ACC");
                branchRTGSAccounts.put(branchCode, brRTGSAccount);
                branchACHAccounts.put(branchCode, brACHAccountNo);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in getFeeInfo():: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    private int getCfsTxPartNo() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.CFSTX_PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.CFSTX_PARTITION_SIZE;
        return part;
    }

    public long getSessionId(PreparedStatement sequenceSelect) throws SQLException, NotFoundException {
        ResultSet resultSet = sequenceSelect.executeQuery();

        if (!resultSet.next())
            throw new NotFoundException("Could not found sequence for ApplierSessionId in tbCFSCardAccount.");
        return resultSet.getLong(1);
    }

    public void saveTx
            (PreparedStatement cfstx_ps, String tx_pk, String tx_seq, String tx_code, long amount, String src_account, String dest_account,
             String creation_Date, String creation_time, Long batchPk, Timestamp tx_dateTime, String tx_orig_date,
             String tx_orig_time, String sgbActionCode, String session_id, String extraInfo, String totalDestAccount,
             String sgbBranchId, String branchDocNo) throws SQLException {

        cfstx_ps.setString(1, tx_pk);
        cfstx_ps.setInt(2, getCfsTxPartNo());
        cfstx_ps.setString(3, tx_seq);
        cfstx_ps.setString(4, tx_code);
        cfstx_ps.setLong(5, amount);
        cfstx_ps.setString(6, src_account);
        cfstx_ps.setString(7, dest_account);
        cfstx_ps.setString(8, creation_Date);
        cfstx_ps.setString(9, creation_time);
        cfstx_ps.setLong(10, batchPk.longValue());
        cfstx_ps.setTimestamp(11, tx_dateTime);
        cfstx_ps.setString(12, tx_orig_date);
        cfstx_ps.setString(13, tx_orig_time);
        cfstx_ps.setString(14, sgbActionCode);
        cfstx_ps.setString(15, session_id);
        cfstx_ps.setString(16, extraInfo);
        cfstx_ps.setString(17, extraInfo);
        cfstx_ps.setString(18, sgbBranchId);
        cfstx_ps.setString(19, branchDocNo);
        cfstx_ps.setString(20, totalDestAccount);
        try {
            cfstx_ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void updateFeeLog(PreparedStatement fee_log_ps, long logId) throws SQLException {
        fee_log_ps.setLong(1, logId);
        fee_log_ps.execute();
    }

    public void saveFeeLogFromFARA
            (PreparedStatement feeLog_ps, String src_account_no, String dest_account_no, String txSrc, long amount, String branchCode,
             String txDate, String txTime, String createDate, String createTime, String transType, String isReverse,
             long origAmount, String isReversed, String trackCode, String fileName, String destIBAN, String branchDocNo) throws SQLException {

        feeLog_ps.setString(1, src_account_no);
        feeLog_ps.setString(2, dest_account_no);
        feeLog_ps.setString(3, txSrc);
        feeLog_ps.setString(4, branchCode);
        feeLog_ps.setLong(5, amount);
        feeLog_ps.setString(6, txDate);
        feeLog_ps.setString(7, txTime);
        feeLog_ps.setString(8, createDate);
        feeLog_ps.setString(9, createTime);
        feeLog_ps.setString(10, transType);
        feeLog_ps.setString(11, isReverse);
        feeLog_ps.setLong(12, origAmount);
        feeLog_ps.setString(13, isReversed);
        feeLog_ps.setString(14, trackCode);
        feeLog_ps.setString(15, fileName);
        feeLog_ps.setString(16, destIBAN);
        feeLog_ps.setString(17, branchDocNo);
        try {
            feeLog_ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public long getBatch(Integer batch_status, String operator) throws SQLException, NotFoundException {
        if (Constants.ACTIVE_BATCH == batch_status.intValue())
            if (batchMap.containsKey(batch_status))
                return batchMap.get(batch_status);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from tbCFSBatch where batch_status = " + batch_status + " " +
                    operator + " SGB_STATUS = " + batch_status + " " + operator + " FARA_STATUS= " +
                    batch_status + " with ur";
            connection = ChannelFacadeNew.getDbConnectionPool().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                long batch = resultSet.getLong("BATCH_PK");
                if (Constants.ACTIVE_BATCH == batch_status.intValue())
                    batchMap.put(batch_status, batch);
                return batch;
            } else {
                log.error("Not Found Active Batch");
                throw new NotFoundException();
            }

        } catch (SQLException e) {
            log.error("getBatch - Error = " + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            ChannelFacadeNew.getDbConnectionPool().returnConnection(connection);
        }
    }

    public void getOpCode(Connection connection, String type) throws SQLException, NotFoundException {

        String sql = "select SGB_TX_CODE,TX_CODE from TBCFSTXTYPE where TX_CODE in('";

        if (type.equalsIgnoreCase(Constants.BRANCH_CHANNEL)) {
            sql = sql + TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE + "','" + TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE;
        } else {
            sql = sql + Constants.EBNK_ACH_OPERATION_CODE + "','" + Constants.EBNK_RTGS_OPERATION_CODE;
        }
        sql = sql + "') with ur";
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String txCode = resultSet.getString("TX_CODE");
                if (txCode.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE) ||
                        txCode.trim().equalsIgnoreCase(Constants.EBNK_ACH_OPERATION_CODE))
                    achOpCode = resultSet.getString("SGB_TX_CODE");
                else
                    rtgsOpCode = resultSet.getString("SGB_TX_CODE");
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    public void getReverseOpCode(Connection connection, String type) throws SQLException, NotFoundException {

        String sql = "select SGB_TX_CODE,TX_CODE from TBCFSTXTYPE where TX_CODE in('";
        if (type.equalsIgnoreCase(Constants.BRANCH_CHANNEL)) {
            sql = sql + TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL + "','" + TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE_REVERSAL;
        } else {
            sql = sql + Constants.EBNK_ACH_REVERSE_OPERATION_CODE + "','" + Constants.EBNK_RTGS_REVERSE_OPERATION_CODE;
        }
        sql = sql + "') with ur";
        Statement statement = null;
        ResultSet resultSet = null;
        achOpCode = null;
        rtgsOpCode = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String txCode = resultSet.getString("TX_CODE");
                if (txCode.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL) ||
                        txCode.trim().equalsIgnoreCase(Constants.EBNK_ACH_REVERSE_OPERATION_CODE))
                    achOpCode = resultSet.getString("SGB_TX_CODE");
                else
                    rtgsOpCode = resultSet.getString("SGB_TX_CODE");
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }

    public Connection createConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            log.error(e);
        }
        return connection;
    }

    class InfoClass {
        private String txSrc;
        private String transactionType;
        private String branchCode;
        private long maxLogId;
        private int countTransaction;

        InfoClass(String txSrc, String transactionType, String branchCode, long maxLogId, int countTransaction) {
            this.txSrc = txSrc;
            this.transactionType = transactionType;
            this.branchCode = branchCode;
            this.maxLogId = maxLogId;
            this.countTransaction = countTransaction;
        }

        public String getTxSrc() {
            return txSrc;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getBranchCode() {
            return branchCode;
        }

        public long getMaxLogId() {
            return maxLogId;
        }

        public long getCountTransaction() {
            return countTransaction;
        }

    }
}
