package dpi.atlas.model.tj.facade;

import branch.dpi.atlas.model.tj.entity.AccountHistory;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.AccountType;
import dpi.atlas.model.entity.cms.Cmparam;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.*;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import dpi.atlas.util.Constants;
import branch.dpi.atlas.util.FarsiUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 27, 2007
 * Time: 4:15:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class CFSFacadeNew {
    private static Log log = LogFactory.getLog(CFSFacadeNew.class);
    static Map accountTypeMap = new HashMap();
    static Map accountTypeMinBalanceMap = new HashMap();
    static Map customerTmplSrvTxValueMap = new HashMap();
    static Map deviceMap = new HashMap();
    static Map accountRangeMap = new HashMap();
    static Map branchMap = new HashMap();
    static Map giftAccountsMap = new HashMap();
    static Map giftCardMap = new HashMap();
    static Map giftAccCustomerIdMap = new HashMap();
    static Map txTypeSgbTxCodeMap = new HashMap();
    static Map txTypeSPWChannelCodeMap = new HashMap();
    static Map imdMap = new HashMap();
    static Map batchMap = new HashMap();
    static DBConnectionPoolHit dbConnectionPool;
    static Map cmParametersByIDMap = new HashMap();
    static Map<String,Map<Long,String>> cmParamMap = new HashMap<String, Map<Long,String>>();
    static Map<String, Long> minBalance = new HashMap();


    static LinkedList _DBPool = new LinkedList();
    static long usedConnectionCount = 0;
    static long unUsedConnectionCount = 0;

    private static final String datasourceName = "tj-CFS-Datasource";

    public static Map<String, Long> getMinBalance() {
        return minBalance;
    }

    public static Map<String, Map<Long, String>> getCmParamMap() {
        return cmParamMap;
    }

    public static long getUsedConnectionCount() {
        return usedConnectionCount;
    }

    public static long getUnUsedConnectionCount() {
        return unUsedConnectionCount;
    }

    public static String getDBPoolHandlerNames() {
        return "CFSFacadeNew DBC size = " + _DBPool.size();

    }


    public static synchronized void initialize(int initialPoolSize, int maxPoolSizee, int optimalPoolSize) throws SQLException, Exception {
        if (dbConnectionPool != null)
            return;
        dbConnectionPool = new DBConnectionPoolHit(initialPoolSize, maxPoolSizee, datasourceName, optimalPoolSize);
    }

    public static DBConnectionPoolHit getDbConnectionPool() { //TODO  TEMPORARY !!! jus for SGBFileReaderNew and ...., must be modified
        return dbConnectionPool;
    }

    public static void insertCFSTxLog(String sessionId, String messageId, String logDate, String logTime, String txType,
                                      String serviceType, String actionCode, String txString, long batchPk) throws SQLException {


        txString = (txString.length() > 1000 ? txString.substring(0, 1000) : txString);
        int len = txString.length();

        StringBuffer newTxStringBuffer = new StringBuffer(1000);
        int offset = 254;
        int i = 0;
        int maxLimit = len / offset;

        for (i = 0; i < maxLimit; i++) {
            newTxStringBuffer.append(txString.substring(i * offset, i * offset + offset) + "'||'");
        }

        newTxStringBuffer.append(txString.substring(i * offset, txString.length()));

        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "insert into tbCFSTXLog " +
                    "(PARTNO,SESSION_ID,MESSAGE_ID,LOG_DATE,LOG_TIME," +
                    "TX_TYPE,SERVICE_TYPE,ACTION_CODE,TX_STRING,TX_DATETIME," +
                    "BATCH_PK) values(" +
                    getPartNo() + ",'" + sessionId + "','" + messageId + "','" + logDate + "','" + logTime + "','" +
                    txType + "','" + serviceType + "','" + actionCode + "','" + newTxStringBuffer + "',current_timestamp," +
                    batchPk + ")";

            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertCFSTxLog()#1 =   -- Error :: " + e + " -- newTxStringBuffer  = " + newTxStringBuffer);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void insertCFSTxLog(TxLog txLog) throws SQLException {
        String sql = "insert into tbCFSTXLog " +
                "(PARTNO,SESSION_ID,MESSAGE_ID,LOG_DATE,LOG_TIME," +
                "TX_TYPE,SERVICE_TYPE,ACTION_CODE,TX_STRING,TX_DATETIME," +
                "BATCH_PK) values(" +
                txLog.getPartNo() + ",'" + txLog.getSessionId() + "','" + txLog.getMessageId() + "','" + txLog.getLogDate() + "','" + txLog.getLogTime() + "','" +
                txLog.getTxType() + "','" + txLog.getServiceType() + "','" + txLog.getActionCode() + "','" + (txLog.getTxString().length() > 250 ? txLog.getTxString().substring(0, 250) : txLog.getTxString()) + "',current_timestamp," +
                txLog.getBatchPk() + ")";

        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertCFSTxLog()#2 =   -- Error :: " + e + " -- txString = " + txLog.getTxString());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long getFollowTx(String pan, String accountNo, String destAccNo, String txOrigDate, String txOrigTime, String sgbBranchId, String branchDocNo, String serviceType) throws SQLException, NotFoundException {
        String hql = "select Amount from tbCFSTx where  CARD_NO = '" + pan + "' and SRC_ACCOUNT_NO = '" + accountNo + "' and DEST_ACCOUNT_NO='" + destAccNo + "' " +
                " and TX_ORIG_DATE = '" + txOrigDate + "' and TX_ORIG_TIME = '" + txOrigTime + "' and SGB_BRANCH_ID = '" + sgbBranchId + "' and BRANCH_DOCNO = '" + branchDocNo + "'  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next())
                return (resultSet.getLong("AMOUNT"));
            else
                throw new NotFoundException("Transaction with pan = " + pan + ", accountNo = " + accountNo + ", orig_date = " + txOrigDate + ", origTime = " + txOrigTime + " does not exist");

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getFollowTx() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Tx getOriginalTx(String pan, String terminal_id, String date, String time, String rrn) throws SQLException, NotFoundException {
        String hql = "select * from tbCFSTx where CARD_NO = '" + pan + "' and DEVICE_CODE = '" + terminal_id + "'" +
                " and TX_ORIG_DATE = '" + date + "' and TX_ORIG_TIME = '" + time + "' and RRN = '" + rrn + "' and IS_REVERSAL_TXN = '0' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Tx tx = new Tx();
                tx.setTxPk(resultSet.getString("TX_PK"));
                tx.setSessionId(resultSet.getString("SESSION_ID"));
                tx.setMessageId(resultSet.getString("TX_SEQ"));
                tx.setTxCode(resultSet.getString("TX_CODE"));
                tx.setTxSrc(resultSet.getString("TX_SRC"));
                tx.setCurrency(resultSet.getString("CURRENCY"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setDeviceCode(terminal_id);
                tx.setCardNo(pan);
                tx.setCardSequenceNo(resultSet.getString("CARD_SEQUENCE_NO"));
                tx.setTxSequenceNumber(resultSet.getString("STAN"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setAcquirer(resultSet.getString("ACQUIRER"));
                tx.setSrcBranchId(resultSet.getString("SRC_BRANCH_ID"));
                tx.setTxOrigDate(date);
                tx.setTxOrigTime(time);
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setOrigTxPk(resultSet.getString("ORIG_TX_PK"));
                tx.setTxDateTime(resultSet.getTimestamp("TX_DATETIME"));
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setBatchPk(resultSet.getLong("BATCH_PK"));
                tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                tx.setPartNo(resultSet.getInt("PARTNO"));
                tx.setIsReversed(resultSet.getInt("ISREVERSED"));
                tx.setIsCutovered(resultSet.getInt("ISCUTOVERED"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setLastTransLimit(resultSet.getLong("LAST_TRANS_LIMIT"));
                tx.setDescription(resultSet.getString("DESC"));
                tx.setRRN(resultSet.getString("RRN"));
                tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID"));
                tx.setId1(resultSet.getString("ID1"));
                tx.setHostCode(resultSet.getString("HOSTCODE"));
                tx.setTerminalType(resultSet.getString("TERMINAL_TYPE"));
                tx.setSettlementDate(resultSet.getString("SETTLE_DATE"));
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO"));
                // for group card
                tx.setCardType(resultSet.getString("TX_SEQUENCE_NUMBER"));


                return (tx);
            } else {
                throw new NotFoundException("Transaction with pan = " + pan + ", terminal_id = " + terminal_id + ", orig_date = " + date + ", origTime = " + time + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getOriginalTx() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Card getCard(String cardNo) throws NotFoundException, SQLException {
        String hql = "select SEQUENCE_NO,CREATION_DATE,CREATION_TIME from tbCFSCard where PAN = '" + cardNo + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Card card = new Card(cardNo, resultSet.getString("SEQUENCE_NO"), resultSet.getString("CREATION_DATE"), resultSet.getString("CREATION_TIME"));
                return card;
            } else {
                throw new NotFoundException("No card exist for card no " + cardNo);
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getCard() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static CardAccount getCardAccountWithTransLimit(String accNo, String cardNo) throws NotFoundException, SQLException, Exception {
        String hql = "select SEQUENCE_NO,ACCOUNT_TYPE,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,MAX_TRANS_LIMIT,STATUS,SERIES,CARD_TYPE, SMS_REGISTER " +
                "from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accNo + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                CardAccount cardAccount = new CardAccount(cardNo, resultSet.getString("SEQUENCE_NO"),
                        resultSet.getString("ACCOUNT_TYPE"),
                        resultSet.getString("ACCOUNT_NO"),
                        resultSet.getString("CREATION_DATE"),
                        resultSet.getString("CREATION_TIME"),
                        resultSet.getLong("MAX_TRANS_LIMIT"),
                        resultSet.getInt("STATUS"),
                        resultSet.getInt("SERIES"));
                cardAccount.setCardType(resultSet.getString("CARD_TYPE"));
                cardAccount.setSmsRegister(resultSet.getString("SMS_REGISTER"));
                return cardAccount;
            } else {
                throw new NotFoundException("No Card Account found, " + accNo + "," + cardNo + ", " + "Account " + accNo + " Is Not Assigned To Card " + cardNo);
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getCardAccountWithTransLimit() =   -- Error1 :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } catch (Exception e) {
            log.error("CFSFacadeNew.getCardAccountWithTransLimit() =   -- Error2 :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static CardAccount getCardAccountWithTransLimit(String cardNo) throws NotFoundException, SQLException {
        String hql = "select SEQUENCE_NO,ACCOUNT_TYPE,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,MAX_TRANS_LIMIT,STATUS " +
                "from tbCFSCardAccount where PAN = '" + cardNo + "'  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                CardAccount cardAccount = new CardAccount(cardNo, resultSet.getString("SEQUENCE_NO"),
                        resultSet.getString("ACCOUNT_TYPE"),
                        resultSet.getString("ACCOUNT_NO"),
                        resultSet.getString("CREATION_DATE"),
                        resultSet.getString("CREATION_TIME"),
                        resultSet.getLong("MAX_TRANS_LIMIT"),
                        resultSet.getInt("STATUS"));
                return cardAccount;
            } else {
                throw new NotFoundException("No Card Account found, " + cardNo + ", " + "Account  Is Not Assigned To Card " + cardNo);
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getCardAccountWithTransLimit() =   *** Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Account getAccount(String acc) throws NotFoundException, SQLException {
        String hql = "select ACCOUNT_NO, CUSTOMER_ID, ACCOUNT_TYPE,ACCOUNT_SRC, STATUS,BALANCE,CREATION_DATE, CREATION_TIME," +
                "SGB_BRANCH_ID,SPARROW_BRANCH_ID, HOST_ID,SUBSIDY_AMOUNT,LOCK_STATUS,ACCOUNT_TITLE from tbCustomerAccounts where ACCOUNT_NO = '" + acc + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Account account = new Account(
                        resultSet.getString("ACCOUNT_NO"), resultSet.getString("CUSTOMER_ID"), resultSet.getString("ACCOUNT_TYPE"),
                        resultSet.getInt("ACCOUNT_SRC"), resultSet.getLong("STATUS"), resultSet.getLong("BALANCE"),
                        resultSet.getString("CREATION_DATE"), resultSet.getString("CREATION_TIME"), resultSet.getString("SGB_BRANCH_ID"),
                        resultSet.getString("SPARROW_BRANCH_ID"), resultSet.getInt("HOST_ID"), resultSet.getLong("SUBSIDY_AMOUNT"));
                account.setLockStatus(resultSet.getInt("LOCK_STATUS"));
                account.setAccountTitle(resultSet.getString("ACCOUNT_TITLE") != null ? resultSet.getString("ACCOUNT_TITLE").trim() : "0");
                return account;
            } else {
                throw new NotFoundException("Account " + acc + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getAccount() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void insertSMSLog(String txString, String sessionId, String messageType,
                                    String channelType, String accountNo) throws SQLException {


        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "insert into TB_SMS_LOG " +
                    "(PARTNO,SESSION_ID,MESSAGE_TYPE," +
                    "CHANNEL_TYPE,ACCOUNT_NO,TX_STRING,TX_DATETIME ) values(" +
                    getPartNo() + ",'" + sessionId + "','" + messageType + "', '" + channelType + "','" + accountNo + "','" + txString + "',current_timestamp )";

            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertSMSLog()#1 =   -- Error :: " + e + " -- TxString  = " + txString);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int updateAccount(String acc, int status, String origEditDate) throws SQLException {
        String hql = "update tbCustomerAccounts set status = " + status + " where ACCOUNT_NO = '" + acc + "' ";
        int count = 0;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            count = statement.executeUpdate(hql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccount() #1-- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccount() #2=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return count;
    }

    public static AccountType getAccountType(String accountType) throws NotFoundException, SQLException {
        if (accountTypeMap.containsKey(accountType))
            return (AccountType) accountTypeMap.get(accountType);

        String hql = "select * from tbAccountType where ID = '" + accountType + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                AccountType accountTypeObj = new AccountType(resultSet.getString("ID"), resultSet.getString("DESCRIPTION"),
                        resultSet.getString("HOST_ACC_TYPE1"),
                        resultSet.getString("HOST_ACC_TYPE2"),
                        new Long(resultSet.getLong("MIN_BALANCE")),
                        new Long(resultSet.getLong("WITHDRAW_LIMIT")));
                accountTypeMap.put(accountType, accountTypeObj);
                return accountTypeObj;
            } else {
                throw new NotFoundException("AccountType " + accountType + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getAccountType() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);

        }
    }

    public static boolean getHostLockStatus(int hostID) throws NotFoundException, SQLException {
        if (accountTypeMinBalanceMap.containsKey("" + hostID))
            return ((Boolean) accountTypeMinBalanceMap.get("" + hostID)).booleanValue();
        String hql = "select LOCK_STATUS from tbHosts where Host_ID = " + hostID + " with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                boolean isLocked = false;
                if (resultSet.getLong("LOCK_STATUS") == 1)
                    isLocked = true;
                accountTypeMinBalanceMap.put("" + hostID, new Boolean(isLocked));
                return isLocked;
            } else {
                throw new NotFoundException("Host " + hostID + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getHostLockStatus() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void txnDoRevokeTransaction(Tx tx) throws NotFoundException, ModelException, SQLException, ISOException,ServerAuthenticationException {

        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        long maxTransLimit = 0;
        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoRevokeTransaction()#1= -- Error :: " + e);
            throw new ISOException();
        }

        if (!tx.getTxSrc().equals(CFSConstants.TXN_SRC_SGB)) {
            Connection connection = null;
            try {
                connection = dbConnectionPool.getConnection();


                if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD)) {
                    List<String> amountExist=new ArrayList<String>();
                    amountExist = updateCardAccountTransactionalForRevoke(connection, srcAccountNo, tx.getCardNo(),tx.getTxCode(),tx.getSgbBranchId());
                    if(amountExist.size()>=2){
                        maxTransLimit=Long.valueOf(amountExist.get(0));
                        if(amountExist.get(1).equalsIgnoreCase("1")){
                            //DisCharge Before
                            tx.setAmount(maxTransLimit);
                            tx.setBranchDocNo(amountExist.get(2));
                            connection.commit();
                        }else{
                            //Discharge now
                            long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -maxTransLimit, tx.getTxCode(),0, 0, new HashMap());
                            tx.setSrc_account_balance(srcBalance);
                            tx.setAmount(maxTransLimit);
                            long destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, maxTransLimit, tx.getTxCode(), 0);
                            tx.setDest_account_balance(destBalance);
                            tx.setBranchDocNo(getGiftDocNo(tx.getCardNo()));
                            saveGiftTxTransactional(connection, tx, "");
                            tx.setTxSrc(Fields.SERVICE_CMS);
                            saveTxTransactional(connection, tx);
                            tx.setTxSrc(Fields.SERVICE_NASIM);
                            connection.commit();
                        }
                    }
                } else {
                    maxTransLimit = updateCardAccountTransactionalForRevoke(connection, srcAccountNo, tx.getCardNo());
                    long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -maxTransLimit, tx.getTxCode(), 0, 0, new HashMap());
                    tx.setSrc_account_balance(srcBalance);
                    tx.setAmount(maxTransLimit);
                    saveTxTransactional(connection, tx);
                    connection.commit();
                }

            } catch (NotFoundException e) {
                log.error("CFSFacadeNew.txnDoRevokeTransaction()#2 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoRevokeTransaction()#3 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ModelException e) {
                log.error("CFSFacadeNew.txnDoRevokeTransaction()#4 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoRevokeTransaction()#5 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ServerAuthenticationException e) {
                log.error("CFSFacadeNew.txnDoRevokeTransaction()#8 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoRevokeTransaction()#9 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (SQLException e) {
                log.error("CFSFacadeNew.txnDoRevokeTransaction()#6 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoRevokeTransaction()#7 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } finally {
                dbConnectionPool.returnConnection(connection);
            }
        }
    }

    public static void txnDoOneWayTransaction(Tx tx, long minBalance) throws NotFoundException, ModelException, SQLException, ISOException,ServerAuthenticationException {

        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoOneWayTransaction()#1= -- Error :: " + e);
            throw new ISOException();
        }

        long amount = tx.getAmount();

        if (!tx.getTxSrc().equals(CFSConstants.TXN_SRC_SGB)) {
            Connection connection = null;
            try {
                connection = dbConnectionPool.getConnection();

                if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT) ||tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_DEPOSIT)||
                        tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_MANZUME_DEPOSIT)) {
                    int destAccountNature = Integer.parseInt(tx.getDeviceCode().trim());
                    if ((destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME)){
                        savePayId(connection, destAccountNo, tx.getId1(), tx.getTxSrc(), tx.getSessionId());
                    }
                    tx.setDeviceCode("");
                }
                if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT) || tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_GIFT_DEPOSIT)
                        || tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_DEPOSIT) || tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_ATM)
                        ||tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_MANZUME_DEPOSIT)) {

                    long destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, amount, tx.getTxCode(), 0);
                    tx.setDest_account_balance(destBalance);
                } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW) || tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_WITHDRAW)||
                        tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ATM) || tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_WAGE_SMS)) {

                    long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, 0, new HashMap());
                    tx.setSrc_account_balance(srcBalance);
                } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD)){
                    long destBalance = updateDestAccountTransactional(connection, destAccountNo, amount,tx.getCardSequenceNo());
                    tx.setDest_account_balance(destBalance);
                    saveGiftTxTransactional(connection,tx,"");
                    tx.setCardSequenceNo("");
                } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_GIFTCARD)) {
                    if (getGiftCardTx(connection,tx)) {
                        long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, 0, new HashMap());
                        tx.setSrc_account_balance(srcBalance);
                        setGiftTxIsDoneTransactional(connection, tx);
                        tx.setCardSequenceNo("");
                    } else {
                        // not found document
                        throw new ServerAuthenticationException("");

                    }
                }

                saveTxTransactional(connection, tx);
                connection.commit();

            } catch (NotFoundException e) {
                log.error("CFSFacadeNew.txnDoOneWayTransaction()#2 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoOneWayTransaction()#3 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ModelException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoOneWayTransaction()#5 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (SQLException e) {
                log.error("CFSFacadeNew.txnDoOneWayTransaction()#6 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoOneWayTransaction()#7 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ServerAuthenticationException e) {
                log.error("CFSFacadeNew.txnDoOneWayTransaction()#8 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoOneWayTransaction()#9 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } finally {
                dbConnectionPool.returnConnection(connection);
            }
        }
    }


    public static long updateSrcAccountByTypeTransactional(Connection connection, String accountNo, long amount, String txType, long minBalance, long wage, HashMap specificData) throws NotFoundException, ModelException, SQLException {
        if(txType.equals(TJCommand.CMD_BRANCH_WITHDRAW_ATM))
            return updateAccountTransactional4ATM(connection, accountNo, amount);
        else
            return updateAccountTransactional(connection, accountNo, amount, minBalance, specificData, 0);

    }


    public static long updateAccountTransactional(Connection connection, String accountNo, long amount, long minBalance,
                                                  HashMap specificData, long wage) throws ModelException, SQLException, NotFoundException {
        String sql = "SELECT balance, SUBSIDY_AMOUNT " +
                "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";
        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        try {
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                if ((amount <= 0) && (balance + amount + wage - subsidyAmount < minBalance))
                    throw new ModelException("Insufficient fund");
                long new_balance = balance + amount + wage;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance +
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                return new_balance;
            }
            throw new NotFoundException("Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #4= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }


    public static long updateDestAccountTransactional(Connection connection, String accountNo, long amount, long minBalance, HashMap specificData, long wage) throws ModelException, SQLException, NotFoundException {
        String sql = "SELECT balance , SUBSIDY_AMOUNT from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;

        try {
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");

//                if ((amount <= 0) && (balance + amount + wage < minBalance)) {
                if ((amount <= 0) && (balance + amount + wage - subsidyAmount < minBalance)) {
                    throw new ModelException("Insufficient fund");
                }
                long new_balance = balance + amount + wage;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);

                return new_balance;
            }
            throw new NotFoundException("Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #4= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static long updateDestAccountByTypeTransactional(Connection connection, String accountNo, long amount, String txType, long minBalance) throws NotFoundException, ModelException, SQLException {
        try {
//            return updateAccountTransactional(connection, accountNo, amount, minBalance, null, 0);
            return updateDestAccountTransactional(connection, accountNo, amount, minBalance, null, 0);
        } catch (NotFoundException e) {
        }
        return 0;
    }


    public static void saveTxTransactional(Connection connection, Tx tx) throws SQLException {

        String sql = "Insert into tbCFSTX " +
                "(TX_PK,PARTNO,SESSION_ID,TX_SEQ,TX_CODE,TX_SRC," +
                "AMOUNT,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,DEVICE_CODE," +
                "CARD_NO,CREATION_DATE,CREATION_TIME," +
                "ACQUIRER,SRC_BRANCH_ID,BATCH_PK,TX_DATETIME,TX_ORIG_DATE," +
                "TX_ORIG_TIME,ISREVERSED,ISCUTOVERED,SGB_ACTION_CODE," +
                "SGB_BRANCH_ID,SRC_ACC_BALANCE,IS_REVERSAL_TXN,ORIG_TX_PK,DESC,FEE_AMOUNT,BRANCH_DOCNO," +
                "LAST_TRANS_LIMIT," +
                "EBNK_MSG_SEQ,TOTAL_DEST_ACCOUNT,STAN,DEST_ACC_BALANCE,RRN,id1,terminal_id,id2,hostcode, pay_code, " +
                "TX_SEQUENCE_NUMBER, TERMINAL_TYPE, SETTLE_DATE, EXTRA_INFO" +
                ") values('" + tx.getTxPk() + "'," + tx.getPartNo() + ",'" + tx.getSessionId() + "','" + tx.getMessageId() +
                "','" + tx.getTxCode() + "','" + tx.getTxSrc() + "'," + tx.getAmount() + ",'" + tx.getCurrency() + "','" +
                tx.getSrcAccountNo() + "','" + tx.getDestAccountNo() + "','" + tx.getDeviceCode() + "','" + tx.getCardNo() +
                "','" + tx.getCreationDate() + "','" + tx.getCreationTime() + "','" +
                tx.getAcquirer() + "','" + tx.getSrcBranchId() + "'," + tx.getBatchPk() + ",current_timestamp,'" +
                tx.getTxOrigDate() + "','" + tx.getTxOrigTime() + "'," + tx.getIsReversed() + "," + tx.getIsCutovered() +
                ",'" + tx.getSgbActionCode() + "','" + tx.getSgbBranchId() + "'," + tx.getSrc_account_balance() + ",'" +
                tx.getIsReversalTxn() + "','" + tx.getOrigTxPk() + "','" + tx.getDescription() + "'," + tx.getFeeAmount() +
                ",'" + tx.getBranchDocNo() + "'," + tx.getLastTransLimit() + ",'" + tx.getMessageSeq() + "','" +
                tx.getTotalDestAccNo() + "','" + tx.getTxSequenceNumber() + "'," + tx.getDest_account_balance() + ",'" +
                tx.getRRN() + "','" + tx.getId1() + "','" + tx.getMerchantTerminalId() + "','" + tx.getsPayCode() + "','"
                + tx.getHostCode() + "', '" + tx.getCbPayId() + "','" + tx.getCardType() + "','" + tx.getTerminalType() +
                "','" + tx.getSettlementDate() + "','" + tx.getDescription() + "')";
        if (log.isDebugEnabled()) log.debug("sql :: " + sql);

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.saveTxTransactional() = -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }


    public static void setTxIsReversedTransactional(Connection connection, Tx orig_tx) throws NotFoundException, SQLException {
        String sql = "update tbCFSTX set ISREVERSED = 1 where TX_PK = '" + orig_tx.getTxPk() + "'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.setTxIsReversedTransactional() -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }


    public static String getCustomerTmplSrvTxValue(int cust_tmpl_id, String tx_id, String txValueId) throws SQLException, NotFoundException {
        if (customerTmplSrvTxValueMap.containsKey(tx_id + txValueId))
            return (String) customerTmplSrvTxValueMap.get(tx_id + txValueId);

        String sql = "select TXVAlue from TBCUSTOMERTMPLSRV where txID = '" + tx_id + "' and txValueId = '" + txValueId + "' and TEMPLATEID=" + cust_tmpl_id + " with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String tx_value = resultSet.getString("TXVAlue");
                customerTmplSrvTxValueMap.put(tx_id + txValueId, tx_value);
                return tx_value;
            } else {
                throw new NotFoundException("Can not Find any TBCUSTOMERTMPLSRV with  txID = '" + tx_id + "' and txValueId = '" + txValueId + "'");
            }
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
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static int getAccountRange(String accRange) throws SQLException, NotFoundException {
        if (accountRangeMap.containsKey(accRange))
            return (Integer) accountRangeMap.get(accRange);

        String sql = "select ACCOUNT_RANGE_TYPE from TBACCOUNTRANGE where ACCRANGE = '" + accRange + "'  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                int account_range_type = resultSet.getInt("ACCOUNT_RANGE_TYPE");
                accountRangeMap.put(accRange, account_range_type);
                return account_range_type;
            } else {
                throw new NotFoundException("Can not Find any Account_Range_Type  for accRange = '" + accRange + "'");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static TxType getTxTypeSgbTxCode(String txCode) throws SQLException, NotFoundException {
        if (txTypeSgbTxCodeMap.containsKey(txCode))
            return (TxType) txTypeSgbTxCodeMap.get(txCode);
        String sql = "select SGB_TX_CODE,SPW_CHANNEL_CODE from TBCFSTXTYPE where TX_CODE = '" + txCode + "'  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                TxType txType = new TxType(resultSet.getString("SGB_TX_CODE"), resultSet.getString("SPW_CHANNEL_CODE"));
                txTypeSgbTxCodeMap.put(txCode, txType);
                return txType;
            } else {
                throw new NotFoundException("Can not Find any TBCFSTXTYPE where TX_CODE = '" + txCode + "'");
            }
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
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Batch getBatchNoCache(Long batch_status, String operator) throws Exception, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select * from tbCFSBatch where batch_status = " + batch_status + " " + operator + " SGB_STATUS = " + batch_status + " " + operator + " FARA_STATUS= " + batch_status + " with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Timestamp closeTime = resultSet.getTimestamp("BATCH_CLOSE_DATE");
                java.sql.Date batchCloseDate = null;
                if (closeTime != null)
                    batchCloseDate = new java.sql.Date(closeTime.getTime());
                java.sql.Date batchOpenDate = new java.sql.Date(resultSet.getTimestamp("BATCH_OPEN_DATE").getTime());
                Batch batch;
                batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS"));

                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));

                if (Constants.ACTIVE_BATCH == batch_status.intValue())
                    batchMap.put(batch_status, batch);
                return batch;
            }

            throw new NotFoundException();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Imd getIMD(String issuerId) throws SQLException, NotFoundException {
        if (imdMap.containsKey(issuerId))
            return (Imd) imdMap.get(issuerId);

        String sql = "select * from tbIssuer where ISSUER_ID_NUMBER = '" + issuerId + "' with ur";
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Imd imd = new Imd(issuerId, resultSet.getString("ISSUER_NAME"), resultSet.getString("DEBIT_ACCOUNT_NO"),
                        resultSet.getLong("TRANS_BASE_FEE"), resultSet.getLong("TRANS_MIN_FEE"), resultSet.getLong("TRANS_MAX_FEE"),
                        resultSet.getLong("TRANS_PERCENTAG_FE"), resultSet.getLong("LORO_BASE_FEE"), resultSet.getLong("LORO_MIN_FEE"),
                        resultSet.getLong("LORO_MAX_FEE"), resultSet.getLong("LORO_PERCENTAGE_FE"), resultSet.getString("CREATION_DATE"),
                        resultSet.getString("CREATION_TIME"), resultSet.getString("ACCOUNT_NO"), resultSet.getString("CHARGE_ACCOUNT_NO"));

                imd.setCreditAccountNo(resultSet.getString("CREDIT_ACCOUNT_NO"));
                imd.setDebitAccountNo(resultSet.getString("DEBIT_ACCOUNT_NO"));
                imd.setIssuerBranch(resultSet.getString("ISSUER_BRANCH"));
                imd.setNftWageFee(resultSet.getLong("NFT_WAGE_FEE"));
                imd.setNftWagePercentage(resultSet.getFloat("NFT_WAGE_PERCENT"));
                imd.setNftPgWageFee(resultSet.getLong("PGNFT_WAGE_FEE"));
                imd.setNftPgWagePercentage(resultSet.getFloat("PGNFT_WAGE_PERCENT"));
                imd.setNbalWageFee(resultSet.getLong("NBAL_WAGE_FEE"));
                imd.setNbalWagePercentage(resultSet.getFloat("NBAL_WAGE_PERCENT"));
                imd.setNftPosWageFee(resultSet.getLong("NFTPS_WAGE_FEE"));
                imd.setNftPosWagePercentage(resultSet.getFloat("NFTPS_WAGE_PERCENT"));
                imd.setNftMpWageFee(resultSet.getLong("MPNFT_WAGE_FEE"));
                imd.setNftMpWagePercentage(resultSet.getFloat("MPNFT_WAGE_PERCENT"));
                imd.setNftKioskWageFee(resultSet.getLong("KSNFT_WAGE_FEE"));
                imd.setNftKioskWagePercentage(resultSet.getFloat("KSNFT_WAGE_PERCENT"));
                imd.setScshrPbWageFee(resultSet.getLong("PBSCSHR_WAGE_FEE"));
                imd.setNbalKSWageFee(resultSet.getLong("KSNBAL_WAGE_FEE"));
                imd.setNbalMPWageFee(resultSet.getLong("MPNBAL_WAGE_FEE"));
                imd.setNbalPBWageFee(resultSet.getLong("PBNBAL_WAGE_FEE"));
                imd.setNbalPosWageFee(resultSet.getLong("POSNBAL_WAGE_FEE"));
                imd.setNbalSIWageFee(resultSet.getLong("SINBAL_WAGE_FEE"));
                imd.setNbalPGWageFee(resultSet.getLong("PGNBAL_WAGE_FEE"));

                imdMap.put(issuerId, imd);
                return imd;
            }
            throw new NotFoundException();
        } catch (SQLException e) {
            log.error("e = " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean checkDuplicate(String session_id, String message_id) throws SQLException {
        String hql = "select Session_id,TX_SEQ from tbCFSTx  where Session_Id = '" + session_id
                + "' and TX_SEQ = '" + message_id + "'" +
                " with ur";
        boolean txExists = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            if (log.isDebugEnabled()) log.debug("hql :: " + hql);
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) txExists = true;
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
            dbConnectionPool.returnConnection(connection);
        }
        return txExists;
    }

   private static int getPartNo() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        return part;
    }

    private static int getCfsTxPartNo() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.CFSTX_PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.CFSTX_PARTITION_SIZE;
        return part;
    }


    public static Map getAccountTypeMap() {
        return accountTypeMap;
    }

    public static Map getAccountTypeMinBalanceMap() {
        return accountTypeMinBalanceMap;
    }

    public static Map getCustomerTmplSrvTxValueMap() {
        return customerTmplSrvTxValueMap;
    }

    public static Map getDeviceMap() {
        return deviceMap;
    }

    public static Map getAccountRangeMap() {
        return accountRangeMap;
    }

    public static Map getBranchMap() {
        return branchMap;
    }

    public static Map getGiftAccountsMap() {
        return giftAccountsMap;
    }

    public static Map getGiftAccCustomerIdMap() {
        return giftAccCustomerIdMap;
    }

    public static Map getGiftCardMap() {
        return giftCardMap;
    }

    public static Map getTxTypeSgbTxCodeMap() {
        return txTypeSgbTxCodeMap;
    }

    public static Map getTxTypeSPWChannelCodeMap() {
        return txTypeSPWChannelCodeMap;
    }

    public static Map getImdMap() {
        return imdMap;
    }

    public static boolean findSGBBatchForDate(String sgbFileDate) throws NotFoundException, SQLException {

        boolean txExists = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String hql = "select * from tbSGBBatch where sgb_FileDate = '" + sgbFileDate + "' and batch_Status <> '3' with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) txExists = true;
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);

        }
        return txExists;
    }

    public static SGBBatch getSGBBatch(String batch_status) throws NotFoundException, SQLException {

        SGBBatch batch = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String hql = "select * from TBSGBBatch where batch_status = '" + batch_status + "' with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                batch = new SGBBatch(resultSet.getTimestamp("BATCH_OPEN_DATE"), resultSet.getString("BATCH_STATUS"),
                        resultSet.getTimestamp("BATCH_CLOSE_DATE"), resultSet.getString("APPROVER_ID"), null);
                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
                batch.setSgbFileDate(resultSet.getString("SGB_FILEDATE"));
            }

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);


        }

        if (batch == null)
            throw new NotFoundException("Not Found any SGBBatch for batch_status = " + batch_status);

        return batch;
    }

    public static void insertSGBBatch(SGBBatch activeSGBBatch) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            String sql = "insert into TBSGBBatch (BATCH_PK,BATCH_OPEN_DATE,BATCH_STATUS) values (?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, activeSGBBatch.getBatchPk().longValue());
            statement.setTimestamp(2, new Timestamp(activeSGBBatch.getBatchOpenDate().getTime()));
            statement.setString(3, activeSGBBatch.getBatchStatus());

            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long getSGBMaxBatch_PK() throws SQLException {

        long max_batch_pk = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String hql = "select max(batch_pk) from TBSGBBatch with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next())
                max_batch_pk = resultSet.getLong(1);
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);

        }
        return max_batch_pk;
    }

    public static void updateSGBBatch(SGBBatch activeSGBBatch) throws SQLException {

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            String sql = "update TBSGBBatch " +
                    "set sgb_fileDate = ?, batch_status = ?,batch_close_date = ?" +
                    " where batch_pk = " + activeSGBBatch.getBatchPk() + "";
            statement = connection.prepareStatement(sql);
            statement.setString(1, activeSGBBatch.getSgbFileDate());
            statement.setString(2, activeSGBBatch.getBatchStatus());

            if (activeSGBBatch.getBatchCloseDate() != null) {
                statement.setTimestamp(3, new Timestamp(activeSGBBatch.getBatchCloseDate().getTime()));
            } else {
                statement.setTimestamp(3, null);
            }
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long getSGBLogMaxLogId() throws SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String hql = "select max(log_id) from TB_SGB_Log with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            long maxLogId = 0;
            if (resultSet.next()) {
                maxLogId = resultSet.getLong(1);
            }
            return maxLogId;

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }
//    public static ResultSet getSGBLogsWithBatchStatus0() throws SQLException {
//
//        try {
//            String hql = "select * from TB_SGB_Log where proccess_status = '0' order by sgb_batch_pk desc,log_id with ur";
//            return executeQuery(hql);
//        } catch (SQLException e) {
//            log.error(e);
//            throw e;
//        }
//    }

    public static void updateCustomerBalance(PreparedStatement customerAccountPS, String accountNo,
                                             long formattedBalance) throws SQLException, NotFoundException {
        customerAccountPS.setLong(1, formattedBalance);
        customerAccountPS.setString(2, accountNo);
        customerAccountPS.execute();
        if (customerAccountPS.getUpdateCount() == 0)
            throw new NotFoundException("Could not found account = " + accountNo);

    }

    public static void updateCustomerBalance(PreparedStatement customerAccountPS, String accountNo, long formattedBalance,
                                             long subsidaryAmount) throws SQLException, NotFoundException {
        customerAccountPS.setLong(1, formattedBalance);
        customerAccountPS.setLong(2, subsidaryAmount);
        customerAccountPS.setString(3, accountNo);
        customerAccountPS.execute();
        if (customerAccountPS.getUpdateCount() == 0)
            throw new NotFoundException("Could not found account = " + accountNo);

    }

    public static void updateCfsCardAccountMaxTransLimit
            (PreparedStatement customerAccountPS, String accountNo, String pan, long formattedBalance) throws SQLException, NotFoundException {
        customerAccountPS.setLong(1, formattedBalance);
        customerAccountPS.setString(2, accountNo);
        customerAccountPS.setString(3, pan);
        customerAccountPS.execute();
        if (customerAccountPS.getUpdateCount() == 0)
            throw new NotFoundException("Could not found account = " + accountNo);

    }

    public static long getCustomerBalance(PreparedStatement customerAccountPSSelect, String accountNo)
            throws SQLException, NotFoundException {
        customerAccountPSSelect.setString(1, accountNo);
        ResultSet resultSet = customerAccountPSSelect.executeQuery();

        if (!resultSet.next())
            throw new NotFoundException("Could not found account = " + accountNo);
        return resultSet.getLong(1);

    }

    public static CustomerAccount getCustomerBalanceObj(PreparedStatement customerAccountPSSelect, String accountNo) throws SQLException, NotFoundException {
        CustomerAccount customerAccount = null;

        customerAccountPSSelect.setString(1, accountNo);
        ResultSet resultSet = customerAccountPSSelect.executeQuery();

        if (!resultSet.next() || resultSet.getLong(3) == 9) {
            throw new NotFoundException("Could not found account = " + accountNo);
        } else {
            customerAccount = new CustomerAccount();
            customerAccount.setBalance(resultSet.getLong(1));
            customerAccount.setSubsidy_amount(resultSet.getLong(2));
        }
        return customerAccount;

    }

    public static long getCfsCardAccountWithoutStatus(PreparedStatement cfsCardAccountPSSelect, String accountNo,
                                                      String pan) throws SQLException, NotFoundException {
        cfsCardAccountPSSelect.setString(1, accountNo);
        cfsCardAccountPSSelect.setString(2, pan);
        ResultSet resultSet = cfsCardAccountPSSelect.executeQuery();

        if (!resultSet.next())
            throw new NotFoundException("Could not found account = " + accountNo + ", pan = " + pan + " in tbCFSCardAccount.");
        return resultSet.getLong(1);

    }

    public static void updateSGBLog(PreparedStatement sgb_log_ps, long logId) throws SQLException {
        sgb_log_ps.setLong(1, logId);
        sgb_log_ps.execute();
    }

    public static void saveSGB2Tx
            (PreparedStatement cfstx_ps, String tx_pk, String tx_seq, String tx_code, long amount, String src_account, String dest_account,
             String creation_Date, String creation_time, String src_branch_id, Long batchPk, Timestamp tx_dateTime, String tx_orig_date,
             String tx_orig_time, String cardNo, String sgbActionCode, String sgbBranchId, String branchDocNo, long lastTransLimit, long src_Account_balance,
             long dest_Account_balance,String session_id) throws SQLException {

        cfstx_ps.setString(1, tx_pk);
        cfstx_ps.setInt(2, getCfsTxPartNo());
        cfstx_ps.setString(3, tx_seq);
        cfstx_ps.setString(4, tx_code);
        cfstx_ps.setLong(5, amount);
        cfstx_ps.setString(6, src_account);
        cfstx_ps.setString(7, dest_account);
        cfstx_ps.setString(8, creation_Date);
        cfstx_ps.setString(9, creation_time);
        cfstx_ps.setString(10, src_branch_id);
        cfstx_ps.setLong(11, batchPk.longValue());
        cfstx_ps.setTimestamp(12, tx_dateTime);
        cfstx_ps.setString(13, tx_orig_date);
        cfstx_ps.setString(14, tx_orig_time);
        cfstx_ps.setLong(15, src_Account_balance);
        cfstx_ps.setString(16, cardNo);
        cfstx_ps.setString(17, sgbActionCode);
        cfstx_ps.setString(18, sgbBranchId);
        cfstx_ps.setString(19, branchDocNo);
        cfstx_ps.setLong(20, lastTransLimit);
        cfstx_ps.setLong(21, dest_Account_balance);
        cfstx_ps.setString(22, session_id);
        try {
            cfstx_ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static SGBBatch getLastSGBBatchWithStatus2() throws NotFoundException, SQLException {

        SGBBatch batch = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String hql = "select * from TBSGBBatch where batch_status <> '1' order by batch_pk desc with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                batch = new SGBBatch(resultSet.getTimestamp("BATCH_OPEN_DATE"), resultSet.getString("BATCH_STATUS"),
                        resultSet.getTimestamp("BATCH_CLOSE_DATE"), resultSet.getString("APPROVER_ID"), null);
                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
                batch.setSgbFileDate(resultSet.getString("SGB_FILEDATE"));
            }

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

        if (batch == null)
            throw new NotFoundException("Not Found any SGBBatch for batch_status = 2");

        return batch;
    }

public static long getSessionId(PreparedStatement sequenceSelect) throws SQLException, NotFoundException {
        ResultSet resultSet = sequenceSelect.executeQuery();

        if (!resultSet.next())
            throw new NotFoundException("Could not found sequence for ApplierSessionId in tbCFSCardAccount.");
        return resultSet.getLong(1);

    }

    public static void updateSessionId(PreparedStatement sequenceUpdate, long sequence) throws SQLException {
        sequenceUpdate.setLong(1, sequence);
        sequenceUpdate.execute();
    }

    public static Tx getOriginalBranchTx(String date, String msgSeq, String time,String txSrc) throws SQLException, NotFoundException {

        String hql = "select TX_PK, SESSION_ID, TX_SEQ, TX_CODE, TX_SRC, CURRENCY, SRC_ACCOUNT_NO, " +
                "DEST_ACCOUNT_NO, TOTAL_DEST_ACCOUNT, CARD_SEQUENCE_NO, STAN, CREATION_DATE, CREATION_TIME, " +
                "ACQUIRER, SRC_BRANCH_ID, TX_ORIG_TIME, SGB_ACTION_CODE,SGB_BRANCH_ID, CARD_NO, ORIG_TX_PK, TX_DATETIME, AMOUNT, " +
                "BATCH_PK, SRC_ACC_BALANCE, PARTNO, ISREVERSED, ISCUTOVERED, FEE_AMOUNT, desc, RRN, HOSTCODE,ID1,TX_ORIG_DATE" +
                " from tbCFSTx where " +
                "rrn = '" + msgSeq + "' and " +
                "TX_ORIG_DATE = '" + date + "' and " +
                "TX_ORIG_TIME = '" + time + "' and " +
                "TX_SRC = '"+txSrc+"' and IS_REVERSAL_TXN = '0' with ur";

        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Tx tx = new Tx();
                tx.setTxPk(resultSet.getString("TX_PK"));
                tx.setSessionId(resultSet.getString("SESSION_ID"));
                tx.setMessageId(resultSet.getString("TX_SEQ"));
                tx.setTxCode(resultSet.getString("TX_CODE"));
                tx.setTxSrc(resultSet.getString("TX_SRC"));
                tx.setCurrency(resultSet.getString("CURRENCY"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setCardSequenceNo(resultSet.getString("CARD_SEQUENCE_NO"));
                tx.setTxSequenceNumber(resultSet.getString("STAN"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setAcquirer(resultSet.getString("ACQUIRER"));
                tx.setSrcBranchId(resultSet.getString("SRC_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setCardNo(resultSet.getString("CARD_NO"));
                tx.setIsReversalTxn('0');
                tx.setOrigTxPk(resultSet.getString("ORIG_TX_PK"));
                tx.setTxDateTime(resultSet.getTimestamp("TX_DATETIME"));
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setBatchPk(resultSet.getLong("BATCH_PK"));
                tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                tx.setPartNo(new Integer(resultSet.getInt("PARTNO")));
                tx.setIsReversed(resultSet.getInt("ISREVERSED"));
                tx.setIsCutovered(resultSet.getInt("ISCUTOVERED"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setDescription(resultSet.getString("desc"));
                tx.setRRN(resultSet.getString("RRN"));
                tx.setHostCode(resultSet.getString("HOSTCODE"));
                tx.setId1(resultSet.getString("ID1"));


                return (tx);
            } else {
                throw new NotFoundException("Transaction branch Message Sequence = " + msgSeq + " does not exist");
            }
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
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static Tx getOriginalBranchTx(String destAccountNo, String date, String amount, String branchNo, String branchDocNo, String time) throws SQLException, NotFoundException {

        String hql = "select * from tbCFSTx where DEST_ACCOUNT_NO = '" + destAccountNo + "' and AMOUNT = " + amount +
                " and SGB_BRANCH_ID = '" + branchNo + "' and BRANCH_DOCNO = '" + branchDocNo + "' and IS_REVERSAL_TXN = '0' and TX_ORIG_DATE = '" + date + "' and TX_ORIG_TIME = '" + time + "' order by tx_seq  with ur";

        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Tx tx = new Tx();
                tx.setTxPk(resultSet.getString("TX_PK"));
                tx.setSessionId(resultSet.getString("SESSION_ID"));
                tx.setMessageId(resultSet.getString("TX_SEQ"));
                tx.setTxCode(resultSet.getString("TX_CODE"));
                tx.setTxSrc(resultSet.getString("TX_SRC"));
                tx.setCurrency(resultSet.getString("CURRENCY"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setCardSequenceNo(resultSet.getString("CARD_SEQUENCE_NO"));
                tx.setTxSequenceNumber(resultSet.getString("STAN"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setAcquirer(resultSet.getString("ACQUIRER"));
                tx.setSrcBranchId(resultSet.getString("SRC_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setCardNo(resultSet.getString("CARD_NO"));
                tx.setIsReversalTxn('0');
                tx.setOrigTxPk(resultSet.getString("ORIG_TX_PK"));
                tx.setTxDateTime(resultSet.getTimestamp("TX_DATETIME"));
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setBatchPk(resultSet.getLong("BATCH_PK"));
                tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                tx.setPartNo(new Integer(resultSet.getInt("PARTNO")));
                tx.setIsReversed(resultSet.getInt("ISREVERSED"));
                tx.setIsCutovered(resultSet.getInt("ISCUTOVERED"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setDescription(resultSet.getString("desc"));
                tx.setRRN(resultSet.getString("RRN"));
                tx.setHostCode(resultSet.getString("HOSTCODE"));
                tx.setId1(resultSet.getString("ID1"));


                return (tx);
            } else {
                throw new NotFoundException("Transaction branchDocNo = " + branchDocNo + " does not exist");
            }
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
            dbConnectionPool.returnConnection(connection);
        }

    }


    public static void txnDoOneWayReverse(Tx orig_tx, Tx rev_tx) throws NotFoundException, SQLException, ModelException,ServerAuthenticationException {
        log.info("rev_tx.getTxCode() = " + rev_tx.getTxCode());

        String srcAccountNo = orig_tx.getSrcAccountNo();
        String destAccountNo = orig_tx.getDestAccountNo();

        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoOneWayReverse() #4=   -- Error :: " + e);
        }

        long amount = orig_tx.getAmount();

        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            if (rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_REVERSAL)
                    ||rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_DEPOSIT_REVERSAL)) {
                int destAccountNature = Integer.parseInt(rev_tx.getDeviceCode().trim());
                if ((destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME)){
                    reversePay(connection, destAccountNo, rev_tx.getId1());
                }
                rev_tx.setDeviceCode("");
            }
            if (rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_REVERSAL) || rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_DEPOSIT_REVERSAL)) {
                long destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, -amount, rev_tx.getTxCode(), 0);
                rev_tx.setDest_account_balance(destBalance);
            } else if (rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_REVERSAL)  || rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_WITHDRAW_REVERSAL)
                   ||rev_tx.getTxCode().equals(TJCommand.CMD_BRANCH_ATM_WITHDRAW_REVERSAL) || rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_WAGE_REVERSAL)
                    ||rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL) || rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE_REVERSAL) ) {
                long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, amount, rev_tx.getTxCode(), 0, 0, new HashMap());
                rev_tx.setSrc_account_balance(srcBalance);
            }else if (rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD_REVERSAL)) {

                if(rev_tx.getCardSequenceNo().equalsIgnoreCase("1")){
                    //batch giftCard
                    if(checkRequestNoTransactional(connection,rev_tx)){
                        throw  new ModelException("");
                    }
                }
                ///////////////////////////////
                long destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, -amount, rev_tx.getTxCode(), 0);
                rev_tx.setDest_account_balance(destBalance);
                setGiftTxIsDoneTransactional(connection,rev_tx);
                rev_tx.setCardSequenceNo("");
            }
            else if(rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_ATM_DEPOSIT_REVERSAL))
            {
                long destBalance=updateDestAccountTransactional4ATM(connection, destAccountNo, -amount);
                rev_tx.setDest_account_balance(destBalance);
            }

            orig_tx.setIsReversed(CFSConstants.REVERSED);
            setTxIsReversedTransactional(connection, orig_tx);

            if (rev_tx != null) saveTxTransactional(connection, rev_tx);

            if(rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_WAGE_REVERSAL)
                    ||rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL)
                    || rev_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE_REVERSAL)){
                long origAmount=setFeeIsReversedTransactional(connection,orig_tx.getTxPk(),orig_tx.getSessionId());
                if(origAmount!=-1){
                    String transactionType=((orig_tx.getTxCode().trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE)) ? Constants.RTGS : Constants.ACH);
                    insertFeeLog(connection,rev_tx,transactionType,origAmount,Constants.TX_IS_REVERSAL,Constants.TX_IS_REVERSED);
                }
            }

            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoOneWayReverse() #1=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoOneWayReverse() #2=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (ModelException e) {
            log.error("CFSFacadeNew.txnDoOneWayReverse() #3=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (ServerAuthenticationException e) {
            log.error("CFSFacadeNew.txnDoOneWayReverse() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }


    }


    public static long updateCardAccountTransactionalForRevoke(Connection connection, String accountNo, String cardNo) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select Max_Trans_Limit from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) maxTransLimit = resultSet.getLong("Max_Trans_Limit");
        } catch (SQLException e) {
            log.error("sql = " + sql);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        if (maxTransLimit <= 0)
            throw new ModelException("Not Sufficient Funds for PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'");


        sql = "update tbCFSCardAccount set max_trans_limit =0 , status=0   where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'";
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
            statement1.execute(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
        }

        return maxTransLimit;

    }

    public static long updateCardAccountTransactional(Connection connection, String accountNo, String cardNo, long amount, long accBalance) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select Max_Trans_Limit from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) maxTransLimit = resultSet.getLong("Max_Trans_Limit");
        } catch (SQLException e) {
            log.error("sql = " + sql);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

//        if (Math.min(accBalance, maxTransLimit) + amount < 0)
        if (maxTransLimit + amount < 0)
            throw new ModelException("Not Sufficient Funds for PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'");


        sql = "update tbCFSCardAccount set max_trans_limit = max_trans_limit + " + amount + " where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'";
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
            statement1.execute(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
        }

        return maxTransLimit;

    }

    public static int updateCardAccountTransactional(Connection connection, String accountNo, long amount) throws SQLException, ModelException {

        String sql = "update tbCFSCardAccount set max_trans_limit = max_trans_limit + " + amount + " where  ACCOUNT_NO = '" + accountNo + "' and series>1 and status=1 and max_trans_limit + " + amount + ">=0";
        int updateCount = 0;
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
            updateCount = statement1.executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
        }

        return updateCount;

    }

    public static Collection getCardAccounts(String cardNo) throws SQLException {
        String hql = "select * from tbCFSCardAccount where PAN = '" + cardNo + "' with ur";


        Collection collection = new HashSet();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                CardAccount cardAccount = new CardAccount(cardNo, resultSet.getString("SEQUENCE_NO"), resultSet.getString("ACCOUNT_TYPE"),
                        resultSet.getString("ACCOUNT_NO"), resultSet.getString("CREATION_DATE"), resultSet.getString("CREATION_TIME")
                        , resultSet.getLong("MAX_TRANS_LIMIT"), resultSet.getInt("STATUS"));

                collection.add(cardAccount);
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
            dbConnectionPool.returnConnection(connection);

        }
        return collection;
    }


    public static List getSPWTxList(String accountNo, int transCount) throws SQLException, NotFoundException {
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, AMOUNT," +
                "FEE_AMOUNT, SRC_ACC_BALANCE,IS_REVERSAL_TXN,DEST_ACC_BALANCE,CREATION_DATE,CREATION_TIME  from tbCFSTx as tx ";
        hql = hql + " where (tx.src_Account_No = '" + accountNo + "' or tx.dest_Account_No = '" + accountNo + "' ) " +
                "order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));

                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);
                // add the tx to the list
                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void executeUpdate(String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error = " + e + " -- sql = " + sql);
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    private static ResultSet executeQuery(String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet;
        log.debug("--------> Excecute : " + sql);
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            CachedRowSet crs = new CachedRowSet();
            crs.populate(resultSet);
            resultSet.close();
            connection.commit();
            return crs;
        } catch (SQLException e) {
            log.debug("Error SQL : " + sql);
            log.error(e);
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void putSGBLogWithPS
            (String
                     accountGroup, String
                     bankCode, String
                     accountNo, String
                     debitCredit, String
                     amount, String
                     effectiveDate, String
                     operationCode, String
                     documentNo, String
                     branchCode, String
                     journalNo, String
                     accountBranchCode, String
                     time, String
                     terminalAccount, String
                     cardNo, String
                     creationDate, String
                     creationTime, long logId, String
                     sgbFileDate, String
                     processStatus, long sgb_Batch_pk, PreparedStatement
                     sgb_log_ps) throws SQLException {
        sgb_log_ps.setString(1, accountGroup);
        sgb_log_ps.setInt(2, getPartNo());
        sgb_log_ps.setString(3, bankCode);
        sgb_log_ps.setString(4, accountNo);
        sgb_log_ps.setString(5, debitCredit);
        sgb_log_ps.setString(6, amount);
        sgb_log_ps.setString(7, effectiveDate);
        sgb_log_ps.setString(8, operationCode);
        sgb_log_ps.setString(9, documentNo);
        sgb_log_ps.setString(10, branchCode);
        sgb_log_ps.setString(11, journalNo);
        sgb_log_ps.setString(12, accountBranchCode);
        sgb_log_ps.setString(13, time);
        sgb_log_ps.setString(14, terminalAccount);
        sgb_log_ps.setString(15, creationDate);
        sgb_log_ps.setString(16, creationTime);
        sgb_log_ps.setLong(17, logId);
        sgb_log_ps.setString(18, sgbFileDate);
        sgb_log_ps.setString(19, processStatus);
        sgb_log_ps.setLong(20, sgb_Batch_pk);
        sgb_log_ps.setString(21, cardNo);
        try {
            sgb_log_ps.execute();
        } catch (SQLException e) {
            if (sgb_log_ps != null)
                sgb_log_ps.close();
            throw e;
        }
    }


    public static void updateTbcustomeraccounts(String accountNo, String accountStatus, String blckUser) throws SQLException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        String sql = "update TBCUSTOMERACCOUNTS set status = " + accountStatus + "," +
                " block_user='" + blckUser + "' ," +
                " block_date= timestamp_format('" + tString.substring(0, 19) + "', 'YYYY-MM-DD HH24:MI:SS' )" +
                " Where ACCOUNT_NO  = '" + accountNo.trim() + "'";
        executeUpdate(sql);
    }


    //  *******************GROUP CARD*******************************************************

    public static List getCardTxList(String cardNo, int transCount, String fromDate,String toDate) throws SQLException, NotFoundException {
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO, CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME,CREATION_DATE,CREATION_TIME,  " +
                "IS_REVERSAL_TXN,AMOUNT,FEE_AMOUNT, SRC_ACC_BALANCE, DEST_ACC_BALANCE,SGB_ACTION_CODE,ACQUIRER,BRANCH_DOCNO,TOTAL_DEST_ACCOUNT from tbCFSTx as tx ";
        hql = hql + "where (tx.CARD_No = '" + cardNo + "' or tx.TOTAL_DEST_ACCOUNT = '" + cardNo + "') ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+fromDate + "' and '"+ toDate + "'";

        hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";

        List txs = new ArrayList();

        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setAcquirer(resultSet.getString("ACQUIRER"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setCardNo(resultSet.getString("CARD_NO"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO"));

                //source account
                if (resultSet.getString("CARD_No").trim().equals(cardNo)) {
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("TOTAL_DEST_ACCOUNT").trim().equals(cardNo)) {
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                // add the tx to the list
                txs.add(tx);
            }
            connection.commit();

            return txs;

        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List getAccountTxList(String accountNo, int transCount, String fromDate,String toDate) throws SQLException, NotFoundException {
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO, CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME,CREATION_DATE,CREATION_TIME,  " +
                "IS_REVERSAL_TXN,AMOUNT,FEE_AMOUNT, SRC_ACC_BALANCE, DEST_ACC_BALANCE,SGB_ACTION_CODE,SGB_BRANCH_ID,BRANCH_DOCNO,TOTAL_DEST_ACCOUNT from tbCFSTx as tx ";
        hql = hql + "where (tx.src_account_no = '" + accountNo + "' or dest_account_no = '" + accountNo + "') ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+ fromDate + "' and '"+ toDate + "'";

        hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";

        List txs = new ArrayList();

        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO"));
                tx.setCardNo(" ");

                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    String cardNo=resultSet.getString("CARD_NO");
                    if(cardNo!=null && !cardNo.trim().equalsIgnoreCase("") &&
                            cardNo.trim().length()==16 && cardNo.trim().startsWith(Constants.BANKE_TEJARAT_BIN_NEW)){
                    tx.setCardNo(cardNo);
                    }
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    String cardNo=resultSet.getString("TOTAL_DEST_ACCOUNT");
                    if(cardNo!=null && !cardNo.trim().equalsIgnoreCase("") &&
                            cardNo.trim().length()==16 && cardNo.trim().startsWith(Constants.BANKE_TEJARAT_BIN_NEW)){
                        tx.setCardNo(cardNo);
                    }
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                // add the tx to the list
                txs.add(tx);
            }
            connection.commit();

            return txs;

        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void insertBLCKLog(Connection connection, String sessionId, String tx_code, String
            accountNo, String
                                             cardNo, String
                                             amount, String
                                             service, short
                                             blockType, String
                                             stan, String rrn, String wage) throws SQLException {
        String sql = "insert into TB_BLCK_Log" +
                "(PARTNO,TX_DATETIME,SESSION_ID,TX_CODE,ACCOUNT_NO,CARD_NO,AMOUNT,SERVICE,BLOCK_TYPE,STAN,RRN,FEE_AMOUNT,CREATION_DATE,CREATION_TIME) values(" +
                getCfsTxPartNo() + ",current_timestamp,'" + sessionId + "','" + tx_code + "','" + accountNo + "','" + cardNo + "','" +
                amount + "','" + service + "'," + blockType + ",'" + stan + "','" + rrn + "','" + wage + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "')";

        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertBLCKLog= -- Error :: " + e);
            if (statement != null)
                statement.close();
            throw e;
        }
    }

    public static void insertBLCKLog(Connection connection, String sessionId, String tx_code, String accountNo,
                                     String cardNo, String amount, String service, short blockType, String stan,
                                     String rrn, String wage, String deviceCode, String origDate, String origTime,
                                     char isReversalTxn, int isReversed, String txPk, String origTxPk) throws SQLException {
        String sql = "insert into TB_BLCK_Log" +
                "(PARTNO,TX_DATETIME,SESSION_ID,TX_CODE,ACCOUNT_NO,CARD_NO,AMOUNT,SERVICE,BLOCK_TYPE,STAN,RRN,FEE_AMOUNT," +
                "DEVICE_CODE,TX_ORIG_DATE,TX_ORIG_TIME,IS_REVERSAL_TXN,ISREVERSED,TX_PK,ORIG_TX_PK,CREATION_DATE,CREATION_TIME) values(" +
                getCfsTxPartNo() + ",current_timestamp,'" + sessionId + "','" + tx_code + "','" + accountNo + "','" + cardNo + "','" +
                amount + "','" + service + "'," + blockType + ",'" + stan + "','" + rrn + "','" + wage + "','" + deviceCode + "','" +
                origDate + "','" + origTime + "'," + "'" + isReversalTxn + "'," + isReversed + ",'" + txPk + "','" + origTxPk + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "')";

        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertBLCKLog= -- Error :: " + e);
            if (statement != null)
                statement.close();
            throw e;
        }
    }

    public static boolean isGroupCard(String accountGroup) throws Exception {
        try {
            ArrayList groupCardList = getGroupCardType();
            for (int i = 0; i < groupCardList.size(); i++) {
                if (groupCardList.get(i).equals(accountGroup))
                    return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    private static ArrayList getGroupCardType() {
        ArrayList groupCardList = new ArrayList();
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE1);
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE2);
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE3);
        return groupCardList;
    }
//  *******************GROUP CARD*******************************************************

    public static Branch getBranch(String branchCode) throws SQLException, NotFoundException {
        if (branchMap.containsKey(branchCode))
            return (Branch) branchMap.get(branchCode);

        String sql = "select ACCOUNT_NO,ISMODIFIED,FEE_ACCOUNT_NO,RTGS_ACCOUNT_NO,ACH_ACCOUNT_NO from TBBranch where branch_CODE = '" + branchCode + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Branch branch = new Branch(branchCode, resultSet.getString("ACCOUNT_NO"));
                branch.setFeeAccountNo(resultSet.getString("FEE_ACCOUNT_NO"));
                branch.setRtgsAccountNo(resultSet.getString("RTGS_ACCOUNT_NO"));
                branch.setAchAccountNo(resultSet.getString("ACH_ACCOUNT_NO"));

                if (resultSet.getString("ISMODIFIED").equals("1"))
                    throw new NotFoundException("Account_no for BranchCode: " + branchCode + " is disabled");

                branchMap.put(branchCode, branch);
                return branch;
            } else {
                throw new NotFoundException("Can not Find any TBBranch with branch_CODE = " + branchCode);
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getBranch() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    //****************Branch Manager******************
    public static boolean doBlockAmountTransactional(String accountNo, long minBalance, long blockAmount, String blockNo, String brokerId, String providerId,
                                                  String user, String branchId, String desc, String createDate, String createTime,String accountStatus,
                                                  String blockDate,String blockTime,Tx tx,String documentStatus) throws NotFoundException, ModelException, SQLException, ServerAuthenticationException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        Connection connection = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Statement insStm = null;
        boolean havingDocument=false;
        connection = dbConnectionPool.getConnection();
        try {
            String sql = "SELECT LOCK_STATUS,balance, SUBSIDY_AMOUNT,SGB_BRANCH_ID,ACCOUNT_TYPE " +
                    "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update";
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long newBalance=balance;
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                long newSubsidyAmount = subsidyAmount + blockAmount;
                int lockStatus = accRS.getInt("LOCK_STATUS");
                if (lockStatus != 9 && !accountStatus.equalsIgnoreCase(Constants.JURIDICAL_BLOCK_REQUEST)) {
                    if (balance - newSubsidyAmount < minBalance)
                        throw new ModelException("Insufficient fund");
                }
                String sgbBranchId = accRS.getString("SGB_BRANCH_ID");
                if (user.equals(Constants.CHN_USER_BRANCH) && !sgbBranchId.equalsIgnoreCase(branchId))
                    throw new ServerAuthenticationException("SGB_Branch_ID is not equal Block_Branch_ID");

                if (user.equals(Constants.CHN_USER_SIMIN) && documentStatus.equalsIgnoreCase(Constants.TRUE)){
                    String accountType=accRS.getString("ACCOUNT_TYPE");

                    minBalance=getMinBalance(accountType);
                    if (balance - subsidyAmount -tx.getAmount() >= minBalance ){
                        newBalance=balance-tx.getAmount();
                        tx.setSrc_account_balance(newBalance);
                        saveTxTransactional(connection,tx);
                        havingDocument=true;
                    }
                }

                String updateSQL = "update TBCUSTOMERACCOUNTS set SUBSIDY_AMOUNT= " + newSubsidyAmount+",balance= "+newBalance+
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                String insertSql = "insert into " + Constants.TB_BLOCK_AMOUNT +
                        "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,BLOCKAMOUNT,CREATEDATE,CREATETIME,UPDATEDATETIME," +
                        "CHN_USER,BRANCH,DESC,TX_ORIG_DATE,TX_ORIG_TIME)"+
                        " values('" + accountNo + "','" + blockNo + "','" +
                        brokerId + "','" + providerId + "'," + blockAmount + ",'" + createDate + "','" +
                        createTime + "', timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + user + "', '" + branchId +
                        "', '" + desc + "', '" + blockDate +"', '" + blockTime+"')";
                insStm = connection.createStatement();
                insStm.executeUpdate(insertSql);
                connection.commit();
            } else {
                throw new NotFoundException("Not found Account = " + accountNo);
            }
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ServerAuthenticationException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional()#6 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doBlockAmountTransactional()#7 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional()#8 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
                if (statement != null) statement.close();
                if (updateST != null) updateST.close();
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional() #12= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return havingDocument;
    }

    public static boolean doUnBlockAmountTransactional(String accNo, String blckNo, String brokerId, String providerId,
                                                    long unBlckAmnt, String brchId, String user, String reason, String desc,
                                                    String createDate, String createTime,String blockDate,
                                                    String blockTime, String unblockDesc, Tx tx,String documentStatus) throws SQLException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        long minBalance=0;
        long newSubsidyAmount=0;
        long newBalance=0;
        String SQL = "delete from " + Constants.TB_BLOCK_AMOUNT + " where ACCOUNT_NO = '" + accNo.trim() + "'and BLOCK_NO = '" +
                blckNo.trim() + "' and BROKER_ID = '" + brokerId.trim() + "' and " + " PROVIDER_ID = '" + providerId.trim() + "'";
        String insertSql = "insert into " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS +
                "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,UNBLOCKAMOUNT,BRANCHID,CHN_USER,UNBLOCK_REASON,DESC,CREATEDATE," +
                "CREATETIME,UPDATEDATETIME,TX_ORIG_DATE,TX_ORIG_TIME,BLOCK_REASON)"+
                " values ('" + accNo + "','" +blckNo + "','" + brokerId + "','" + providerId + "'," + unBlckAmnt + ",'" +
                brchId + "','" + user + "','" + reason + "','" + unblockDesc + "','" + createDate + "','" + createTime +
                "',timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + blockDate +
                "','" + blockTime + "','" + desc +"')";
        Connection connection = null;
        Statement statement = null;
        Statement stm = null;
        Statement insStm = null;
        Statement updStm = null;
        ResultSet accRS=null;
        boolean havingDocument=false;
        try {
            connection = dbConnectionPool.getConnection();
            stm = connection.createStatement();
            stm.executeUpdate(SQL);
            insStm = connection.createStatement();
            insStm.executeUpdate(insertSql);

            String sql = "SELECT balance, SUBSIDY_AMOUNT,ACCOUNT_TYPE from tbCustomerAccounts " +
                    "where ACCOUNT_NO = '" + accNo.trim() + "' and LOCK_STATUS = 1 for update";
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                newBalance = balance;
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                String accountType = accRS.getString("ACCOUNT_TYPE");

                newSubsidyAmount = subsidyAmount - unBlckAmnt;

                if (user.equals(Constants.CHN_USER_SIMIN) && documentStatus.equalsIgnoreCase(Constants.TRUE)) {
                    minBalance = getMinBalance(accountType);

                    if (balance - newSubsidyAmount - tx.getAmount() >= minBalance) {
                        newBalance = balance - tx.getAmount();
                        tx.setSrc_account_balance(newBalance);
                        saveTxTransactional(connection, tx);
                        havingDocument=true;
                    }
                }

                String subcidUpdSql = "update " + Constants.TB_CUSTOMER_ACCOUNTS + " set SUBSIDY_AMOUNT =" + newSubsidyAmount +
                        ",balance=" + newBalance + " where ACCOUNT_NO = '" + accNo.trim() + "'";
                updStm = connection.createStatement();
                updStm.executeUpdate(subcidUpdSql);
                connection.commit();
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.doUnBlockAmountTransactional() #1 -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doUnBlockAmountTransactional()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (stm != null) stm.close();
            if (insStm != null) insStm.close();
            if (updStm != null) updStm.close();
            if (statement != null) statement.close();
            if (accRS != null) accRS.close();
            dbConnectionPool.returnConnection(connection);
        }
        return havingDocument;
    }

    public static String[] checkBlockStAcc(String blockNo, String brokerId, String providerId, String accountNo, String branchId, String serviceType) throws NotFoundException, ServerAuthenticationException, SQLException {
        String selectSql = "select BLOCKAMOUNT,BRANCH,DESC from " + Constants.TB_BLOCK_AMOUNT + " where ACCOUNT_NO='" + accountNo + "' and BLOCK_NO = '" + blockNo + "' " +
                " and BROKER_ID = '" + brokerId + "' and PROVIDER_ID = '" + providerId + "'  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String[] describeAmount = new String[2];
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(selectSql);
            if (rs.next()) {
                describeAmount[0] = rs.getString("BLOCKAMOUNT");
                describeAmount[1] = rs.getString("DESC").trim();
                String branch = rs.getString("BRANCH").trim();
                if (serviceType.equals(Constants.BRANCH_SERVICE) && !branch.equalsIgnoreCase(branchId)) {
                    connection.rollback();
                    throw new ServerAuthenticationException("SGB_Branch_ID is not equal Block_Branch_ID");
                }
                connection.commit();
                return describeAmount;
            } else {
                connection.rollback();
                throw new NotFoundException(ActionCode.NO_REALTED_BLCK_EXIST);
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkBlockStAcc() = -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean findBlockNo4BM(String blockNo, String brokerId, String providerId, String account_no) throws SQLException {
        String blckSelectSQL = " select BLOCK_NO from " + Constants.TB_BLOCK_AMOUNT +
                " where ACCOUNT_NO = '" + account_no + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "' with ur";
        String unblckSelectSQL = " select BLOCK_NO from " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS +
                " where  ACCOUNT_NO = '" + account_no + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "' with ur";
        boolean isDuplicateBlockNo = false;
        Connection conn = null;
        Statement stm1 = null;
        Statement stm2 = null;
        ResultSet rs = null;
        try {
            conn = dbConnectionPool.getConnection();
            stm1 = conn.createStatement();
            stm2 = conn.createStatement();
            rs = stm1.executeQuery(blckSelectSQL);
            if (rs.next()) {
                conn.commit();
                return true;
            } else {
                rs = stm1.executeQuery(unblckSelectSQL);
                if (rs.next()) {
                    conn.commit();
                    return true;
                }
            }
            conn.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.findBlockNo4BM() =   -- Error :: " + e);
            conn.rollback();
            throw e;
        } finally {
            if (stm1 != null) stm1.close();
            if (stm2 != null) stm2.close();
            dbConnectionPool.returnConnection(conn);
        }
        return isDuplicateBlockNo;
    }

    public static String getSgbBranchId(String acc, int status) throws NotFoundException, SQLException, ServerAuthenticationException {
        String hql = "select SGB_BRANCH_ID,STATUS from tbCustomerAccounts where ACCOUNT_NO = '" + acc + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            if (resultSet.next()) {
                if (resultSet.getInt("STATUS") == status) {
                    connection.rollback();
                    throw new ServerAuthenticationException("status is equal to Request_status");
                }
                connection.commit();
                return resultSet.getString("SGB_BRANCH_ID");
            } else {
                connection.rollback();
                throw new NotFoundException("Account " + acc + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getSgbBranchId() =   -- Error :: " + e + " -- hql = " + hql);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void resetAccount(String brokerId, String providerId, String account_no, String branchCode) throws SQLException, ServerAuthenticationException, NotFoundException, ModelException {
        String accountSelectSql = " select LOCK_STATUS,SUBSIDY_AMOUNT,SGB_BRANCH_ID from tbcustomerAccounts" +
                " where ACCOUNT_NO = '" + account_no + "' for update";
        String blckSelectSQL = " select sum(BLOCKAMOUNT) from " + Constants.TB_BLOCK_AMOUNT +
                " where ACCOUNT_NO = '" + account_no + "' and BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "' with ur";
        String blckDeleteSQL = " delete from " + Constants.TB_BLOCK_AMOUNT +
                " where ACCOUNT_NO = '" + account_no + "' and BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "'";
        String unblckDeleteSQL = " delete from " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS +
                " where  ACCOUNT_NO = '" + account_no + "' and BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "'";
        boolean isDuplicateBlockNo = false;
        Connection conn = null;
        Statement stm1 = null;
        Statement stm2 = null;
        Statement stm3 = null;
        Statement stm4 = null;
        Statement stm5 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String branchId;
        int lockStatus;
        long sumBlock = 0;
        try {
            conn = dbConnectionPool.getConnection();
            stm1 = conn.createStatement();
            stm2 = conn.createStatement();
            stm3 = conn.createStatement();
            stm4 = conn.createStatement();
            stm5 = conn.createStatement();
            rs1 = stm1.executeQuery(accountSelectSql);
            if (rs1.next()) {
                branchId = rs1.getString("SGB_BRANCH_ID");
                lockStatus = rs1.getInt("LOCK_STATUS");
                if (!branchId.equalsIgnoreCase(branchCode)) {
                    throw new ServerAuthenticationException("");
                }
                if (lockStatus != 9) {
                    throw new ModelException();
                }
                rs2 = stm2.executeQuery(blckSelectSQL);
                if (rs2.next()) {
                    sumBlock = rs2.getLong(1);
                    if (sumBlock > 0) {
                        stm3.executeUpdate(blckDeleteSQL);
                        String updateSubsidy = "update tbcustomeraccounts set SUBSIDY_AMOUNT=SUBSIDY_AMOUNT-" + sumBlock + " where account_No='" + account_no + "'";
                        stm4.executeUpdate(updateSubsidy);
                    }
                }
                stm5.executeUpdate(unblckDeleteSQL);
                conn.commit();
            } else {
                throw new NotFoundException();
            }
        } catch (NotFoundException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ServerAuthenticationException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount()#6 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.resetAccount()#7 = -- Error :: " + e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount()#8 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (rs1 != null) rs1.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #9= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (rs2 != null) rs2.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #10= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (stm1 != null) stm1.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #11= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (stm2 != null) stm2.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #12= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (stm3 != null) stm3.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #13= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (stm4 != null) stm4.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #14= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (stm5 != null) stm5.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.resetAccount() #15= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(conn);
        }
    }

    public static List getBranchTxList(String accountNo, String cardNo, String fromDate, String toDate, String fromTime, String toTime,
                                       String minAmount, String maxAmount, int transCount, String opCode, String branchDocNo,
                                       String creditDebit, String fromSequence) throws SQLException {
        String brchDocNo;
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, " +
                " SGB_ACTION_CODE, SGB_BRANCH_ID, IS_REVERSAL_TXN, AMOUNT, FEE_AMOUNT, SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,log_id,CREATION_DATE,DESC,TERMINAL_ID,TX_SRC,HOSTCODE,CREATION_TIME  " +
                " from tbCFSTx as tx ";
        if (creditDebit != null && creditDebit.equals(Constants.CREDIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "') or " +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ))";
        else if (creditDebit != null && creditDebit.equals(Constants.DEBIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and  IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ) or" +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "' ))";
        else
            hql = hql + " where (tx.src_Account_No = '" + accountNo + "' or tx.dest_Account_No = '" + accountNo + "' )";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+ fromDate + "' and '"+ toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (minAmount != null && !minAmount.equals(""))
            hql = hql + " and tx.amount >=  " + Long.valueOf(minAmount);
        if (maxAmount != null && !maxAmount.equals(""))
            hql = hql + " and tx.amount <=  " + Long.valueOf(maxAmount);
        if (opCode != null && !opCode.equals(""))
            hql = hql + " and tx.SGB_ACTION_CODE =  '" + opCode + "'";
        if (branchDocNo != null && !branchDocNo.equals(""))
            hql = hql + " and tx.BRANCH_DOCNO =  '" + branchDocNo + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " order by tx.log_ID desc fetch first " + transCount + " rows only   with ur";
        } else
            hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setRRN(resultSet.getString("log_id"));
                tx.setDescription(null != resultSet.getString("DESC") ? resultSet.getString("DESC").trim() : " ");
                tx.setHostCode(null != resultSet.getString("HOSTCODE") ? resultSet.getString("HOSTCODE") : "  ");
                if (resultSet.getString("TX_SRC").trim().equalsIgnoreCase(Fields.SERVICE_NASIM))
                    tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                else
                    tx.setMerchantTerminalId("00");
                brchDocNo = resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000";
                tx.setBranchDocNo(brchDocNo);
                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);
                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean getFollowTx(String date, String msgSeq, String time, String serviceType) throws SQLException, NotFoundException {
        String sql = "select TX_PK, SESSION_ID from tbCFSTx where " +
                "rrn = '" + msgSeq + "' and " +
                "TX_ORIG_DATE = '" + date + "' and " +
                "TX_ORIG_TIME = '" + time + "' and " +
                "TX_SRC = '" + serviceType + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getFollowTx() =   -- Error :: " + e + " -- sql = " + sql);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int getBranchTxCount(String accountNo, String cardNo, String fromDate, String toDate, String fromTime, String toTime,
                                       String minAmount, String maxAmount, String opCode, String branchDocNo,
                                       String creditDebit, String fromSequence) throws SQLException {
        String hql = "select count(*) from tbCFSTx as tx ";
        if (creditDebit != null && creditDebit.equals(Constants.CREDIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "') or " +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ))";
        else if (creditDebit != null && creditDebit.equals(Constants.DEBIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and  IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ) or" +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "' ))";
        else
            hql = hql + " where (tx.src_Account_No = '" + accountNo + "' or tx.dest_Account_No = '" + accountNo + "' )";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+ fromDate + "' and '"+ toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (minAmount != null && !minAmount.equals(""))
            hql = hql + " and tx.amount >=  " + Long.valueOf(minAmount);
        if (maxAmount != null && !maxAmount.equals(""))
            hql = hql + " and tx.amount <=  " + Long.valueOf(maxAmount);
        if (opCode != null && !opCode.equals(""))
            hql = hql + " and tx.SGB_ACTION_CODE =  '" + opCode + "'";
        if (branchDocNo != null && !branchDocNo.equals(""))
            hql = hql + " and tx.BRANCH_DOCNO =  '" + branchDocNo + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " with ur";
        } else
            hql = hql + " with ur";
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            if (resultSet.next()) {
                connection.commit();
                return resultSet.getInt(1);
            }
            connection.commit();
            return 0;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List getBranchFullStatement(String accountNo, String cardNo, String fromDate, String toDate, String fromTime, String toTime,
                                              String minAmount, String maxAmount, int transCount, String opCode, String branchDocNo,
                                              String creditDebit, String fromSequence) throws SQLException {
        String brchDocNo;
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, " +
                " SGB_ACTION_CODE, SGB_BRANCH_ID, IS_REVERSAL_TXN, AMOUNT, FEE_AMOUNT, SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,log_id,CREATION_DATE,EXTRA_INFO,TERMINAL_ID,TX_SRC,HOSTCODE,CREATION_TIME,EBNK_MSG_SEQ,ID1,ID2 " +
                " from tbCFSTx as tx ";
        if (creditDebit != null && creditDebit.equals(Constants.CREDIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "') or " +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ))";
        else if (creditDebit != null && creditDebit.equals(Constants.DEBIT))
            hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and  IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ) or" +
                    " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "' ))";
        else
            hql = hql + " where (tx.src_Account_No = '" + accountNo + "' or tx.dest_Account_No = '" + accountNo + "' )";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+ fromDate + "' and '"+ toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (minAmount != null && !minAmount.equals(""))
            hql = hql + " and tx.amount >=  " + Long.valueOf(minAmount);
        if (maxAmount != null && !maxAmount.equals(""))
            hql = hql + " and tx.amount <=  " + Long.valueOf(maxAmount);
        if (opCode != null && !opCode.equals(""))
            hql = hql + " and tx.SGB_ACTION_CODE =  '" + opCode + "'";
        if (branchDocNo != null && !branchDocNo.equals(""))
            hql = hql + " and tx.BRANCH_DOCNO =  '" + branchDocNo + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " order by tx.log_ID desc fetch first " + transCount + " rows only   with ur";
        } else
            hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setRRN(resultSet.getString("log_id"));
                tx.setDescription(null != resultSet.getString("EXTRA_INFO") ? resultSet.getString("EXTRA_INFO").trim() : " ");
                tx.setHostCode(null != resultSet.getString("HOSTCODE") ? resultSet.getString("HOSTCODE") : "  ");
                if (resultSet.getString("TX_SRC").trim().equalsIgnoreCase(Fields.SERVICE_NASIM))
                    tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                else
                    tx.setMerchantTerminalId("00");
                brchDocNo = resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000";
                tx.setBranchDocNo(brchDocNo);
                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                tx.setMessageSeq(null != resultSet.getString("EBNK_MSG_SEQ") ? resultSet.getString("EBNK_MSG_SEQ").trim() : " ");
                tx.setId1(null != resultSet.getString("ID1") ? resultSet.getString("ID1").trim() : " ");
                tx.setId2(null != resultSet.getString("ID2") ? resultSet.getString("ID2").trim() : " ");

                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int doRevokeAccountTransactional(String accountNo, String branchId,String session_id,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statementHistory = null;
        ResultSet historyRS = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement srvStatement = null;
        ResultSet srvRS = null;
        Statement custaccStatement = null;
        Statement cardStatement = null;
        ResultSet cardRS = null;
        Statement deleteAccST = null;
        Statement deleteSrvST = null;
        Statement insStm = null;
        Statement nacLogStatment = null;
        connection = dbConnectionPool.getConnection();
        try {
            String historySql = "SELECT ACCOUNT_NO from tbaccounthistory where ACCOUNT_NO = '" + accountNo + "' with ur";
            statementHistory = connection.createStatement();
            historyRS = statementHistory.executeQuery(historySql);
            if (historyRS.next()) {
//                    "revoke account before"
                connection.rollback();
                return 1;
            }

                String accountSql = "SELECT CUSTOMER_ID,ACCOUNT_TYPE,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS," +
                    "CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE,SPARROW_BRANCH_ID,ORIG_EDIT_DATE," +
                    "SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,balance, SUBSIDY_AMOUNT " +
                    "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";
            statement = connection.createStatement();
            accRS = statement.executeQuery(accountSql);
            if (accRS.next()) {
                if (accRS.getLong("BALANCE") != 0 || accRS.getLong("SUBSIDY_AMOUNT") != 0) {
                    //"Invalid operation"
                    connection.rollback();
                    return 2;
                }

                String sgbBranchId = accRS.getString("SGB_BRANCH_ID");
                if (!sgbBranchId.equalsIgnoreCase(branchId)) {
                    connection.rollback();
                    return 3;
                }

                //we should check all type of account because the account should not have any cards. So, the line below was deactivated at version 971107
//                if (accRS.getString("ACCOUNT_TITLE").trim().equalsIgnoreCase("3")) {
                    String cardSql = "SELECT pan from tbCfscardAccount where ACCOUNT_NO = '" + accountNo + "' and status=1 with ur";
                    cardStatement = connection.createStatement();
                    cardRS = cardStatement.executeQuery(cardSql);
                    if (cardRS.next()) {
                        //ACCOUNT ASSIGNED TO CARD
                        connection.rollback();
                        return 4;
                    }
//                }

                String srvSql = "SELECT CUSTOMER_ID,STATUS,CREATION_DATE,HOST_ID,TEMPLATE_ID,NATIONAL_CODE," +
                        "TMP_UPDATE_DATE,ACCOUNT_NATURE, SMSNOTIFICATION,STATUSMELLI,E_STATUS,SERVICE_STATUS,STATUSD " +
                        "from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' for update;";
                srvStatement = connection.createStatement();
                srvRS = srvStatement.executeQuery(srvSql);
                if (srvRS.next()) {
                    if (!srvRS.getString("E_STATUS").equals(Constants.E_STATUS_ACTIVE)){
                        connection.rollback();
                        return 5;
                    }
                    if (!srvRS.getString("SERVICE_STATUS").equals(Constants.SERVICE_STATUS_ACTIVE)) {
                        connection.rollback();
                        return 8;
                    }
                    if (srvRS.getString("STATUSD").startsWith(Constants.CM_BLOCK) || srvRS.getString("STATUSD").endsWith(Constants.CM_BLOCK)) {
                        connection.rollback();
                        return 9;
                    }

                    AccountHistory accountHistory = new AccountHistory(
                            accRS.getString("CUSTOMER_ID"), accRS.getString("ACCOUNT_TYPE"), accountNo, accRS.getInt("LOCK_STATUS"), accRS.getString("CURRENCY"),
                            accRS.getString("ACCOUNT_TITLE"), accRS.getInt("STATUS"), accRS.getString("CREATION_DATE"), accRS.getString("CREATION_TIME"),
                            accRS.getString("ORIG_CREATE_DATE"), accRS.getString("SPARROW_BRANCH_ID"), accRS.getString("ORIG_EDIT_DATE"), accRS.getString("SGB_BRANCH_ID"),
                            accRS.getString("NATURE"), accRS.getString("ACCOUNT_OPENER_NAME"), accRS.getInt("WITHDRAW_TYPE"), srvRS.getString("CUSTOMER_ID"), srvRS.getInt("STATUS"),
                            srvRS.getString("CREATION_DATE"), srvRS.getInt("HOST_ID"), srvRS.getInt("TEMPLATE_ID"), srvRS.getString("NATIONAL_CODE"), srvRS.getString("TMP_UPDATE_DATE"),
                            srvRS.getInt("ACCOUNT_NATURE"), srvRS.getString("SMSNOTIFICATION"), srvRS.getInt("STATUSMELLI"));


                    String insertSql = "insert into TBACCOUNTHISTORY " +
                            "(CUSTOMER_ID,ACCOUNT_TYPE,ACCOUNT_NO,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS,CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE," +
                            "SPARROW_BRANCH_ID,ORIG_EDIT_DATE,SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,CUSTOMER_ID_srv,STATUS_srv," +
                            "CREATION_DATE_srv,HOST_ID_srv,TEMPLATE_ID,NATIONAL_CODE,TMP_UPDATE_DATE,ACCOUNT_NATURE,SMSNOTIFICATION,STATUSMELLI,CARD_STATUS) " +
                            "values(";
                    if (accountHistory.getCustomerId() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCustomerId() + "', ";
                    if (accountHistory.getAccountType() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getAccountType() + "', ";
                    insertSql = insertSql + "'" + accountNo + "', ";
                    insertSql = insertSql + accountHistory.getLockStatus() + ", ";
                    if (accountHistory.getCurrency() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCurrency() + "', ";
                    if (accountHistory.getAccountTitle() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getAccountTitle() + "', ";
                    insertSql = insertSql + accountHistory.getAccountStatus() + ", ";
                    if (accountHistory.getCreationDate() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCreationDate() + "', ";
                    if (accountHistory.getCreationTime() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCreationTime() + "', ";

                    if (accountHistory.getOrigCreatDate() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getOrigCreatDate() + "', ";

                    if (accountHistory.getSparrow_branch_id() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getSparrow_branch_id() + "', ";

                    if (accountHistory.getOrigEditDate() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getOrigEditDate() + "', ";
                    if (accountHistory.getSgb_branch_id() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getSgb_branch_id() + "', ";

                    if (accountHistory.getNature() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getNature() + "', ";

                    if (accountHistory.getAccountOpenerName() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getAccountOpenerName() + "', ";
                    insertSql = insertSql + accountHistory.getWithdrawType() + ", ";
                    if (accountHistory.getCustomerIdSrv() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCustomerIdSrv() + "', ";

                    insertSql = insertSql + "'" + accountHistory.getStatusSrv() + "', ";

                    if (accountHistory.getCreationDateSrv() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getCreationDateSrv() + "', ";

                    insertSql = insertSql + "'" + accountHistory.getHostIDSrv() + "', ";

                    insertSql = insertSql + accountHistory.getTemplateIdSrv() + ", ";

                    if (accountHistory.getNationalCode() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getNationalCode() + "', ";
                    if (accountHistory.getTmpUpdateDate() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getTmpUpdateDate() + "', ";

                    insertSql = insertSql + accountHistory.getAccountNature() + ", ";

                    if (accountHistory.getSmsNotification() == null)
                        insertSql = insertSql + "'',";
                    else
                        insertSql = insertSql + "'" + accountHistory.getSmsNotification() + "', ";
                    insertSql = insertSql + accountHistory.getStatusMelli() + ", ";
                    insertSql = insertSql + "'"+ DateUtil.getSystemDate()+ "' ";
                    insertSql = insertSql + ")";
                    insStm = connection.createStatement();
                    insStm.executeUpdate(insertSql);


                    String deleteAccSQL = "delete from TBCUSTOMERACCOUNTS where account_no = '" + accountNo + "'";
                    deleteAccST = connection.createStatement();
                    deleteAccST.execute(deleteAccSQL);

                    String deleteSrvSQL = "delete from TBCUSTOMERSRV where account_no = '" + accountNo + "'";
                    deleteSrvST = connection.createStatement();
                    deleteSrvST.execute(deleteSrvSQL);

                    String cusaccUpdate = "update tbcustacc set status='" + Constants.REVOKE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and status='" + Constants.ACTIVE_ROW_STATUS + "'";
                    custaccStatement=connection.createStatement();
                    custaccStatement.execute(cusaccUpdate);


                    String nationalCode= accountHistory.getNationalCode()==null?"":accountHistory.getNationalCode().trim();
                    String accountTitle= accountHistory.getAccountTitle()==null?"":accountHistory.getAccountTitle().trim();
                    String accountGroup=accountHistory.getAccountType()==null?"":accountHistory.getAccountType().trim();
                    String origCreatDate=accountHistory.getOrigCreatDate()==null?"":accountHistory.getOrigCreatDate().trim();

                    String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                            "SESSION_ID," +
                            "CHANNEL_TYPE," +
                            "PARTNO," +
                            "INSERT_DATETIME," +
                            "ACCOUNT_NO," +
                            "NATIONAL_CODE," +
                            "OPERATION_TYPE," +
                            "ACCOUNT_TITLE," +
                            "HANDLED,"+
                            "OPEN_DATE,"+
                            "OPEN_TIME,"+
                            "BRANCH_ID,"+
                            "ACCOUNT_GROUP"+
                            ") values(" +
                            "'" + session_id + "'," +
                            "'" + channel_type + "'," +
                            getPartNo() + "," +
                            "current_timestamp, " +
                            "'" + accountNo + "'," +
                            "'" +nationalCode+"'," +
                            "'" + Constants.REVOKE_STATUS + "',"+
                            "'" + accountTitle + "'," +
                            1 +","+
                            "'" + origCreatDate + "'," +
                            "'" + DateUtil.getSystemTime() + "'," +
                            "'" +sgbBranchId + "'," +
                            "'" + accountGroup + "'" +
                            " )";

                    nacLogStatment = connection.createStatement();
                    nacLogStatment.execute(insertCustomer_sql);
                    nacLogStatment.close();

                    connection.commit();
                } else{
                    connection.rollback();
                    return 6;
                }
            } else{
                connection.rollback();
                return 7;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doRevokeAccountTransactional()#7 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional()#8 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (historyRS != null) historyRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #16= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statementHistory != null) statementHistory.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #17= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #9= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #10= -- Error :: " + e1);
                e1.printStackTrace();
            }

            try {
                if (srvRS != null) srvRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #11= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (srvStatement != null) srvStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #12= -- Error :: " + e1);
                e1.printStackTrace();
            }

            try {
                if (deleteSrvST != null) deleteSrvST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #13= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (deleteAccST != null) deleteAccST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #14= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #15= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (cardRS != null) cardRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #17= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (cardStatement != null) cardStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #18= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (custaccStatement != null) custaccStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #19= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (nacLogStatment != null) nacLogStatment.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #20= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static void doRevivalAccountTransactional(String accountNo, String branchId,String session_id,String channel_type) throws NotFoundException, SQLException, ServerAuthenticationException, ModelException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Statement accStatement = null;
        Statement srvStatement = null;
        Statement deleteAccST = null;
        Statement custaccStatement = null;
        Statement nacLogStatement=null;
        connection = dbConnectionPool.getConnection();
        try {
            String accountSql = "SELECT * from tbAccounthistory where ACCOUNT_NO = '" + accountNo + "' for update;";
            statement = connection.createStatement();
            rs = statement.executeQuery(accountSql);
            if (rs.next()) {

                if (rs.getString("REVOKE_TYPE").equalsIgnoreCase(Constants.UNCONDITIONAL_REVOKE_STATUS)) {
                    throw new ModelException("Invalid operation");
                }

                String sgbBranchId = rs.getString("SGB_BRANCH_ID");
                if (!sgbBranchId.equalsIgnoreCase(branchId))
                    throw new ServerAuthenticationException("SGB_Branch_ID is not equal Block_Branch_ID");

                int srvStatus;
                if (rs.getString("STATUS_srv") == null)
                    srvStatus = 0;
                else
                    srvStatus = Integer.parseInt(rs.getString("STATUS_srv"));

                int srvHostId;
                if (rs.getString("HOST_ID_srv") == null)
                    srvHostId = 0;
                else
                    srvHostId = Integer.parseInt(rs.getString("HOST_ID_srv"));

                AccountHistory accountHistory = new AccountHistory(
                        rs.getString("CUSTOMER_ID"), rs.getString("ACCOUNT_TYPE"), accountNo, rs.getInt("LOCK_STATUS"), rs.getString("CURRENCY"),
                        rs.getString("ACCOUNT_TITLE"), rs.getInt("STATUS"), rs.getString("CREATION_DATE"), rs.getString("CREATION_TIME"),
                        rs.getString("ORIG_CREATE_DATE"), rs.getString("SPARROW_BRANCH_ID"), rs.getString("ORIG_EDIT_DATE"), rs.getString("SGB_BRANCH_ID"),
                        rs.getString("NATURE"), rs.getString("ACCOUNT_OPENER_NAME"), rs.getInt("WITHDRAW_TYPE"), rs.getString("CUSTOMER_ID_SRV"), srvStatus,
                        rs.getString("CREATION_DATE_srv"), srvHostId, rs.getInt("TEMPLATE_ID"), rs.getString("NATIONAL_CODE"), rs.getString("TMP_UPDATE_DATE"),
                        rs.getInt("ACCOUNT_NATURE"), rs.getString("SMSNOTIFICATION"), rs.getInt("STATUSMELLI"));


                String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                        "CUSTOMER_ID," +
                        "ACCOUNT_TYPE," +     // Account Group
                        "ACCOUNT_NO," +
                        "LOCK_STATUS," +
                        "CURRENCY," +
                        "BALANCE," +
                        "ACCOUNT_TITLE," +
                        "STATUS," +
                        "ACCOUNT_SRC," +
                        "CREATION_DATE," +
                        "CREATION_TIME," +
                        "ORIG_CREATE_DATE," +
                        "ORIG_EDIT_DATE," +
                        "SGB_BRANCH_ID," +
                        "HOST_ID, " +
                        "SPARROW_BRANCH_ID, " +
                        "NATURE, " +
                        "ACCOUNT_OPENER_NAME, " +
                        "WITHDRAW_TYPE " +
                        ") values(";
                if (accountHistory.getCustomerId() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCustomerId() + "',";

                if (accountHistory.getAccountType() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountType() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountNo + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getLockStatus() + ",";
                if (accountHistory.getCurrency() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCurrency() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + "0,";
                if (accountHistory.getAccountTitle() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountTitle() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getAccountStatus() + ",";
                insertCustomerAccount_sql = insertCustomerAccount_sql + 2 + ",";

                if (accountHistory.getCreationDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCreationDate() + "',";

                if (accountHistory.getCreationTime() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCreationTime() + "',";

                if (accountHistory.getOrigCreatDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getOrigCreatDate() + "',";

                if (accountHistory.getOrigEditDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getOrigEditDate() + "',";

                if (accountHistory.getSgb_branch_id() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getSgb_branch_id() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + Constants.HOST_CFS + ",";

                if (accountHistory.getSparrow_branch_id() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getSparrow_branch_id() + "',";

                if (accountHistory.getNature() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getNature() + "',";

                if (accountHistory.getAccountOpenerName() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountOpenerName() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getWithdrawType();
                insertCustomerAccount_sql = insertCustomerAccount_sql + ")";

                accStatement = connection.createStatement();
                accStatement.execute(insertCustomerAccount_sql);


                String srvSql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                        "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP," +
                        "NATIONAL_CODE,STATUSMELLI, LANG, SMSNOTIFICATION, ACCOUNT_NATURE, TMP_UPDATE_DATE" +
                        ") values (";

                if (accountHistory.getCustomerIdSrv() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getCustomerIdSrv() + "',";
                srvSql = srvSql + "'1234',";
                srvSql = srvSql + "'1234',";
                srvSql = srvSql + "current_timestamp,";
                srvSql = srvSql + "'" + accountNo + "',";
                srvSql = srvSql + accountHistory.getTemplateIdSrv() + ",";
                srvSql = srvSql + accountHistory.getStatusSrv() + ",";
                srvSql = srvSql + accountHistory.getHostIDSrv() + ",";

                if (accountHistory.getCreationDateSrv() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getCreationDateSrv() + "',";

                if (accountHistory.getAccountType() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getAccountType() + "',";

                if (accountHistory.getNationalCode() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getNationalCode() + "',";
                srvSql = srvSql + accountHistory.getStatusMelli() + ",";
                srvSql = srvSql + 1 + ",";

                if (accountHistory.getSmsNotification() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getSmsNotification() + "',";

                srvSql = srvSql + accountHistory.getAccountNature() + ",";
                if (accountHistory.getTmpUpdateDate() == null)
                    srvSql = srvSql + "''";
                else
                    srvSql = srvSql + "'" + accountHistory.getTmpUpdateDate() + "'";
                srvSql = srvSql + " )";

                srvStatement = connection.createStatement();
                srvStatement.execute(srvSql);

                String deleteAccSQL = "delete from TBACCOUNTHISTORY where account_no = '" + accountNo + "'";
                deleteAccST = connection.createStatement();
                deleteAccST.execute(deleteAccSQL);

                String cusaccUpdate = "update tbcustacc set status='" + Constants.ACTIVE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and status='" + Constants.REVOKE_ROW_STATUS+"'";
                custaccStatement=connection.createStatement();
                custaccStatement.execute(cusaccUpdate);


  String  nationalCode=accountHistory.getNationalCode()==null?"":accountHistory.getNationalCode().trim();
  String  accountTitle=accountHistory.getAccountTitle()==null?"":accountHistory.getAccountTitle().trim();
  String accountGroup=accountHistory.getAccountType()==null?"":accountHistory.getAccountType().trim();
  String origCreatDate=accountHistory.getOrigCreatDate()==null?"":accountHistory.getOrigCreatDate().trim();

                String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                        "SESSION_ID," +
                        "CHANNEL_TYPE," +
                        "PARTNO," +
                        "INSERT_DATETIME," +
                        "ACCOUNT_NO," +
                        "NATIONAL_CODE," +
                        "OPERATION_TYPE," +
                        "ACCOUNT_TITLE," +
                        "HANDLED,"+
                        "OPEN_DATE,"+
                        "OPEN_TIME,"+
                        "BRANCH_ID,"+
                        "ACCOUNT_GROUP"+
                        ") values(" +
                        "'" + session_id + "'," +
                        "'" + channel_type + "'," +
                        getPartNo() + "," +
                        "current_timestamp, " +
                        "'" + accountNo + "'," +
                        "'" + nationalCode+ "'," +
                        "'" + Constants.REVIVAL_STATUS+ "'," +
                        "'" + accountTitle + "'," +
                        1 +","+
                        "'" + origCreatDate + "'," +
                        "'" + DateUtil.getSystemTime() + "'," +
                        "'" + sgbBranchId + "'," +
                        "'" + accountGroup + "'" +
                        " )";
                nacLogStatement = connection.createStatement();
                nacLogStatement.execute(insertCustomer_sql);
                nacLogStatement.close();

                connection.commit();
            } else
                throw new NotFoundException("Not found Account = " + accountNo);
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.doRevivalAccountTransactional()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevivalAccountTransactional()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ServerAuthenticationException e) {
            log.error("CFSFacadeNew.doRevivalAccountTransactional()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevivalAccountTransactional()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doRevivalAccountTransactional()#5 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevivalAccountTransactional()#6 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.error("CFSFacadeNew.doRevivalAccountTransactional()#7 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevivalAccountTransactional()#8 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (accStatement != null) accStatement.close();
                if (srvStatement != null) srvStatement.close();
                if (deleteAccST != null) deleteAccST.close();
                if (custaccStatement != null) custaccStatement.close();
                if (nacLogStatement != null) nacLogStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doRevokeAccountTransactional() #9= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static boolean updateAccountStatus(String acc, int status, String sessionId, String terminalId, String userId,
                                           String branchCode, String channelType, String desc,Tx tx,String documentStatus) throws NotFoundException, SQLException {
        long newBalance=0;
        long minBalance=0;
        int count = 0;
        Connection connection = null;
        Statement statement = null;
        Statement insertStatement = null;
        Statement smsStatement = null;
        Statement selectStatement = null;
        ResultSet accRS=null;
        boolean havingDocument=false;
        try {
            connection = dbConnectionPool.getConnection();

            if (channelType.equalsIgnoreCase(Constants.CHN_USER_SIMIN)) {
                String selectSql = "SELECT balance, SUBSIDY_AMOUNT,ACCOUNT_TYPE from tbCustomerAccounts " +
                        "where ACCOUNT_NO = '" + acc + "' and LOCK_STATUS = 1 for update";
                selectStatement = connection.createStatement();
                accRS = selectStatement.executeQuery(selectSql);
                if (accRS.next()) {
                    long balance = accRS.getLong("BALANCE");
                    long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                    String accountType = accRS.getString("ACCOUNT_TYPE");

                    minBalance = getMinBalance(accountType);

                    if ((documentStatus.equalsIgnoreCase(Constants.TRUE)) && (balance - subsidyAmount - tx.getAmount() >= minBalance)) {
                        newBalance = balance - tx.getAmount();
                        tx.setSrc_account_balance(newBalance);
                        saveTxTransactional(connection, tx);
                        havingDocument=true;
                    } else {
                        newBalance = balance;
                    }
                }
            }
            String hql = "update tbCustomerAccounts set status = " + status;
            if(channelType.equalsIgnoreCase(Constants.CHN_USER_SIMIN)){
                hql=hql+",balance="+newBalance;
            }
            hql=hql+ " where ACCOUNT_NO = '" + acc + "' ";

            statement = connection.createStatement();
            count = statement.executeUpdate(hql);
            if (count > 0) {
                String sql = "insert into TBSERVICESTATUSLOG " +
                        "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                        "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                        " values('" +
                        acc + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                        branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + status + "'," +
                        "'" + channelType + "','" + Constants.ACCOUNT_STATUS_MESSAGE+ "','" + desc  + "')";
                insertStatement = connection.createStatement();
                insertStatement.executeUpdate(sql);

                if(status==0 && channelType.equalsIgnoreCase(Constants.CHN_USER_SIMIN)){
                    String smsSql = "INSERT INTO TBMSGTRN (ID, TRANSACTION_TYPE, TRANSACTION_DATE," +
                            "TRANSACTION_TIME, ACCOUNT_NUMBER, AMOUNT, CHANNEL) " +
                            "VALUES (NEXT VALUE FOR TBMSGTRN_SEQ" +
                            ",'"+Constants.SMS_TRANSACTION_TYPE_BLOCK_ACCOUNT+"'"+
                            ",'"+DateUtil.getSystemDate()+"'"+
                            ",'"+DateUtil.getSystemTime()+"'"+
                            ",'"+acc+"'"+
                            ",0"+
                            ",'" + channelType + "'" +
                            ")";
                    smsStatement = connection.createStatement();
                    smsStatement.executeUpdate(smsSql);
                }
                connection.commit();
            } else {
                connection.rollback();
                throw new NotFoundException("Account " + acc + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccount() #1-- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccount() #2=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            if (insertStatement != null) insertStatement.close();
            if (smsStatement != null) smsStatement.close();
            if (selectStatement != null) selectStatement.close();
            if (accRS != null) accRS.close();
            dbConnectionPool.returnConnection(connection);
        }
        return havingDocument;
    }

    public static String getBlockNo() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String blockNo = "";

        String sql = "select next value for seq_BlockNo as maxID from sysibm.sysdummy1 with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            connection.commit();
            if (rs.next())
                blockNo = rs.getString(1).trim();


            try {
                blockNo = ISOUtil.zeropad(blockNo, 13);
            } catch (ISOException e) {
                log.error("Can not zeropad BlockNo = '" + blockNo + "' in CFSFacadeNew--getBlockNo : " + e.getMessage());
            }
            return blockNo;

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<String> getCustomerInfo(String customerId) throws NotFoundException, SQLException, Exception {
        String hql = "select NAME_FA,SURNAME_FA from tbCustomer where CUSTOMER_ID = '" + customerId + "' with ur";
        List<String> info = new ArrayList<String>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                info.add(0, resultSet.getString("NAME_FA"));
                info.add(1, resultSet.getString("SURNAME_FA"));

                connection.commit();
                return info;
            } else {
                connection.rollback();
                throw new NotFoundException("customer with customer_id " + customerId + " does not exist");
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getCustomerInfo() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.getCustomerInfo() =   -- Error2 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static boolean checkGiftDocNo(String branchCode,String nationalCode,String branchDocNo,String date) throws SQLException {
        String sql = "select BRANCH_DOCNO from TBGIFTCARD where branch_CODE = '" + branchCode + "' and BRANCH_DOCNO='" +
                branchDocNo + "' and NATIONAL_CODE='" + nationalCode + "' and TX_DATE='" + date + "' with ur";

        Connection connection = null;

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                return true;
            } else
                return false;

        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkGiftDocNo() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String getGiftDocNo(String cardNo) throws SQLException,ServerAuthenticationException {

        String sql = "select BRANCH_DOCNO from TBGIFTCARD where CARD_NO = '" + cardNo + "' and TX_CODE='" +
                TJCommand.CMD_CMS_GIFTCARD_CHARGE+ "' and ISDONE=0 with ur";

        Connection connection = null;

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("BRANCH_DOCNO");
            } else
                throw new ServerAuthenticationException("");

        } catch (ServerAuthenticationException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkGiftDocNo() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }



    public static long updateDestAccountTransactional(Connection connection, String accountNo, long amount,String requestType) throws ModelException, SQLException, NotFoundException {
        String sql = "SELECT balance from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;

        try {
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");

                if (requestType.equalsIgnoreCase("0") && balance!=0) {
                    throw new ModelException("");
                }
                long new_balance = balance + amount ;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);

                return new_balance;
            }
            throw new NotFoundException("Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #4= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static void getConflictAccount(String branchCode,String accountNo) throws SQLException, NotFoundException,ModelException {

        String sql = "select E_CONFLICT_ACC,STATUS from TBBranch where branch_CODE = '" + branchCode + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                if (resultSet.getString("STATUS").equals("0"))
                    throw new NotFoundException("E conflict Account for BranchCode: " + branchCode + " is disabled");

                String confilctAccount=resultSet.getString("E_CONFLICT_ACC");
                if(confilctAccount==null || confilctAccount.trim().equals(""))
                    throw new NotFoundException("E conflict Account for BranchCode: " + branchCode + " is disabled");
                else{
                    if(!accountNo.equals(confilctAccount.trim()))
                        throw new ModelException("");
                }
            } else {
                throw new NotFoundException("Can not Find any E_Conflict account with branch_CODE = " + branchCode);
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getConflictAccount() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static boolean getGiftCardTx(Connection connection,Tx tx) throws SQLException {

        String requestType= tx.getCardSequenceNo();
        String txCode="";
        if(requestType!=null &&  !requestType.trim().equals("") && requestType.trim().equals("1")){
            //CANCELLATION
            txCode=TJCommand.CMD_CMS_CANCELLATION;
        }else if(requestType!=null &&  !requestType.trim().equals("") && requestType.trim().equals("2")){
            //DISCHARGE
            txCode=TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD;
        }
        String sql = "select  ISDONE from TBGIFTCARD where branch_CODE = '" + tx.getSgbBranchId() + "' and BRANCH_DOCNO='" +
                tx.getBranchDocNo() + "' and NATIONAL_CODE='" + tx.getDescription().trim() + "' and TX_CODE='" + txCode + "'" +
                " and ISDONE =0 for update";

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return true;
            } else
                return false;

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getGiftCardTx() =   -- Error:: " + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }


    public static long getGiftCardTx(String branchCode, String branchDocNo, String nationalCode, String txCode, int isDone,String origDate) throws SQLException,NotFoundException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select  AMOUNT from TBGIFTCARD where branch_CODE = '" + branchCode + "' and BRANCH_DOCNO='" +
                branchDocNo + "' and NATIONAL_CODE='" + nationalCode + "' and TX_CODE='" + txCode + "'" +
                " and ISDONE =" + isDone+" and TX_DATE='"+origDate+"' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return (resultSet.getLong("AMOUNT"));
            else
                throw new NotFoundException("cancellation request with branchCode = " + branchCode + ", branchDocNo = " +
                        branchDocNo + ", nationalCode = " + nationalCode + ", origDate = " + origDate + " does not exist");


        } catch (SQLException e) {
            log.error("CFSFacadeNew.getGiftCardTx() =   -- Error:: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void setGiftTxIsDoneTransactional(Connection connection, Tx orig_tx) throws NotFoundException, SQLException {

        String txCode="";
        if(orig_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_GIFTCARD)){
            String requestType= orig_tx.getCardSequenceNo();
            if(requestType!=null &&  !requestType.trim().equals("") && requestType.trim().equals("1")){
                //CANCELLATION
                txCode=TJCommand.CMD_CMS_CANCELLATION;
            }else if(requestType!=null &&  !requestType.trim().equals("") && requestType.trim().equals("2")){
                //DISCHARGE
                txCode=TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD;
            }
        }else if(orig_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD_REVERSAL)){
            txCode=TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD;
        }

        String sql = "update TBGIFTCARD set ISDONE = 1 where branch_CODE = '" + orig_tx.getSgbBranchId() + "' and BRANCH_DOCNO='" +
                orig_tx.getBranchDocNo() + "' and NATIONAL_CODE='" + orig_tx.getDescription().trim() + "' and TX_CODE='" + txCode + "' ";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.setGiftTxIsDoneTransactional() -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static List<String> CheckGiftCardTx(String branchCode, String txCode, String pan) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> giftCardList=new ArrayList<String>();
        String sql = "select  AMOUNT,BRANCH_DOCNO from TBGIFTCARD where branch_CODE = '" + branchCode + "' and CARD_NO='" +
                pan + "' and TX_CODE='" + txCode + "'" +" with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()){
                giftCardList.add(0,String.valueOf(resultSet.getLong("AMOUNT")));
                giftCardList.add(1,String.valueOf(resultSet.getString("BRANCH_DOCNO")));
            }
            return giftCardList;


        } catch (SQLException e) {
            log.error("CFSFacadeNew.CheckGiftCardTx() =   -- Error:: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<String> updateCardAccountTransactionalForRevoke(Connection connection, String accountNo,String cardNo,String txCode,String branchCode) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> amountExist=new ArrayList<String>();
        String sql = "select Max_Trans_Limit from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) maxTransLimit = resultSet.getLong("Max_Trans_Limit");
        } catch (SQLException e) {
            log.error("sql = " + sql);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        if (maxTransLimit <= 0) {
            List<String> giftCardList=new ArrayList<String>();
            giftCardList=CheckGiftCardTx(branchCode, txCode, cardNo);
            if (giftCardList.size() == 2) {
                long amount = Long.valueOf(giftCardList.get(0));
                if (amount > 0) {
                    //exist in tbgiftcard
                    amountExist.add(0, String.valueOf(amount));
                    amountExist.add(1, "1");
                    amountExist.add(2,giftCardList.get(1));  //Branch Doc Number
                    return amountExist;
                } else {
                    throw new ModelException("Not Sufficient Funds for PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'");
                }
            } else {
                throw new ModelException("Not Sufficient Funds for PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'");
            }
        }

        amountExist.add(0, String.valueOf(maxTransLimit));
        amountExist.add(1,"0");

        sql = "update tbCFSCardAccount set max_trans_limit =0 , status=0   where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'";
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
            statement1.execute(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
        }

        return amountExist;
    }

    public static boolean checkRequestNoTransactional(Connection connection, Tx orig_tx) throws NotFoundException, SQLException, ServerAuthenticationException {

        String txCode = "";
        boolean isDone = false;
        Statement statement = null;
        ResultSet resultSet = null;
        txCode = TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD;

        String sql = "select REQUEST_NO from TBGIFTCARD where branch_CODE = '" + orig_tx.getSgbBranchId() + "' and BRANCH_DOCNO='" +
                orig_tx.getBranchDocNo() + "' and NATIONAL_CODE='" + orig_tx.getDescription().trim() + "' and TX_CODE='" + txCode +
                "' and ISDONE = 0 for update";

        try {

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String requestNo = resultSet.getString("REQUEST_NO");
                if (requestNo != null && !"".equalsIgnoreCase(requestNo.trim()))
                    isDone = true;
            } else {
                // not found document
                throw new ServerAuthenticationException("");

            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkRequestNoTransactional() -- Error :: " + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return isDone;
    }

    public static void txDoRemittanceTransaction(Tx tx, long minBalance, String serial_no, String reg_date, String national_code, String external_id, String feeAmountOpCode) throws NotFoundException, ModelException, SQLException, ISOException,ServerAuthenticationException {

        Connection connection = null;
        Statement remittaneUpdateSt = null;
        Statement remittaneSt = null;
        ResultSet remittanceRs = null;
        String accountReminttance = "";
        String accountFeeAmount = "";
        String opCode = tx.getSgbActionCode();
        long amount = 0;
        long feeAmount = 0;

        String txPk = tx.getTxPk();
        String txmessageId = tx.getMessageId();
        String branchFeeAccountNo = tx.getSrcAccountNo();
        String branchTavazonAccountNo = tx.getDestAccountNo();
        try {
            branchFeeAccountNo = ISOUtil.zeropad(branchFeeAccountNo, 13);
            branchTavazonAccountNo = ISOUtil.zeropad(branchTavazonAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoOneWayTransaction()#1= -- Error :: " + e);
            throw new ISOException();
        }


        if (!tx.getTxSrc().equals(CFSConstants.TXN_SRC_SGB)) {

            try {
                connection = dbConnectionPool.getConnection();

                String sql = "select AMOUNT,FEE_AMOUNT,REMITTANCE_ACCOUNT_NO,FEE_ACCOUNT_NO from TB_REMITTANCE where SERIAL_NO ='" + serial_no + "' and REG_DATE = '" + reg_date + "'and NATIONAL_CODE= '"
                        + national_code + "'";
                if (external_id != null && !external_id.trim().equalsIgnoreCase(""))
                    sql = sql + " and EXTERNAL_ID = '" + external_id + "'";

                sql = sql + " and REMITTANCE_STATUS='" + Constants.REGISTERED_STATUS + "' for update";
                remittaneSt = connection.createStatement();
                remittanceRs = remittaneSt.executeQuery(sql);
                if (remittanceRs.next()) {
                    amount = remittanceRs.getLong("AMOUNT");
                    feeAmount = remittanceRs.getLong("FEE_AMOUNT");
                    accountReminttance = remittanceRs.getString("REMITTANCE_ACCOUNT_NO").trim();
                    accountFeeAmount = remittanceRs.getString("FEE_ACCOUNT_NO").trim();


                    // fund transfer from remittanceFeeAccount to BranchFeeAccount
                    // ----------start------------------------------------------------
                    tx.setSrcAccountNo(accountFeeAmount);
                    long srcBalance = updateSrcAccountByTypeTransactional(connection, accountFeeAmount, -feeAmount, tx.getTxCode(), minBalance, 0, new HashMap());
                    tx.setSrc_account_balance(srcBalance);

                    tx.setDestAccountNo(branchFeeAccountNo);
                    tx.setTotalDestAccNo(branchFeeAccountNo);
                    tx.setTxPk("FK" + txPk.substring(2));
                    tx.setMessageId("2");
                    tx.setAmount(feeAmount);
                    tx.setSgbActionCode(feeAmountOpCode);

                    saveTxTransactional(connection, tx);
                    //---------END-----------------------------------------------------

                    // fund transfer from remittanceAccount to tavazonAccount
                    // -----------------start----------------------------------------------
                    tx.setSrcAccountNo(accountReminttance);
                    srcBalance = updateSrcAccountByTypeTransactional(connection, accountReminttance, -amount, tx.getTxCode(), minBalance, 0, new HashMap());
                    tx.setSrc_account_balance(srcBalance);

                    tx.setDestAccountNo(branchTavazonAccountNo);
                    tx.setTotalDestAccNo(branchTavazonAccountNo);
                    tx.setTxPk(txPk);
                    tx.setMessageId(txmessageId);
                    tx.setAmount(amount);
                    tx.setSgbActionCode(opCode);

                    saveTxTransactional(connection, tx);
                    //---------END-----------------------------------------------------

                    //Update Remittance status
                    String sqlUpdate = "update tb_remittance set REMITTANCE_STATUS='" + Constants.PAYED_STATUS + "' where SERIAL_NO ='" + serial_no + "' and REG_DATE = '" + reg_date + "'and NATIONAL_CODE= '"
                            + national_code + "'";
                    if (external_id != null && !external_id.trim().equalsIgnoreCase(""))
                        sql = sql + " and EXTERNAL_ID = '" + external_id + "'";

                    sql = sql + " and REMITTANCE_STATUS='" + Constants.REGISTERED_STATUS + "'";
                    remittaneUpdateSt = connection.createStatement();
                    remittaneUpdateSt.execute(sqlUpdate);

                    connection.commit();
                } else
                    throw new ServerAuthenticationException("can not Find remittance for serial number:" + serial_no);

            } catch (NotFoundException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txDoRemittanceTransaction()#3 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ModelException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txDoRemittanceTransaction()#5 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (ServerAuthenticationException e) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txDoRemittanceTransaction()#5 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } catch (SQLException e) {
                log.error("CFSFacadeNew.txDoRemittanceTransaction()#6 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txDoRemittanceTransaction()#7 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } finally {
                if(remittanceRs != null) remittanceRs.close();
                if(remittaneUpdateSt != null) remittaneUpdateSt.close();
                if(remittaneSt != null) remittaneSt.close();
                dbConnectionPool.returnConnection(connection);
            }
        }
    }
    public static void insertRemittanceLog(String txString, String sessionId, String messageType,
                                    String channelType, String serial_no, String national_code, String external_id, String req_date) throws SQLException {


        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "insert into TB_REMITTANCE_LOG " +
                    "(PARTNO,SESSION_ID,TX_CODE,CHANNEL_TYPE,SERIAL_NO,NATIONAL_CODE,EXTERNAL_ID,REG_DATE,TX_DATETIME,TX_STRING) values(" +
                    getPartNo() + ",'" + sessionId + "','" + messageType + "', '" + channelType + "','" + serial_no + "'," +
                    "'" + national_code + "','"+ external_id +"','"+ req_date +"', current_timestamp ,'" + txString + "')";

            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertRemittanceLog()#1 =   -- Error :: " + e + " -- TxString  = " + txString);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //****************Branch Manager******************
    public static String getCMParam(String modifier, String ID) throws SQLException, NotFoundException {

        if (cmParametersByIDMap.containsKey("" + modifier + ID))
            return (String) cmParametersByIDMap.get("" + modifier + ID);

        String sql;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            sql = "select DESCRIPTION  from TBCMPARAMS where MODIFIER = '" + modifier + "' and ID=" + ID + "  with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String desc = resultSet.getString("DESCRIPTION");
                cmParametersByIDMap.put("" + modifier + ID, desc);
                return desc;
            } else {
                throw new NotFoundException("Can not Find any DESCRIPTION  where MODIFIER = '" + modifier + "' and ID = '" + ID + "'");
            }

        } catch (SQLException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } catch (NumberFormatException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    //**********************CMS************start***********************
    public static void txnDoTransaction4GiftCard(Tx tx, HashMap extraData)
            throws NotFoundException, ModelException, ISOException, SQLException {


        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        long amount = tx.getAmount();

        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            long srcBalance = 0;
            srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), 0, -tx.getFeeAmount(), extraData);
            tx.setSrc_account_balance(srcBalance);

            if (tx.getTxCode().equals(TJCommand.CMD_CMS_GIFTCARD_CHARGE))
                InsertCardAndCardAccountTransactional(connection, tx.getCardNo(), tx.getDestAccountNo(), tx.getTxOrigDate(), amount, (String) extraData.get(Fields.CUSTOMER_ID));

            if (extraData.containsKey(Fields.MAX_TRANS_LIMIT) && (((Long) extraData.get(Fields.MAX_TRANS_LIMIT)).longValue() != CFSConstants.IGNORE_MAX_TRANS_LIMIT))
                updateCardAccountTransactional(connection, srcAccountNo, tx.getCardNo(), -amount - tx.getFeeAmount(), srcBalance);

            long destBalance = 0;
            destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, amount, tx.getTxCode(), 0);
            tx.setDest_account_balance(destBalance);


            saveTxTransactional(connection, tx);
            saveGiftTxTransactional(connection, tx, (String) extraData.get(Fields.REQUEST_NO));
            if (tx.getTxCode().equals(TJCommand.CMD_CMS_GIFTCARD_CHARGE)){
                increaseLimitAmount(connection,tx.getDescription(),amount);
            }
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoTransaction4GiftCard() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4GiftCard() #5=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoTransaction4GiftCard() #6=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4GiftCard() #7=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoTransaction4GiftCard() #8=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4GiftCard() #9=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void txnDoReverse4GiftCard(Tx orig_tx, Tx rev_tx) throws ModelException, SQLException, NotFoundException {

        String srcAccountNo = orig_tx.getSrcAccountNo();
        String destAccountNo = orig_tx.getDestAccountNo();
        long amount = orig_tx.getAmount();
        long srcBalance = 0;
        long destBalance = 0;

        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            long last_trans_limit = CFSConstants.IGNORE_MAX_TRANS_LIMIT;

            srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, amount, rev_tx.getTxCode(), 0, orig_tx.getFeeAmount(), new HashMap());
            rev_tx.setSrc_account_balance(srcBalance);

            destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, -amount, orig_tx.getTxCode().trim(), 0);

            if (orig_tx.getLastTransLimit() != CFSConstants.IGNORE_MAX_TRANS_LIMIT)
                last_trans_limit = updateCardAccountTransactional(connection, destAccountNo, rev_tx.getCardNo(), -amount, destBalance);
            rev_tx.setLastTransLimit(last_trans_limit);

            orig_tx.setIsReversed(CFSConstants.REVERSED);
            setTxIsReversedTransactional(connection, orig_tx);

            if (rev_tx.getTxCode().equals(TJCommand.CMD_CMS_GIFTCARD_DISCHARGE)){
                setGiftTxIsReversedTransactional(connection, orig_tx);
                decreaseLimitAmount(connection,rev_tx.getDescription(),amount);
                }


            if (rev_tx != null) rev_tx.setDest_account_balance(destBalance);
            saveTxTransactional(connection, rev_tx);

            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoReverse4GiftCard() #2  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoReverse4GiftCard() #3  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoReverse4GiftCard() #4  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoReverse4GiftCard() #5  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoReverse4GiftCard() #6  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoReverse4GiftCard() #7  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static Branch getGiftCardAccounts(String branchCode) throws SQLException, NotFoundException {
        if (giftAccountsMap.containsKey(branchCode))
            return (Branch) giftAccountsMap.get(branchCode);

        String sql = "select ACCOUNT_NO,ISMODIFIED,BRANCH_CRD_ACC,CENTER_CRD_ACC,E_CONFLICT_ACC,STATUS,BATCH_CRD_ACC from TBBranch where branch_CODE = '" + branchCode + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String branchCrdAcc=resultSet.getString("BRANCH_CRD_ACC");
                if (branchCrdAcc != null && !branchCrdAcc.trim().equalsIgnoreCase(""))
                    branchCrdAcc = branchCrdAcc.trim();
                else
                    branchCrdAcc = "";

                String centerCardAcc=resultSet.getString("CENTER_CRD_ACC");
                if(centerCardAcc!=null && !centerCardAcc.trim().equalsIgnoreCase(""))
                    centerCardAcc=centerCardAcc.trim();
                else
                    centerCardAcc="";

                String eConflictAcc=resultSet.getString("E_CONFLICT_ACC");
                if(eConflictAcc!=null && !eConflictAcc.trim().equalsIgnoreCase(""))
                    eConflictAcc=eConflictAcc.trim();
                else
                    eConflictAcc="";

                String batchCrdAcc=resultSet.getString("BATCH_CRD_ACC");
                if(batchCrdAcc!=null && !batchCrdAcc.trim().equalsIgnoreCase(""))
                    batchCrdAcc=batchCrdAcc.trim();
                else
                    batchCrdAcc="";


                Branch branch = new Branch(branchCode, branchCrdAcc, centerCardAcc,eConflictAcc,resultSet.getString("ACCOUNT_NO").trim(),batchCrdAcc);

                if (resultSet.getString("STATUS").equals("0"))
                    throw new NotFoundException("Gift Card Accounts for BranchCode: " + branchCode + " is disabled");

                if (resultSet.getString("ISMODIFIED").equals("1"))
                    throw new NotFoundException("Gift Card Accounts for BranchCode: " + branchCode + " is disabled");

                giftAccountsMap.put(branchCode, branch);
                return branch;
            } else {
                throw new NotFoundException("Can not Find any TBBranch with branch_CODE = " + branchCode);
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getGiftCardAccounts() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static GiftCard getBranchDepositTx(String branchCode, String requestNo, String accountNo) throws SQLException, NotFoundException,ModelException {
        if (giftCardMap.containsKey(branchCode + requestNo))
            return (GiftCard) giftCardMap.get(branchCode + requestNo);

        String sql = "select BRANCH_DOCNO,NATIONAL_CODE,ISDONE from TBGIFTCARD where branch_CODE = '" + branchCode + "' and DEST_ACCOUNT_NO='" + accountNo + "' " +
                "and TX_CODE='" + TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD + "'  order by  TX_DATETIME desc fetch first 1 rows only with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                if (resultSet.getString("ISDONE").equals("1"))
                    throw new ModelException("gift deposit transaction is Reversed! " );
                GiftCard giftCard = new GiftCard(branchCode, resultSet.getString("NATIONAL_CODE"), resultSet.getString("BRANCH_DOCNO"));
                giftCardMap.put(branchCode + requestNo, giftCard);
                return giftCard;
            } else {
                throw new NotFoundException("Can not Find any gift deposit transaction for branch_CODE = " + branchCode);
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getBranchDepositTx() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static GiftCard getBatchDepositTx(String branchCode,String accountNo ) throws SQLException, NotFoundException {

        String sql = "select BRANCH_DOCNO,TX_DATE,AMOUNT from TBGIFTCARD where branch_CODE = '" + branchCode + "' and DEST_ACCOUNT_NO='" + accountNo + "'" +
                " and TX_CODE='" + TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD + "'  and ISDONE=0 and  REQUEST_NO=''  order by  TX_DATETIME  fetch first 1 rows only with ur";


        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                GiftCard giftCard = new GiftCard(branchCode, resultSet.getString("BRANCH_DOCNO"),resultSet.getString("TX_DATE"),resultSet.getString("AMOUNT"));
                return giftCard;
            } else {
                throw new NotFoundException("Can not Find any gift batch deposit transaction for branch_CODE = " + branchCode);
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getBatchDepositTx() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static long  getBatchDepositTx(String branchCode, String requestNo, String branchDocNo,String transDate,String accountNo) throws SQLException, NotFoundException, ModelException,ServerAuthenticationException {
        String sql = "select BRANCH_DOCNO,AMOUNT,ISDONE,REQUEST_NO from TBGIFTCARD where branch_CODE = '" + branchCode + "'  and DEST_ACCOUNT_NO='" + accountNo + "' " +
                "and TX_CODE='" + TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD + "' and  branch_DOCNO = '" + branchDocNo + "' and TX_DATE='"+transDate+"'  for update";

        Connection connection = null;
        Statement statement = null;
        Statement updatestatement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                if (resultSet.getString("ISDONE").equals("1"))
                    throw new ModelException("Batch gift deposit transaction is Reversed! ");
                if (resultSet.getString("REQUEST_NO").trim().equals("")) {
                    sql = "update TBGIFTCARD set REQUEST_NO='" + requestNo + "' where branch_CODE = '" + branchCode + "'  and DEST_ACCOUNT_NO='" + accountNo + "' " +
                            "  and TX_CODE='" + TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD + "' and  branch_DOCNO = '" + branchDocNo + "' and TX_DATE='"+transDate+"'" ;
                    updatestatement = connection.createStatement();
                    updatestatement.execute(sql);
                }else{
                    throw new ServerAuthenticationException("Batch gift deposit transaction is Duplicate! ");
                }
                connection.commit();
                return resultSet.getLong("AMOUNT");
            } else {
                throw new NotFoundException("Can not Find any batch gift deposit transaction for branch_CODE = " + branchCode);
            }
        } catch (NotFoundException e) {
            log.debug("CFSFacadeNew.getBatchDepositTx() =   -- Error1 :: " + e );
            connection.rollback();
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.getBatchDepositTx() =   -- Error2 :: " + e);
            connection.rollback();
            throw e;
        } catch (ServerAuthenticationException e) {
            log.debug("CFSFacadeNew.getBatchDepositTx() =   -- Error4 :: " + e);
            connection.rollback();
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getBatchDepositTx() =   -- Error3:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (updatestatement != null) updatestatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean ExistCardInDB(String serial) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select PAN from TBCFSCARD where pan = '" + serial + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String findCustomerID(String accountNo) throws SQLException {
        if (giftAccCustomerIdMap.containsKey(accountNo))
            return (String) giftAccCustomerIdMap.get(accountNo);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String customerId = "";

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "select customer_Id from tbcustomeraccounts where account_no ='" + accountNo + "' with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                customerId = resultSet.getString("CUSTOMER_ID").trim();
                giftAccCustomerIdMap.put(accountNo, customerId);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.findCustomerID  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return customerId;
    }

    private static void InsertCardAndCardAccountTransactional(Connection connection, String cardNo, String accountNo, String editDate, long amount, String customerId) throws SQLException {
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + customerId.trim() + "'," +
                "'" + Constants.CARD_SEQUENCE_NO_DEFAULT + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'" +
                ")";
        try {
            statement = connection.createStatement();
            statement.execute(insertcard_sql);

            InsertCardAccountTransactional(connection, cardNo, accountNo, editDate, amount);

        } catch (SQLException e) {
            log.error(e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }

    }

    private static void InsertCardAccountTransactional(Connection connection, String cardNo, String accountNo, String editDate, long amount) throws SQLException {
        Statement statement = null;

        String sql = "insert into TBCFSCARDACCOUNT(" +
                "PAN," +
                "ACCOUNT_TYPE," +
                "ACCOUNT_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_EDIT_DATE," +
                "STATUS," +
                "MAX_TRANS_LIMIT," +
                "SEQUENCE_NO" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + CFSConstants.GIFT_CARD_007 + "'," +
                "'" + accountNo + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'," +
                "" + Constants.CARD_STATUS_ACTIVE_FLAG + "," +
                amount + ",'"+Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID+"'" +
                ")";

        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.InsertCardAccountTransactional()--CMS-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static void saveGiftTxTransactional(Connection connection, Tx tx, String reqNo) throws SQLException {

        String sql = "Insert into tbGiftCard " +
                "(PARTNO,SESSION_ID,TX_CODE,TX_SRC,AMOUNT,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO," +
                "CARD_NO,BRANCH_CODE,TX_DATETIME,TX_DATE,TX_TIME,BRANCH_DOCNO,REQUEST_NO,NATIONAL_CODE" +
                ") values(" + tx.getPartNo() + ",'" + tx.getSessionId() + "','" + tx.getTxCode() + "','" + tx.getTxSrc() + "'," + tx.getAmount() +
                ",'" + tx.getSrcAccountNo() + "','" + tx.getDestAccountNo() + "','" + tx.getCardNo() + "','" + tx.getSgbBranchId() +
                "',current_timestamp,'" + tx.getTxOrigDate() + "','" + tx.getTxOrigTime() + "','" +
                tx.getBranchDocNo() + "','" + reqNo + "','" + tx.getDescription() + "')";

        if (log.isDebugEnabled()) log.debug("sql :: " + sql);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.saveGiftTxTransactional() = -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static GiftCard getGiftCardTx(String branchCode, String requestNo, String cardNo, String messageType) throws SQLException, NotFoundException {

        String sql = "select SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,BRANCH_DOCNO,ISDONE,TX_DATE,TX_TIME,AMOUNT from TBGIFTCARD where branch_CODE = '" + branchCode + "' and REQUEST_NO='" +
                requestNo + "' and CARD_NO='" + cardNo + "' " + "and TX_CODE='" + messageType + "'  with ur";

        Connection connection = null;

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                GiftCard giftCard = new GiftCard(branchCode, cardNo, resultSet.getString("SRC_ACCOUNT_NO"), resultSet.getString("DEST_ACCOUNT_NO"), resultSet.getString("BRANCH_DOCNO")
                        , resultSet.getString("ISDONE"), resultSet.getString("TX_DATE"), resultSet.getString("TX_TIME"), resultSet.getString("AMOUNT"));
                return giftCard;
            } else
                throw new NotFoundException("Can not Find any gift transaction for branch_CODE = " + branchCode);

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getGiftCardTx() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static GiftCard getGiftCardTx(String branchCode, String requestNo, String messageType) throws SQLException {

        String sql = "select  BRANCH_DOCNO,NATIONAL_CODE from TBGIFTCARD where branch_CODE = '" + branchCode + "' and REQUEST_NO='" +
                requestNo + "'  and TX_CODE='" + messageType + "'  with ur";

        Connection connection = null;

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                GiftCard giftCard = new GiftCard(branchCode, resultSet.getString("NATIONAL_CODE"), resultSet.getString("BRANCH_DOCNO"));
                return giftCard;
            } else
                return null;

        } catch (SQLException e) {
            log.error("CFSFacadeNew.getGiftCardTx() =   -- Error:: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Tx getOriginalTx4GiftCard(String destAccountNo, String date, String amount, String branchNo, String branchDocNo, String time, String cardNo) throws SQLException, NotFoundException {

        String hql = "select * from tbCFSTx where DEST_ACCOUNT_NO = '" + destAccountNo + "' and AMOUNT = " + amount +
                " and SGB_BRANCH_ID = '" + branchNo + "' and BRANCH_DOCNO = '" + branchDocNo +
                "' and IS_REVERSAL_TXN = '0' and TX_ORIG_DATE = '" + date + "' and TX_ORIG_TIME = '" + time + "' and CARD_NO = '" + cardNo + "'  with ur";

        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                Tx tx = new Tx();
                tx.setTxPk(resultSet.getString("TX_PK"));
                tx.setSessionId(resultSet.getString("SESSION_ID"));
                tx.setMessageId(resultSet.getString("TX_SEQ"));
                tx.setTxCode(resultSet.getString("TX_CODE"));
                tx.setTxSrc(resultSet.getString("TX_SRC"));
                tx.setCurrency(resultSet.getString("CURRENCY"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setCardSequenceNo(resultSet.getString("CARD_SEQUENCE_NO"));
                tx.setTxSequenceNumber(resultSet.getString("STAN"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setAcquirer(resultSet.getString("ACQUIRER"));
                tx.setSrcBranchId(resultSet.getString("SRC_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setCardNo(resultSet.getString("CARD_NO"));
                tx.setIsReversalTxn('0');
                tx.setOrigTxPk(resultSet.getString("ORIG_TX_PK"));
                tx.setTxDateTime(resultSet.getTimestamp("TX_DATETIME"));
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setBatchPk(resultSet.getLong("BATCH_PK"));
                tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                tx.setPartNo(resultSet.getInt("PARTNO"));
                tx.setIsReversed(resultSet.getInt("ISREVERSED"));
                tx.setIsCutovered(resultSet.getInt("ISCUTOVERED"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setDescription(resultSet.getString("desc"));
                tx.setRRN(resultSet.getString("RRN"));
                tx.setHostCode(resultSet.getString("HOSTCODE"));
                tx.setId1(resultSet.getString("ID1"));
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO"));
                tx.setMessageSeq(resultSet.getString("EBNK_MSG_SEQ"));


                return (tx);
            } else {
                throw new NotFoundException("Transaction branchDocNo = " + branchDocNo + " does not exist");
            }
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
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void setGiftTxIsReversedTransactional(Connection connection, Tx orig_tx) throws NotFoundException, SQLException {

        String sql = "update TBGIFTCARD set ISDONE = 1 where branch_CODE = '" + orig_tx.getSgbBranchId() + "' and REQUEST_NO='" +
                orig_tx.getMessageSeq() + "' and CARD_NO='" + orig_tx.getCardNo() + "' and TX_CODE='" + orig_tx.getTxCode() + "' ";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.setGiftTxIsReversedTransactional() -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static List getChargeTxList(String accountNo, String fromDate, String toDate) throws SQLException {
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,TX_ORIG_DATE, TX_ORIG_TIME, " +
                " IS_REVERSAL_TXN, AMOUNT,SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,CREATION_DATE,TERMINAL_ID,CREATION_TIME ,SGB_ACTION_CODE,SGB_BRANCH_ID " +
                " from tbCFSTx as tx ";
        hql = hql + " where ((tx.src_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.REVERSED + "') or " +
                " (tx.dest_Account_No = '" + accountNo + "' and IS_REVERSAL_TXN='" + Constants.NOT_REVERSED + "' ))";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        hql = hql + "and tx_src='" + Constants.BRANCH_SERVICE + "' order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first 150 rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000");
                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);
                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List getWfpTxList(String accountNo, String fromDate, String toDate) throws SQLException {
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,TX_ORIG_DATE, TX_ORIG_TIME, " +
                " IS_REVERSAL_TXN, AMOUNT,SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,CREATION_DATE,TERMINAL_ID,CREATION_TIME ,SGB_ACTION_CODE,SGB_BRANCH_ID " +
                " from tbCFSTx as tx ";
        hql = hql + " where tx.dest_Account_No = '" + accountNo + "' ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        hql = hql + "and tx_src='" + Constants.BRANCH_SERVICE + "' order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first 150 rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000");
                tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                    tx.setCRDB(Constants.DEBIT);
                else
                    tx.setCRDB(Constants.CREDIT);
                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String getLastChargeAmount(String accountNo, String cardNo) throws SQLException, NotFoundException {

        String hql = "select AMOUNT   from tbcharge  where  ACCOUNT_NO='" + accountNo + "'  ";
        if (cardNo != null && !cardNo.equals(""))
            hql = hql + "and CARD_NO='" + cardNo + "'";
        hql = hql + "  and  PROCCESS_STATUS='1' and Request_type='1' order by CREATION_DATE desc, CREATION_TIME desc  fetch first 1 rows only   with ur";

        String amount = "";
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next())
                amount = resultSet.getString("AMOUNT");
            else
                throw new NotFoundException("Charge Record not found! cardNo = " + cardNo);
            return amount;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void InsertChargeLogTransactional(Connection connection, String cardNo, String accountNo, long amount, String sessionId, String chargeType, String requestType, int batchPk) throws SQLException {

        String queryInsert = "insert into tbcharge" +
                "(partno, card_no, account_no, amount, session_id, charge_batch_pk, charge_filedate, charge_type, creation_date, creation_time, effective_date, effective_time, proccess_status, try_count, " +
                "first_error, second_error, action_code,CHARGE_FILENAME,request_type)" +
                " values (?,?,?,?,?,?,'',0,?,?,?,?,'1',1,'','','0000','',?)";

        PreparedStatement ps = null;

        try {
            String date = DateUtil.getSystemDate();
            String time = DateUtil.getSystemTime();

            ps = connection.prepareStatement(queryInsert);
            ps.setInt(1, getPartNo());
            ps.setString(2, cardNo);
            ps.setString(3, accountNo);
            ps.setLong(4, amount);
            ps.setString(5, sessionId);
            ps.setInt(6, batchPk);
            ps.setString(7, date);
            ps.setString(8, time);
            ps.setString(9, date);
            ps.setString(10, time);
            ps.setString(11, requestType);

            ps.executeUpdate();


        } catch (SQLException e) {
            log.error("SQL Exception during InsertChargeLogTransactional " + e);
            throw e;
        } finally {
            if (ps != null) ps.close();
        }
    }

    private static void decreaseBlockedAmount(Connection connection, String accountNo, Long amount) throws SQLException, NotFoundException {
        long subsidyAmount = 0;
        PreparedStatement ps = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select SUBSIDY_AMOUNT from tbcustomeraccounts where  ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) subsidyAmount = resultSet.getLong("SUBSIDY_AMOUNT");
            else {
                log.error("decreaseBlockedAmount() -- #1 Error ::  ");
                throw new NotFoundException("Not found Account = " + accountNo);
            }

            if (subsidyAmount - amount < 0)
                throw new ModelException("Not Sufficient Funds for account = '" + accountNo + "'");

            String decreaseBlockedAmount = "update tbcustomeraccounts set SUBSIDY_AMOUNT = SUBSIDY_AMOUNT - ?  where account_no = ? ";

            ps = connection.prepareStatement(decreaseBlockedAmount);
            ps.setLong(1, amount);
            ps.setString(2, accountNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.decreaseBlockedAmount : " + accountNo + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (ps != null) ps.close();

        }

    }

    public static long updateToZeroCardAccountTransactional(Connection connection, String accountNo, String cardNo) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select Max_Trans_Limit from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                maxTransLimit = resultSet.getLong("Max_Trans_Limit");
            if (maxTransLimit > 0) {
                sql = "update tbCFSCardAccount set max_trans_limit = 0 where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'";
                statement = connection.createStatement();
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();

        }
        return maxTransLimit;

    }

    public static long updateToZeroCardAccountTransactional(Connection connection, String accountNo) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select sum(Max_Trans_Limit) as totalAmount from tbCFSCardAccount where ACCOUNT_NO = '" + accountNo + "'  and series>1 and status=1";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                maxTransLimit = resultSet.getLong("totalAmount");
            if (maxTransLimit > 0) {
                sql = "update tbCFSCardAccount set max_trans_limit = 0 where ACCOUNT_NO = '" + accountNo + "'  and series>1 and status=1 ";
                statement = connection.createStatement();
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();

        }
        return maxTransLimit;

    }

    public static void txnDoDChargeTransaction4AllCards(Tx tx, HashMap extraData)
            throws NotFoundException, ModelException, ISOException, SQLException {
        long totalAmount = 0;
        int updateCount = 0;
        String accountNo = tx.getSrcAccountNo();
        long amount = tx.getAmount();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String chargeType = (String) extraData.get(Fields.DCHARGE_TYPE);
            if (chargeType.equalsIgnoreCase(Constants.DCHARGE_AMOUNT) || chargeType.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE)) {
                updateCount = updateCardAccountTransactional(connection, accountNo, -amount);
                totalAmount = updateCount * amount;
            } else {
                totalAmount = updateToZeroCardAccountTransactional(connection, accountNo);
            }

            tx.setLastTransLimit(totalAmount);
            if (totalAmount > 0) {
                decreaseBlockedAmount(connection, accountNo, totalAmount);
                insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), accountNo, "", String.valueOf(totalAmount),
                        CFSConstants.GROUP_CARD, CFSConstants.UNBLOCK, tx.getTxSequenceNumber(), tx.getRRN(), String.valueOf(tx.getFeeAmount()));

                InsertChargeLogTransactional(connection, "", accountNo, totalAmount, tx.getSessionId(), chargeType, Constants.DCHARGE, Constants.DCHARGE_BATCHPK);
            }
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #5=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #6=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #7=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #8=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction4AllCards() #9=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void txnDoDChargeTransaction(Tx tx, HashMap extraData)
            throws NotFoundException, ModelException, ISOException, SQLException {
        long maxTransLimit;
        String accountNo = tx.getSrcAccountNo();
        long amount = tx.getAmount();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String chargeType = (String) extraData.get(Fields.DCHARGE_TYPE);
            if (chargeType.equalsIgnoreCase(Constants.DCHARGE_AMOUNT) || chargeType.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE)){
                maxTransLimit = updateCardAccountTransactional(connection, accountNo, tx.getCardNo(), -amount, 0);
                tx.setSrcCardBalance(maxTransLimit-amount);
            }else {
                maxTransLimit = updateToZeroCardAccountTransactional(connection, accountNo, tx.getCardNo());
                amount = maxTransLimit;
                tx.setSrcCardBalance(maxTransLimit);
            }

            tx.setLastTransLimit(maxTransLimit);
            if (amount > 0) {
                decreaseBlockedAmount(connection, accountNo, amount);
                insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), accountNo, tx.getCardNo(), String.valueOf(amount),
                        CFSConstants.GROUP_CARD, CFSConstants.UNBLOCK, tx.getTxSequenceNumber(), tx.getRRN(), String.valueOf(tx.getFeeAmount()));

                InsertChargeLogTransactional(connection, tx.getCardNo(), accountNo, amount, tx.getSessionId(), chargeType, Constants.DCHARGE, Constants.DCHARGE_BATCHPK);
            }
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #5=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoDChargeTransaction() #6=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #7=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction() #8=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #9=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static List getGroupCardList(String accountNo) throws SQLException {

        String hql = "select ACCOUNT_TYPE,MAX_TRANS_LIMIT,PAN " +
                "from tbCFSCardAccount where ACCOUNT_NO = '" + accountNo + "' and series>1 and status=1 with ur";

        List cards = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                CardAccount cardAccount = new CardAccount();
                cardAccount.setPan(resultSet.getString("PAN"));
                cardAccount.setAccountType(resultSet.getString("ACCOUNT_TYPE"));
                cardAccount.setMaxTransLimit(resultSet.getLong("MAX_TRANS_LIMIT"));
                cards.add(cardAccount);
            }
            connection.commit();
            return cards;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void txnDoImmedaieChargeTransaction(Tx tx) throws NotFoundException, ModelException, ISOException, SQLException {
        long maxTransLimit;
        String accountNo = tx.getSrcAccountNo();
        long amount = tx.getAmount();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
                maxTransLimit = updateCardAccountTransactional(connection, accountNo, tx.getCardNo(), amount, 0);

            tx.setLastTransLimit(maxTransLimit);
            tx.setDestCardBalance(maxTransLimit+amount);

            increaseBlockAmount(connection, accountNo, amount);
                insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), accountNo, tx.getCardNo(), String.valueOf(amount),
                        CFSConstants.GROUP_CARD, CFSConstants.BLOCK, tx.getTxSequenceNumber(), tx.getRRN(), String.valueOf(tx.getFeeAmount()));

                InsertChargeLogTransactional(connection, tx.getCardNo(), accountNo, amount, tx.getSessionId(), "", Constants.CHARGE, Constants.DCHARGE_BATCHPK);
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #5=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoDChargeTransaction() #6=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #7=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoDChargeTransaction() #8=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDChargeTransaction() #9=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    private static void increaseBlockAmount(Connection connection, String accountNo, Long amount) throws SQLException, NotFoundException {
        long subsidyAmount = 0;
        long balance = 0;
        Statement updateStatment = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select BALANCE,SUBSIDY_AMOUNT from tbcustomeraccounts where  ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                subsidyAmount = resultSet.getLong("SUBSIDY_AMOUNT");
                balance = resultSet.getLong("BALANCE");

            }else {
                log.error("increaseBlockAmount() -- #1 Error ::  ");
                throw new NotFoundException("Not found Account = " + accountNo);
            }

            if (balance-subsidyAmount < amount )
                throw new ModelException("Not Sufficient Funds for account = '" + accountNo + "'");

            long newSubsidyAmount = subsidyAmount + amount;

            String increaseBlockAmount = "update tbcustomeraccounts set SUBSIDY_AMOUNT = "+newSubsidyAmount+"  where account_no ='"+accountNo+"'";

            updateStatment = connection.createStatement();
            updateStatment.executeUpdate(increaseBlockAmount);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.increaseBlockAmount : " + accountNo + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (updateStatment != null) updateStatment.close();

        }

    }

    //**********************CMS************end***********************
    //**********************TOURIST************start***********************
    public static List<String> ExistTouristCardInDB(String serial) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> cardInfo=new ArrayList<String>();

        String sql = "select account_no,MAX_TRANS_LIMIT,STATUS from TBCFSCARDaccount where pan = '" + serial + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()){
                cardInfo.add(0,resultSet.getString("account_no"));
                cardInfo.add(1, String.valueOf(resultSet.getLong("MAX_TRANS_LIMIT")));
                cardInfo.add(2,resultSet.getString("STATUS"));
            }
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return cardInfo;
    }

    public static void txnDoTransaction4TouristCard(Tx tx, HashMap extraData, long minBalance)
            throws NotFoundException, ModelException, ISOException, SQLException {


        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        long amount = tx.getAmount();

        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_TOURIST_CHARGE)) {
                long srcBalance = 0;
                srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, -tx.getFeeAmount(), extraData);
                tx.setSrc_account_balance(srcBalance);

                long destBalance = 0;
                destBalance = updateAccountTransactional4Tourist(connection, destAccountNo, amount, minBalance);
                tx.setDest_account_balance(destBalance);

                long lastTransLimit=0;
                lastTransLimit=updateCardAccountTransactional(connection, destAccountNo, tx.getCardNo(), amount, destBalance);
                tx.setLastTransLimit(lastTransLimit);

                insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), destAccountNo, tx.getCardNo(), String.valueOf(amount),
                        CFSConstants.TOURIST_SERVICE, CFSConstants.BLOCK, tx.getBranchDocNo(), tx.getRRN(), String.valueOf(tx.getFeeAmount()),
                        tx.getDeviceCode(), tx.getTxOrigDate(), tx.getTxOrigTime(), tx.getIsReversalTxn(), tx.getIsReversed(), tx.getTxPk(), tx.getOrigTxPk());

                insertTouristLog(connection,tx,tx.getDestAccountNo(),CFSConstants.CHARGE_TRANSACTION);
                tx.setTotalDestAccNo(tx.getCardNo());
                tx.setCardNo(CFSConstants.BRANCH_ACQUIRE);
                saveTxTransactional(connection, tx);
            } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_TOURIST_DISCHARGE)) {

                long maxTransLimit = updateCardAccountTransactional(connection, srcAccountNo, tx.getCardNo(), -amount, 0);
                tx.setLastTransLimit(maxTransLimit);

                long srcBalance = 0;
                srcBalance = updateAccountTransactional4Tourist(connection, srcAccountNo, -amount, minBalance);
                tx.setSrc_account_balance(srcBalance);

                long destBalance = 0;
                destBalance = updateDestAccountTourist(connection, destAccountNo, amount);
                tx.setDest_account_balance(destBalance);

                insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), srcAccountNo, tx.getCardNo(), String.valueOf(amount),
                        CFSConstants.TOURIST_SERVICE, CFSConstants.UNBLOCK, tx.getBranchDocNo(), tx.getRRN(), String.valueOf(tx.getFeeAmount()),
                        tx.getDeviceCode(), tx.getTxOrigDate(), tx.getTxOrigTime(), tx.getIsReversalTxn(), tx.getIsReversed(), tx.getTxPk(), tx.getOrigTxPk());

                saveTxTransactional(connection, tx);
                insertTouristLog(connection,tx,tx.getSrcAccountNo(),CFSConstants.DCHARGE_TRANSACTION);

            } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_TOURIST_CARD_REVOKE)) {

                long maxTransLimit = updateCardAccountTransactional(connection, srcAccountNo, tx.getCardNo());
                tx.setLastTransLimit(maxTransLimit);
                tx.setAmount(maxTransLimit);
                amount = maxTransLimit;

                if (maxTransLimit > 0) {

                    long srcBalance = 0;
                    srcBalance = updateAccountTransactional4Tourist(connection, srcAccountNo, -amount, minBalance);
                    tx.setSrc_account_balance(srcBalance);

                    long destBalance = 0;
                    destBalance = updateDestAccountTourist(connection, destAccountNo, amount);
                    tx.setDest_account_balance(destBalance);

                    insertBLCKLog(connection, tx.getSessionId(), tx.getTxCode(), srcAccountNo, tx.getCardNo(), String.valueOf(amount),
                            CFSConstants.TOURIST_SERVICE, CFSConstants.UNBLOCK, tx.getBranchDocNo(), tx.getRRN(), String.valueOf(tx.getFeeAmount()),
                            tx.getDeviceCode(), tx.getTxOrigDate(), tx.getTxOrigTime(), tx.getIsReversalTxn(), tx.getIsReversed(), tx.getTxPk(), tx.getOrigTxPk());

                    saveTxTransactional(connection, tx);
                }
            } else if (tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_TOURIST_FUND_TRANSFER)) {
                long srcBalance = 0;
                srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, -tx.getFeeAmount(), extraData);
                tx.setSrc_account_balance(srcBalance);

                long destBalance = 0;
                destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, amount, tx.getTxCode(), 0);
                tx.setDest_account_balance(destBalance);

                saveTxTransactional(connection, tx);
            }

            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoTransaction4TouristCard() #4=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4TouristCard() #5=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoTransaction4TouristCard() #6=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4TouristCard() #7=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoTransaction4TouristCard() #8=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoTransaction4TouristCard() #9=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long updateAccountTransactional4Tourist(Connection connection, String accountNo, long amount,long minBalance) throws ModelException, SQLException, NotFoundException {
        String sql = "SELECT balance , SUBSIDY_AMOUNT from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;

        try {
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");

                if(amount<0){
                    if (balance + amount < minBalance)
                        throw new ModelException("Insufficient fund");
                    if (subsidyAmount + amount < 0)
                        throw new ModelException("Insufficient fund");
                }

                long new_balance = balance + amount;
                long new_subsidyAmount = subsidyAmount + amount;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance + ", SUBSIDY_AMOUNT = " + new_subsidyAmount + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);

                return new_balance;
            }
            throw new NotFoundException("Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional4Tourist() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional4Tourist() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional4Tourist() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional4Tourist() #4= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static long updateCardAccountTransactional(Connection connection, String accountNo, String cardNo) throws SQLException, ModelException {
        long maxTransLimit = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select Max_Trans_Limit from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                maxTransLimit = resultSet.getLong("Max_Trans_Limit");
        } catch (SQLException e) {
            log.error("sql = " + sql);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        sql = "update tbCFSCardAccount set max_trans_limit = 0 , status= 0 where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'";
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
            statement1.execute(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
        }

        return maxTransLimit;

    }

    public static List getCardStatement(String accountNo,String cardNo, String fromDate, String toDate, String fromTime, String toTime,
                                        int transCount,String fromSequence) throws SQLException {
        String brchDocNo;
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, " +
                " SGB_ACTION_CODE, SGB_BRANCH_ID, IS_REVERSAL_TXN, AMOUNT, FEE_AMOUNT, SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,log_id,CREATION_DATE,EXTRA_INFO,TERMINAL_ID,TX_SRC,HOSTCODE,CREATION_TIME,EBNK_MSG_SEQ,ID1,ID2,LAST_TRANS_LIMIT " +
                " from tbCFSTx as tx ";
        hql = hql + " where (tx.CARD_No = '" + cardNo + "' or tx.TOTAL_DEST_ACCOUNT = '" + cardNo + "' )";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+fromDate + "' and '"+ toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " order by tx.log_ID desc fetch first " + transCount + " rows only   with ur";
        } else
            hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setRRN(resultSet.getString("log_id"));
                tx.setDescription(null != resultSet.getString("EXTRA_INFO") ? resultSet.getString("EXTRA_INFO").trim() : " ");
                tx.setHostCode(null != resultSet.getString("HOSTCODE") ? resultSet.getString("HOSTCODE") : "  ");
                if (resultSet.getString("TX_SRC").trim().equalsIgnoreCase(Fields.SERVICE_NASIM))
                    tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                else
                    tx.setMerchantTerminalId("00");
                brchDocNo = resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000";
                tx.setBranchDocNo(brchDocNo);
                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("LAST_TRANS_LIMIT"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("LAST_TRANS_LIMIT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                tx.setMessageSeq(null != resultSet.getString("EBNK_MSG_SEQ") ? resultSet.getString("EBNK_MSG_SEQ").trim() : " ");
                tx.setId1(null != resultSet.getString("ID1") ? resultSet.getString("ID1").trim() : " ");
                tx.setId2(null != resultSet.getString("ID2") ? resultSet.getString("ID2").trim() : " ");

                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List getChargeStatement(String accountNo,String cardNo, String fromDate, String toDate, String fromTime, String toTime,
                                          int transCount,String fromSequence) throws SQLException {
        String brchDocNo;
        String hql = "select ACCOUNT_NO, CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, IS_REVERSAL_TXN, AMOUNT, FEE_AMOUNT, STAN, " +
                "log_id,CREATION_DATE,CREATION_TIME,BLOCK_TYPE " +
                " from TB_BLCK_Log as tx ";
        hql = hql + " where (tx.CARD_No = '" + cardNo + "' and tx.ACCOUNT_NO = '" + accountNo + "' and SERVICE= '"+CFSConstants.TOURIST_SERVICE+"')";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+fromDate + "' and '"+toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " order by tx.log_ID desc fetch first " + transCount + " rows only   with ur";
        } else
            hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(accountNo);
                tx.setDestAccountNo(accountNo);
                tx.setRRN(resultSet.getString("log_id"));

                brchDocNo = resultSet.getString("STAN") != null &&
                        !"".equals(resultSet.getString("STAN").trim()) ?
                        resultSet.getString("STAN").trim() : "000000";
                tx.setBranchDocNo(brchDocNo);

                if (resultSet.getString("BLOCK_TYPE").trim().equalsIgnoreCase(String.valueOf(CFSConstants.UNBLOCK))) {
                    tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("BLOCK_TYPE").trim().equalsIgnoreCase(String.valueOf(CFSConstants.BLOCK))) {
                    tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long updateDestAccountTourist(Connection connection, String accountNo, long amount) throws ModelException, SQLException, NotFoundException {
        String sql = "SELECT balance from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;

        try {
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");

                long new_balance = balance + amount ;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);

                return new_balance;
            }
            throw new NotFoundException("Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional() #4= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static List getTouristStatement(String accountNo, String fromDate, String toDate, String fromTime, String toTime,
                                           int transCount, String fromSequence) throws SQLException {
        String brchDocNo;
        String hql = "select SRC_ACCOUNT_NO, DEST_ACCOUNT_NO,CARD_NO, TX_ORIG_DATE, TX_ORIG_TIME, " +
                " SGB_ACTION_CODE, SGB_BRANCH_ID, IS_REVERSAL_TXN, AMOUNT, FEE_AMOUNT, SRC_ACC_BALANCE,  BRANCH_DOCNO, " +
                "DEST_ACC_BALANCE,log_id,CREATION_DATE,EXTRA_INFO,TERMINAL_ID,TX_SRC,HOSTCODE,CREATION_TIME,EBNK_MSG_SEQ,ID1,ID2 " +
                " from tbCFSTx as tx ";
        hql = hql + " where (tx.src_Account_No = '" + accountNo + "' or tx.dest_Account_No = '" + accountNo + "' )";
        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  tx.CREATION_DATE  between '"+ fromDate + "' and '"+ toDate + "'";
        if (fromTime != null && !"".equals(fromTime))
            hql = hql + " and tx.CREATION_TIME >='" + fromTime + "'";
        if (toTime != null && !"".equals(toTime))
            hql = hql + " and tx.CREATION_TIME <='" + toTime + "'";
        if (fromSequence != null && !fromSequence.equals("")) {
            hql = hql + " and tx.log_ID >  " + fromSequence;
            hql = hql + " order by tx.log_ID desc fetch first " + transCount + " rows only   with ur";
        } else
            hql = hql + " order by tx.CREATION_DATE desc, tx.CREATION_TIME desc  fetch first " + transCount + " rows only   with ur";
        List txs = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Tx tx = new Tx();
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTxn('0');
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setIsReversed(resultSet.getInt("IS_REVERSAL_TXN"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setRRN(resultSet.getString("log_id"));
                tx.setDescription(null != resultSet.getString("EXTRA_INFO") ? resultSet.getString("EXTRA_INFO").trim() : " ");
                tx.setHostCode(null != resultSet.getString("HOSTCODE") ? resultSet.getString("HOSTCODE") : "  ");
                if (resultSet.getString("TX_SRC").trim().equalsIgnoreCase(Fields.SERVICE_NASIM))
                    tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID").trim());
                else
                    tx.setMerchantTerminalId("00");
                brchDocNo = resultSet.getString("BRANCH_DOCNO") != null &&
                        !"".equals(resultSet.getString("BRANCH_DOCNO").trim()) ?
                        resultSet.getString("BRANCH_DOCNO").trim() : "000000";
                tx.setBranchDocNo(brchDocNo);
                if (resultSet.getString("SRC_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                    tx.setAmount(resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.CREDIT);
                    else
                        tx.setCRDB(Constants.DEBIT);
                } else if (resultSet.getString("DEST_ACCOUNT_NO").trim().equals(accountNo)) {
                    tx.setSrc_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                    if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED))
                        tx.setCRDB(Constants.DEBIT);
                    else
                        tx.setCRDB(Constants.CREDIT);
                } else
                    tx.setCRDB(Constants.CREDITDEBIT);

                tx.setMessageSeq(null != resultSet.getString("EBNK_MSG_SEQ") ? resultSet.getString("EBNK_MSG_SEQ").trim() : " ");
                tx.setId1(null != resultSet.getString("ID1") ? resultSet.getString("ID1").trim() : " ");
                tx.setId2(null != resultSet.getString("ID2") ? resultSet.getString("ID2").trim() : " ");

                txs.add(tx);
            }
            connection.commit();
            return txs;
        } catch (SQLException e) {
            log.error("e = " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static void insertTouristLog(Connection connection,Tx tx,String accountNo,String txType ) throws SQLException {


        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "insert into TB_TOURIST_LOG " +
                    "(SESSION_ID, TX_CODE, PARTNO, HANDLED, STAN, TX_DATE, TX_TIME , TERMINAL_TYPE, RRN, AMOUNT, PAN, ACCOUNT_NO, OPCODE, ISREVERSED, ACTIONCODE, INSERTDATETIME, TERMINAL_ID, TX_TYPE, CREATION_DATE, CREATION_TIME ) VALUES  ('" +
                    tx.getSessionId()+ "', '" + tx.getTxCode() + "', " + getPartNo() + ", 0, '"+tx.getBranchDocNo()+"', '" + tx.getTxOrigDate() + "', '" + tx.getTxOrigTime() + "', '', '" +
                    tx.getRRN() + "', " + tx.getAmount() + ", '" + tx.getCardNo() + "', '" + accountNo + "', '" + tx.getSgbActionCode() + "',  " + tx.getIsReversed() + ", '', CURRENT_TIMESTAMP, '', '" + txType + "', '" + tx.getCreationDate() + "', '" + tx.getCreationTime() + "')";


            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertTouristLog= -- Error :: " + e);
            if (statement != null)
                statement.close();
            throw e;
        }
    }

    //**********************TOURIST************end***********************
    //**********************AMX************started***********************
    public static int inquiryRevokeAccount(String accountNo, String branchId, String nationalCode, String externalIdNumber) throws SQLException {
        Connection connection = null;
        Statement statementHistory = null;
        ResultSet historyRS = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement srvStatement = null;
        ResultSet srvRS = null;
        Statement cardStatement = null;
        ResultSet cardRS = null;
        Statement statementCard = null;
        ResultSet resultSetCard = null;
        Statement statementCustAcc = null;
        ResultSet resultSetCustAcc = null;
        String customerId = "";
        connection = dbConnectionPool.getConnection();
        try {

            String hql = "select ACCOUNT_TITLE,SUB_TITLE,CUSTOMER_ID from tbcustacc where  account_no= '" + accountNo + "' and NATIONAL_CODE='" + nationalCode.trim() +
                    "' and EXTERNAL_ID_NUMBER='" + externalIdNumber + "' and status='" + Constants.ACTIVE_ROW_STATUS + "' for update";
            statementCustAcc=connection.createStatement();
            resultSetCustAcc = statementCustAcc.executeQuery(hql);

            if (resultSetCustAcc.next()) {
                customerId = resultSetCustAcc.getString("CUSTOMER_ID").trim();

                if (resultSetCustAcc.getString("ACCOUNT_TITLE").equalsIgnoreCase("3") && !resultSetCustAcc.getString("SUB_TITLE").equalsIgnoreCase("0")) {
                    // remove row
                    hql = "SELECT tbcfscardaccount.PAN" +
                            "  FROM tbcfscardaccount INNER JOIN tbcfscard" +
                            "  ON tbcfscardaccount.pan = tbcfscard.pan" +
                            "  where tbcfscard.customer_id='" + customerId + "' and status=1 with ur";
                    statementCard=connection.createStatement();
                    resultSetCard = statementCard.executeQuery(hql);
                    if (resultSetCard.next()) {
                        connection.rollback();
                        return 4;
                    }

                } else {
                    //remove another
                    String historySql = "SELECT ACCOUNT_NO from tbaccounthistory where ACCOUNT_NO = '" + accountNo + "' with ur";
                    statementHistory = connection.createStatement();
                    historyRS = statementHistory.executeQuery(historySql);
                    if (historyRS.next()) {
//                    "revoke account before"
                        connection.rollback();
                        return 1;
                    }


                    String accountSql = "SELECT ACCOUNT_TITLE,STATUS,SGB_BRANCH_ID,balance, SUBSIDY_AMOUNT " +
                            "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' and LOCK_STATUS<>9 for update";
                    statement = connection.createStatement();
                    accRS = statement.executeQuery(accountSql);
                    if (accRS.next()) {
                        if (accRS.getLong("BALANCE") != 0) {
                            //"account has balance"
                            connection.rollback();
                            return 2;
                        }
                        if (accRS.getLong("SUBSIDY_AMOUNT") != 0) {
                            //"account has block  amount"
                            connection.rollback();
                            return 8;
                        }
                        if (accRS.getLong("STATUS") != 1) {
                            //"account is blocked"
                            connection.rollback();
                            return 9;
                        }

                        String sgbBranchId = accRS.getString("SGB_BRANCH_ID");
                        if (!sgbBranchId.equalsIgnoreCase(branchId)) {
                            connection.rollback();
                            return 3;
                        }

                        String cardSql = "SELECT pan from tbCfscardAccount where ACCOUNT_NO = '" + accountNo + "' and status=1 with ur";
                        cardStatement = connection.createStatement();
                        cardRS = cardStatement.executeQuery(cardSql);
                        if (cardRS.next()) {
                            //ACCOUNT ASSIGNED TO CARD
                            connection.rollback();
                            return 4;
                        }

                        String srvSql = "SELECT E_STATUS,SERVICE_STATUS,STATUSD from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' for update";
                        srvStatement = connection.createStatement();
                        srvRS = srvStatement.executeQuery(srvSql);
                        if (srvRS.next()) {
                            if (!srvRS.getString("E_STATUS").equals(Constants.E_STATUS_ACTIVE)) {
                                connection.rollback();
                                return 5;
                            }
                            if (!srvRS.getString("SERVICE_STATUS").equals(Constants.SERVICE_STATUS_ACTIVE)) {
                                connection.rollback();
                                return 11;
                            }
                            if (srvRS.getString("STATUSD").startsWith(Constants.CM_BLOCK) || srvRS.getString("STATUSD").endsWith(Constants.CM_BLOCK)) {
                                connection.rollback();
                                return 12;
                            }
                            connection.commit();
                        } else {
                            connection.rollback();
                            return 6;
                        }
                    } else {
                        connection.rollback();
                        return 7;
                    }
                }
            } else {
                connection.rollback();
                return 10;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.inquiryRevokeAccount()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.inquiryRevokeAccount()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (historyRS != null) historyRS.close();
                if (statementHistory != null) statementHistory.close();
                if (accRS != null) accRS.close();
                if (statement != null) statement.close();
                if (srvRS != null) srvRS.close();
                if (srvStatement != null) srvStatement.close();
                if (cardRS != null) cardRS.close();
                if (cardStatement != null) cardStatement.close();
                if (resultSetCard != null) resultSetCard.close();
                if (resultSetCustAcc != null) resultSetCustAcc.close();
                if (statementCard != null) statementCard.close();
                if (statementCustAcc != null) statementCustAcc.close();

            } catch (SQLException e1) {
                log.error("CFSFacadeNew.inquiryRevokeAccount() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static int conditionalRevokeAccount(String accountNo, String branchId, String nationalCode, String externalIdNumber,String session_id,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statementHistory = null;
        ResultSet historyRS = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement srvStatement = null;
        ResultSet srvRS = null;
        Statement cardStatement = null;
        ResultSet cardRS = null;
        Statement statementCard = null;
        ResultSet resultSetCard = null;
        Statement statementCustAcc = null;
        ResultSet resultSetCustAcc = null;
        Statement statementDelCustAcc = null;
        Statement custaccStatement = null;
        Statement deleteAccST = null;
        Statement deleteSrvST = null;
        Statement insStm = null;
        Statement nacLogStatment = null;


        String customerId = "";
        String accountTitle="";
        String subTitle="";
        connection = dbConnectionPool.getConnection();
        try {

            String hql = "select ACCOUNT_TITLE,SUB_TITLE,CUSTOMER_ID from tbcustacc where  account_no= '" + accountNo + "' and NATIONAL_CODE='" + nationalCode.trim() +
                    "' and EXTERNAL_ID_NUMBER='" + externalIdNumber + "' and status='" + Constants.ACTIVE_ROW_STATUS + "' for update";
            statementCustAcc=connection.createStatement();
            resultSetCustAcc = statementCustAcc.executeQuery(hql);

            if (resultSetCustAcc.next()) {
                customerId = resultSetCustAcc.getString("CUSTOMER_ID").trim();
                accountTitle=resultSetCustAcc.getString("ACCOUNT_TITLE").trim();
                subTitle=resultSetCustAcc.getString("SUB_TITLE").trim();

                if (resultSetCustAcc.getString("ACCOUNT_TITLE").equalsIgnoreCase("3") && !resultSetCustAcc.getString("SUB_TITLE").equalsIgnoreCase("0")) {
                    // remove row
                    hql = "SELECT tbcfscardaccount.PAN" +
                            "  FROM tbcfscardaccount INNER JOIN tbcfscard" +
                            "  ON tbcfscardaccount.pan = tbcfscard.pan" +
                            "  where tbcfscard.customer_id='" + customerId + "' and status=1 with ur";
                    statementCard=connection.createStatement();
                    resultSetCard = statementCard.executeQuery(hql);
                    if (resultSetCard.next()) {
                        connection.rollback();
                        return 4;
                    }
                    hql = "update tbcustacc set status='" + Constants.REMOVE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and NATIONAL_CODE='" + nationalCode.trim() +
                            "' and EXTERNAL_ID_NUMBER='" + externalIdNumber + "' and status='" + Constants.ACTIVE_ROW_STATUS + "'";
                    statementDelCustAcc=connection.createStatement();
                    statementDelCustAcc.execute(hql);


                    String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                            "SESSION_ID," +
                            "CHANNEL_TYPE," +
                            "PARTNO," +
                            "INSERT_DATETIME," +
                            "ACCOUNT_NO," +
                            "NATIONAL_CODE," +
                            "EXTERNAL_ID_NUMBER," +
                            "OPERATION_TYPE,"+
                            "ACCOUNT_TITLE," +
                            "SUB_TITLE," +
                            "HANDLED,"+
                            "BRANCH_ID"+
                            ") values(" +
                            "'" + session_id + "'," +
                            "'" + channel_type + "'," +
                            getPartNo() + "," +
                            "current_timestamp, " +
                            "'" + accountNo + "'," +
                            "'" + nationalCode.trim() + "'," +
                            "'" + externalIdNumber + "'," +
                            "'" + Constants.REMOVE_ROW + "'," +
                            "'" + accountTitle + "'," +
                            "'" + subTitle + "'," +
                            1 +","+
                            "'" + branchId + "'" +
                            " )";
                    nacLogStatment = connection.createStatement();
                    nacLogStatment.execute(insertNaclog_sql);
                    nacLogStatment.close();
                    connection.commit();

                } else {
                    //remove another
                    String historySql = "SELECT ACCOUNT_NO from tbaccounthistory where ACCOUNT_NO = '" + accountNo + "' with ur";
                    statementHistory = connection.createStatement();
                    historyRS = statementHistory.executeQuery(historySql);
                    if (historyRS.next()) {
//                    "revoke account before"
                        connection.rollback();
                        return 1;
                    }


                    String accountSql = "SELECT CUSTOMER_ID,ACCOUNT_TYPE,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS," +
                            " CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE,SPARROW_BRANCH_ID,ORIG_EDIT_DATE," +
                            " SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,balance, SUBSIDY_AMOUNT " +
                            " from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' and LOCK_STATUS<>9 for update";
                    statement = connection.createStatement();
                    accRS = statement.executeQuery(accountSql);
                    if (accRS.next()) {
                        if (accRS.getLong("BALANCE") != 0) {
                            //"account has balance"
                            connection.rollback();
                            return 2;
                        }
                        if (accRS.getLong("SUBSIDY_AMOUNT") != 0) {
                            //"account has block  amount"
                            connection.rollback();
                            return 8;
                        }
                        if (accRS.getLong("STATUS") != 1) {
                            //"account is blocked"
                            connection.rollback();
                            return 9;
                        }

                        String sgbBranchId = accRS.getString("SGB_BRANCH_ID");
                        if (!sgbBranchId.equalsIgnoreCase(branchId)) {
                            connection.rollback();
                            return 3;
                        }

                        String cardSql = "SELECT pan from tbCfscardAccount where ACCOUNT_NO = '" + accountNo + "' and status=1 with ur";
                        cardStatement = connection.createStatement();
                        cardRS = cardStatement.executeQuery(cardSql);
                        if (cardRS.next()) {
                            //ACCOUNT ASSIGNED TO CARD
                            connection.rollback();
                            return 4;
                        }

                        String srvSql = "SELECT  CUSTOMER_ID,STATUS,CREATION_DATE,HOST_ID,TEMPLATE_ID,NATIONAL_CODE," +
                                " TMP_UPDATE_DATE,ACCOUNT_NATURE, SMSNOTIFICATION,STATUSMELLI,E_STATUS,SERVICE_STATUS,STATUSD  " +
                                " from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' for update";
                        srvStatement = connection.createStatement();
                        srvRS = srvStatement.executeQuery(srvSql);
                        if (srvRS.next()) {
                            if (!srvRS.getString("E_STATUS").equals(Constants.E_STATUS_ACTIVE)) {
                                connection.rollback();
                                return 5;
                            }
                            if (!srvRS.getString("SERVICE_STATUS").equals(Constants.SERVICE_STATUS_ACTIVE)) {
                                connection.rollback();
                                return 11;
                            }
                            if (srvRS.getString("STATUSD").startsWith(Constants.CM_BLOCK) || srvRS.getString("STATUSD").endsWith(Constants.CM_BLOCK)) {
                                connection.rollback();
                                return 12;
                            }
                            AccountHistory accountHistory = new AccountHistory(
                                    accRS.getString("CUSTOMER_ID"), accRS.getString("ACCOUNT_TYPE"), accountNo, accRS.getInt("LOCK_STATUS"), accRS.getString("CURRENCY"),
                                    accRS.getString("ACCOUNT_TITLE"), accRS.getInt("STATUS"), accRS.getString("CREATION_DATE"), accRS.getString("CREATION_TIME"),
                                    accRS.getString("ORIG_CREATE_DATE"), accRS.getString("SPARROW_BRANCH_ID"), accRS.getString("ORIG_EDIT_DATE"), accRS.getString("SGB_BRANCH_ID"),
                                    accRS.getString("NATURE"), accRS.getString("ACCOUNT_OPENER_NAME"), accRS.getInt("WITHDRAW_TYPE"), srvRS.getString("CUSTOMER_ID"), srvRS.getInt("STATUS"),
                                    srvRS.getString("CREATION_DATE"), srvRS.getInt("HOST_ID"), srvRS.getInt("TEMPLATE_ID"), srvRS.getString("NATIONAL_CODE"), srvRS.getString("TMP_UPDATE_DATE"),
                                    srvRS.getInt("ACCOUNT_NATURE"), srvRS.getString("SMSNOTIFICATION"), srvRS.getInt("STATUSMELLI"));


                            String insertSql = "insert into TBACCOUNTHISTORY " +
                                    "(CUSTOMER_ID,ACCOUNT_TYPE,ACCOUNT_NO,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS,CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE," +
                                    "SPARROW_BRANCH_ID,ORIG_EDIT_DATE,SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,CUSTOMER_ID_srv,STATUS_srv," +
                                    "CREATION_DATE_srv,HOST_ID_srv,TEMPLATE_ID,NATIONAL_CODE,TMP_UPDATE_DATE,ACCOUNT_NATURE,SMSNOTIFICATION,STATUSMELLI,CARD_STATUS) " +
                                    "values(";
                            if (accountHistory.getCustomerId() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCustomerId() + "', ";
                            if (accountHistory.getAccountType() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountType() + "', ";
                            insertSql = insertSql + "'" + accountNo + "', ";
                            insertSql = insertSql + accountHistory.getLockStatus() + ", ";
                            if (accountHistory.getCurrency() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCurrency() + "', ";
                            if (accountHistory.getAccountTitle() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountTitle() + "', ";
                            insertSql = insertSql + accountHistory.getAccountStatus() + ", ";
                            if (accountHistory.getCreationDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationDate() + "', ";
                            if (accountHistory.getCreationTime() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationTime() + "', ";

                            if (accountHistory.getOrigCreatDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getOrigCreatDate() + "', ";

                            if (accountHistory.getSparrow_branch_id() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSparrow_branch_id() + "', ";

                            if (accountHistory.getOrigEditDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getOrigEditDate() + "', ";
                            if (accountHistory.getSgb_branch_id() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSgb_branch_id() + "', ";

                            if (accountHistory.getNature() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getNature() + "', ";

                            if (accountHistory.getAccountOpenerName() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountOpenerName() + "', ";
                            insertSql = insertSql + accountHistory.getWithdrawType() + ", ";
                            if (accountHistory.getCustomerIdSrv() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCustomerIdSrv() + "', ";

                            insertSql = insertSql + "'" + accountHistory.getStatusSrv() + "', ";

                            if (accountHistory.getCreationDateSrv() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationDateSrv() + "', ";

                            insertSql = insertSql + "'" + accountHistory.getHostIDSrv() + "', ";

                            insertSql = insertSql + accountHistory.getTemplateIdSrv() + ", ";

                            if (accountHistory.getNationalCode() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getNationalCode() + "', ";
                            if (accountHistory.getTmpUpdateDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getTmpUpdateDate() + "', ";

                            insertSql = insertSql + accountHistory.getAccountNature() + ", ";

                            if (accountHistory.getSmsNotification() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSmsNotification() + "', ";
                            insertSql = insertSql + accountHistory.getStatusMelli() + ", ";
                            insertSql = insertSql + "'" + DateUtil.getSystemDate() + "' ";
                            insertSql = insertSql + ")";
                            insStm = connection.createStatement();
                            insStm.executeUpdate(insertSql);


                            String deleteAccSQL = "delete from TBCUSTOMERACCOUNTS where account_no = '" + accountNo + "'";
                            deleteAccST = connection.createStatement();
                            deleteAccST.execute(deleteAccSQL);

                            String deleteSrvSQL = "delete from TBCUSTOMERSRV where account_no = '" + accountNo + "'";
                            deleteSrvST = connection.createStatement();
                            deleteSrvST.execute(deleteSrvSQL);

                            String cusaccUpdate = "update tbcustacc set status='" + Constants.REVOKE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and status='" + Constants.ACTIVE_ROW_STATUS + "'";
                            custaccStatement = connection.createStatement();
                            custaccStatement.execute(cusaccUpdate);

                            String accountGroup=accountHistory.getAccountType()==null?"":accountHistory.getAccountType().trim();
                            String origCreatDate=accountHistory.getOrigCreatDate()==null?"":accountHistory.getOrigCreatDate().trim();

                            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                                    "SESSION_ID," +
                                    "CHANNEL_TYPE," +
                                    "PARTNO," +
                                    "INSERT_DATETIME," +
                                    "ACCOUNT_NO," +
                                    "NATIONAL_CODE," +
                                    "EXTERNAL_ID_NUMBER," +
                                    "OPERATION_TYPE," +
                                    "ACCOUNT_TITLE," +
                                    "SUB_TITLE," +
                                    "HANDLED,"+
                                    "OPEN_DATE,"+
                                    "OPEN_TIME,"+
                                    "BRANCH_ID,"+
                                    "ACCOUNT_GROUP"+
                                    ") values(" +
                                    "'" + session_id + "'," +
                                    "'" + channel_type + "'," +
                                    getPartNo() + "," +
                                    "current_timestamp, " +
                                    "'" + accountNo + "'," +
                                    "'" + nationalCode + "'," +
                                    "'" + externalIdNumber + "'," +
                                    "'" + Constants.REVOKE_STATUS + "'," +
                                    "'" + accountTitle + "'," +
                                    "'" + subTitle + "'," +
                                    1 +","+
                                    "'" + origCreatDate + "'," +
                                    "'" + DateUtil.getSystemTime() + "'," +
                                    "'" + sgbBranchId + "'," +
                                    "'" +accountGroup + "'" +
                                    " )";
                            nacLogStatment = connection.createStatement();
                            nacLogStatment.execute(insertNaclog_sql);
                            nacLogStatment.close();



                            connection.commit();
                        } else {
                            connection.rollback();
                            return 6;
                        }
                    } else {
                        connection.rollback();
                        return 7;
                    }
                }
            } else {
                connection.rollback();
                return 10;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.conditionalRevokeAccount()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.conditionalRevokeAccount()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (historyRS != null) historyRS.close();
                if (statementHistory != null) statementHistory.close();
                if (accRS != null) accRS.close();
                if (statement != null) statement.close();
                if (srvRS != null) srvRS.close();
                if (srvStatement != null) srvStatement.close();
                if (cardRS != null) cardRS.close();
                if (cardStatement != null) cardStatement.close();
                if (resultSetCard != null) resultSetCard.close();
                if (resultSetCustAcc != null) resultSetCustAcc.close();
                if (statementCard != null) statementCard.close();
                if (statementCustAcc != null) statementCustAcc.close();
                if (statementDelCustAcc != null) statementDelCustAcc.close();
                if (custaccStatement != null) custaccStatement.close();
                if (deleteAccST != null) deleteAccST.close();
                if (deleteSrvST != null) deleteSrvST.close();
                if (insStm != null) insStm.close();
                if (nacLogStatment != null) nacLogStatment.close();

            } catch (SQLException e1) {
                log.error("CFSFacadeNew.conditionalRevokeAccount() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static int unConditionalRevokeAccount(String accountNo, String branchId, String nationalCode, String externalIdNumber, String sessionId,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statementHistory = null;
        ResultSet historyRS = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement srvStatement = null;
        ResultSet srvRS = null;
        Statement cardStatement = null;
        ResultSet cardRS = null;
        Statement statementCustAcc = null;
        ResultSet resultSetCustAcc = null;
        Statement custaccStatement = null;
        Statement cardUpdateStatement = null;
        Statement deleteAccST = null;
        Statement deleteSrvST = null;
        Statement insStm = null;
        Statement insertUnblockStm = null;
        Statement deleteBlockStm = null;
        Statement selectBlockStatement = null;
        ResultSet selectBlockResult = null;
        Statement insertServiceStatement = null;
        Statement insertEstatusStatement = null;
        Statement insertSstatusStatement = null;
        Statement insertStatusDEStatement = null;
        Statement insertStatusDBStatement = null;
        Statement insertRemoveLogStatement = null;
        Statement insertRevokeHistoryStatement = null;
        Statement nacLogStatment = null;
        String customerId = "";
        String subSidyAmountFlag = "0";
        String blockAccountFlag = "0";
        String eBlockFlag = "0";
        String sBlockFlag = "0";
        String dBlockFlag = "00";
        connection = dbConnectionPool.getConnection();
        String unblockDesc = "";
        String accountTitle ="";
        String subTitle="";
        byte[] descByte = {46, 40, 69, 60, 79, 40, 35, 85, 83, 76, 88, 35, 32, 85, 32, 66, 39, 69};
        try {
//            String s = new String(descByte, "ISO-8859-1");
            unblockDesc = FarsiUtil.convertWindows1256(descByte);
        } catch (UnsupportedEncodingException e) {
            unblockDesc = "";
        }
        try {

            String hql = "select ACCOUNT_TITLE,SUB_TITLE,CUSTOMER_ID from tbcustacc where  account_no= '" + accountNo + "' and NATIONAL_CODE='" + nationalCode.trim() +
                    "' and EXTERNAL_ID_NUMBER='" + externalIdNumber + "' and status='" + Constants.ACTIVE_ROW_STATUS + "' for update";
            statementCustAcc=connection.createStatement();
            resultSetCustAcc = statementCustAcc.executeQuery(hql);

            if (resultSetCustAcc.next()) {
                customerId = resultSetCustAcc.getString("CUSTOMER_ID").trim();
                accountTitle = resultSetCustAcc.getString("ACCOUNT_TITLE").trim();
                subTitle = resultSetCustAcc.getString("SUB_TITLE").trim();

                if (resultSetCustAcc.getString("ACCOUNT_TITLE").equalsIgnoreCase("3") && !resultSetCustAcc.getString("SUB_TITLE").equalsIgnoreCase("0")) {
                    // remove row :: Invalid operation
                    connection.rollback();
                    return 4;
                } else {
                    //remove another
                    String historySql = "SELECT ACCOUNT_NO from tbaccounthistory where ACCOUNT_NO = '" + accountNo + "' with ur";
                    statementHistory = connection.createStatement();
                    historyRS = statementHistory.executeQuery(historySql);
                    if (historyRS.next()) {
//                    "revoke account before"
                        connection.rollback();
                        return 1;
                    }


                    String accountSql = "SELECT CUSTOMER_ID,ACCOUNT_TYPE,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS," +
                            " CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE,SPARROW_BRANCH_ID,ORIG_EDIT_DATE," +
                            " SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,balance, SUBSIDY_AMOUNT " +
                            " from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' and LOCK_STATUS<>9 for update";
                    statement = connection.createStatement();
                    accRS = statement.executeQuery(accountSql);
                    if (accRS.next()) {

                        if (accRS.getString("ACCOUNT_TYPE").equalsIgnoreCase(Constants.GIFT_CARD_007) || accRS.getString("ACCOUNT_TYPE").equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE1) ||
                                accRS.getString("ACCOUNT_TYPE").equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE2) || accRS.getString("ACCOUNT_TYPE").equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE3)) {
                            //revoked Group or Gift account is invalid
                            connection.rollback();
                            return 2;
                        }

                        String srvSql = "SELECT  CUSTOMER_ID,STATUS,CREATION_DATE,HOST_ID,TEMPLATE_ID,NATIONAL_CODE," +
                                " TMP_UPDATE_DATE,ACCOUNT_NATURE, SMSNOTIFICATION,STATUSMELLI,E_STATUS,SERVICE_STATUS,STATUSD " +
                                " from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' for update";
                        srvStatement = connection.createStatement();
                        srvRS = srvStatement.executeQuery(srvSql);
                        if (srvRS.next()) {

                            AccountHistory accountHistory = new AccountHistory(
                                    accRS.getString("CUSTOMER_ID"), accRS.getString("ACCOUNT_TYPE"), accountNo, accRS.getInt("LOCK_STATUS"), accRS.getString("CURRENCY"),
                                    accRS.getString("ACCOUNT_TITLE"), accRS.getInt("STATUS"), accRS.getString("CREATION_DATE"), accRS.getString("CREATION_TIME"),
                                    accRS.getString("ORIG_CREATE_DATE"), accRS.getString("SPARROW_BRANCH_ID"), accRS.getString("ORIG_EDIT_DATE"), accRS.getString("SGB_BRANCH_ID"),
                                    accRS.getString("NATURE"), accRS.getString("ACCOUNT_OPENER_NAME"), accRS.getInt("WITHDRAW_TYPE"), srvRS.getString("CUSTOMER_ID"), srvRS.getInt("STATUS"),
                                    srvRS.getString("CREATION_DATE"), srvRS.getInt("HOST_ID"), srvRS.getInt("TEMPLATE_ID"), srvRS.getString("NATIONAL_CODE"), srvRS.getString("TMP_UPDATE_DATE"),
                                    srvRS.getInt("ACCOUNT_NATURE"), srvRS.getString("SMSNOTIFICATION"), srvRS.getInt("STATUSMELLI"));

                            accountHistory.setBalance(accRS.getLong("BALANCE"));
                            long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                            accountHistory.setSubsidyAmount(subsidyAmount);
                            accountHistory.setStatusD(srvRS.getString("STATUSD"));
                            if (subsidyAmount != 0) {

                                String selectSql = "select * from TBBLCKSTACC where ACCOUNT_NO='" + accountNo + "' for update";
                                selectBlockStatement = connection.createStatement();
                                selectBlockResult = selectBlockStatement.executeQuery(selectSql);
                                while (selectBlockResult.next()) {
                                    subSidyAmountFlag = "1";

                                    Timestamp t = new Timestamp(System.currentTimeMillis());
                                    String tString = t.toString();

                                    String insertSql = "insert into TBUNBLCKSTACC" +
                                            "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,UNBLOCKAMOUNT,BRANCHID,CHN_USER,UNBLOCK_REASON,DESC,CREATEDATE," +
                                            "CREATETIME,UPDATEDATETIME,TX_ORIG_DATE,TX_ORIG_TIME,BLOCK_REASON)" +
                                            " values ('" + accountNo + "','" + selectBlockResult.getString("BLOCK_NO") + "','" + selectBlockResult.getString("BROKER_ID") + "','" + selectBlockResult.getString("PROVIDER_ID") + "'," + selectBlockResult.getLong("BLOCKAMOUNT") + ",'" +
                                            selectBlockResult.getString("BRANCH") + "','" + selectBlockResult.getString("CHN_USER") + "','','" + unblockDesc + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() +
                                            "',timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + selectBlockResult.getString("TX_ORIG_DATE") + "','" + selectBlockResult.getString("TX_ORIG_TIME") + "','" + selectBlockResult.getString("DESC") + "')";

                                    insertUnblockStm = connection.createStatement();
                                    insertUnblockStm.executeUpdate(insertSql);


                                    String insertRevokeHistorySql = "insert into tbrevokehistory" +
                                            "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,BLOCKAMOUNT)" +
                                            " values ('" + accountNo + "','" + selectBlockResult.getString("BLOCK_NO") + "','" + selectBlockResult.getString("BROKER_ID") + "','" + selectBlockResult.getString("PROVIDER_ID") + "'," + selectBlockResult.getLong("BLOCKAMOUNT") + ")";

                                    insertRevokeHistoryStatement = connection.createStatement();
                                    insertRevokeHistoryStatement.executeUpdate(insertRevokeHistorySql);
                                }

                                if (subSidyAmountFlag.equalsIgnoreCase("1")) {
                                    String SQL = "delete from TBBLCKSTACC where ACCOUNT_NO = '" + accountNo.trim() + "'";
                                    deleteBlockStm = connection.createStatement();
                                    deleteBlockStm.executeUpdate(SQL);
                                }
                            }

                            String cardSql = "SELECT pan from tbCfscardAccount where ACCOUNT_NO = '" + accountNo + "' and status=1 with ur";
                            cardStatement = connection.createStatement();
                            cardRS = cardStatement.executeQuery(cardSql);
                            if (cardRS.next()) {
                                String pan = cardRS.getString("PAN");
                                accountHistory.setCardNo(pan);

                                String cardUpdate = "update tbCfscardAccount set status=0 where  account_no= '" + accountNo + "' and status=1 and pan='" + pan + "'";
                                cardUpdateStatement = connection.createStatement();
                                cardUpdateStatement.execute(cardUpdate);
                            }else{
                                accountHistory.setCardNo("");
                            }

                            blockAccountFlag= String.valueOf(accRS.getLong("STATUS"));
                            if (!blockAccountFlag.equalsIgnoreCase(Constants.ACCOUNT_STATUS_ACTIVE)) {
                                //"account is blocked"

                                String sql = "insert into TBSERVICESTATUSLOG " +
                                        "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                        "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                        " values('" +
                                        accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','1'," +
                                        "'" + Constants.AMX + "','" + Constants.ACCOUNT_STATUS_MESSAGE + "','" + unblockDesc + "')";
                                insertServiceStatement = connection.createStatement();
                                insertServiceStatement.executeUpdate(sql);
                            }
                            eBlockFlag=srvRS.getString("E_STATUS");
                            if (!eBlockFlag.equalsIgnoreCase(Constants.E_STATUS_ACTIVE)) {
                                //"E-Status is blocked"

                                String sql = "insert into TBSERVICESTATUSLOG " +
                                        "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                        "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                        " values('" +
                                        accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','1'," +
                                        "'" + Constants.AMX + "','" + Constants.E_STATUS_MESSAGE + "','" + unblockDesc + "')";
                                insertEstatusStatement = connection.createStatement();
                                insertEstatusStatement.executeUpdate(sql);
                            }
                            sBlockFlag=srvRS.getString("SERVICE_STATUS");
                            if (!sBlockFlag.equals(Constants.SERVICE_STATUS_ACTIVE)) {
                                //"Service-Status is blocked"

                                String sql = "insert into TBSERVICESTATUSLOG " +
                                        "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                        "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                        " values('" +
                                        accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','1'," +
                                        "'" + Constants.AMX + "','" + Constants.SERVICE_STATUS_MESSAGE + "','" + unblockDesc + "')";
                                insertSstatusStatement = connection.createStatement();
                                insertSstatusStatement.executeUpdate(sql);
                            }
                            dBlockFlag=srvRS.getString("STATUSD");
                            if (!dBlockFlag.equals(Constants.DEPOSIT_UBLOCK)) {
                                //"StatusD is blocked"
                                if(dBlockFlag.startsWith(Constants.CM_BLOCK))
                                {
                                    String sql = "insert into TBSERVICESTATUSLOG " +
                                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                            " values('" +
                                            accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() +
                                            "','"+Constants.NON_BRANCH_POSTFIX+"'," +"'" + Constants.AMX + "','" + Fields.UNBLOCK_DEPOSIT + "','" + unblockDesc + "')";
                                    insertStatusDEStatement = connection.createStatement();
                                    insertStatusDEStatement.executeUpdate(sql);
                                }
                                if(dBlockFlag.endsWith(Constants.CM_BLOCK))
                                {
                                    String sql = "insert into TBSERVICESTATUSLOG " +
                                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                            " values('" +
                                            accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() +
                                            "','"+Constants.BRANCH_POSTFIX+"'," +"'" + Constants.AMX + "','" + Fields.UNBLOCK_DEPOSIT + "','" + unblockDesc + "')";
                                    insertStatusDBStatement = connection.createStatement();
                                    insertStatusDBStatement.executeUpdate(sql);

                                }
                            }

                            accountHistory.setAccountStatus(Integer.parseInt(blockAccountFlag));
                            accountHistory.seteStatus(eBlockFlag);
                            accountHistory.setServiceStatus(sBlockFlag);
                            accountHistory.setRevokeType(Constants.UNCONDITIONAL_REVOKE_STATUS);

                            String insertSql = "insert into TBACCOUNTHISTORY " +
                                    "(CUSTOMER_ID,ACCOUNT_TYPE,ACCOUNT_NO,LOCK_STATUS,CURRENCY,ACCOUNT_TITLE,STATUS,CREATION_DATE,CREATION_TIME,ORIG_CREATE_DATE," +
                                    "SPARROW_BRANCH_ID,ORIG_EDIT_DATE,SGB_BRANCH_ID,NATURE,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE,CUSTOMER_ID_srv,STATUS_srv," +
                                    "CREATION_DATE_srv,HOST_ID_srv,TEMPLATE_ID,NATIONAL_CODE,TMP_UPDATE_DATE,ACCOUNT_NATURE,SMSNOTIFICATION,STATUSMELLI," +
                                    "CARD_STATUS,CARD_NO,BALANCE,SUBSIDY_AMOUNT,E_STATUS,SERVICE_STATUS,REVOKE_TYPE,STATUSD) " +
                                    "values(";
                            if (accountHistory.getCustomerId() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCustomerId() + "', ";
                            if (accountHistory.getAccountType() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountType() + "', ";
                            insertSql = insertSql + "'" + accountNo + "', ";
                            insertSql = insertSql + accountHistory.getLockStatus() + ", ";
                            if (accountHistory.getCurrency() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCurrency() + "', ";
                            if (accountHistory.getAccountTitle() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountTitle() + "', ";
                            insertSql = insertSql + accountHistory.getAccountStatus() + ", ";
                            if (accountHistory.getCreationDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationDate() + "', ";
                            if (accountHistory.getCreationTime() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationTime() + "', ";

                            if (accountHistory.getOrigCreatDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getOrigCreatDate() + "', ";

                            if (accountHistory.getSparrow_branch_id() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSparrow_branch_id() + "', ";

                            if (accountHistory.getOrigEditDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getOrigEditDate() + "', ";
                            if (accountHistory.getSgb_branch_id() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSgb_branch_id() + "', ";

                            if (accountHistory.getNature() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getNature() + "', ";

                            if (accountHistory.getAccountOpenerName() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getAccountOpenerName() + "', ";
                            insertSql = insertSql + accountHistory.getWithdrawType() + ", ";
                            if (accountHistory.getCustomerIdSrv() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCustomerIdSrv() + "', ";

                            insertSql = insertSql + "'" + accountHistory.getStatusSrv() + "', ";

                            if (accountHistory.getCreationDateSrv() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getCreationDateSrv() + "', ";

                            insertSql = insertSql + "'" + accountHistory.getHostIDSrv() + "', ";

                            insertSql = insertSql + accountHistory.getTemplateIdSrv() + ", ";

                            if (accountHistory.getNationalCode() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getNationalCode() + "', ";
                            if (accountHistory.getTmpUpdateDate() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getTmpUpdateDate() + "', ";

                            insertSql = insertSql + accountHistory.getAccountNature() + ", ";

                            if (accountHistory.getSmsNotification() == null)
                                insertSql = insertSql + "'',";
                            else
                                insertSql = insertSql + "'" + accountHistory.getSmsNotification() + "', ";
                            insertSql = insertSql + accountHistory.getStatusMelli() + ", ";
                            insertSql = insertSql + "'" + DateUtil.getSystemDate() + "', ";
                            insertSql = insertSql + "'" + accountHistory.getCardNo() + "', ";
                            insertSql = insertSql + accountHistory.getBalance() + ", ";
                            insertSql = insertSql + accountHistory.getSubsidyAmount() + ", ";
                            insertSql = insertSql + "'" + accountHistory.geteStatus() + "', ";
                            insertSql = insertSql + "'" + accountHistory.getServiceStatus() + "', ";
                            insertSql = insertSql + "'" + accountHistory.getRevokeType() + "', ";
                            insertSql = insertSql + "'" + accountHistory.getStatusD() + "' ";
                            insertSql = insertSql + ")";
                            insStm = connection.createStatement();
                            insStm.executeUpdate(insertSql);


                            String deleteAccSQL = "delete from TBCUSTOMERACCOUNTS where account_no = '" + accountNo + "'";
                            deleteAccST = connection.createStatement();
                            deleteAccST.execute(deleteAccSQL);

                            String deleteSrvSQL = "delete from TBCUSTOMERSRV where account_no = '" + accountNo + "'";
                            deleteSrvST = connection.createStatement();
                            deleteSrvST.execute(deleteSrvSQL);

                            String cusaccUpdate = "update tbcustacc set status='" + Constants.REVOKE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and status='" + Constants.ACTIVE_ROW_STATUS + "'";
                            custaccStatement = connection.createStatement();
                            custaccStatement.execute(cusaccUpdate);

                            String insertRemoveLog = "insert into TBSERVICESTATUSLOG " +
                                    "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                                    "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                                    " values('" +
                                    accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.REVOKE_ACCOUNT_STATUS + "'," +
                                    "'" + Constants.AMX + "','" + Constants.REVOKE_ACCOUNT_MESSAGE + "','" + unblockDesc + "')";
                            insertRemoveLogStatement = connection.createStatement();
                            insertRemoveLogStatement.executeUpdate(insertRemoveLog);

                            String accountGroup=accountHistory.getAccountType()==null?"":accountHistory.getAccountType().trim();
                            String origCreatDate=accountHistory.getOrigCreatDate()==null?"":accountHistory.getOrigCreatDate().trim();
                            String sgbBrachId=accountHistory.getSgb_branch_id()==null?"":accountHistory.getSgb_branch_id().trim();

                            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                                    "SESSION_ID," +
                                    "CHANNEL_TYPE," +
                                    "PARTNO," +
                                    "INSERT_DATETIME," +
                                    "ACCOUNT_NO," +
                                    "NATIONAL_CODE," +
                                    "EXTERNAL_ID_NUMBER," +
                                    "OPERATION_TYPE,"+
                                    "ACCOUNT_TITLE," +
                                    "SUB_TITLE," +
                                    "HANDLED,"+
                                    "OPEN_DATE,"+
                                    "OPEN_TIME,"+
                                    "BRANCH_ID,"+
                                    "ACCOUNT_GROUP"+
                                    ") values(" +
                                    "'" + sessionId + "'," +
                                    "'" + channel_type + "'," +
                                    getPartNo() + "," +
                                    "current_timestamp, " +
                                    "'" +accountNo + "'," +
                                    "'" +nationalCode.trim() + "'," +
                                    "'" + externalIdNumber + "'," +
                                    "'" + Constants.REVOKE_STATUS + "'," +
                                    "'" + accountTitle + "'," +
                                    "'" + subTitle + "'," +
                                    1 +","+
                                    "'" +origCreatDate+ "'," +
                                    "'" + DateUtil.getSystemTime() + "'," +
                                    "'" + sgbBrachId + "'," +
                                    "'" + accountGroup + "'" +
                                    " )";
                            nacLogStatment = connection.createStatement();
                            nacLogStatment.execute(insertNaclog_sql);
                            nacLogStatment.close();

                            connection.commit();
                        } else {
                            connection.rollback();
                            return 6;
                        }
                    } else {
                        connection.rollback();
                        return 7;
                    }
                }
            } else {
                connection.rollback();
                return 10;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.conditionalRevokeAccount()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.conditionalRevokeAccount()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (historyRS != null) historyRS.close();
                if (accRS != null) accRS.close();
                if (srvRS != null) srvRS.close();
                if (cardRS != null) cardRS.close();
                if (resultSetCustAcc != null) resultSetCustAcc.close();
                if (selectBlockResult != null) selectBlockResult.close();
                if (statementHistory != null) statementHistory.close();
                if (cardStatement != null) cardStatement.close();
                if (statement != null) statement.close();
                if (srvStatement != null) srvStatement.close();
                if (statementCustAcc != null) statementCustAcc.close();
                if (custaccStatement != null) custaccStatement.close();
                if (deleteAccST != null) deleteAccST.close();
                if (deleteSrvST != null) deleteSrvST.close();
                if (insStm != null) insStm.close();
                if (selectBlockStatement != null) selectBlockStatement.close();
                if (insertUnblockStm != null) insertUnblockStm.close();
                if (deleteBlockStm != null) deleteBlockStm.close();
                if (cardUpdateStatement != null) cardUpdateStatement.close();
                if (insertServiceStatement != null) insertServiceStatement.close();
                if (insertEstatusStatement != null) insertEstatusStatement.close();
                if (insertSstatusStatement != null) insertSstatusStatement.close();
                if (insertStatusDEStatement != null) insertStatusDEStatement.close();
                if (insertStatusDBStatement != null) insertStatusDBStatement.close();
                if (insertRemoveLogStatement != null) insertRemoveLogStatement.close();
                if (insertRevokeHistoryStatement != null) insertRevokeHistoryStatement.close();
                if (nacLogStatment != null) nacLogStatment.close();

            } catch (SQLException e1) {
                log.error("CFSFacadeNew.conditionalRevokeAccount() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static void revivalAccountTransactional(String accountNo, String branchId, String sessionId,String nationalCode,String externalIdNumber,String channel_type) throws NotFoundException, SQLException, ServerAuthenticationException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Statement accStatement = null;
        Statement srvStatement = null;
        Statement deleteAccST = null;
        Statement custaccStatement = null;
        Statement selectUnBlockStatement = null;
        Statement deleteUnBlockStatement = null;
        Statement deleteBlockStm = null;
        Statement cardUpdateStatement = null;
        Statement deleteAccountLogStatement = null;
        Statement deleteEStatusLogStatement = null;
        Statement deleteserviceStatusLogStatement = null;
        Statement deleteStatusDLogStatement = null;
        Statement insertRemoveLogStatement = null;
        ResultSet selectUnBlockResualt = null;
        Statement selectBlockStatement = null;
        Statement insertBlockStm = null;
        ResultSet selectBlockResult = null;
        Statement statementCustAcc = null;
        ResultSet resultSetCustAcc = null;
        Statement nacLogStatment = null;
        connection = dbConnectionPool.getConnection();

        try {
            String accountSql = "SELECT * from tbAccounthistory where ACCOUNT_NO = '" + accountNo + "' for update;";
            statement = connection.createStatement();
            rs = statement.executeQuery(accountSql);
            if (rs.next()) {
                String sgbBranchId = rs.getString("SGB_BRANCH_ID");
                if (!sgbBranchId.equalsIgnoreCase(branchId))
                    throw new ServerAuthenticationException("SGB_Branch_ID is not equal Block_Branch_ID");

                int srvStatus;
                if (rs.getString("STATUS_srv") == null)
                    srvStatus = 0;
                else
                    srvStatus = Integer.parseInt(rs.getString("STATUS_srv"));

                int srvHostId;
                if (rs.getString("HOST_ID_srv") == null)
                    srvHostId = 0;
                else
                    srvHostId = Integer.parseInt(rs.getString("HOST_ID_srv"));

                AccountHistory accountHistory = new AccountHistory(
                        rs.getString("CUSTOMER_ID"), rs.getString("ACCOUNT_TYPE"), accountNo, rs.getInt("LOCK_STATUS"), rs.getString("CURRENCY"),
                        rs.getString("ACCOUNT_TITLE"), rs.getInt("STATUS"), rs.getString("CREATION_DATE"), rs.getString("CREATION_TIME"),
                        rs.getString("ORIG_CREATE_DATE"), rs.getString("SPARROW_BRANCH_ID"), rs.getString("ORIG_EDIT_DATE"), rs.getString("SGB_BRANCH_ID"),
                        rs.getString("NATURE"), rs.getString("ACCOUNT_OPENER_NAME"), rs.getInt("WITHDRAW_TYPE"), rs.getString("CUSTOMER_ID_SRV"), srvStatus,
                        rs.getString("CREATION_DATE_srv"), srvHostId, rs.getInt("TEMPLATE_ID"), rs.getString("NATIONAL_CODE"), rs.getString("TMP_UPDATE_DATE"),
                        rs.getInt("ACCOUNT_NATURE"), rs.getString("SMSNOTIFICATION"), rs.getInt("STATUSMELLI"), rs.getString("CARD_NO"), rs.getLong("BALANCE"),
                        rs.getLong("SUBSIDY_AMOUNT"), rs.getString("E_STATUS"),rs.getString("SERVICE_STATUS"), rs.getString("REVOKE_TYPE"),rs.getString("STATUSD"));


                String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                        "CUSTOMER_ID," +
                        "ACCOUNT_TYPE," +     // Account Group
                        "ACCOUNT_NO," +
                        "LOCK_STATUS," +
                        "CURRENCY," +
                        "BALANCE," +
                        "ACCOUNT_TITLE," +
                        "STATUS," +
                        "ACCOUNT_SRC," +
                        "CREATION_DATE," +
                        "CREATION_TIME," +
                        "ORIG_CREATE_DATE," +
                        "ORIG_EDIT_DATE," +
                        "SGB_BRANCH_ID," +
                        "HOST_ID, " +
                        "SPARROW_BRANCH_ID, " +
                        "NATURE, " +
                        "ACCOUNT_OPENER_NAME, " +
                        "WITHDRAW_TYPE, " +
                        "SUBSIDY_AMOUNT " +
                        ") values(";
                if (accountHistory.getCustomerId() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCustomerId() + "',";

                if (accountHistory.getAccountType() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountType() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountNo + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getLockStatus() + ",";
                if (accountHistory.getCurrency() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCurrency() + "',";

                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getBalance() + ",";

                if (accountHistory.getAccountTitle() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountTitle() + "',";

                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getAccountStatus() + ",";

                insertCustomerAccount_sql = insertCustomerAccount_sql + 2 + ",";

                if (accountHistory.getCreationDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCreationDate() + "',";

                if (accountHistory.getCreationTime() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getCreationTime() + "',";

                if (accountHistory.getOrigCreatDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getOrigCreatDate() + "',";

                if (accountHistory.getOrigEditDate() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getOrigEditDate() + "',";

                if (accountHistory.getSgb_branch_id() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getSgb_branch_id() + "',";
                insertCustomerAccount_sql = insertCustomerAccount_sql + Constants.HOST_CFS + ",";

                if (accountHistory.getSparrow_branch_id() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getSparrow_branch_id() + "',";

                if (accountHistory.getNature() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getNature() + "',";

                if (accountHistory.getAccountOpenerName() == null)
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'',";
                else
                    insertCustomerAccount_sql = insertCustomerAccount_sql + "'" + accountHistory.getAccountOpenerName() + "',";

                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getWithdrawType() + ",";

                insertCustomerAccount_sql = insertCustomerAccount_sql + accountHistory.getSubsidyAmount();

                insertCustomerAccount_sql = insertCustomerAccount_sql + ")";

                accStatement = connection.createStatement();
                accStatement.execute(insertCustomerAccount_sql);


                String srvSql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                        "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP," +
                        "NATIONAL_CODE,STATUSMELLI, LANG, SMSNOTIFICATION, ACCOUNT_NATURE, TMP_UPDATE_DATE, E_STATUS, SERVICE_STATUS, STATUSD" +
                        ") values (";

                if (accountHistory.getCustomerIdSrv() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getCustomerIdSrv() + "',";
                srvSql = srvSql + "'1234',";
                srvSql = srvSql + "'1234',";
                srvSql = srvSql + "current_timestamp,";
                srvSql = srvSql + "'" + accountNo + "',";
                srvSql = srvSql + accountHistory.getTemplateIdSrv() + ",";
                srvSql = srvSql + accountHistory.getStatusSrv() + ",";
                srvSql = srvSql + accountHistory.getHostIDSrv() + ",";

                if (accountHistory.getCreationDateSrv() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getCreationDateSrv() + "',";

                if (accountHistory.getAccountType() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getAccountType() + "',";

                if (accountHistory.getNationalCode() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getNationalCode() + "',";
                srvSql = srvSql + accountHistory.getStatusMelli() + ",";
                srvSql = srvSql + 1 + ",";

                if (accountHistory.getSmsNotification() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getSmsNotification() + "',";

                srvSql = srvSql + accountHistory.getAccountNature() + ",";
                if (accountHistory.getTmpUpdateDate() == null)
                    srvSql = srvSql + "'',";
                else
                    srvSql = srvSql + "'" + accountHistory.getTmpUpdateDate() + "',";

                srvSql = srvSql + "'" + accountHistory.geteStatus() + "',";

                srvSql = srvSql + "'" + accountHistory.getServiceStatus() + "',";

                srvSql = srvSql + "'" + accountHistory.getStatusD() + "'";

                srvSql = srvSql + " )";

                srvStatement = connection.createStatement();
                srvStatement.execute(srvSql);

                String deleteAccSQL = "delete from TBACCOUNTHISTORY where account_no = '" + accountNo + "'";
                deleteAccST = connection.createStatement();
                deleteAccST.execute(deleteAccSQL);

                String cusaccUpdate = "update tbcustacc set status='" + Constants.ACTIVE_ROW_STATUS + "' where  account_no= '" + accountNo + "' and status='" + Constants.REVOKE_ROW_STATUS + "'";
                custaccStatement = connection.createStatement();
                custaccStatement.execute(cusaccUpdate);


                if (accountHistory.getRevokeType().equalsIgnoreCase(Constants.UNCONDITIONAL_REVOKE_STATUS)) {

                    String selectSql = "select * from tbrevokehistory where ACCOUNT_NO='" + accountNo + "' for update";
                    selectBlockStatement = connection.createStatement();
                    selectBlockResult = selectBlockStatement.executeQuery(selectSql);
                    String subSidyAmountFlag = "0";
                    String blockNo = "";
                    String brokerId = "";
                    String providerId = "";
                    long blockAmount = 0;
                    while (selectBlockResult.next()) {
                        subSidyAmountFlag = "1";

                        blockNo = selectBlockResult.getString("BLOCK_NO");
                        brokerId = selectBlockResult.getString("BROKER_ID");
                        providerId = selectBlockResult.getString("PROVIDER_ID");
                        blockAmount = selectBlockResult.getLong("BLOCKAMOUNT");

                        String unblockSql = "select * from tbunblckstacc where ACCOUNT_NO='" + accountNo + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "'for update";
                        selectUnBlockStatement = connection.createStatement();
                        selectUnBlockResualt = selectUnBlockStatement.executeQuery(unblockSql);

                        if (selectUnBlockResualt.next()) {

                            Timestamp t = new Timestamp(System.currentTimeMillis());
                            String tString = t.toString();

                            String insertSql = "insert into TBBLCKSTACC" +
                                    "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,BLOCKAMOUNT,CREATEDATE,CREATETIME,UPDATEDATETIME," +
                                    "CHN_USER,BRANCH,DESC,TX_ORIG_DATE,TX_ORIG_TIME)" +
                                    " values('" + accountNo + "','" + blockNo + "','" + brokerId + "','" + providerId + "'," + blockAmount + ",'" + DateUtil.getSystemDate().substring(0, 2) + selectUnBlockResualt.getString("TX_ORIG_DATE") + "','" +
                                    selectUnBlockResualt.getString("TX_ORIG_TIME") + "', timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + selectUnBlockResualt.getString("CHN_USER") + "', '" +
                                    selectUnBlockResualt.getString("BRANCHID") + "', '" + selectUnBlockResualt.getString("BLOCK_REASON") + "', '" + selectUnBlockResualt.getString("TX_ORIG_DATE") + "', '" + selectUnBlockResualt.getString("TX_ORIG_TIME") + "')";
                            insertBlockStm = connection.createStatement();
                            insertBlockStm.executeUpdate(insertSql);

                            String unblockdel = "delete from tbunblckstacc where ACCOUNT_NO='" + accountNo + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "'";
                            deleteUnBlockStatement = connection.createStatement();
                            deleteUnBlockStatement.executeUpdate(unblockdel);
                        }
                    }

                    if (subSidyAmountFlag.equalsIgnoreCase("1")) {
                        String SQL = "delete from tbrevokehistory where ACCOUNT_NO = '" + accountNo.trim() + "'";
                        deleteBlockStm = connection.createStatement();
                        deleteBlockStm.executeUpdate(SQL);
                    }
                    if (accountHistory.getCardNo() != null && !accountHistory.getCardNo().trim().equalsIgnoreCase("")) {

                        String cardUpdate = "update tbCfscardAccount set status=1 where  account_no= '" + accountNo + "' and pan='" + accountHistory.getCardNo().trim() + "'";
                        cardUpdateStatement = connection.createStatement();
                        cardUpdateStatement.execute(cardUpdate);
                    }

                    if (accountHistory.getAccountStatus()!=Integer.parseInt(Constants.ACCOUNT_STATUS_ACTIVE)) {
                        //"account was blocke"

                        String deleteAccountLog = "delete from TBSERVICESTATUSLOG where ACCOUNT_NO='" + accountNo + "' and CHANNEL_TYPE='" + Constants.AMX + "' and MESSAGE_TYPE='" + Constants.ACCOUNT_STATUS_MESSAGE + "'";
                        deleteAccountLogStatement = connection.createStatement();
                        deleteAccountLogStatement.executeUpdate(deleteAccountLog);
                    }
                    if (!accountHistory.geteStatus().equalsIgnoreCase(Constants.E_STATUS_ACTIVE)) {
                        //"E-Status was blocke"

                        String deleteEStatusLog = "delete from TBSERVICESTATUSLOG where ACCOUNT_NO='" + accountNo + "' and CHANNEL_TYPE='" + Constants.AMX + "' and MESSAGE_TYPE='" + Constants.E_STATUS_MESSAGE + "'";
                        deleteEStatusLogStatement = connection.createStatement();
                        deleteEStatusLogStatement.executeUpdate(deleteEStatusLog);
                    }
                    if (!accountHistory.getServiceStatus().equalsIgnoreCase(Constants.SERVICE_STATUS_ACTIVE)) {
                        //"Service-Status was blocke"

                        String deleteserviceStatusLog = "delete from TBSERVICESTATUSLOG where ACCOUNT_NO='" + accountNo + "' and CHANNEL_TYPE='" + Constants.AMX + "' and MESSAGE_TYPE='" + Constants.SERVICE_STATUS_MESSAGE + "'";
                        deleteserviceStatusLogStatement = connection.createStatement();
                        deleteserviceStatusLogStatement.executeUpdate(deleteserviceStatusLog);
                    }
                    if (!accountHistory.getStatusD().equalsIgnoreCase(Constants.DEPOSIT_UBLOCK)) {
                        //"StatusD was blocked"
                        String deleteStatusDLog = "delete from TBSERVICESTATUSLOG where ACCOUNT_NO='" + accountNo + "' and CHANNEL_TYPE='" + Constants.AMX + "' and MESSAGE_TYPE='" + Fields.UNBLOCK_DEPOSIT + "'";
                        deleteStatusDLogStatement = connection.createStatement();
                        deleteStatusDLogStatement.executeUpdate(deleteStatusDLog);
                    }

                    String unblockDesc = "";
                    byte[] descByte = {46, 40, 69, 60, 79, 40, 35, 85, 83, 76, 88, 35, 32, 85, 32, 66, 39, 69};
                    try {
//            String s = new String(descByte, "ISO-8859-1");
                        unblockDesc = FarsiUtil.convertWindows1256(descByte);
                    } catch (UnsupportedEncodingException e) {
                        unblockDesc = "";
                    }

                    String insertRemoveLog = "insert into TBSERVICESTATUSLOG " +
                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                            " values('" +
                            accountNo + "','" + sessionId + "','','','" + branchId + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.REVIVAL_ACCOUNT_STATUS + "'," +
                            "'" + Constants.AMX + "','" + Constants.REVOKE_ACCOUNT_MESSAGE + "','" + unblockDesc + "')";
                    insertRemoveLogStatement = connection.createStatement();
                    insertRemoveLogStatement.executeUpdate(insertRemoveLog);
                }


                String accountTitle= accountHistory.getAccountTitle()==null?"":accountHistory.getAccountTitle().trim();
                String accountGroup=accountHistory.getAccountType()==null?"":accountHistory.getAccountType().trim();
                String origCreatDate=accountHistory.getOrigCreatDate()==null?"":accountHistory.getOrigCreatDate().trim();


                String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                        "SESSION_ID," +
                        "CHANNEL_TYPE," +
                        "PARTNO," +
                        "INSERT_DATETIME," +
                        "ACCOUNT_NO," +
                        "NATIONAL_CODE," +
                        "EXTERNAL_ID_NUMBER," +
                        "OPERATION_TYPE," +
                        "ACCOUNT_TITLE," +
                        "HANDLED,"+
                        "OPEN_DATE,"+
                        "OPEN_TIME,"+
                        "BRANCH_ID,"+
                        "ACCOUNT_GROUP"+
                        ") values(" +
                        "'" + sessionId + "'," +
                        "'" + channel_type + "'," +
                        getPartNo() + "," +
                        "current_timestamp, " +
                        "'" + accountNo + "'," +
                        "'" + nationalCode.trim() + "'," +
                        "'" +externalIdNumber + "'," +
                        "'" +Constants.REVIVAL_STATUS + "'," +
                        "'" + accountTitle + "'," +
                        1 +","+
                        "'" + origCreatDate + "'," +
                        "'" + DateUtil.getSystemTime() + "'," +
                        "'" + sgbBranchId + "'," +
                        "'" + accountGroup+ "'" +
                        " )";
                nacLogStatment = connection.createStatement();
                nacLogStatment.execute(insertNaclog_sql);
                nacLogStatment.close();

                connection.commit();
            } else{
                String hql = "select CUSTOMER_ID from tbcustacc where  account_no= '" + accountNo + "' and NATIONAL_CODE='" + nationalCode.trim() +
                        "' and EXTERNAL_ID_NUMBER='" + externalIdNumber + "' and status='" + Constants.ACTIVE_ROW_STATUS + "' with ur";
                statementCustAcc=connection.createStatement();
                resultSetCustAcc = statementCustAcc.executeQuery(hql);
                connection.commit();

                if (!resultSetCustAcc.next()) {
                throw new NotFoundException("Not found Account = " + accountNo);
                }
            }
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.revivalAccountTransactional()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.revivalAccountTransactional()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ServerAuthenticationException e) {
            log.error("CFSFacadeNew.revivalAccountTransactional()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.revivalAccountTransactional()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            if (e.getErrorCode() != Constants.DISTINCT_VIOLATION_ERROR)
                log.error("CFSFacadeNew.revivalAccountTransactional()#5 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.revivalAccountTransactional()#6 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectBlockResult != null) selectBlockResult.close();
                if (selectUnBlockResualt != null) selectUnBlockResualt.close();
                if (statement != null) statement.close();
                if (accStatement != null) accStatement.close();
                if (srvStatement != null) srvStatement.close();
                if (deleteAccST != null) deleteAccST.close();
                if (custaccStatement != null) custaccStatement.close();
                if (selectBlockStatement != null) selectBlockStatement.close();
                if (selectUnBlockStatement != null) selectUnBlockStatement.close();
                if (insertBlockStm != null) insertBlockStm.close();
                if (deleteUnBlockStatement != null) deleteUnBlockStatement.close();
                if (deleteBlockStm != null) deleteBlockStm.close();
                if (cardUpdateStatement != null) cardUpdateStatement.close();
                if (deleteAccountLogStatement != null) deleteAccountLogStatement.close();
                if (deleteEStatusLogStatement != null) deleteEStatusLogStatement.close();
                if (deleteserviceStatusLogStatement != null) deleteserviceStatusLogStatement.close();
                if (deleteStatusDLogStatement != null) deleteStatusDLogStatement.close();
                if (insertRemoveLogStatement != null) insertRemoveLogStatement.close();
                if (statementCustAcc != null) statementCustAcc.close();
                if (resultSetCustAcc != null) resultSetCustAcc.close();
                if (nacLogStatment != null) nacLogStatment.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.revivalAccountTransactional() #7= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void txnDoFTTransaction(Tx tx, long minBalance, HashMap extraData)
            throws NotFoundException, ModelException, ISOException, SQLException {

        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoFTTransaction() #1=   -- Error :: " + e);
            throw new ISOException();
        }
        long amount = tx.getAmount();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            long srcBalance = 0;
            if (extraData.get(Constants.SRC_HOST_ID).equals(Constants.CFS_HOSTID)) {
                srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, -tx.getFeeAmount(), extraData);
            }
            tx.setSrc_account_balance(srcBalance);

            long destBalance = 0;
            if (extraData.get(Constants.DEST_HOST_ID).equals(Constants.CFS_HOSTID)) {
            destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, amount, tx.getTxCode(), 0);
            }
            tx.setDest_account_balance(destBalance);

            saveTxTransactional(connection, tx);
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoFTTransaction() #1=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransaction() #2=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoFTTransaction() #3=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransaction() #4=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoFTTransaction() #5=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransaction() #6=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void txnDoFTReverse(Tx orig_tx, Tx rev_tx, HashMap extraData) throws ModelException, SQLException, NotFoundException {

        String srcAccountNo = orig_tx.getSrcAccountNo();
        String destAccountNo = orig_tx.getDestAccountNo();

        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoFTReverse() #1 -- Error :: " + e);
        }

        long amount = orig_tx.getAmount();
        long srcBalance = 0;

        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            if (extraData.get(Constants.SRC_HOST_ID).equals(Constants.CFS_HOSTID)) {
                srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, amount, rev_tx.getTxCode(), 0, orig_tx.getFeeAmount(), extraData);
            }
            rev_tx.setSrc_account_balance(srcBalance);

            long destBalance = 0;

            Long balanceMinimum = (Long) extraData.get(Fields.BALANCE_MINIMUM);
            if (log.isDebugEnabled()) log.debug("balanceMinimum=" + balanceMinimum);

            destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, -amount, orig_tx.getTxCode().trim(), balanceMinimum.longValue());

            orig_tx.setIsReversed(CFSConstants.REVERSED);

            setTxIsReversedTransactional(connection, orig_tx);

            if (rev_tx != null) rev_tx.setDest_account_balance(destBalance);

            saveTxTransactional(connection, rev_tx);

            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoFTReverse() #2  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTReverse() #3  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoFTReverse() #4  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTReverse() #5  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoFTReverse() #6  -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTReverse() #7  -- Error :: " + e1);
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }

    }

    //**********************AMX************end***********************
    //*********************Delete document******start********************
    public static Tx getInquiryTx(String accountNo, String docNo,String log_id,String requestType) throws SQLException, NotFoundException {
        String sql = "select TX_PK,SESSION_ID,TX_SEQ,TX_CODE,TX_SRC,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,DEVICE_CODE," +
                "CARD_NO,CARD_SEQUENCE_NO,CREATION_DATE,CREATION_TIME,ACQUIRER,SRC_BRANCH_ID,TX_ORIG_DATE,TX_ORIG_TIME," +
                "SGB_ACTION_CODE,SGB_BRANCH_ID,IS_REVERSAL_TXN,ORIG_TX_PK,TX_DATETIME,AMOUNT,BATCH_PK,FEE_AMOUNT,SRC_ACC_BALANCE," +
                "PARTNO,ISREVERSED,ISCUTOVERED,DESC,LAST_TRANS_LIMIT,BRANCH_DOCNO,EBNK_MSG_SEQ,TOTAL_DEST_ACCOUNT,TX_SEQUENCE_NUMBER," +
                "DEST_ACC_BALANCE,RRN,ID1,TERMINAL_ID,ID2,STAN,HOSTCODE,TERMINAL_TYPE,LOG_ID,SETTLE_DATE,EXTRA_INFO" +
                " from tbCFSTx where ";
        if (requestType.equalsIgnoreCase(Constants.DEPOSIT_REQUEST)) {
            sql = sql + " dest_Account_No = '" + accountNo + "' and ";
        } else {
            sql = sql + " src_Account_No = '" + accountNo + "' and ";
        }

        sql = sql + "log_id = " + log_id + " and " +
                "TX_SRC = 'NASIM' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Tx tx = new Tx();

                tx.setTxPk(resultSet.getString("TX_PK"));
                tx.setSessionId(resultSet.getString("SESSION_ID"));
                tx.setMessageId(resultSet.getString("TX_SEQ"));
                tx.setTxCode(resultSet.getString("TX_CODE"));
                tx.setTxSrc(resultSet.getString("TX_SRC"));
                tx.setCurrency(resultSet.getString("CURRENCY"));
                tx.setSrcAccountNo(resultSet.getString("SRC_ACCOUNT_NO"));
                tx.setDestAccountNo(resultSet.getString("DEST_ACCOUNT_NO"));
                tx.setDeviceCode(resultSet.getString("DEVICE_CODE")!=null ? resultSet.getString("DEVICE_CODE") : "");
                tx.setCardNo(resultSet.getString("CARD_NO"));
                tx.setCardSequenceNo(resultSet.getString("CARD_SEQUENCE_NO")!=null ? resultSet.getString("CARD_SEQUENCE_NO") : "");
                tx.setCreationDate(resultSet.getString("CREATION_DATE"));
                tx.setCreationTime(resultSet.getString("CREATION_TIME"));
                tx.setAcquirer(resultSet.getString("ACQUIRER")!=null ? resultSet.getString("ACQUIRER") : "");
                tx.setSrcBranchId(resultSet.getString("SRC_BRANCH_ID"));
                tx.setTxOrigDate(resultSet.getString("TX_ORIG_DATE"));
                tx.setTxOrigTime(resultSet.getString("TX_ORIG_TIME"));
                tx.setSgbActionCode(resultSet.getString("SGB_ACTION_CODE"));
                tx.setSgbBranchId(resultSet.getString("SGB_BRANCH_ID"));
                tx.setIsReversalTx(Integer.parseInt(resultSet.getString("IS_REVERSAL_TXN")));
                tx.setOrigTxPk(resultSet.getString("ORIG_TX_PK"));
                tx.setTxDateTime(resultSet.getTimestamp("TX_DATETIME"));
                tx.setAmount(resultSet.getLong("AMOUNT"));
                tx.setBatchPk(resultSet.getLong("BATCH_PK"));
                tx.setFeeAmount(resultSet.getLong("FEE_AMOUNT"));
                tx.setSrc_account_balance(resultSet.getLong("SRC_ACC_BALANCE"));
                tx.setPartNo(resultSet.getInt("PARTNO"));
                tx.setIsReversed(resultSet.getInt("ISREVERSED"));
                tx.setIsCutovered(resultSet.getInt("ISCUTOVERED"));
                tx.setDescription(resultSet.getString("desc"));
                tx.setLastTransLimit(resultSet.getLong("LAST_TRANS_LIMIT"));
                tx.setBranchDocNo(resultSet.getString("BRANCH_DOCNO"));
                tx.setMessageSeq(resultSet.getString("EBNK_MSG_SEQ"));
                tx.setTotalDestAccNo(resultSet.getString("TOTAL_DEST_ACCOUNT"));
                tx.setTxSequenceNumber(resultSet.getString("TX_SEQUENCE_NUMBER")!=null ? resultSet.getString("TX_SEQUENCE_NUMBER") : "");
                tx.setDest_account_balance(resultSet.getLong("DEST_ACC_BALANCE"));
                tx.setRRN(resultSet.getString("RRN"));
                tx.setId1(resultSet.getString("ID1"));
                tx.setMerchantTerminalId(resultSet.getString("TERMINAL_ID"));
                tx.setId2(resultSet.getString("ID2"));
                tx.setTxSequenceNumber(resultSet.getString("STAN"));
                tx.setHostCode(resultSet.getString("HOSTCODE"));
                tx.setTerminalType(resultSet.getString("TERMINAL_TYPE"));
                tx.setsPayCode(resultSet.getString("log_id"));
                tx.setSettlementDate(resultSet.getString("SETTLE_DATE"));
                tx.setExtraInfo(null != resultSet.getString("EXTRA_INFO") ? resultSet.getString("EXTRA_INFO").trim() : " ");

                return (tx);
            } else {
                throw new NotFoundException("Transaction branchDocNo = " + docNo + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getInquiryTx() =   -- Error :: " + e + " -- sql = " + sql);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void txnDoDeleteTransaction(Tx tx, String accountNo, String requestType) throws NotFoundException, ModelException, SQLException, ISOException, ServerAuthenticationException {

        long amount = 0;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            if (tx.getSrcAccountNo().trim().equals(accountNo)) {
                if (tx.getIsReversalTx() == CFSConstants.REVERSED){
                 //the withdrawal reverse will be removed so we have to withdrawal from source account number and amount will be negative.
                    amount=-(tx.getAmount()+tx.getFeeAmount());
                }else{
                    //the withdrawal will be removed so we have to deposit to source account number and amount will be positive.
                    amount=(tx.getAmount()+tx.getFeeAmount());
                }
            } else if (tx.getDestAccountNo().trim().equals(accountNo)) {
                if (tx.getIsReversalTx() == CFSConstants.REVERSED){
                    //the deposit reversal will be removed so we have to deposit to destination account number and amount will be positive.
                    amount=tx.getAmount();
                }else{
                    //the deposit will be removed so we have to withdrawal to destination account number and amount will be negative.
                    amount=-tx.getAmount();
                }
            }
            updateAccountTransactional(connection, accountNo, amount, 0, null, 0);

            if (tx.getIsReversalTx() == CFSConstants.REVERSED)
                setTxIsReversedTransactional(connection, tx.getOrigTxPk(), Constants.NOT_REVERSED);

            saveTxHistoryTransactional(connection, tx);
            deleteTxTransactional(connection,accountNo,requestType,tx.getsPayCode());
            connection.commit();

        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoDeleteTransaction()#2 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDeleteTransaction()#3 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.error("CFSFacadeNew.txnDoDeleteTransaction()#4 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDeleteTransaction()#5 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoDeleteTransaction()#6 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoDeleteTransaction()#7 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }

    }

    private static void saveTxHistoryTransactional(Connection connection, Tx tx) throws SQLException {


        String sql = "Insert into tbTransDel " +
                "(TX_PK,SESSION_ID,TX_SEQ,TX_CODE,TX_SRC,CURRENCY,SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,DEVICE_CODE,"+
                "CARD_NO,CARD_SEQUENCE_NO,CREATION_DATE,CREATION_TIME,ACQUIRER,SRC_BRANCH_ID,TX_ORIG_DATE,TX_ORIG_TIME," +
                "SGB_ACTION_CODE,SGB_BRANCH_ID,IS_REVERSAL_TXN,ORIG_TX_PK,TX_DATETIME,AMOUNT,BATCH_PK,FEE_AMOUNT,SRC_ACC_BALANCE," +
                "PARTNO,ISREVERSED,ISCUTOVERED,DESC,LAST_TRANS_LIMIT,BRANCH_DOCNO,EBNK_MSG_SEQ,TOTAL_DEST_ACCOUNT,TX_SEQUENCE_NUMBER," +
                "DEST_ACC_BALANCE,RRN,ID1,TERMINAL_ID,ID2,STAN,HOSTCODE,TERMINAL_TYPE,LOG_ID,SETTLE_DATE,EXTRA_INFO" +
                ") values(" +
                "'"+tx.getTxPk() + "'" +
                ",'" + tx.getSessionId() + "'" +
                ",'" + tx.getMessageId() + "'" +
                ",'" + tx.getTxCode() + "'" +
                ",'" + tx.getTxSrc() + "'"+
                ",'" + tx.getCurrency() + "'"+
                ",'" + tx.getSrcAccountNo() + "'"+
                ",'" + tx.getDestAccountNo() + "'"+
                ",'" + tx.getDeviceCode() + "'"+
                ",'" + tx.getCardNo() + "'"+
                ",'" + tx.getCardSequenceNo() + "'"+
                ",'" + tx.getCreationDate() + "'"+
                ",'" + tx.getCreationTime() + "'"+
                ",'" + tx.getAcquirer() + "'"+
                ",'" + tx.getSrcBranchId() + "'"+
                ",'" + tx.getTxOrigDate() + "'"+
                ",'" + tx.getTxOrigTime() + "'"+
                ",'" + tx.getSgbActionCode() + "'"+
                ",'" + tx.getSgbBranchId() + "'"+
                ",'" + tx.getIsReversalTx() + "'"+
                ",'" + tx.getOrigTxPk() + "'"+
                ",current_timestamp" +
                "," + tx.getAmount() +
                "," + tx.getBatchPk() +
                "," + tx.getFeeAmount() +
                "," + tx.getSrc_account_balance() +
                "," + tx.getPartNo() +
                ","+  tx.getIsReversed() +
                "," + tx.getIsCutovered() +
                ",'" + tx.getDescription() + "'"+
                "," + tx.getLastTransLimit() +
                ",'" + tx.getBranchDocNo() + "'"+
                ",'" + tx.getMessageSeq() + "'"+
                ",'" + tx.getTotalDestAccNo() + "'"+
                ",'" + tx.getTxSequenceNumber() + "'"+
                "," + tx.getDest_account_balance() +
                ",'" +tx.getRRN() + "'"+
                ",'" +tx.getId1() + "'"+
                ",'" +tx.getMerchantTerminalId() + "'"+
                ",'" +tx.getId2() + "'"+
                ",'" +tx.getTxSequenceNumber() + "'"+
                ",'" +tx.getHostCode() + "'"+
                ",'" +tx.getTerminalType() + "'"+
                "," +tx.getsPayCode() +
                ",'" +tx.getSettlementDate() + "'"+
                ",'" +tx.getExtraInfo() + "'"+
                ")";

        if (log.isDebugEnabled()) log.debug("sql :: " + sql);

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.saveTxHistoryTransactional() = -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void deleteTxTransactional(Connection connection,String accountNo, String requestType,String log_id) throws SQLException {

        String sql = "delete from tbCFSTx  where ";

        if (requestType.equalsIgnoreCase(Constants.DEPOSIT_REQUEST)) {
            sql = sql + " dest_Account_No = '" + accountNo + "' and ";
        } else {
            sql = sql + " src_Account_No = '" + accountNo + "' and ";
        }

        sql = sql + "log_id = " + log_id + " and " +
                "TX_SRC = 'NASIM'";

        if (log.isDebugEnabled()) log.debug("sql :: " + sql);

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.deleteTxTransactional() = -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static void setTxIsReversedTransactional(Connection connection, String origTxPk, String isReverse) throws NotFoundException, SQLException {
        String sql = "update tbCFSTX set ISREVERSED =" + isReverse + " where TX_PK = '" + origTxPk + "'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.setTxIsReversedTransactional4DeleteTx() -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    //*********************Delete document******end************************

    //**********************ATM************start***********************

    public static boolean checkATMExist(String accountNo) throws SQLException {

        String sqLATM = "select ACCOUNT_NO from tbdevice where ACCOUNT_NO = '" + accountNo + "' and DEVICE_TYPE=" + Constants.ATM_TYPE + " and STATUS='1' with ur";
        Statement sqLATMStatement = null;
        ResultSet sqLATMRs = null;
        boolean flag = false;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            sqLATMStatement = connection.createStatement();
            sqLATMRs = sqLATMStatement.executeQuery(sqLATM);

            if (sqLATMRs.next())
                flag = true;
            connection.commit();

        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkATMDeviceExist() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (sqLATMRs != null) sqLATMRs.close();
                if (sqLATMStatement != null) sqLATMStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.checkATMDeviceExist() #2= -- Error:: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
        return flag;
    }

    public static long updateAccountTransactional4ATM(Connection connection, String accountNo, long amount)
            throws SQLException, NotFoundException {

        Statement blncStatement = null;
        Statement updateST = null;
        ResultSet blnc = null;

        long new_balance = 0;
        try {
            String sqlBalance = "select balance from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update";
            blncStatement = connection.createStatement();
            blnc = blncStatement.executeQuery(sqlBalance);
            if (blnc.next()) {
                long balance = blnc.getLong("balance");
                new_balance = balance + amount;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance +
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                return new_balance;
            }
            throw new NotFoundException("Exseption in  updateAccountTransactional4ATM():: Not found Account = " + accountNo);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateAccountTransactional4ATM() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (blncStatement != null) blncStatement.close();
                if (blnc != null) blnc.close();
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateAccountTransactional4ATM() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static long updateDestAccountTransactional4ATM(Connection connection, String accountNo, long amount)
            throws SQLException, NotFoundException {


        String sqlBalance = "select balance from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update";

        Statement blncStatement = null;
        Statement updateST = null;
        ResultSet blnc = null;
        long new_balance = 0;
        try {
            blncStatement = connection.createStatement();
            blnc = blncStatement.executeQuery(sqlBalance);
            if (blnc.next()) {
                long balance = blnc.getLong("balance");
                new_balance = balance + amount;
                String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance +
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                return new_balance;
            }
            throw new NotFoundException("Exseption in  updateDestAccountTransactional4ATM()#1:: Not found Account = " + accountNo);

        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateDestAccountTransactional4ATM() #2= -- Error :: " + e);
            throw e;
        } finally {

            try {
                if (blncStatement != null) blncStatement.close();
                if (blnc != null) blnc.close();
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.updateDestAccountTransactional4ATM() #5= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }
    //***************ATM******** end*********
    //*********************Stock Deposit******start************************
    public static void txnDoFTTransactionPG(Tx tx, long minBalance,long destMinBalance, HashMap extraData)
            throws NotFoundException, ModelException, ISOException, SQLException {

        String srcAccountNo = tx.getSrcAccountNo();
        String destAccountNo = tx.getDestAccountNo();
        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoFTTransaction() #1=   -- Error :: " + e);
            throw new ISOException();
        }
        long amount = tx.getAmount();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            long srcBalance = 0;
            if (extraData.get(Constants.SRC_HOST_ID).equals(Constants.CFS_HOSTID)) {
                srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, -tx.getFeeAmount(), extraData);
            }
            tx.setSrc_account_balance(srcBalance);

            long destBalance = 0;
            if (extraData.get(Constants.DEST_HOST_ID).equals(Constants.CFS_HOSTID)) {
                destBalance = updateDestAccountByTypeTransactional(connection, destAccountNo, amount, tx.getTxCode(), 0);
            }
            tx.setDest_account_balance(destBalance);

            saveTxTransactional(connection, tx);

            //if dest account is cfs, we will get wage from this account
            if (extraData.get(Constants.DEST_HOST_ID).equals(Constants.CFS_HOSTID)) {
                Tx wageTx= (Tx) extraData.get(Constants.WAGE_TX);
                long wageAmount= wageTx.getAmount();

                srcBalance = updateSrcAccountByTypeTransactional(connection, destAccountNo, -wageAmount, tx.getTxCode(), destMinBalance, 0, extraData);
                extraData.put(Constants.DEST_ACCOUNT_BALANCE,srcBalance);
                wageTx.setSrc_account_balance(srcBalance);
                saveTxTransactional(connection, wageTx);
            }
            connection.commit();
        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnDoFTTransactionPG() #1=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransactionPG() #2=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            log.debug("CFSFacadeNew.txnDoFTTransactionPG() #3=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransactionPG() #4=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoFTTransactionPG() #5=   -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoFTTransactionPG() #6=   -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }
    //*********************Stock Deposit******end************************
    //*********************SMS Group card******start************************
    public static String getCustomerCellphone(String pan) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String cellphone="00000000000";
        String sql = "SELECT CELLPHONE FROM tbcustomer INNER JOIN tbcfscard ON tbcustomer.customer_id = tbcfscard.customer_id  where tbcfscard.pan='"+pan+"' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                cellphone=resultSet.getString("CELLPHONE");
            }
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return cellphone;
    }
    //*********************SMS Group card******end************************
    //*********************PAYA******Start************************
    public static int txnDoPayaTransaction(Tx tx, HashMap extraData) throws SQLException, ISOException {

        Statement checkPayaSt = null;
        ResultSet checkPayaRs = null;
        Statement checkTbAchSt = null;
        ResultSet checkTbAchRs = null;
        Statement checkAccountSt = null;
        ResultSet checkAccountRs = null;
        Statement updateAccountSt = null;
        Statement updateBrAchSt = null;
        Statement insertSt = null;
        int result = 0;
        long amount=0;
        String xmlStr="";

        String destAccountNo = tx.getDestAccountNo();
        try {
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);
        } catch (ISOException e) {
            log.error("CFSFacadeNew.txnDoPayaTransaction()#3= -- Error :: " + e);
            throw new ISOException();
        }

        String userId = (String) extraData.get(Fields.USER_ID);
        String dueDate = (String) extraData.get(Fields.DUE_DATE);
        xmlStr = (String) extraData.get(Fields.PAYA_REQUEST);
        String branchCode = tx.getSgbBranchId();
        if (branchCode.length() < 6)
            try {
                branchCode = ISOUtil.padleft(branchCode, 6, '0');
            } catch (ISOException e) {
                log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                throw new ISOException();
            }

        if (!tx.getTxSrc().equals(CFSConstants.TXN_SRC_SGB)) {
            Connection connection = null;
            try {
                connection = dbConnectionPool.getConnection();

                String checkPayaSql = "SELECT ACHST from tbpayastatus with ur";
                checkPayaSt = connection.createStatement();
                checkPayaRs = checkPayaSt.executeQuery(checkPayaSql);
                if (checkPayaRs.next()) {
                    String status = checkPayaRs.getString(1).trim();
                    if (status.equalsIgnoreCase(CFSConstants.PAYA_IS_ACTIVE)) {

                        String checkTbAchSql = "SELECT AMOUNT from TBBRACH " +
                                "WHERE BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='" + dueDate + "' AND " +
                                "SERIAL=" + tx.getBranchDocNo() + " AND STEP=1 AND STATUS="+CFSConstants.ACTIVE_PAYA_REQUEST+" for update";
                        checkTbAchSt = connection.createStatement();
                        checkTbAchRs = checkTbAchSt.executeQuery(checkTbAchSql);
                        if (checkTbAchRs.next()) {
                           amount=checkTbAchRs.getLong("AMOUNT");
                            tx.setAmount(amount);
                            extraData.put(Fields.AMOUNT, String.valueOf(amount));
                            extraData.put(Fields.AMOUNT,String.valueOf(amount));

                            String checkAccountsql = "SELECT balance,SGB_BRANCH_ID from tbCustomerAccounts where ACCOUNT_NO = '" + destAccountNo + "' for update;";

                            checkAccountSt = connection.createStatement();
                            checkAccountRs = checkAccountSt.executeQuery(checkAccountsql);
                            if (checkAccountRs.next()) {
                                long balance = checkAccountRs.getLong("BALANCE");
                                String sgbBranchId = checkAccountRs.getString("SGB_BRANCH_ID");
                                if (sgbBranchId.equalsIgnoreCase(tx.getSgbBranchId())) {
                                    long new_balance = balance + amount;
                                    String updateSQL = "update TBCUSTOMERACCOUNTS set balance = " + new_balance + "  where account_no = '" + destAccountNo + "'";
                                    updateAccountSt = connection.createStatement();
                                    updateAccountSt.execute(updateSQL);
                                    tx.setDest_account_balance(new_balance);

                                    saveTxTransactional(connection, tx);

                                    //update tbBrAch
                                    String updatetb = "UPDATE TBBRACH SET " +
                                            "STEP = 2,EFF_DATE='" + tx.getTxOrigDate() + "',OPID=" + tx.getSgbActionCode() +
                                            ",UPDATE_TIMESTAMP =current_timestamp,UPDATE_USER ='" + userId + "', " +
                                            "TX_STRING='"+xmlStr+"' WHERE " +
                                            "BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='" + dueDate + "'" +
                                            " AND SERIAL=" + tx.getBranchDocNo();
                                    updateBrAchSt = connection.createStatement();
                                    updateBrAchSt.execute(updatetb);

                                    try {
                                        //send to Queue
                                        Connector connector = ChannelManagerEngine.getInstance().getConnector("cfs-ACH-connector");
                                        CMMessage responseMsg = new CMMessage();
                                        responseMsg.setAttribute("result", (xmlStr));
                                        responseMsg.setAttribute(Fields.SESSION_ID, tx.getSessionId());

                                        connector.sendAsync(responseMsg);
                                    } catch (Exception e) {
                                        //333,222,500
                                        connection.rollback();
                                        result=5;
                                    }
                                    connection.commit();
                                } else {
                                    //account's branch is not equal to applicant branch then send error
                                    result=4;
                                    connection.rollback();
                                }
                            } else {
                                //aacount not found in tbcustomeraccounts
                                result=3;
                                connection.rollback();
                            }
                        } else {
                            //Not Found Ach Request
                            result=2;
                            connection.rollback();
                        }
                    } else {
                        //Paya ins not Active
                        // MOVE 555 TO L-ERRCODE
                        result = 1;
                        connection.rollback();
                    }
                } else {
                    //Paya ins not Active
                    // MOVE 555 TO L-ERRCODE
                    result = 1;
                    connection.rollback();
                }
            } catch (SQLException e) {
                log.error("CFSFacadeNew.txnDoPayaTransaction()#1 = -- Error :: " + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("CFSFacadeNew.txnDoPayaTransaction()#2 = -- Error :: " + e1);
                    e1.printStackTrace();
                }
                throw e;
            } finally {
                if(checkPayaRs != null) checkPayaRs.close();
                if(checkTbAchRs != null) checkTbAchRs.close();
                if(checkAccountRs != null) checkAccountRs.close();
                if(checkPayaSt != null) checkPayaSt.close();
                if(checkTbAchSt != null) checkTbAchSt.close();
                if(checkAccountSt != null) checkAccountSt.close();
                if(updateAccountSt != null) updateAccountSt.close();
                if(updateBrAchSt != null) updateBrAchSt.close();
                if(insertSt != null) insertSt.close();
                dbConnectionPool.returnConnection(connection);
            }
        }
        return result;
    }
   public static int resendPayaRequest() throws SQLException, ISOException {

        Statement checkPayaSt = null;
        ResultSet checkPayaRs = null;
        Statement checkTbAchSt = null;
        ResultSet checkTbAchRs = null;
        Statement checkAccountSt = null;
        ResultSet checkAccountRs = null;
        Statement updateAccountSt = null;
        Statement updateBrAchSt = null;
        Statement insertSt = null;
        int result = 0;
        boolean flag = false;
        String xmlStr = "";
       String babat="";
        Connection connection = null;

        try {
            connection = dbConnectionPool.getConnection();

            String checkPayaSql = "SELECT ACHST from tbpayastatus with ur";
            checkPayaSt = connection.createStatement();
            checkPayaRs = checkPayaSt.executeQuery(checkPayaSql);
            if (checkPayaRs.next()) {
                String status = checkPayaRs.getString("ACHST").trim();
                if (status.equalsIgnoreCase(CFSConstants.PAYA_IS_ACTIVE)) {

                    String checkTbAchSql = "SELECT SERIAL,DUE_DATE,CREAT_DATE,EFF_DATE,BANK_SEND,BANK_RECV," +
                            "AMOUNT,SOURCE_IBAN,PAYMENT_CODE,DEST_IBAN,NAME_SEND," +
                            "MCODE_SEND,PCODE_SEND,NAME_RECV,DESC,OPID,MCODE_RECV,PCODE_RECV,REASON," +
                            "SHAHAB_RECV,SHAHAB_SEND,TRACK_ID,REFID,BRANCH_SEND from TBBRACH " +
                            "WHERE STEP=2 AND STATUS="+CFSConstants.ACTIVE_PAYA_REQUEST+" for update";
                    checkTbAchSt = connection.createStatement();
                    checkTbAchRs = checkTbAchSt.executeQuery(checkTbAchSql);
                    while (checkTbAchRs.next()) {
                        if (!flag)
                            flag = true;

                        int serial = checkTbAchRs.getInt("SERIAL");
                        String bankSend = checkTbAchRs.getString("BANK_SEND");
                        String bankRcv = checkTbAchRs.getString("BANK_RECV");
                        String sourceIban = checkTbAchRs.getString("SOURCE_IBAN").trim();
                        String destIban = checkTbAchRs.getString("DEST_IBAN").trim();
                        String paymentId = checkTbAchRs.getString("PAYMENT_CODE").trim();
                        String nameSend = checkTbAchRs.getString("NAME_SEND").trim();
                        String meliCode = checkTbAchRs.getString("MCODE_SEND").trim();
                        String postalCode = checkTbAchRs.getString("PCODE_SEND").trim();
                        String recName = checkTbAchRs.getString("NAME_RECV").trim();
                        String opCode = checkTbAchRs.getString("OPID").trim();
                        String amount = checkTbAchRs.getString("AMOUNT").trim();
                        int trackId = checkTbAchRs.getInt("TRACK_ID");
                        String senderShahab = checkTbAchRs.getString("SHAHAB_SEND").trim();
                        String reciverMeliCode = checkTbAchRs.getString("MCODE_RECV").trim();
                        String reciverPostalCode = checkTbAchRs.getString("PCODE_RECV").trim();
                        String reciverShahab = checkTbAchRs.getString("SHAHAB_RECV").trim();
                        String reason = checkTbAchRs.getString("REASON").trim();
                        String description = checkTbAchRs.getString("DESC").trim();
                        babat = reason.trim()+"-"+description;
                        String referenceID = checkTbAchRs.getString("REFID").trim();
                        String branchSend = checkTbAchRs.getString("BRANCH_SEND").trim();
                        String date=DateUtil.getSystemDate();
                        try {
                            xmlStr = generatePayaRequest(date,String.valueOf(serial),date,opCode,bankSend,
                                    bankRcv,amount,sourceIban,destIban,paymentId,nameSend,meliCode,postalCode,senderShahab,
                                    recName,reciverMeliCode,reciverPostalCode,reciverShahab,babat,referenceID,
                                    String.valueOf(trackId),branchSend);
                            if(xmlStr==null || xmlStr.trim().equalsIgnoreCase(""))
                                throw new Exception("XMLStr is not valid");
                        } catch (Exception e) {
                            result = 6;
                            break;
                        }

                        try {
                            //send to Queue
                            Connector connector = ChannelManagerEngine.getInstance().getConnector("cfs-ACH-connector");
                            CMMessage responseMsg = new CMMessage();
                            responseMsg.setAttribute("result", (xmlStr));
                            responseMsg.setAttribute(Fields.SESSION_ID, "");

                            connector.sendAsync(responseMsg);
                        } catch (Exception e) {
                            //333,222,500
                            result = 5;
                            break;
                        }
                    }

                    connection.commit();
                    if (!flag) {
                        //Not Found Ach Request
                        result = 2;
                    }
                } else {
                    //Paya ins not Active
                    // MOVE 555 TO L-ERRCODE
                    result = 1;
                }
            } else {
                //Paya ins not Active
                // MOVE 555 TO L-ERRCODE
                result = 1;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnDoPayaTransaction()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnDoPayaTransaction()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (checkPayaRs != null) checkPayaRs.close();
            if (checkTbAchRs != null) checkTbAchRs.close();
            if (checkAccountRs != null) checkAccountRs.close();
            if (checkPayaSt != null) checkPayaSt.close();
            if (checkTbAchSt != null) checkTbAchSt.close();
            if (checkAccountSt != null) checkAccountSt.close();
            if (updateAccountSt != null) updateAccountSt.close();
            if (updateBrAchSt != null) updateBrAchSt.close();
            if (insertSt != null) insertSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return result;
    }

    public static List<String> generatePayaRequestFromDb(String branchCode, String dueDate, String serial,
                                                   String transDate, String opCode) throws SQLException, ISOException, NotFoundException {

        Connection connection = null;
        Statement checkTbAchSt = null;
        ResultSet checkTbAchRs = null;
        List<String> list=new ArrayList<String>();
        String xmlStr = "";
        String trackingCode = "";
        String identificationType="";
        String babat="";

        try {
            connection = dbConnectionPool.getConnection();

            if (branchCode.length() < 6)
                branchCode = ISOUtil.padleft(branchCode, 6, '0');

            String checkTbAchSql = "SELECT BANK_SEND,BANK_RECV,AMOUNT,SOURCE_IBAN,PAYMENT_CODE,DEST_IBAN,NAME_SEND," +
                    "MCODE_SEND,PCODE_SEND,NAME_RECV,REFID,TRACK_ID,MCODE_RECV,PCODE_RECV,REASON,SHAHAB_RECV," +
                    "SHAHAB_SEND,DESC from TBBRACH " +
                    "WHERE BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='" + dueDate + "' AND " +
                    "SERIAL=" + serial + " AND STEP=1 AND STATUS="+CFSConstants.ACTIVE_PAYA_REQUEST+" with ur";
            checkTbAchSt = connection.createStatement();
            checkTbAchRs = checkTbAchSt.executeQuery(checkTbAchSql);
            connection.commit();
            if (checkTbAchRs.next()) {


                String bankSend = checkTbAchRs.getString("BANK_SEND");
                String bankRcv = checkTbAchRs.getString("BANK_RECV");
                String amount = checkTbAchRs.getString("AMOUNT").trim();
                String sourceIban = checkTbAchRs.getString("SOURCE_IBAN").trim();
                String destIban = checkTbAchRs.getString("DEST_IBAN").trim();
                String paymentCode = checkTbAchRs.getString("PAYMENT_CODE").trim();
                String senderName = checkTbAchRs.getString("NAME_SEND").trim();
                String senderMeliCode = checkTbAchRs.getString("MCODE_SEND").trim();
                String senderNationalCode=senderMeliCode;
                if(senderNationalCode!=null) {
                   senderNationalCode=ISOUtil.zeroUnPad(senderNationalCode.trim());
                   if(senderNationalCode.length()<10)
                       senderNationalCode=ISOUtil.padleft(senderNationalCode,10,'0');
                   senderMeliCode=senderNationalCode;
                }
                String senderPostalCode = checkTbAchRs.getString("PCODE_SEND").trim();
                String senderShahab = checkTbAchRs.getString("SHAHAB_SEND").trim();
                String reciverName = checkTbAchRs.getString("NAME_RECV".trim());
                String reciverMeliCode = checkTbAchRs.getString("MCODE_RECV").trim();
                String reciverNationalCode=reciverMeliCode;
                if(reciverNationalCode!=null) {
                    reciverNationalCode=ISOUtil.zeroUnPad(reciverNationalCode.trim());
                    if(reciverNationalCode.length()<10)
                        reciverNationalCode=ISOUtil.padleft(reciverNationalCode,10,'0');
                    reciverMeliCode=reciverNationalCode;
                }
                String reciverPostalCode = checkTbAchRs.getString("PCODE_RECV").trim();
                String reciverShahab = checkTbAchRs.getString("SHAHAB_RECV").trim();
                String reason = checkTbAchRs.getString("REASON").trim();
                String description = checkTbAchRs.getString("DESC").trim();
                babat = reason.trim()+"-"+description;
                String referenceID = checkTbAchRs.getString("REFID").trim();
                int trackId = checkTbAchRs.getInt("TRACK_ID");

                amount = ISOUtil.zeropad(amount, 18);

                trackingCode = dueDate + ISOUtil.zeropad(bankSend, 3) + "2" + ISOUtil.zeropad(String.valueOf(trackId), 6);
                identificationType="B"+branchCode+"-P01-T1";

                Element requestService = new Element("RequestService");
                requestService.setAttribute(new Attribute("type", "Process"))
                        .setAttribute(new Attribute("name", "IssuePaymentOrder"))
                        .setAttribute(new Attribute("id", "120"));
                Document doc = new Document(requestService);
                doc.setRootElement(requestService);

                Element signature = new Element("Signature");


                signature.addContent(new Element("Object")
                        .addContent(new Element("paymentOrderAgreement")
                                .addContent(new Element("referenceID").setText(ISOUtil.zeroUnPad(referenceID.trim())))

                                .addContent(new Element("requestDate").setText(dueDate.substring(0, 4) + "-" + dueDate.substring(4, 6) + "-" + dueDate.substring(6)))
                                .addContent(new Element("paymentOrder")
                                        .addContent(new Element("effectiveDate").setText(transDate.substring(0, 4) + "-" + transDate.substring(4, 6) + "-" + transDate.substring(6)))
                                        .addContent(new Element("amount").setText(ISOUtil.zeroUnPad(amount)))
                                        .addContent(new Element("description").setText(babat.trim()))
                                        .addContent(new Element("debtorAgent").addContent(new Element("bic").setText(ISOUtil.zeropad(bankSend, 3))))
                                        .addContent(new Element("debtorIBAN").addContent(new Element("value").setText(sourceIban.trim())))
                                        .addContent(new Element("debtorName").setText(senderName.trim()))
                                        .addContent(new Element("debtorPostalCode").setText(senderPostalCode.trim()))
                                        .addContent(new Element("debtorCountryCode").setText("IR"))
                                        .addContent(new Element("debtorNationalCode").setText(senderMeliCode))
                                        .addContent(new Element("debtorShahabCode").setText(senderShahab))
                                        .addContent(new Element("debtorBranchCode").setText(branchCode))
                                        .addContent(new Element("creditorAgent").addContent(new Element("bic").setText(ISOUtil.zeropad(bankRcv, 3))))
                                        .addContent(new Element("creditorIBAN").addContent(new Element("value").setText(destIban.trim())))
                                        .addContent(new Element("creditorName").setText(reciverName.trim()))
                                        .addContent(new Element("creditorPostalCode").setText(reciverPostalCode.trim()))
                                        .addContent(new Element("creditorCountryCode").setText("IR"))
                                        .addContent(new Element("creditorNationalCode").setText(reciverMeliCode.trim()))
                                        .addContent(new Element("creditorShahabCode").setText(reciverShahab.trim()))
                                        .addContent(new Element("creditorBranchCode").setText("EMPTY"))
                                        .addContent(new Element("paymentCode").setText(paymentCode.trim()))
                                        .addContent(new Element("trackingCode").setText(trackingCode))
                                        .addContent(new Element("core").addContent(new Element("processDate").setText(transDate.substring(0, 4) + "-" + transDate.substring(4, 6) + "-" + transDate.substring(6)))
                                                .addContent(new Element("additionalInformation").setText("EMPTY"))
                                                .addContent(new Element("coreTransaction").addContent(new Element("transactionType")
                                                        .setText(opCode)).addContent(new Element("actionDate")
                                                        .setText(transDate.substring(0, 4) + "-" + transDate.substring(4, 6) + "-" + transDate.substring(6))).addContent(new Element("referenceID")
                                                        .setText(serial)).
                                                        addContent(new Element("amount").setText(ISOUtil.zeroUnPad(amount)))
                                                )).addContent(new Element("identificationType").setText(identificationType))
                                )));

                doc.getRootElement().addContent(signature);

                Format format = Format.getRawFormat();
                format.setTextMode(Format.TextMode.PRESERVE);
                XMLOutputter xmlOutput = new XMLOutputter(format);

                xmlStr = xmlOutput.outputString(doc);
                list.add(0,xmlStr);
                list.add(1, String.valueOf(trackingCode));
            } else {
                //Not Found Ach Request
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.generatePayaRequest()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.generatePayaRequest()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (checkTbAchRs != null) checkTbAchRs.close();
            if (checkTbAchSt != null) checkTbAchSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return list;
    }

    public static String generatePayaRequest(String dueDate, String serial, String transDate, String opCode,
                                             String bankSend, String bankRcv, String amount, String sourceIban, String destIban,
                                             String paymentCode, String senderName, String senderMeliCode, String senderPostalCode,
                                             String senderShahab, String reciverName, String reciverMeliCode, String reciverPostalCode,
                                             String reciverShahab, String babat, String referenceID, String startTrack,String branchCode)
            throws SQLException, ISOException, NotFoundException {

        String xmlStr = "";
        String trackingCode = "";
        String identificationType="";

        amount = ISOUtil.zeropad(amount, 18);
        paymentCode = ISOUtil.zeropad(paymentCode, 12);

        trackingCode = dueDate + ISOUtil.zeropad(bankSend, 3) + "2" + ISOUtil.zeropad(String.valueOf(startTrack), 6);

        identificationType="B"+branchCode+"-P01-T1";
        String senderNationalCode=senderMeliCode;
        if(senderNationalCode!=null) {
            senderNationalCode=ISOUtil.zeroUnPad(senderNationalCode.trim());
            if(senderNationalCode.length()<10)
                senderNationalCode=ISOUtil.padleft(senderNationalCode,10,'0');
            senderMeliCode=senderNationalCode;
        }

        String reciverNationalCode=reciverMeliCode;
        if(reciverNationalCode!=null) {
            reciverNationalCode=ISOUtil.zeroUnPad(reciverNationalCode.trim());
            if(reciverNationalCode.length()<10)
                reciverNationalCode=ISOUtil.padleft(reciverNationalCode,10,'0');
            reciverMeliCode=reciverNationalCode;
        }

        Element requestService = new Element("RequestService");
        requestService.setAttribute(new Attribute("type", "Process"))
                .setAttribute(new Attribute("name", "IssuePaymentOrder"))
                .setAttribute(new Attribute("id", "120"));
        Document doc = new Document(requestService);
        doc.setRootElement(requestService);

        Element signature = new Element("Signature");


        signature.addContent(new Element("Object")
                .addContent(new Element("paymentOrderAgreement")
                        .addContent(new Element("referenceID").setText(ISOUtil.zeroUnPad(referenceID)))
                        .addContent(new Element("requestDate").setText(dueDate))
                        .addContent(new Element("paymentOrder")
                                .addContent(new Element("effectiveDate").setText(transDate))
                                .addContent(new Element("amount").setText(ISOUtil.zeroUnPad(amount)))
                                .addContent(new Element("description").setText(babat))
                                .addContent(new Element("debtorAgent").addContent(new Element("bic").setText(ISOUtil.zeropad(bankSend,3))))
                                .addContent(new Element("debtorIBAN").addContent(new Element("value").setText(sourceIban)))
                                .addContent(new Element("debtorName").setText(senderName))
                                .addContent(new Element("debtorPostalCode").setText(senderPostalCode))
                                .addContent(new Element("debtorCountryCode").setText("IR"))
                                .addContent(new Element("debtorNationalCode").setText(senderMeliCode))
                                .addContent(new Element("debtorShahabCode").setText(senderShahab))
                                .addContent(new Element("debtorBranchCode").setText(" "))
                                .addContent(new Element("creditorAgent").addContent(new Element("bic").setText(ISOUtil.zeropad(bankRcv, 3))))
                                .addContent(new Element("creditorIBAN").addContent(new Element("value").setText(destIban)))
                                .addContent(new Element("creditorName").setText(reciverName))
                                .addContent(new Element("creditorPostalCode").setText(reciverPostalCode))
                                .addContent(new Element("creditorCountryCode").setText("IR"))
                                .addContent(new Element("creditorNationalCode").setText(reciverMeliCode))
                                .addContent(new Element("creditorShahabCode").setText(reciverShahab))
                                .addContent(new Element("creditorBranchCode").setText(" "))
                                .addContent(new Element("paymentCode").setText(paymentCode))
                                .addContent(new Element("trackingCode").setText(trackingCode))
                                .addContent(new Element("core").addContent(new Element("processDate").setText(transDate))
                                        .addContent(new Element("additionalInformation").setText(" "))
                                        .addContent(new Element("coreTransaction").addContent(new Element("transactionType")
                                                .setText(opCode)).addContent(new Element("actionDate")
                                                .setText(transDate)).addContent(new Element("referenceID")
                                                .setText(serial)).
                                                addContent(new Element("amount").setText(ISOUtil.zeroUnPad(amount)))
                                        )).addContent(new Element("identificationType").setText(identificationType))
                        )));

        doc.getRootElement().addContent(signature);
        Format format = Format.getRawFormat();
        format.setTextMode(Format.TextMode.PRESERVE);
        XMLOutputter xmlOutput = new XMLOutputter(format);
        xmlStr = xmlOutput.outputString(doc);

        return xmlStr;
    }
    //*********************PAYA******end************************
    //*********************one time paid******start************************
    public static void savePayId(Connection connection, String accountNo, String id, String txSrc, String sessionId) throws ServerAuthenticationException, SQLException {

        Statement selectStatement = null;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            String sql = "select ISPAID FROM tbonetimeid where ACCOUNT_NO = '" + accountNo + "' and id = " + id + " for update";
            selectStatement = connection.createStatement();
            resultSet = selectStatement.executeQuery(sql);
            if (resultSet.next()) {
                String isPaid = resultSet.getString("ISPAID");
                if (isPaid != null && isPaid.trim().equalsIgnoreCase(Constants.ID_PAID))
                    throw new ServerAuthenticationException("");
                else {
                    //update
                    String updateSQL = "update tbonetimeid set " +
                            "ISPAID = '" + Constants.ID_PAID + "', " +
                            "TX_SRC='" + txSrc + "', " +
                            "SESSION_ID='" + sessionId + "', " +
                            "UPDATE_DATE='" + DateUtil.getSystemDate() + "', " +
                            "UPDATE_TIME='" + DateUtil.getSystemTime() + "' " +
                            "where ACCOUNT_NO = '" + accountNo + "' and id = " + id ;
                    statement = connection.createStatement();
                    statement.execute(updateSQL);
                }
            } else {
                //insert
                String insert = "insert into TBONETIMEID " +
                        "(ACCOUNT_NO,ID,TX_SRC,CREATION_DATE,CREATION_TIME,UPDATE_DATE,UPDATE_TIME,SESSION_ID,ISPAID,HOSTID) " +
                        " values('" +
                        accountNo + "', " +
                        id + ", " +
                        "'" + txSrc + "', " +
                        "'" + DateUtil.getSystemDate() + "', " +
                        "'" + DateUtil.getSystemTime() + "', " +
                        "'', " +
                        "'', " +
                        "'" + sessionId + "', " +
                        "'" + Constants.ID_PAID + "', " +
                        "'" + Constants.CFS_HOSTID + "'" +
                        ")";
                statement = connection.createStatement();
                statement.executeUpdate(insert);
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.savePayId() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (selectStatement != null) selectStatement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.savePayId() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static void reversePay(Connection connection, String accountNo, String id) throws SQLException {

        Statement statement = null;

        try {
            String updateSQL = "update tbonetimeid set " +
                    "ISPAID = '" + Constants.ID_NOT_PAID + "', " +
                    "UPDATE_DATE='" + DateUtil.getSystemDate() + "', " +
                    "UPDATE_TIME='" + DateUtil.getSystemTime() + "' " +
                    "where ACCOUNT_NO = '" + accountNo + "' and id = " + id ;
            statement = connection.createStatement();
            statement.execute(updateSQL);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.reversePay() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.reversePay() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }
    //*********************one time paid******end************************
//*********************Wage******start************************
    public static void txnGetWageTransaction(Tx tx, long minBalance,long origAmount) throws NotFoundException, ModelException, SQLException, ISOException {
        String srcAccountNo = tx.getSrcAccountNo();
        long amount = tx.getAmount();
        String messageType = tx.getTxCode();
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();

            long srcBalance = updateSrcAccountByTypeTransactional(connection, srcAccountNo, -amount, tx.getTxCode(), minBalance, 0, new HashMap());
            tx.setSrc_account_balance(srcBalance);
            saveTxTransactional(connection, tx);
            String transactionType=((tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE)) ? Constants.RTGS : Constants.ACH);
            insertFeeLog(connection,tx,transactionType,origAmount,Constants.TX_IS_NOT_REVERSAL,Constants.TX_IS_NOT_REVERSED);
            connection.commit();

        } catch (NotFoundException e) {
            log.error("CFSFacadeNew.txnGetWageTransaction()#2 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnGetWageTransaction()#3 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnGetWageTransaction()#5 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.txnGetWageTransaction()#6 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.txnGetWageTransaction()#7 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Map<Long, String> fillCMParamMap(String modifier) throws SQLException {
        if (cmParamMap.containsKey(modifier))
            return cmParamMap.get(modifier);

        String sql = "select ID, DESCRIPTION  from tbcmparams where modifier = '" + modifier + "'   with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            Map<Long, String> map = new HashMap<Long, String>();
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String desc = resultSet.getString("DESCRIPTION").trim();
                map.put(id, desc);
            }
            if (map.size() == 0) {
                log.error("*******************************************************************************");
                log.error("::: Error: There is no record in tbcmparams for modifier: '" + modifier + "'");
                log.error("*******************************************************************************");
            }
            connection.commit();
            cmParamMap.put(modifier, map);
            return map;
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
            dbConnectionPool.returnConnection(connection);
        }
    }




    public static List<String> getDescription(String txCode) throws SQLException, NotFoundException {


        List<String> description=new ArrayList<String>();
        String sql = "select SGBDESC,FULL_DESC from TBSGBCODE  where SGBCODE = '" + txCode + "'  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                description.add(0,resultSet.getString("SGBDESC"));
                description.add(1,resultSet.getString("FULL_DESC"));
            } else {
                throw new NotFoundException("Can not Find any SGBDescription where SGB_CODE = '" + txCode + "'");
            }
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return description;
    }

    public static void insertFeeLog(Connection connection, Tx tx,String transactionType,long origAmount,String isReversal,String isReversed) throws SQLException {

        String srcAccountNo=tx.getSrcAccountNo();
        String destAccountNo=tx.getDestAccountNo();

//        if(isReversal.equalsIgnoreCase(Constants.TX_IS_REVERSAL)){
//            //tx is reversal tx
//            srcAccountNo=tx.getDestAccountNo();
//            destAccountNo=tx.getSrcAccountNo();
//        }

        String sql = "Insert into TB_FEE_LOG " +
                "(SRC_ACCOUNT_NO,DEST_ACCOUNT_NO,TX_SRC,BRANCH_CODE,FEE_AMOUNT,TX_DATE,TX_TIME,CREATION_DATE,CREATION_TIME," +
                "TRANSACTION_TYPE,AMOUNT,TX_PK,SESSION_ID,IS_REVERSAL_TXN,ISREVERSED,BRANCH_DOCNO" +
                ") values(" +
                "'"+srcAccountNo + "'"+
                ",'" + destAccountNo + "'"+
                ",'"+Constants.BRANCH+"'"+
                ",'" + tx.getSgbBranchId() + "'" +
                "," + tx.getAmount() + "" +
                ",'" +tx.getTxOrigDate() + "'"+
                ",'" + tx.getTxOrigTime() + "'" +
                ",'" + tx.getCreationDate() + "'"+
                ",'" + tx.getCreationTime() + "'" +
                ",'" + transactionType + "'" +
                "," + origAmount +
                ",'" + tx.getTxPk() + "'" +
                ",'" + tx.getSessionId() + "'" +
                ",'" + isReversal + "'" +
                ",'" + isReversed + "'" +
                ",'" + tx.getBranchDocNo() + "'" +
                ")";

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertFeeLogOrig() = -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static long setFeeIsReversedTransactional(Connection connection, String txPk,String sessionId) throws NotFoundException, SQLException {

        Statement statement = null;
        Statement selectStatement = null;
        ResultSet resultSet=null;

        try {
            String selectSql="select amount,log_id from tb_fee_log where TX_PK = '" + txPk + "' and session_id='"+sessionId+"' for update";
            selectStatement = connection.createStatement();
            resultSet=selectStatement.executeQuery(selectSql);
            if(resultSet.next()){
                long log_id=resultSet.getLong("log_id");
                long amount=resultSet.getLong("amount");
                String sql = "update tb_fee_log set ISREVERSED = '"+Constants.TX_IS_REVERSED+"' where log_id = " + log_id;
                statement = connection.createStatement();
                statement.execute(sql);
                return amount;
            }else{
                return -1;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.setFeeIsReversedTransactional() -- Error :: " + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (selectStatement != null) selectStatement.close();
            if (statement != null) statement.close();
        }
    }
    //*********************Wage******end************************
    //********************CBI****** START **********************
    public static String[] findBlockNo4CBI(String blockNo, String brokerId, String providerId, String account_no) throws SQLException {
        String blckSelectSQL = " select BLOCK_NO,BLOCKAMOUNT from " + Constants.TB_BLOCK_AMOUNT +
                " where ACCOUNT_NO = '" + account_no + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "' with ur";
        String unblckSelectSQL = " select BLOCK_NO,UNBLOCKAMOUNT from " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS +
                " where  ACCOUNT_NO = '" + account_no + "' and BLOCK_NO = '" + blockNo + "' and  BROKER_ID='" + brokerId + "' and PROVIDER_ID='" + providerId + "' with ur";

        Connection con = null;
        Statement stm1 = null;
        Statement stm2 = null;
        ResultSet rs = null;
        String[] inquiry = new String[2];
        String blockAmount="";
        String unBlockAmount="";
        try {
            con = dbConnectionPool.getConnection();
            stm1 = con.createStatement();
            rs = stm1.executeQuery(blckSelectSQL);
            if (rs.next()) {
                blockAmount=rs.getString("BLOCKAMOUNT").trim();
                inquiry[0]=blockAmount;
                inquiry[1]=Constants.DUPLICATE_BLOCK_STATUS;
                con.commit();
                return inquiry;
            } else {
                stm2 = con.createStatement();
                rs = stm2.executeQuery(unblckSelectSQL);
                if (rs.next()) {
                    unBlockAmount=rs.getString("UNBLOCKAMOUNT").trim();
                    inquiry[0]=unBlockAmount;
                    inquiry[1]=Constants.DUPLICATE_UNBLOCK_STATUS;
                    con.commit();
                    return inquiry;
                }
            }
            con.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.findBlockNo4CBI() =   -- Error :: " + e);
            con.rollback();
            throw e;
        } finally {
            if (stm1 != null) stm1.close();
            if (stm2 != null) stm2.close();
            if (rs != null) rs.close();
            dbConnectionPool.returnConnection(con);
        }
        return inquiry;
    }


    public static long[] doBlockAmountTransactional4CBI(String accountNo, long minBalance, long blockAmount, String blockNo, String brokerId, String providerId,
                                                      String user, String branchId, String desc, String createDate, String createTime,String accountStatus,
                                                      String blockDate,String blockTime,String  cbiFlag, Tx tx) throws NotFoundException, ModelException, SQLException, ServerAuthenticationException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        Connection connection = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Statement insStm = null;
        long newSubsidyAmount=0;
        long newBalance=0;
        long[] result={blockAmount,0}; //0= without document 1=with document
        connection = dbConnectionPool.getConnection();
        try {
            String sql = "SELECT balance, SUBSIDY_AMOUNT,ACCOUNT_TYPE from tbCustomerAccounts " +
                    "where ACCOUNT_NO = '" + accountNo + "' and LOCK_STATUS = 1 for update";
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                String accountType=accRS.getString("ACCOUNT_TYPE");

                minBalance=getMinBalance(accountType);

                if (balance - subsidyAmount -blockAmount < minBalance ){
                        throw new ModelException("Insufficient fund");
                }else {
                    newSubsidyAmount = subsidyAmount + blockAmount;
                }

                if(cbiFlag.equals(Constants.CBI_FLAG_ORG))
                {
                    if (balance - newSubsidyAmount - tx.getAmount() >= minBalance) {
                        newBalance = balance - tx.getAmount();
                        tx.setSrc_account_balance(newBalance);
                        saveTxTransactional(connection, tx);
                        result[1]=1;
                    }else{
                        newBalance=balance;
                    }
                String updateSQL = "update TBCUSTOMERACCOUNTS set SUBSIDY_AMOUNT= " + newSubsidyAmount +",balance=" + newBalance +
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                String insertSql = "insert into " + Constants.TB_BLOCK_AMOUNT +
                        "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,BLOCKAMOUNT,CREATEDATE,CREATETIME,UPDATEDATETIME," +
                        "CHN_USER,BRANCH,DESC,TX_ORIG_DATE,TX_ORIG_TIME)" +
                        " values('" + accountNo + "','" + blockNo + "','" +
                        brokerId + "','" + providerId + "'," + blockAmount + ",'" + createDate + "','" +
                        createTime + "', timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + user + "', '" + branchId +
                        "', '" + desc + "', '" + blockDate + "', '" + blockTime + "')";
                insStm = connection.createStatement();
                insStm.executeUpdate(insertSql);
                connection.commit();
                }
            } else {
                throw new NotFoundException("Not found Account = " + accountNo);
            }
            return result;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4CBI()#1 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        }
        catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4CBI()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doBlockAmountTransactional4CBI()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4CBI()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
                if (statement != null) statement.close();
                if (updateST != null) updateST.close();
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional()4CBI #8= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String[] checkBlockStAcc4CBI(String blockNo, String brokerId, String providerId, String accountNo, String branchId, String serviceType) throws NotFoundException, ServerAuthenticationException, SQLException {
        String selectSqlBlck = "select BLOCKAMOUNT,DESC from " + Constants.TB_BLOCK_AMOUNT + " where ACCOUNT_NO='" + accountNo + "' and BLOCK_NO = '" + blockNo + "' " +
                " and BROKER_ID = '" + brokerId + "' and PROVIDER_ID = '" + providerId + "'  with ur";


        String selectSqlUnblck = "select UNBLOCKAMOUNT,DESC from " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS + " where ACCOUNT_NO='" + accountNo + "' and BLOCK_NO = '" + blockNo + "' " +
                " and BROKER_ID = '" + brokerId + "' and PROVIDER_ID = '" + providerId + "'  with ur";
        Connection connection = null;
        Statement blckSt = null;
        Statement unBlckSt = null;
        ResultSet blckRs = null;
        ResultSet unBlckRs = null;
        String[] describeAmount = new String[3];
        try {
            connection = dbConnectionPool.getConnection();
            blckSt = connection.createStatement();
            blckRs = blckSt.executeQuery(selectSqlBlck);
            if (blckRs.next()) {
                describeAmount[0] = blckRs.getString("BLOCKAMOUNT");
                describeAmount[1]=  blckRs.getString("DESC").trim();
                describeAmount[2] = Constants.NOT_DUPLICATE_BLOCK_STATUS;
                connection.commit();
                return describeAmount;
            } else {
                unBlckSt = connection.createStatement();
                unBlckRs = unBlckSt.executeQuery(selectSqlUnblck);
                if(unBlckRs.next()){
                    describeAmount[0] = unBlckRs.getString("UNBLOCKAMOUNT");
                    describeAmount[1]=  unBlckRs.getString("DESC").trim();
                    describeAmount[2] = Constants.DUPLICATE_BLOCK_STATUS;
                    connection.commit();
                    return describeAmount;
                }
                else{
                    connection.rollback();
                    throw new NotFoundException(ActionCode.NO_REALTED_BLCK_EXIST);
                }
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkBlockStAcc4CBI() = -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (blckRs != null) blckRs.close();
            if (blckSt != null) blckSt.close();
            if (unBlckRs != null) unBlckRs.close();
            if (unBlckSt != null) unBlckSt.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static long getMinBalance(String accountType) throws SQLException {
        if (minBalance.containsKey(accountType))
            return minBalance.get(accountType);

        String hql = "select MIN_BALANCE from tbAccountType where ID = '" + accountType + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                long min=resultSet.getLong("MIN_BALANCE");

                minBalance.put(accountType, min);
                return min;
            } else {
                long min=0;
                minBalance.put(accountType, min);
                return min;
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getMinBalance() =   -- Error :: " + e + " -- hql = " + hql);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);

        }
    }
    //********************CBI***** end *************************
    //****************Financial**********************
    public static void doBlockAmountTransactional4Financial(String accountNo, long minBalance, long blockAmount, String blockNo, String brokerId, String providerId,
                                                            String user, String branchId, String desc, String createDate, String createTime,String accountStatus,
                                                            String blockDate,String blockTime) throws NotFoundException, ModelException, SQLException, ServerAuthenticationException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        Connection connection = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Statement insStm = null;
        connection = dbConnectionPool.getConnection();
        try {
            String sql = "SELECT LOCK_STATUS,balance, SUBSIDY_AMOUNT,SGB_BRANCH_ID " +
                    "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {
                long balance = accRS.getLong("BALANCE");
                long subsidyAmount = accRS.getLong("SUBSIDY_AMOUNT");
                long newSubsidyAmount = subsidyAmount + blockAmount;
                int lockStatus = accRS.getInt("LOCK_STATUS");
                String updateSQL = "update TBCUSTOMERACCOUNTS set SUBSIDY_AMOUNT= " + newSubsidyAmount +
                        "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                String insertSql = "insert into " + Constants.TB_BLOCK_AMOUNT +
                        "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,BLOCKAMOUNT,CREATEDATE,CREATETIME,UPDATEDATETIME," +
                        "CHN_USER,BRANCH,DESC,TX_ORIG_DATE,TX_ORIG_TIME)"+
                        " values('" + accountNo + "','" + blockNo + "','" +
                        brokerId + "','" + providerId + "'," + blockAmount + ",'" + createDate + "','" +
                        createTime + "', timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + user + "', '" + branchId +
                        "', '" + desc + "', '" + blockDate +"', '" + blockTime+"')";
                insStm = connection.createStatement();
                insStm.executeUpdate(insertSql);
                connection.commit();
            } else {
                throw new NotFoundException("Not found Account = " + accountNo);
            }
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial()#1 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doBlockAmountTransactional4Financial()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial() #5= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial() #6= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial() #7= -- Error :: " + e1);
                e1.printStackTrace();
            }
            try {
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doBlockAmountTransactional4Financial() #8= -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void doUnBlockAmountTransactional4Financial(String accNo, String blckNo, String brokerId, String providerId,
                                                              long unBlckAmnt, String brchId, String user, String reason, String desc,
                                                              String createDate, String createTime,String blockDate,String blockTime, String unblockDesc) throws SQLException {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String tString = t.toString();
        String SQL = "delete from " + Constants.TB_BLOCK_AMOUNT + " where ACCOUNT_NO = '" + accNo.trim() + "'and BLOCK_NO = '" +
                blckNo.trim() + "' and BROKER_ID = '" + brokerId.trim() + "' and " + " PROVIDER_ID = '" + providerId.trim() + "'";
        String insertSql = "insert into " + Constants.TB_UNBLOCK_STOCK_ACCOUNTS +
                "(ACCOUNT_NO,BLOCK_NO,BROKER_ID,PROVIDER_ID,UNBLOCKAMOUNT,BRANCHID,CHN_USER,UNBLOCK_REASON,DESC,CREATEDATE," +
                "CREATETIME,UPDATEDATETIME,TX_ORIG_DATE,TX_ORIG_TIME,BLOCK_REASON)"+
                " values ('" + accNo + "','" +blckNo + "','" + brokerId + "','" + providerId + "'," + unBlckAmnt + ",'" +
                brchId + "','" + user + "','" + reason + "','" + unblockDesc + "','" + createDate + "','" + createTime +
                "',timestamp_format('" + tString.substring(0, 19) + "' , 'YYYY-MM-DD HH24:MI:SS'), '" + blockDate +
                "','" + blockTime + "','" + desc +"')";
        String subcidUpdSql = "update " + Constants.TB_CUSTOMER_ACCOUNTS + " set SUBSIDY_AMOUNT =SUBSIDY_AMOUNT- " + unBlckAmnt +
                " where ACCOUNT_NO = '" + accNo.trim() + "'";
        Connection connection = null;
        Statement stm = null;
        Statement insStm = null;
        Statement updStm = null;
        try {
            connection = dbConnectionPool.getConnection();
            stm = connection.createStatement();
            stm.executeUpdate(SQL);
            insStm = connection.createStatement();
            insStm.executeUpdate(insertSql);
            updStm = connection.createStatement();
            updStm.executeUpdate(subcidUpdSql);
            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.doUnBlockAmountTransactional4Financial() #1 -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.doUnBlockAmountTransactional4Financial()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (stm != null) stm.close();
            if (insStm != null) insStm.close();
            if (updStm != null) updStm.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //*************************************
    //*******GiftCardLimit**********start**
    private static void increaseLimitAmount(Connection connection, String identityCode, Long amount) throws SQLException, NotFoundException {
        String dateNow = DateUtil.getSystemDate();
        PreparedStatement ps = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select tx_date from tbgiftcardlimit where  identity_code = '" + identityCode + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String date = resultSet.getString("tx_date");
                if (date != null && !date.equalsIgnoreCase("") && date.equalsIgnoreCase(dateNow)) {
                    //Date in table is equal to date now
                    String increaseAmount = "update tbgiftcardlimit set AMOUNT = AMOUNT + ?  where identity_code = ? ";
                    ps = connection.prepareStatement(increaseAmount);
                    ps.setLong(1, amount);
                    ps.setString(2, identityCode);
                    ps.executeUpdate();
                } else {
                    //Date in table is equal to date now
                    String increaseAmount = "update tbgiftcardlimit set AMOUNT = ?, TX_DATE=?  where identity_code = ? ";
                    ps = connection.prepareStatement(increaseAmount);
                    ps.setLong(1, amount);
                    ps.setString(2, dateNow);
                    ps.setString(3, identityCode);
                    ps.executeUpdate();
                }
            } else {
                String insertIdentity = "insert into tbgiftcardlimit (IDENTITY_CODE,TX_DATE,AMOUNT) values (?,?,?)";

                ps = connection.prepareStatement(insertIdentity);
                ps.setString(1, identityCode);
                ps.setString(2, dateNow);
                ps.setLong(3, amount);
                ps.executeUpdate();

            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.increaseLimitAmount" + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (ps != null) ps.close();

        }
    }

    private static void decreaseLimitAmount(Connection connection, String identityCode, Long amount) throws SQLException, NotFoundException {
        String dateNow = DateUtil.getSystemDate();
        PreparedStatement ps = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select tx_date from tbgiftcardlimit where  identity_code = '" + identityCode + "' for update;";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String date = resultSet.getString("tx_date");
                if (date != null && !date.equalsIgnoreCase("") && date.equalsIgnoreCase(dateNow)) {
                    //Date in table is equal to date now
                    String increaseAmount = "update tbgiftcardlimit set AMOUNT = AMOUNT - ?  where identity_code = ? ";
                    ps = connection.prepareStatement(increaseAmount);
                    ps.setLong(1, amount);
                    ps.setString(2, identityCode);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            log.error("CFSFacadeNew.decreaseLimitAmount" + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (ps != null) ps.close();
        }

    }
    //*******GiftCardLimit**********end****

    //*******Mahcheck**********start***
    public static void insertMahcheckSMS(String accountNo, String channelType, long chequeCode,String requestType) throws SQLException {

        Connection connection = null;
        Statement insertStatement = null;
        Statement smsStatement = null;
        try {
            connection = dbConnectionPool.getConnection();

            String smsSql = "INSERT INTO TBMSGTRN (ID, TRANSACTION_TYPE, TRANSACTION_DATE," +
                    "TRANSACTION_TIME, ACCOUNT_NUMBER, AMOUNT, CHANNEL) " +
                    "VALUES (NEXT VALUE FOR TBMSGTRN_SEQ" +
                    ",'"+requestType+"'"+
                    ",'"+DateUtil.getSystemDate()+"'"+
                    ",'"+DateUtil.getSystemTime()+"'"+
                    ",'"+accountNo+"',"+
                    chequeCode+
                    ",'" + channelType + "'" +
                    ")";
            smsStatement = connection.createStatement();
            smsStatement.executeUpdate(smsSql);

            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertMahcheckSMS() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        }
        finally {
            if (insertStatement != null) insertStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //*******Mahcheck**********end****

}


