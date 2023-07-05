package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.FormatException;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.general.RefNoGeneratorNew;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.SGBBatch;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.CustomerAccount;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.calendar.Calendar;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.DateUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.*;

/**
 * User: Shahram Boroon
 * Date: Nov 5, 2005
 * Time: 4:05:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SGBFileApplierNew extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SGBFileApplierNew.class);
    private String filePath;
    private String outPutPath;
    private String smsOutPutPath;
    private String smsOutPutPath4Backup;
    private long commit_size = 1000;
    String giftCardOperationCode;
    String deliveryAndBlockOperationCode;
    private ArrayList deliveryAndBlockOperationCodeArray = new ArrayList();
    private BufferedWriter bufferedWriter;
    private BufferedWriter SMSBufferedWriter;


    public synchronized void doProcess(CMMessage msg, Map holder) throws CFSFault {
        System.out.println(" *********** SGBFileApplierNew :: doProcess ***********");
        String commitSizeStr = msg.getAttributeAsString(CFSConstants.APPLIER_COMMIT_SIZE);
        if (commitSizeStr != null && !commitSizeStr.equals(""))
            commit_size = Long.parseLong(commitSizeStr);
        List<String> smsTempList = new ArrayList<String>();
        List<String> smsFinalList = new ArrayList<String>();
        Connection selectConnection;
        Statement stmntLogRS = null;
        ResultSet sgbLogRS = null;
        String hql;
        Connection connection;
        PreparedStatement customerAccountPSSelect;
        PreparedStatement cfsCardAccountPSSelect;
        PreparedStatement customerAccountPSUpdate;
        PreparedStatement cfsCardAccountPSUpdate;
        PreparedStatement sgb_log_ps;
        PreparedStatement cfstx_ps;
        PreparedStatement sequenceSelect;
        PreparedStatement sequenceUpdate;
        long[] sequence=new long[]{0};
        try {
            selectConnection = CFSFacadeNew.getDbConnectionPool().getConnection();
            connection = CFSFacadeNew.getDbConnectionPool().getConnection();
            connection.setAutoCommit(false);
            String sql = "select balance, subsidy_amount,lock_STATUS from tbCustomerAccounts where account_no = ? for update";
            customerAccountPSSelect = connection.prepareStatement(sql);
            sql = "select MAX_TRANS_LIMIT from tbCfsCardAccount where account_no = ? and pan = ? for update";
            cfsCardAccountPSSelect = connection.prepareStatement(sql);
            sql = "update tbCustomerAccounts set balance = balance + ? , subsidy_amount = ? where account_no = ?";
            customerAccountPSUpdate = connection.prepareStatement(sql);
            sql = "update tbCFSCardAccount set MAX_TRANS_LIMIT = MAX_TRANS_LIMIT + ? where account_no = ? and pan = ?";
            cfsCardAccountPSUpdate = connection.prepareStatement(sql);
            sql = "update tb_sgb_Log set proccess_status = '1' where log_id = ? ";
            sgb_log_ps = connection.prepareStatement(sql);
            sql = "Insert into tbCFSTX " +
                    "(TX_PK,PARTNO,TX_SEQ,TX_CODE,TX_SRC," +
                    "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                    "CREATION_DATE,CREATION_TIME," +
                    "SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                    "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED," +
                    "IS_REVERSAL_TXN,SRC_ACC_BALANCE, " +
                    "CARD_NO,SGB_ACTION_CODE,SGB_BRANCH_ID,BRANCH_DOCNO,LAST_TRANS_LIMIT,DEST_ACC_BALANCE,SESSION_ID " +
                    ") values(?,?,?,?,'" + CFSConstants.TXN_SRC_SGB + "'," +
                    "?,'364',?,?,?,?,?,?,?,?,?,0,0,'0',?,?,?,?,?,?,?,?)";
            cfstx_ps = connection.prepareStatement(sql);
            sql= "select Sequencer from tbSequence where name = '" + CFSConstants.APPLIER_SESSION_ID + "' with ur";
            sequenceSelect=connection.prepareStatement(sql);
            sql="update tbSequence set SEQUENCER = ?  where name = '" + CFSConstants.APPLIER_SESSION_ID + "' ";
            sequenceUpdate=connection.prepareStatement(sql);
        } catch (SQLException e) {
            log.error(" *********** SGBFileApplierNew :: Can not build preparedStatement, SQLException " + e.toString() + " ***********");
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        }


        SGBBatch sgbCutOverBatch = null;
        try {
            try {
                sgbCutOverBatch = CFSFacadeNew.getSGBBatch(String.valueOf(CFSConstants.ACTIVE_BATCH));
                sgbCutOverBatch.setBatchStatus(String.valueOf(CFSConstants.CUT_OVER_BATCH));
                CFSFacadeNew.updateSGBBatch(sgbCutOverBatch);
            } catch (NotFoundException e) {
                try {
                    log.error(" *********** SGBFileApplierNew :: No sgbBatch, Error = " + e);
                    sgbCutOverBatch = CFSFacadeNew.getSGBBatch(String.valueOf(CFSConstants.CUT_OVER_BATCH));
                } catch (NotFoundException e1) {
                    log.error(e1);
                }
            }
            try {
                sequence[0]=CFSFacadeNew.getSessionId(sequenceSelect);
            } catch (NotFoundException e) {
                    log.error(e);
            }

            RefNoGeneratorNew generator = RefNoGeneratorNew.getInstance();
            Batch batch = CFSFacadeNew.getBatchNoCache((long) CFSConstants.ACTIVE_BATCH, Constants.OPERATOR_AND);

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(new File(outPutPath)));
                if (bufferedWriter != null)
                    bufferedWriter.write("CFSAPPLIER" + sgbCutOverBatch.getSgbFileDate() + "\n");
            } catch (IOException e) {
                log.error(" *********** SGBFileApplier :: ERROR in opening output file" + e.getMessage());
            }

            long[] rowInserted_count = new long[]{0};
            long[] loop_count = new long[]{0};
            List<String> accountNotFound = new ArrayList<String>();
            long t1 = System.currentTimeMillis();

            boolean isDailyApplier = false;
            String fileCycle = (sgbCutOverBatch.getSgbFileDate() != null ? sgbCutOverBatch.getSgbFileDate().substring(8).trim() : "");
            if (fileCycle.equals("") || fileCycle.equals("00")){
                chargeAccountsProcess(msg,holder,selectConnection,connection,sgbCutOverBatch,generator,batch,rowInserted_count,loop_count,sequence);
                hql = "select * from TB_SGB_Log where proccess_status = '0' order by sgb_batch_pk desc,log_id with ur";
            }else {
                hql = "select * from TB_SGB_Log where SGB_BATCH_PK = " + sgbCutOverBatch.getBatchPk() + " and proccess_status = '0' with ur";
                isDailyApplier = true;
            }

            stmntLogRS = selectConnection.createStatement();
            sgbLogRS = stmntLogRS.executeQuery(hql);

            while (sgbLogRS.next()) {
                loop_count[0]++;
                if (loop_count[0] % commit_size == 0) {
                    log.error("****************************** Total rows proccessed = " + loop_count[0] + " ---- proccessed records / second = " + (loop_count[0] * 1000 / (System.currentTimeMillis() - t1)) + " ********************************");
                }

                long sgbBatchPk = sgbLogRS.getLong("SGB_BATCH_PK");
                long logId = sgbLogRS.getLong("LOG_ID");
                String accountNo = sgbLogRS.getString("ACCOUNT_NO");
                accountNo = "00" + accountNo;
                String debit_credit = sgbLogRS.getString("DEBIT_CREDIT");
                long amount = Long.parseLong(sgbLogRS.getString("AMOUNT"));
                long formattedBalance;
                long newSubsidyAmount = 0;
                String tx_code;
                String src_account;
                String dest_account;
                long srcAccountBalance = 0;
                long destAccountBalance = 0;
                String creationDate = sgbLogRS.getString("CREATION_DATE");

                try {
                    if(accountNotFound.contains(accountNo)){
                        continue;
                    }

                    String operationCode = sgbLogRS.getString("OPERATION_CODE");

                    if (deliveryAndBlockOperationCodeArray.contains(operationCode)) { // subsidy deposit
                        //TODO at the moment comment but should be corrected!!!
                        if (debit_credit.equals("1")) { // it's not deposit !!!
                            formattedBalance = amount; //TODO it's possible to have a negative amount??? it results deduction 'balance' and 'unblockedAmount', is it legal?????
                            newSubsidyAmount = amount;
                            tx_code = TJCommand.CMD_DEPOSIT;
                            src_account = "0000000000000";
                            dest_account = "00" + sgbLogRS.getString("ACCOUNT_NO");

                        } else {
                            formattedBalance = 0;
                            newSubsidyAmount = -amount; //TODO it should consider debit_credit ???
                            tx_code = TJCommand.CMD_UNBLOCK;
                            src_account = "0000000000000";
                            dest_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                        }

                    } else if (debit_credit.equals("1")) { // Normal deposit
                        formattedBalance = amount;
                        tx_code = TJCommand.CMD_DEPOSIT;
                        src_account = "0000000000000";
                        dest_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                    } else {  // Normal cash
                        formattedBalance = -amount;
                        tx_code = TJCommand.CMD_CASH;
                        src_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                        dest_account = "0000000000000";
                    }


                    String card_no = sgbLogRS.getString("CARD_NO");
                    String cardnoTmp = sgbLogRS.getString("CARD_NO").substring(6);
                    long max_trans_limit = -1000000000000000000L;

                    if (operationCode.equalsIgnoreCase(giftCardOperationCode)) {
                        long cardBalance;
                        if (card_no.startsWith(Constants.BANKE_TEJARAT_BIN) && cardnoTmp.startsWith("49"))
                            cardBalance = Long.valueOf(cardnoTmp.substring(2, 4)) * 10000;
                        else if (card_no.startsWith(Constants.BANKE_TEJARAT_BIN) && cardnoTmp.startsWith("4"))
                            cardBalance = Long.valueOf(cardnoTmp.substring(1, 4)) * 100000;
                        else if(card_no.startsWith(Constants.BANKE_TEJARAT_BIN) || (card_no.startsWith(Constants.BANKE_TEJARAT_BIN_NEW) && (!cardnoTmp.startsWith("2") && !cardnoTmp.startsWith("3")))) {
                            if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                                try {
                                    if (bufferedWriter != null)
                                        bufferedWriter.write("GiftCard: Log_ID = " + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo + "\n");
                                } catch (IOException e) {
                                    log.error(" *********** SGBFileApplier :: ERROR in output file" + e.getMessage());
                                }
                            }
                            throw new Exception("ERROR In Input Card Number: Input Gift Card is not a correct Gift Card : Log_ID = "
                                    + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo);
                        }
                        long tbmax_trans_limit;
                        try {
                            tbmax_trans_limit = CFSFacadeNew.getCfsCardAccountWithoutStatus(cfsCardAccountPSSelect, accountNo, card_no);
                        } catch (Exception e) {
                            log.error("@@@@@@@@@ Error in getCfsCardAccountWithoutStatus -- Error = " + e);
                            throw e;
                        }

                        if (tbmax_trans_limit == max_trans_limit) {
                            if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                                try {
                                    if (bufferedWriter != null)
                                        bufferedWriter.write("GiftCard: Log_ID = " + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo + "\n");
                                } catch (IOException e) {
                                    log.error(" *********** SGBFileApplier :: ERROR in output file" + e.getMessage());
                                }
                            }
                            throw new Exception("ERROR In Card Table Properties: Input Gift Card transaction has wrong operation on a non Gift Card: Log_ID = "
                                    + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo);
                        }
//                        if (tbmax_trans_limit + formattedBalance > cardBalance) {
//                            if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
//                                try {
//                                    if (bufferedWriter != null)
//                                        bufferedWriter.write("GiftCard: Log_ID = " + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo + "\n");
//                                } catch (IOException e) {
//                                    log.error(" *********** SGBFileApplier :: ERROR in output file" + e.getMessage());
//                                }
//                            }
//                            throw new Exception("ERROR : Gift Card Max_Trans_limit will be greater than its actual balance : Log_ID = "
//                                    + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo);
//                        }
                        max_trans_limit = tbmax_trans_limit;
                    }


                    long last_balance;
                    long currentSubsidyAmount;

                    CustomerAccount customerAccount;
                    try {
                        customerAccount = CFSFacadeNew.getCustomerBalanceObj(customerAccountPSSelect, accountNo);
                        last_balance = customerAccount.getBalance();
                        currentSubsidyAmount = customerAccount.getSubsidy_amount();
                        if (currentSubsidyAmount > 9999999999999999L) {
                            log.error("@@@@@@@@@ Error in getCustomerBalance -- Subsidy_amount is too long --currentSubsidyAmount= " + currentSubsidyAmount);
                            throw new FormatException("Error in getCustomerBalance -- Subsidy_amount is too long!!!");

                        }

                    } catch (NotFoundException e) {
                        log.error("@@@@@@@@@ Error in getCustomerBalance -- Error = " + e);
                        accountNotFound.add(accountNo);
                        throw e;
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in getCustomerBalance -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }

                    if (deliveryAndBlockOperationCodeArray.contains(operationCode)) {// TODO we suppose that (currentSubsidyAmount + newSubsidyAmount) can be <0
                        // TODO for operationCode.equalsIgnoreCase(unBlockOperationCode), in the future, probaly this condition can be set -> if (currentSubsidyAmount + newSubsidyAmount) < 0 then newSubsidyAmount = - currentSubsidyAmount
                        if ((currentSubsidyAmount + newSubsidyAmount) < 0) {
                            newSubsidyAmount = -currentSubsidyAmount;
                            String errorMessage = "final_subsidy_amount  = currentSubsidyAmount + newSubsidyAmount = " + (currentSubsidyAmount + newSubsidyAmount) + " !!! < 0  but it's set to 0 - log_id = "
                                    + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo + " -- operationCode = " + operationCode;
                            log.info(errorMessage);
                            if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                                try {
                                    if (bufferedWriter != null)
                                        bufferedWriter.write("subsidy_amount: log_id = " + logId + ", Card_no = " + card_no + ", Account_No = " + accountNo + " -- operationCode = " + operationCode + "\n");
                                } catch (IOException e1) {
                                    log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                                }
                            }
                        }
                    }


                    if (tx_code.equalsIgnoreCase(TJCommand.CMD_UNBLOCK)) {
                        destAccountBalance = last_balance;
                    } else if (tx_code.equalsIgnoreCase(TJCommand.CMD_DEPOSIT)) {
                        destAccountBalance = last_balance + formattedBalance;
                    } else if (tx_code.equalsIgnoreCase(TJCommand.CMD_CASH)) {
                        srcAccountBalance = last_balance + formattedBalance;
                    }
                    String branch_id = sgbLogRS.getString("BRANCH_CODE");
                    try {
                        if (!tx_code.equalsIgnoreCase(TJCommand.CMD_UNBLOCK)) {

                            String refNo = generator.generateRefNo("TT");
                            String session_id=ISOUtil.zeropad(String.valueOf(++sequence[0]), 12);
                            CFSFacadeNew.saveSGB2Tx(cfstx_ps, refNo, sgbLogRS.getString("JOURNAL_NO"), tx_code,
                                    amount, src_account, dest_account, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                    branch_id, batch.getBatchPk(), new Timestamp(System.currentTimeMillis()),
                                    sgbLogRS.getString("EFFECTIVE_DATE"), sgbLogRS.getString("TIME"), card_no, operationCode, branch_id, sgbLogRS.getString("DOCUMENT_NO"), max_trans_limit, srcAccountBalance,
                                    destAccountBalance,session_id);

                            if (isDailyApplier) {
                                String smsTempStr = generateSMSNotification(refNo, sgbLogRS.getString("EFFECTIVE_DATE"),
                                        sgbLogRS.getString("TIME"), src_account, dest_account,
                                        amount, srcAccountBalance, destAccountBalance, debit_credit, operationCode, branch_id,sgbLogRS.getString("DOCUMENT_NO"));
                                if (!smsTempStr.equals(""))
                                    smsTempList.add(smsTempStr);
                            }
                        }
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in saveSGB2Tx -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }


                    try {
                        if (!operationCode.equalsIgnoreCase(giftCardOperationCode)) {
                            CFSFacadeNew.updateCustomerBalance(customerAccountPSUpdate, accountNo, formattedBalance, (currentSubsidyAmount + newSubsidyAmount));
                        }
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in updateCustomerBalance -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }


                    try {
                        if (operationCode.equalsIgnoreCase(giftCardOperationCode))
                            CFSFacadeNew.updateCfsCardAccountMaxTransLimit(cfsCardAccountPSUpdate, accountNo, card_no, formattedBalance);
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in updateCfsCardAccountMaxTransLimit -- Error = " + e);
                        throw e;

                    }

                    try {
                        CFSFacadeNew.updateSGBLog(sgb_log_ps, logId);
                    } catch (SQLException e) {
                        log.error("@@@@@@@@@ Error in updateSGBLog -- Error = " + e);
                        throw e;
                    }
                    if (log.isDebugEnabled())
                        log.debug("Updated successfully account = " + accountNo + ", Log_id = " + logId);

                    rowInserted_count[0]++;

                    if (rowInserted_count[0] % commit_size == 0) {
                        try {
                            CFSFacadeNew.updateSessionId(sequenceUpdate, sequence[0]);
                        } catch (SQLException e) {
                            log.error("ROLLBACK !!!! - SQLException in updateSessionId - Error = " + e);
                            throw e;
                        }
                        connection.commit();
                        log.error("****************************** Total rowInserted_count = " + rowInserted_count[0] + " ********************************");
                        if (isDailyApplier) {
                            smsFinalList.addAll(smsTempList);
                            smsTempList.clear();
                        }
                    }
                } catch (SQLException e) {
                    log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - SQLException - Can not update account = " + accountNo + ", Log_id = " + logId + ": " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("SQLException: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }
                    connection.rollback();
                    smsTempList.clear();
                } catch (NotFoundException e) {
                    log.error("Can not find account = " + accountNo + ", Log_id = " + logId + ": " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("not find account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }

                } catch (FormatException e) {
                    log.error("Subsidy cannot be negative for  account = " + accountNo + ", Log_id = " + logId + ": " + e + " -just bypass it!!!!");
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("Subsidy: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }

                } catch (Exception e) {
                    log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - Exception - Can not update account = " + accountNo + ", Log_id = " + logId + " due to : " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("ROLLBACK: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }
                    connection.rollback();
                    smsTempList.clear();
                }
            }
            try {
                CFSFacadeNew.updateSessionId(sequenceUpdate, sequence[0]);
            } catch (SQLException e) {
                log.error("ROLLBACK !!!! - SQLException in updateSessionId - Error = " + e);
                connection.rollback();
            }
            selectConnection.commit();
            try {
                connection.commit();
                customerAccountPSUpdate.close();
                cfstx_ps.close();
                sequenceUpdate.close();
                if (isDailyApplier) {
                    smsFinalList.addAll(smsTempList);
                    smsTempList.clear();
                }
            } catch (SQLException e) {
                log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - SQLException in Final COMMIT - Error = " + e);
                connection.rollback();
                smsTempList.clear();
                if (customerAccountPSUpdate != null) customerAccountPSUpdate.close();
                if (cfstx_ps != null) cfstx_ps.close();
                if (sequenceUpdate != null) sequenceUpdate.close();

            }

            log.error("**********************************  END Of Applier *************************************** ");
            log.error("****************************** Total rows proccessed = " + loop_count[0] + "   *******************************");
            log.error("****************************** Total rowInserted_count = " + rowInserted_count[0] + " *******************************");
            log.error("****************************************************************************************** ");
            try {
                if (bufferedWriter != null)
                    bufferedWriter.write("**********************************  END Of Applier *************************************** " + "\n");
                bufferedWriter.write("****************************** Total rows proccessed = " + loop_count[0] + "   *******************************" + "\n");
            } catch (IOException e) {
                log.error(" *********** SGBFileApplier :: ERROR in opening output file" + e.getMessage());
            }


            if (sgbCutOverBatch != null) {
                // Rename sgb file
                String sgbFileDate = sgbCutOverBatch.getSgbFileDate();
                File sgbFile = new File(filePath);
                if (sgbFile != null && sgbFile.isFile())
                    sgbFile.renameTo(new File(filePath + "_" + sgbFileDate));
                //Ending Cutover and changing this status to Cut-Overed
                Calendar cal = Calendar.getInstance();
                sgbCutOverBatch.setBatchStatus(String.valueOf(CFSConstants.CLOSED_BATCH));
                sgbCutOverBatch.setBatchCloseDate(cal.getTime());
                CFSFacadeNew.updateSGBBatch(sgbCutOverBatch);
            } else {
                log.error("No active or cutover SGB batch exists.");
            }

            if (isDailyApplier) {
                log.error("********************************** Start Create SMS For Applier *************************************** ");

                try {

                    File applierSMS_Backup;
                    File applierSMS = new File(smsOutPutPath);
                    SMSBufferedWriter = new BufferedWriter(new FileWriter(applierSMS));
                    if (SMSBufferedWriter != null) {
                        SMSBufferedWriter.write("SMS4APPLIER" + sgbCutOverBatch.getSgbFileDate().substring(2) + "\n");
                        for (String smsStr : smsFinalList)
                            SMSBufferedWriter.write(smsStr);

                        SMSBufferedWriter.close();
                        applierSMS_Backup = new File(smsOutPutPath4Backup+ "_" + sgbCutOverBatch.getSgbFileDate());
                        copyFile(applierSMS, applierSMS_Backup);
                    }
                } catch (IOException e) {
                    log.error(" *********** SGBFileApplier :: ERROR in opening SMS output file" + e.getMessage());
                }
                log.error("**********************************  END Of Create SMS For Applier *************************************** ");

            }

        } catch (Exception e) {
            log.error(e.getMessage());
            try {
                selectConnection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            writeFile(smsFinalList,sgbCutOverBatch.getSgbFileDate());
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        } finally {
            try {
                if (sgbLogRS != null) sgbLogRS.close();
                if (stmntLogRS != null) stmntLogRS.close();
                CFSFacadeNew.getDbConnectionPool().returnConnection(selectConnection);
                CFSFacadeNew.getDbConnectionPool().returnConnection(connection);
                if (bufferedWriter != null)
                    bufferedWriter.close();
                if (SMSBufferedWriter != null)
                    SMSBufferedWriter.close();
                smsFinalList.clear();
            } catch (SQLException e) {
                log.error("Error in closing sgbLogRS or stmntLogRS - error = " + e);
            } catch (IOException e) {
                log.error(" *********** SGBFileApplier :: ERROR in output file" + e.getMessage());
            }
        }
    }

  private void writeFile( List<String> smsFinalList,String fileDate){
      log.error("********************************** Start Create SMS For Applier *************************************** ");

      try {
           if(smsFinalList.isEmpty()) return;
          File applierSMS_Backup;
          File applierSMS = new File(smsOutPutPath);
          SMSBufferedWriter = new BufferedWriter(new FileWriter(applierSMS));
          if (SMSBufferedWriter != null) {
              SMSBufferedWriter.write("SMS4APPLIER" + fileDate + "\n");
              for (String smsStr : smsFinalList)
                  SMSBufferedWriter.write(smsStr);

              SMSBufferedWriter.close();
              applierSMS_Backup = new File(smsOutPutPath4Backup+ "_" + fileDate);
              copyFile(applierSMS, applierSMS_Backup);
          }
      } catch (IOException e) {
          log.error(" *********** SGBFileApplier :: ERROR in opening SMS output file" + e.getMessage());
      }
      log.error("**********************************  END Of Create SMS For Applier *************************************** ");

  }

    private static void copyFile(File source, File dest) throws IOException {

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        try {

            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    private String generateSMSNotification(String refNo, String date, String time, String src_account, String dest_account,
                                           long amount, long srcAccountBalance, long destAccountBalance, String debit_credit,
                                           String operationCode, String branch_id,String checkNo) {
        if (operationCode.equalsIgnoreCase(giftCardOperationCode))
            return "";
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(ISOUtil.zeropad(refNo, 32)).
                    append(date).
                    append(time.substring(0, 4));
            String accountNo = "";
            String balance = "";
            if (debit_credit.equals(Constants.DEPOSIT)) {
                accountNo = dest_account.substring(2);
                balance = ISOUtil.zeropad(String.valueOf(destAccountBalance), 15);
            } else {
                accountNo = src_account.substring(2);
                balance = ISOUtil.zeropad(String.valueOf(srcAccountBalance), 15);
                debit_credit = Constants.WITHDRAW;
            }
            sb.append(accountNo).
                    append(ISOUtil.zeropad(String.valueOf(amount), 15)).
                    append(balance).append(debit_credit).
                    append(operationCode).
                    append(Constants.BRANCH_CHANNEL_CODE).
                    append(ISOUtil.zeropad(branch_id, 15)).
                    append(Constants.BANKE_TEJARAT_BIN_NEW)
                    .append(ISOUtil.zeropad(checkNo, 7))
                    .append("\n");

            return sb.toString();
        } catch (ISOException e) {
            log.error("ISOException in genarating SMSNotification for RefId:" + refNo);
            return "";
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {

        filePath = cfg.get(CFSConstants.FILE_PATH);
        outPutPath = cfg.get(CFSConstants.OUTPUT_PATH);
        smsOutPutPath = cfg.get(CFSConstants.SMS_OUTPUT_PATH);
        smsOutPutPath4Backup = cfg.get(CFSConstants.SMS_OUTPUT_PATH_BAK);

        giftCardOperationCode = cfg.get(CFSConstants.GIFT_CARD_APPLIER_OPERATION_CODE);
        if ((giftCardOperationCode == null) || giftCardOperationCode.equalsIgnoreCase("")) {
            log.fatal("giftCardOperationCode Field is not Specified");
            throw new ConfigurationException("giftCardOperationCode Field is not Specified");
        }

        deliveryAndBlockOperationCode = cfg.get(CFSConstants.DELIVERY_BLOCK_APPLIER_OPERATION_CODE);
        if ((deliveryAndBlockOperationCode == null) || deliveryAndBlockOperationCode.equalsIgnoreCase("")) {
            log.fatal("deliveryAndBlockOperationCode Field is not Specified");
            throw new ConfigurationException("deliveryAndBlockOperationCode Field is not Specified");
        }

        StringTokenizer deliveryAndBlockOperationCodeSt = new StringTokenizer(deliveryAndBlockOperationCode);
        while (deliveryAndBlockOperationCodeSt.hasMoreTokens()) {
            deliveryAndBlockOperationCodeArray.add(deliveryAndBlockOperationCodeSt.nextToken(",").trim());
        }
    }

    public synchronized void chargeAccountsProcess(CMMessage msg, Map holder,Connection selectConnection,Connection connection,SGBBatch sgbCutOverBatch,RefNoGeneratorNew generator,
                                                   Batch batch,long[] rowInserted_count,long[] loop_count,long[] sequence) throws CFSFault {
        System.out.println(" *********** Start charge accounts process ***********");
        Statement stmntLogRS = null;
        ResultSet sgbLogRS = null;
        String hql;
        PreparedStatement customerAccountPSSelect;
        PreparedStatement customerAccountPSUpdate;
        PreparedStatement sgb_log_ps;
        PreparedStatement cfstx_ps;
        PreparedStatement sequenceUpdate;
        try {

            String sql = "select balance, subsidy_amount,lock_STATUS from tbCustomerAccounts where account_no = ? for update";
            customerAccountPSSelect = connection.prepareStatement(sql);

            sql = "update tbCustomerAccounts set balance = balance + ? , subsidy_amount = ? where account_no = ?";
            customerAccountPSUpdate = connection.prepareStatement(sql);

            sql = "update tb_sgb_Log set proccess_status = '1' where log_id = ? ";
            sgb_log_ps = connection.prepareStatement(sql);

            sql = "Insert into tbCFSTX " +
                    "(TX_PK,PARTNO,TX_SEQ,TX_CODE,TX_SRC," +
                    "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                    "CREATION_DATE,CREATION_TIME," +
                    "SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                    "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED," +
                    "IS_REVERSAL_TXN,SRC_ACC_BALANCE, " +
                    "CARD_NO,SGB_ACTION_CODE,SGB_BRANCH_ID,BRANCH_DOCNO,LAST_TRANS_LIMIT,DEST_ACC_BALANCE,SESSION_ID " +
                    ") values(?,?,?,?,'" + CFSConstants.TXN_SRC_SGB + "'," +
                    "?,'364',?,?,?,?,?,?,?,?,?,0,0,'0',?,?,?,?,?,?,?,?)";
            cfstx_ps = connection.prepareStatement(sql);
            sql="update tbSequence set SEQUENCER = ?  where name = '" + CFSConstants.APPLIER_SESSION_ID + "' ";
            sequenceUpdate=connection.prepareStatement(sql);
        } catch (SQLException e) {
            log.error(" *********** SGBFileApplierNew :: Can not build preparedStatement, SQLException " + e.toString() + " ***********");
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        }
        try {

            hql = "select * from TB_SGB_Log where proccess_status = '0' and SGB_BATCH_PK = " + sgbCutOverBatch.getBatchPk() + " and account_no in ('00004903137','00004903129','00004903145') order by log_id with ur";

            stmntLogRS = selectConnection.createStatement();
            sgbLogRS = stmntLogRS.executeQuery(hql);

             while (sgbLogRS.next()) {
                loop_count[0]++;

                long sgbBatchPk = sgbLogRS.getLong("SGB_BATCH_PK");
                long logId = sgbLogRS.getLong("LOG_ID");
                String accountNo = sgbLogRS.getString("ACCOUNT_NO");
                accountNo = "00" + accountNo;
                String debit_credit = sgbLogRS.getString("DEBIT_CREDIT");
                long amount = Long.parseLong(sgbLogRS.getString("AMOUNT"));
                long formattedBalance;
                long newSubsidyAmount = 0;
                String tx_code;
                String src_account;
                String dest_account;
                long srcAccountBalance = 0;
                long destAccountBalance = 0;
                String creationDate = sgbLogRS.getString("CREATION_DATE");

                try {
                    String operationCode = sgbLogRS.getString("OPERATION_CODE");

                   if (debit_credit.equals("1")) { // Normal deposit
                        formattedBalance = amount;
                        tx_code = TJCommand.CMD_DEPOSIT;
                        src_account = "0000000000000";
                        dest_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                    } else {  // Normal cash
                        formattedBalance = -amount;
                        tx_code = TJCommand.CMD_CASH;
                        src_account = "00" + sgbLogRS.getString("ACCOUNT_NO");
                        dest_account = "0000000000000";
                   }


                    String card_no = sgbLogRS.getString("CARD_NO");
                    long max_trans_limit = -1000000000000000000L;

                    long last_balance;
                    long currentSubsidyAmount;

                    CustomerAccount customerAccount;
                    try {
                        customerAccount = CFSFacadeNew.getCustomerBalanceObj(customerAccountPSSelect, accountNo);
                        last_balance = customerAccount.getBalance();
                        currentSubsidyAmount = customerAccount.getSubsidy_amount();
                        if (currentSubsidyAmount > 9999999999999999L) {
                            log.error("@@@@@@@@@ Error in getCustomerBalance -- Subsidy_amount is too long --currentSubsidyAmount= " + currentSubsidyAmount);
                            throw new FormatException("Error in getCustomerBalance -- Subsidy_amount is too long!!!");

                        }
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in getCustomerBalance -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }


                   if (tx_code.equalsIgnoreCase(TJCommand.CMD_DEPOSIT)) {
                        destAccountBalance = last_balance + formattedBalance;
                    } else if (tx_code.equalsIgnoreCase(TJCommand.CMD_CASH)) {
                        srcAccountBalance = last_balance + formattedBalance;
                    }
                    String branch_id = sgbLogRS.getString("BRANCH_CODE");
                    try {
                        if (!tx_code.equalsIgnoreCase(TJCommand.CMD_UNBLOCK)) {

                            String refNo = generator.generateRefNo("TT");
                            String session_id=ISOUtil.zeropad(String.valueOf(++sequence[0]), 12);
                            CFSFacadeNew.saveSGB2Tx(cfstx_ps, refNo, sgbLogRS.getString("JOURNAL_NO"), tx_code,
                                    amount, src_account, dest_account, DateUtil.getSystemDate(), DateUtil.getSystemTime(),
                                    branch_id, batch.getBatchPk(), new Timestamp(System.currentTimeMillis()),
                                    sgbLogRS.getString("EFFECTIVE_DATE"), sgbLogRS.getString("TIME"), card_no, operationCode, branch_id, sgbLogRS.getString("DOCUMENT_NO"), max_trans_limit, srcAccountBalance,
                                    destAccountBalance,session_id);
                        }
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in saveSGB2Tx -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }


                    try {
                        if (!operationCode.equalsIgnoreCase(giftCardOperationCode)) {
                            CFSFacadeNew.updateCustomerBalance(customerAccountPSUpdate, accountNo, formattedBalance, (currentSubsidyAmount + newSubsidyAmount));
                        }
                    } catch (Exception e) {
                        log.error("@@@@@@@@@ Error in updateCustomerBalance -- Error = " + e);
                        throw e; //TODO for such cases, it's batter to issue a more gentle Eexception, there is no need to issu a general and hazard error which results a rollback!!!! such as 'FormatException'
                    }


                    try {
                        CFSFacadeNew.updateSGBLog(sgb_log_ps, logId);
                    } catch (SQLException e) {
                        log.error("@@@@@@@@@ Error in updateSGBLog -- Error = " + e);
                        throw e;
                    }
                    if (log.isDebugEnabled())
                        log.debug("Updated successfully account = " + accountNo + ", Log_id = " + logId);

                    rowInserted_count[0]++;

                } catch (SQLException e) {
                    log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - SQLException - Can not update account = " + accountNo + ", Log_id = " + logId + ": " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("SQLException: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }
                    connection.rollback();
                } catch (NotFoundException e) {
                    log.error("Can not find account = " + accountNo + ", Log_id = " + logId + ": " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("not find account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }

                } catch (FormatException e) {
                    log.error("Subsidy cannot be negative for  account = " + accountNo + ", Log_id = " + logId + ": " + e + " -just bypass it!!!!");
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("Subsidy: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }

                } catch (Exception e) {
                    log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - Exception - Can not update account = " + accountNo + ", Log_id = " + logId + " due to : " + e);
                    if (sgbBatchPk == sgbCutOverBatch.getBatchPk()) {
                        try {
                            if (bufferedWriter != null)
                                bufferedWriter.write("ROLLBACK: account = " + accountNo + ", Log_id = " + logId + "\n");
                        } catch (IOException e1) {
                            log.error(" *********** SGBFileApplier :: ERROR in output file" + e1.getMessage());
                        }
                    }
                    connection.rollback();
                }
            }
            try {
                CFSFacadeNew.updateSessionId(sequenceUpdate, sequence[0]);
            } catch (SQLException e) {
                log.error("ROLLBACK !!!! - SQLException in updateSessionId - Error = " + e);
                connection.rollback();
            }
            selectConnection.commit();
            try {
                connection.commit();
                customerAccountPSUpdate.close();
                cfstx_ps.close();
                sequenceUpdate.close();
            } catch (SQLException e) {
                log.error("ROLLBACK !!!! - Probably needs to reexecute Applier - SQLException in Final COMMIT - Error = " + e);
                connection.rollback();
                if (customerAccountPSUpdate != null) customerAccountPSUpdate.close();
                if (cfstx_ps != null) cfstx_ps.close();
                if (sequenceUpdate != null) sequenceUpdate.close();

            }

            log.error("**********************************  Total charge_account's rows processed = " + loop_count[0] + " *************************************** ");
            log.error("**********************************  END Of charge accounts process *************************************** ");
            try {
                if (bufferedWriter != null)
                    bufferedWriter.write("**********************************  Total charge_account's rows processed = " + loop_count[0] + " *************************************** " + "\n");
                    bufferedWriter.write("**********************************  END Of charge accounts process *************************************** " + "\n");
            } catch (IOException e) {
                log.error(" *********** SGBFileApplier :: ERROR in opening output file" + e.getMessage());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            try {
                selectConnection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, e);
        } finally {
            try {
                if (sgbLogRS != null) sgbLogRS.close();
                if (stmntLogRS != null) stmntLogRS.close();
            } catch (SQLException e) {
                log.error("Error in closing sgbLogRS or stmntLogRS - error = " + e);
            }
        }
    }

}
