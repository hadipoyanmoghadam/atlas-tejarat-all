package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.general.RefNoGeneratorNew;
import dpi.atlas.model.tj.entity.SGBBatch;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.util.DateUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.sql.*;

/**
 * User: Shahram Boroon
 * Date: Nov 5, 2005
 * Time: 4:05:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SGBFileApplierReverseNew extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SGBFileApplierReverseNew.class);
    private long commit_size = 1000;
    String giftCardOperationCode;

    public synchronized void doProcess(CMMessage msg, Map holder) throws CFSFault {
        String commitSizeStr = msg.getAttributeAsString(CFSConstants.APPLIER_COMMIT_SIZE);
        if (commitSizeStr != null && !commitSizeStr.equals(""))
            commit_size = Long.parseLong(commitSizeStr);
                             
        PreparedStatement cfsCardAccountPSSelect = null;
        PreparedStatement cfsCardAccountPSUpdate = null;
        PreparedStatement customerAccountPSSelect = null;
        PreparedStatement customerAccountPSUpdate = null;
        PreparedStatement sgb_log_ps = null;
        PreparedStatement cfstx_ps = null;
        Connection connection = null;

        Connection selectConnection = null;
        Statement stmntLogRS = null;
        ResultSet sgbLogRS = null;

        try {
            selectConnection = CFSFacadeNew.getDbConnectionPool().getConnection();
            connection = CFSFacadeNew.getDbConnectionPool().getConnection();
            String sql = "select balance from tbCustomerAccounts where account_no = ?";
            customerAccountPSSelect = connection.prepareStatement(sql);
            sql = "select MAX_TRANS_LIMIT from tbCfsCardAccount where account_no = ? and pan = ?";
            cfsCardAccountPSSelect = connection.prepareStatement(sql);
            sql = "update tbCustomerAccounts set balance = balance + ? where account_no = ?";
            customerAccountPSUpdate = connection.prepareStatement(sql);
            sql = "update tbCFSCardAccount set MAX_TRANS_LIMIT = MAX_TRANS_LIMIT + ? where account_no = ? and pan = ?";
            cfsCardAccountPSUpdate = connection.prepareStatement(sql);
            sql = "update tb_sgb_Log set proccess_status = '2' where log_id = ? ";
            sgb_log_ps = connection.prepareStatement(sql);
            sql = "Insert into tbCFSTX " +
                    "(TX_PK,PARTNO,TX_SEQ,TX_CODE,TX_SRC," +
                    "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                    "CREATION_DATE,CREATION_TIME," +
                    "SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                    "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED," +
                    "IS_REVERSAL_TXN,SRC_ACC_BALANCE, " +
                    "CARD_NO,SGB_ACTION_CODE,SGB_BRANCH_ID,BRANCH_DOCNO,LAST_TRANS_LIMIT,DEST_ACC_BALANCE  " +
                    ") values(?,?,?,?,'" + CFSConstants.TXN_SRC_SGB + "'," +
                    "?,'364',?,?,?,?,?,?,?,?,?,0,0,'0',?,?,?,?,?,?,?,?)";
            cfstx_ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            log.error("Can not build preparedStatement...");
        }

        try {
            SGBBatch sgbCutOveredBatch ;
            try {
                sgbCutOveredBatch = CFSFacadeNew.getLastSGBBatchWithStatus2();
                log.info("CutOvered Batch = " + sgbCutOveredBatch);
                if (sgbCutOveredBatch.getBatchStatus().equals("" + CFSConstants.REVERSED_BATCH))
                    throw new CFSFault(CFSFault.FLT_SGB_BATCH_NOT_AVAILABLE, CFSFault.FLT_SGB_BATCH_NOT_AVAILABLE);
                sgbCutOveredBatch.setBatchStatus(String.valueOf(CFSConstants.REVERSED_BATCH));
            } catch (NotFoundException e) {
                log.error("Can not found SGB Batch with status = 2, therefor we will return");
                try {
                    selectConnection.rollback();
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                return;
            }


//            sgbLogRS = CFSFacadeNew.getSGBLogsWithBatchPk(sgbCutOveredBatch.getBatchPk());
            String hql = "select * from TB_SGB_Log where sgb_batch_pk = " + sgbCutOveredBatch.getBatchPk()+ " and proccess_status <> '2' order by log_id with ur";
            stmntLogRS = selectConnection.createStatement();
            sgbLogRS = stmntLogRS.executeQuery(hql);


            RefNoGeneratorNew generator = RefNoGeneratorNew.getInstance();

            Batch batch = CFSFacadeNew.getBatchNoCache((long) CFSConstants.ACTIVE_BATCH, Constants.OPERATOR_AND);


            long rowInserted_count = 0;
            while (sgbLogRS.next()) {
                String process_status = sgbLogRS.getString("PROCCESS_STATUS");
                long logId = sgbLogRS.getLong("LOG_ID");
                if (process_status.equals("1")) {
                    String accountNo = sgbLogRS.getString("ACCOUNT_NO");
                    accountNo = "00" + accountNo;
                    String debit_credit = sgbLogRS.getString("DEBIT_CREDIT");
                    long amount = Long.parseLong(sgbLogRS.getString("AMOUNT"));
                    long formattedBalance;
                    String tx_code;
                    String src_account;
                    String dest_account;

                    long srcAccountBalance = 0;
                    long destAccountBalance = 0;

                    if (!debit_credit.equals("1")) {
                        formattedBalance = amount;
                        tx_code = TJCommand.CMD_DEPOSIT;
                        src_account = "0000000000000";
                        dest_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                    } else {
                        formattedBalance = -amount;
                        tx_code = TJCommand.CMD_CASH;
                        src_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                        dest_account = "0000000000000";
                    }

                    try {
                        String operationCode = sgbLogRS.getString("OPERATION_CODE");
                        String card_no = sgbLogRS.getString("CARD_NO");
                        long max_trans_limit = -1000000000000000000L;

                        if (operationCode.equalsIgnoreCase(giftCardOperationCode))
                            max_trans_limit = CFSFacadeNew.getCfsCardAccountWithoutStatus(cfsCardAccountPSSelect, accountNo, card_no);

                        long last_balance = CFSFacadeNew.getCustomerBalance(customerAccountPSSelect, accountNo);
                        if (tx_code.equalsIgnoreCase(TJCommand.CMD_DEPOSIT)) {
                            destAccountBalance = last_balance + formattedBalance;
                        } else if (tx_code.equalsIgnoreCase(TJCommand.CMD_CASH)) {
                            srcAccountBalance = last_balance + formattedBalance;
                        }

                        String branch_id = sgbLogRS.getString("BRANCH_CODE");
                        CFSFacadeNew.saveSGB2Tx(cfstx_ps, generator.generateRefNo("TT"), sgbLogRS.getString("JOURNAL_NO"), tx_code,
                                amount, src_account, dest_account, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                branch_id, batch.getBatchPk(), new java.sql.Timestamp(System.currentTimeMillis()),
                                sgbLogRS.getString("EFFECTIVE_DATE"), sgbLogRS.getString("TIME"), card_no, operationCode, branch_id, sgbLogRS.getString("DOCUMENT_NO"), max_trans_limit, srcAccountBalance,
                                destAccountBalance,"");

                        if (!operationCode.equalsIgnoreCase(giftCardOperationCode)) {
                            CFSFacadeNew.updateCustomerBalance(customerAccountPSUpdate, accountNo, formattedBalance);
                        }

                        if (operationCode.equalsIgnoreCase(giftCardOperationCode))
                            CFSFacadeNew.updateCfsCardAccountMaxTransLimit(cfsCardAccountPSUpdate, accountNo, card_no, formattedBalance);

                        CFSFacadeNew.updateSGBLog(sgb_log_ps, logId);

                        rowInserted_count++;
                        if (rowInserted_count % commit_size == 0)
                            connection.commit();
                        log.debug("Updated successfully account = " + accountNo + ", Log_id = " + logId);
                    } catch (SQLException e) {
                        log.error("Can not update account = " + accountNo + ", Log_id = " + logId + ": " + e);
                        //                    connection.rollback();
                    } catch (NotFoundException e) {
                        log.error("Can not find account = " + accountNo + ", Log_id = " + logId + ": " + e);
                    }
                } else
                    //this will revers transactions that have not proccessed yet(process_status = 0)
                    //This only change this status....
                    CFSFacadeNew.updateSGBLog(sgb_log_ps, logId);


            }
            connection.commit();
            selectConnection.commit();
            sgbLogRS.close();
            customerAccountPSUpdate.close();
            cfstx_ps.close();

            if (sgbCutOveredBatch != null) {
                // Rename sgb file
                CFSFacadeNew.updateSGBBatch(sgbCutOveredBatch);
            } else {
                log.error("No active or cutover SGB batch exists.");
            }
        }
        catch (CFSFault e) {
            log.error(e.getMessage());
            try {
                selectConnection.rollback();
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;

        } catch (SQLException e) {
            log.error(e);
            try {
                selectConnection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        }
        finally {
            try {
                if (sgbLogRS != null) sgbLogRS.close();
                if (stmntLogRS != null) stmntLogRS.close();
                CFSFacadeNew.getDbConnectionPool().returnConnection(selectConnection);
                CFSFacadeNew.getDbConnectionPool().returnConnection(connection);           
            } catch (SQLException e) {
                log.error("Error in closing sgbLogRS or stmntLogRS - error = " + e);
            }
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        giftCardOperationCode = cfg.get(CFSConstants.GIFT_CARD_APPLIER_OPERATION_CODE);     

    }
}
