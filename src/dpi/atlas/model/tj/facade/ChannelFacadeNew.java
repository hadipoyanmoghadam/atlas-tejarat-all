package dpi.atlas.model.tj.facade;

import branch.dpi.atlas.model.tj.entity.*;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReport;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.Policy;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildInfo;
import branch.dpi.atlas.service.cm.handler.pg.wfp.WFPCharge;
import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.entity.log.Block;
import dpi.atlas.model.entity.log.RowOfBlock;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.*;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.core.TJCommand;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.UtilityFunctions;
import dpi.atlas.model.entity.log.SAFLog;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.DateUtil;
import dpi.atlas.util.Constants;
import dpi.atlas.model.entity.cms.Cmparam;

import branch.dpi.atlas.util.FarsiUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;
import java.util.Date;

import sun.jdbc.rowset.CachedRowSet;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardNoList;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.Charge;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 28, 2007
 * Time: 4:00:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelFacadeNew {
    private static Log log = LogFactory.getLog(ChannelFacadeNew.class);
    public static final short status = 0;
    static DBConnectionPoolHit dbConnectionPool;
    static ArrayList bankValidCodeslist;
    static Map cmServerByIDMap = new HashMap();
    static Map cmSourceServicesByIDMap = new HashMap();
    static Map cmServerServicesByIDMap = new HashMap();
    static Map billpaymentAccountByIDMap = new HashMap();
    static Map intermediateAccountByIDMap = new HashMap();
    static Map intermediateAccountByTypeMap = new HashMap();
    static Map cmParametersMap = new HashMap();
    static Map cmParametersByIDMap = new HashMap();
    static Map customerTemplateByIDMap = new HashMap();
    static Map batchMap = new HashMap();
    static Map opCodeMap = new HashMap();
    static Map allTemplateByIDMap = new HashMap();
    static Map customerTmplSrvTxValueMap = new HashMap();
    static Map txTypeSgbTxCodeMap = new HashMap();
    static Map deviceMap = new HashMap();
    static Map dbPoolMap = new HashMap();
    static Map<String,Map<Long,String>> cmParamMap = new HashMap<String, Map<Long,String>>();
    static Map branchMap = new HashMap();
    static Map<String,List<String>> descriptionMap = new HashMap<String,List<String>>();

    static Map txTypeSPWChannelCodeMap = new HashMap<String, String>();

    static Map imdMap = new HashMap();
    static Map manzumeBranchCodeMap = new HashMap();
    static Map virtualAccMap = new HashMap();
    static Map manzumeLongTermAccGroupMap = new HashMap();
    static List iranInsuranceAccountList = new ArrayList();
    private static final String datasourceName = "tj-CM-Datasource";

    public static Map<String, Map<Long, String>> getCmParamMap() {
        return cmParamMap;
    }

    public static Map<String, List<String>> getDescriptionMap() {
        return descriptionMap;
    }

    public static Map getCmParametersByIDMap() {
        return cmParametersByIDMap;
    }

    public static Map getBranchMap() {
        return branchMap;
    }
    public static Map getManzumeBranchCodeMap() {
        return manzumeBranchCodeMap;
    }

    public static Map getVirtualAccMap() {
        return virtualAccMap;
    }

    public static Map getManzumeLongTermAccGroupMap() {
        return manzumeLongTermAccGroupMap;
    }
    public static String getDBPoolHandlerNames() {
        Iterator iterator = dbPoolMap.keySet().iterator();
        String ret = "";
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            ret += key + " size = " + ((LinkedList) dbPoolMap.get(key)).size();
            if (iterator.hasNext())
                ret += "\n";
        }
        return ret;
    }

    public static synchronized void initialize(int initialPoolSize, int maxPoolSize) throws Exception {

        if (dbConnectionPool != null)
            return;
        dbConnectionPool = new DBConnectionPoolHit(initialPoolSize, maxPoolSize, datasourceName, initialPoolSize);
    }

    public static synchronized void initialize(int initialPoolSize, int maxPoolSize, int optimalPoolSize) throws Exception {

        if (dbConnectionPool != null)
            return;
        dbConnectionPool = new DBConnectionPoolHit(initialPoolSize, maxPoolSize, datasourceName, optimalPoolSize);
        new UtilityFunctions().initialConnectionPool(dbConnectionPool);

    }

    public static DBConnectionPoolHit getDbConnectionPool() {
        return dbConnectionPool;
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

    public static Map getIntermediateAccountByIDMap() {
        return intermediateAccountByIDMap;
    }

    public static Map getIntermediateAccountByTypeMap() {
        return intermediateAccountByTypeMap;
    }

    public static Map getCustomerTmplSrvTxValueMap() {
        return customerTmplSrvTxValueMap;
    }

    public static Map getImdMap() {
        return imdMap;
    }

    public static Map getTxTypeSgbTxCodeMap() {
        return txTypeSgbTxCodeMap;
    }

    public static Map getTxTypeSPWChannelCodeMap() {
        return txTypeSPWChannelCodeMap;
    }

    public static Map getDeviceMap() {
        return deviceMap;
    }

    public static Map getCustomerTemplateByIDMap() {
        return customerTemplateByIDMap;
    }

    public static Map getCmServerServicesByIDMap() {
        return cmServerServicesByIDMap;
    }

    public static Map getOpCodeMap() {
        return opCodeMap;
    }

    public static Map getAllTemplateByIDMap() {
        return allTemplateByIDMap;
    }

    public static Map getBillpaymentAccountByIDMap() {
        return billpaymentAccountByIDMap;
    }

    public static Map getCmSourceServicesByIDMap() {
        return cmSourceServicesByIDMap;
    }

    public static ArrayList getBankValidCodeslist() {
        return bankValidCodeslist;
    }

    private static int getPartNo() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        return part;

    }

    public static long findAndUpdateSequence(String name, long incrementValue) throws Exception {
        long seq = 0;
        String selectSql = "";

        Statement selectStatement = null;
        Statement updateStatement = null;
        String updateSql;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            if (dbConnectionPool == null)
                dbConnectionPool = new DBConnectionPoolHit(1, 200, "findAndUpdateSequence-Datasource", 50);


            connection = dbConnectionPool.getConnection();
            selectStatement = connection.createStatement();
            selectSql = "select Sequencer from tbSequence where name = '" + name + "' for update";
            resultSet = selectStatement.executeQuery(selectSql);
            seq = 0;
            if (resultSet.next()) {
                seq = resultSet.getLong(1);
            } else {
                log.error("Can not find name = " + name + " in tbSequence.");
                throw new SQLException("Can not find name = " + name + " in tbSequence.");
            }

            updateSql = "update tbSequence set SEQUENCER = " + (seq + incrementValue) + "  where name = '" + name + "' ";
            updateStatement = connection.createStatement();
            long t1 = System.currentTimeMillis();
            updateStatement.execute(updateSql);
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 100)
                log.info("**duration time in findAndUpdateSequence " + (t2 - t1));
            connection.commit();

        } catch (SQLException e) {
            log.error("Error:: in findAndUpdateSequence  -- selectSql  = " + selectSql + " Error = " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (selectStatement != null) selectStatement.close();
            if (updateStatement != null) updateStatement.close();
            dbConnectionPool.returnConnection(connection);
        }

        return seq;
    }

    public static long findAndUpdateCMCounter(String name, String days_str, long incrementValue) throws NotFoundException, SQLException {
        long counter = 0;
        String selectSql;
        ResultSet resultSet1 = null;
        Statement statement1 = null;
        Statement statement2 = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            selectSql = "select counter from tbCMCounter where name = '" + name + "' and days = '" + days_str + "' for update";

            statement1 = connection.createStatement();
            long t1 = System.currentTimeMillis();
            resultSet1 = statement1.executeQuery(selectSql);
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 100)
                log.info("**duration time in findAndupdateCMCounter1 " + (t2 - t1));
            counter = 0;
            if (resultSet1.next()) {
                counter = resultSet1.getLong(1);
            } else {
                if (connection != null) connection.rollback();
                throw new NotFoundException("Can not find name = " + name + " in tbSequence.");
            }

            String updateSql = "update tbCMCounter set counter = " + (counter + incrementValue) + "  where name = '" + name + "' and days = '" + days_str + "'";
            statement2 = connection.createStatement();
            long t3 = System.currentTimeMillis();
            statement2.executeUpdate(updateSql);
            long t4 = System.currentTimeMillis();
            if (t4 - t3 > 100)
                log.info("**duration time in findAndupdateCMCounter2 " + (t2 - t1));

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e.getMessage());
            throw e;
        } finally {
            if (resultSet1 != null) resultSet1.close();
            if (statement1 != null) statement1.close();
            if (statement2 != null) statement2.close();
            dbConnectionPool.returnConnection(connection);
        }

        return counter;
    }

    public static Map getCmServerByIDMap() {
        return cmServerByIDMap;
    }

    public static String getBillAccount(String companyCode, String utilityTypeCode) throws NotFoundException, SQLException {

        if (billpaymentAccountByIDMap.containsKey("" + utilityTypeCode + companyCode))
            return (String) billpaymentAccountByIDMap.get("" + utilityTypeCode + companyCode);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String hql = "select account_no , ACTIVE_DATE , IsEnabled from tbUtilityBranches " +
                " where utility_brn_id =" + companyCode +
                " and utility_id =" + utilityTypeCode + " with ur";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            long t1 = System.currentTimeMillis();
            resultSet = statement.executeQuery(hql);
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 200)
                log.info("**duration time in getBillAccount " + (t2 - t1));
            connection.commit();
            if (resultSet.next()) {
                String currentDate = DateUtil.getSystemDate();

                String account_no = resultSet.getString(1);
                String active_date = resultSet.getString(2);
                if ((active_date != null) && (!active_date.equalsIgnoreCase(""))) {
                    if (currentDate.compareTo(active_date) < 0) {
                        throw new NotFoundException("Account_no for utilityTypeCode: " + utilityTypeCode + " and companyCode: " + companyCode + " will be activated at DATE :" + active_date);
                    }
                }

                int isEnabled = resultSet.getInt(3);
                if (isEnabled == 0) {
                    throw new NotFoundException("Account_no for utilityTypeCode: " + utilityTypeCode + " and companyCode: " + companyCode + " is disabled");
                }

                billpaymentAccountByIDMap.put("" + utilityTypeCode + companyCode, account_no);
                return account_no;
            } else {
                throw new NotFoundException("No account_no exist for utilityTypeCode: " + utilityTypeCode + " and companyCode: " + companyCode);
            }
        } catch (SQLException e) {
            log.error("Error = " + e + " -- hql = " + hql);
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

    public static Batch getBatchPk(Integer batch_status) throws SQLException, NotFoundException {

        if (Constants.ACTIVE_BATCH == batch_status.intValue())
            if (batchMap.containsKey(batch_status))
                return (Batch) batchMap.get(batch_status);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from tbCFSBatch where batch_status = " + batch_status +
                    "  and SGB_STATUS = " + batch_status + " and  FARA_STATUS= " + batch_status + " with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Timestamp closeTime = resultSet.getTimestamp("BATCH_CLOSE_DATE");
                Date batchCloseDate = null;
                if (closeTime != null)
                    batchCloseDate = new Date(closeTime.getTime());
                Date batchOpenDate = new Date(resultSet.getTimestamp("BATCH_OPEN_DATE").getTime());

                Batch batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS"));

                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));

                if (CFSConstants.ACTIVE_BATCH == batch_status.intValue())
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
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static Batch getBatch(Integer batch_status, String operator) throws SQLException, NotFoundException {
        Batch batch;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from tbCFSBatch where batch_status = " + batch_status + " " +
                    operator + " SGB_STATUS = " + batch_status + " " + operator + " FARA_STATUS= " +
                    batch_status + " with ur";
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
                batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS"));

                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
                return batch;
            }
            throw new NotFoundException();
        } catch (SQLException e) {
            log.error("getBatch - Error = " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Batch getBatchFromCash(Integer batch_status, String operator) throws SQLException, NotFoundException {
//        if (Constants.ACTIVE_BATCH == batch_status.intValue())
//            if (batchMap.containsKey(batch_status))
//                return (Batch) batchMap.get(batch_status);

        Batch batch;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from tbCFSBatch " +
                    " where batch_status = " + batch_status + " " + operator + " SGB_STATUS = " + batch_status + " " + operator + " FARA_STATUS= " + batch_status + " with ur";

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
                batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS"));

                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
//                if (Constants.ACTIVE_BATCH == batch_status.intValue())
//                    batchMap.put(batch_status, batch);
                return batch;
            }
            throw new NotFoundException();
        } catch (SQLException e) {
            log.error("getBatch - Error = " + e);
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

    public static Batch getBatch(Integer batch_status, String operator, boolean getFromCash) throws SQLException, NotFoundException {
        if (getFromCash)
            return getBatchFromCash(batch_status, operator);
        else
            return getBatch(batch_status, operator);

    }

    public static Batch txnCutOverJob() throws Exception {
        Batch batch = new Batch();
        Batch preBatch = null;

        try {
            preBatch = getBatchPk(new Integer(Constants.ACTIVE_BATCH));
        } catch (NotFoundException e) {
        }

        if (preBatch != null) {
            preBatch.setCfsStatus(CFSConstants.CUT_OVER_BATCH);
            preBatch.setSgbStatus(CFSConstants.CUT_OVER_BATCH);
            preBatch.setFaraStatus(CFSConstants.CUT_OVER_BATCH);

            preBatch.setBatchCloseDate(new java.sql.Timestamp(System.currentTimeMillis()));
            batch.setBatchPk(new Long(preBatch.getBatchPk().longValue() + 1));
            updateBatch(preBatch);
        } else {
            try {
                preBatch = getLastClosedBatch();
                batch.setBatchPk(new Long(preBatch.getBatchPk().longValue() + 1));
            } catch (NotFoundException e) {
                batch.setBatchPk(new Long(1));
            } catch (SQLException e) {
            }
        }

        batch.setBatchType((Constants.BACTH_TYPE_CR2_TRANS));
        batch.setBatchOpenDate(new java.sql.Timestamp(System.currentTimeMillis()));
        batch.setCfsStatus((CFSConstants.ACTIVE_BATCH)); //0: Active 1:CutOver 2:Closed
        batch.setSgbStatus((CFSConstants.ACTIVE_BATCH)); //0: Active 1:CutOver 2:Closed
        batch.setFaraStatus((CFSConstants.ACTIVE_BATCH)); //0: Active 1:CutOver 2:Closed

        putBatch(batch);

        return batch;
    }

    private static void putBatch(Batch batch) throws SQLException {

        if ((Constants.ACTIVE_BATCH == batch.getCfsStatus()) && (Constants.ACTIVE_BATCH == batch.getSgbStatus()) && (Constants.ACTIVE_BATCH == batch.getFaraStatus()))
            setStaticBatch(batch);


        String sql = "insert into tbCFSBatch " +
                "(BATCH_PK,BATCH_TYPE,BATCH_OPEN_DATE,BATCH_STATUS,SGB_STATUS,FARA_STATUS) values(?,?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, batch.getBatchPk().longValue());
            statement.setInt(2, batch.getBatchType());
            statement.setTimestamp(3, new Timestamp(batch.getBatchOpenDate().getTime()));
            statement.setInt(4, batch.getCfsStatus());
            statement.setInt(5, batch.getSgbStatus());
            statement.setInt(6, batch.getFaraStatus());
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

    public static void setStaticBatch(Batch batch) {
        Integer key = new Integer(batch.getCfsStatus() + batch.getSgbStatus() + batch.getFaraStatus());
        if (batchMap.containsKey(key)) {
            Batch pre_Batch = (Batch) batchMap.remove(key);
            log.error("pre_Batch = " + pre_Batch);
        }
        log.error("batch = " + batch);
        batchMap.put(key, batch);
    }

    private static Batch getLastClosedBatch() throws NotFoundException, SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select * from tbCFSBatch order by batch_pk desc with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                Timestamp closeTime = resultSet.getTimestamp("BATCH_CLOSE_DATE");
                Date batchCloseDate = null;
                if (closeTime != null)
                    batchCloseDate = new Date(closeTime.getTime());
                Date batchOpenDate = new Date(resultSet.getTimestamp("BATCH_OPEN_DATE").getTime());
                Batch batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS")
                );
                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
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
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);

        }
    }

    private static void updateBatch(Batch pre_batch) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            String sql = "update tbCFSBatch set BATCH_STATUS = ?, SGB_STATUS = ?, FARA_STATUS = ?, BATCH_CLOSE_DATE = ? where BATCH_PK = ? ";
            connection = dbConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, pre_batch.getCfsStatus());
            statement.setInt(2, pre_batch.getSgbStatus());
            statement.setInt(3, pre_batch.getFaraStatus());
            statement.setTimestamp(4, new Timestamp(pre_batch.getBatchCloseDate().getTime()));
            statement.setLong(5, pre_batch.getBatchPk().longValue());
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

    public static void initializeBillPaymentTx(String billId, String paymentId, String channelType, String refNode, String actionCode, String sessionId) throws SQLException {
        ResultSet resultSet1 = null;
        Statement statement1 = null;
        Statement statement2 = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String selectSql = "select BILLID from TBBillPaymentTx where BILLID=" + billId + " and PAYMENTID ="
                    + paymentId + " for update ";
            statement1 = connection.createStatement();
            resultSet1 = statement1.executeQuery(selectSql);
            String updateSql;
            if (resultSet1.next()) {
                updateSql = "update TBBillPaymentTx set actionCode ='" + actionCode + "' , channelType='" + channelType + "' ," +
                        " refNo='" + refNode + "' , PARTNO=" + getPartNo() + " , SESSION_ID='" + sessionId + "' ," +
                        " TX_DATETIME = current_timestamp  where BILLID =" + billId + " and PAYMENTID =" + paymentId;
            } else {
                updateSql = "insert into TBBillPaymentTx(BILLID,PAYMENTID,CHANNELTYPE,refNo,ACTIONCODE,PARTNO,SESSION_ID,TX_DATETIME)" +
                        " values(" + billId + "," + paymentId + ",'" + channelType/*.substring(0,3)*/ + "','" + refNode + "','" +
                        ActionCode.TRANSACTION_PENDING + "'," + getPartNo() + ",'" + sessionId + "',current_timestamp )";
            }

            statement2 = connection.createStatement();
            statement2.executeUpdate(updateSql);
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e);
            throw e;
        } finally {
            if (resultSet1 != null) resultSet1.close();
            if (statement1 != null) statement1.close();
            if (statement2 != null) statement2.close();
            dbConnectionPool.returnConnection(connection);
        }


    }

    public static boolean checkBillPaymentExists(String
                                                         billId, String
                                                         paymentId, String
                                                         actionCode) throws SQLException {
        String hql;
        if (actionCode == null) {
            hql = "select BILLID from TBBillPaymentTx where BILLID=" + billId + " and PAYMENTID =" + paymentId + " with ur";
        } else {
            hql = "select BILLID from TBBillPaymentTx where BILLID=" + billId + " and PAYMENTID =" + paymentId + " and ( ACTIONCODE ='" + actionCode + "' or ACTIONCODE ='" + ActionCode.TRANSACTION_PENDING + "') with ur";
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
//            long t1 = System.currentTimeMillis();
            resultSet = statement.executeQuery(hql);
            connection.commit();
//            long t2 = System.currentTimeMillis();
//            if (t2 - t1 > 200)
//                log.info("**duration time in checkBillPaymentExists " + (t2 - t1));
            return resultSet.next();
        } catch (SQLException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void updateBillPayment(String billId, String paymentId, String actionCode, String sessionId) throws SQLException {


        String sql = "update TBBillPaymentTx set actionCode ='" + actionCode + "', TX_DATETIME = current_timestamp " +
                " where BILLID =" + billId + " and PAYMENTID =" + paymentId + " and actionCode ='" + ActionCode.TRANSACTION_PENDING + "' and session_id='" + sessionId + "'";

        executeUpdate(sql);
    }

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

    public static Vector getCMParam(String modifier) throws SQLException, NotFoundException {
        if (cmParametersMap.containsKey("" + modifier))
            return (Vector) cmParametersMap.get("" + modifier);

        String sql;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            sql = "select ID, DESCRIPTION  from TBCMPARAMS where MODIFIER = '" + modifier + "'  with ur";
            Vector param = new Vector();
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Cmparam cmparam = new Cmparam();
                cmparam.setId(Integer.valueOf(resultSet.getString("ID")));
                cmparam.setDescription(resultSet.getString("DESCRIPTION"));
                param.addElement(cmparam);
            }
            cmParametersMap.put("" + modifier, param);
            connection.commit();
            return param;
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


    public static Map findAccountHost(String srcAccountNo, String destAccountNo) throws NotFoundException, SQLException {
        Map accounts = new HashMap();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String Sql = "select account_No,HOST_id,STATUS,account_group,account_nature" +
                ",SMSNotification,statusMelli,NATIONAL_CODE,E_STATUS, SERVICE_STATUS,STATUSD  " +
                " from tbcustomersrv  where tbcustomersrv.account_No = '" + srcAccountNo +
                "'  or tbcustomersrv.account_No = '" + destAccountNo + "'  with ur";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(Sql);
            while (resultSet.next()) {
                CustomerServiceNew customerService = new CustomerServiceNew();
                customerService.setAccountNo(resultSet.getString("account_No").trim());
                customerService.setAccountGroup(resultSet.getString("account_group"));
                customerService.setHostId(Integer.parseInt(resultSet.getString("HOST_id").trim()));
                customerService.setStatus(Integer.parseInt(resultSet.getString("STATUS").trim()));
                customerService.setAccountNature(resultSet.getInt("account_nature"));
                customerService.setSmsNotification(resultSet.getString("SMSNotification"));
                customerService.setStatusMelli(resultSet.getByte("statusMelli"));
                customerService.setNationalCode(resultSet.getString("NATIONAL_CODE"));
                customerService.seteStatus(resultSet.getString("E_STATUS"));
                customerService.setServiceStatus(resultSet.getString("SERVICE_STATUS"));
                customerService.setStatusD(resultSet.getString("STATUSD"));
                if (srcAccountNo.equals(resultSet.getString("account_No").trim()))
                    accounts.put(Constants.ACCOUNT_DATA, customerService);
                else
                    accounts.put(Constants.DEST_ACCOUNT_DATA, customerService);
            }
            connection.commit();

            if (accounts.size() < 2) {
                throw new NotFoundException("Customer with Account No = " + srcAccountNo + " or Account No = " + destAccountNo + "  does not exist");
            }
            return accounts;
        } catch (SQLException e) {
            log.error("Error = " + e + " -- sql = " + Sql);
            connection.rollback();
            throw e;
        } catch (NumberFormatException e) {
            log.error("Error = " + e + " -- sql = " + Sql);
            connection.rollback();
            throw e;
        } catch (NotFoundException e) {
//            log.error("Error = " + e + " -- sql = " + Sql);
            log.debug("Error = " + e + " -- sql = " + Sql);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateCardAcc(String tableName, long amnt, String accNo, String pan, String fieldNameAmount, String fieldNameDate) throws SQLException {
        ResultSet resultSet1 = null;
        Statement statement1 = null;
        Statement statement2 = null;
        Connection connection = null;
        try {
            String readSql = "SELECT  " + fieldNameAmount + ", " + fieldNameDate + "  from " + tableName + " WHERE account_no = '" + accNo + "' AND pan = '" + pan + "' for update ";
            connection = dbConnectionPool.getConnection();
            statement1 = connection.createStatement();
            resultSet1 = statement1.executeQuery(readSql);

            if (resultSet1.next()) {
                long withdraw_amount = resultSet1.getLong(fieldNameAmount);
                String withdraw_date = resultSet1.getString(fieldNameDate);
                String currentDate = DateUtil.getSystemDate();
                long totalWithdrawAmount = amnt;

                if (withdraw_date == null) {
                    withdraw_date = "00000000";
                }

                if (currentDate.equalsIgnoreCase(withdraw_date)) {
                    totalWithdrawAmount += withdraw_amount;
                }

                if (totalWithdrawAmount < 0)
                    totalWithdrawAmount = 0;

                String updateSQL = " UPDATE " + tableName + " SET " + fieldNameAmount + " = " + totalWithdrawAmount +
                        " ,  " + fieldNameDate + " = '" + currentDate + "' WHERE account_no = '" + accNo + "' AND pan = '" + pan + "'";

                statement2 = connection.createStatement();
                statement2.executeUpdate(updateSQL);

            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e);
            throw e;
        } finally {
            if (resultSet1 != null) resultSet1.close();
            if (statement1 != null) statement1.close();
            if (statement2 != null) statement2.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<Long> findCardCurrentWithdrawAmount(String PAN, String tableName) throws SQLException {

        ArrayList withdrawAmntSet = new ArrayList<Long>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String withdraw_date;
        long withdrawAmnt = 0;
        long withdrawAmntSTB = 0;

        String sql = "SELECT  WITHDRAW_DATE,WITHDRAW_AMOUNT,STBWITHDRAW_DATE,STBWITHDRAW_AMOUNT  FROM " + tableName + " WHERE pan = '" + PAN + "' WITH UR";
        String currentDate = DateUtil.getSystemDate();

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {

                withdraw_date = resultSet.getString("WITHDRAW_DATE");
                if (withdraw_date != null && withdraw_date.equalsIgnoreCase(currentDate))
                    withdrawAmnt += resultSet.getLong("WITHDRAW_AMOUNT");

                withdraw_date = resultSet.getString("STBWITHDRAW_DATE");
                if (withdraw_date != null && withdraw_date.equalsIgnoreCase(currentDate))
                    withdrawAmntSTB += resultSet.getLong("STBWITHDRAW_AMOUNT");
            }
            if (withdrawAmnt < 0)
                withdrawAmnt = 0;
            if (withdrawAmntSTB < 0)
                withdrawAmntSTB = 0;

            connection.commit();
            withdrawAmntSet.add(withdrawAmnt);
            withdrawAmntSet.add(withdrawAmntSTB);
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

        return withdrawAmntSet;
    }

    public static long findCardCurrentWithdrawAmount(String PAN, String fieldNameAmount, String fieldNameDate) throws SQLException {

        String CFSaccSQL = "SELECT " + fieldNameAmount + " FROM " + CardAccount.CFS_CARD_ACCOUNTS_TABLE_NAME;
        String nonCFSaccSQL = "SELECT " + fieldNameAmount + " FROM " + CardAccount.NON_CFS_CARD_ACCOUNTS_TABLE_NAME;
        String condition = " WHERE pan = '" + PAN + "'";

        String currentDate = DateUtil.getSystemDate();
        if (currentDate != null)
            condition += " AND " + fieldNameDate + " = '" + currentDate + "' ";

        Connection connection = null;
        Statement statement1 = null;
        ResultSet resultSet1 = null;
        Statement statement2 = null;
        ResultSet resultSet2 = null;

        long nonCFSwithdrawAmntTotal = 0;
        long CFSwithdrawAmntTotal = 0;
        try {
            connection = dbConnectionPool.getConnection();

            nonCFSaccSQL = nonCFSaccSQL + condition + " WITH UR";
            statement1 = connection.createStatement();
            resultSet1 = statement1.executeQuery(nonCFSaccSQL);


            while (resultSet1.next()) {
                long nonCFSwithdrawAmnt = resultSet1.getLong(fieldNameAmount);
                nonCFSwithdrawAmntTotal += nonCFSwithdrawAmnt;
            }

            CFSaccSQL = CFSaccSQL + condition + " WITH UR";
            statement2 = connection.createStatement();
            resultSet2 = statement2.executeQuery(CFSaccSQL);

            while (resultSet2.next()) {
                long CFSwithdrawAmnt = resultSet2.getLong(fieldNameAmount);
                CFSwithdrawAmntTotal += CFSwithdrawAmnt;
            }
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
            if (resultSet1 != null) resultSet1.close();
            if (statement1 != null) statement1.close();
            if (resultSet2 != null) resultSet2.close();
            if (statement2 != null) statement2.close();
            dbConnectionPool.returnConnection(connection);
        }

        return (nonCFSwithdrawAmntTotal + CFSwithdrawAmntTotal);
    }

    public static Card getCard(String cardNo) throws NotFoundException, SQLException {
        String hql = "select CUSTOMER_ID,ORIG_EDIT_DATE,SEQUENCE_NO,CREATION_DATE,CREATION_TIME,TEMPLATE_ID from tbCFSCard where PAN = '" + cardNo + "' with ur";

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
                card.setTemplateID(resultSet.getInt("TEMPLATE_ID"));
                card.setCustomerID(resultSet.getString("CUSTOMER_ID"));
                card.setOrigEditDate(resultSet.getString("ORIG_EDIT_DATE"));
                return card;
            } else {
                throw new NotFoundException("No card exist for card no " + cardNo);
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getCard() =   -- Error :: " + e + " -- hql = " + hql);
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


    public static String getTxTypeSgbTxCode
            (String txCode) throws SQLException, NotFoundException {
        if (txTypeSgbTxCodeMap.containsKey(txCode))
            return (String) txTypeSgbTxCodeMap.get(txCode);

        String sql = "select SGB_TX_CODE from TBCFSTXTYPE where TX_CODE = '" + txCode + "'  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String sgb_tx_code = resultSet.getString("SGB_TX_CODE");
                txTypeSgbTxCodeMap.put(txCode, sgb_tx_code);
                return sgb_tx_code;
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

    public static void insertCMCounterPlusBlockSize(CMCounter cmCounter, int block_size) throws SQLException {
        String insertSql = "insert into tbCMCounter (name,days,counter) values('"
                + cmCounter.getName() + "','" + cmCounter.getDays() + "'," + (cmCounter.getCounter().intValue() + block_size) + ")";
        Statement insertStatement = null;
        Connection connection = null;

        log.debug("5");
        try {
            connection = dbConnectionPool.getConnection();
            insertStatement = connection.createStatement();
            insertStatement.execute(insertSql);
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } finally {
            if (insertStatement != null) insertStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
        log.debug("6");
    }

    public static CustomerServiceNew findCustomerAccountSrv(String accountNo) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select customer_id, HOST_id, LANG, PIN, STATUS, TEMPLATE_id, TPIN," +
                    " account_group, account_nature, SmsNotification, statusMelli, NATIONAL_CODE, E_STATUS, " +
                    " SERVICE_STATUS,STATUSD from tbcustomersrv  where tbcustomersrv.account_No = '" + accountNo + "'   with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();

            CustomerServiceNew customerService;
            if (resultSet.next()) {
                customerService = new CustomerServiceNew();
                customerService.setAccountNo(accountNo);
                customerService.setCustomerId(resultSet.getString("customer_id"));
                customerService.setHostId(Integer.parseInt(resultSet.getString("HOST_id")));
                customerService.setLang(Long.parseLong(resultSet.getString("LANG")));
                customerService.setPin(resultSet.getString("PIN"));
                customerService.setStatus(Integer.parseInt(resultSet.getString("STATUS")));
                customerService.setTemplateId(Long.parseLong(resultSet.getString("TEMPLATE_id")));
                customerService.settPin(resultSet.getString("TPIN"));
                customerService.setAccountGroup(resultSet.getString("account_group"));
                customerService.setAccountNature(resultSet.getInt("account_nature"));
                customerService.setSmsNotification(resultSet.getString("SMSNotification"));
                customerService.setStatusMelli(resultSet.getByte("statusMelli"));
                customerService.setNationalCode(resultSet.getString("NATIONAL_CODE"));
                customerService.seteStatus(resultSet.getString("E_STATUS"));
                customerService.setServiceStatus(resultSet.getString("SERVICE_STATUS"));
                customerService.setStatusD(resultSet.getString("STATUSD"));
            } else {
                throw new NotFoundException("Customer with Account No = " + accountNo + " does not exist");
            }
            return customerService;
        } catch (SQLException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } catch (NumberFormatException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } catch (NotFoundException e) {
            log.debug(e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static IntermediateAccount findIntermediateAccount(String channelId, int hostId, int destHostId, String channelTypeCode) throws NotFoundException, SQLException {
        if (intermediateAccountByTypeMap.containsKey("" + channelId + hostId + destHostId + channelTypeCode))
            return (IntermediateAccount) intermediateAccountByTypeMap.get("" + channelId + hostId + destHostId + channelTypeCode);

        String Sql = "select ACCOUNT_NO from TBIntermediateAcc  where channel_id = '" + channelId + "' and host_id=" + hostId + " and Desthostid=" + destHostId + " and channel_type_code= '" + channelTypeCode + "'  with ur";
        IntermediateAccount intermediateAccount;
        ResultSet resultSet = executeQuery(Sql);
        if (resultSet.next()) {
            intermediateAccount = new IntermediateAccount();
            intermediateAccount.setAccountNo(resultSet.getString("ACCOUNT_NO"));
            intermediateAccount.setChannelId(channelId);
            intermediateAccount.setHostID(hostId);
            intermediateAccount.setDestHostID(destHostId);
            resultSet.close();
            intermediateAccountByTypeMap.put("" + channelId + hostId + destHostId + channelTypeCode, intermediateAccount);
            return intermediateAccount;
        } else {
            throw new NotFoundException("Intermediate Account For Channel Id = " + channelId + " and channel Type =" + channelTypeCode + " and host Id =" + hostId + "  does not exist");
        }
    }

    public static CustomerTmplChnn getCustomerTemplateServer(int templateId, int serverId) throws NotFoundException, SQLException {
        if (customerTemplateByIDMap.containsKey("" + templateId + serverId))
            return (CustomerTmplChnn) customerTemplateByIDMap.get("" + templateId + serverId);

        CustomerTmplChnn customerTmplChnn;
        try {
            String selectSQL = " select SERVICES,MAXLIMIT,MINLIMIT from TBCUSTOMERTMPLCHN " +
                    " customerTmplSrv where customerTmplSrv.template_ID = " + templateId +
                    " and customerTmplSrv.server_ID = " + serverId + "  with ur";
            log.debug("selectSQL in getCustomerTemplateServer is:" + selectSQL);
            ResultSet resultSet = executeQuery(selectSQL);
            if (resultSet.next()) {
                customerTmplChnn = new CustomerTmplChnn();
                customerTmplChnn.setTemplateID(templateId);
                customerTmplChnn.setServiceID(serverId);
                customerTmplChnn.setServices(Integer.parseInt(resultSet.getString("SERVICES")));
                customerTmplChnn.setMaxLimit(Long.parseLong(resultSet.getString("MAXLIMIT")));
                customerTmplChnn.setMinLimit(Long.parseLong(resultSet.getString("MINLIMIT")));
                resultSet.close();
                customerTemplateByIDMap.put("" + templateId + serverId, customerTmplChnn);
            } else
                throw new NotFoundException("Server with ID = " + serverId + " not assigned to customer template with ID = " + templateId);
        } catch (NumberFormatException e) {
            log.error(e);
            throw e;
        }
        return customerTmplChnn;

    }

    public static void insertCMLog(String msgSequence, String msgId) throws SQLException {
        String sql = "insert into TBLOG " +
                "(message_id, message_sequence,tx_datetime)" +
                " values('" + msgId + "','" + msgSequence + "',current timestamp)";
        executeUpdate(sql);
    }

    public static void updateTbbillPaymentTxToReversed(String billId, String paymentId) throws SQLException {
        String sql = "update TBBILLPAYMENTTX set ACTIONCODE = '" + ActionCode.ALREADY_REVERSED +
                "' where billId = " + billId + " and paymentId = " + paymentId + " ";
        executeUpdate(sql);
    }

    public static Imd getIMD(String issuerId) throws SQLException, NotFoundException { // TODO should be meved into the 'UtilityCore'
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
                        new Long(resultSet.getLong("TRANS_BASE_FEE")), new Long(resultSet.getLong("TRANS_MIN_FEE")), new Long(resultSet.getLong("TRANS_MAX_FEE")),
                        new Long(resultSet.getLong("TRANS_PERCENTAG_FE")), new Long(resultSet.getLong("LORO_BASE_FEE")), new Long(resultSet.getString("LORO_MIN_FEE")),
                        new Long(resultSet.getLong("LORO_MAX_FEE")), new Long(resultSet.getLong("LORO_PERCENTAGE_FE")), resultSet.getString("CREATION_DATE"),
                        resultSet.getString("CREATION_TIME"), resultSet.getString("ACCOUNT_NO"), resultSet.getString("CHARGE_ACCOUNT_NO"));

                imd.setCreditAccountNo(resultSet.getString("CREDIT_ACCOUNT_NO"));
                imd.setDebitAccountNo(resultSet.getString("DEBIT_ACCOUNT_NO"));
                imd.setIssuerBranch(resultSet.getString("ISSUER_BRANCH"));
                imd.setNftWageFee(new Long(resultSet.getLong("NFT_WAGE_FEE")));
                imd.setNftWagePercentage(new Float(resultSet.getFloat("NFT_WAGE_PERCENT")));
                imd.setNftPgWageFee(new Long(resultSet.getLong("PGNFT_WAGE_FEE")));
                imd.setNftPgWagePercentage(new Float(resultSet.getFloat("PGNFT_WAGE_PERCENT")));
                imd.setNbalWageFee(new Long(resultSet.getLong("NBAL_WAGE_FEE")));
                imd.setNbalWagePercentage(new Float(resultSet.getFloat("NBAL_WAGE_PERCENT")));

                imdMap.put(issuerId, imd);
                return imd;
            }
            throw new NotFoundException();
        } catch (SQLException e) {
            log.error("e = " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateDailyWithdrawLimit4NFT(String accountNo, long amount, Long dailyWithdrawalLimit, String NftTypeCommand) throws SQLException, ModelException {

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Connection connection = null;
        String sql = "select NFT_DATE ,NFT_AMOUNT ,PGNFT_DATE ,PGNFT_AMOUNT,NFTPOS_DATE ,NFTPOS_AMOUNT ,MPNFT_DATE ,MPNFT_AMOUNT,KSNFT_DATE ,KSNFT_AMOUNT from tbNoncfscardacc where ACCOUNT_NO = '" + accountNo + "' for update";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {

                String NFT_DATE_COLUMN;
                String NFT_AMOUNT_COLUMN;


                if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_PG)) {
                    NFT_DATE_COLUMN = "PGNFT_DATE";
                    NFT_AMOUNT_COLUMN = "PGNFT_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_POS) || NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_POS_BRANCH)) {
                    NFT_DATE_COLUMN = "NFTPOS_DATE";
                    NFT_AMOUNT_COLUMN = "NFTPOS_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_MP)) {
                    NFT_DATE_COLUMN = "MPNFT_DATE";
                    NFT_AMOUNT_COLUMN = "MPNFT_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_KIOSK)) {
                    NFT_DATE_COLUMN = "KSNFT_DATE";
                    NFT_AMOUNT_COLUMN = "KSNFT_AMOUNT";
                } else {
                    NFT_DATE_COLUMN = "NFT_DATE";
                    NFT_AMOUNT_COLUMN = "NFT_AMOUNT";
                }

                String nftDate = accRS.getString(NFT_DATE_COLUMN);
                long nftAmount = accRS.getLong(NFT_AMOUNT_COLUMN);
                String currentDate = DateUtil.getSystemDate();

                if (currentDate.equalsIgnoreCase(nftDate)) {
                    if ((nftAmount + Math.abs(amount)) <= dailyWithdrawalLimit.longValue()) {
                        nftAmount += Math.abs(amount);
                    } else {
                        // TODO It must be handled in finer level; Probably issuer more detail DataBase Exception such as LimitExceedsingModelException
                        throw new ModelException(CFSConstants.NFT_WITHDRAWAL_LIMIT_EXCEED_ERROR);
                    }
                } else {
                    if (Math.abs(amount) > dailyWithdrawalLimit.longValue()) {
                        throw new ModelException(CFSConstants.NFT_WITHDRAWAL_LIMIT_EXCEED_ERROR);
                    }
                    nftAmount = Math.abs(amount);
                    nftDate = currentDate;
                }


                String updateSQL = "update tbNoncfscardacc set " + NFT_DATE_COLUMN + "='" + nftDate + "' , " + NFT_AMOUNT_COLUMN + " =" + nftAmount + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                connection.commit();

            } else
                connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error("ChannelFacadeNew.updateDailyWithdrawLimit4NFT() #1= -- Error :: " + e);
            throw e;
        } catch (ModelException e) {
            if (connection != null) connection.rollback();
            log.debug("ChannelFacadeNew.updateDailyWithdrawLimit4NFT() #2= -- Error :: " + e);
            throw e;

        } finally {
            if (accRS != null) accRS.close();
            if (statement != null) statement.close();
            if (updateST != null) updateST.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void ReverseDailyWithdrawLimit4NFT(String accountNo, long amount, Long dailyWithdrawalLimit, String NftTypeCommand, String origDate) throws SQLException, ModelException {

        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Connection connection = null;

        String sql = "select NFT_DATE ,NFT_AMOUNT ,PGNFT_DATE ,PGNFT_AMOUNT,NFTPOS_DATE ,NFTPOS_AMOUNT ,MPNFT_DATE ,MPNFT_AMOUNT,KSNFT_DATE ,KSNFT_AMOUNT from tbNoncfscardacc where ACCOUNT_NO = '" + accountNo + "' for update";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {

                String NFT_DATE_COLUMN;
                String NFT_AMOUNT_COLUMN;

                if (NftTypeCommand.endsWith(CFSConstants.TRANSACTION_TYPE_PG)) {
                    NFT_DATE_COLUMN = "PGNFT_DATE";
                    NFT_AMOUNT_COLUMN = "PGNFT_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_POS) || NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_POS_BRANCH)) {
                    NFT_DATE_COLUMN = "NFTPOS_DATE";
                    NFT_AMOUNT_COLUMN = "NFTPOS_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_MP)) {
                    NFT_DATE_COLUMN = "MPNFT_DATE";
                    NFT_AMOUNT_COLUMN = "MPNFT_AMOUNT";
                } else if (NftTypeCommand.endsWith(Constants.TRANSACTION_TYPE_KIOSK)) {
                    NFT_DATE_COLUMN = "KSNFT_DATE";
                    NFT_AMOUNT_COLUMN = "KSNFT_AMOUNT";
                } else {
                    NFT_DATE_COLUMN = "NFT_DATE";
                    NFT_AMOUNT_COLUMN = "NFT_AMOUNT";
                }

                String nftDate = accRS.getString(NFT_DATE_COLUMN);
                long nftAmount = accRS.getLong(NFT_AMOUNT_COLUMN);
                String currentDate = DateUtil.getSystemDate();
//                    currentDate = origDate;


                if (currentDate.equalsIgnoreCase(nftDate))
                    nftAmount -= amount;

                String updateSQL = "update tbNoncfscardacc set " + NFT_DATE_COLUMN + "='" + nftDate + "' , " + NFT_AMOUNT_COLUMN + " =" + nftAmount + "  where account_no = '" + accountNo + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                connection.commit();

            } else
                connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error("ChannelFacadeNew.updateDailyWithdrawLimit4NFT() #1= -- Error :: " + e);
            throw e;

        } finally {
            if (accRS != null) accRS.close();
            if (statement != null) statement.close();
            if (updateST != null) updateST.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static String getAlgorithmNo(String accountNo) throws SQLException {
        String sql = "SELECT algorithm_no FROM " + Constants.TB_CHECK_DIGITS_ALG + " WHERE account_no= '" + accountNo + "' WITH UR";
        ResultSet rs = executeQuery(sql);
        if (rs.next())
            return rs.getString("algorithm_no");
        else
            return "";
    }

    //***************CFD ******************************
    public static String existCustomerAccountInSRV(String accountNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select CUSTOMER_ID from tbcustomersrv where ACCOUNT_NO = '" + accountNo + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return resultSet.getString("CUSTOMER_ID").trim();
            else
                return null;

        } catch (SQLException e) {
            log.error("Error in existCustomerAccountInSRV>>>" + e);
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

    }

    public static String insertCustomerAccountInSRV(CFDAccount account, int hostId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String customerIdStr = "";

        String sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            connection.commit();
            if (rs.next())
                customerIdStr = rs.getString(1).trim();


            try {
                customerIdStr = ISOUtil.zeropad(customerIdStr, 12);
            } catch (ISOException e) {
                log.error("Can not zeropad CUSTOMER_ID = '" + customerIdStr + "' in ChannelFacadeNew--insertCustomerAccountInSRV : " + e.getMessage());
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
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

        sql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LANG, LAST_USAGE_TIME, " +
                "ACCOUNT_NO, STATUS, HOST_ID, TEMPLATE_ID, CREATION_DATE,ACCOUNT_GROUP" +
                ") values (" +
                "'" + customerIdStr + "' ," +
                "'" + 1234 + "', " +
                "'" + 1234 + "', " +
                1 + " ," +
                "current_timestamp, " +
                "'" + account.getAccount_no() + "' ," +
                1 + " , " +
                hostId + "," +
                1 + " , " +
                "'" + DateUtil.getSystemDate() + "' ," +
                "'" + account.getAccount_group() + "' )";

        executeUpdate(sql);
        return customerIdStr;
    }

    public static int updatetCustomerAccountInSRV(CFDAccount account, int hostId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set HOST_ID = " + hostId + ",LAST_USAGE_TIME= current_timestamp " +
                " where ACCOUNT_NO = '" + account.getAccount_no() + "' ";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            connection.commit();

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetCustomerAccountInSRV = " + e + " -- sql = " + sql);
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
//            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;

    }

    public static String ExistCustomerAccountInDB(String account_no) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select customer_id from tbcustomeraccounts where account_no = '" + account_no + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return resultSet.getString("customer_id").trim();
            else
                return null;
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

    public static int InsertCardForFARA(CFDAccount account, CFDPan pan, String customerId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + customerId.trim() + "'," +
                "'" + pan.getSequence() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'" +
                ")";

        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(insertcard_sql);

            if (ExistCardAccountInNonCFS(account.getAccount_no(), pan.getSerial())) {
                if (pan.getCardType() != Constants.GIFT_CARD) {
                    updateCardAccountInNonCFSTransactional(connection, account, pan);
                } else {
                    connection.rollback();
                    return -1;
                }
            } else
                insertCardAccountInNonCFSTransactional(connection, account, pan);

            connection.commit();
            return 0;
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int UpdateCardForFARA(CFDAccount account, CFDPan pan) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String updateCFSCard_sql = "update tbCFSCard set ";
        String updateCFSCard_sql_end = " where PAN = '" + pan.getSerial() + "'";
        String set_str_sql = "";
        set_str_sql += " ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,SEQUENCE_NO = '" + pan.getSequence() + "'";
        updateCFSCard_sql += set_str_sql + updateCFSCard_sql_end;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(updateCFSCard_sql);

            if (ExistCardAccountInNonCFS(account.getAccount_no(), pan.getSerial())) {
                if (pan.getCardType() != Constants.GIFT_CARD) {
                    updateCardAccountInNonCFSTransactional(connection, account, pan);
                } else {
                    connection.rollback();
                    return -1;
                }
            } else
                insertCardAccountInNonCFSTransactional(connection, account, pan);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static void InsertCardAndCardAccount(CFDPan pan, CFDAccount account, String customerId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + customerId.trim() + "'," +
                "'" + pan.getSequence() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'" +
                ")";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(insertcard_sql);

            InsertCardAccountTransactional(connection, account, pan);

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    private static void insertCardAccountInNonCFSTransactional(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        try {
            deactiveOldAccounts(connection, account);
            String insertCardAccount_sql = "insert into tbnoncfscardacc(" +
                    "PAN," +
                    "ACCOUNT_TYPE," +
                    "ACCOUNT_NO," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "BRANCH_ID," +
                    "SEQUENCE_NO," +
                    "STATUS," +
                    "MAX_TRANS_LIMIT" +
                    ") values (" +
                    "'" + pan.getSerial() + "'," +
                    "'" + account.getAccount_type() + "'," +
                    "'" + account.getAccount_no() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + pan.getCreation_date_pan() + "'," +
                    "'" + pan.getEdit_date_pan() + "'," +
                    "'" + pan.getSparrow_branch_id() + "'," +
                    "'" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'," +
                    "" + pan.getCardAccount_status() + "," +//Status for CardAccount that Insered.
                    Constants.IGNORE_MAX_TRANS_LIMIT +
                    ")";

            statement = connection.createStatement();
            statement.execute(insertCardAccount_sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCardAccountInNonCFSTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.insertCardAccountInNonCFSTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    private static int updateCardAccountInNonCFSTransactional(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        //update tbNONCFSCardAccount
        String updateCFSCardAccount_sql = "update tbnoncfscardacc set ";
        String updateCFSCardAccount_sql_end = " where PAN = '" + pan.getSerial() + "' And ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " branch_id = '" + pan.getSparrow_branch_id() + "'";
        set_str_sql += " ,ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,STATUS = " + pan.getCardAccount_status() + "";
        set_str_sql += " ,MAX_TRANS_LIMIT = " + account.getMax_trans_limit() + " ";
        updateCFSCardAccount_sql += set_str_sql + updateCFSCardAccount_sql_end;

        try {
            deactiveOldAccounts(connection, account);

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCFSCardAccount_sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updateCardAccountInNonCFSTransactional = " + e + " -- sql = " + updateCFSCardAccount_sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;
    }

    private static void InsertCardAccountTransactional(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        long max_trans_limit = pan.getCardType() == Constants.GIFT_CARD ? account.getMax_trans_limit() : Constants.IGNORE_MAX_TRANS_LIMIT;

        String insertCardAccount_sql = "insert into TBCFSCARDACCOUNT(" +
                "PAN," +
                "ACCOUNT_TYPE," +
                "ACCOUNT_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE," +
                "BRANCH_ID," +
                "SEQUENCE_NO," +
                "STATUS," +
                "MAX_TRANS_LIMIT" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + account.getAccount_type() + "'," +
                "'" + account.getAccount_no() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'," +
                "'" + pan.getSparrow_branch_id() + "'," +
                "'" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'," +
                "" + pan.getCardAccount_status() + "," +//Status for CardAccount that Insered.
                max_trans_limit +
                ")";

        //update TBCUSTOMERACCOUNTS
        String updateCustomerAccount = "update TBCUSTOMERACCOUNTS set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " SPARROW_BRANCH_ID = '" + pan.getSparrow_branch_id() + "' ";
        updateCustomerAccount += set_str_sql + updateCustomerAccount_end;

        try {
            statement = connection.createStatement();
            statement.execute(insertCardAccount_sql);

            if (pan.getCardType() != Constants.GIFT_CARD) {
                statement = connection.createStatement();
                statement.execute(updateCustomerAccount);
            }

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertCardAccountTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCardAccountTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    private static int UpdateCardAccountTransactional(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        //update tbCFSCardAccount
        String updateCFSCardAccount_sql = "update tbCFSCardAccount set ";
        String updateCFSCardAccount_sql_end = " where PAN = '" + pan.getSerial() + "' And ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " branch_id = '" + pan.getSparrow_branch_id() + "'";
        set_str_sql += " ,ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,STATUS = " + pan.getCardAccount_status() + "";
        set_str_sql += " ,MAX_TRANS_LIMIT = " + account.getMax_trans_limit() + " ";
        updateCFSCardAccount_sql += set_str_sql + updateCFSCardAccount_sql_end;
        //update TBCUSTOMERACCOUNTS
        String updateCustomerAccount = "update TBCUSTOMERACCOUNTS set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql2 = "";
        set_str_sql2 += " SPARROW_BRANCH_ID = '" + pan.getSparrow_branch_id() + "' ";
        updateCustomerAccount += set_str_sql2 + updateCustomerAccount_end;

        try {
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCFSCardAccount_sql);

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCustomerAccount);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--UpdateCardAccountTransactional = " + e + " -- sql = " + updateCFSCardAccount_sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;
    }

    public static int UpdateCardAndCardAccount(CFDPan pan, CFDAccount account) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;

        String updateCFSCard_sql = "update tbCFSCard set ";
        String updateCFSCard_sql_end = " where PAN = '" + pan.getSerial() + "'";
        String set_str_sql = "";
        set_str_sql += " ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,SEQUENCE_NO = '" + pan.getSequence() + "'";
        updateCFSCard_sql += set_str_sql + updateCFSCard_sql_end;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(updateCFSCard_sql);

            if (ExistCardAccountInDB(account.getAccount_no(), pan.getSerial()))
                UpdateCardAccountTransactional(connection, account, pan);
            else
                InsertCardAccountTransactional(connection, account, pan);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
    }

    private static String insertCustomerAccountInSRVTransactional(Connection connection, CFDAccount account, int hostId, String nationalCode) throws SQLException {
        ResultSet rs = null;
        Statement statement = null;
        String customerIdStr = "";
        String sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next())
                customerIdStr = rs.getString(1).trim();

            try {
                customerIdStr = ISOUtil.zeropad(customerIdStr, 12);
            } catch (ISOException e) {
                log.error("Can not zeropad CUSTOMER_ID = '" + customerIdStr + "' in ChannelFacadeNew--insertCustomerAccountInSRV : " + e.getMessage());
            }

            sql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LANG, LAST_USAGE_TIME, " +
                    "ACCOUNT_NO, STATUS, HOST_ID, TEMPLATE_ID, CREATION_DATE,ACCOUNT_GROUP,NATIONAL_CODE" +
                    ") values (" +
                    "'" + customerIdStr + "' ," +
                    "'" + 1234 + "', " +
                    "'" + 1234 + "', " +
                    1 + " ," +
                    "current_timestamp, " +
                    "'" + account.getAccount_no() + "' ," +
                    1 + " , " +
                    hostId + "," +
                    1 + " , " +
                    "'" + DateUtil.getSystemDate() + "' ," +
                    "'" + account.getAccount_group() + "'," +
                    "'" + nationalCode + "' )";

            statement = connection.createStatement();
            statement.execute(sql);
            return customerIdStr;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            if (rs != null) rs.close();
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }

    }

    private static int updatetCustomerAccountInSRVTransactional(Connection connection, String accountNo, int hostId, String nationalCode) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set HOST_ID = " + hostId + ",LAST_USAGE_TIME= current_timestamp ,NATIONAL_CODE= '" + nationalCode + "'" +

                " where ACCOUNT_NO = '" + accountNo + "' ";


        try {

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetCustomerAccountInSRVTransactional = " + e + " -- sql = " + sql);
//            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;

    }

    public static int UpdateCustomerAndAccountForCFS(CFDCustomerInfo customerInfo, CFDAccount account, String customerId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        try {
            connection = dbConnectionPool.getConnection();

            String customersrvId = existCustomerAccountInSRV(account.getAccount_no());
            //Creating Update CustomerTable Statement
            String updateCustomer_sql = "update TBCUSTOMER set ";
            String updateCustomer_sql_end = " where customer_id = '" + customerId + "'";
            String set_str_sql = "";
            set_str_sql += " ORIG_EDIT_DATE = '" + account.getEdit_date_account() + "' ";

            if ((customerInfo.getSex() != null) && (!customerInfo.getSex().equals("0"))) {
                set_str_sql += " ,SEX = " + customerInfo.getSex();
            }

            if ((customerInfo.getName_latin() != null) && (customerInfo.getName_latin().length() > 0)) {
                set_str_sql += " ,CARD_HOLDER_NAME = '" + customerInfo.getName_latin() + "' ";
            }

            if ((customerInfo.getFamily_latin() != null) && (customerInfo.getFamily_latin().length() > 0)) {
                set_str_sql += " ,CARD_HOLDER_SURNAM = '" + customerInfo.getFamily_latin() + "' ";
            }

            if ((customerInfo.getName_persian() != null) && (customerInfo.getName_persian().length() > 0)) {
                set_str_sql += " ,NAME_FA = '" + customerInfo.getName_persian() + "' ";
            }

            if ((customerInfo.getFamily_persian() != null) && (customerInfo.getFamily_persian().length() > 0)) {
                set_str_sql += " ,SURNAME_FA = '" + customerInfo.getFamily_persian() + "' ";
            }

            if (customerInfo.getAddress() != null && customerInfo.getAddress().length() > 0) {
                set_str_sql += " ,ADDRESS1 = '" + customerInfo.getAddress() + "' ";
            }
            if (customerInfo.getNationalCode() != null && customerInfo.getNationalCode().length() > 0) {
                set_str_sql += " ,NATIONAL_CODE = '" + customerInfo.getNationalCode() + "' ";
            }
            if (customerInfo.getBirthDate() != null && customerInfo.getBirthDate().length() > 0) {
                set_str_sql += " ,BIRTHDATE = '" + customerInfo.getBirthDate() + "' ";
            }
            if (customerInfo.getIDNumber() != null && customerInfo.getIDNumber().length() > 0) {
                set_str_sql += " ,ID_NUMBER = '" + customerInfo.getIDNumber() + "' ";
            }
            if (customerInfo.getPhoneNumber() != null && customerInfo.getPhoneNumber().length() > 0) {
                set_str_sql += " ,HOMEPHONE = '" + customerInfo.getPhoneNumber() + "' ";
            }


            updateCustomer_sql += set_str_sql + updateCustomer_sql_end;
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCustomer_sql);
            //Creating Update CustomerAccountsTable Statement
            String updateCustomerAccount_sql = "update TBCUSTOMERACCOUNTS set ";
            String updateCustomerAccount_sql_end = " where customer_id = '" + customerId + "'";
            set_str_sql = "";
            set_str_sql += " ORIG_EDIT_DATE = '" + account.getEdit_date_account() + "' ";
            if (account.getAccount_status() != null) {
                set_str_sql += " ,STATUS = " + account.getAccount_status();
            }
            if (account.getSgb_branch_id() != null) {
                set_str_sql += " ,SGB_BRANCH_ID = '" + account.getSgb_branch_id() + "' ";
            }
            if (account.getAccount_opener_name() != null) {
                set_str_sql += " ,ACCOUNT_OPENER_NAME = '" + account.getAccount_opener_name() + "' ";
            }
            if (account.getWithdraw_type() != null) {
                set_str_sql += " ,WITHDRAW_TYPE = " + account.getWithdraw_type() + " ";
            }

            if (account.getAccount_title() != null) {
                set_str_sql += " ,ACCOUNT_TITLE = '" + account.getAccount_title() + "' ";
            }

            updateCustomerAccount_sql += set_str_sql + updateCustomerAccount_sql_end;
            statement = connection.createStatement();
            updateCount += statement.executeUpdate(updateCustomerAccount_sql);

            if (customersrvId != null) {
                updateCount += updatetCustomerAccountInSRVTransactional(connection, account.getAccount_no(), Constants.HOST_CFS, customerInfo.getNationalCode());
            } else {
                insertCustomerAccountInSRVTransactional(connection, account, Constants.HOST_CFS, customerInfo.getNationalCode());
            }

            connection.commit();

        } catch (SQLException e) {
            log.error("ERROR in UpdateCustomerAndAccountForCFS()>>" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

        return updateCount;
    }

    public static String InsertCustomerAndAccountForCFS(CFDCustomerInfo customerInfo, CFDAccount account) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String customerId = "1";

        try {
            connection = dbConnectionPool.getConnection();
            String maxCustomer_id_sql = "select next value for seq_customer as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error(e);
            }

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "sex," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "ADDRESS1," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "TEMPLATE_ID, " +
                    "HOMEPHONE," +
                    "NATIONAL_CODE," +
                    "BIRTHDATE," +
                    "ID_NUMBER" +
                    ") values(" +
                    "'" + customerId + "'," +
                    customerInfo.getSex() + "," +
                    "'" + customerInfo.getName_latin() + "'," +
                    "'" + customerInfo.getFamily_latin() + "'," +
                    "'" + customerInfo.getName_persian() + "'," +
                    "'" + customerInfo.getFamily_persian() + "'," +
                    "'" + customerInfo.getAddress() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + account.getCreation_date_account() + "'," +
                    "'" + account.getEdit_date_account() + "'," +
                    "1," +
                    "'" + customerInfo.getPhoneNumber() + "'," +
                    "'" + customerInfo.getNationalCode() + "'," +
                    "'" + customerInfo.getBirthDate() + "'," +
                    "'" + customerInfo.getIDNumber() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                    "CUSTOMER_ID," +
                    "ACCOUNT_TYPE," +
                    "ACCOUNT_NO," +
                    "CURRENCY," +
                    "BALANCE," +
                    "STATUS," +
                    "ACCOUNT_SRC," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "SGB_BRANCH_ID," +
                    "HOST_ID, " +
                    "WITHDRAW_TYPE, " +
                    "ACCOUNT_OPENER_NAME, " +
                    "ACCOUNT_TITLE " +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + account.getAccount_type() + "'," +
                    "'" + account.getAccount_no() + "'," +
                    "'" + "364" + "'," +
//                    account.getBalance().trim() + "," +
                    "0," +
//                    Long.parseLong(account.getBalance().trim()) + "," +
//                    "0" + "," +
                    account.getAccount_status() + "," +
                    2 + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + account.getCreation_date_account() + "'," +
                    "'" + account.getEdit_date_account() + "'," +
                    "'" + account.getSgb_branch_id() + "'," +
                    Constants.HOST_CFS + "," +
                    "" + account.getWithdraw_type() + "," +
                    "'" + account.getAccount_opener_name() + "'," +
                    "'" + account.getAccount_title() + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustomerAccount_sql);

            String customersrvId = existCustomerAccountInSRV(account.getAccount_no());

            if (customersrvId != null) {
                updatetCustomerAccountInSRVTransactional(connection, account.getAccount_no(), Constants.HOST_CFS, customerInfo.getNationalCode());
            } else {
                insertCustomerAccountInSRVTransactional(connection, account, Constants.HOST_CFS, customerInfo.getNationalCode());
            }

            connection.commit();

        } catch (SQLException e) {
            log.error("ERROR in InsertCustomerAndAccountForCFS()>>" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return customerId;
    }

    public static int DeactiveCardAccountInCFSCARD(String accountNo, String cardNo) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String updateCardAccount = "update tbcfscardaccount set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + accountNo + "' And PAN = '" + cardNo + "' AND STATUS=1";
        String set_str_sql = "";
        set_str_sql += " STATUS = 0 ";
        updateCardAccount += set_str_sql + updateCustomerAccount_end;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCardAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccountInNonCFS = " + e + " -- sql = " + updateCardAccount);
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
        return updateCount;
    }

    public static int DeactiveCardAccountInNonCFS(String accountNo, String cardNo) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String updateCardAccount = "update tbnoncfscardacc set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + accountNo + "' And PAN = '" + cardNo + "'  AND STATUS=1";
        String set_str_sql = "";
        set_str_sql += " STATUS = 0 ";
        updateCardAccount += set_str_sql + updateCustomerAccount_end;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCardAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccountInNonCFS = " + e + " -- sql = " + updateCardAccount);
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
        return updateCount;
    }

    public static int DeactiveCardAccountInCFSCARD(String cardNo) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String updateCardAccount = "update tbcfscardaccount set ";
        String updateCustomerAccount_end = " where PAN = '" + cardNo + "' AND STATUS=1";
        String set_str_sql = "";
        set_str_sql += " STATUS = 0 ";
        updateCardAccount += set_str_sql + updateCustomerAccount_end;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCardAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccountInNonCFS with CardNo = " + e + " -- sql = " + updateCardAccount);
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
        return updateCount;
    }

    public static int DeactiveCardAccountInNonCFS(String cardNo) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String updateCardAccount = "update tbnoncfscardacc set ";
        String updateCustomerAccount_end = " where PAN = '" + cardNo + "'  AND STATUS=1";
        String set_str_sql = "";
        set_str_sql += " STATUS = 0 ";
        updateCardAccount += set_str_sql + updateCustomerAccount_end;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCardAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccountInNonCFS with CardNo = " + e + " -- sql = " + updateCardAccount);
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
        return updateCount;
    }


    public static int DeactiveCustomerAccount(CFDAccount account) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;
        //Creating Deactive CustomerAccountsTable Statement
        String updateCustomerAccount = "update TBCUSTOMERACCOUNTS set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " STATUS = 0 ";
        updateCustomerAccount += set_str_sql + updateCustomerAccount_end;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCustomerAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCustomerAccount = " + e + " -- sql = " + updateCustomerAccount);
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
        return updateCount;
    }

    public static int DeactiveCardAccount(CFDAccount account, CFDPan pan) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String updateCardAccount = "update tbCFSCardAccount set STATUS = 0 ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + account.getAccount_no() + "' And PAN = '" + pan.getSerial() + "'";
        updateCardAccount += updateCustomerAccount_end;
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateCardAccount);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccount = " + e + " -- sql = " + updateCardAccount);
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
        return updateCount;
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

    private static boolean ExistCardAccountInDB(String accountNo, String cardNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select PAN from TBCFSCARDACCOUNT where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            return resultSet.next();
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

    private static boolean ExistCardAccountInNonCFS(String accountNo, String cardNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select PAN from tbnoncfscardacc where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            return (resultSet.next());
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

    private static void deactiveOldAccounts(Connection connection, CFDAccount account) throws SQLException {
        Statement statement = null;
        String updateAccountsStatus = "update tbnoncfscardacc set STATUS = 0 where ACCOUNT_NO = '" + account.getAccount_no() + "'";
        try {

            statement = connection.createStatement();
            statement.executeUpdate(updateAccountsStatus);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--deactiveOldAccounts = " + e + " -- sql = " + updateAccountsStatus);
        } finally {
            if (statement != null) statement.close();
        }
    }
    //  *******************GROUP CARD*******************************************************
    public static int UpdateCardAndCardAccount4Parent(CFDPan pan, CFDAccount account) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;

        String updateCFSCard_sql = "update tbCFSCard set ";
        String updateCFSCard_sql_end = " where PAN = '" + pan.getSerial() + "'";
        String set_str_sql = "";
        set_str_sql += " ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,SEQUENCE_NO = '" + pan.getSequence() + "'";
        updateCFSCard_sql += set_str_sql + updateCFSCard_sql_end;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(updateCFSCard_sql);

            if (ExistCardAccountInDB(account.getAccount_no(), pan.getSerial()))
                UpdateCardAccountTransactional(connection, account, pan);
            else
                InsertCardAccount4ParentCard(connection, account, pan);
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateCardAndCardAccount4Parent()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
    }

    public static void InsertCardAccount4ParentCard(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        try {
            account.setMax_trans_limit(String.valueOf(Constants.IGNORE_MAX_TRANS_LIMIT));
        } catch (Exception e) {
            log.error(e);
        }
        String insertCardAccount_sql = "insert into TBCFSCARDACCOUNT(" +
                "PAN," +
                "ACCOUNT_TYPE," +
                "ACCOUNT_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE," +
                "BRANCH_ID," +
                "SEQUENCE_NO," +
                "STATUS," +
                "MAX_TRANS_LIMIT," +
                "SERIES" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + account.getAccount_type() + "'," +
                "'" + account.getAccount_no() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'," +
                "'" + pan.getSparrow_branch_id() + "'," +
                "'" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'," +
                "" + pan.getCardAccount_status() + "," +//Status for CardAccount that Insered.
                Constants.IGNORE_MAX_TRANS_LIMIT + "," +
                +pan.getRow() +  //new item added to insert card series 26.10.91
                ")";
        //update TBCUSTOMERACCOUNTS
        String updateCustomerAccount = "update TBCUSTOMERACCOUNTS set ";
        String updateCustomerAccount_end = " where ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " SPARROW_BRANCH_ID = '" + pan.getSparrow_branch_id() + "' ";
        updateCustomerAccount += set_str_sql + updateCustomerAccount_end;
        try {
            statement = connection.createStatement();
            statement.execute(insertCardAccount_sql);
            statement = connection.createStatement();
            statement.execute(updateCustomerAccount);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertCardAccount4ParentCard() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCardAccount4ParentCard() #2= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static void InsertCardAndCardAccount4ParentCard(CFDPan pan, CFDAccount account, String customerId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + customerId.trim() + "'," +
                "'" + pan.getSequence() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'" +
                ")";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(insertcard_sql);


            InsertCardAccount4ParentCard(connection, account, pan);

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCardAndCardAccount4ParentCard()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int DeactiveCardAccount4Parent(String pan) throws SQLException, ServerAuthenticationException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();

            String sql = "select account_no from tbCFSCardAccount where pan='" + pan + "' And status=1";
            statement = connection.createStatement();
            resultSet1 = statement.executeQuery(sql);
            if (resultSet1.next()) {
                String accountNo = resultSet1.getString("ACCOUNT_NO");
                sql = "SELECT pan FROM tbCFSCardAccount where account_No='" + accountNo +
                        "' And series>1 And status=1 with ur";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
                if (resultSet.next())
                    throw new ServerAuthenticationException("parent has active child card!");

                String updateMaxTransLimit = "update tbCFSCardAccount set status=0 where pan='" + pan + "'";
                statement = connection.createStatement();
                updateCount = statement.executeUpdate(updateMaxTransLimit);

            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccount4Parent = " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.DeactiveCardAccount4Parent()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
    }

    public static int DeactiveCardAccount4Parent(CFDAccount account, CFDPan pan, String sessionId, String rrn) throws SQLException {
        int updateCount = 0;
        long sum_max_trans_limit = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            String updateStatus = "update tbCFSCardAccount set status=0 where account_No='" +
                    account.getAccount_no() + "' And status=1";
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(updateStatus);
            if (updateCount > 0) {
                String sql = "SELECT sum(max_trans_limit) FROM tbCFSCardAccount where account_No='" + account.getAccount_no() +
                        "' And series>1 and max_trans_limit>0 with ur";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
                if (resultSet.next())
                    sum_max_trans_limit = resultSet.getLong(1);
                if (sum_max_trans_limit > 0) {
                    String updateMaxTransLimit = "update tbCFSCardAccount set max_trans_limit=0 where account_No='" + account.getAccount_no() +
                            "' And series>1 and max_trans_limit>0";
                    statement = connection.createStatement();
                    statement.executeUpdate(updateMaxTransLimit);
                    String updateSubsidyAmount = "update TBCUSTOMERACCOUNTS set  SUBSIDY_AMOUNT=SUBSIDY_AMOUNT - " + sum_max_trans_limit +
                            "  where account_no = '" + account.getAccount_no() + "'";
                    statement = connection.createStatement();
                    statement.executeUpdate(updateSubsidyAmount);
                    insertBLCKLog(connection, sessionId, Constants.DEACTIVE_PARENT_CARD_CFD, account.getAccount_no(), pan.getSerial(),
                            String.valueOf(sum_max_trans_limit), Constants.GROUP_CARD, Constants.UNBLOCK_GROUP_CARD, rrn);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccount4Parent = " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.DeactiveCardAccount4Parent()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
    }

    public static void insertBLCKLog(Connection connection, String sessionId, String tx_code, String accountNo, String cardNo, String
            amount, String service, short blockType, String rrn) throws SQLException {
        Statement statement = null;
        String insertBlckLog_sql = "insert into TB_BLCK_Log(" +
                "PARTNO," +
                "TX_DATETIME," +
                "SESSION_ID," +
                "TX_CODE," +
                "ACCOUNT_NO," +
                "CARD_NO," +
                "AMOUNT," +
                "SERVICE," +
                "BLOCK_TYPE," +
                "FEE_AMOUNT," +
                "RRN," +
                "CREATION_DATE," +
                "CREATION_TIME" +
                ") values(" +
                getPartNo() + "," +
                "current_timestamp," +
                "'" + sessionId + "'," +
                "'" + tx_code + "'," +
                "'" + accountNo + "'," +
                "'" + cardNo + "'," +
                "'" + amount + "'," +
                "'" + service + "'," +
                blockType + "," +
                "'0'," +
                "'" + rrn + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'" +
                ")";
        try {
            statement = connection.createStatement();
            statement.execute(insertBlckLog_sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--insertBLCKLog = " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static int UpdateCardAndCardAccount4Child(CFDPan pan, CFDAccount account) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;

        String updateCFSCard_sql = "update tbCFSCard set ";
        String updateCFSCard_sql_end = " where PAN = '" + pan.getSerial() + "'";
        String set_str_sql = "";
        set_str_sql += " ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,SEQUENCE_NO = '" + pan.getSequence() + "'";
        updateCFSCard_sql += set_str_sql + updateCFSCard_sql_end;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(updateCFSCard_sql);

            if (ExistCardAccountInDB(account.getAccount_no(), pan.getSerial()))
                UpdateCardAccount4Child(connection, account, pan);
            else
                InsertCardAccount4ChildCard(connection, account, pan);
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateCardAndCardAccount4Child()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
    }

    public static void UpdateCardAccount4Child(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        int updateCount = 0;
        Statement statement = null;
        statement = connection.createStatement();

//          //update tbCFSCardAccount
        String updateCFSCardAccount_sql = "update tbCFSCardAccount set ";
        String updateCFSCardAccount_sql_end = " where PAN = '" + pan.getSerial() + "' And ACCOUNT_NO = '" + account.getAccount_no() + "'";
        String set_str_sql = "";
        set_str_sql += " branch_id = '" + pan.getSparrow_branch_id() + "'";
        set_str_sql += " ,ORIG_EDIT_DATE = '" + pan.getEdit_date_pan() + "'";
        set_str_sql += " ,STATUS = " + pan.getCardAccount_status() + "";
        updateCFSCardAccount_sql += set_str_sql + updateCFSCardAccount_sql_end;
        try {
            statement.executeUpdate(updateCFSCardAccount_sql);
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--UpdateCardAccount4Child = " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static void InsertCardAccount4ChildCard(Connection connection, CFDAccount account, CFDPan pan) throws SQLException {
        Statement statement = null;
        try {
            try {
                account.setMax_trans_limit("0");
            } catch (Exception e) {
                log.error(e);
            }
            String insertCardAccount_sql = "insert into TBCFSCARDACCOUNT(" +
                    "PAN," +
                    "ACCOUNT_TYPE," +
                    "ACCOUNT_NO," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "BRANCH_ID," +
                    "SEQUENCE_NO," +
                    "STATUS," +
                    "MAX_TRANS_LIMIT," +
                    "SERIES" +
                    ") values (" +
                    "'" + pan.getSerial() + "'," +
                    "'" + account.getAccount_type() + "'," +
                    "'" + account.getAccount_no() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + pan.getCreation_date_pan() + "'," +
                    "'" + pan.getEdit_date_pan() + "'," +
                    "'" + pan.getSparrow_branch_id() + "'," +
                    "'" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'," +
                    "" + pan.getCardAccount_status() + "," +//Status for CardAccount that Insered.
                    account.getMax_trans_limit() + "," +
                    +pan.getRow() +  //new item added to insert card series 26.10.91
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCardAccount_sql);
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--InsertCardAccount4ChildCard = " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static void InsertCardAndCardAccount4ChildCard(CFDPan pan, CFDAccount account, String customerId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_CREATE_DATE," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + pan.getSerial() + "'," +
                "'" + customerId.trim() + "'," +
                "'" + pan.getSequence() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + pan.getCreation_date_pan() + "'," +
                "'" + pan.getEdit_date_pan() + "'" +
                ")";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(insertcard_sql);


            InsertCardAccount4ChildCard(connection, account, pan);

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCardAndCardAccount4ChildCard()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static int DeactiveCardAccount4Child(String pan, String sessionId, String rrn) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            String sql = "SELECT MAX_TRANS_LIMIT,ACCOUNT_NO FROM tbCFSCardAccount where  PAN = '" +
                    pan + "' and STATUS=1 for update";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                long max_trans_limit = resultSet.getLong("max_trans_limit");
                String accountNo = resultSet.getString("ACCOUNT_NO");
                String updateStatus = "update tbCFSCardAccount set status=0,max_trans_limit=0  where PAN = '" + pan + "'";
                statement = connection.createStatement();
                updateCount = statement.executeUpdate(updateStatus);

                sql = "insert into TBCHARGEPOLICY_log(SESSION_ID,CARD_NO,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,CHANNEL_TYPE,POLICY_STATUS)" +
                        " select '" + sessionId + "',CARD_NO,ACCOUNT_NO,  '" + DateUtil.getSystemDate() + "', '" + DateUtil.getSystemTime() + "' ,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,'" + Fields.SERVICE_CMS + "','" + Constants.POLICY_STATUS_REVOKED + "'  from  TBCHARGEPOLICY where card_no='" + pan + "'  and  ISDONE='0'";
                statement = connection.createStatement();
                statement.executeUpdate(sql);

                updateStatus = "update tbchargepolicy set isdone = '2' where  card_no = '" +pan + "'  and  isdone = '0' ";
                statement = connection.createStatement();
                statement.executeUpdate(updateStatus);
                if (max_trans_limit > 0) {
                    updateStatus = "update TBCUSTOMERACCOUNTS set  SUBSIDY_AMOUNT=SUBSIDY_AMOUNT - " + max_trans_limit +
                            "  where account_no = '" + accountNo + "'";
                    statement = connection.createStatement();
                    statement.executeUpdate(updateStatus);
                    insertBLCKLog(connection, sessionId, Constants.DEACTIVE_CHILD_CARD_CFD, accountNo, pan,
                            String.valueOf(max_trans_limit), Constants.GROUP_CARD, Constants.UNBLOCK_GROUP_CARD, rrn);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DeactiveCardAccount4Child = " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateCardAndCardAccount4Child()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;
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

//****************Branch Manager******************

    public static String[] findCustomerID(String accountNo,String nationalCode,String externalIdNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String[] customerId=new String[5];

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "select customer_Id,Account_title,status,account_type,SUB_TITLE from tbcustacc where account_no ='" + accountNo + "' and status='1' ";
            if(nationalCode!=null && !nationalCode.trim().equalsIgnoreCase(""))
                sql=sql+" and NATIONAL_CODE='"+nationalCode.trim()+"' ";
            if(externalIdNo!=null && !externalIdNo.trim().equalsIgnoreCase(""))
                sql=sql+" and EXTERNAL_ID_NUMBER='"+externalIdNo.trim()+"' ";
            if((nationalCode==null || nationalCode.trim().equalsIgnoreCase("")) && (externalIdNo==null || externalIdNo.trim().equalsIgnoreCase("")))
                sql=sql+ " and SUB_TITLE='0'";
            sql=sql+" with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {

                customerId[0] = resultSet.getString("CUSTOMER_ID").trim();
                customerId[1]= resultSet.getString("ACCOUNT_TITLE").trim();
                customerId[2] = resultSet.getString("status").trim();
                customerId[3]= resultSet.getString("account_type").trim();
                customerId[4]= resultSet.getString("SUB_TITLE").trim();
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.findCustomerID  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return customerId;
    }

    public static void createNAC(BranchMessage branchMsg,String origAccountTitles,String session_id,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String customerId = "";
        String accountNo = branchMsg.getAccountNo();
        Statement nacLogStatment=null;

        try {

            connection = dbConnectionPool.getConnection();
            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error(e);
            }
            resultSet.close();
            statement.close();

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "OWNER_INDEX," +
                    "SEX," +
                    "ORIG_CREATE_DATE," +    // create date
                    "ORIG_EDIT_DATE," +      // change date
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NATIONAL_CODE," +
                    "BIRTHDATE," +
                    "ID_NUMBER," +
                    "ID_SERIAL_NO," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_PLACE," +
                    "ID_ISSUE_CODE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "POSTALCODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "FOREIGN_COUNTRY_CODE," +
                    "STATEMENT_TYPE," +
                    "CELLPHONE," +
                    "FAX," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "STATUSMELLI," +
                    "EMAILADDRESS" +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getEn_FirstName().trim() + "'," +
                    "'" + branchMsg.getEn_LastName().trim() + "'," +
                    "'" + branchMsg.getFirstName().trim() + "'," +
                    "'" + branchMsg.getLastName().trim() + "'," +
                    "'" + branchMsg.getFatherName().trim() + "'," +
                    "'" + branchMsg.getOwnerIndex() + "'," +
                    branchMsg.getGender() + "," +
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + branchMsg.getChangeDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getNationalCode() + "'," +
                    "'" + branchMsg.getBirthDate() + "'," +
                    "'" + branchMsg.getID_Number() + "'," +
                    "'" + branchMsg.getID_SerialNumber() + "'," +
                    "'" + branchMsg.getID_Series() + "'," +
                    "'" + branchMsg.getID_IssueDate() + "'," +
                    "'" + branchMsg.getID_IssuePlace().trim() + "'," +
                    "'" + branchMsg.getID_IssueCode() + "'," +
                    "'" + branchMsg.getTel_Number1().trim() + "'," +
                    "'" + branchMsg.getTel_Number2().trim() + "'," +
                    "'" + branchMsg.getPostalCode() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getForeignCountryCode() + "'," +
                    branchMsg.getStatementType() + "," +
                    "'" + branchMsg.getMob_Number().trim() + "'," +
                    "'" + branchMsg.getFax_Number().trim() + "'," +
                    "'" + branchMsg.getAddress1().trim() + "'," +
                    "'" + branchMsg.getAddress2().trim() + "',"
                    + branchMsg.getNationalCodeValid().trim() + "," +
                    "'" + branchMsg.getEmailAddress().trim() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String accountTitle="0";
            String subTitle="0";

            if (!branchMsg.getOwnerIndex().equalsIgnoreCase("00")) {
                if(origAccountTitles.trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    accountTitle = Constants.SHARED_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                } else if (origAccountTitles.trim().equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    accountTitle = Constants.HOGHOOGHI_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                }
            } else {
                accountTitle = branchMsg.getAccountType();
                subTitle = "0";
            }

            String account_type=Constants.NORMAL_ACCOUNT;
            if(branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE1) || branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE2)
                    || branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE3))
                account_type=Constants.GROUP_CARD_ACCOUNT;

            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE," +
                    "ACCOUNT_TYPE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getNationalCode().trim() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getAccountNo() + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    "'" + account_type + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();


            if(branchMsg.getOwnerIndex().equalsIgnoreCase("00")){

            String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                    "CUSTOMER_ID," +
                    "ACCOUNT_TYPE," +     // Account Group
                    "ACCOUNT_NO," +
                    "LOCK_STATUS," +
                    "CURRENCY," +
                    "BALANCE," +
                    "ACCOUNT_TITLE," +
                    "STATUS," +        // Account Status default = 1 = Normal, CMDB default = null
                    "ACCOUNT_SRC," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "SGB_BRANCH_ID," +
                    "HOST_ID " +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getAccountGroup() + "'," +
                    "'" + branchMsg.getAccountNo() + "'," +
                    "9," +
                    "'" + branchMsg.getCurrencyCode() + "'," +
                    "0, " +
                    "'" + branchMsg.getAccountType() + "'," +
                    "1," +
                    2 + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + branchMsg.getChangeDate() + "'," +
                    "'" + branchMsg.getBranchCode() + "'," +
                    Constants.HOST_CFS +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustomerAccount_sql);
            statement.close();

            String customersrvId = existCustomerAccountInSRV(branchMsg.getAccountNo());

            if (customersrvId != null) {
                updateCustomerAccountInSRVTransactional4BM(connection, accountNo, branchMsg.getNationalCode().trim());
            } else {
                insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_SGB,
                        branchMsg.getAccountGroup(), branchMsg.getNationalCode().trim(), branchMsg.getNationalCodeValid(),customerId);
            }
            }
            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "BIRTHDATE," +
                    "CELLPHONE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "EMAILADDRESS," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ID_NUMBER," +
                    "ID_ISSUE_PLACE," +
                    "ID_SERIAL_NO," +
                    "OPERATION_TYPE," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_CODE," +
                    "POSTALCODE," +
                    "FAX," +
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
                    "'" + branchMsg.getAccountNo() + "'," +
                    "'" + branchMsg.getFirstName().trim() + "'," +
                    "'" + branchMsg.getLastName().trim() + "'," +
                    "'" + branchMsg.getFatherName().trim() + "'," +
                    branchMsg.getGender() + "," +
                    "'" + branchMsg.getBirthDate() + "'," +
                    "'" + branchMsg.getMob_Number().trim() + "'," +
                    "'" + branchMsg.getTel_Number1().trim() + "'," +
                    "'" + branchMsg.getTel_Number2().trim() + "'," +
                    "'" + branchMsg.getEmailAddress().trim() + "'," +
                    "'" + branchMsg.getAddress1().trim() + "'," +
                    "'" + branchMsg.getAddress2().trim() + "',"+
                    "'" + branchMsg.getAccountType() + "',"+
                    "'" + branchMsg.getNationalCode().trim() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getID_Number() + "'," +
                    "'" + branchMsg.getID_IssueCode().trim() + "'," +
                    "'" + branchMsg.getID_SerialNumber() + "'," +
                    "'" + Constants.CREATE_NAC_STATUS + "'," +
                    "'" + branchMsg.getID_Series() + "'," +
                    "'" + branchMsg.getID_IssueDate() + "'," +
                    "'" + branchMsg.getID_IssueCode() + "'," +
                    "'" + branchMsg.getPostalCode() + "'," +
                    "'" + branchMsg.getFax_Number().trim() + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    1  +","+
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getBranchCode() + "'," +
                    "'" + branchMsg.getAccountGroup().trim() + "'" +
                    " )";
            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertNaclog_sql);
            nacLogStatment.close();
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.createNAC=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (nacLogStatment != null) nacLogStatment.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    private static int updateCustomerAccountInSRVTransactional4BM(Connection connection, String account, String nationalCode) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set LAST_USAGE_TIME= current_timestamp,NATIONAL_CODE= '" + nationalCode + "'" +
                " where ACCOUNT_NO = '" + account + "' ";
        try {

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetCustomerAccountInSRVTransactional with national Code = " + e + " -- sql = " + sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;

    }

    private static String insertCustomerAccountInSRVTransactional(Connection connection, String account, int hostId,
                                                                  String accountGroup, String nationalCode, String statusMelli,String customerIdStr) throws SQLException {
        ResultSet rs = null;
        Statement statement = null;
        try {


            try {
                customerIdStr = ISOUtil.zeropad(customerIdStr, 12);
            } catch (ISOException e) {
                log.error("Can not zeropad CUSTOMER_ID = '" + customerIdStr + "' in ChannelFacadeNew--insertCustomerAccountInSRV : " + e.getMessage());
            }

            String sql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                    "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP," +
                    "NATIONAL_CODE,STATUSMELLI, LANG, SMSNOTIFICATION" +
                    ") values (" +
                    "'" + customerIdStr + "' ," +
                    "'" + 1234 + "', " +
                    "'" + 1234 + "', " +
                    "current_timestamp, " +
                    "'" + account + "' ," +
                    "1," +
                    "1," +
                    hostId + "," +
                    "'" + DateUtil.getSystemDate() + "' ," +
                    "'" + accountGroup + "'," +
                    "'" + nationalCode + "'," +
                    statusMelli + "," +
                    1 + "," +
                    "'" + 0 + "'" +
                    " )";

            statement = connection.createStatement();
            statement.execute(sql);
            return customerIdStr;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            if (rs != null) rs.close();
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #3= -- Error :: " + e1);
                e1.printStackTrace();
            }
        }
    }

    public static void updateNAC(BranchMessage branchMsg, String customerId,String session_id,String chanal_type,String origAccountTitle) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String accountNo = branchMsg.getAccountNo();

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "update TBCUSTOMER set " +
                    "CARD_HOLDER_NAME = '" + branchMsg.getEn_FirstName().trim() + "'," +
                    "CARD_HOLDER_SURNAM = '" + branchMsg.getEn_LastName().trim() + "'," +
                    "NAME_FA ='" + branchMsg.getFirstName().trim() + "'," +
                    "SURNAME_FA ='" + branchMsg.getLastName().trim() + "'," +
                    "FATHERNAME ='" + branchMsg.getFatherName().trim() + "'," +
                    "OWNER_INDEX ='" + branchMsg.getOwnerIndex() + "'," +
                    "SEX =" + branchMsg.getGender() + "," +
                    "ORIG_EDIT_DATE ='" + branchMsg.getChangeDate() + "'," +
                    "NATIONAL_CODE ='" + branchMsg.getNationalCode().trim() + "'," +
                    "BIRTHDATE ='" + branchMsg.getBirthDate() + "'," +
                    "ID_NUMBER ='" + branchMsg.getID_Number() + "'," +
                    "ID_SERIAL_NO ='" + branchMsg.getID_SerialNumber() + "'," +
                    "ID_SERIES ='" + branchMsg.getID_Series() + "'," +
                    "ID_ISSUE_DATE ='" + branchMsg.getID_IssueDate() + "'," +
                    "ID_ISSUE_PLACE ='" + branchMsg.getID_IssuePlace().trim() + "'," +
                    "ID_ISSUE_CODE ='" + branchMsg.getID_IssueCode() + "'," +
                    "HOMEPHONE ='" + branchMsg.getTel_Number1().trim() + "'," +
                    "TEL_NUMBER2 ='" + branchMsg.getTel_Number2().trim() + "'," +
                    "POSTALCODE ='" + branchMsg.getPostalCode() + "'," +
                    "EXTERNAL_ID_NUMBER ='" + branchMsg.getExt_IdNumber() + "'," +
                    "FOREIGN_COUNTRY_CODE ='" + branchMsg.getForeignCountryCode() + "'," +
                    "STATEMENT_TYPE =" + branchMsg.getStatementType() + "," +
                    "CELLPHONE ='" + branchMsg.getMob_Number().trim() + "'," +
                    "FAX ='" + branchMsg.getFax_Number().trim() + "'," +
                    "ADDRESS1 ='" + branchMsg.getAddress1().trim() + "'," +
                    "ADDRESS2 ='" + branchMsg.getAddress2().trim() + "'," +
                    "STATUSMELLI = " + branchMsg.getNationalCodeValid() + "," +
                    "EMAILADDRESS ='" + branchMsg.getEmailAddress().trim() + "'" +
                    " where CUSTOMER_ID = '" + customerId + "'";

            statement = connection.createStatement();
            int n = statement.executeUpdate(sql);
            statement.close();
            if (n == 0)
                throw new NotFoundException("Customer Not Found in tbCustomer, Customer_ID=" + customerId + " and Owner_Index=" + branchMsg.getOwnerIndex());

            if(branchMsg.getOwnerIndex().equalsIgnoreCase("00")){
            // Update tbcustomerAccounts
            if (!branchMsg.getAccountType().equals("0")) {
                sql = "update TBCUSTOMERACCOUNTS set " +
                        "ACCOUNT_TITLE = '" + branchMsg.getAccountType() + "'," +
                        "ORIG_EDIT_DATE = '" + branchMsg.getChangeDate() + "'" +
                        " where CUSTOMER_ID =  '" + customerId + "'";

                statement = connection.createStatement();
                statement.executeUpdate(sql);
                statement.close();
            }
            // Update tbcustomersrv
            sql = "update tbcustomersrv set STATUSMELLI = " + branchMsg.getNationalCodeValid() + ",LAST_USAGE_TIME = " +
                    "current_timestamp,NATIONAL_CODE= '" + branchMsg.getNationalCode().trim() + "' where ACCOUNT_NO = '" + branchMsg.getAccountNo() + "' ";
            statement = connection.createStatement();
            int count = statement.executeUpdate(sql);
            statement.close();
            if (count == 0) {
                throw new NotFoundException("Customer Not Found in tbCustomerSrv, AccountNo=" + branchMsg.getAccountNo());
            }
            }
            //Update tbcustacc
            String accountTitle = "0";
            String subTitle = "0";
            if (!branchMsg.getOwnerIndex().equalsIgnoreCase("00")) {
                if (origAccountTitle.trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    accountTitle = Constants.SHARED_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                } else if (origAccountTitle.trim().equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    accountTitle = Constants.HOGHOOGHI_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                }
            } else {
                accountTitle = branchMsg.getAccountType();
                subTitle = "0";
            }
            sql = "update TBCUSTACC set " +
                    "NATIONAL_CODE= '"+branchMsg.getNationalCode().trim()+"',"+
                    "EXTERNAL_ID_NUMBER= '"+branchMsg.getExt_IdNumber()+"',"+
                    "SUB_TITLE= '"+subTitle+"',"+
                    "ACCOUNT_TITLE = '" + accountTitle + "'" +
                    " where ACCOUNT_NO= '"+branchMsg.getAccountNo()+"' and CUSTOMER_ID =  '" + customerId + "'";

            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

            String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "BIRTHDATE," +
                    "CELLPHONE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "EMAILADDRESS," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ID_NUMBER," +
                    "ID_ISSUE_PLACE," +
                    "ID_SERIAL_NO," +
                    "OPERATION_TYPE," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_CODE," +
                    "POSTALCODE," +
                    "FAX," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE," +
                    "HANDLED,"+
                    "OPEN_DATE,"+
                    "OPEN_TIME,"+
                    "BRANCH_ID,"+
                    "ACCOUNT_GROUP"+
                    ") values(" +
                    "'" + session_id + "'," +
                    "'" + chanal_type + "'," +
                     getPartNo() + "," +
                    "current_timestamp, " +
                    "'" + branchMsg.getAccountNo() + "'," +
                    "'" + branchMsg.getFirstName().trim() + "'," +
                    "'" + branchMsg.getLastName().trim() + "'," +
                    "'" + branchMsg.getFatherName().trim() + "'," +
                    branchMsg.getGender() + "," +
                    "'" + branchMsg.getBirthDate() + "'," +
                    "'" + branchMsg.getMob_Number().trim() + "'," +
                    "'" + branchMsg.getTel_Number1().trim() + "'," +
                    "'" + branchMsg.getTel_Number2().trim() + "'," +
                    "'" + branchMsg.getEmailAddress().trim() + "'," +
                    "'" + branchMsg.getAddress1().trim() + "'," +
                    "'" + branchMsg.getAddress2().trim() + "',"+
                    "'" + branchMsg.getAccountType() + "',"+
                    "'" + branchMsg.getNationalCode().trim() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getID_Number() + "'," +
                    "'" + branchMsg.getID_IssueCode().trim() + "'," +
                    "'" + branchMsg.getID_SerialNumber() + "'," +
                    "'" + Constants.UPDATE_NAC_STATUS + "'," +
                    "'" + branchMsg.getID_Series() + "'," +
                    "'" + branchMsg.getID_IssueDate() + "'," +
                    "'" + branchMsg.getID_IssueCode() + "'," +
                    "'" + branchMsg.getPostalCode() + "'," +
                    "'" + branchMsg.getFax_Number().trim() + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                     0  +","+
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getBranchCode() + "'," +
                    "'" + branchMsg.getAccountGroup().trim() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateNAC,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateNAC,  Can not rollback -- Error2 :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateAccount(BranchMessage branchMsg) throws SQLException, NotFoundException {
        Connection connection = dbConnectionPool.getConnection();
        Statement selectSt = null;
        Statement accountSt = null;
        Statement customerSt = null;
        ResultSet rs = null;
        String customerIdStr = "";
        String accountNo = branchMsg.getAccountNo();
        int updateCount;
        try {

            String selectSql = "select CUSTOMER_ID from TBCUSTOMERACCOUNTS where ACCOUNT_NO = '" + accountNo + "'  AND LOCK_STATUS <> 9 for update";
            selectSt = connection.createStatement();
            rs = selectSt.executeQuery(selectSql);
            if (rs.next()) {
                customerIdStr = rs.getString(1);
                String accountSql = "update TBCUSTOMERACCOUNTS set ACCOUNT_OPENER_NAME = " +
                        "'" + branchMsg.getAccountOpenerName().trim() + "'," +
                        "WITHDRAW_TYPE = " + branchMsg.getWithdrawType() + " where " +
                        " ACCOUNT_NO = '" + accountNo + "'  AND LOCK_STATUS <> 9 ";

                accountSt = connection.createStatement();
                accountSt.executeUpdate(accountSql);

                if (branchMsg.getAddress2() != null && !branchMsg.getAddress2().trim().equalsIgnoreCase("")) {
                    String customerSql = "update TBCUSTOMER set ADDRESS2 = " +
                            "'" + branchMsg.getAddress2().trim() + "' where " +
                            " CUSTOMER_ID = '" + customerIdStr + "'";

                    customerSt = connection.createStatement();
                    updateCount = customerSt.executeUpdate(customerSql);
                    if (updateCount == 0)
                        throw new NotFoundException("Customer Not Found, Customer_ID=" + customerIdStr);
                }
                connection.commit();
            } else
                throw new NotFoundException("Account Not Found, AccountNo=" + accountNo);
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateAccount,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            connection.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (selectSt != null) selectSt.close();
            if (accountSt != null) accountSt.close();
            if (customerSt != null) customerSt.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int checkLockStatus(String accountNo) throws SQLException, NotFoundException {
        Connection connection = dbConnectionPool.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {

            String sql = "select  LOCK_STATUS from TBCUSTOMERACCOUNTS where ACCOUNT_NO ='" + accountNo + "' with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                connection.commit();
                return resultSet.getInt("LOCK_STATUS");
            } else
                throw new NotFoundException("Account Not Found, AccountNo=" + accountNo);
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.checkLockStatus,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void onlineAccount(BranchMessage branchMsg, String sessionId) throws SQLException, NotFoundException {
        Connection connection = dbConnectionPool.getConnection();
        Statement statement = null;
        Statement insertStatement = null;
        try {
            String balance = (branchMsg.getBalance().charAt(0) == '-') ? branchMsg.getBalance() :
                    branchMsg.getBalance().substring(1);
            String sql = "update TBCUSTOMERACCOUNTS set LOCK_STATUS = 1, " +
                    " WITHDRAW_TYPE = " + branchMsg.getWithdrawType() + "," +
                    " ACCOUNT_OPENER_NAME = '" + branchMsg.getAccountOpenerName().trim() + "'," +
                    " BALANCE = " + balance + ", " +
                    " STATUS = " + branchMsg.getAccountStatus() +
                    " where ACCOUNT_NO = '" + branchMsg.getAccountNo() + "'";
            statement = connection.createStatement();
            int count = statement.executeUpdate(sql);

            if (count == 0) {
                connection.rollback();
                throw new NotFoundException("Customer Not Found in tbCustomerAccounts, AccountNo=" + branchMsg.getAccountNo());
            }

            sql = "update TBCUSTOMERSRV set HOST_ID = 1" +
                    " where ACCOUNT_NO = '" + branchMsg.getAccountNo() + "'";
            statement = connection.createStatement();
            int countsrv = statement.executeUpdate(sql);

            if (countsrv == 0) {
                connection.rollback();
                throw new NotFoundException("Customer Not Found in tbCustomersrv, AccountNo=" + branchMsg.getAccountNo());
            }
            if (branchMsg.getAccountStatus().equalsIgnoreCase(Constants.ACCOUNT_STATUS_INACTIVE)) {
                String desc = "";
                byte[] descByte = {82, 124, 82, 39, 77, 61, 77, 39, 35, 83, 37, 65, 60, 40, 58};
                try {
                    String s = new String(descByte, "ISO-8859-1");
                    desc = FarsiUtil.convertWindows1256(descByte);
                } catch (UnsupportedEncodingException e) {
                    desc = "";
                }

                String insertSql = "insert into TBSERVICESTATUSLOG " +
                        "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                        "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                        " values('" +
                        branchMsg.getAccountNo() + "','" + sessionId + "','','" + branchMsg.getUserId() + "','" +
                        branchMsg.getBranchCode() + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + status + "'," +
                        "'" + Constants.CHN_USER_BRANCH + "','" + Constants.ACCOUNT_STATUS_MESSAGE + "','" + desc + "')";
                insertStatement = connection.createStatement();
                insertStatement.executeUpdate(insertSql);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateAccount,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } finally {
            if (statement != null) statement.close();
            if (insertStatement != null) insertStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void insertBranchLog(String sessionId, String pin, String messageId, String messageSequence,
                                       String branchCode, String userId, String reqDate, String reqTime, String txDate, String txTime, String accountNo,
                                       String cardNo, String actionCode, String txString, long duration, String terminalId) throws SQLException {

        if (txString.length() > 800)
            txString = txString.substring(0, 800);
        try {
            String sql = "insert into tb_branch_log " +
                    "(PARTNO,SESSION_ID,PIN,MESSAGE_ID,MESSAGE_SEQUENCE,BRANCH_CODE," +
                    "USER_ID,TX_DATETIME,REQ_DATE,REQ_TIME,TX_DATE,TX_TIME,ACCOUNT_NO,CARD_NO,ACTIONCODE,TX_STRING,DURATION,TERMINAL_ID) " +
                    " values(" +
                    getPartNo() + ",'" + sessionId + "','" + pin + "','" + messageId + "','" + messageSequence + "','" +
                    branchCode + "','" + userId + "',current_timestamp,'" + reqDate + "','" + reqTime + "','" + txDate + "','" + txTime + "','" +
                    accountNo + "','" + cardNo + "','" + actionCode + "','" + txString + "'," + duration + ",'" + terminalId + "')";
            executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        }

    }

    public static List<String> findBranchLogs(String messageSequence, String serviceType) throws SQLException {
        List<String> logSet = new ArrayList<String>();
        String hql = "select Session_id,Message_id,PIN from tb_branch_Log where " +
                " MESSAGE_SEQUENCE = '" + messageSequence + "'";

        if (serviceType.equalsIgnoreCase(Constants.SIMIN_SERVICE))
            hql = hql + " and TERMINAL_ID='SI'";
        else
            hql = hql + " and TERMINAL_ID<>'SI'";

        hql = hql + "  order by message_Id desc with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                logSet.add(resultSet.getString("Session_id"));
                logSet.add(resultSet.getString("Message_id"));
                logSet.add(resultSet.getString("PIN"));
            }
            connection.commit();
            return logSet;
        } catch (SQLException e) {
            log.error("EEROR ::: SQLException ::: Inside ChannelFacadeNew.findBranchLogs, error ::: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Account getAccount(String acc) throws NotFoundException, SQLException {
        String hql = "select ACCOUNT_NO, CUSTOMER_ID, ACCOUNT_TYPE,ACCOUNT_SRC, STATUS,BALANCE,CREATION_DATE, CREATION_TIME," +
                "SGB_BRANCH_ID,SPARROW_BRANCH_ID, HOST_ID,SUBSIDY_AMOUNT,ACCOUNT_TITLE,CURRENCY,ACCOUNT_OPENER_NAME,WITHDRAW_TYPE from tbCustomerAccounts where ACCOUNT_NO = '" + acc + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                Account account = new Account(
                        resultSet.getString("ACCOUNT_NO"), resultSet.getString("CUSTOMER_ID"), resultSet.getString("ACCOUNT_TYPE"),
                        resultSet.getInt("ACCOUNT_SRC"), resultSet.getLong("STATUS"), resultSet.getLong("BALANCE"),
                        resultSet.getString("CREATION_DATE"), resultSet.getString("CREATION_TIME"), resultSet.getString("SGB_BRANCH_ID"),
                        resultSet.getString("SPARROW_BRANCH_ID"), resultSet.getInt("HOST_ID"), resultSet.getLong("SUBSIDY_AMOUNT"),
                        resultSet.getString("ACCOUNT_TITLE"), resultSet.getString("CURRENCY"), resultSet.getString("ACCOUNT_OPENER_NAME"), resultSet.getInt("WITHDRAW_TYPE"));

                connection.commit();
                return account;
            } else {
                connection.rollback();
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccount() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Customer getCustomerInfo(String customerId) throws NotFoundException, SQLException, Exception {
        String hql = "select * from tbCustomer where CUSTOMER_ID = '" + customerId + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("CUSTOMER_ID"), resultSet.getString("NAME_FA"), resultSet.getString("SURNAME_FA"),
                        resultSet.getString("ADDRESS1"), resultSet.getString("ADDRESS2"), resultSet.getString("ORIG_CREATE_DATE"), resultSet.getString("OWNER_INDEX"),
                        resultSet.getInt("STATEMENT_TYPE"), resultSet.getString("ORIG_EDIT_DATE"), resultSet.getString("FATHERNAME"), resultSet.getInt("SEX"),
                        resultSet.getString("NATIONAL_CODE"), resultSet.getString("BIRTHDATE"), resultSet.getString("ID_NUMBER"), resultSet.getString("ID_SERIAL_NO"),
                        resultSet.getString("ID_SERIES"), resultSet.getString("ID_ISSUE_DATE"), resultSet.getString("ID_ISSUE_CODE"), resultSet.getString("ID_ISSUE_PLACE"),
                        resultSet.getString("CARD_HOLDER_NAME"), resultSet.getString("CARD_HOLDER_SURNAM"), resultSet.getString("EXTERNAL_ID_NUMBER"),
                        resultSet.getString("FOREIGN_COUNTRY_CODE"), resultSet.getString("HOMEPHONE"), resultSet.getString("TEL_NUMBER2"), resultSet.getString("CELLPHONE"),
                        resultSet.getString("FAX"), resultSet.getString("POSTALCODE"), resultSet.getInt("STATUSMELLI"), resultSet.getString("EMAILADDRESS"));

                connection.commit();
                return customer;
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

    public static List<CustomerServiceNew> getAccountList(String nationalCode) throws NotFoundException, SQLException, Exception {
        String hql = "select ACCOUNT_NO, ACCOUNT_GROUP, HOST_ID from tbCustomersrv where  NATIONAL_CODE= '" + nationalCode + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            List<CustomerServiceNew> accountList = new ArrayList<CustomerServiceNew>();
            while (resultSet.next()) {
                CustomerServiceNew account = new CustomerServiceNew();
                account.setAccountNo(resultSet.getString("ACCOUNT_NO"));
                account.setHostId(resultSet.getInt("HOST_ID"));
                account.setAccountGroup(resultSet.getString("ACCOUNT_GROUP"));
                accountList.add(account);
            }
            if (accountList.isEmpty()) {
                connection.rollback();
                throw new NotFoundException();
            } else {
                connection.commit();
                return accountList;
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountList() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.getAccountList() =   -- Error2 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void checkServiceStatus(String accountNo, String serviceStatus) throws NotFoundException, SQLException, dpi.atlas.model.facade.cm.ServerAuthenticationException {

        Connection connection = null;
        Statement selectStatement = null;
        ResultSet selectResultSet = null;
        String hql = "select SERVICE_STATUS from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' with ur";
        try {
            connection = dbConnectionPool.getConnection();
            selectStatement = connection.createStatement();
            selectResultSet = selectStatement.executeQuery(hql);
            connection.commit();
            if (selectResultSet.next()) {
                if (selectResultSet.getString("SERVICE_STATUS").equals(serviceStatus)) {
                    throw new dpi.atlas.model.facade.cm.ServerAuthenticationException("status is equal to Request_status");
                }
            } else {
                throw new NotFoundException("Account " + accountNo + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkServiceStatus() =   -- Error :: " + e + " -- hql = " + hql);
            connection.rollback();
            throw e;
        } finally {
            if (selectResultSet != null) selectResultSet.close();
            if (selectStatement != null) selectStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateServiceStatus(String accountNo, String sessionId, String terminalId, String userId, String branchCode,
                                           String serviceStatus, String channelType, String desc) throws SQLException {

        Connection connection = null;
        Statement updateStatement = null;
        Statement insertStatement = null;
        try {
            connection = dbConnectionPool.getConnection();
            String hqlUpdate = "update tbCustomersrv set SERVICE_STATUS = '" + serviceStatus + "' where ACCOUNT_NO = '" + accountNo + "' ";
            updateStatement = connection.createStatement();
            updateStatement.executeUpdate(hqlUpdate);

            String sql = "insert into TBSERVICESTATUSLOG " +
                    "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                    "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                    " values('" +
                    accountNo + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                    branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + serviceStatus + "'," +
                    "'" + channelType + "','" + Constants.SERVICE_STATUS_MESSAGE + "','" + desc + "')";
            insertStatement = connection.createStatement();
            insertStatement.executeUpdate(sql);

            connection.commit();

        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateServiceStatus() =   -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (updateStatement != null) updateStatement.close();
            if (insertStatement != null) insertStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void createBatchNAC(BranchMessage branchMsg,String origAccountTitle,String session_id,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement nacLogStatment=null;
        String customerId = "";
        String accountNo = branchMsg.getAccountNo();

        try {

            connection = dbConnectionPool.getConnection();
            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error(e);
            }
            resultSet.close();
            statement.close();

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "OWNER_INDEX," +
                    "SEX," +
                    "ORIG_CREATE_DATE," +    // create date
                    "ORIG_EDIT_DATE," +      // change date
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NATIONAL_CODE," +
                    "BIRTHDATE," +
                    "ID_NUMBER," +
                    "ID_SERIAL_NO," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_PLACE," +
                    "ID_ISSUE_CODE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "POSTALCODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "FOREIGN_COUNTRY_CODE," +
                    "STATEMENT_TYPE," +
                    "CELLPHONE," +
                    "FAX," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "STATUSMELLI," +
                    "EMAILADDRESS" +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getEn_FirstName().trim() + "'," +
                    "'" + branchMsg.getEn_LastName().trim() + "'," +
                    "'" + branchMsg.getFirstName().trim() + "'," +
                    "'" + branchMsg.getLastName().trim() + "'," +
                    "'" + branchMsg.getFatherName().trim() + "'," +
                    "'" + branchMsg.getOwnerIndex() + "'," +
                    branchMsg.getGender() + "," +
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + branchMsg.getChangeDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getNationalCode() + "'," +
                    "'" + branchMsg.getBirthDate() + "'," +
                    "'" + branchMsg.getID_Number() + "'," +
                    "'" + branchMsg.getID_SerialNumber() + "'," +
                    "'" + branchMsg.getID_Series() + "'," +
                    "'" + branchMsg.getID_IssueDate() + "'," +
                    "'" + branchMsg.getID_IssuePlace().trim() + "'," +
                    "'" + branchMsg.getID_IssueCode() + "'," +
                    "'" + branchMsg.getTel_Number1().trim() + "'," +
                    "'" + branchMsg.getTel_Number2().trim() + "'," +
                    "'" + branchMsg.getPostalCode() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getForeignCountryCode() + "'," +
                    branchMsg.getStatementType() + "," +
                    "'" + branchMsg.getMob_Number().trim() + "'," +
                    "'" + branchMsg.getFax_Number().trim() + "'," +
                    "'" + branchMsg.getAddress1().trim() + "'," +
                    "'" + branchMsg.getAddress2().trim() + "',"
                    + branchMsg.getNationalCodeValid().trim() + "," +
                    "'" + branchMsg.getEmailAddress().trim() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String accountTitle = "0";
            String subTitle = "0";
            if (!branchMsg.getOwnerIndex().equalsIgnoreCase("00")) {
                if(origAccountTitle.trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    accountTitle = Constants.SHARED_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                } else if (origAccountTitle.trim().equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    accountTitle = Constants.HOGHOOGHI_ACCOUNT_TYPE;
                    subTitle = branchMsg.getAccountType();
                }

            } else {
                accountTitle = branchMsg.getAccountType();
                subTitle = "0";
            }

            String account_type=Constants.NORMAL_ACCOUNT;
            if(branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE1) || branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE2)
                    || branchMsg.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE3))
                account_type=Constants.GROUP_CARD_ACCOUNT;

            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE," +
                    "ACCOUNT_TYPE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getNationalCode().trim() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getAccountNo() + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    "'" + account_type + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();


            if(branchMsg.getOwnerIndex().equalsIgnoreCase("00")){

            String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                    "CUSTOMER_ID," +
                    "ACCOUNT_TYPE," +     // Account Group
                    "ACCOUNT_NO," +
                    "LOCK_STATUS," +
                    "CURRENCY," +
                    "BALANCE," +
                    "ACCOUNT_TITLE," +
                    "STATUS," +        // Account Status default = 1 = Normal, CMDB default = null
                    "ACCOUNT_SRC," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "SGB_BRANCH_ID," +
                    "HOST_ID, " +
                    "ACCOUNT_OPENER_NAME, " +
                    "WITHDRAW_TYPE " +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + branchMsg.getAccountGroup() + "'," +
                    "'" + branchMsg.getAccountNo() + "'," +
                    "1," +
                    "'" + branchMsg.getCurrencyCode() + "'," +
                    "0, " +
                    "'" + branchMsg.getAccountType() + "'," +
                    "1," +
                    2 + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + branchMsg.getChangeDate() + "'," +
                    "'" + branchMsg.getBranchCode() + "'," +
                    Constants.HOST_CFS + "," +
                    "'" + branchMsg.getAccountOpenerName().trim() + "'," +
                    branchMsg.getWithdrawType() +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustomerAccount_sql);
            statement.close();

            String customersrvId = existCustomerAccountInSRV(branchMsg.getAccountNo());

            if (customersrvId != null) {
                updatetCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS, branchMsg.getNationalCode().trim());
            } else {
                insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                        branchMsg.getAccountGroup(), branchMsg.getNationalCode().trim(), branchMsg.getNationalCodeValid(),customerId);
            }
            }
            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "BIRTHDATE," +
                    "CELLPHONE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "EMAILADDRESS," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ID_NUMBER," +
                    "ID_ISSUE_PLACE," +
                    "ID_SERIAL_NO," +
                    "OPERATION_TYPE," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_CODE," +
                    "POSTALCODE," +
                    "FAX," +
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
                    "'" + branchMsg.getAccountNo() + "'," +
                    "'" + branchMsg.getFirstName().trim() + "'," +
                    "'" + branchMsg.getLastName().trim() + "'," +
                    "'" + branchMsg.getFatherName().trim() + "'," +
                    branchMsg.getGender() + "," +
                    "'" + branchMsg.getBirthDate() + "'," +
                    "'" + branchMsg.getMob_Number().trim() + "'," +
                    "'" + branchMsg.getTel_Number1().trim() + "'," +
                    "'" + branchMsg.getTel_Number2().trim() + "'," +
                    "'" + branchMsg.getEmailAddress().trim() + "'," +
                    "'" + branchMsg.getAddress1().trim() + "'," +
                    "'" + branchMsg.getAddress2().trim() + "',"+
                    "'" + branchMsg.getAccountType() + "',"+
                    "'" + branchMsg.getNationalCode().trim() + "'," +
                    "'" + branchMsg.getExt_IdNumber() + "'," +
                    "'" + branchMsg.getID_Number() + "'," +
                    "'" + branchMsg.getID_IssueCode().trim() + "'," +
                    "'" + branchMsg.getID_SerialNumber() + "'," +
                    "'" + Constants.CREATE_NAC_STATUS + "'," +
                    "'" + branchMsg.getID_Series() + "'," +
                    "'" + branchMsg.getID_IssueDate() + "'," +
                    "'" + branchMsg.getID_IssueCode() + "'," +
                    "'" + branchMsg.getPostalCode() + "'," +
                    "'" + branchMsg.getFax_Number().trim() + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    1 +","+
                    "'" + branchMsg.getCreateDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchMsg.getBranchCode() + "'," +
                    "'" + branchMsg.getAccountGroup().trim() + "'" +
                    " )";
            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertNaclog_sql);
            nacLogStatment.close();

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.createNAC=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (nacLogStatment != null) nacLogStatment.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<BlockReport> getBlockUnBlockReport(String accountNo, String requestType, String pin) throws NotFoundException, SQLException {
        String hql = "";
        List<BlockReport> reports = new ArrayList<BlockReport>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();

            if (requestType.equals("0")) {
                //block Report
                if (pin.equalsIgnoreCase(Constants.PIN_BLOCK_REPORT))
                    hql = "select * from tbblckstacc where  Account_no= '" + accountNo + "' order by CREATEDATE desc, CREATETIME desc  fetch first 150 rows only with ur";
                else // Simin Report
                    hql = "select * from tbblckstacc where  Account_no= '" + accountNo + "' and CHN_USER='" + Constants.SIMIN_SERVICE + "' order by CREATEDATE desc, CREATETIME desc  fetch first 150 rows only with ur";
                resultSet = statement.executeQuery(hql);

                while (resultSet.next()) {
                    BlockReport report = new BlockReport();
                    report.setAmount(resultSet.getLong("BLOCKAMOUNT"));
                    report.setDesc(resultSet.getString("DESC").trim());
                    if (resultSet.getString("BROKER_ID").equals(Constants.BROKER_ID_BRANCH) &&
                            resultSet.getString("PROVIDER_ID").equals(Constants.PROVIDER_ID_BRANCH)) {
                        String date = resultSet.getString("TX_ORIG_DATE");
                        if (date != null && !date.equals(""))
                            report.setDate(date);
                        else
                            report.setDate("");
                    } else {
                        String date = resultSet.getString("CREATEDATE");
                        if (date != null && !date.equals(""))
                            report.setDate(date);
                        else
                            report.setDate("");
                    }
                    report.setBranchCode(resultSet.getString("BRANCH").trim());
                    report.setBlockRow(resultSet.getString("BLOCK_NO").trim());
                    report.setChnUser(resultSet.getString("CHN_USER").trim());
                    reports.add(report);
                }
            } else {
                //unblock Report
                if (pin.equalsIgnoreCase(Constants.PIN_BLOCK_REPORT))
                    hql = "select * from tbunblckstacc where  Account_no= '" + accountNo + "' order by CREATEDATE desc, CREATETIME desc fetch first 150 rows only with ur";
                else //SIMIN Report
                    hql = "select * from tbunblckstacc where  Account_no= '" + accountNo + "' and CHN_USER='" + Constants.SIMIN_SERVICE + "'  order by CREATEDATE desc, CREATETIME desc fetch first 150 rows only with ur";
                resultSet = statement.executeQuery(hql);

                while (resultSet.next()) {
                    BlockReport report = new BlockReport();
                    report.setAmount(resultSet.getLong("UNBLOCKAMOUNT"));
                    report.setDesc(resultSet.getString("DESC").trim());
                    if (resultSet.getString("BROKER_ID").equals(Constants.BROKER_ID_BRANCH) &&
                            resultSet.getString("PROVIDER_ID").equals(Constants.PROVIDER_ID_BRANCH)) {
                        String date = resultSet.getString("TX_ORIG_DATE");
                        if (date != null && !date.equals(""))
                            report.setDate(date);
                        else
                            report.setDate("");
                    } else {
                        String date = resultSet.getString("CREATEDATE");
                        if (date != null && !date.equals(""))
                            report.setDate(date);
                        else
                            report.setDate("");
                    }
                    report.setBranchCode(resultSet.getString("BRANCHID").trim());
                    report.setBlockRow(resultSet.getString("BLOCK_NO").trim());
                    report.setChnUser(resultSet.getString("CHN_USER").trim());
                    reports.add(report);
                }
            }
            if (reports.isEmpty()) {
                connection.rollback();
                throw new NotFoundException();
            } else {
                connection.commit();
                return reports;
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getBlockUnBlockReport() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String getCardNoFromAccount(String accountNo,String nationalCode,String extIdNumber) throws NotFoundException, ModelException, SQLException, Exception {
        String hql = "";
        Connection connection = null;
        Statement statementCFS = null;
        ResultSet resultSetCfs = null;
        Statement statementFara = null;
        ResultSet resultSetFara = null;
        String cardNo = "";
        try {
            connection = dbConnectionPool.getConnection();
            statementCFS = connection.createStatement();
            statementFara = connection.createStatement();

            String[] accountInfo = findCustomerID(accountNo,nationalCode,extIdNumber);
            String customerId = accountInfo[0];
            if (customerId != null && !customerId.equals("")) {

                hql = "SELECT ACCOUNT_TYPE,tbcfscardaccount.PAN,SERIES" +
                        "  FROM tbcfscardaccount INNER JOIN tbcfscard" +
                        "  ON tbcfscardaccount.pan = tbcfscard.pan" +
                        "  where tbcfscard.customer_id='"+customerId+"' and status=1 with ur";
            resultSetCfs = statementCFS.executeQuery(hql);

            if (resultSetCfs.next()) {
                    if (!resultSetCfs.getString("ACCOUNT_TYPE").equals(Constants.GIFT_CARD_007) && !accountInfo[3].equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT)) {
                        cardNo = resultSetCfs.getString("PAN");
                        if (resultSetCfs.next()) {
                            // more than one card assign to account
                            throw new ModelException("");
                        } else {
                            //not Found another card then return card_no
                            connection.commit();
                            return cardNo;
                        }
                    } else if (accountInfo[3].equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT)) {
                        //Account is groupCard
                        if (resultSetCfs.getInt("SERIES") == 1) {
                            cardNo = resultSetCfs.getString("PAN");
                        } else {
                            while (resultSetCfs.next()) {
                                if (resultSetCfs.getInt("SERIES") == 1) {
                                    cardNo = resultSetCfs.getString("PAN");
                                    break;
                                }
                            }
                        }
                        if (cardNo.equalsIgnoreCase("")) {
                            // account not found in tbcfscardaccount and tbnoncfscardacc
                            throw new NotFoundException("");
                        }
                        connection.commit();
                        return cardNo;
                    } else {
                        //Account is gift Card
                        throw new ModelException();
                    }
                } else {
                    // account not found in tbcfscardaccount
                    throw new NotFoundException("");
                }
            } else {
                //not found account in tbcfscardaccount and search in tbnoncfscardacc
                hql = "select PAN from tbnoncfscardacc where  Account_no= '" + accountNo + "' and status=1 with ur";
                resultSetFara = statementFara.executeQuery(hql);
                if (resultSetFara.next()) {
                    cardNo = resultSetFara.getString("PAN");
                    if (resultSetFara.next()) {
                        // more than one card assign to account
                        throw new ModelException("");
                    } else {
                        //not Found another card then return card_no
                        connection.commit();
                        return cardNo;
                    }
                } else {
                    // account not found in tbcfscardaccount and tbnoncfscardacc
                    throw new NotFoundException("");
                }
            }
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#1 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#3 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getCardNoFromAccount()#4 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#5 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.getCardNoFromAccount()#6 = -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            try {
                if (resultSetFara != null) resultSetFara.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#7 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementFara != null) statementFara.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#8 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (resultSetCfs != null) resultSetCfs.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#9 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementCFS != null) statementCFS.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getCardNoFromAccount()#10 = -- Error :: " + e);
                e.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static List<String> getAccountNoFromCard(String cardNo) throws NotFoundException, SQLException, Exception {
        String hql = "";
        Connection connection = null;
        Statement statementCFS = null;
        ResultSet resultSetCfs = null;
        Statement statementFara = null;
        ResultSet resultSetFara = null;
        List<String> accountList = new ArrayList<String>();
        try {
            connection = dbConnectionPool.getConnection();
            statementCFS = connection.createStatement();
            statementFara = connection.createStatement();

            hql = "select ACCOUNT_TYPE,Account_no from tbcfscardaccount where  pan= '" + cardNo + "' and status=1 with ur";
            resultSetCfs = statementCFS.executeQuery(hql);

            while (resultSetCfs.next()) {
                if (!resultSetCfs.getString("ACCOUNT_TYPE").equals(Constants.GIFT_CARD_007)) {
                    accountList.add(resultSetCfs.getString("account_no"));
                }
            }

            hql = "select account_no from tbnoncfscardacc where  pan= '" + cardNo + "' and status=1 with ur";
            resultSetFara = statementFara.executeQuery(hql);
            while (resultSetFara.next()) {
                accountList.add(resultSetFara.getString("account_no"));
            }
            if (accountList.size() == 0)
                throw new NotFoundException();

            connection.commit();
            return accountList;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#1 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountNoFromCard()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.getAccountNoFromCard()#5 = -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            try {
                if (resultSetFara != null) resultSetFara.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#6 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementFara != null) statementFara.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#7 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (resultSetCfs != null) resultSetCfs.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#8 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementCFS != null) statementCFS.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.getAccountNoFromCard()#9 = -- Error :: " + e);
                e.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static BlockReport getAccountBlockReport(String accountNo, String pin) throws NotFoundException, SQLException {
        String hql = "";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        BlockReport report = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();

            hql = "select * from tbservicestatuslog where  Account_no= '" + accountNo + "' and MESSAGE_TYPE='" + Fields.ACC_ST + "' order by CREATION_DATE desc, CREATION_TIME desc fetch first 1 rows only with ur";
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                String chn_user = resultSet.getString("CHANNEL_TYPE").trim();
                if (pin.equalsIgnoreCase(Constants.PIN_SIMIN_ACCOUNT_BLOCK_REPORT) &&
                        !chn_user.equalsIgnoreCase(Constants.SIMIN_SERVICE)) {
                    connection.rollback();
                    throw new NotFoundException();
                }
                if (resultSet.getString("SERVICE_STATUS").equalsIgnoreCase(Constants.ACCOUNT_STATUS_ACTIVE)) {
                    connection.rollback();
                    throw new NotFoundException();
                }

                //block Report
                report = new BlockReport();
                report.setDesc(resultSet.getString("DESC").trim());
                String date = resultSet.getString("CREATION_DATE");
                if (date != null && !date.equals(""))
                    report.setDate(date);
                else
                    report.setDate("");

                if (chn_user.equalsIgnoreCase(Constants.SIMIN_SERVICE)) {
                    if (pin.equalsIgnoreCase(Constants.PIN_SIMIN_ACCOUNT_BLOCK_REPORT))
                        report.setBranchCode(resultSet.getString("USER_ID").trim());
                    else
                        report.setBranchCode(resultSet.getString("USER_ID").trim().substring(1));
                } else {
                    report.setBranchCode(resultSet.getString("BRANCH_CODE").trim());
                }
                report.setChnUser(resultSet.getString("CHANNEL_TYPE").trim());

                connection.commit();
                return report;

            } else {
                connection.rollback();
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountBlockReport() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    public static void removeRow(BranchMessage branchMessage,String session_id,String channel_type) throws NotFoundException, SQLException, ModelException, Exception {
        String hql = "";
        Connection connection = null;
        Statement statementCard = null;
        ResultSet resultSetCard = null;
        Statement statementCustAcc = null;
        ResultSet resultSetCustAcc = null;
        Statement statementDelCustAcc = null;
        Statement nacLogStatment = null;
        String customerId="";
        try {
            connection = dbConnectionPool.getConnection();
            statementCard = connection.createStatement();
            statementCustAcc = connection.createStatement();
            statementDelCustAcc = connection.createStatement();
            String account_no= branchMessage.getAccountNo();
            String nationalCode=branchMessage.getNationalCode().trim();
            String externalIdNumber=branchMessage.getExt_IdNumber();

            hql = "select CUSTOMER_ID from tbcustacc where  account_no= '" + account_no + "' and NATIONAL_CODE='"+nationalCode +
                    "' and EXTERNAL_ID_NUMBER='"+externalIdNumber+"' and status='"+Constants.ACTIVE_ROW_STATUS+"' for update";
            resultSetCustAcc = statementCustAcc.executeQuery(hql);

            if (resultSetCustAcc.next()) {
                customerId=resultSetCustAcc.getString("CUSTOMER_ID").trim();
            }else{
                throw new NotFoundException();
            }

            hql = "SELECT tbcfscardaccount.PAN"+
                    "  FROM tbcfscardaccount INNER JOIN tbcfscard"+
                    "  ON tbcfscardaccount.pan = tbcfscard.pan"+
                    "  where tbcfscard.customer_id='"+customerId+"' and status=1 with ur";
            resultSetCard = statementCard.executeQuery(hql);
            if (resultSetCard.next()) {
                throw  new ModelException();
            }

            hql = "update tbcustacc set status='"+Constants.REMOVE_ROW_STATUS+"' where  account_no= '" + account_no + "' and NATIONAL_CODE='"+nationalCode.trim()+
                    "' and EXTERNAL_ID_NUMBER='"+externalIdNumber+"' and status='"+Constants.ACTIVE_ROW_STATUS + "'";
            statementDelCustAcc.execute(hql);

            String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "OPERATION_TYPE," +
                    "HANDLED,"+
                    "BRANCH_ID"+
                    ") values(" +
                    "'" + session_id + "'," +
                    "'" + channel_type + "'," +
                    getPartNo() + "," +
                    "current_timestamp , " +
                    "'" + account_no + "'," +
                    "'" + nationalCode + "'," +
                    "'" + externalIdNumber + "',"+
                    "'"+Constants.REMOVE_ROW+"',"+
                    1 +","+
                    "'" + branchMessage.getBranchCode() + "'" +
                    ")" ;

            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertCustomer_sql);
            nacLogStatment.close();
            connection.commit();
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.removeRow()#1 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.removeRow()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.removeRow()#3 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.removeRow()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.removeRow()#5 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.removeRow()#4 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (resultSetCustAcc != null) resultSetCustAcc.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#6 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementCustAcc != null) statementCustAcc.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#7 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (resultSetCard != null) resultSetCard.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#8 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementCard != null) statementCard.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#9 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (statementDelCustAcc != null) statementDelCustAcc.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#10 = -- Error :: " + e);
                e.printStackTrace();
            }
            try {
                if (nacLogStatment != null) nacLogStatment.close();
            } catch (SQLException e) {
                log.error("ChannelFacadeNew.removeRow()#11 = -- Error :: " + e);
                e.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<String> getPinpadAccountList(String barnchCode) throws NotFoundException, SQLException, Exception {
        if (barnchCode.equalsIgnoreCase("00268"))
            barnchCode="00000";
        String hql = "select ACCOUNT_NO from tbdevice where branch_id='"+barnchCode+"' and  device_type=3 and ismodified='0' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            List<String> accountList = new ArrayList<String>();
            while (resultSet.next()) {
                accountList.add(resultSet.getString("ACCOUNT_NO"));
            }
            if (accountList.isEmpty()) {
                connection.rollback();
                throw new NotFoundException();
            } else {
                connection.commit();
                return accountList;
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getPinpadAccountList() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.getPinpadAccountList() =   -- Error2 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean findCFSAccount(String accountNo) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select  status  from tbcustomeraccounts  where account_No = '" + accountNo + "' and lock_status=1   with ur";
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
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int changeAccountType(String accountNo, String sessionId, String userId, String branchCode,
                                        String accountType, String channelType, String desc,String terminal) throws SQLException {

        Connection connection = null;
        Statement updateStatement = null;
        Statement insertStatement = null;
        Statement selectStatement = null;
        ResultSet selectResultSet = null;
        Statement selectCardStatement = null;
        ResultSet selectCardResultSet = null;

        try {
            connection = dbConnectionPool.getConnection();

            String hql = "select ACCOUNT_TITLE,ACCOUNT_TYPE from tbCustacc where ACCOUNT_NO = '" + accountNo + "' and SUB_TITLE='0' for update";

            selectStatement = connection.createStatement();
            selectResultSet = selectStatement.executeQuery(hql);
            if (selectResultSet.next()) {
                if (selectResultSet.getString("ACCOUNT_TYPE").trim().equalsIgnoreCase(accountType)) {
                    return 1; //account type has changed before.
                }
                if (selectResultSet.getString("ACCOUNT_TITLE").trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    return 2;//invalid operation
                }
            } else {
                return 3;//accountNo does not exist
            }

            String cardSql = "select pan from tbCfscardaccount where ACCOUNT_NO = '" + accountNo + "' and status=1 with ur";
            selectCardStatement = connection.createStatement();
            selectCardResultSet = selectCardStatement.executeQuery(cardSql);
            if (selectCardResultSet.next()) {
                return 4; //account assigned to card
            }

            String hqlUpdate = "update tbcustacc set  ACCOUNT_TYPE= '" + accountType + "' where ACCOUNT_NO = '" + accountNo + "' ";
            updateStatement = connection.createStatement();
            updateStatement.executeUpdate(hqlUpdate);

            String sql = "insert into TBSERVICESTATUSLOG " +
                    "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                    "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                    " values('" +
                    accountNo + "','" + sessionId + "','"+terminal+"','" + userId + "','" +
                    branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + accountType + "'," +
                    "'" + channelType + "','" + Constants.ACCOUNT_TYPE_MESSAGE + "','" + desc + "')";
            insertStatement = connection.createStatement();
            insertStatement.executeUpdate(sql);

            connection.commit();
        } catch (SQLException e) {
            log.error("CFSFacadeNew.changeAccountType() =   -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (updateStatement != null) updateStatement.close();
            if (insertStatement != null) insertStatement.close();
            if (selectResultSet != null) selectResultSet.close();
            if (selectStatement != null) selectStatement.close();
            if (selectCardStatement != null) selectCardStatement.close();
            if (selectCardResultSet != null) selectCardResultSet.close();
            dbConnectionPool.returnConnection(connection);
        }
        return 0;
    }

    public static String getAccountType(String accountNo) throws NotFoundException, SQLException {

        String hql = "select ACCOUNT_TYPE from tbCustacc where ACCOUNT_NO = '" + accountNo + "' and SUB_TITLE='0'  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("ACCOUNT_TYPE");
            }else{
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountType() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    //****************Branch Manager******************

    //****************CMS******************

    public static List<String> findCMSMessageLog(String rrn) throws SQLException {
        List<String> messageLog = new ArrayList<String>();

        String hql = "select Session_id,Message_id,BRANCH_CODE from tb_CFD_Log where RRN = '" + rrn + "' " +
                " order by message_Id with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                messageLog.add(resultSet.getString("Session_id"));
                messageLog.add(resultSet.getString("Message_id"));
                messageLog.add(resultSet.getString("BRANCH_CODE"));
            }
            return messageLog;
        } catch (SQLException e) {
            log.error("EEROR ::: SQLException ::: Inside ChannelFacadeNew.findCMSMessageLog, error ::: " + e.getMessage());
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

    public static void insertCMSLog(String MessageId, String CardNo, String TX_String, String ActionCode, String messageType, String rrn,
                                    String sessionId, long duration, String branchCode, String accountNo, String hostId) throws SQLException {

        if (TX_String.length() > 1500)
            TX_String = TX_String.substring(0, 1500);

        String sql = "insert into tb_cfd_log (PARTNO,TX_DATETIME,MESSAGE_ID,CARD_NO,ACTIONCODE,TX_STRING,MESSAGE_TYPE,RRN," +
                "SESSION_ID,DURATION,BRANCH_CODE,ACCOUNT_NO,HOST_ID,TX_DATE,TX_TIME) values(" + getPartNo() + ",current_timestamp,'" + MessageId + "'," +
                "'" + CardNo + "','" + ActionCode + "','" + TX_String + "','" + messageType + "','" + rrn + "','" + sessionId + "'" +
                "," + duration + ",'" + branchCode + "','" + accountNo + "'," + hostId + ",'"+ DateUtil.getSystemDate()+"','" +DateUtil.getSystemTime() +"')";
        try {
            executeUpdate(sql);
        } catch (SQLException e) {
            log.error(e);
            throw e;
        }
    }

    public static void UpdateCardAndCardAccount(String cardNo, String accountNo, String editDate, String accountGroup, String row, long maxTransLimit,String cardType) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String updateCFSCard_sql = "update tbCFSCard set ORIG_EDIT_DATE = '" + editDate + "' where PAN = '" + cardNo + "'";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(updateCFSCard_sql);

            if (ExistCardAccountInDB(accountNo, cardNo))
                UpdateCardAccountTransactional(connection, cardNo, accountNo, editDate);
            else
                InsertCardAccountTransactional(connection, cardNo, accountNo, editDate, accountGroup, row, maxTransLimit,cardType);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static void InsertCardAndCardAccount(String cardNo, String accountNo, String editDate, String accountGroup, String customerId, String row, long maxTransLimit,String latinName,String cardType) throws SQLException {
          Connection connection = null;
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
              connection = dbConnectionPool.getConnection();
              statement = connection.createStatement();
              statement.execute(insertcard_sql);
              InsertCardAccountTransactional(connection, cardNo, accountNo, editDate, accountGroup, row, maxTransLimit,cardType);
              UpdateLatinName4CardTransactional(connection, customerId, latinName);
              connection.commit();
          } catch (SQLException e) {
              log.error(e);
              try {
                  connection.rollback();
              } catch (SQLException e1) {
                  log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                  e1.printStackTrace();
              }
              throw e;
          } finally {
              if (statement != null) statement.close();
              dbConnectionPool.returnConnection(connection);
          }
      }
    public static void InsertChildCardAndAccount(String cardNo, String accountNo, String editDate, String accountGroup, String row, long maxTransLimit,String latinName,ChildInfo childInfo,String accountTitle) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String customerId = "";
        try {
        connection = dbConnectionPool.getConnection();
        String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

        statement = connection.createStatement();
        resultSet = statement.executeQuery(maxCustomer_id_sql);
        resultSet.next();
        String maxID = resultSet.getString(1).trim();
        if (maxID != null)
            customerId = maxID;
        try {
            customerId = ISOUtil.zeropad(customerId, 10);
        } catch (ISOException e) {
            log.error(e);
        }
        resultSet.close();
        statement.close();

            String firstName="";
            String lastName="";
            if(!latinName.trim().equals("")  && !latinName.trim().equals("#")){
                ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
                firstName =latinStr.get(0).toString();
                if(latinStr.size()>1 )
                    lastName =latinStr.get(1).toString();
            }

        String insertCustomer_sql = "insert into TBCUSTOMER(" +
                "CUSTOMER_ID," +
                "CARD_HOLDER_NAME," +
                "CARD_HOLDER_SURNAM," +
                "NAME_FA," +
                "SURNAME_FA," +
                "FATHERNAME," +
                "SEX," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "NATIONAL_CODE," +
                "BIRTHDATE," +
                "ID_NUMBER," +
                "ID_ISSUE_CODE," +
                "EXTERNAL_ID_NUMBER," +
                "CELLPHONE," +
                "STATUSMELLI" +
                ") values(" +
                "'" + customerId + "'," +
                "'" + firstName + "'," +
                "'" +lastName + "'," +
                "'" + childInfo.getName().trim() + "'," +
                "'" + childInfo.getFamily().trim() + "'," +
                "'" + childInfo.getFatherName().trim() + "'," +
                childInfo.getSex() + "," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + childInfo.getNationalCode() + "'," +
                "'" + childInfo.getBirthDate() + "'," +
                "'" + childInfo.getIDNumber() + "'," +
                "'" + childInfo.getIssuePlace().trim() + "'," +
                "'" + childInfo.getFrgCode() + "'," +
                "'" + childInfo.getMobileNo().trim() + "'," +
                +Constants.MELLICODE_VALID +
                " )";
        statement = connection.createStatement();
        statement.execute(insertCustomer_sql);
        statement.close();

        String insertCustAcc_sql = "insert into TBCUSTACC (" +
                "CUSTOMER_ID," +
                "NATIONAL_CODE," +
                "EXTERNAL_ID_NUMBER," +
                "ACCOUNT_NO," +
                "STATUS," +
                "ACCOUNT_TITLE," +
                "ACCOUNT_TYPE," +
                "SUB_TITLE" +
                ") values (" +
                "'" + customerId + "'," +
                "'" + childInfo.getNationalCode().trim() + "'," +
                "'" + childInfo.getFrgCode() + "'," +
                "'" + childInfo.getAccountno() + "'," +
                "'1'," +
                "'" + accountTitle + "'," +
                "'G'," +
                "'"+childInfo.getAccountType().trim()+"'" +
                ")";
        statement = connection.createStatement();
        statement.execute(insertCustAcc_sql);
        statement.close();


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

                statement = connection.createStatement();
                statement.execute(insertcard_sql);


            InsertCardAccountTransactional(connection, cardNo, accountNo, editDate, accountGroup, row, maxTransLimit,Constants.GROUP_CARD_TYPE_STR);

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertChildCardAndAccount()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void InsertCardForFARA(String cardNo, String accountNo, String editDate, String accountGroup, String nationalCode,String latinName) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {

            connection = dbConnectionPool.getConnection();

            String customerId = existCustomerAccountInSRV(accountNo);
            if (customerId != null) {
                updateCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_FARAGIR);
                if(isCustomerExist(customerId))
                    UpdateLatinName4CardTransactional(connection, customerId, latinName);
                else
                    InsertLatinName4CardTransactional(connection, customerId, latinName);

            } else {
                customerId = insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_FARAGIR, accountGroup, nationalCode);
                InsertLatinName4CardTransactional(connection, customerId, latinName);
            }
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

            statement = connection.createStatement();
            statement.execute(insertcard_sql);

            insertCardAccountInNonCFSTransactional(connection, cardNo, accountNo, editDate, accountGroup);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static void UpdateCardForFARA(String cardNo, String accountNo, String editDate, String accountGroup, String nationalCode) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String updateCFSCard_sql = "update tbCFSCard set ORIG_EDIT_DATE = '" + editDate + "'  where PAN = '" + cardNo + "'";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(updateCFSCard_sql);

            if (ExistCardAccountInNonCFS(accountNo, cardNo))
                updateCardAccountInNonCFSTransactional(connection, cardNo, accountNo, editDate);
            else
                insertCardAccountInNonCFSTransactional(connection, cardNo, accountNo, editDate, accountGroup);

            if (existCustomerAccountInSRV(accountNo) != null)
                updateCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_FARAGIR);
            else

                insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_FARAGIR, accountGroup, nationalCode);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCustomerAndAccountForCFS()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }


    private static void UpdateCardAccountTransactional(Connection connection, String cardNo, String accountNo, String editDate) throws SQLException {
        Statement statement = null;

        String sql = "update tbCFSCardAccount set ORIG_EDIT_DATE = '" + editDate + "' ,STATUS =" + Constants.CARD_STATUS_ACTIVE_FLAG + "   where PAN = '" + cardNo + "' And ACCOUNT_NO = '" + accountNo + "'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--UpdateCardAccountTransactional --CMS-- = " + e + " -- sql = " + sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void InsertCardAccountTransactional(Connection connection, String cardNo, String accountNo, String editDate, String accountGroup, String row, long maxTransLimit,String cardType) throws SQLException {
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
                "SERIES," +
                "CARD_TYPE," +
                "SEQUENCE_NO" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + accountGroup + "'," +
                "'" + accountNo + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'," +
                "" + Constants.CARD_STATUS_ACTIVE_FLAG + "," +
                maxTransLimit + "," +
                row + ",'"+
                cardType + "','"
                + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'" +
                ")";

        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertCardAccountTransactional()--CMS-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void updateCardAccountInNonCFSTransactional(Connection connection, String cardNo, String accountNo, String editDate) throws SQLException {
        Statement statement = null;
        String sql = "update tbnoncfscardacc set ORIG_EDIT_DATE = '" + editDate + "' ,STATUS =" + Constants.CARD_STATUS_ACTIVE_FLAG + " where PAN = '" + cardNo + "' And ACCOUNT_NO = '" + accountNo + "'";

        try {

            statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updateCardAccountInNonCFSTransactional = " + e + " -- sql = " + sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void insertCardAccountInNonCFSTransactional(Connection connection, String cardNo, String accountNo, String editDate, String accountGroup) throws SQLException {
        Statement statement = null;
        try {
            String insertCardAccount_sql = "insert into tbnoncfscardacc(" +
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
                    "'" + accountGroup + "'," +
                    "'" + accountNo + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + editDate + "'," +
                    "" + Constants.CARD_STATUS_ACTIVE_FLAG + "," +
                    Constants.IGNORE_MAX_TRANS_LIMIT + ",'" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'" +
                    ")";

            statement = connection.createStatement();
            statement.execute(insertCardAccount_sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCardAccountInNonCFSTransactional()--CMS-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void updateCustomerAccountInSRVTransactional(Connection connection, String accountNo, int hostId) throws SQLException {
        Statement statement = null;
        String sql = "update TBCUSTOMERSRV set HOST_ID = " + hostId + ",LAST_USAGE_TIME= current_timestamp  where ACCOUNT_NO = '" + accountNo + "' ";

        try {

            statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetCustomerAccountInSRVTransactional--CMS-- = " + e + " -- sql = " + sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static String insertCustomerAccountInSRVTransactional(Connection connection, String accountNo, int hostId, String accountGroup, String nationalCode) throws SQLException {
        ResultSet rs = null;
        Statement statement = null;
        String customerIdStr = "";
        String sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";
        try {

            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next())
                customerIdStr = rs.getString(1).trim();

            try {
                customerIdStr = ISOUtil.zeropad(customerIdStr, 12);
            } catch (ISOException e) {
                log.error("Can not zeropad CUSTOMER_ID = '" + customerIdStr + "' in ChannelFacadeNew--insertCustomerAccountInSRV : " + e.getMessage());
            }

            sql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                    "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP,National_Code," +
                    "LANG, SMSNOTIFICATION" +
                    ") values (" +
                    "'" + customerIdStr + "' ," +
                    "'" + 1234 + "', " +
                    "'" + 1234 + "', " +
                    "current_timestamp, " +
                    "'" + accountNo + "' ," +
                    "1," +
                    "1," +
                    hostId + "," +
                    "'" + DateUtil.getSystemDate() + "' ," +
                    "'" + accountGroup + "'," +
                    "'" + nationalCode + "'," +
                    1 + "," +
                    "'" + 0 + "'" +
                    " )";

            statement = connection.createStatement();
            statement.execute(sql);
            return customerIdStr;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #1= -- Error :: " + e);
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }
    }

    public static String insertCustomerAccountInSRV(String accountNo, int hostId, String accountGroup, String nationalCode) throws SQLException {
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null;
        String customerIdStr = "";
        String sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next())
                customerIdStr = rs.getString(1).trim();

            try {
                customerIdStr = ISOUtil.zeropad(customerIdStr, 12);
            } catch (ISOException e) {
                log.error("Can not zeropad CUSTOMER_ID = '" + customerIdStr + "' in ChannelFacadeNew--insertCustomerAccountInSRV : " + e.getMessage());
            }

            sql = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                    "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP,National_Code," +
                    "LANG, SMSNOTIFICATION" +
                    ") values (" +
                    "'" + customerIdStr + "' ," +
                    "'" + 1234 + "', " +
                    "'" + 1234 + "', " +
                    "current_timestamp, " +
                    "'" + accountNo + "' ," +
                    "1," +
                    "1," +
                    hostId + "," +
                    "'" + DateUtil.getSystemDate() + "' ," +
                    "'" + accountGroup + "'," +
                    "'" + nationalCode + "'," +
                    1 + "," +
                    "'" + 0 + "'" +
                    " )";

            statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
            return customerIdStr;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertCustomerAccountInSRVTransactional() #1= -- Error :: " + e);
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

    public static CardAccount getCFSCardAccounts(String cardNo,String accountNo) throws SQLException {

        String hql = "select ACCOUNT_TYPE,SERIES,CARD_TYPE" +
                " from tbCFSCardAccount where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "'  and STATUS = 1  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CardAccount cardAccount = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            if (resultSet.next()) {
                cardAccount = new CardAccount();
                cardAccount.setAccountType(resultSet.getString("ACCOUNT_TYPE"));
                cardAccount.setSeries(resultSet.getInt("SERIES"));
                cardAccount.setCardType(resultSet.getString("CARD_TYPE"));
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
        return cardAccount;
    }

    public static List<CardAccount> getCFSCardAccounts(String cardNo) throws SQLException {

        String hql = "select ACCOUNT_TYPE,ACCOUNT_NO,MAX_TRANS_LIMIT,SERIES,WITHDRAW_DATE,WITHDRAW_AMOUNT,PBWITHDRAW_DATE,PBWITHDRAW_AMOUNT,STBWITHDRAW_DATE,STBWITHDRAW_AMOUNT,CARD_TYPE" +
                " from tbCFSCardAccount where PAN = '" + cardNo + "' and STATUS = 1  with ur";


        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<CardAccount> cardAccountList = new ArrayList<CardAccount>();
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                CardAccount cardAccount = new CardAccount(cardNo, resultSet.getString("ACCOUNT_TYPE"), resultSet.getString("ACCOUNT_NO"),
                        resultSet.getLong("MAX_TRANS_LIMIT"), resultSet.getLong("WITHDRAW_AMOUNT"),
                        resultSet.getInt("SERIES"), resultSet.getLong("PBWITHDRAW_AMOUNT"),
                        resultSet.getLong("STBWITHDRAW_AMOUNT"));

                cardAccount.setWithdrawDate(resultSet.getString("WITHDRAW_DATE") == null ? "" : resultSet.getString("WITHDRAW_DATE"));
                cardAccount.setPBwithdrawDate(resultSet.getString("PBWITHDRAW_DATE") == null ? "" : resultSet.getString("PBWITHDRAW_DATE"));
                cardAccount.setSTBwithdrawDate(resultSet.getString("STBWITHDRAW_DATE") == null ? "" : resultSet.getString("STBWITHDRAW_DATE"));
                cardAccount.setCardType(resultSet.getString("CARD_TYPE"));
                cardAccountList.add(cardAccount);
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
        return cardAccountList;
    }

    public static List<NonCFSCardAccount> getNonCFSCardAccounts(String cardNo) throws SQLException {

        String hql = "select ACCOUNT_TYPE,ACCOUNT_NO,MAX_TRANS_LIMIT,WITHDRAW_DATE,WITHDRAW_AMOUNT,PBWITHDRAW_DATE,PBWITHDRAW_AMOUNT,STBWITHDRAW_DATE," +
                "STBWITHDRAW_AMOUNT,NFT_DATE,NFT_AMOUNT,PGNFT_DATE,PGNFT_AMOUNT,NFTPOS_DATE,NFTPOS_AMOUNT,MPNFT_DATE,MPNFT_AMOUNT,KSNFT_DATE,KSNFT_AMOUNT" +
                " from tbnonCFSCardAcc where PAN = '" + cardNo + "' and STATUS = 1  with ur";


        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<NonCFSCardAccount> cardAccountList = new ArrayList<NonCFSCardAccount>();
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                NonCFSCardAccount cardAccount = new NonCFSCardAccount(cardNo, resultSet.getString("ACCOUNT_TYPE"), resultSet.getString("ACCOUNT_NO"),
                        resultSet.getLong("MAX_TRANS_LIMIT"), resultSet.getLong("WITHDRAW_AMOUNT"), resultSet.getLong("PBWITHDRAW_AMOUNT"),
                        resultSet.getLong("STBWITHDRAW_AMOUNT"), resultSet.getLong("NFT_AMOUNT"), resultSet.getLong("PGNFT_AMOUNT"),
                        resultSet.getLong("NFTPOS_AMOUNT"), resultSet.getLong("MPNFT_AMOUNT"), resultSet.getLong("KSNFT_AMOUNT"));

                cardAccount.setWithdrawDate(resultSet.getString("WITHDRAW_DATE") == null ? "" : resultSet.getString("WITHDRAW_DATE"));
                cardAccount.setPBwithdrawDate(resultSet.getString("PBWITHDRAW_DATE") == null ? "" : resultSet.getString("PBWITHDRAW_DATE"));
                cardAccount.setSTBwithdrawDate(resultSet.getString("STBWITHDRAW_DATE") == null ? "" : resultSet.getString("STBWITHDRAW_DATE"));
                cardAccount.setNft_date(resultSet.getString("NFT_DATE") == null ? "" : resultSet.getString("NFT_DATE"));
                cardAccount.setPgnft_date(resultSet.getString("PGNFT_DATE") == null ? "" : resultSet.getString("PGNFT_DATE"));
                cardAccount.setNftpos_date(resultSet.getString("NFTPOS_DATE") == null ? "" : resultSet.getString("NFTPOS_DATE"));
                cardAccount.setMpnft_date(resultSet.getString("MPNFT_DATE") == null ? "" : resultSet.getString("MPNFT_DATE"));
                cardAccount.setKsnft_date(resultSet.getString("KSNFT_DATE") == null ? "" : resultSet.getString("KSNFT_DATE"));


                cardAccountList.add(cardAccount);
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
        return cardAccountList;
    }

    public static String[] getCFSCardAccount(String cardNo) throws SQLException {

        String hql = "select ACCOUNT_NO,CARD_TYPE  from tbcfscardaccount where PAN = '" + cardNo + "' and STATUS = 1  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String[] account=new String[2];

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                account[0] = resultSet.getString("ACCOUNT_NO").trim();
                account[1] = resultSet.getString("CARD_TYPE").trim();
            }

            return account;

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

    public static String getCardAccount(String cardNo, String tableName) throws SQLException {

        String hql = "select ACCOUNT_NO  from " + tableName + " where PAN = '" + cardNo + "' and STATUS = 1  with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();

            if (resultSet.next())
                return resultSet.getString("ACCOUNT_NO");
            else
                return "";

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

    public static void reCreateCard(List<NonCFSCardAccount> nonCFSCardAccountList, List<CardAccount> cardAccountList, String newCardNo, String editDate, String cardNo, Card cardObj, String session_id) throws SQLException {

        String sql = "";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();

            InsertCardTransactional(connection, newCardNo, editDate, cardObj.getCustomerID(), cardObj.getTemplateID());

            if (cardAccountList.size() > 0) {
                Iterator it = cardAccountList.iterator();
                while (it.hasNext()) {
                    CardAccount cardAcc = (CardAccount) it.next();
                    InsertCardAccountTransactional(connection, cardAcc, newCardNo, editDate);

                    if (cardAcc.getSeries() > 1) {
                        CardPolicy existPolicy = getPolicy(cardNo);
                        if (existPolicy != null && !existPolicy.equals("")) {
                            String date = DateUtil.getSystemDate();
                            String time = DateUtil.getSystemTime();
                            sql = "insert into TBCHARGEPOLICY(SESSION_ID,CARD_NO,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,TYPE,START_DATE,END_DATE,NEXT_DATE ,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,ISDONE)" +
                                    " select '" + session_id + "','" + newCardNo + "',ACCOUNT_NO,  '" + date + "', '" + time + "' ,TYPE,START_DATE,END_DATE,NEXT_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,'" + 0 + "'  from  TBCHARGEPOLICY where card_no='" + cardNo + "'  and  ISDONE='0'";


                            statement = connection.createStatement();
                            statement.executeUpdate(sql);

                            sql = "insert into TBCHARGEPOLICY_log(SESSION_ID,CARD_NO,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,CHANNEL_TYPE)" +
                                    " select '" + session_id + "','" + newCardNo + "',ACCOUNT_NO,  '" + date + "', '" + time + "' ,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,'" + Fields.SERVICE_CMS + "'  from  TBCHARGEPOLICY where card_no='" + cardNo + "'  and  ISDONE='0'";

                            statement = connection.createStatement();
                            statement.executeUpdate(sql);

                            sql = "insert into TBCHARGEPOLICY_log(SESSION_ID,CARD_NO,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,CHANNEL_TYPE,POLICY_STATUS)" +
                                    " select '" + session_id + "',CARD_NO,ACCOUNT_NO,  '" + date + "', '" + time + "' ,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,'" + Fields.SERVICE_CMS + "','" + Constants.POLICY_STATUS_REVOKED + "'  from  TBCHARGEPOLICY where card_no='" + cardNo + "'  and  ISDONE='0'";

                            statement = connection.createStatement();
                            statement.executeUpdate(sql);

                            sql = "update tbchargepolicy set isdone = '2' where  card_no = '" + cardNo + "'  and  isdone = '0' ";


                            statement = connection.createStatement();
                            statement.executeUpdate(sql);

                        }

                        sql = "update tbcfscardaccount set  STATUS = " + Constants.INACTIVE_ACC_ST + " , MAX_TRANS_LIMIT=0  where  PAN = '" + cardNo + "' AND STATUS=1";
                        statement = connection.createStatement();
                        statement.executeUpdate(sql);

                    }  else{
                        sql = "update tbcfscardaccount set  STATUS = " + Constants.INACTIVE_ACC_ST + " where  PAN = '" + cardNo + "' AND STATUS=1";
                        statement = connection.createStatement();
                        statement.executeUpdate(sql);

                    }
                }

            }

            if (nonCFSCardAccountList.size() > 0) {
                sql = "update tbnoncfscardacc set  STATUS = " + Constants.INACTIVE_ACC_ST + " where  PAN = '" + cardNo + "' AND STATUS=1";
                statement = connection.createStatement();
                statement.executeUpdate(sql);

                Iterator it = nonCFSCardAccountList.iterator();
                while (it.hasNext())
                    InsertNonCFSCardAccountTransactional(connection, (NonCFSCardAccount) it.next(), newCardNo, editDate);
            }

            connection.commit();

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--reCreateCard = " + e + " -- sql = " + sql);
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

    private static void InsertCardAccountTransactional(Connection connection, CardAccount cardAccount, String cardNo, String editDate) throws SQLException

    {
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
                "WITHDRAW_DATE," +
                "WITHDRAW_AMOUNT," +
                "SERIES," +
                "PBWITHDRAW_DATE," +
                "PBWITHDRAW_AMOUNT," +
                "STBWITHDRAW_DATE," +
                "STBWITHDRAW_AMOUNT," +
                "CARD_TYPE," +
                "SEQUENCE_NO" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + cardAccount.getAccountType() + "'," +
                "'" + cardAccount.getAccountNo() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'," +
                "" + Constants.CARD_STATUS_ACTIVE_FLAG + "," +
                "" + cardAccount.getMaxTransLimit() + "," +
                "'" + cardAccount.getWithdrawDate() + "'," +
                "" + cardAccount.getWithdrawAmount() + "," +
                "" + cardAccount.getSeries() + "," +
                "'" + cardAccount.getPBwithdrawDate() + "'," +
                "" + cardAccount.getPBwithdrawAmount() + "," +
                "'" + cardAccount.getSTBwithdrawDate() + "'," +
                "" + cardAccount.getSTBwithdrawAmount() + ",'" +cardAccount.getCardType() +"','" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "'" +
                ")";

        try {
            statement = connection.createStatement();
            statement.execute(sql);


        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertCardAccountTransactional()--CMS<ReCreateCard>-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static void InsertNonCFSCardAccountTransactional(Connection connection, NonCFSCardAccount cardAccount, String cardNo, String editDate) throws SQLException

    {
        Statement statement = null;

        String sql = "insert into TBNONCFSCARDACC(" +
                "PAN," +
                "ACCOUNT_TYPE," +
                "ACCOUNT_NO," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_EDIT_DATE," +
                "STATUS," +
                "MAX_TRANS_LIMIT," +
                "WITHDRAW_DATE," +
                "WITHDRAW_AMOUNT," +
                "SEQUENCE_NO," +
                "PBWITHDRAW_DATE," +
                "PBWITHDRAW_AMOUNT," +
                "STBWITHDRAW_DATE," +
                "STBWITHDRAW_AMOUNT," +
                "NFT_DATE," +
                "NFT_AMOUNT," +
                "PGNFT_DATE," +
                "PGNFT_AMOUNT," +
                "NFTPOS_DATE," +
                "NFTPOS_AMOUNT," +
                "MPNFT_DATE," +
                "MPNFT_AMOUNT," +
                "KSNFT_DATE," +
                "KSNFT_AMOUNT" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + cardAccount.getAccountType() + "'," +
                "'" + cardAccount.getAccountNo() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'," +
                "" + Constants.CARD_STATUS_ACTIVE_FLAG + "," +
                "" + cardAccount.getMaxTransLimit() + "," +
                "'" + cardAccount.getWithdrawDate() + "'," +
                "" + cardAccount.getWithdrawAmount() + "," +
                " '" + Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID + "' ," +
                "'" + cardAccount.getPBwithdrawDate() + "'," +
                "" + cardAccount.getPBwithdrawAmount() + "," +
                "'" + cardAccount.getSTBwithdrawDate() + "'," +
                "" + cardAccount.getSTBwithdrawAmount() + "," +
                "'" + cardAccount.getNft_date() + "'," +
                "" + cardAccount.getNft_amount() + "," +
                "'" + cardAccount.getPgnft_date() + "'," +
                "" + cardAccount.getPgnft_amount() + "," +
                "'" + cardAccount.getNftpos_date() + "'," +
                "" + cardAccount.getNftpos_amount() + "," +
                "'" + cardAccount.getMpnft_date() + "'," +
                "" + cardAccount.getMpnft_amount() + "," +
                "'" + cardAccount.getKsnft_date() + "'," +
                "" + cardAccount.getKsnft_amount() + "" +
                ")";

        try {
            statement = connection.createStatement();
            statement.execute(sql);


        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertNonCFSCardAccountTransactional()--CMS<ReCreateCard>-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
    }

    public static void InsertCardTransactional(Connection connection, String cardNo, String editDate, String customerId, int templateId) throws SQLException {
        Statement statement = null;
        String insertcard_sql = "insert into TBCFSCARD(" +
                "PAN," +
                "CUSTOMER_ID," +
                "SEQUENCE_NO," +
                "TEMPLATE_ID," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "ORIG_EDIT_DATE" +
                ") values (" +
                "'" + cardNo + "'," +
                "'" + customerId.trim() + "'," +
                "'" + Constants.CARD_SEQUENCE_NO_DEFAULT + "'," +
                "" + templateId + "," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "'" + editDate + "'" +
                ")";
        try {
            statement = connection.createStatement();
            statement.execute(insertcard_sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertCardTransactional()--CMS<ReCreateCard>-- #1= -- Error :: " + e);
            throw e;

        } finally {
            if (statement != null) statement.close();
        }

    }

    public static String findAccountHost(String accountNo) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String hostId = Constants.HOST_ID_UNKNOWN;

        try {
            String sql = "select  HOST_id  from tbcustomersrv  where account_No = '" + accountNo + "'   with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();

            if (resultSet.next())
                hostId = resultSet.getString("HOST_id");

            return hostId;
        } catch (SQLException e) {
            log.error(e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String  getCustomerIdByCardNo(String cardNo) throws NotFoundException, SQLException {
        String hql = "select CUSTOMER_ID from tbCFSCard where PAN = '" + cardNo + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("CUSTOMER_ID");
            } else {
                throw new NotFoundException("No card exist for card no " + cardNo);
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getCard() =   -- Error :: " + e + " -- hql = " + hql);
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
    public static String  getSubtitleByCustomerId(String account_no,String customerId) throws NotFoundException, SQLException {
        String hql = "select SUB_TITLE from tbcustacc where account_no = '" + account_no + "' and customer_id='"+customerId+"' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("SUB_TITLE");
            } else {
                throw new NotFoundException("No card exist for customer_id:: " + customerId);
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getSubtitleByCustomerId() =   -- Error :: " + e + " -- hql = " + hql);
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

    public static String[] findCustomerID_getSubTitle(String accountNo, String nationalCode, String externalIdNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String[] customerId=new String [3];

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "select customer_Id,Sub_title,status from tbcustacc where account_no ='" + accountNo + "' and status='1' ";
            if(nationalCode!=null && !nationalCode.trim().equalsIgnoreCase(""))
                sql=sql+" and NATIONAL_CODE='"+nationalCode.trim()+"' ";
            if(externalIdNo!=null && !externalIdNo.trim().equalsIgnoreCase(""))
                sql=sql+" and EXTERNAL_ID_NUMBER='"+externalIdNo.trim()+"' ";
            if((nationalCode==null || nationalCode.trim().equalsIgnoreCase("")) && (externalIdNo==null || externalIdNo.trim().equalsIgnoreCase("")))
                sql=sql+ " and SUB_TITLE='0'";
            sql=sql+" with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {

                customerId[0] = resultSet.getString("CUSTOMER_ID").trim();
                customerId[1]=resultSet.getString("Sub_TITLE").trim();
                customerId[2]=resultSet.getString("status").trim();
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.findCustomerID  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return customerId;
    }

    public static List getChargeRecords(String accountNo, String cardNo, String fromDate, String toDate) throws SQLException {

        String hql = "select EFFECTIVE_DATE,CREATION_DATE,CHARGE_TYPE,AMOUNT,action_code,REQUEST_TYPE " +
                " from tbcharge  where  ACCOUNT_NO='" + accountNo + "' and CARD_NO='" + cardNo + "' and  PROCCESS_STATUS in ('1','2') ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        hql = hql + " order by CREATION_DATE desc, CREATION_TIME desc  fetch first 150 rows only   with ur";
        List charges = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Charge tx = new Charge();
                tx.setChargeDate(resultSet.getString("CREATION_DATE"));
                String effectiveDate=resultSet.getString("EFFECTIVE_DATE");
                if(effectiveDate!=null && !effectiveDate.trim().equalsIgnoreCase(""))
                    tx.setEfectiveDate(effectiveDate.trim());
                else
                    tx.setEfectiveDate("");
                tx.setType(resultSet.getString("CHARGE_TYPE"));
                tx.setAmount(resultSet.getString("AMOUNT"));
                tx.setChargeActionCode(resultSet.getString("ACTION_CODE"));
                tx.setRequestType(resultSet.getString("REQUEST_TYPE"));
                charges.add(tx);
            }
            connection.commit();
            return charges;
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


    public static boolean ExistPolicyInDB(String cardNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select card_No from TBCHARGEPOLICY where Card_no = '" + cardNo + "'  and  ISDONE='0'  with ur";

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

    public static CardPolicy getPolicyByAccountNo(String accNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CardPolicy policy = null;

        String sql = "select * from TBCHARGEPOLICY where Account_no = '" + accNo + "'  and  ISDONE='0'  fetch first 1 rows only with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                policy = new CardPolicy(resultSet.getString("SESSION_ID"), "", resultSet.getString("AMOUNT"), resultSet.getString("ACCOUNT_NO"),
                        resultSet.getString("TYPE"), resultSet.getString("START_DATE"),
                        resultSet.getString("END_DATE"), resultSet.getString("NEXT_DATE"), resultSet.getInt("COUNT"),
                        resultSet.getInt("INTERVAL"), resultSet.getString("ISDONE"), resultSet.getString("INTERVAL_TYPE"));

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
        return policy;
    }

    public static CardPolicy getPolicy(String cardNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CardPolicy policy = null;

        String sql = "select * from TBCHARGEPOLICY where Card_no = '" + cardNo + "'  and  ISDONE='0'  with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                policy = new CardPolicy(resultSet.getString("SESSION_ID"), cardNo, resultSet.getString("AMOUNT"), resultSet.getString("ACCOUNT_NO"),
                        resultSet.getString("TYPE"), resultSet.getString("START_DATE"),
                        resultSet.getString("END_DATE"), resultSet.getString("NEXT_DATE"), resultSet.getInt("COUNT"),
                        resultSet.getInt("INTERVAL"), resultSet.getString("ISDONE"), resultSet.getString("INTERVAL_TYPE"));

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
        return policy;
    }

    public static CardPolicy getEndedPolicy(String cardNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CardPolicy policy = null;

        String sql = "select * from TBCHARGEPOLICY where Card_no = '" + cardNo + "'  and  ISDONE in ('1','3')  order by CREATION_DATE desc, CREATION_DATE desc  fetch first 1 rows only with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                policy = new CardPolicy(resultSet.getString("SESSION_ID"), cardNo, resultSet.getString("AMOUNT"), resultSet.getString("ACCOUNT_NO"),
                        resultSet.getString("TYPE"), resultSet.getString("START_DATE"),
                        resultSet.getString("END_DATE"), resultSet.getString("NEXT_DATE"), resultSet.getInt("COUNT"),
                        resultSet.getInt("INTERVAL"), resultSet.getString("ISDONE"), resultSet.getString("INTERVAL_TYPE"));

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
        return policy;
    }

    public static List getPolicyHistory(String accountNo, String cardNo, String fromDate, String toDate) throws SQLException {

        String hql = "select *  from TBCHARGEPOLICY_LOG  where  CARD_NO='" + cardNo + "'  and  account_NO='" + accountNo + "' ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";
        else
            hql = hql + " order by CREATION_DATE desc, CREATION_TIME desc  fetch first 150 rows only   with ur";
        List history = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                Policy policy = new Policy();
                policy.setType(resultSet.getString("TYPE"));
                policy.setAmount(resultSet.getString("AMOUNT"));
                policy.setChangeDate(resultSet.getString("CREATION_DATE"));
                policy.setChannelType(resultSet.getString("CHANNEL_TYPE"));
                policy.setCount(resultSet.getInt("COUNT"));
                policy.setInterval(resultSet.getInt("INTERVAL"));
                policy.setIntervalType(resultSet.getString("INTERVAL_TYPE"));
                policy.setStartDate(resultSet.getString("START_DATE"));
                policy.setChangeTime(resultSet.getString("CREATION_TIME"));
                String chargeStatus=resultSet.getString("POLICY_STATUS");
                if(chargeStatus==null || chargeStatus.trim().equalsIgnoreCase(""))
                    chargeStatus="";
                policy.setChargeStatus(chargeStatus);
                history.add(policy);
            }
            connection.commit();

//            String sql = "select * from TBCHARGEPOLICY where Card_no = '" + cardNo + "'  and  account_NO='" + accountNo + "'  with ur";
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(sql);
//            connection.commit();
//            if (resultSet.next()) {
//                Policy policy = new Policy();
//                policy.setType(resultSet.getString("TYPE"));
//                policy.setAmount(resultSet.getString("AMOUNT"));
//                policy.setChangeDate( resultSet.getString("CREATION_DATE"));
//                policy.setChannelType( "");
//                policy.setCount(resultSet.getInt("COUNT"));
//                policy.setInterval(resultSet.getInt("INTERVAL"));
//                policy.setIntervalType(resultSet.getString("INTERVAL_TYPE"));
//                policy.setStartDate(resultSet.getString("START_DATE"));
//                policy.setChangeTime( resultSet.getString("CREATION_TIME"));
//                policy.setIsDone( resultSet.getString("ISDONE"));
//
//                history.add(policy);
//            }

            return history;
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


    public static void InsertPolicy4GroupCard(String cardNo, String accountNo, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String channelType) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            String date = DateUtil.getSystemDate();
            String time = DateUtil.getSystemTime();

            String sql = "insert into TBCHARGEPOLICY(" +
                    "SESSION_ID," +
                    "CARD_NO," +
                    "ACCOUNT_NO," +
                    "TYPE," +
                    "START_DATE," +
                    "END_DATE," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NEXT_DATE," +
                    "COUNT," +
                    "AMOUNT," +
                    "INTERVAL," +
                    "INTERVAL_TYPE," +
                    "ISDONE" +
                    ") values (" +
                    "'" + sessionId + "'," +
                    "'" + cardNo + "'," +
                    "'" + accountNo + "'," +
                    "'" + type + "'," +
                    "'" + startDate + "'," +
                    "'" + endDate + "'," +
                    "'" + date + "'," +
                    "'" + time + "'," +
                    "'" + nextDate + "'," +
                    "" + count + "," +
                    "" + amount + "," +
                    "" + interval + "," +
                    "'" + intervalType + "'," +
                    "'" + 0 + "'" +
                    ")";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);

            InsertPolicyHistoryTransactional(connection, cardNo, accountNo, sessionId, type, startDate, count, amount, interval, intervalType, channelType, date, time);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertPolicy4GroupCard()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void InsertPolicy4GroupCardTransactional(Connection connection, String cardNo, String accountNo, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType) throws SQLException {
        Statement statement = null;

        try {

            String sql = "insert into TBCHARGEPOLICY(" +
                    "SESSION_ID," +
                    "CARD_NO," +
                    "ACCOUNT_NO," +
                    "TYPE," +
                    "START_DATE," +
                    "END_DATE," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NEXT_DATE," +
                    "COUNT," +
                    "AMOUNT," +
                    "INTERVAL," +
                    "INTERVAL_TYPE," +
                    "ISDONE" +
                    ") values (" +
                    "'" + sessionId + "'," +
                    "'" + cardNo + "'," +
                    "'" + accountNo + "'," +
                    "'" + type + "'," +
                    "'" + startDate + "'," +
                    "'" + endDate + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + nextDate + "'," +
                    "" + count + "," +
                    "" + amount + "," +
                    "" + interval + "," +
                    "'" + intervalType + "'," +
                    "'" + 0 + "'" +
                    ")";


            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertPolicy4GroupCardTransactional()--CMS-- #1= -- Error :: " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }

    }

    public static void InsertCardAndPolicy(String cardNo, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String editDate, String row, long maxTransLimit, String latinName, ChildInfo childInfo, String accountTitle) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String customerId = "";
        try {
            String accountNo = childInfo.getAccountno();
            connection = dbConnectionPool.getConnection();
            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error(e);
            }
            resultSet.close();
            statement.close();

            String firstName = "";
            String lastName = "";
            if (!latinName.trim().equals("") && !latinName.trim().equals("#")) {
                ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
                firstName = latinStr.get(0).toString();
                if (latinStr.size() > 1)
                    lastName = latinStr.get(1).toString();
            }

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NATIONAL_CODE," +
                    "BIRTHDATE," +
                    "ID_NUMBER," +
                    "ID_ISSUE_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "CELLPHONE," +
                    "STATUSMELLI" +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" + firstName + "'," +
                    "'" + lastName + "'," +
                    "'" + childInfo.getName().trim() + "'," +
                    "'" + childInfo.getFamily().trim() + "'," +
                    "'" + childInfo.getFatherName().trim() + "'," +
                    childInfo.getSex() + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + childInfo.getNationalCode() + "'," +
                    "'" + childInfo.getBirthDate() + "'," +
                    "'" + childInfo.getIDNumber() + "'," +
                    "'" + childInfo.getIssuePlace().trim() + "'," +
                    "'" + childInfo.getFrgCode() + "'," +
                    "'" + childInfo.getMobileNo().trim() + "'," +
                    +Constants.MELLICODE_VALID +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "ACCOUNT_TYPE," +
                    "SUB_TITLE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + childInfo.getNationalCode().trim() + "'," +
                    "'" + childInfo.getFrgCode() + "'," +
                    "'" + childInfo.getAccountno() + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'G'," +
                    "'" + childInfo.getAccountType().trim() + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();

            String insertcard_sql = "insert into TBCFSCARD(" +
                    "PAN," +
                    "CUSTOMER_ID," +
                    "SEQUENCE_NO," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_EDIT_DATE" +
                    ") values (" +
                    "'" + cardNo + "'," +
                    "'" + customerId + "'," +
                    "'" + Constants.CARD_SEQUENCE_NO_DEFAULT + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + editDate + "'" +
                    ")";

            statement = connection.createStatement();
            statement.execute(insertcard_sql);

            InsertCardAccountTransactional(connection, cardNo, accountNo, editDate, childInfo.getAccountgroup(), row, maxTransLimit, Constants.GROUP_CARD_TYPE_STR);
            InsertPolicy4GroupCardTransactional(connection, cardNo, accountNo, sessionId, type, startDate, endDate, nextDate, count, amount, interval, intervalType);
            InsertPolicyHistoryTransactional(connection, cardNo, accountNo, sessionId, type, startDate, count, amount, interval, intervalType, Fields.SERVICE_CMS, DateUtil.getSystemDate(), DateUtil.getSystemTime());
            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.InsertCardAndPolicy()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void UpdatePolicy4GroupCard(String cardNo, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String channelType, String accountNo) throws SQLException {

        Connection connection = null;
        Statement statement = null;

        String update_sql = "update TBCHARGEPOLICY set " +
                "SESSION_ID='" + sessionId + "'," +
                "TYPE='" + type + "'," +
                "START_DATE='" + startDate + "'," +
                "END_DATE='" + endDate + "'," +
                "NEXT_DATE='" + nextDate + "'," +
                "COUNT=" + count + "," +
                "AMOUNT=" + amount + "," +
                "INTERVAL=" + interval + "," +
                "INTERVAL_TYPE='" + intervalType + "'" +
                " where card_no='" + cardNo + "' and  ISDONE='0'  ";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.execute(update_sql);

            InsertPolicyHistoryTransactional(connection, cardNo, accountNo, sessionId, type, startDate, count, amount, interval, intervalType, channelType, DateUtil.getSystemDate(), DateUtil.getSystemTime());
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdatePolicy4GroupCatrd()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void EndedPolicy4GroupCard(String cardNo, String channelType, CardPolicy existPolicy) throws SQLException {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dbConnectionPool.getConnection();
            String update_sql = "update TBCHARGEPOLICY set " +
                    "ISDONE='3'" +
                    " where card_no='" + cardNo + "' and isdone = '0'";

            statement = connection.createStatement();
            statement.execute(update_sql);

            InsertPolicyHistoryTransactional(connection,channelType, existPolicy,Constants.POLICY_STATUS_ENDED) ;

            connection.commit();

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.EndedPolicy4GroupCard()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void InsertPolicyHistoryTransactional(Connection connection, String channelType, CardPolicy existPolicy, String  isDone) throws SQLException {
        Statement statement = null;
        String sql = "insert into TBCHARGEPOLICY_LOG(" +
                "SESSION_ID," +
                "CARD_NO," +
                "ACCOUNT_NO," +
                "TYPE," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "COUNT," +
                "AMOUNT," +
                "INTERVAL," +
                "INTERVAL_TYPE," +
                "CHANNEL_TYPE," +
                "START_DATE," +
                "POLICY_STATUS" +
                ") values (" +
                "'" + existPolicy.getSessionId() + "'," +
                "'" + existPolicy.getCardno() + "'," +
                "'" + existPolicy.getAccountNo() + "'," +
                "'" + existPolicy.getType() + "'," +
                "'" + DateUtil.getSystemDate() + "'," +
                "'" + DateUtil.getSystemTime() + "'," +
                "" + existPolicy.getCount() + "," +
                "" + existPolicy.getAmount() + "," +
                "" + existPolicy.getInterval() + "," +
                "'" + existPolicy.getIntervalType() + "'," +
                "'" + channelType + "'," +
                "'" + existPolicy.getStartDate() + "'," +
                "'" + isDone + "'" +
                ")";
        try {
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertPolicyHistoryTransactional()-Error :: " + e);
            throw e;

        } finally {
            if (statement != null) statement.close();
        }

    }

    public static void InsertPolicyHistoryTransactional(Connection connection, String cardNo, String accountNo, String sessionId, String type, String startDate, int count, String amount, int interval, String intervalType, String channelType, String date, String time) throws SQLException {
        Statement statement = null;
        String sql = "insert into TBCHARGEPOLICY_LOG(" +
                "SESSION_ID," +
                "CARD_NO," +
                "ACCOUNT_NO," +
                "TYPE," +
                "CREATION_DATE," +
                "CREATION_TIME," +
                "COUNT," +
                "AMOUNT," +
                "INTERVAL," +
                "INTERVAL_TYPE," +
                "CHANNEL_TYPE," +
                "START_DATE" +
                ") values (" +
                "'" + sessionId + "'," +
                "'" + cardNo + "'," +
                "'" + accountNo + "'," +
                "'" + type + "'," +
                "'" + date + "'," +
                "'" + time + "'," +
                "" + count + "," +
                "" + amount + "," +
                "" + interval + "," +
                "'" + intervalType + "'," +
                "'" + channelType + "'," +
                "'" + startDate + "'" +
                ")";
        try {
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InsertPolicyHistoryTransactional()--2 -Error :: " + e);
            throw e;

        } finally {
            if (statement != null) statement.close();
        }

    }

    public static void UpdatePolicy4CardNoListTransactional(Connection connection, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, CardNoList cards) throws SQLException {
        PreparedStatement statement = null;

        String update_sql = "update TBCHARGEPOLICY set " +
                "SESSION_ID='" + sessionId + "'," +
                "TYPE='" + type + "'," +
                "START_DATE='" + startDate + "'," +
                "END_DATE='" + endDate + "'," +
                "NEXT_DATE='" + nextDate + "'," +
                "COUNT=" + count + "," +
                "AMOUNT=" + amount + "," +
                "INTERVAL=" + interval + "," +
                "INTERVAL_TYPE='" + intervalType + "'" +
                " where card_no=?  and  ISDONE='0'  ";


        try {
            statement = connection.prepareStatement(update_sql);

            for (int i = 0; i < cards.getCardNo().size(); i++) {
                statement.setString(1, cards.getCardNo().get(i));
                statement.addBatch();
            }
            statement.executeBatch();

        } catch (SQLException e) {
            log.error(e);
            throw e;

        } finally {
            if (statement != null) statement.close();
        }

    }

    public static void UpdatePolicy4CardNoList(String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String channelType, CardNoList cards) throws SQLException {
        Connection connection = null;
        PreparedStatement statement2 = null;
        String insertSql = "insert into TBCHARGEPOLICY_LOG(SESSION_ID,CARD_NO,ACCOUNT_NO,CREATION_DATE,CREATION_TIME,TYPE,START_DATE,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,CHANNEL_TYPE)" +
                " select '" + sessionId + "',CARD_NO,ACCOUNT_NO,  '" + DateUtil.getSystemDate() + "', '" + DateUtil.getSystemTime() + "' ,'" + type + "','" + startDate + "'," + count + "," + amount + "," + interval + ",'" + intervalType + "','" + channelType + "'  from  TBCHARGEPOLICY where card_no=?   and  ISDONE='0' ";
        try {
            connection = dbConnectionPool.getConnection();
            statement2 = connection.prepareStatement(insertSql);

            for (int i = 0; i < cards.getCardNo().size(); i++) {
                statement2.setString(1, cards.getCardNo().get(i));
                statement2.addBatch();
            }
            statement2.executeBatch();
            UpdatePolicy4CardNoListTransactional(connection, sessionId, type, startDate, endDate, nextDate, count, amount, interval, intervalType, cards);
            connection.commit();


        } catch (SQLException e) {
            log.error("ChannelFacadeNew.UpdatePolicy4CardNoList()-Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdatePolicy4CardNoList()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }

            throw e;

        } finally {
            if (statement2 != null) statement2.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void UpdatePolicy4AccountNoTransactional(Connection connection, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String accountNo) throws SQLException {
        Statement statement = null;

        String update_sql = "update TBCHARGEPOLICY set " +
                "SESSION_ID='" + sessionId + "'," +
                "TYPE='" + type + "'," +
                "START_DATE='" + startDate + "'," +
                "END_DATE='" + endDate + "'," +
                "NEXT_DATE='" + nextDate + "'," +
                "COUNT=" + count + "," +
                "AMOUNT=" + amount + "," +
                "INTERVAL=" + interval + "," +
                "INTERVAL_TYPE='" + intervalType + "'" +
                " where Account_no='" + accountNo + "' and  ISDONE='0' ";
        try {
            statement = connection.createStatement();
            statement.executeUpdate(update_sql);

        } catch (SQLException e) {
            log.error(e);
            throw e;

        } finally {
            if (statement != null) statement.close();
        }

    }

    public static void UpdatePolicy4AccountNo(String accountNo, String sessionId, String type, String startDate, String endDate, String nextDate, int count, String amount, int interval, String intervalType, String channelType) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String sql = "insert into TBCHARGEPOLICY_LOG(SESSION_ID,CARD_NO,ACCOUNT_NO,TYPE,CREATION_DATE,CREATION_TIME,COUNT,AMOUNT,INTERVAL,INTERVAL_TYPE,channel_Type ,START_DATE )" +
                " select '" + sessionId + "',CARD_NO,ACCOUNT_NO,'" + type + "','" + DateUtil.getSystemDate() + "'," +
                "                  '" + DateUtil.getSystemTime() + "'," + count + "," + amount + "," +
                "" + interval + ",'" + intervalType + "','" + channelType + "','" + startDate + "' from  TBCHARGEPOLICY where account_no='" + accountNo + "' and  ISDONE='0' ";
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);

            UpdatePolicy4AccountNoTransactional(connection, sessionId, type, startDate, endDate, nextDate, count, amount, interval, intervalType, accountNo);

            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.UpdatePolicy4AccountNo()-Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdatePolicy4GroupCatrd()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static void UpdateLatinName4Card(String customerId, String latinName) throws SQLException {
        Statement statement = null;
        Connection connection = null;

        String firstName="";
        String lastName="";
        if(!latinName.trim().equals("")  && !latinName.trim().equals("#")){
            ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
            firstName =latinStr.get(0).toString();
            if(latinStr.size()>1 )
                lastName =latinStr.get(1).toString();
        }

         String sql = "Update tbcustomer set CARD_HOLDER_NAME='"+firstName+"' ,CARD_HOLDER_SURNAM='"+lastName+"' where customer_id='"+customerId+"'";

          try {
              connection = dbConnectionPool.getConnection();
              statement = connection.createStatement();
              statement.executeUpdate(sql);
              connection.commit();
          } catch (SQLException e) {
              log.error("ChannelFacadeNew.UpdateLatinName4Card()--CMS-- #1= -- Error :: " + e);
              try {
                  connection.rollback();
              } catch (SQLException e1) {
                  log.error("ChannelFacadeNew.UpdateLatinName4Card()=  Can not rollback -- Error :: " + e1);
                  e1.printStackTrace();
              }
              throw e;
          } finally {
              if (statement != null) statement.close();
              dbConnectionPool.returnConnection(connection);
          }
      }
    private static void UpdateLatinName4CardTransactional(Connection connection, String customerId, String latinName) throws SQLException {
        Statement statement = null;

        String firstName="";
        String lastName="";
        if(!latinName.trim().equals("")  && !latinName.trim().equals("#")){
            ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
            firstName =latinStr.get(0).toString();
            if(latinStr.size()>1 )
                lastName =latinStr.get(1).toString();
        }

        String sql = "Update tbcustomer set CARD_HOLDER_NAME='"+firstName+"' ,CARD_HOLDER_SURNAM='"+lastName+"' where customer_id='"+customerId+"'";

         try {
             statement = connection.createStatement();
             statement.executeUpdate(sql);
         } catch (SQLException e) {
             log.error("ChannelFacadeNew.UpdateLatinName4CardTransactional()--CMS-- #1= -- Error :: " + e);
             throw e;
         } finally {
             if (statement != null) statement.close();
         }
     }
    private static void InsertLatinName4CardTransactional(Connection connection, String customerId, String latinName) throws SQLException {
          Statement statement = null;
        String firstName="";
        String lastName="";
        if(!latinName.trim().equals("")  && !latinName.trim().equals("#")){
            ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
            firstName =latinStr.get(0).toString();
            if(latinStr.size()>1 )
                lastName =latinStr.get(1).toString();
        }
                String sql = "insert into TBCUSTOMER(" +
                        "CUSTOMER_ID," +
                        "CARD_HOLDER_NAME," +
                        "CARD_HOLDER_SURNAM," +
                        "CREATION_DATE," +
                        "CREATION_TIME" +
                        ") values(" +
                        "'" + customerId + "'," +
                        "'" + firstName + "'," +
                        "'" + lastName + "'," +
                        "'" + DateUtil.getSystemDate() + "'," +
                        "'" + DateUtil.getSystemTime() + "'" +
                        " )";
           try {
               statement = connection.createStatement();
               statement.execute(sql);
           } catch (SQLException e) {
               log.error("ChannelFacadeNew.InsertLatinName4CardTransactional()--CMS-- #1= -- Error :: " + e);
               throw e;
           } finally {
               if (statement != null) statement.close();
           }
       }
    public static void UpdateChildCardAndAccount(String accountNo, String editDate, String latinName,ChildInfo childInfo,String customerId) throws SQLException, NotFoundException {

          Connection connection = null;
          Statement statement = null;
          ResultSet resultSet = null;
        String firstName="";
        String lastName="";
        if(!latinName.trim().equals("")  && !latinName.trim().equals("#")){
            ArrayList latinStr = CMUtil.tokenizString(latinName, "#");
            firstName =latinStr.get(0).toString();
            if(latinStr.size()>1 )
                lastName =latinStr.get(1).toString();
        }
          try {
              connection = dbConnectionPool.getConnection();
              String sql = "update TBCUSTOMER set " +
                      "CARD_HOLDER_NAME = '" + firstName + "'," +
                      "CARD_HOLDER_SURNAM = '" +lastName+ "'," +
                      "NAME_FA ='" + childInfo.getName().trim() + "'," +
                      "SURNAME_FA ='" + childInfo.getFamily().trim() + "'," +
                      "FATHERNAME ='" + childInfo.getFatherName().trim() + "'," +
                      "SEX =" + childInfo.getSex() + "," +
                      "NATIONAL_CODE ='" +childInfo.getNationalCode()+ "'," +
                      "BIRTHDATE ='" + childInfo.getBirthDate() + "'," +
                      "ID_NUMBER ='" +childInfo.getIDNumber()  + "'," +
                      "ID_ISSUE_CODE ='" + childInfo.getIssuePlace().trim() + "'," +
                      "EXTERNAL_ID_NUMBER ='" +childInfo.getFrgCode()  + "'," +
                      "ORIG_EDIT_DATE ='" +editDate + "'," +
                      "CELLPHONE ='" +childInfo.getMobileNo().trim()  + "'" +
                      " where CUSTOMER_ID = '" + customerId + "'";

            statement = connection.createStatement();
            int n = statement.executeUpdate(sql);
            statement.close();
            if (n == 0)
                throw new NotFoundException("Customer Not Found in tbCustomer, Customer_ID=" + customerId);

            sql = "update TBCUSTACC set " +
                    "NATIONAL_CODE= '" + childInfo.getNationalCode().trim() + "'," +
                    "EXTERNAL_ID_NUMBER= '" + childInfo.getFrgCode() + "'" +
                    " where ACCOUNT_NO= '" + accountNo + "' and CUSTOMER_ID =  '" + customerId + "'";

            statement = connection.createStatement();
            n = statement.executeUpdate(sql);
            statement.close();
            if (n == 0)
                throw new NotFoundException("Customer Not Found in tbCustomer, Customer_ID=" + customerId);

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateChildCardAndAccount,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateChildCardAndAccount,  Can not rollback -- Error2 :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static Charge getLastCharge(String cardNo) throws SQLException {

        String sql = "select EFFECTIVE_DATE,CREATION_DATE,CHARGE_TYPE,AMOUNT,action_code   from tbcharge  where  CARD_NO='" + cardNo + "'"
                + "  and  PROCCESS_STATUS='1' and Request_type='1' order by CREATION_DATE desc, CREATION_TIME desc  fetch first 1 rows only   with ur";

        String amount = "";
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Charge charge = new Charge();
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                charge.setChargeDate(resultSet.getString("CREATION_DATE"));
                charge.setEfectiveDate(resultSet.getString("EFFECTIVE_DATE"));
                charge.setType(resultSet.getString("CHARGE_TYPE"));
                charge.setAmount(resultSet.getString("AMOUNT"));
                charge.setChargeActionCode(resultSet.getString("ACTION_CODE"));
            }
            connection.commit();
            return charge;
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

    public static String getAccountNoByCustomerId(String customerId) throws NotFoundException, SQLException {
        String sql = "select account_no from tbcustacc where CUSTOMER_ID = '" + customerId + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("account_no");
            } else {
                throw new NotFoundException("No account exist for customerId " + customerId);
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountNoByCustomerId() =   -- Error :: " + e + " -- sql = " + sql);
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

    public static String getAccountTypeByCustomerId(String customerId) throws NotFoundException, SQLException {
        String sql = "select account_type from tbcustacc where CUSTOMER_ID = '" + customerId + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                return resultSet.getString("account_type");
            } else {
                throw new NotFoundException("No account exist for customerId " + customerId);
            }
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.getAccountTypeByCustomerId() =   -- Error :: " + e + " -- sql = " + sql);
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

    public static List getChargeRecords(String accountNo, String fromDate, String toDate) throws SQLException {

        String hql = "select EFFECTIVE_DATE,CREATION_DATE,AMOUNT,CARD_NO " +
                " from tbcharge  where  ACCOUNT_NO='" + accountNo + "' and  PROCCESS_STATUS='1' ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        hql = hql + " order by CREATION_DATE desc, CREATION_TIME desc  fetch first 150 rows only   with ur";
        List charges = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                ChargeReport tx = new ChargeReport();
                tx.setChargeDate(resultSet.getString("CREATION_DATE"));
                tx.setEfectiveDate(resultSet.getString("EFFECTIVE_DATE"));
                tx.setAmount(resultSet.getString("AMOUNT"));
                tx.setCardNo(resultSet.getString("CARD_NO"));

                charges.add(tx);
            }
            connection.commit();
            return charges;
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

    public static String getTotalChargeAmount(String accountNo, String fromDate, String toDate) throws SQLException {

        String hql = "select sum(AMOUNT) as TAmount " +
                " from tbcharge  where  ACCOUNT_NO='" + accountNo + "' and  PROCCESS_STATUS='1' ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";
        else
            hql = hql + " with ur";
//                hql = hql + " fetch first 150 rows only  with ur";
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            resultSet.next();
            return resultSet.getString("TAMOUNT");
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

    public static int UpdateSMSRegistration4GroupCard(String accountNo, String cardNo, String registerStr) throws SQLException {
        int updateCount = 0;
        Connection connection = null;
        Statement statement = null;

        String sql = "update tbcfscardaccount set SMS_REGISTER='" + registerStr + "'"
                + " where ACCOUNT_NO = '" + accountNo + "' And PAN = '" + cardNo + "' AND STATUS=1 AND SERIES>1";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--UpdateSMSRegistration4GroupCard = " + e + " -- sql = " + sql);
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
        return updateCount;
    }

    public static String getSMSRegistration(String accountNo, String cardNo) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select SMS_REGISTER from TBCFSCARDACCOUNT where PAN = '" + cardNo + "' and ACCOUNT_NO = '" + accountNo + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return resultSet.getString("SMS_REGISTER");
            else
                throw new NotFoundException("Can not Find pan:" + cardNo);


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

    public static boolean checkParentSmsStatus(String accountNo) throws SQLException,NotFoundException {
        boolean smsStatus = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select SMSNOTIFICATION from tbcustomersrv where ACCOUNT_NO = '" + accountNo + "' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if(resultSet.next()){
                String smsNotification=resultSet.getString("SMSNOTIFICATION");
                if(smsNotification!=null && smsNotification.trim().equalsIgnoreCase("1"))
                    smsStatus=true;
            }else{
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--checkParentSmsStatus() = " + e + " -- sql = " + sql);
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
        return smsStatus;
    }

    public static boolean isCustomerExist(String customer_id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select customer_id from tbcustomer where customer_id = '" + customer_id + "' with ur";

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

    public static Map<Long, String> fillCMParamMap(String modifier) throws SQLException
    {
        if(cmParamMap.containsKey(modifier))
            return  cmParamMap.get(modifier);

        String sql = "select ID, DESCRIPTION  from tbcmparams where modifier = '" + modifier +"'   with ur";
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
            if(map.size() == 0){
                log.error("*******************************************************************************");
                log.error("::: Error: There is no record in tbcmparams for modifier: '" + modifier + "'"  );
                log.error("*******************************************************************************");
            }
            connection.commit();
            cmParamMap.put(modifier, map);
            return  map;
        }catch(SQLException e){
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

    public static String[] getSummaryReport(String accountNo, String cardNo, String fromDate, String toDate) throws SQLException {

        String[] result=new String[4];
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        long credit=0;
        long debit=0;

        String trSrc = "select AMOUNT,CARD_No,FEE_AMOUNT,TOTAL_DEST_ACCOUNT,IS_REVERSAL_TXN  from TBcfstx  where src_account_no='"+accountNo+"' and  CARD_NO='" + cardNo + "'";

        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals(""))
            trSrc = trSrc + " and  CREATION_DATE  between '" +  fromDate + "' and '" + toDate + "'";

        trSrc = trSrc + " with ur";


        String trDest = "select AMOUNT,CARD_No,FEE_AMOUNT,TOTAL_DEST_ACCOUNT,IS_REVERSAL_TXN  from TBcfstx  where dest_account_no='"+accountNo+"' and  TOTAL_DEST_ACCOUNT='" + cardNo + "'";

        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals(""))
            trDest = trDest + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        trDest = trDest + " with ur";

        String charge = "select sum(AMOUNT)  from tbcharge  where account_no='"+accountNo+"' and  CARD_NO='" + cardNo + "' and REQUEST_TYPE='"+Constants.CHARGE+"'";

        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals(""))
            charge = charge + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        charge = charge + " with ur";

        String dCharge = "select sum(AMOUNT)  from tbcharge  where account_no='"+accountNo+"' and  CARD_NO='" + cardNo + "' and REQUEST_TYPE='"+Constants.DCHARGE+"'";

        if (fromDate != null && !fromDate.equals("") && toDate != null && !toDate.equals(""))
            dCharge = dCharge + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        dCharge = dCharge + " with ur";


        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery(trSrc);
            while (resultSet.next()) {
                if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED)) {
//                    tx.setCRDB(Constants.CREDIT);
                    credit = credit + (resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                } else {
//                    tx.setCRDB(Constants.DEBIT);
                    debit = debit + (resultSet.getLong("AMOUNT") + resultSet.getLong("FEE_AMOUNT"));
                }
            }

            resultSet = statement.executeQuery(trDest);
            while (resultSet.next()) {
                if (resultSet.getString("IS_REVERSAL_TXN").equals(Constants.REVERSED)) {
//                    tx.setCRDB(Constants.DEBIT);
                    debit = debit + resultSet.getLong("AMOUNT");
                } else {
//                    tx.setCRDB(Constants.CREDIT);
                    credit = credit + resultSet.getLong("AMOUNT");
                }
            }

            result[0]=String.valueOf(credit);

            result[1]=String.valueOf(debit);

            resultSet = statement.executeQuery(charge);
            if (resultSet.next())
                result[2]=(resultSet.getString(1));


            resultSet = statement.executeQuery(dCharge);
            if (resultSet.next())
                result[3]=(resultSet.getString(1));


            connection.commit();

            return result;
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

    //****************CMS*******end***********

    public static List<String> getPatternsList(String accNo) throws SQLException, ISOException {

        String sql = "select PATTERN FROM tb_paycode_pattern where " +
                "ACCOUNT_NO = '" + accNo + "' and STATUS = '" + Constants.ACTIVE_PATTERN + "' and VALIDITY = '" + Constants.VALID_PATTERN + "' with ur";
        Connection conn = null;
        Statement stm = null;
        ResultSet resultSet = null;
        List<String> list = new ArrayList<String>();
        try {
            conn = dbConnectionPool.getConnection();
            stm = conn.createStatement();
            resultSet = stm.executeQuery(sql);

            while (resultSet.next()) {
                String pattern = resultSet.getString("PATTERN").trim();
                list.add(pattern);
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(conn);
        }
        return list;
    }
    public static String  getWFPRevokedCards(String accountNo, String fromDate, String toDate) throws SQLException {

        String hql = "select sum(decimal(amount)) as amount " +
                " from tb_blck_log  where  ACCOUNT_NO='" + accountNo + "' and TX_CODE='" + Constants.DEACTIVE_CHILD_CARD_CFD + "' and SERVICE='" + Constants.GROUP_CARD + "'  ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

            hql = hql + " with ur";


        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            connection.commit();
            if (resultSet.next())
                return resultSet.getString("amount");

            else return "0";

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

    public static List getWFPCharges(String accountNo, String fromDate, String toDate) throws SQLException {

        String hql = "select CREATION_DATE,sum(AMOUNT) as amount,CHARGE_FILENAME,REQUEST_TYPE " +
                " from tbcharge  where  ACCOUNT_NO='" + accountNo + "' and  PROCCESS_STATUS='1'  ";

        if (fromDate != null && !fromDate.equals(""))
            hql = hql + " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

        hql = hql + " group by CHARGE_FILENAME,CREATION_DATE,REQUEST_TYPE order by CREATION_DATE desc fetch first 150 rows only   with ur";
        List charges = new ArrayList();
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);
            while (resultSet.next()) {
                WFPCharge tx = new WFPCharge();
                tx.setChargeDate(resultSet.getString("CREATION_DATE"));
                tx.setChargeTime("230000");
                tx.setAmount(resultSet.getString("AMOUNT"));
                tx.setRequestType(resultSet.getString("REQUEST_TYPE"));
                tx.setFileName(resultSet.getString("CHARGE_FILENAME"));

                charges.add(tx);
            }
            connection.commit();
            return charges;
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

    //****************Tourist*******start***********
    public static void insertServiceLog(String sessionId, String pin, String messageId, String messageSequence,
                                        String branchCode, String userId, String txDate, String txTime, String accountNo,
                                        String cardNo, String actionCode, String txString, long duration, String channelId) throws SQLException {

        if (txString.length() > 800)
            txString = txString.substring(0, 800);
        try {
            String sql = "insert into tb_service_log " +
                    "(PARTNO,SESSION_ID,PIN,MESSAGE_ID,MESSAGE_SEQUENCE,CHANNEL_ID,BRANCH_CODE," +
                    "USER_ID,TX_DATETIME,TX_DATE,TX_TIME,ACCOUNT_NO,CARD_NO,ACTIONCODE,TX_STRING,DURATION) " +
                    " values(" +
                    getPartNo() + ",'" + sessionId + "','" + pin + "','" + messageId + "','" + messageSequence + "','" + channelId+ "','" +
                    branchCode + "','" + userId + "',current_timestamp,'" + txDate + "','" + txTime + "','" +
                    accountNo + "','" + cardNo + "','" + actionCode + "','" + txString + "'," + duration + ")";
            executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        }

    }

    public static List<String> findServiceLogs(String messageSequence, String serviceType) throws SQLException {
        List<String> logSet = new ArrayList<String>();
        String hql = "select Session_id,Message_id,PIN,CARD_NO from tb_service_log where " +
                " MESSAGE_SEQUENCE = '" + messageSequence + "'";

        if (serviceType.equalsIgnoreCase(Constants.TOURIST_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_TOURIST+"'";
        else if (serviceType.equalsIgnoreCase(Constants.CREDITS_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_CREDITS+"'";
        else if (serviceType.equalsIgnoreCase(Constants.AMX_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_AMX+"'";
        else if (serviceType.equalsIgnoreCase(Constants.SAFE_BOX_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_SAFE_BOX+"'";
        else if (serviceType.equalsIgnoreCase(Constants.SMS_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_SMS +"'";
        else if (serviceType.equalsIgnoreCase(Constants.MARHONAT_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_MARHONAT_INSURANCE+"'";
        else if (serviceType.equalsIgnoreCase(Constants.MANZUME_SERVICE))
            hql = hql + " and CHANNEL_ID='"+Constants.TERMINAL_ID_MANZUME+"'";


        hql = hql + "  order by message_Id desc with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                logSet.add(resultSet.getString("Session_id"));
                logSet.add(resultSet.getString("Message_id"));
                logSet.add(resultSet.getString("PIN"));
                logSet.add(resultSet.getString("CARD_NO"));
            }
            connection.commit();
            return logSet;
        } catch (SQLException e) {
            log.error("EEROR ::: SQLException ::: Inside ChannelFacadeNew.findServiceLogs, error ::: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static String[] getTouristSummaryReport(String accountNo, String cardNo, String fromDate, String toDate, String paymentId, String transactionType, boolean isAccountBase) throws SQLException {

        String[] result = new String[2];
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "";
        try {

            if (isAccountBase) {
                if (transactionType.equalsIgnoreCase(Constants.DEPOSIT_TRANSACTION)) {
                    // request is account base and transaction is deposit
                    sql = "select sum(amount), count(*) from tbcfstx where ((src_account_no='" + accountNo + "' and IS_REVERSAL_TXN='1') or (dest_account_no='" + accountNo + "' and IS_REVERSAL_TXN='0'))" +
                            " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

                    if (paymentId != null && !paymentId.equals(""))
                        sql = sql + " and  ID1='" + paymentId + "'";

                    sql = sql + " with ur";

                } else {
                    // request is account base and transaction is withdraw
                    sql = "select sum(amount), count(*) from tbcfstx where ((src_account_no='" + accountNo + "' and IS_REVERSAL_TXN='0') or (dest_account_no='" + accountNo + "' and IS_REVERSAL_TXN='1'))" +
                            " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

                    if (paymentId != null && !paymentId.equals(""))
                        sql = sql + " and  ID1='" + paymentId + "'";

                    sql = sql + " with ur";

                }
            } else {
                if (transactionType.equalsIgnoreCase(Constants.DEPOSIT_TRANSACTION)) {
                    // request is card base and transaction is deposit
                    sql = "select sum(amount), count(*) from TB_TOURIST_LOG where PAN='" + cardNo + "' and ((TX_TYPE='D' and ISREVERSED=0) or " +
                            "(TX_TYPE='W' and ISREVERSED=1))" +
                            " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

                    sql = sql + " with ur";

                } else {
                    // request is card base and transaction is withdraw
                    sql = "select sum(amount), count(*) from TB_TOURIST_LOG where PAN='" + cardNo + "' and ((TX_TYPE='D' and ISREVERSED=1) or " +
                            "(TX_TYPE='W' and ISREVERSED=0))" +
                            " and  CREATION_DATE  between '" + fromDate + "' and '" + toDate + "'";

                    sql = sql + " with ur";
                }
            }

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                result[0] = resultSet.getString(1);
                result[1] = resultSet.getString(2);
            }
            connection.commit();

            return result;
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

    //****************Tourist*******end***********
    //****************AMX*******start***********
    public static void createAMXNAC(AmxMessage amxMessage,String origAccountTitles,String session_id,String channel_type) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement nacLogStatment=null;
        String customerId = "";
        String accountNo = amxMessage.getAccountNo();

        try {

            connection = dbConnectionPool.getConnection();
            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error(e);
            }
            resultSet.close();
            statement.close();

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "OWNER_INDEX," +
                    "SEX," +
                    "ORIG_CREATE_DATE," +    // create date
                    "ORIG_EDIT_DATE," +      // change date
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "NATIONAL_CODE," +
                    "BIRTHDATE," +
                    "ID_NUMBER," +
                    "ID_SERIAL_NO," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_PLACE," +
                    "ID_ISSUE_CODE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "POSTALCODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "FOREIGN_COUNTRY_CODE," +
                    "STATEMENT_TYPE," +
                    "CELLPHONE," +
                    "FAX," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "STATUSMELLI," +
                    "EMAILADDRESS" +
                    ") values(" +
                    "'" + customerId + "'," +
                    "''," +
                    "''," +
                    "'" + amxMessage.getFirstName().trim() + "'," +
                    "'" + amxMessage.getLastName().trim() + "'," +
                    "'" + amxMessage.getFatherName().trim() + "'," +
                    "'" + amxMessage.getOwnerIndex() + "'," +
                    amxMessage.getGender() + "," +
                    "'" + amxMessage.getCreateDate() + "'," +
                    "'" + amxMessage.getChangeDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + amxMessage.getNationalCode().trim() + "'," +
                    "'" + amxMessage.getBirthDate() + "'," +
                    "'" + amxMessage.getID_Number() + "'," +
                    "'" + amxMessage.getID_SerialNumber() + "'," +
                    "'" + amxMessage.getID_Series() + "'," +
                    "'" + amxMessage.getID_IssueDate() + "'," +
                    "'" + amxMessage.getID_IssuePlace().trim() + "'," +
                    "'" + amxMessage.getID_IssueCode() + "'," +
                    "'" + amxMessage.getTel_Number1().trim() + "'," +
                    "'" + amxMessage.getTel_Number2().trim() + "'," +
                    "'" + amxMessage.getPostalCode() + "'," +
                    "'" + amxMessage.getExt_IdNumber() + "'," +
                    "'" + amxMessage.getForeignCountryCode() + "'," +
                    amxMessage.getStatementType() + "," +
                    "'" + amxMessage.getMob_Number().trim() + "'," +
                    "'" + amxMessage.getFax_Number().trim() + "'," +
                    "'" + amxMessage.getAddress1().trim() + "'," +
                    "'" + amxMessage.getAddress2().trim() + "',"
                    + amxMessage.getNationalCodeValid().trim() + "," +
                    "'" + amxMessage.getEmailAddress().trim() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String accountTitle = "0";
            String subTitle = "0";
            if (!amxMessage.getOwnerIndex().equalsIgnoreCase("00")) {
                if(origAccountTitles.trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    accountTitle = Constants.SHARED_ACCOUNT_TYPE;
                    subTitle = amxMessage.getAccountType();
                } else if (origAccountTitles.trim().equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    accountTitle = Constants.HOGHOOGHI_ACCOUNT_TYPE;
                    subTitle = amxMessage.getAccountType();
                }

            } else {
                accountTitle = amxMessage.getAccountType();
                subTitle = "0";
            }


            String account_type=Constants.NORMAL_ACCOUNT;
            if(amxMessage.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE1) || amxMessage.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE2)
                    || amxMessage.getAccountGroup().equalsIgnoreCase(Constants.GROUP_CARD_ACCOUNT_TYPE3))
                account_type=Constants.GROUP_CARD_ACCOUNT;


            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE," +
                    "ACCOUNT_TYPE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + amxMessage.getNationalCode().trim() + "'," +
                    "'" + amxMessage.getExt_IdNumber() + "'," +
                    "'" + amxMessage.getAccountNo() + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    "'" + account_type + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();


            if(amxMessage.getOwnerIndex().equalsIgnoreCase("00")){

                String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                        "CUSTOMER_ID," +
                        "ACCOUNT_TYPE," +     // Account Group
                        "ACCOUNT_NO," +
                        "LOCK_STATUS," +
                        "CURRENCY," +
                        "BALANCE," +
                        "ACCOUNT_TITLE," +
                        "STATUS," +        // Account Status default = 1 = Normal, CMDB default = null
                        "ACCOUNT_SRC," +
                        "CREATION_DATE," +
                        "CREATION_TIME," +
                        "ORIG_CREATE_DATE," +
                        "ORIG_EDIT_DATE," +
                        "SGB_BRANCH_ID," +
                        "HOST_ID, " +
                        "ACCOUNT_OPENER_NAME, " +
                        "WITHDRAW_TYPE " +
                        ") values(" +
                        "'" + customerId + "'," +
                        "'" + amxMessage.getAccountGroup() + "'," +
                        "'" + amxMessage.getAccountNo() + "'," +
                        "1," +
                        "'" + amxMessage.getCurrencyCode() + "'," +
                        "0, " +
                        "'" + amxMessage.getAccountType() + "'," +
                        "1," +
                        2 + "," +
                        "'" + DateUtil.getSystemDate() + "'," +
                        "'" + DateUtil.getSystemTime() + "'," +
                        "'" + amxMessage.getCreateDate() + "'," +
                        "'" + amxMessage.getChangeDate() + "'," +
                        "'" + amxMessage.getBranchCode() + "'," +
                        Constants.HOST_CFS + "," +
                        "'" + amxMessage.getAccountOpenerName().trim() + "'," +
                        amxMessage.getWithdrawType() +
                        ")";
                statement = connection.createStatement();
                statement.execute(insertCustomerAccount_sql);
                statement.close();

                String customersrvId = existCustomerAccountInSRV(amxMessage.getAccountNo());

                if (customersrvId != null) {
                    updatetCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS, amxMessage.getNationalCode().trim());
                } else {
                    insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                            amxMessage.getAccountGroup(), amxMessage.getNationalCode().trim(), amxMessage.getNationalCodeValid(),customerId);
                }
            }

            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "BIRTHDATE," +
                    "CELLPHONE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "EMAILADDRESS," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ID_NUMBER," +
                    "ID_ISSUE_PLACE," +
                    "ID_SERIAL_NO," +
                    "OPERATION_TYPE," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_CODE," +
                    "POSTALCODE," +
                    "FAX," +
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
                    "'" + amxMessage.getAccountNo() + "'," +
                    "'" + amxMessage.getFirstName().trim() + "'," +
                    "'" + amxMessage.getLastName().trim() + "'," +
                    "'" + amxMessage.getFatherName().trim() + "'," +
                    amxMessage.getGender() + "," +
                    "'" + amxMessage.getBirthDate() + "'," +
                    "'" + amxMessage.getMob_Number().trim() + "'," +
                    "'" + amxMessage.getTel_Number1().trim() + "'," +
                    "'" + amxMessage.getTel_Number2().trim() + "'," +
                    "'" + amxMessage.getEmailAddress().trim() + "'," +
                    "'" + amxMessage.getAddress1().trim() + "'," +
                    "'" + amxMessage.getAddress2().trim() + "',"+
                    "'" + amxMessage.getAccountType() + "',"+
                    "'" + amxMessage.getNationalCode().trim() + "'," +
                    "'" + amxMessage.getExt_IdNumber() + "'," +
                    "'" + amxMessage.getID_Number() + "'," +
                    "'" + amxMessage.getID_IssueCode().trim() + "'," +
                    "'" + amxMessage.getID_SerialNumber() + "'," +
                    "'" + Constants.CREATE_NAC_STATUS + "'," +
                    "'" + amxMessage.getID_Series() + "'," +
                    "'" + amxMessage.getID_IssueDate() + "'," +
                    "'" + amxMessage.getID_IssueCode() + "'," +
                    "'" + amxMessage.getPostalCode() + "'," +
                    "'" + amxMessage.getFax_Number().trim() + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    1 +","+
                    "'" + amxMessage.getCreateDate()  + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + amxMessage.getBranchCode() + "'," +
                    "'" + amxMessage.getAccountGroup().trim() + "'" +
                    " )";
            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertNaclog_sql);
            nacLogStatment.close();


            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.createAMXNAC=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (nacLogStatment != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateAMXNAC(AmxMessage amxMessage, String customerId,String session_id,String chanal_type,String origAccountTitle) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String accountNo = amxMessage.getAccountNo();

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "update TBCUSTOMER set " +
                    "CARD_HOLDER_NAME = ''," +
                    "CARD_HOLDER_SURNAM = ''," +
                    "NAME_FA ='" + amxMessage.getFirstName().trim() + "'," +
                    "SURNAME_FA ='" + amxMessage.getLastName().trim() + "'," +
                    "FATHERNAME ='" + amxMessage.getFatherName().trim() + "'," +
                    "OWNER_INDEX ='" + amxMessage.getOwnerIndex() + "'," +
                    "SEX =" + amxMessage.getGender() + "," +
                    "ORIG_EDIT_DATE ='" + amxMessage.getChangeDate() + "'," +
                    "NATIONAL_CODE ='" + amxMessage.getNationalCode().trim() + "'," +
                    "BIRTHDATE ='" + amxMessage.getBirthDate() + "'," +
                    "ID_NUMBER ='" + amxMessage.getID_Number() + "'," +
                    "ID_SERIAL_NO ='" + amxMessage.getID_SerialNumber() + "'," +
                    "ID_SERIES ='" + amxMessage.getID_Series() + "'," +
                    "ID_ISSUE_DATE ='" + amxMessage.getID_IssueDate() + "'," +
                    "ID_ISSUE_PLACE ='" + amxMessage.getID_IssuePlace().trim() + "'," +
                    "ID_ISSUE_CODE ='" + amxMessage.getID_IssueCode() + "'," +
                    "HOMEPHONE ='" + amxMessage.getTel_Number1().trim() + "'," +
                    "TEL_NUMBER2 ='" + amxMessage.getTel_Number2().trim() + "'," +
                    "POSTALCODE ='" + amxMessage.getPostalCode() + "'," +
                    "EXTERNAL_ID_NUMBER ='" + amxMessage.getExt_IdNumber() + "'," +
                    "FOREIGN_COUNTRY_CODE ='" + amxMessage.getForeignCountryCode() + "'," +
                    "STATEMENT_TYPE =" + amxMessage.getStatementType() + "," +
                    "CELLPHONE ='" + amxMessage.getMob_Number().trim() + "'," +
                    "FAX ='" + amxMessage.getFax_Number().trim() + "'," +
                    "ADDRESS1 ='" + amxMessage.getAddress1().trim() + "'," +
                    "ADDRESS2 ='" + amxMessage.getAddress2().trim() + "'," +
                    "STATUSMELLI = " + amxMessage.getNationalCodeValid() + "," +
                    "EMAILADDRESS ='" + amxMessage.getEmailAddress().trim() + "'" +
                    " where CUSTOMER_ID = '" + customerId + "'";

            statement = connection.createStatement();
            int n = statement.executeUpdate(sql);
            statement.close();
            if (n == 0)
                throw new NotFoundException("Customer Not Found in tbCustomer, Customer_ID=" + customerId + " and Owner_Index=" + amxMessage.getOwnerIndex());

            if(amxMessage.getOwnerIndex().equalsIgnoreCase("00")){
                // Update tbcustomerAccounts
                if (!amxMessage.getAccountType().equals("0")) {
                    sql = "update TBCUSTOMERACCOUNTS set " +
                            "ACCOUNT_TITLE = '" + amxMessage.getAccountType() + "'," +
                            "ORIG_EDIT_DATE = '" + amxMessage.getChangeDate() + "'" +
                            " where CUSTOMER_ID =  '" + customerId + "'";

                    statement = connection.createStatement();
                    statement.executeUpdate(sql);
                    statement.close();
                }
                // Update tbcustomersrv
                sql = "update tbcustomersrv set STATUSMELLI = " + amxMessage.getNationalCodeValid() + ",LAST_USAGE_TIME = " +
                        "current_timestamp,NATIONAL_CODE= '" + amxMessage.getNationalCode().trim() + "' where ACCOUNT_NO = '" + amxMessage.getAccountNo() + "' ";
                statement = connection.createStatement();
                int count = statement.executeUpdate(sql);
                statement.close();
                if (count == 0) {
                    throw new NotFoundException("Customer Not Found in tbCustomerSrv, AccountNo=" + amxMessage.getAccountNo());
                }
            }
            //Update tbcustacc
            String accountTitle = "0";
            String subTitle = "0";
            if (!amxMessage.getOwnerIndex().equalsIgnoreCase("00")) {
                if (origAccountTitle.trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    accountTitle = Constants.SHARED_ACCOUNT_TYPE;
                    subTitle = amxMessage.getAccountType();
                } else if (origAccountTitle.trim().equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    accountTitle = Constants.HOGHOOGHI_ACCOUNT_TYPE;
                    subTitle = amxMessage.getAccountType();
                }
            } else {
                accountTitle = amxMessage.getAccountType();
                subTitle = "0";
            }
            sql = "update TBCUSTACC set " +
                    "NATIONAL_CODE= '"+amxMessage.getNationalCode().trim()+"',"+
                    "EXTERNAL_ID_NUMBER= '"+amxMessage.getExt_IdNumber()+"',"+
                    "SUB_TITLE= '"+subTitle+"',"+
                    "ACCOUNT_TITLE = '" + accountTitle + "'" +
                    " where ACCOUNT_NO= '"+amxMessage.getAccountNo()+"' and CUSTOMER_ID =  '" + customerId + "'";

            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();

            String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "FATHERNAME," +
                    "SEX," +
                    "BIRTHDATE," +
                    "CELLPHONE," +
                    "HOMEPHONE," +
                    "TEL_NUMBER2," +
                    "EMAILADDRESS," +
                    "ADDRESS1," +
                    "ADDRESS2," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ID_NUMBER," +
                    "ID_ISSUE_PLACE," +
                    "ID_SERIAL_NO," +
                    "OPERATION_TYPE," +
                    "ID_SERIES," +
                    "ID_ISSUE_DATE," +
                    "ID_ISSUE_CODE," +
                    "POSTALCODE," +
                    "FAX," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE," +
                    "HANDLED,"+
                    "OPEN_DATE,"+
                    "OPEN_TIME,"+
                    "BRANCH_ID,"+
                    "ACCOUNT_GROUP"+
                    ") values(" +
                    "'" + session_id + "'," +
                    "'" + chanal_type + "'," +
                    getPartNo() + "," +
                    "current_timestamp , " +
                    "'" + amxMessage.getAccountNo() + "'," +
                    "'" + amxMessage.getFirstName().trim() + "'," +
                    "'" + amxMessage.getLastName().trim() + "'," +
                    "'" + amxMessage.getFatherName().trim() + "'," +
                    amxMessage.getGender() + "," +
                    "'" + amxMessage.getBirthDate() + "'," +
                    "'" + amxMessage.getMob_Number().trim() + "'," +
                    "'" + amxMessage.getTel_Number1().trim() + "'," +
                    "'" + amxMessage.getTel_Number2().trim() + "'," +
                    "'" + amxMessage.getEmailAddress().trim() + "'," +
                    "'" + amxMessage.getAddress1().trim() + "'," +
                    "'" + amxMessage.getAddress2().trim() + "',"+
                    "'" + amxMessage.getAccountType() + "',"+
                    "'" + amxMessage.getNationalCode().trim() + "'," +
                    "'" + amxMessage.getExt_IdNumber() + "'," +
                    "'" + amxMessage.getID_Number() + "'," +
                    "'" + amxMessage.getID_IssueCode().trim() + "'," +
                    "'" + amxMessage.getID_SerialNumber() + "'," +
                    "'" + Constants.UPDATE_NAC_STATUS + "'," +
                    "'" + amxMessage.getID_Series() + "'," +
                    "'" + amxMessage.getID_IssueDate() + "'," +
                    "'" + amxMessage.getID_IssueCode() + "'," +
                    "'" + amxMessage.getPostalCode() + "'," +
                    "'" + amxMessage.getFax_Number().trim() + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    0 +","+
                    "'" + amxMessage.getCreateDate()  + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + amxMessage.getBranchCode() + "'," +
                    "'" + amxMessage.getAccountGroup().trim() + "'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateAMXNAC,  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (NotFoundException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateAMXNAC,  Can not rollback -- Error2 :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static void insertSAFLogBranch(String safName, String msgString, int hostId,
                                          String sessionId, String messageId, String messageType,
                                          String actionCode, String channelId, String srcAccount, String destAccount) throws SQLException {
        try {

            if (msgString!=null && msgString.length() > 2000)
                throw new SQLException("msgString is too long::" + msgString.length());

            String sql = "insert into TBSAFLOGBR " +
                    "(PARTNO,SAFNAME,INSERTDATE,MSGSTRING," +
                    "HOST_ID,MESSAGE_TYPE,SESSION_ID,message_id,CHANNEL_ID,ActionCode,src_account_no,dest_account_no,TRYCOUNT,HANDLED) values(" +
                    getPartNo() + ",'" + safName + "',current_timestamp,'" + msgString + "'," + hostId + ",'" +
                    messageType + "','" + sessionId + "','" + messageId + "','" + channelId + "'," +
                    "'" + actionCode + "','" + srcAccount + "','" + destAccount + "',0,0)";


            executeUpdate(sql);
        } catch (SQLException e) {
            log.error(e);
            throw e;
        }
    }

    public static void updateSafLogBR(SAFLog safLog) throws SQLException {

        Connection connection = null;
        Statement statement1 = null;
        try {
            connection = dbConnectionPool.getConnection();

            String actionCode = safLog.getActionCode();
            if(actionCode.startsWith(("*")))
                actionCode=actionCode.substring(1);

            String hql = "update  tbSAFLogBR set TryCount = " + safLog.getTryCount() +
                    ",LastTryDate = current_timestamp, Handled = " + safLog.getHandled() + " , actioncode='" + actionCode + "' " +
                    ", MSGPRIORITY=" + safLog.getMsgPriority() + ", SAFPRIORITY=" + safLog.getSAFPriority() + " , duration=" + (System.currentTimeMillis() - safLog.getStartTime()) + "  where sequencer = " + safLog.getSequencer();
            statement1 = connection.createStatement();
            statement1.executeUpdate(hql);
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e);
            throw e;
        } finally {
            if (statement1 != null) statement1.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean checkBranch(String branchCode) throws SQLException {
        if (branchMap.containsKey(branchCode))
            return true;

        String sql = "select ACCOUNT_NO,ISMODIFIED from TBBranch where branch_CODE = '" + branchCode + "' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {

                if (resultSet.getString("ISMODIFIED").equals("1"))
                    return false;

                branchMap.put(branchCode, resultSet.getString("ACCOUNT_NO"));
                return true;
            } else {
                return false;
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
    public static List<String> checkSafLogBR(String session_id) throws SQLException {

        Connection connection = null;
        Statement statement1 = null;
        ResultSet resultSet=null;
        List<String> transaction=null;
        try {
            connection = dbConnectionPool.getConnection();


            String hql = "select ACTIONCODE,SAFNAME,HANDLED from tbSAFLogBR where session_id = '"+session_id+"' and (SAFNAME='TJ_BR_FT' or SAFNAME='TJ_BR_REV') with ur";
            statement1 = connection.createStatement();
            resultSet=statement1.executeQuery(hql);
            connection.commit();
            if(resultSet.next()){
                transaction=new ArrayList<String>();
                transaction.add(0,resultSet.getString("ACTIONCODE"));
                transaction.add(1,resultSet.getString("SAFNAME"));
                transaction.add(2,resultSet.getString("HANDLED"));
            }

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement1 != null) statement1.close();
            dbConnectionPool.returnConnection(connection);
        }
        return transaction;
    }
    //****************AMX*******end***********
    //***************REMITTANCE Start********

    public static String[] remittanceInquiry(String serial_no, String reg_date, String national_code, String external_id) throws SQLException, NotFoundException {
        String[] remittance = new String[11];

        String sql = "select * from TB_REMITTANCE where SERIAL_NO ='" + serial_no + "' and REG_DATE = '" + reg_date + "'and NATIONAL_CODE= '"
                + national_code + "'";
        if(external_id!=null && !external_id.trim().equalsIgnoreCase(""))
            sql=sql+" and EXTERNAL_ID = '" + external_id + "'";

                sql=sql+" with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;


        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                remittance[0] = resultSet.getString("SERIAL_NO").trim();
                remittance[1] = resultSet.getString("REG_DATE").trim();
                remittance[2] = resultSet.getString("NATIONAL_CODE").trim();
                remittance[3] = resultSet.getString("EXTERNAL_CODE");
                remittance[4] = resultSet.getString("BIRTH_DATE").trim();
                remittance[5] = resultSet.getString("CELLPHONE_NO").trim();
                remittance[6] = resultSet.getString("EXPIRE_DATE").trim();
                remittance[7] = resultSet.getString("AMOUNT").trim();
                remittance[8] = resultSet.getString("FEE_AMOUNT").trim();
                remittance[9] = resultSet.getString("REMITTANCE_STATUS").trim();
                remittance[10] = resultSet.getString("MESSAGE_SEQUENCE").trim();

                connection.commit();
                return remittance;

            } else
                throw new NotFoundException("can not Find remittance for serial number:" + serial_no);
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
    //**********REMITTANCE*******end***********

    //**********Deposit block*******start***********
    public static String[] getDepositStatus(String accountNo) throws NotFoundException, SQLException, dpi.atlas.model.facade.cm.ServerAuthenticationException {
        Connection connection = null;
        Statement selectStatement = null;
        ResultSet selectResultSet = null;
        String accountInfo[];
        String statusD = "";
        String hql = "select STATUSD,HOST_ID from tbCustomersrv where ACCOUNT_NO = '" + accountNo + "' with ur";
        try {
            connection = dbConnectionPool.getConnection();
            selectStatement = connection.createStatement();
            selectResultSet = selectStatement.executeQuery(hql);
            connection.commit();
            if (selectResultSet.next()) {
                accountInfo = new String[3];
                statusD = selectResultSet.getString("STATUSD");
                accountInfo[0] = statusD.substring(0, 1);
                accountInfo[1] = statusD.substring(1, 2);
                accountInfo[2] = selectResultSet.getString("HOST_ID");
            } else {
                throw new NotFoundException("Account " + accountNo + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.getDepositStatus() =   -- Error :: " + e + " -- hql = " + hql);
            connection.rollback();
            throw e;
        } finally {
            if (selectResultSet != null) selectResultSet.close();
            if (selectStatement != null) selectStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return accountInfo;
    }

    public static boolean checkUser(String accountNo, String request, String user) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement selectStatement = null;
        ResultSet selectResultSet = null;
        boolean permission = false;
        String hql = "select USER_ID from tbservicestatuslog where  Account_no= '" + accountNo + "' and MESSAGE_TYPE='" + Fields.BLOCK_DEPOSIT + "' and SERVICE_STATUS='" + request + "' order by CREATION_DATE desc, CREATION_TIME desc fetch first 1 rows only with ur";
        try {
            connection = dbConnectionPool.getConnection();
            selectStatement = connection.createStatement();
            selectResultSet = selectStatement.executeQuery(hql);
            connection.commit();
            if (selectResultSet.next()) {
                String block_user = selectResultSet.getString("USER_ID");
                if (block_user != null && user != null && block_user.trim().equalsIgnoreCase(user.trim()))
                    permission = true;
            } else {
                throw new NotFoundException("Account " + accountNo + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.checkUser() =   -- Error :: " + e + " -- hql = " + hql);
            connection.rollback();
            throw e;
        } finally {
            if (selectResultSet != null) selectResultSet.close();
            if (selectStatement != null) selectStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return permission;
    }

    public static void updateDepositStatus(String accountNo, String sessionId, String terminalId, String userId, String branchCode,
                                           String statusE, String statusB, String messageType, String channelType, String desc) throws SQLException, NotFoundException {

        Connection connection = null;
        Statement updateStatement = null;
        Statement insertStatement = null;
        Statement selectStatement = null;
        ResultSet selectResultSet = null;
        String statusD = "";
        String finalStatus = "";
        try {
            connection = dbConnectionPool.getConnection();

            String hql = "select STATUSD from tbcustomersrv where Account_no= '" + accountNo + "' for update";
            selectStatement = connection.createStatement();
            selectResultSet = selectStatement.executeQuery(hql);
            if (selectResultSet.next()) {
                statusD = selectResultSet.getString("STATUSD");
                if (statusE != null && statusB != null) {
                    finalStatus = statusE + statusB;
                } else if (statusE == null && statusB != null) {
                    finalStatus = statusD.substring(0, 1) + statusB;
                } else { // statusE!=null && statusB==null
                    finalStatus = statusE + statusD.substring(1, 2);
                }

                String hqlUpdate = "update tbCustomersrv set STATUSD = '" + finalStatus + "' where ACCOUNT_NO = '" + accountNo + "' ";
                updateStatement = connection.createStatement();
                updateStatement.executeUpdate(hqlUpdate);

                if (statusE != null && statusB != null) {

                    String sql = "insert into TBSERVICESTATUSLOG " +
                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                            " values('" +
                            accountNo + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                            branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.BRANCH_POSTFIX + "'," +
                            "'" + channelType + "','" + messageType + "','" + desc + "')";
                    insertStatement = connection.createStatement();
                    insertStatement.executeUpdate(sql);

                    sql = "insert into TBSERVICESTATUSLOG " +
                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                            " values('" +
                            accountNo + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                            branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.NON_BRANCH_POSTFIX + "'," +
                            "'" + channelType + "','" + messageType + "','" + desc + "')";
                    insertStatement = connection.createStatement();
                    insertStatement.executeUpdate(sql);

                } else if (statusE == null && statusB != null) {
                    String sql = "insert into TBSERVICESTATUSLOG " +
                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                            " values('" +
                            accountNo + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                            branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.BRANCH_POSTFIX + "'," +
                            "'" + channelType + "','" + messageType + "','" + desc + "')";
                    insertStatement = connection.createStatement();
                    insertStatement.executeUpdate(sql);
                } else { // statusE!=null && statusB==null
                    String sql = "insert into TBSERVICESTATUSLOG " +
                            "(ACCOUNT_NO,SESSION_ID,TERMINAL_ID,USER_ID,BRANCH_CODE," +
                            "CREATION_DATE,CREATION_TIME,SERVICE_STATUS,CHANNEL_TYPE,MESSAGE_TYPE,DESC) " +
                            " values('" +
                            accountNo + "','" + sessionId + "','" + terminalId + "','" + userId + "','" +
                            branchCode + "','" + DateUtil.getSystemDate() + "','" + DateUtil.getSystemTime() + "','" + Constants.NON_BRANCH_POSTFIX + "'," +
                            "'" + channelType + "','" + messageType + "','" + desc + "')";
                    insertStatement = connection.createStatement();
                    insertStatement.executeUpdate(sql);
                }

                connection.commit();

            } else {
                throw new NotFoundException("Account " + accountNo + " does not exist");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.updateDepositStatus() =   -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (updateStatement != null) updateStatement.close();
            if (insertStatement != null) insertStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //**********Deposit block*******end***********
    //**********Simin isExistNAC**** Start*****
    public static boolean isExistNac(BranchMessage branchMsg) throws SQLException {
        boolean isExist=false;
        String selectSql;
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            selectSql = "select LOCK_STATUS from TBCUSTOMERACCOUNTS where ACCOUNT_NO = '" + branchMsg.getAccountNo() + "' and LOCK_STATUS=1 with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSql);

            if (resultSet.next()) {
                isExist=true;
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.isExistNac  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

        return isExist;
    }
    //*********Simin isExistNAC********end********

    //**********SiminCreateNAC**** Start*****
    public static void createNACSimin(BranchMessage branchMessage,String session_id ,String channel_type) throws SQLException{
        String nationalCode = "0000000000";
        String firstName = "";

        firstName=branchMessage.getFirstName();

        String lastName = branchMessage.getLastName();


        Statement statement = null;
        ResultSet resultSet = null;
        Statement nacLogStatment = null;
        Connection connection = null;
        String customerId = "";
        String accountNo = branchMessage.getAccountNo().trim();
        try {
            connection = dbConnectionPool.getConnection();


            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error("ERROR1:: customerId=" + customerId + " lenght is too long");
            }
            resultSet.close();
            statement.close();

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "sex," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "ADDRESS1," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "TEMPLATE_ID," +
                    "HOMEPHONE," +
                    "NATIONAL_CODE," +
                    "ID_NUMBER," +
                    "BIRTHDATE" +
                    ") values(" +
                    "'" + customerId + "'," +
                    0 + "," +
                    "''," +
                    "''," +
                    "'" + firstName + "'," +
                    "'" + lastName + "'," +
                    "'BANK_TEJARAT'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    1 + "," +
                    "''," +
                    "'" + nationalCode + "'," +
                    "''," +
                    "'00000000'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String accountTitle = branchMessage.getAccountType();
            String subTitle = "0";
            String accountGroup=ISOUtil.zeropad(branchMessage.getAccountGroup(), 3);
            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + nationalCode + "'," +
                    "''," +
                    "'" + accountNo + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();

            String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                    "CUSTOMER_ID," +
                    "ACCOUNT_TYPE," +
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
                    "ACCOUNT_OPENER_NAME, " +
                    "WITHDRAW_TYPE " +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" +  accountGroup + "'," +
                    "'" + accountNo + "'," +
                    "1," +
                    "'" + Constants.DEFAULT_CURRENCY + "'," +
                    "0, " +
                    "'" + accountTitle + "'," +
                    "1," +
                    2 + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + branchMessage.getBranchCodeBody() + "'," +
                    Constants.HOST_CFS + "," +
                    "''," +
                    0 +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustomerAccount_sql);
            statement.close();


            String customersrvId = existCustomerAccountInSRV(accountNo);

            if (customersrvId != null) {
                updatetCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS, nationalCode);
            } else {
                insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                       accountGroup , nationalCode, Constants.NATIONAL_STATUS, customerId);
            }

            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "SEX," +
                    "BIRTHDATE," +
                    "ACCOUNT_TYPE," +
                    "NATIONAL_CODE," +
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
                    "'" +accountNo + "'," +
                    "'" +firstName + "'," +
                    "'" +lastName + "'," +
                    branchMessage.getGender() + "," +
                    "'00000000'," +
                    "'" + branchMessage.getAccountType() + "',"+
                    "'" + nationalCode.trim() + "'," +
                    "'" + Constants.CREATE_NAC_STATUS + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    1 +","+
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" +  branchMessage.getBranchCodeBody() + "'," +
                    "'" + branchMessage.getAccountGroup().trim() + "'" +
                    " )";
            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertNaclog_sql);
            nacLogStatment.close();

            connection.commit();

        } catch (SQLException e) {
            log.error("SQLException for branchCode=" + branchMessage.getBranchCodeBody() + "Error::" + e);
            System.out.println("SQLException for branchCode=" + branchMessage.getCreateDate());

        } catch (ISOException e) {
            log.error("ISOException for branchCode=" + branchMessage.getBranchCode() + "Error::" + e);
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (nacLogStatment != null) nacLogStatment.close();
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }
    //************SiminCreateNAC*****end***********
    //***************ATM******** Start*********
    public static String[] inquiryATM(String accountNo_ATM) throws SQLException, NotFoundException,ModelException {
        String[] inquiry = new String[2];
        Connection connection = null;
        Statement accountStatement = null;
        Statement deviceStatement = null;
        ResultSet accountResultSet = null;
        ResultSet deviceResultSet = null;

        try {
            connection = dbConnectionPool.getConnection();

            String device_sql = "select BRANCH_ID from tbdevice where ACCOUNT_NO ='" + accountNo_ATM + "' and DEVICE_TYPE=" + Constants.ATM_TYPE + " and status='1' with ur";
            deviceStatement = connection.createStatement();
            deviceResultSet = deviceStatement.executeQuery(device_sql);
            if (deviceResultSet.next()) {
                inquiry[0] = deviceResultSet.getString("BRANCH_ID").trim();

                String sql = "select balance from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo_ATM + "' and LOCK_STATUS=" + 1 + " with ur";
                accountStatement = connection.createStatement();
                accountResultSet = accountStatement.executeQuery(sql);
                if (accountResultSet.next()) {
                    inquiry[1] = accountResultSet.getString(1);
                } else {
                    connection.rollback();
                    throw new ModelException("Can not find device for account_no::" + accountNo_ATM);
                }
            } else {
                connection.rollback();
                throw new NotFoundException("Can not find device for account_no::" + accountNo_ATM);
            }
            connection.commit();
            return inquiry;


        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (accountResultSet != null) accountResultSet.close();
            if (deviceResultSet != null) deviceResultSet.close();
            if (accountStatement != null) accountStatement.close();
            if (deviceStatement != null) deviceStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    //**********ATM*******end***********
    //***************SMS Start********

    public static int disableSMS(String accountNo) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set SMSNOTIFICATION= '" + Constants.DISABLE_SMS_NOTIFICATION + "' where ACCOUNT_NO = '" + accountNo + "' ";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            connection.commit();
            if (updateCount == 0) {
                throw new NotFoundException("AccountNo Not Found in tbcustomersrv accountNo=" + accountNo);
            }

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--DisableSMS = " + e + " -- sql = " + sql);
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;

    }

    public static int activeSMS(String accountNo) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set SMSNOTIFICATION= '" + Constants.SMS_NOTIFICATION + "' where ACCOUNT_NO = '" + accountNo + "' ";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            connection.commit();
            if (updateCount == 0) {
                throw new NotFoundException("AccountNo Not Found in tbcustomersrv accountNo=" + accountNo);
            }

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--ActiveSMS = " + e + " -- sql = " + sql);
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;

    }

    public static String getSgbDescription(String txCode) throws SQLException, NotFoundException {


        String sql = "select SGBDESC from TBSGBCODE  where SGBCODE = '" + txCode + "'  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String sgb_description = resultSet.getString("SGBDESC");
                return sgb_description;
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
    }


    //**********SMS*******end***********
    //**********Stock Deposit*******start***********
    public static List<String> getDescription (String sgbCode,String feeSgbCode) throws SQLException, NotFoundException {
        if (descriptionMap.containsKey(sgbCode+feeSgbCode))
            return (List<String>) descriptionMap.get(sgbCode+feeSgbCode);
        String sql = "select SGBCODE,SGBDESC,FULL_DESC from tbsgbcode where SGBCODE in ('"+sgbCode+"','"+feeSgbCode+"') with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            List<String> descriptionList=new ArrayList<String>();
            while (resultSet.next()) {
                String txCode=resultSet.getString("SGBCODE").trim();
                if(txCode.equalsIgnoreCase(sgbCode)) {
                    descriptionList.add(0, resultSet.getString("SGBDESC"));
                    descriptionList.add(1, resultSet.getString("FULL_DESC"));
                }else {
                    descriptionList.add(2, resultSet.getString("SGBDESC"));
                    descriptionList.add(3, resultSet.getString("FULL_DESC"));
                }
            }
            connection.commit();

            if(descriptionList.size()>0) {
                descriptionMap.put(sgbCode + feeSgbCode, descriptionList);
                return descriptionList;
            }else{
                throw new NotFoundException("Can not Find any tbsgbcode where SGBCODE = '" + sgbCode + "' or "+feeSgbCode);
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

    public static String getTxTypeSgbTxCode (String txCode,String feeTxCode) throws SQLException, NotFoundException {
        if (txTypeSgbTxCodeMap.containsKey(txCode))
            return (String) txTypeSgbTxCodeMap.get(txCode);

        String sql = "select TX_CODE,SGB_TX_CODE from TBCFSTXTYPE where TX_CODE  in ('" + txCode + "','"+feeTxCode+"')  with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            String sgb_tx_code="";
            String fee_tx_code="";

            while (resultSet.next()) {
                String tx_code=resultSet.getString("TX_CODE");
                if(tx_code.trim().equalsIgnoreCase(TJCommand.CMD_DEPOSIT_STOCK_PG))
                    sgb_tx_code = resultSet.getString("SGB_TX_CODE").trim();
                else
                    fee_tx_code = resultSet.getString("SGB_TX_CODE").trim();
            }
            connection.commit();
            if(sgb_tx_code==null || sgb_tx_code.trim().equalsIgnoreCase("") ||
                    fee_tx_code==null || fee_tx_code.trim().equalsIgnoreCase("")){
                throw new NotFoundException("Can not Find any TBCFSTXTYPE where TX_CODE = '" + txCode + "' or TX_CODE = '" + feeTxCode + "'");
            }else {
                sgb_tx_code=sgb_tx_code+","+fee_tx_code;
                txTypeSgbTxCodeMap.put(txCode, sgb_tx_code);
                return sgb_tx_code;
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

    public static List<String> checkSafLogSTK(String session_id) throws SQLException,ModelException {

        Connection connection = null;
        Statement statement1 = null;
        ResultSet resultSet=null;
        List<String> transaction=null;
        try {
            connection = dbConnectionPool.getConnection();


            String hql = "select ACTIONCODE,SAFNAME,HANDLED from tbSAFLogBR where session_id = '"+session_id+"' and (SAFNAME='TJ_STK_FT' or SAFNAME='TJ_STK_REV') with ur";
            statement1 = connection.createStatement();
            resultSet=statement1.executeQuery(hql);

            if(resultSet.next()){
                transaction=new ArrayList<String>();
                transaction.add(0,resultSet.getString("ACTIONCODE"));
                transaction.add(1,resultSet.getString("SAFNAME"));
                transaction.add(2,resultSet.getString("HANDLED"));
            }
            if(resultSet.next())
                throw  new ModelException();

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            log.error(e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement1 != null) statement1.close();
            dbConnectionPool.returnConnection(connection);
        }
        return transaction;
    }

    //**********Stock Deposit*******end***********
    //**********PAYA*******Start***********
    public static String registerPayaRequest(BranchMessage branchMsg) throws SQLException,ModelException {
        Connection connection = dbConnectionPool.getConnection();
        Statement selectSt = null;
        Statement insertSt = null;
        ResultSet rs = null;
        Statement seqSt = null;
        ResultSet seqrs = null;
        String stepNo="1";
        String status=Constants.ACTIVE_PAYA_REQUEST;
        String dueDate=branchMsg.getDueDate().trim();
        String branchCode=branchMsg.getBranchCode().trim();
        String userId=branchMsg.getUserId().trim();
        branchMsg.setDueDate(dueDate);
        int trackId=0;
        int serial=0;
        String channelCode="2";
        String returnValue="";
        try {
            if(branchCode.length()<6)
                try {
                    branchCode=ISOUtil.padleft(branchCode,6,'0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                }
            if(userId.length()<6)
                try {
                    userId=ISOUtil.padleft(userId,6,'0');
                } catch (ISOException e) {
                    log.error("Can not zeropad userId = '" + userId + "' in registerPayaRequest() : " + e.getMessage());
                }
            String refId=branchCode+branchMsg.getDocumentNo()+branchMsg.getDueDate()+
                    dueDate;

            String achTrackId = "select next value for seq_ach as maxID from sysibm.sysdummy1 with ur";

            seqSt = connection.createStatement();
            seqrs = seqSt.executeQuery(achTrackId);
            seqrs.next();
            String maxID = seqrs.getString(1).trim();
            if (maxID != null)
                trackId = Integer.parseInt(maxID);

            serial=Integer.parseInt(branchMsg.getDocumentNo());

            String insertStr="INSERT INTO TBBRACH( "+
                            "BRANCH_SEND ,"+
                            "SERIAL ,"+
                            "DUE_DATE ,"+
                            "CREAT_DATE ,"+
                            "STEP ,"+
                            "STATUS ,"+
                            "CREAT_USER,"+
                            "BANK_SEND ,"+
                            "BANK_RECV ,"+
                            "AMOUNT ,"+
                            "SOURCE_ACC,"+
                            "SOURCE_IBAN,"+
                            "PAYMENT_CODE,"+
                            "DEST_IBAN,"+
                            "NAME_SEND ,"+
                            "MCODE_SEND ,"+
                            "PCODE_SEND ,"+
                            "ADDR_SEND ,"+
                            "TELL_SEND ,"+
                            "NAME_RECV ,"+
                            "UPDATE_TIMESTAMP,"+
                            "REFID,"+
                            "DESC ,"+
                            "TRACK_ID,"+
                            "SHAHAB_SEND,"+
                            "SHAHAB_RECV,"+
                            "MCODE_RECV,"+
                            "PCODE_RECV,"+
                            "REASON"+
                    ")VALUES("+
                    "'"+branchCode+"',"+
                    serial+","+
                    "'"+dueDate+"',"+
                    "'"+DateUtil.getSystemDate()+"',"+
                    stepNo+","+
                    status+","+
                    "'"+userId+"',"+
                    "'"+branchMsg.getBankSend()+"',"+
                    "'"+branchMsg.getBankRecv()+"',"+
                    branchMsg.getAmount()+","+
                    "'"+branchMsg.getAccountNo()+"',"+
                    "'"+branchMsg.getSourceIban().trim()+"',"+
                    "'"+branchMsg.getPaymentCode().trim()+"',"+
                    "'"+branchMsg.getDestIban().trim()+"',"+
                    "'"+branchMsg.getSenderName().trim()+"',"+
                    "'"+branchMsg.getMeliCode()+"',"+
                    "'"+branchMsg.getPostalCode()+"',"+
                    "'"+branchMsg.getAddress1().trim()+"',"+
                    "'"+branchMsg.getTel_Number1().trim()+"',"+
                    "'"+branchMsg.getReciverName().trim()+"',"+
                    "current_timestamp," +
                    "'"+refId+"',"+
                    "'"+branchMsg.getPayaDescription().trim()+"',"+
                    trackId+","+
                    "'"+branchMsg.getSenderShahab()+"',"+
                    "'"+branchMsg.getReciverShahab()+"',"+
                    "'"+branchMsg.getReciverMeliCode()+"',"+
                    "'"+branchMsg.getReciverPostalCode()+"',"+
                    "'"+branchMsg.getReason().trim()+"'"+
                    ")";

                insertSt = connection.createStatement();
            try{
                insertSt.execute(insertStr);
            }catch (SQLException exception){
                if (exception.getErrorCode() == Constants.DISTINCT_VIOLATION_ERROR) {
                    throw new ModelException();
                }else
                    throw new SQLException(exception);
            }

            connection.commit();
            try {
                returnValue=dueDate.substring(2)+ISOUtil.zeropad(branchMsg.getBankSend(), 3)+channelCode+ISOUtil.zeropad(String.valueOf(trackId),6);
            } catch (ISOException e) {
                log.error("Can not zeropad startTrack = '" + trackId + "' in registerPayaRequest() : " + e.getMessage());
            }
        } catch (ModelException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.registerPayaRequest(),  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.registerPayaRequest(),  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (seqrs != null) seqrs.close();
            if (seqSt != null) seqSt.close();
            if (selectSt != null) selectSt.close();
            if (insertSt != null) insertSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return returnValue;
    }
    public static PayaRequest getPayaRequest(String branchCode,String serial,String dueDate) throws SQLException, NotFoundException {
        Connection connection=null;
        Statement selectSt = null;
        ResultSet rs = null;
        PayaRequest request=null;
        try {
            if(branchCode.length()<6)
                try {
                    branchCode=ISOUtil.padleft(branchCode,6,'0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                }
            String achSql = "SELECT BRANCH_SEND,CREAT_DATE,BANK_SEND,BANK_RECV," +
                    "AMOUNT,SOURCE_IBAN,PAYMENT_CODE,DEST_IBAN,NAME_SEND," +
                    "MCODE_SEND,PCODE_SEND,ADDR_SEND,TELL_SEND,NAME_RECV,DESC,SHAHAB_SEND,SHAHAB_RECV," +
                    "MCODE_RECV,PCODE_RECV,REASON from TBBRACH " +
                    "WHERE BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='"+dueDate+"' AND SERIAL=" + serial + " AND " +
                    "STEP=1 AND STATUS="+Constants.ACTIVE_PAYA_REQUEST+" with ur";
            connection= dbConnectionPool.getConnection();
            selectSt = connection.createStatement();
            rs = selectSt.executeQuery(achSql);
            connection.commit();
            if (rs.next()) {

                request=new PayaRequest(serial,dueDate,
                        rs.getString("BANK_SEND"),rs.getString("BANK_RECV"),
                        rs.getLong("AMOUNT"),rs.getString("CREAT_DATE"),rs.getString("SOURCE_IBAN"),
                        rs.getString("PAYMENT_CODE"),rs.getString("DEST_IBAN"),rs.getString("NAME_SEND"),
                        rs.getString("MCODE_SEND"),rs.getString("PCODE_SEND"),rs.getString("ADDR_SEND"),
                        rs.getString("TELL_SEND"),rs.getString("NAME_RECV"),rs.getString("DESC"),rs.getString("SHAHAB_SEND"),
                        rs.getString("SHAHAB_RECV"),rs.getString("MCODE_RECV"),rs.getString("PCODE_RECV"),
                        rs.getString("REASON"));

            }else{
                throw new NotFoundException();
            }
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.registerPayaRequest(),  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (selectSt != null) selectSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return request;
    }

    public static void inactivePayaRequest(String branchCode, String serial, String startDate, String userId) throws SQLException, ISOException, NotFoundException {
        Connection connection = null;
        Statement updateBrAchSt = null;

        try {
            if (branchCode.length() < 6)
                try {
                    branchCode = ISOUtil.padleft(branchCode, 6, '0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                    throw new ISOException();
                }
            connection = dbConnectionPool.getConnection();

            //update tbBrAch
            String updatetb = "UPDATE TBBRACH SET " +
                    "STATUS =" + Constants.INACTIVE_PAYA_REQUEST + " WHERE " +
                    "BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='" + startDate + "' AND" +
                    " SERIAL=" + serial + " AND  STEP=1 AND STATUS="+Constants.ACTIVE_PAYA_REQUEST;
            updateBrAchSt = connection.createStatement();
            int count = updateBrAchSt.executeUpdate(updatetb);
            if (count > 0)
                connection.commit();
            else {
                //Not Found Ach Request
                throw new NotFoundException("");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.inactivePayaRequest()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.inactivePayaRequest()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (updateBrAchSt != null) updateBrAchSt.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<PayaRequest> getPayaRequestinDueDate(String branchCode,String dueDate) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement selectSt = null;
        ResultSet rs = null;
        PayaRequest request=null;
        List<PayaRequest> requestList=new ArrayList<PayaRequest>();
        try {
            if(branchCode.length()<6)
                try {
                    branchCode=ISOUtil.padleft(branchCode,6,'0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                }
            String achSql = "SELECT SERIAL,DUE_DATE,CREAT_DATE,BANK_SEND,BANK_RECV," +
                    "AMOUNT,SOURCE_IBAN,PAYMENT_CODE,DEST_IBAN,NAME_SEND," +
                    "MCODE_SEND,PCODE_SEND,ADDR_SEND,TELL_SEND,NAME_RECV,DESC,SHAHAB_SEND," +
                    "SHAHAB_RECV,MCODE_RECV,PCODE_RECV,REASON from TBBRACH " +
                    "WHERE BRANCH_SEND ='" + branchCode + "' AND DUE_DATE<='"+dueDate+"' " +
                    "AND STEP=1 AND STATUS="+Constants.ACTIVE_PAYA_REQUEST+" FETCH FIRST 150 ROWS ONLY with ur";
            connection=dbConnectionPool.getConnection();
            selectSt = connection.createStatement();
            rs = selectSt.executeQuery(achSql);

            while (rs.next()) {

                request=new PayaRequest(rs.getString("SERIAL"),dueDate,
                        rs.getString("BANK_SEND"),rs.getString("BANK_RECV"),
                        rs.getLong("AMOUNT"),rs.getString("CREAT_DATE"),rs.getString("SOURCE_IBAN"),
                        rs.getString("PAYMENT_CODE"),rs.getString("DEST_IBAN"),rs.getString("NAME_SEND"),
                        rs.getString("MCODE_SEND"),rs.getString("PCODE_SEND"),rs.getString("ADDR_SEND"),
                        rs.getString("TELL_SEND"),rs.getString("NAME_RECV"),rs.getString("DESC"),rs.getString("SHAHAB_SEND"),
                        rs.getString("SHAHAB_RECV"),rs.getString("MCODE_RECV"),rs.getString("PCODE_RECV"),
                        rs.getString("REASON"));
                requestList.add(request);

            }
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.registerPayaRequest(),  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (selectSt != null) selectSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return requestList;
    }

    public static void deletePayaRequest(String branchCode, String serial, String startDate) throws SQLException, ISOException, NotFoundException {
        Connection connection = null;
        Statement updateBrAchSt = null;

        try {
            if (branchCode.length() < 6)
                try {
                    branchCode = ISOUtil.padleft(branchCode, 6, '0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                    throw new ISOException();
                }
            connection = dbConnectionPool.getConnection();

            //update tbBrAch
            String updatetb = "UPDATE TBBRACH SET " +
                    "STATUS =" + Constants.DELETE_PAYA_REQUEST + " WHERE " +
                    "BRANCH_SEND ='" + branchCode + "' AND DUE_DATE='" + startDate + "' AND SERIAL=" + serial + " AND " +
                    " STEP=1 AND STATUS="+Constants.ACTIVE_PAYA_REQUEST;
            updateBrAchSt = connection.createStatement();
            int count = updateBrAchSt.executeUpdate(updatetb);
            if (count > 0)
                connection.commit();
            else {
                //Not Found Ach Request
                throw new NotFoundException("");
            }
        } catch (SQLException e) {
            log.error("CFSFacadeNew.deletePayaRequest()#1 = -- Error :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("CFSFacadeNew.deletePayaRequest()#2 = -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (updateBrAchSt != null) updateBrAchSt.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static List<PayaRequest> getPayaReport(String branchCode,String fromDate,String toDate) throws SQLException, NotFoundException {
        Connection connection= null;
        Statement selectSt = null;
        ResultSet rs = null;
        PayaRequest request=null;
        List<PayaRequest> requestList=new ArrayList<PayaRequest>();
        try {
            if(branchCode.length()<6)
                try {
                    branchCode=ISOUtil.padleft(branchCode,6,'0');
                } catch (ISOException e) {
                    log.error("Can not zeropad branchCode = '" + branchCode + "' in registerPayaRequest() : " + e.getMessage());
                }
            String achSql = "SELECT BRANCH_SEND,DUE_DATE,CREAT_DATE,BANK_SEND,BANK_RECV," +
                    "AMOUNT,SOURCE_IBAN,PAYMENT_CODE,DEST_IBAN,NAME_SEND," +
                    "MCODE_SEND,PCODE_SEND,ADDR_SEND,TELL_SEND,NAME_RECV,SERIAL,SHAHAB_SEND,"+
                    "SHAHAB_RECV,MCODE_RECV,PCODE_RECV,REASON from TBBRACH " +
                    "WHERE BRANCH_SEND ='" + branchCode + "' AND DUE_DATE between "+
                    "'"+fromDate+"' AND '"+toDate+"' ORDER BY DUE_DATE FETCH FIRST 80 ROWS ONLY with ur";
            connection=dbConnectionPool.getConnection();
            selectSt = connection.createStatement();
            rs = selectSt.executeQuery(achSql);

            while (rs.next()) {

                request=new PayaRequest(rs.getString("BRANCH_SEND"),rs.getString("DUE_DATE"),
                        rs.getString("CREAT_DATE"),rs.getString("BANK_SEND"),rs.getString("BANK_RECV"),
                        rs.getLong("AMOUNT"),rs.getString("SOURCE_IBAN"),rs.getString("PAYMENT_CODE"),rs.getString("DEST_IBAN"),
                        rs.getString("NAME_SEND"),rs.getString("MCODE_SEND"),rs.getString("PCODE_SEND"),rs.getString("ADDR_SEND"),
                        rs.getString("TELL_SEND"),rs.getString("NAME_RECV"),rs.getString("SERIAL"),rs.getString("SHAHAB_SEND"),
                        rs.getString("SHAHAB_RECV"),rs.getString("MCODE_RECV"),rs.getString("PCODE_RECV"),
                        rs.getString("REASON"));
                requestList.add(request);

            }
            connection.commit();
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.registerPayaRequest(),  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (selectSt != null) selectSt.close();
            dbConnectionPool.returnConnection(connection);
        }
        return requestList;
    }

    public static void UpdateAchStatus(String status,String expCode,String expDesc,String cycleNo,String effectiveDate,String refId) throws SQLException, NotFoundException {
        Connection connection = null;
        Statement statement = null;

        String updateAch = "UPDATE TBBRACH SET STEP=3 ,STATUS="+status+" ,EXP_CODE='"+expCode+
                "' ,EXP_DESC ='"+expDesc+"' ,CYCLE_NO ="+cycleNo+" ,EFF_DATE ='"+effectiveDate+"' WHERE  REFID='"+refId+"' AND STEP =2";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            int count=statement.executeUpdate(updateAch);

            if(count<1){
                connection.rollback();
                throw new NotFoundException();
            }else{
                connection.commit();
            }
        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.UpdateAchStatus()=  Can not rollback -- Error :: " + e1);
                e1.printStackTrace();
            }
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //**********PAYA*******End***********

    //*********siminATM*****start*******

    public static String[] checkATM(String deviceCode) throws SQLException {

        String [] deviceFields=new String[8];
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String selectSql = "select ACCOUNT_NO,STATUS,ACCOUNT_BALANCE,CREATION_DATE,CREATION_TIME,BRANCH_ID,DEVICE_TYPE,DEVICE_CODE from tbdevice where DEVICE_CODE ='" + deviceCode + "' with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSql);

            if (resultSet.next()) {
                deviceFields[0]=resultSet.getString("ACCOUNT_NO").trim();
                deviceFields[1]=resultSet.getString("STATUS").trim();
                deviceFields[2]= String.valueOf(resultSet.getLong("ACCOUNT_BALANCE"));
                deviceFields[3]= resultSet.getString("CREATION_DATE").trim();
                deviceFields[4]=resultSet.getString("CREATION_TIME").trim();
                deviceFields[5]=resultSet.getString("BRANCH_ID").trim();
                deviceFields[6]=resultSet.getString("DEVICE_TYPE").trim();
                deviceFields[7]=resultSet.getString("DEVICE_CODE").trim();
            }

            connection.commit();

        }
        catch (SQLException e) {
            log.error("ChannelFacadeNew.checkATM  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

        return deviceFields;
    }

    public static int updatetATM_Status(String accountNo,String deviceCode) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        String sql =  "update tbdevice set STATUS = '" + 1 + "'  where ACCOUNT_NO = '" +accountNo+ "' and DEVICE_CODE='" + deviceCode+ "'";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            connection.commit();

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetATM_Status = " + e + " -- sql = " + sql);
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;

    }

    public static void createNAC_ATM(Connection connection,String accountNo,String branchCode,String session_id,String channel_type,String firstName,String lastName,String accountGroup)throws SQLException{
        String nationalCode = "0000000000";
        Statement statement = null;
        ResultSet resultSet = null;
        Statement nacLogStatment = null;
        String customerId = "";
        try {
            String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(maxCustomer_id_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                customerId = maxID;
            try {
                customerId = ISOUtil.zeropad(customerId, 10);
            } catch (ISOException e) {
                log.error("ERROR1:: customerId=" + customerId + " lenght is too long");
            }
            resultSet.close();
            statement.close();

            String insertCustomer_sql = "insert into TBCUSTOMER(" +
                    "CUSTOMER_ID," +
                    "sex," +
                    "CARD_HOLDER_NAME," +
                    "CARD_HOLDER_SURNAM," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "ADDRESS1," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ORIG_CREATE_DATE," +
                    "ORIG_EDIT_DATE," +
                    "TEMPLATE_ID," +
                    "HOMEPHONE," +
                    "NATIONAL_CODE," +
                    "ID_NUMBER," +
                    "BIRTHDATE" +
                    ") values(" +
                    "'" + customerId + "'," +
                    0 + "," +
                    "''," +
                    "''," +
                    "'" + firstName + "'," +
                    "'" + lastName + "'," +
                    "'BANK_TEJARAT'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    1 + "," +
                    "''," +
                    "'" + nationalCode + "'," +
                    "''," +
                    "'00000000'" +
                    " )";
            statement = connection.createStatement();
            statement.execute(insertCustomer_sql);
            statement.close();

            String accountTitle = "8";
            String subTitle = "0";

            String insertCustAcc_sql = "insert into TBCUSTACC (" +
                    "CUSTOMER_ID," +
                    "NATIONAL_CODE," +
                    "EXTERNAL_ID_NUMBER," +
                    "ACCOUNT_NO," +
                    "STATUS," +
                    "ACCOUNT_TITLE," +
                    "SUB_TITLE" +
                    ") values (" +
                    "'" + customerId + "'," +
                    "'" + nationalCode + "'," +
                    "''," +
                    "'" + accountNo + "'," +
                    "'1'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustAcc_sql);
            statement.close();

            String insertCustomerAccount_sql = "insert into TBCUSTOMERACCOUNTS(" +
                    "CUSTOMER_ID," +
                    "ACCOUNT_TYPE," +
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
                    "ACCOUNT_OPENER_NAME, " +
                    "WITHDRAW_TYPE " +
                    ") values(" +
                    "'" + customerId + "'," +
                    "'" +  accountGroup + "'," +
                    "'" + accountNo + "'," +
                    "1," +
                    "'" + Constants.DEFAULT_CURRENCY + "'," +
                    "0, " +
                    "'" + accountTitle + "'," +
                    "1," +
                    2 + "," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + branchCode + "'," +
                    Constants.HOST_CFS + "," +
                    "''," +
                    0 +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertCustomerAccount_sql);
            statement.close();


            String customersrvId = existCustomerAccountInSRV(accountNo);

            if (customersrvId != null) {
                updateDeviceCustomerAccountInSRVTransactional(connection, accountNo, nationalCode, Constants.HOST_CFS,accountGroup);
            } else {
                insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                        accountGroup, nationalCode, Constants.NATIONAL_STATUS, customerId);
            }
            String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                    "SESSION_ID," +
                    "CHANNEL_TYPE," +
                    "PARTNO," +
                    "INSERT_DATETIME," +
                    "ACCOUNT_NO," +
                    "NAME_FA," +
                    "SURNAME_FA," +
                    "BIRTHDATE," +
                    "NATIONAL_CODE," +
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
                    "'" +accountNo + "'," +
                    "'" +firstName + "'," +
                    "'" +lastName + "'," +
                    "'00000000'," +
                    "'" + nationalCode.trim() + "'," +
                    "'" + Constants.CREATE_NAC_STATUS + "'," +
                    "'" + accountTitle + "'," +
                    "'" + subTitle + "'," +
                    1 +","+
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + branchCode + "'," +
                    "'" + accountGroup + "'" +
                    " )";
            nacLogStatment = connection.createStatement();
            nacLogStatment.execute(insertNaclog_sql);
            nacLogStatment.close();

        } catch (SQLException e) {
            log.error("SQLException for branchCode=" + branchCode + "Error::" + e);
             connection.rollback();
            throw e;
        } finally {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (nacLogStatment != null) nacLogStatment.close();
        }
    }


    public static void  createDevicePinpad(String accountNo,String deviceCode,String branchCode,long accountBalance,String sessionId,String channelType,
                                           String pin,String userId,
                                           String deviceStatus) throws SQLException {

        Statement statement = null;
        Connection connection = null;
        String deviceCreationDate="";
        String deviceCreationTime="";
        try {
            connection = dbConnectionPool.getConnection();
            deviceCreationDate=DateUtil.getSystemDate();
            deviceCreationTime=DateUtil.getSystemTime();

            String insertSql = "insert into TBDEVICE(" +
                    "IIN," +
                    "BRANCH_ID," +
                    "ACCOUNT_NO," +
                    "DEVICE_CODE," +
                    "DEVICE_TYPE," +
                    "ISMODIFIED," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ACCOUNT_BALANCE," +
                    "STATUS" +
                    ") values(" +
                    "'" + Constants.BANKE_TEJARAT_BIN_NEW + "'," +
                    "'" + branchCode + "'," +
                    "'" + accountNo + "'," +
                    "'" + deviceCode + "'," +
                    Constants.PINPAD_TYPE + "," +
                    "'" + Constants.ACTIVE_ISMODIFIED + "'," +
                    "'" + deviceCreationDate + "'," +
                    "'" + deviceCreationTime + "'," +
                    0 + "," +
                    "'" + Constants.OFFLINE_ATM_STATUS + "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertSql);

            insertDeviceInDeviceHistory(connection,accountNo, deviceCode, branchCode, pin, userId, sessionId, accountBalance, deviceCreationDate, deviceCreationTime, "", Constants.PINPAD_DEVICE_TYPE,"");
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.deviceCreationDate  -- Error#1 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static int offlineNacATM(String accountNo, String branchId, String deviceCode, String session_id, String channel_type,String pin,String userId
            ,String deviceType,String SN10Url,String SN10Pass,String SN10User,String driver) throws SQLException {
        Connection connection = null;
        Connection connectionSN10=null;
        Statement statement = null;
        ResultSet accRS = null;
        ResultSet deviceRS = null;
        Statement updateAccST = null;
        Statement updateSrvST = null;
        Statement nacLogStatment = null;
        String accountGroup = "";

        connectionSN10=  createConnection(driver, SN10Url, SN10User, SN10Pass);
        connection = dbConnectionPool.getConnection();
        try {

            String accountSql = "SELECT balance, SUBSIDY_AMOUNT " +
                    "from tbCustomerAccounts where ACCOUNT_NO = '" + accountNo + "' for update;";
            statement = connection.createStatement();
            accRS = statement.executeQuery(accountSql);
            if (accRS.next()) {
                if ((accRS.getLong("BALANCE") != 0 || accRS.getLong("SUBSIDY_AMOUNT") != 0)) {
                    //"Invalid operation"
                    connection.rollback();
                    return 2;
                }

                String deviceSql = "SELECT BRANCH_ID,DEVICE_CODE,ACCOUNT_BALANCE,STATUS,ACCOUNT_NO,CREATION_DATE,CREATION_TIME from tbdevice where ACCOUNT_NO = '" + accountNo + "' and DEVICE_CODE='" + deviceCode + "'and BRANCH_ID='" + branchId + "'and DEVICE_TYPE=" + deviceType + " with ur";
                statement = connection.createStatement();
                deviceRS = statement.executeQuery(deviceSql);
                if (deviceRS.next()) {

                    if (deviceRS.getString("STATUS").equals(Constants.OFFLINE_ATM_STATUS)) {
                        //"Invalid operation"
                        connection.rollback();
                        return 2;
                    }

                    String atmAccount = deviceRS.getString("ACCOUNT_NO").trim();
                    if (!atmAccount.equalsIgnoreCase(accountNo)) {
                        //"Invalid operation"
                        connection.rollback();
                        return 2;
                    }


                    String updateAccSQL = "update TBCUSTOMERACCOUNTS set LOCK_STATUS=9 where account_no = '" + accountNo + "'";
                    updateAccST = connection.createStatement();
                    updateAccST.execute(updateAccSQL);

                    String updateSrvSQL = "update TBCUSTOMERSRV set HOST_ID=2 where account_no = '" + accountNo + "'";
                    updateSrvST = connection.createStatement();
                    updateSrvST.execute(updateSrvSQL);


                    String updateSql = "update tbdevice set STATUS = '" + Constants.OFFLINESTATUS_DEVICE + "' where ACCOUNT_NO = '" + accountNo + "' and DEVICE_CODE = '" + deviceCode + "' and BRANCH_ID = '" + branchId + "'and DEVICE_TYPE=" + deviceType ;
                    statement = connection.createStatement();
                    statement.executeUpdate(updateSql);

                    String nationalCode = "0000000000";
                    String accountTitle = "8";

                    if (deviceType.equals(Constants.ATM_TYPE)) {
                        accountGroup = Constants.ATM_ACCOUNT_GROUP;
                    } else if (deviceType.equals(Constants.PINPAD_TYPE)) {
                        accountGroup = Constants.PINPAD_ACCOUNT_GROUP;
                    }

                    String deviceCreationDate =deviceRS.getString("CREATION_DATE").trim();
                    String deviceCreationTime=deviceRS.getString("CREATION_TIME").trim();

                    String insertCustomer_sql = "insert into TB_NAC_LOG(" +
                            "SESSION_ID," +
                            "CHANNEL_TYPE," +
                            "PARTNO," +
                            "INSERT_DATETIME," +
                            "ACCOUNT_NO," +
                            "NATIONAL_CODE," +
                            "OPERATION_TYPE," +
                            "ACCOUNT_TITLE," +
                            "SUB_TITLE," +
                            "HANDLED," +
                            "OPEN_DATE," +
                            "OPEN_TIME," +
                            "BRANCH_ID," +
                            "ACCOUNT_GROUP" +
                            ") values(" +
                            "'" + session_id + "'," +
                            "'" + channel_type + "'," +
                            getPartNo() + "," +
                            "current_timestamp, " +
                            "'" + accountNo + "'," +
                            "'" + nationalCode + "'," +
                            "'" + Constants.REVOKE_STATUS + "'," +
                            "'" + accountTitle + "'," +
                            "'0'," +
                            1 + "," +
                            "'" + deviceCreationDate + "'," +
                            "'" + deviceCreationTime + "'," +
                            "'" + branchId + "'," +
                            "'" + accountGroup + "'" +
                            " )";

                    nacLogStatment = connection.createStatement();
                    nacLogStatment.execute(insertCustomer_sql);
                    nacLogStatment.close();
                    long accBalance=deviceRS.getLong("ACCOUNT_BALANCE");

                    insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchId,pin,userId,session_id,accBalance,deviceCreationDate,deviceCreationTime,Constants.ONLINE_ATM_STATUS,deviceType,"");
                    revokeFromBranchManager(connectionSN10, accountNo, branchId);
                    connection.commit();
                    connectionSN10.commit();
                } else {
                    connection.rollback();
                    if(connectionSN10!=null) connectionSN10.rollback();
                    return 5;
                }
            } else {
                connection.rollback();
                if(connectionSN10!=null) connectionSN10.rollback();
                return 4;
            }
        } catch (SQLException e) {
            log.error(" Error in ChannelFacadeNew.offlineNacATM :: " + e);
            try {
                connection.rollback();
                if(connectionSN10!=null) connectionSN10.rollback();
            } catch (SQLException e1) {
                log.error(" Error in ChannelFacadeNew.offlineNacATM :: " + e1);
                e1.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
                if (deviceRS != null) deviceRS.close();
                if (statement != null) statement.close();
                if (updateSrvST != null) updateSrvST.close();
                if (updateAccST != null) updateAccST.close();
                if (nacLogStatment != null) nacLogStatment.close();
            } catch (SQLException e1) {
                log.error("Error in ChannelFacadeNew.offlineNacATM #1: -- Error :: " + e1);
                e1.printStackTrace();
            }
            dbConnectionPool.returnConnection(connection);
            if(connectionSN10!=null)connectionSN10.close();
        }
        return 0;
    }


    public static void InActiveAtm(String accountNo,String deviceCode,String branchCode,String sessionId,String pin,String userId,
                                   long accountBalance,String deviceCreationDate,String deviceCreationTime,String deviceStatus,String deviceType) throws SQLException  {


        Statement deleteST = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String delete = "delete from tbdevice where account_no = '" + accountNo + "' and DEVICE_CODE='"+deviceCode+"' and BRANCH_ID='"+branchCode+"'and DEVICE_TYPE=" + deviceType ;
            deleteST = connection.createStatement();
            deleteST.execute(delete);
            insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchCode,pin,userId,sessionId,accountBalance,deviceCreationDate,deviceCreationTime,deviceStatus,deviceType,"");
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.InActiveAtm   -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (deleteST != null) deleteST.close();
            dbConnectionPool.returnConnection(connection);
        }

    }
    public static String[] findAccountNo(String accountNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String[] accountInfo=new String[2];

        try {
            connection = dbConnectionPool.getConnection();
            String sql = "select ACCOUNT_NO,LOCK_STATUS from tbcustomeraccounts where account_no ='" + accountNo + "' with ur ";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {

                accountInfo[0] = resultSet.getString("ACCOUNT_NO").trim();
                accountInfo[1]= resultSet.getString("LOCK_STATUS").trim();

            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.findAccountNo  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return accountInfo;
    }

    public static String onlinePinpad(String accountNo,String deviceCode,String branchCode,long accountBalance,String lockStatus,String sessionId,String channelType,
                                   String pin,String userId,String deviceCreationDate,String deviceCreationTime,
                                   String deviceStatus,String deviceType,String finUrl,String SN10Url,String finPass,String SN10Pass,String finUser,String SN10User,String driver) throws SQLException,ModelException {


        Connection connection = null;
        Connection connectionFIN = null;
        Connection connectionSN10 = null;
        Statement updateStatement1 = null;
        Statement updateStatement2 = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement nacLogStatment = null;
        String customerId="";
        String nationalCode="0000000000";
        String accountTitle = "8";
        String subTitle = "0";
        String firstName = "";
        String lastName = "";
        boolean existTransaction=false;
        int checkBalance=0;
        String errorCode="0";
        String accountGroup="";

        try {
            connectionSN10=  createConnection(driver,SN10Url, SN10User,SN10Pass);
            connection = dbConnectionPool.getConnection();
            if(deviceType.equals(Constants.ATM_TYPE)) {

                byte[] firstNameByte = {67, 84, 35, 85, 75, 35, 65, 124, 78, 60, 86, 87, 60, 89};
                try {
                    String s = new String(firstNameByte, "ISO-8859-1");
                    firstName = FarsiUtil.convertWindows1256(firstNameByte);
                } catch (UnsupportedEncodingException e) {
                    firstName = "";
                }
                byte[] lastNameByte = {63, 85, 35, 43, 39, 35, 60, 61};
                try {
                    String s = new String(firstNameByte, "ISO-8859-1");
                    lastName = FarsiUtil.convertWindows1256(lastNameByte);
                } catch (UnsupportedEncodingException e) {
                    lastName = "";
                }
                accountGroup=Constants.ATM_ACCOUNT_GROUP;
            }
            else if(deviceType.equals(Constants.PINPAD_TYPE)) {
                connectionFIN= createConnection(driver,finUrl,finUser,finPass);

                byte[] firstNameByte = {67,84,35,85,75,40,46,41,86};
                try {
                    String s = new String(firstNameByte, "ISO-8859-1");
                    firstName = FarsiUtil.convertWindows1256(firstNameByte);
                } catch (UnsupportedEncodingException e) {
                    firstName = "";
                }
                byte[] lastNameByte = {43,46,88,46,84,86,87,46,89,74,39,85,66};
                try {
                    String s = new String(firstNameByte, "ISO-8859-1");
                    lastName = FarsiUtil.convertWindows1256(lastNameByte);
                } catch (UnsupportedEncodingException e) {
                    lastName = "";
                }
                accountGroup = Constants.PINPAD_ACCOUNT_GROUP;

                existTransaction = searchTransaction(connection, accountNo);
                if (existTransaction == true) {
                    log.error("Error in ChannelFacadeNew.onlineATM()!!The account has a transaction today :: accountNo=" + accountNo);
                    errorCode = "1";
                }
                checkBalance = checkBalance(connectionFIN, accountNo);
                if (checkBalance == 1) {
                    log.error("Error in ChannelFacadeNew.onlineATM()!!The account's balance is not zero in FIN :: accountNo=" + accountNo);
                    errorCode = "2";
                } else if (checkBalance == 2) {
                    log.error("Error in ChannelFacade.onlineATM()!!The account is not in FIN :: accountNo=" + accountNo);
                    errorCode = "3";
                }
                connectionFIN.commit();
            }

         if(errorCode.equals("0")){
            String updateSql = "update tbdevice set  STATUS= '" + Constants.ONLINE_ATM_STATUS + "' where ACCOUNT_NO = '" + accountNo + "'and device_code='" + deviceCode + "' and DEVICE_TYPE=" + deviceType;
            updateStatement1 = connection.createStatement();
            updateStatement1.executeUpdate(updateSql);


            if (lockStatus.equals("9")) {
                String update = "update tbcustomeraccounts set lock_status=1 , balance=0 ,ACCOUNT_TYPE='"+accountGroup+"',SGB_BRANCH_ID='"+branchCode+"' where ACCOUNT_NO='" + accountNo + "'";
                updateStatement2 = connection.createStatement();
                updateStatement2.executeUpdate(update);

                String customersrvId = existCustomerAccountInSRV(accountNo);

                if (customersrvId != null) {
                    updateDeviceCustomerAccountInSRVTransactional(connection, accountNo, "0000000000", Constants.HOST_CFS,accountGroup);
                } else {

                    String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(maxCustomer_id_sql);
                    resultSet.next();
                    String maxID = resultSet.getString(1).trim();
                    if (maxID != null)
                        customerId = maxID;
                    try {
                        customerId = ISOUtil.zeropad(customerId, 10);
                    } catch (ISOException e) {
                        log.error(e);
                    }
                    resultSet.close();
                    statement.close();

                    insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                           accountGroup, "0000000000", Constants.NATIONAL_STATUS, customerId);
                }
                String customerIdCustacc=existAccountInCustacc(accountNo);
                if(customerIdCustacc!=null)
                {
                    updateCustomerTransactional(connection,customerIdCustacc,firstName,lastName);
                }
                String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                        "SESSION_ID," +
                        "CHANNEL_TYPE," +
                        "PARTNO," +
                        "INSERT_DATETIME," +
                        "ACCOUNT_NO," +
                        "NAME_FA," +
                        "SURNAME_FA," +
                        "BIRTHDATE," +
                        "NATIONAL_CODE," +
                        "OPERATION_TYPE," +
                        "ACCOUNT_TITLE," +
                        "SUB_TITLE," +
                        "HANDLED," +
                        "OPEN_DATE," +
                        "OPEN_TIME," +
                        "BRANCH_ID," +
                        "ACCOUNT_GROUP" +
                        ") values(" +
                        "'" + sessionId + "'," +
                        "'" + channelType + "'," +
                        getPartNo() + "," +
                        "current_timestamp, " +
                        "'" + accountNo + "'," +
                        "'" + firstName + "'," +
                        "'" + lastName + "'," +
                        "'00000000'," +
                        "'" + nationalCode + "'," +
                        "'" + Constants.CREATE_NAC_STATUS + "'," +
                        "'" + accountTitle + "'," +
                        "'" + subTitle + "'," +
                        1 + "," +
                        "'" + DateUtil.getSystemDate() + "'," +
                        "'" + DateUtil.getSystemTime() + "'," +
                        "'" + branchCode + "'," +
                        "'" + accountGroup + "'" +
                        " )";
                nacLogStatment = connection.createStatement();
                nacLogStatment.execute(insertNaclog_sql);
                nacLogStatment.close();
                sendToBranchManager(connectionSN10,accountNo,branchCode, accountGroup,deviceType);
            }
            if (lockStatus.equals("1")) {
                throw new ModelException("Invalid ATM opperation!!");
            } else if (lockStatus.equals(Constants.NO_LOCKSTATUS_FLAG)) {
                createNAC_ATM(connection, accountNo, branchCode, sessionId, channelType,firstName,lastName,accountGroup);
                sendToBranchManager(connectionSN10,accountNo,branchCode, accountGroup,deviceType);
            }
            // insert in tbdevicerhistory
            insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchCode,pin,userId,sessionId,accountBalance,deviceCreationDate,deviceCreationTime,deviceStatus,deviceType,"");
         }
            connection.commit();
            connectionSN10.commit();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.onlineATM() #1= -- Error :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            if (connectionFIN != null) connectionFIN.rollback();
            throw e;
        } catch (NumberFormatException e) {
            log.error("ChannelFacadeNew.onlineATM() #2= -- Error :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            if (connectionFIN != null) connectionFIN.rollback();
            throw e;
        }catch (ModelException e){
            log.error("ChannelFacadeNew.onlineATM() #3= -- Error :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            if (connectionFIN != null) connectionFIN.rollback();
            throw e;
        } catch (ISOException e) {
            log.error("ChannelFacadeNew.onlineATM() #4= -- Error :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            if (connectionFIN != null) connectionFIN.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (updateStatement1 != null) updateStatement1.close();
            if (updateStatement2 != null) updateStatement2.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
            if (connectionSN10 != null)connectionSN10.close();
            if (connectionFIN != null) connectionFIN.close();

        }
        return  errorCode;
    }

    private static int updateCustomerAccountInSRVTransactional4ATM(Connection connection, String account, String nationalCode,int hostCode) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMERSRV set LAST_USAGE_TIME= current_timestamp,NATIONAL_CODE= '" + nationalCode + "',HOST_ID="+hostCode +
                " where ACCOUNT_NO = '" + account + "' ";
        try {

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updatetCustomerAccountInSRVTransactional with national Code = " + e + " -- sql = " + sql);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;

    }



    private static void insertDeviceInDeviceHistory(Connection connection,String accountNo, String deviceCode,String branchCode,
                                                    String pin,String userId,String sessionId,long accountBalance,String deviceCreationDate,String deviceCreationTime,String deviceStatus,String deviceType,String oldBranchCode) throws SQLException {
        Statement statement = null;
        try {

            String sql = "insert into tbdeviceHistory(" +
                    "SESSION_ID," +
                    "PIN," +
                    "INSERT_DATE," +
                    "INSERT_TIME," +
                    "USER_ID," +
                    "BRANCH_ID," +
                    "ACCOUNT_NO," +
                    "DEVICE_CODE," +
                    "DEVICE_TYPE," +
                    "ACCOUNT_BALANCE," +
                    "STATUS," +
                    "ISMODIFIED," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "OLD_BRANCH_CODE" +
                    ") values (" +
                    "'" + sessionId + "'," +
                    "'" + pin + "'," +
                    "'" + DateUtil.getSystemDate() + "'," +
                    "'" + DateUtil.getSystemTime() + "'," +
                    "'" + userId + "'," +
                    "'" + branchCode + "'," +
                    "'" + accountNo + "'," +
                    "'" + deviceCode + "'," +
                    deviceType + "," +
                    accountBalance + "," +
                    "'"+ deviceStatus+"',"+
                    "'"+ Constants.ACTIVE_ISMODIFIED+"',"+
                    "'"+ deviceCreationDate+"',"+
                    "'"+deviceCreationTime+"',"+
                    "'"+oldBranchCode+"'"+
                    ")";

            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.insertDeviceInDeviceHistory() #1= -- Error :: " + e);
            throw e;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.insertDeviceInDeviceHistory() #2= -- Error :: " + e1);
            }
        }
    }
    public static  String[] isExistAtm(String accNo,String deviceCode,String deviceType) throws SQLException, ISOException {

        String sql = "select ACCOUNT_NO,ACCOUNT_BALANCE,CREATION_DATE,CREATION_TIME,STATUS,BRANCH_ID FROM tbdevice where " +
              "ACCOUNT_NO = '" + accNo + "' and DEVICE_CODE = '" + deviceCode + "' and DEVICE_TYPE=" + deviceType + " with ur";
        String [] deviceFields=new String[6];
        Connection conn = null;
        Statement stm = null;
        ResultSet resultSet = null;
        try {
            conn = dbConnectionPool.getConnection();
            stm = conn.createStatement();
            resultSet = stm.executeQuery(sql);

            if (resultSet.next()) {
                deviceFields[0]=resultSet.getString("ACCOUNT_NO").trim();
                deviceFields[1]=resultSet.getString("STATUS").trim();
                deviceFields[2]= String.valueOf(resultSet.getLong("ACCOUNT_BALANCE"));
                deviceFields[3]= resultSet.getString("CREATION_DATE").trim();
                deviceFields[4]=resultSet.getString("CREATION_TIME").trim();
                deviceFields[5]=resultSet.getString("BRANCH_ID").trim();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                log.error("ChannelFacadeNew.isExistAtm  -- Error :: " + e);
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(conn);
        }
        return deviceFields;
    }

    //**********siminATM*******end***********
    //**********one time paid*******start***********
    public static boolean checkId(String accNo,String id) throws SQLException, ISOException {

        String sql = "select ISPAID FROM tbonetimeid where " +
                "ACCOUNT_NO = '" + accNo + "' and id = " + id + " and ISPAID = '" + Constants.ID_PAID + "' with ur";
        Connection conn = null;
        Statement stm = null;
        ResultSet resultSet = null;
        boolean isPaid=false;
        try {
            conn = dbConnectionPool.getConnection();
            stm = conn.createStatement();
            resultSet = stm.executeQuery(sql);

            if (resultSet.next()) {
                isPaid=true;
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(conn);
        }
        return isPaid;
    }
    //**********One time paid*******end***********
    //**********SiminCBI**********start********
    public static boolean checkUserExist(String accountNo,String nationalCode,String externalId) throws NotFoundException, SQLException, Exception {

        String sql = "select NATIONAL_CODE,EXTERNAL_ID from TBSHAHAB where ACCOUNT_NO = '" + accountNo + "'with ur ";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean isExist=false;
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                isExist=true;
                String national_code = resultSet.getString("NATIONAL_CODE").trim();
                String external_id = resultSet.getString("EXTERNAL_ID").trim();
                external_id =ISOUtil.zeroUnPad(external_id);
                externalId=ISOUtil.zeroUnPad(externalId);
                if (national_code.trim().equals("9999999999")) {

                    if (!nationalCode.trim().equals("") && nationalCode.trim().equals(national_code.trim()) &&
                            !externalId.trim().equals("") && externalId.trim().equals(external_id) ) {
                        connection.commit();
                        return true;
                    }
                } else {
                    if (!nationalCode.trim().equals("") && nationalCode.equals(national_code)) {
                        connection.commit();
                        return true;

                    }
                }
            }
            if (!isExist) {
                connection.rollback();
                throw new NotFoundException("ChannelFacadeNew.checkUserExist()= --AccountNo =" + accountNo + " does not exist in tbshahab ");
            }
            connection.commit();
            return false;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.checkUserExist() =   -- Error1 :: " + e);
            connection.rollback();
            throw e;
        } catch (Exception e) {
            log.error("ChannelFacadeNew.checkUserExist() =   -- Error2 :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String[] checkIsRevoke(String accountNo) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        Statement accHistorySt=null;
        ResultSet resultSet = null;
        ResultSet accHistoryRs=null;
        String[] fields = new String[4];

        try {
            String sql = "select HOST_id,STATUSD,SMSNOTIFICATION from tbcustomersrv where ACCOUNT_NO ='" + accountNo + "' with ur";

            String accHistory_sql = "select account_no from tbAccounthistory where ACCOUNT_NO='" + accountNo + "' with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();

            if (resultSet.next()) {
                fields[0] = Constants.NOT_REVOKED;
                fields[1] = resultSet.getString("HOST_id").trim();
                fields[2] = resultSet.getString("STATUSD").trim();
                fields[3] = resultSet.getString("SMSNOTIFICATION").trim();
            } else {
                accHistorySt = connection.createStatement();
                accHistoryRs = accHistorySt.executeQuery(accHistory_sql);
                connection.commit();
                if (accHistoryRs.next()) {
                    fields[0] = Constants.IS_REVOKED;
                    fields[1] = "";
                    fields[2] = "";
                    fields[3] = "";
                } else {
                    throw new NotFoundException("ChannelFacadeNew.checkIsRevoke()::Customer with accountNo = " + accountNo + " does not exist");
                }
            }
            return fields;
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.checkIsRevoke() #1= -- Error :: " + e);
            connection.rollback();
            throw e;
        } catch (NumberFormatException e) {
            log.error("ChannelFacadeNew.checkIsRevoke() #2= -- Error :: " + e);
            connection.rollback();
            throw e;
        } catch (NotFoundException e) {
            connection.rollback();
            log.error("ChannelFacadeNew.checkIsRevoke() #3= -- Error :: " + e);
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (accHistoryRs != null) accHistoryRs.close();
            if (accHistorySt != null) accHistorySt.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //**********SiminCBI**********end**********
    //*********PINPAD*********START************
    public static void sendToBranchManager(Connection connectionSN10,String accountNo,String branchCode,String accountGroup,String deviceType) throws SQLException, ISOException {

        Statement statement = null;
        ResultSet resultSet=null;
        String iban = "";
        String identitySerial = "";
        String msgStringReq = "";
        String messageSequence="";
        try {
            String maxMessageSequence_sql = "select next value for seq_SInacIntegration  as maxID from sysibm.sysdummy1 with ur";

            statement = connectionSN10.createStatement();
            resultSet = statement.executeQuery(maxMessageSequence_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                messageSequence = maxID;
            try {
                messageSequence = "SI"+ISOUtil.zeropad(messageSequence, 10);
            } catch (ISOException e) {
                log.error("ERROR inside ChannelFacadeNew.sendToBranchManager():: messageSequence=" + messageSequence + " lenght is too long");
            }
            resultSet.close();
            statement.close();
            iban = calculateIban(accountNo);
            if(deviceType.equals(Constants.ATM_TYPE))
            {identitySerial ="12345678"; }
              else{
            identitySerial = generateIdentitySerial(accountNo, "0000000000");
            }
            msgStringReq = packMessageForCreateNAC(accountGroup, branchCode, identitySerial, iban, accountNo, messageSequence);
            statement = connectionSN10.createStatement();

            String insertSql = "insert into tb_integration_nac_log (MESSAGE_SEQUENCE,Insert_DateTime,pin,Account_NO,MSGSTRING_req,CREATION_DATE) " +
                    "VALUES (" +
                    "'" + messageSequence + "'," +
                    "current_timestamp," +
                    "'" + Constants.BRANCH_MANAGER_CREATE_PIN + "'," +
                    "'" + accountNo + "'," +
                    "'" + msgStringReq + "'," +
                    "'" + DateUtil.getSystemDate() + "'" +
                    ")";

            statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            log.error("Error#1 in ChannelFacadeNew.sendToBranchManager() for message_sequence :: " + messageSequence + " :: " + e);
            throw e;
        } catch (ISOException e) {
            log.error("Error#2 in ChannelFacadeNew.sendToBranchManager() for message_sequence :: " + messageSequence + " :: " + e);
            throw e;
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                log.error("Error in ChannelFacadeNew.sendToBranchManager()!!::" + e);
            }
        }
    }
    public static String packMessageForCreateNAC(String accountGroup, String branchCode, String identitySerial, String iban, String accountNo, String messageSequence) throws ISOException{
        branchCode=ISOUtil.padleft(branchCode,6,'0');
        StringBuilder sb = new StringBuilder();
        sb.append(packHeader(messageSequence,Constants.BRANCH_MANAGER_CREATE_PIN));
        sb.append(accountNo);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(accountGroup);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(branchCode);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(identitySerial);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append("0001");//noteSequence
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(DateUtil.getSystemDate());
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(iban);
        return sb.toString();
    }


    public static String packHeader(String messageSequence,String pin) {
        StringBuilder sb = new StringBuilder();
        sb.append(pin);   //create:995   revoke:997
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(messageSequence);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append("999999"); //branchCode
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append("999998");//userID
        sb.append(Constants.SEPARATOR_BRANCH);
        try {
            sb.append(ISOUtil.padleft("1", 5, ' '));//messageVersion
        } catch (ISOException e) {
            log.error("ERROR::Inside ChannelFacadeNew.packHeader() for messageSequence=" + messageSequence + " messageVersion lenght is too long");
        }

        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append("macAddress");
        sb.append(Constants.SEPARATOR_BRANCH);
        return sb.toString();
    }


    public static String generateIdentitySerial(String accountNo, String nationalCode) throws ISOException {
        accountNo = accountNo.substring(3, 13);
        if ((Long.parseLong(accountNo) > 3200000000L && Long.parseLong(accountNo) < 5400000000L))
            return ISOUtil.padleft("", 8, ' ');
        if ((Long.parseLong(accountNo) > 8100000000L && Long.parseLong(accountNo) < 9996500000L))
            return ISOUtil.padleft("", 8, ' ');
        if (nationalCode.trim().equals("0000000000"))
            return "12345678";

        try {
            long accountNoL = Long.valueOf(accountNo);
            long sum_Account = 0;
            while (accountNoL > 0L) {
                int cur_digit = (int) (accountNoL % 10);
                sum_Account += cur_digit;
                accountNoL = accountNoL / 10;
            }

            long nationalCodeL = Long.valueOf(nationalCode.trim());
            long sum_nationalCode = 0;
            while (nationalCodeL > 0L) {
                int cur_digit = (int) (nationalCodeL % 10);
                sum_nationalCode += cur_digit;
                nationalCodeL = nationalCodeL / 10;
            }

            String serial = "";
            if (0 < sum_Account && sum_Account < 31)    //s1
                serial += "1";
            else if (30 < sum_Account && sum_Account < 61)    //s1
                serial += "2";
            else if (60 < sum_Account && sum_Account < 100)    //s1
                serial += "3";
            else
                serial += "0";

            serial += ISOUtil.zeropad(String.valueOf(sum_Account), 2);    //s2

            if (0 < sum_nationalCode && sum_nationalCode < 31)   //s3
                serial += "1";
            else if (30 < sum_nationalCode && sum_nationalCode < 61)   //s3
                serial += "2";
            else if (60 < sum_nationalCode && sum_nationalCode < 100)   //s3
                serial += "3";

            serial += ISOUtil.zeropad(String.valueOf(sum_nationalCode), 2);  //s4

            serial += "01";  //s5

            return serial;
        } catch (Exception e) {
            log.error("Error in ChannelFacadeNew.generateIdentitySerial()::account:" + accountNo + " nationalcode:" + nationalCode + " " + e);
            return "12345678";
        }
    }
    public static void revokeFromBranchManager(Connection connectionSN10,String accountNo,String branchCode) throws SQLException {

        Statement statement = null;
        ResultSet resultSet=null;
        String msgStringReq = "";
        String messageSequence="";
        try {
            String maxMessageSequence_sql = "select next value for seq_SInacIntegration  as maxID from sysibm.sysdummy1 with ur";

            statement = connectionSN10.createStatement();
            resultSet = statement.executeQuery(maxMessageSequence_sql);
            resultSet.next();
            String maxID = resultSet.getString(1).trim();
            if (maxID != null)
                messageSequence = maxID;
            try {
                messageSequence = "SI"+ISOUtil.zeropad(messageSequence, 10);
            } catch (ISOException e) {
                log.error("Error Inside ChannelFacadeNew.revokeFromBranchManager():: messageSequence=" + messageSequence + " lenght is too long");
            }
            resultSet.close();
            statement.close();


            msgStringReq = packMessageForRevokeAccount(accountNo, messageSequence);
            statement = connectionSN10.createStatement();

            String insertSql = "insert into tb_integration_nac_log (MESSAGE_SEQUENCE,Insert_DateTime,pin,Account_NO,MSGSTRING_req,CREATION_DATE) " +
                    "VALUES (" +
                    "'" + messageSequence + "'," +
                    "current_timestamp," +
                    "'" + Constants.BRANCH_MANAGER_REVOKE_PIN + "'," +
                    "'" + accountNo + "'," +
                    "'" + msgStringReq + "'," +
                    "'" + DateUtil.getSystemDate() + "'" +
                    ")";

            statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            log.error("Error#1 in ChannelFacadeNew.sendToBranchManager for message_sequence :: " + messageSequence + " :: " + e);
            throw e;
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                log.error("Error in ChannelFacadeNew.sendToBranchManager ()!! :: " + e);
                e.printStackTrace();
            }
        }
    }
    public static String packMessageForRevokeAccount(String accountNo, String messageSequence)  {
        String requestType = "01";
        StringBuilder sb = new StringBuilder();
        sb.append(packHeader(messageSequence,Constants.BRANCH_MANAGER_REVOKE_PIN));
        sb.append(requestType);
        sb.append(Constants.SEPARATOR_BRANCH);
        sb.append(accountNo);
        return sb.toString();
    }

    public static String calculateIban(String accountNo) throws ISOException {
        String ibanNumber = accountNo.trim().substring(2);
        StringBuilder stringBuilder = new StringBuilder("3");
        stringBuilder.append(ibanNumber).append("182700");
        Long iban = Long.parseLong(stringBuilder.toString());
        int modIban = 98 - (int) (iban % 97);
        StringBuilder finalIban = new StringBuilder("IR");
        finalIban.append(ISOUtil.zeropad(String.valueOf(modIban), 2)).append("018").append(ISOUtil.zeropad(accountNo.trim(), 19));
        return finalIban.toString();
    }
    public static Connection createConnection(String driver,String url,String user,String password) throws SQLException {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            log.error("Error in ChannelFacadeNew.createConnection()::"+e);
            throw new SQLException("Error in ChannelFacadeNew.createConnection()::"+e);
        }
        return connection;
    }
    public static boolean searchTransaction(Connection connection, String accountNo) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        boolean isExist=false;
        try {
            String sql = "select partno from tbcfstx where (src_account_no='"+accountNo+"' or dest_account_no='"+accountNo+"')" +
                    " and CREATION_DATE='"+DateUtil.getSystemDate()+"' with ur ";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next())
            {
               isExist=true;
            }
         return isExist;
        } catch (SQLException e) {
            log.error("Error Inside ChannelFacadeNew.searchTransaction()--Error#1::"+e);
            connection.rollback();
            throw e;
        } finally {
            try {
                if (resultSet != null){resultSet.close();}
                if (statement != null){statement.close();}
            } catch (SQLException e) {
                log.error("Inside ChannelFacadeNew.searchTransaction()--Error#2::"+e);
                e.printStackTrace();
            }
        }
    }

    public static int checkBalance(Connection connectionFIN, String accountNo) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            accountNo = ISOUtil.zeroUnPad(accountNo);
            String sql = "select BLNC from nac.tabalanc where acnt_no=" + accountNo + " with ur ";
            statement = connectionFIN.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                long balance = resultSet.getLong("BLNC");
                if (balance == 0.00)
                    return 0;
                else
                    return 1;
            } else
                return 2;
        } catch (SQLException e) {
            log.error("Error Inside ChannelFacadeNew.checkBalance()--Error#1::" + e);
            connectionFIN.rollback();
            throw e;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                log.error("Error Inside ChannelFacadeNew.checkBalance()--Error#2::" + e);
                e.printStackTrace();
            }
        }
    }
    private static int updateDeviceCustomerAccountInSRVTransactional(Connection connection, String account, String nationalCode,int hostCode,String accountGroup) throws SQLException {

        Statement statement = null;
        int updateCount = 0;

        try {
            String UpdateCustomerSrv = "update TBCUSTOMERSRV set LAST_USAGE_TIME= current_timestamp,NATIONAL_CODE= '" + nationalCode + "',HOST_ID=" + hostCode +
                    ",ACCOUNT_GROUP='" + accountGroup + "',STATUS=1 where ACCOUNT_NO = '" + account + "'";
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(UpdateCustomerSrv);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updateDeviceCustomerAccountInSRVTransactional " + e);
            throw e;
        } finally {
            if (statement != null) statement.close();
        }

      return updateCount;
    }
    public static String existAccountInCustacc(String accountNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "select CUSTOMER_ID from tbcustacc where ACCOUNT_NO = '" + accountNo + "' and NATIONAL_CODE='0000000000' with ur";

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next())
                return resultSet.getString("CUSTOMER_ID").trim();
            else
                return null;

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew.existAccountInCustacc()>>>" + e);
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

    }
    private static int updateCustomerTransactional(Connection connection, String customerId, String firstName,String lastName) throws SQLException {
        Statement statement = null;
        int updateCount = 0;
        String sql = "update TBCUSTOMER set NAME_FA='"+firstName+"', SURNAME_FA='" + lastName + "' where CUSTOMER_ID = '" + customerId + "' ";

        try {

            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--updateCustomerTransactional  = " + e );
            throw e;
        } finally {
            if (statement != null) statement.close();
        }
        return updateCount;

    }
    //*********PINPAD*********END**************
    //**********Financial Group BlockUnBlock**********start***
    public static String[] getBatchPkProcessStatus(String account_no, String blockNo) throws SQLException, NotFoundException {
        String[] inquiry = new String[2];
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dbConnectionPool.getConnection();
            String sql = "select PROCCESS_STATUS,BATCHPK from tbkashef where ACCOUNT_NO = '" + account_no +
                    "' and BLOCK_NO='" + blockNo + "'  and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "' with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                inquiry[0] = resultSet.getString("PROCCESS_STATUS").trim();
                inquiry[1] = String.valueOf(resultSet.getInt("BATCHPK")).trim();
            } else {
                throw new NotFoundException("Error #1 ChannelFacadeNew.getBatchPkProcessStatus()::Can not find any row for blockNo::" + blockNo);
            }
            connection.commit();
            return inquiry;
        } catch (SQLException e) {
            log.error("Error #2 ChannelFacadeNew.getBatchPkProcessStatus():" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("Error --ChannelFacadeNew.getBatchPkProcessStatus() can not rollback connection!!");
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String getBlockStatus(int batchPk) throws SQLException, NotFoundException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String status="";
        try {
            connection = dbConnectionPool.getConnection();
            String blockStatus = "select STATUS from tbkashefbatch where batchpk = " + batchPk +" with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(blockStatus);
            if (resultSet.next()) {
               status = resultSet.getString("STATUS").trim();
            } else {
                throw new NotFoundException("Error #1 ChannelFacadeNew.getBlockStatus()::Can not find any row for batchpk::" + batchPk);
            }
            connection.commit();
            return status;
        } catch (SQLException e) {
            log.error("Error #2 ChannelFacadeNew.getBlockStatus():" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("Error --ChannelFacadeNew.getBlockStatus() can not rollback connection!!");
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateBlockValues(String account_no, String process_status, String actionCode, int batchpk,String blockNo,String accountStatus,String unblockDescription) throws NotFoundException, SQLException {

        Connection connection = null;
        Statement updateST = null;
        connection = dbConnectionPool.getConnection();
        try {
            String blockUpdateSQL = "update tbkashef set ACTIONCODE='" + actionCode + "'"
                    + "," + "PROCCESS_STATUS = '" + process_status + "'"
                    + "," + "EFFECTIVE_DATE = '" + DateUtil.getSystemDate() + "',"
                    + "EFFECTIVE_TIME = '" + DateUtil.getSystemTime() + "'"
                    + " where BATCHPK="+batchpk+" and BLOCK_NO='" + blockNo + "'and ACCOUNT_NO= '" + account_no + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "'" ;

            String unblockUpdateSQL = "update tbkashef set ACTIONCODE='" + actionCode + "'"
                    + "," + "PROCCESS_STATUS = '" + process_status + "'"
                    + "," + "NATIONALCODE = '" + DateUtil.getSystemTime() + "'," //nationalCode as UnblockTime
                    + "UNBLOCK_DATE = '" + DateUtil.getSystemDate() + "',"
                    +"UNBLOCK_DESC= '"+unblockDescription +"'"
                    + " where BATCHPK="+batchpk+" and BLOCK_NO='" + blockNo + "'and ACCOUNT_NO= '" + account_no + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "'";

            updateST = connection.createStatement();
            if (accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS)) {
                updateST.execute(blockUpdateSQL);
            } else {
                updateST.execute(unblockUpdateSQL);
            }

            connection.commit();
        } catch (SQLException e) {
            log.error("Error --ChannelFacadeNew.updateBlockValues()::" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateBlockValues(),  Can not rollback -- Error :: " + e1);
            }
            throw e;
        } finally {

            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error(" ChannelFacadeNew.updateBlockValues() = -- Error :: " + e1);
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateResultStatus(String accountNo, int batchpk) throws NotFoundException, SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement updateST = null;
        Statement insStm = null;
        int newResualtStatus = 0;
        connection = dbConnectionPool.getConnection();
        try {
            String sql = "SELECT RESULTSTATUS " +
                    "from tbkashefbatch where batchpk = " + batchpk + " with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                int resualtStatus = resultSet.getInt("RESULTSTATUS");
                if (resualtStatus > 0) {
                    newResualtStatus = resualtStatus - 1;
                }

                String updateSQL = "update tbkashefbatch set RESULTSTATUS= " + newResualtStatus +
                        "  where BATCHPK = " + batchpk;
                updateST = connection.createStatement();
                int i=updateST.executeUpdate(updateSQL);
                if (i < 1) {
                    log.error("Inside channelFacadeNew.updateResultStatus()!!Can not update RESULTSTATUS for batchpk::" + batchpk);
                }

                connection.commit();
            } else {
                throw new NotFoundException("Error#1 --ChannelFacadeNew.updateResultStatus() ::Can not find accountNo:: " + accountNo);
            }
        } catch (NotFoundException e) {
            log.error("Error#2--ChannelFacadeNew.updateResultStatus()= :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateResultStatus() = can not roll back connection :: " + e1);
            }
            throw e;
        } catch (SQLException e) {
            log.error("Error#3--ChannelFacadeNew.updateResultStatus():: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateResultStatus()= can not roll back connection :: " + e1);
            }
            throw e;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e1) {
                log.error("Error#4 --ChannelFacadeNew.updateResultStatus()= :: " + e1);
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("Error#5 --ChannelFacadeNew.updateResultStatus()= :: " + e1);
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("Error#6 --ChannelFacadeNew.updateResultStatus()= :: " + e1);
            }
            try {
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("Error#7 --ChannelFacadeNew.updateResultStatus()= :: " + e1);
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateResultStatusUnblock(String accountNo, int batchpk,String type) throws NotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet accRS = null;
        Statement updateST = null;
        Statement insStm = null;
        int newResualtStatus = 0;

        connection = dbConnectionPool.getConnection();
        try {
            String sql = "SELECT RESULTSTATUS " +
                    "from tbkashefbatch where ENDDATE ='" + String.valueOf(batchpk) + "' with ur";
            statement = connection.createStatement();
            accRS = statement.executeQuery(sql);
            if (accRS.next()) {

                int resualtStatus = accRS.getInt("RESULTSTATUS");
                if (type.equals(Constants.BEFORE_UNBLOCK_JOB)) {
                    if (resualtStatus > 0) {
                        newResualtStatus = resualtStatus - 1;
                    }
                } else if (type.equals(Constants.AFTER_UNBLOCK_JOB)) {
                    if (resualtStatus >= 0) {
                        newResualtStatus = resualtStatus + 1;
                    }
                }
                String updateSQL = "update tbkashefbatch set RESULTSTATUS=" + newResualtStatus +
                        "  where ENDDATE ='" + batchpk + "'";
                updateST = connection.createStatement();
                updateST.execute(updateSQL);
                connection.commit();
            } else {
                connection.rollback();
                throw new NotFoundException("Error #1--ChannelfacadeNew.updateResultStatusUnblock():Can not find batchpk = " + batchpk);
            }
        } catch (NotFoundException e) {
            try {
                log.error("Error #2--ChannelFacadeNew.updateResultStatusUnblock()::" + e);
                connection.rollback();
            } catch (SQLException e1) {
                log.error("Error --ChannelfacadeNew.updateResultStatusUnblock() can not roll back connection = :: " + e1);
            }
            throw e;
        } catch (SQLException e) {
            log.error("Error #3--ChannelFacadeNew.updateResultStatusUnblock()::" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateResultStatusUnblock(),Can not rollback connection :: " + e1);
            }
            throw e;
        } finally {
            try {
                if (accRS != null) accRS.close();
            } catch (SQLException e1) {
                log.error("Error #4--ChannelfacadeNew.updateResultStatusUnblock() :: " + e1);
            }
            try {
                if (statement != null) statement.close();
            } catch (SQLException e1) {
                log.error("Error #5 --ChannelfacadeNew.updateResultStatusUnblock():: " + e1);
            }
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("Error #6 -- ChannelfacadeNew.updateResultStatusUnblock()  :: " + e1);

            }
            try {
                if (insStm != null) insStm.close();
            } catch (SQLException e1) {
                log.error("Error #7 --ChannelfacadeNew.updateResultStatusUnblock():: " + e1);

            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateUnBlockDate(int batchpk,String blockNo) throws NotFoundException, SQLException {

        Connection connection = null;
        Statement updateST = null;
        connection = dbConnectionPool.getConnection();
        try {
            String updateSQL = "update tbkashef set UNBLOCK_DATE='" + DateUtil.getSystemDate() + "',NATIONALCODE='"+DateUtil.getSystemTime()
                    + "' where BATCHPK="+batchpk+" and BLOCK_NO='"+blockNo+ "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "'";

            updateST = connection.createStatement();
            int i=updateST.executeUpdate(updateSQL);
            if (i < 1) {
                log.error("Inside channelFacadeNew.updateUnBlockDate()!!Can not update unblockDate for batchpk::" + batchpk);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Error --ChannelFacadeNew.updateUnBlockDate()::" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateUnBlockDate(),  Can not rollback -- Error :: " + e1);
            }
            throw e;
        } finally {

            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error(" ChannelFacadeNew.updateUnBlockDate() = -- Error :: " + e1);
            }
            dbConnectionPool.returnConnection(connection);
        }
    }
    //********SiminRepeatedBlockUnBlock******End**********

    //*********Financial GroupBlock-UnBlock******Start****
    public static Collection findAllBlocksByPriority(int pickNum) throws SQLException {
        List col = new ArrayList();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "select BATCHPK,FILENAME,FILETYPE,FILEDATE,FILETIME,USERID,BLOCKDESC,PRIORITY,UNBLOCK_DATE  from TBKASHEFBATCH where SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "' and " +
                    "STATUS='" + Constants.BLOCK_SERVICE_STATUS + "'" +
                    "  order by PRIORITY desc,FILEDATE ASC,FILETIME ASC fetch first " + pickNum + " rows only with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Block block = new Block();
                block.setBatchPk(resultSet.getInt("BATCHPK"));
                block.setFileName(null != resultSet.getString("FILENAME") ? resultSet.getString("FILENAME").trim() : "");
                block.setFileType(null != resultSet.getString("FILETYPE") ? resultSet.getString("FILETYPE").trim() : "");
                block.setFileDate(resultSet.getString("FILEDATE").trim());
                block.setFileTime(resultSet.getString("FILETIME").trim());
                block.setUnblockDate(resultSet.getString("UNBLOCK_DATE").trim());
                block.setUserId(null != resultSet.getString("USERID") ? resultSet.getString("USERID").trim() : "");
                block.setBlockDesc(null != resultSet.getString("BLOCKDESC") ? resultSet.getString("BLOCKDESC") : "");
                block.setPriority(null != resultSet.getString("PRIORITY") ? resultSet.getString("PRIORITY").trim() : "");
                if (changeStatus(block.getBatchPk(), Constants.BLOCK_SERVICE_EDIT_STATUS) == true) {
                    col.add(block);
                }
            }
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            log.error(" Error--ChannelFacadeNew.findAllBlocksByPriority() = :: " + e);
            connection.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return col;
    }

    public static Collection findEachBlockRows(int batchPk, int pickNum) throws SQLException {
        List blockRows = new ArrayList();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement stm = null;
        ResultSet rst = null;
        int rowCounts = 0;
        try {
            String rowCountsSql = "select count(*) from tbkashef as rowCounts where BATCHPK=" + batchPk + " and PROCCESS_STATUS='" + Constants.BLOCK_PROCESS_STATUS + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "' with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(rowCountsSql);
            statement.close();
            if (resultSet.next()) {
                rowCounts = resultSet.getInt(1);
            }

                String sql = "select * from TBKASHEF where BATCHPK=" + batchPk + " and PROCCESS_STATUS='" + Constants.BLOCK_PROCESS_STATUS + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "'  fetch first " + pickNum + " rows only  with ur";
                for (int i = 0; i < rowCounts; i=i+pickNum) {
                    stm = connection.createStatement();
                    rst = stm.executeQuery(sql);

                    while (rst.next()) {
                        RowOfBlock rowOfBlock = new RowOfBlock();
                        rowOfBlock.setBatchPk(rst.getInt("BATCHPK"));
                        try {
                            rowOfBlock.setAccountNo(ISOUtil.padleft(rst.getString("ACCOUNT_NO").trim(), 13, '0'));
                        } catch (ISOException e) {
                            log.error("ChannelFacadeNew.findEachBlockRows()---Error #1::lenght is too long");
                        }
                        rowOfBlock.setAmount(rst.getString("AMOUNT").trim());
                        rowOfBlock.setHostId(rst.getString("HOSTID").trim());
                        rowOfBlock.setUserId(rst.getString("USER_ID").trim());
                        rowOfBlock.setBranchCode(null != rst.getString("BRANCH_CODE") ? rst.getString("BRANCH_CODE").trim() : "");
                        rowOfBlock.setUnblockDesc(null != rst.getString("UNBLOCK_DESC") ? rst.getString("UNBLOCK_DESC") : "");
                        rowOfBlock.setBlockDesc(null != rst.getString("BLOCK_DESC") ? rst.getString("BLOCK_DESC") : "");
                        try {
                            rowOfBlock.setBlockNo(ISOUtil.padleft(rst.getString("BLOCK_NO").trim(), 13, '0'));
                        } catch (ISOException e) {
                            log.error("ChannelFacadeNew.findEachBlockRows()---Error #2::lenght is too long");
                        }
                        if (changeProcessStatus(Constants.EDIT_BLOCK_PROCESS_STATUS, rowOfBlock.getBatchPk(), rowOfBlock.getBlockNo()) == true) {
                            blockRows.add(rowOfBlock);
                        }
                    }
                    stm.close();
                }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.findEachBlockRows() =   -- Error#3:: " + e);
            connection.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (rst != null) rst.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(connection);
        }
        return blockRows;
    }

    public static boolean changeStatus(int batchPk, String status) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        boolean isUpdated = true;

        try {
            String sql = "update tbkashefbatch set STATUS='" + status + "'"
                    + " where BATCHPK= " + batchPk;

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            int i = statement.executeUpdate(sql);
            statement.close();
            if (i < 1) {
                isUpdated = false;
                log.error("Inside channelFacadeNew.changeStatus()!!Can not update status for batchpk::" + batchPk);
            }
            connection.commit();
            return isUpdated;

        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.changeStatus() = :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.changeStatus(),  Can not rollback :: " + e1);
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void writeActionCode(String actionCode, String blockNo, int batchpk, String processStatus) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "update tbkashef set ACTIONCODE='" + actionCode + "'"
                    + "," + "PROCCESS_STATUS = '" + processStatus + "'"
                    + "," + "EFFECTIVE_DATE = '" + DateUtil.getSystemDate() + "',"
                    + "EFFECTIVE_TIME = '" + DateUtil.getSystemTime() + "'"
                    + " where BATCHPK="+batchpk+" and BLOCK_NO= '" + blockNo + "' and SERVICETYPE= '" + Constants.BLOCK_SERVICE_TYPE + "'";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            int i = statement.executeUpdate(sql);
            statement.close();

            if (i < 1) {
                log.error("Inside ChannelFacadeNew.writeActionCode() can not update for batchpk::" + batchpk + " and blockNo::" + blockNo);
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.writeActionCode()::" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.writeActionCode(),  Can not rollback :: " + e1);
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void updateFinUnblockRow(String blockNo, int batchpk,String actionCode,String processStatus) throws SQLException {

        Connection connection = null;
        Statement updateST = null;
        connection = dbConnectionPool.getConnection();
        try {
            String updateSQL = "update tbkashef set ACTIONCODE='" + actionCode + "'"
                    + "," + "PROCCESS_STATUS = '" + processStatus + "'"
                    + "," + "NATIONALCODE = '" + DateUtil.getSystemTime() + "'," //nationalCode as UnblockTime
                    + "UNBLOCK_DATE = '" + DateUtil.getSystemDate() + "'"
                    + " where BATCHPK="+batchpk+" and BLOCK_NO= '" + blockNo + "' and SERVICETYPE= '" + Constants.BLOCK_SERVICE_TYPE + "'";

            updateST = connection.createStatement();
            int i= updateST.executeUpdate(updateSQL);
            updateST.close();
            if (i < 1) {
                log.error("Inside channelFacadeNew.updateFinUnblockRow()!!Can not update status for batchpk::" + batchpk);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.updateFinUnblockRow()---Error#1" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.updateFinUnblockRow(),Can not rollback -- Error :: " + e1);
            }
            throw e;
        } finally {
            try {
                if (updateST != null) updateST.close();
            } catch (SQLException e1) {
                log.error("ChannelfacadeNew.updateFinUnblockRow()= -- Error #2  :: " + e1);
            }
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean changeProcessStatus(String status, int batchpk, String blockNo) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean isUpdated = true;
        try {
            String sql = "update tbkashef set PROCCESS_STATUS='" + status + "'"
                    + " where BATCHPK ="+ batchpk +" and BLOCK_NO= '" + blockNo + "' and SERVICETYPE= '" + Constants.BLOCK_SERVICE_TYPE + "'";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            int i = statement.executeUpdate(sql);
            statement.close();
            if (i < 1) {
                log.error("Inside ChannelFacadeNew.changeProcessStatus can not update for blockNo::" + blockNo + " and batchpk::" + batchpk);
                isUpdated = false;
            }
            connection.commit();
            return isUpdated;
        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.changeProcessStatus()::" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.changeProcessStatus(),  Can not rollback  :: " + e1);
            }
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static void writeErrorCount( int batchpk) throws SQLException {

            String selectSql = "select count(*) from tbkashef as errorCount where BATCHPK=" + batchpk + " and PROCCESS_STATUS='" + Constants.FAILED_BLOCK_PROCESS_STATUS + "' with ur";
            int errorCount = 0;
            Connection connection = null;
            Statement statement = null;
            Statement stm = null;
            ResultSet rs = null;

            try {
                connection = dbConnectionPool.getConnection();
                statement = connection.createStatement();
                rs = statement.executeQuery(selectSql);
                statement.close();
                if (rs.next()) {
                    errorCount = rs.getInt(1);
                }
                String sql = "update tbkashefbatch set RESULTSTATUS=" + errorCount +" where BATCHPK= " + batchpk;

                stm = connection.createStatement();
                int i = stm.executeUpdate(sql);
                stm.close();
                if (i < 1) {
                    log.error("Inside channelFacadeNew.writeErrorCount()!!Can not update status for batchpk::" + batchpk);
                }
                connection.commit();

            } catch (SQLException e) {
                log.error("Error--ChannelFacadeNew.writeErrorCount()" + e);
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error("ChannelFacadeNew.writeErrorCount(),  Can not rollback -- Error :: " + e1);
                }
                throw e;
            } finally {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (stm != null) stm.close();
                dbConnectionPool.returnConnection(connection);
            }
        }


    public static void insertBlockRow(Block blc) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbConnectionPool.getConnection();
            String sql = "insert into tbkashefbatch " +
                    "(FILENAME,FILETYPE,FILEDATE,FILETIME,SERVICETYPE,PRIORITY," +
                    "STATUS,RESULTSTATUS,ENDDATE,USERID,UNBLOCK_DATE,BLOCKDESC )" +
                    " values('" +
                    blc.getFileName() + "','" + blc.getFileType() + "','" + blc.getFileDate() + "','" + blc.getFileTime() + "','" + Constants.UNBLOCK_SERVICE_TYPE + "','" + blc.getPriority() + "','" +
                    Constants.BLOCK_SERVICE_FINISH_STATUS + "'," + 0 + ",'" + String.valueOf(blc.getBatchPk()) + "','" + blc.getUserId() + "','" + blc.getUnblockDate() + "','" + blc.getBlockDesc() + "')";

            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();

        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.insertBlockRow()::" + e);
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    public static Collection findAllUnBlocksByPriority(int pickNum) throws SQLException {
        List row = new ArrayList();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement stm = null;
        ResultSet rst = null;
        int rowCounts = 0;

        try {
            String rowCountsSql = "select count(*) from TBKASHEFBATCH as unblockCounts where SERVICETYPE='" + Constants.UNBLOCK_SERVICE_TYPE +
                    "'and UNBLOCK_DATE='" + DateUtil.getSystemDate() + "' and STATUS='" + Constants.BLOCK_SERVICE_FINISH_STATUS + "' with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(rowCountsSql);
            statement.close();
            if (resultSet.next()) {
                rowCounts = resultSet.getInt(1);
            }
            String sql = "select FILETYPE,FILEDATE,FILETIME,ENDDATE,USERID,UNBLOCK_DATE,BLOCKDESC from TBKASHEFBATCH where SERVICETYPE='" + Constants.UNBLOCK_SERVICE_TYPE + "'and UNBLOCK_DATE='" + DateUtil.getSystemDate() +
                    "' and STATUS='" + Constants.BLOCK_SERVICE_FINISH_STATUS + "' order by PRIORITY desc,FILEDATE ASC,FILETIME ASC  fetch first " +  pickNum + " rows only  with ur";
            for (int i = 0; i < rowCounts; i=i+pickNum) {

                stm = connection.createStatement();
                rst = stm.executeQuery(sql);
                while (rst.next()) {
                    Block block = new Block();
                    block.setBatchPk(rst.getInt("ENDDATE")); //batchpk 4unblock set in EndDate field
                    block.setFileType(null != rst.getString("FILETYPE") ? rst.getString("FILETYPE").trim() : "");
                    block.setFileDate(rst.getString("FILEDATE").trim());
                    block.setFileTime(rst.getString("FILETIME").trim());
                    block.setUserId(null != rst.getString("USERID") ? rst.getString("USERID").trim() : "");
                    block.setUnblockDate(null != rst.getString("UNBLOCK_DATE") ? rst.getString("UNBLOCK_DATE").trim() : "");
                    block.setBlockDesc(null != rst.getString("BLOCKDESC") ? rst.getString("BLOCKDESC") : "");
                    if (changeStatus(block.getBatchPk(), Constants.UNBLOCK_SERVICE_EDIT_STATUS) == true) {
                        row.add(block);
                    }
                }
                stm.close();
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.findAllUnBlocksByPriority()::" + e);
            if (connection != null) connection.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (rst != null) rst.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(connection);
        }

        return row;
    }

    public static Collection findEachUnBlockRows(int batchpk, int pickNum) throws SQLException {

        List unblockRow = new ArrayList();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement stm = null;
        ResultSet rst = null;
        int rowCounts = 0;

        try {
            String rowcountsSql = "select count(*) from tbkashef as rowCounts where BATCHPK=" + batchpk
                    + " and PROCCESS_STATUS='" + Constants.SUCCESS_BLOCK_PROCESS_STATUS + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "' with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(rowcountsSql);
            statement.close();
            if (resultSet.next()) {
                rowCounts = resultSet.getInt(1);
            }
            String sql = "select BATCHPK,ACCOUNT_NO,AMOUNT,HOSTID,BLOCK_NO,USER_ID,BRANCH_CODE,UNBLOCK_DESC,BLOCK_DESC from TBKASHEF where BATCHPK=" + batchpk + " and PROCCESS_STATUS='" + Constants.SUCCESS_BLOCK_PROCESS_STATUS
                    + "' and SERVICETYPE='" + Constants.BLOCK_SERVICE_TYPE + "' fetch first " + pickNum + " rows only  with ur";

            for (int i = 0; i < rowCounts; i = i + pickNum) {
                stm = connection.createStatement();
                rst = stm.executeQuery(sql);
                while (rst.next()) {
                    RowOfBlock rowOfBlock = new RowOfBlock();
                    rowOfBlock.setBatchPk(rst.getInt("BATCHPK"));
                    try {
                        rowOfBlock.setAccountNo(ISOUtil.padleft(rst.getString("ACCOUNT_NO").trim(), 13, '0'));
                    } catch (ISOException e) {
                        log.error("Error #1--ChannelFacadeNew.findEachUnBlockRows()::lenght is too long");
                    }
                    rowOfBlock.setAmount(rst.getString("AMOUNT").trim());
                    rowOfBlock.setHostId(rst.getString("HOSTID").trim());
                    rowOfBlock.setUserId(rst.getString("USER_ID").trim());
                    rowOfBlock.setBranchCode(null != rst.getString("BRANCH_CODE") ? rst.getString("BRANCH_CODE").trim() : "");
                    try {
                        rowOfBlock.setBlockNo(ISOUtil.padleft(rst.getString("BLOCK_NO").trim(), 13, '0'));
                    } catch (ISOException e) {
                        log.error("Error #2--ChannelFacadeNew.findEachUnBlockRows()::lenght is too long");
                    }
                    rowOfBlock.setUnblockDesc(null != rst.getString("UNBLOCK_DESC") ? rst.getString("UNBLOCK_DESC") : "");
                    rowOfBlock.setBlockDesc(null != rst.getString("BLOCK_DESC") ? rst.getString("BLOCK_DESC") : "");
                    if (changeProcessStatus(Constants.EDIT_UNBLOCK_PROCESS_STATUS, batchpk, rowOfBlock.getBlockNo()) == true) {
                        unblockRow.add(rowOfBlock);
                    }
                }
                stm.close();
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("Error --ChannelFacadeNew.findEachUnBlockRows()::" + e);
            connection.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (rst != null) rst.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(connection);
        }
        return unblockRow;
    }

    public static void unblockErrorUpdate(int batchpk) throws NotFoundException, ServerAuthenticationException, SQLException {

        String selectSql = "select count(*) from tbkashef as errorCount where BATCHPK=" + batchpk + " and PROCCESS_STATUS='" + Constants.FAILED_UNBLOCK_PROCESS_STATUS + "' with ur";
        int errorCount = 0;
        Connection connection = null;
        Statement statement = null;
        Statement stm = null;
        ResultSet rs = null;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(selectSql);
            statement.close();
            if (rs.next()) {
                errorCount = rs.getInt(1);
            }
            String sql = "update tbkashefbatch set RESULTSTATUS=" + errorCount + " where SERVICETYPE= '" + Constants.UNBLOCK_SERVICE_TYPE + "' and ENDDATE ='" + String.valueOf(batchpk) + "'";
            stm = connection.createStatement();
            int i = stm.executeUpdate(sql);
            stm.close();
            if (i < 1) {
                log.error("Inside channelFacadeNew.unblockErrorCount()!!Can not update status for batchpk::" + batchpk);
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("Error--ChannelFacadeNew.unblockErrorCount()" + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.unblockErrorCount(),  Can not rollback -- Error :: " + e1);
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            if (stm != null) stm.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean changeStatusUnblock(int batchPk, String status) throws SQLException {

        Connection connection = null;
        Statement statementUpdateBlockRow = null;
        Statement statementUpdateUnBlockRow = null;
        boolean isUpdated = true;

        try {
            String sqlBlock = "update tbkashefbatch set STATUS='" + status + "'"
                    + " where BATCHPK= " + batchPk;

            connection = dbConnectionPool.getConnection();
            statementUpdateBlockRow = connection.createStatement();
            int i = statementUpdateBlockRow.executeUpdate(sqlBlock);
            statementUpdateBlockRow.close();
            if (i < 1) {
                isUpdated = false;
                log.error("Inside channelFacadeNew.changeStatusUnblock()!!Can not update status for batchpk::" + batchPk);
            }
            String sqlUnblock = "update tbkashefbatch set STATUS='" + Constants.UNBLOCK_ACCOUNT_STATUS + "'"
                    + " where ENDDATE= '" + batchPk + "'";
            statementUpdateUnBlockRow = connection.createStatement();
            int j = statementUpdateUnBlockRow.executeUpdate(sqlUnblock);
            statementUpdateUnBlockRow.close();

            if (j < 1) {
                isUpdated = false;
                log.error("Inside channelFacadeNew.changeStatusUnblock()!!Can not update status for batchpk::" + batchPk);
            }
            connection.commit();
            return isUpdated;

        } catch (SQLException e) {
            log.error("Error --ChannelFacadeNew.changeStatusUnblock() = :: " + e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("ChannelFacadeNew.changeStatusUnblock(),  Can not rollback :: " + e1);
            }
            throw e;
        } finally {
            if (statementUpdateBlockRow != null) statementUpdateBlockRow.close();
            if (statementUpdateUnBlockRow != null) statementUpdateUnBlockRow.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    public static boolean isExistBlockNo(String blockNo,String accountNo) throws SQLException, NotFoundException {
        boolean isExist = false;
        String selectSql;
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        String account_No = "";
        try {
            connection = dbConnectionPool.getConnection();
            selectSql = "select account_no from tbkashef where Block_no = '" + blockNo + "' with ur";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSql);

            if (resultSet.next()) {
                try {
                    account_No = ISOUtil.zeropad(resultSet.getString(1).trim(), 13);
                } catch (ISOException e) {
                    log.error("Can not zeropad accountNo = '" + account_No + "' in isExistBlockNo() : " + e.getMessage());
                }
                if (accountNo.equals(account_No)) {
                    isExist = true;
                } else {
                    connection.rollback();
                    throw new NotFoundException("accountNo::" + accountNo + "is not for BlockNo::" + blockNo);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("ChannelFacadeNew.isExistBlockNo()  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return isExist;
    }

    //**********Financial Group BlockUnBlock**********end**********

    //**********ATM**********start********
    public static void  createDeviceATM(String accountNo,String deviceCode,String branchCode,String sessionId,String channelType,String pin,
                                     String userId,String deviceType,String SN10Url,String SN10Pass
            ,String SN10User,String driver,String lockStatus,boolean hasDevice,String requestAccType,String existDeviceStatus) throws SQLException, ModelException {


        ResultSet resultSet = null;
        ResultSet seqRs = null;
        Statement statement = null;
        Statement seqSt = null;
        Statement updateStatement = null;
        Statement nacLogStatment = null;
        Statement statementUpdateStatusDevice = null;
        Connection connection = null;
        Connection connectionSN10 = null;
        String customerId="";
        String nationalCode="0000000000";
        String accountTitle = "8";
        String subTitle = "0";
        String firstName = "";
        String lastName = "";
        String accountGroup=Constants.ATM_ACCOUNT_GROUP;
        String deviceStatus="1";

        try {
            connectionSN10=  createConnection(driver,SN10Url, SN10User,SN10Pass);
            connection = dbConnectionPool.getConnection();


            byte[] firstNameByte = {67, 84, 35, 85, 75, 35, 65, 124, 78, 60, 86, 87, 60, 89};
            try {
                String s = new String(firstNameByte, "ISO-8859-1");
                firstName = FarsiUtil.convertWindows1256(firstNameByte);
            } catch (UnsupportedEncodingException e) {
                firstName = "";
            }
            byte[] lastNameByte = {63, 85, 35, 43, 39, 35, 60, 61};
            try {
                String s = new String(firstNameByte, "ISO-8859-1");
                lastName = FarsiUtil.convertWindows1256(lastNameByte);
            } catch (UnsupportedEncodingException e) {
                lastName = "";
            }
             if (requestAccType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)) {
                deviceStatus = "0";
            }

            if(hasDevice==false)
            {
                String insertSql = "insert into TBDEVICE(" +
                        "IIN," +
                        "BRANCH_ID," +
                        "ACCOUNT_NO," +
                        "DEVICE_CODE," +
                        "DEVICE_TYPE," +
                        "ISMODIFIED," +
                        "CREATION_DATE," +
                        "CREATION_TIME," +
                        "ACCOUNT_BALANCE," +
                        "STATUS" +
                        ") values(" +
                        "'" + Constants.BANKE_TEJARAT_BIN_NEW + "'," +
                        "'" + branchCode+ "'," +
                        "'" + accountNo + "'," +
                        "'" + deviceCode + "',"+
                        deviceType+ "," +
                        "'" + Constants.ACTIVE_ISMODIFIED + "'," +
                        "'" +DateUtil.getSystemDate() + "'," +
                        "'" + DateUtil.getSystemTime()  + "'," +
                        0+"," +
                        "'" + deviceStatus+ "'" +
                        ")";
                statement = connection.createStatement();
                statement.execute(insertSql);
            }
            else{
                String sqlUpdateStatus="update tbdevice set STATUS ='"+deviceStatus+"' where DEVICE_CODE='"+deviceCode+"'";
                statementUpdateStatusDevice = connection.createStatement();
                statementUpdateStatusDevice.executeUpdate(sqlUpdateStatus);
            }

            if(lockStatus.equals("9")){
                if (requestAccType.equals(Constants.DEVICE_ONLINE_ACC_REQUEST)){
                    String updateSql="update tbcustomeraccounts set lock_status=1 , balance=0 ,ACCOUNT_TYPE='"+accountGroup+"',SGB_BRANCH_ID='"+branchCode+"' where ACCOUNT_NO='" + accountNo + "'";
                    updateStatement = connection.createStatement();
                    updateStatement.executeUpdate(updateSql);

                    String customersrvId = existCustomerAccountInSRV(accountNo);

                    if (customersrvId != null) {
                        updateDeviceCustomerAccountInSRVTransactional(connection, accountNo, "0000000000", Constants.HOST_CFS,accountGroup);

                    } else {

                        String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

                        seqSt = connection.createStatement();
                        seqRs = seqSt.executeQuery(maxCustomer_id_sql);
                        seqRs.next();
                        String maxID = seqRs.getString(1).trim();
                        if (maxID != null)
                            customerId = maxID;
                        try {
                            customerId = ISOUtil.zeropad(customerId, 10);
                        } catch (ISOException e) {
                            log.error(e);
                        }

                        insertCustomerAccountInSRVTransactional(connection, accountNo, Constants.HOST_CFS,
                                accountGroup, "0000000000", Constants.NATIONAL_STATUS, customerId);
                    }
                    String customerIdCustacc=existAccountInCustacc(accountNo);
                    if(customerIdCustacc!=null)
                    {
                        updateCustomerTransactional(connection,customerIdCustacc,firstName,lastName);
                    }
                    String insertNaclog_sql = "insert into TB_NAC_LOG(" +
                            "SESSION_ID," +
                            "CHANNEL_TYPE," +
                            "PARTNO," +
                            "INSERT_DATETIME," +
                            "ACCOUNT_NO," +
                            "NAME_FA," +
                            "SURNAME_FA," +
                            "BIRTHDATE," +
                            "NATIONAL_CODE," +
                            "OPERATION_TYPE," +
                            "ACCOUNT_TITLE," +
                            "SUB_TITLE," +
                            "HANDLED," +
                            "OPEN_DATE," +
                            "OPEN_TIME," +
                            "BRANCH_ID," +
                            "ACCOUNT_GROUP" +
                            ") values(" +
                            "'" + sessionId + "'," +
                            "'" + channelType + "'," +
                            getPartNo() + "," +
                            "current_timestamp, " +
                            "'" + accountNo + "'," +
                            "'" + firstName + "'," +
                            "'" + lastName + "'," +
                            "'00000000'," +
                            "'" + nationalCode + "'," +
                            "'" + Constants.CREATE_NAC_STATUS + "'," +
                            "'" + accountTitle + "'," +
                            "'" + subTitle + "'," +
                            1 + "," +
                            "'" + DateUtil.getSystemDate() + "'," +
                            "'" + DateUtil.getSystemTime() + "'," +
                            "'" + branchCode + "'," +
                            "'" + accountGroup + "'" +
                            " )";
                    nacLogStatment = connection.createStatement();
                    nacLogStatment.execute(insertNaclog_sql);
                    nacLogStatment.close();
                    insertDeviceInDeviceHistory(connection, accountNo, deviceCode, branchCode, pin, userId, sessionId, 0, DateUtil.getSystemDate(), DateUtil.getSystemTime(), existDeviceStatus, deviceType,"");
                    sendToBranchManager(connectionSN10,accountNo,branchCode,accountGroup,deviceType);
                }
                else if (requestAccType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)){


                    String customersrvId = existCustomerAccountInSRV(accountNo);

                    if (customersrvId != null) {
                        updateDeviceCustomerAccountInSRVTransactional(connection, accountNo, "0000000000", Integer.parseInt(Constants.SGB_HOSTID),accountGroup);

                    } else {

                        String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

                        seqSt = connection.createStatement();
                        seqRs = seqSt.executeQuery(maxCustomer_id_sql);
                        seqRs.next();
                        String maxID = seqRs.getString(1).trim();
                        if (maxID != null)
                            customerId = maxID;
                        try {
                            customerId = ISOUtil.zeropad(customerId, 10);
                        } catch (ISOException e) {
                            log.error(e);
                        }

                        insertCustomerAccountInSRVTransactional(connection, accountNo, Integer.parseInt(Constants.SGB_HOSTID),
                                accountGroup, "0000000000", Constants.NATIONAL_STATUS, customerId);
                    }
                    String customerIdCustacc=existAccountInCustacc(accountNo);
                    if(customerIdCustacc!=null)
                    {
                        updateCustomerTransactional(connection,customerIdCustacc,firstName,lastName);
                    }

                    insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchCode,pin,userId,sessionId,0,DateUtil.getSystemDate(),DateUtil.getSystemTime(),existDeviceStatus,deviceType,"");

                }


            }

            else if(lockStatus.equals(Constants.NO_LOCKSTATUS_FLAG)){
                if(requestAccType.equals(Constants.DEVICE_ONLINE_ACC_REQUEST)){
                    createNAC_ATM(connection,accountNo,branchCode,sessionId,channelType,firstName,lastName,accountGroup);
                    insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchCode,pin,userId,sessionId,0,DateUtil.getSystemDate(),DateUtil.getSystemTime(),existDeviceStatus,deviceType,"");
                    sendToBranchManager(connectionSN10,accountNo,branchCode,accountGroup,deviceType);
                } else if (requestAccType.equals(Constants.DEVICE_OFFLINE_ACC_REQUEST)){


                    String customersrvId = existCustomerAccountInSRV(accountNo);

                    if (customersrvId != null) {
                        updateDeviceCustomerAccountInSRVTransactional(connection, accountNo, "0000000000", Integer.parseInt(Constants.SGB_HOSTID),accountGroup);

                    } else {

                        String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

                        seqSt = connection.createStatement();
                        seqRs = seqSt.executeQuery(maxCustomer_id_sql);
                        seqRs.next();
                        String maxID = seqRs.getString(1).trim();
                        if (maxID != null)
                            customerId = maxID;
                        try {
                            customerId = ISOUtil.zeropad(customerId, 10);
                        } catch (ISOException e) {
                            log.error(e);
                        }

                        insertCustomerAccountInSRVTransactional(connection, accountNo, Integer.parseInt(Constants.SGB_HOSTID),
                                accountGroup, "0000000000", Constants.NATIONAL_STATUS, customerId);
                    }
                    String customerIdCustacc=existAccountInCustacc(accountNo);
                    if(customerIdCustacc!=null)
                    {
                        updateCustomerTransactional(connection,customerIdCustacc,firstName,lastName);
                    }

                    insertDeviceInDeviceHistory(connection,accountNo,deviceCode,branchCode,pin,userId,sessionId,0,DateUtil.getSystemDate(),DateUtil.getSystemTime(),existDeviceStatus,deviceType,"");

                }

            }

            connection.commit();
            connectionSN10.commit();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.createDevice  -- Error#1 :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            throw e;
        } catch (ModelException e){
            log.error("ChannelFacadeNew.createDevice  -- Error#2 :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
            throw e;
        } catch (ISOException e) {
            log.error("ChannelFacadeNew.createDevice  -- Error#3 :: " + e);
            connection.rollback();
            if (connectionSN10 != null) connectionSN10.rollback();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (seqRs != null) seqRs.close();
            if (seqSt != null) seqSt.close();
            if (updateStatement != null) updateStatement.close();
            if (statementUpdateStatusDevice != null) statementUpdateStatusDevice.close();
            dbConnectionPool.returnConnection(connection);
            if (connectionSN10 != null)connectionSN10.close();
        }
    }

    public static void changeDeviceBranchCode(String newBranchCode, String deviceCode, String accountNo, String branchCode,
                                              String deviceType, String pin, String userId, String sessionId, long accountBalance
            , String deviceCreationDate, String deviceCreationTime, String deviceStatus) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = dbConnectionPool.getConnection();
            String updateBranchCode="update tbdevice  set BRANCH_ID='"+newBranchCode+"' where DEVICE_CODE='" + deviceCode
                  +"' and BRANCH_ID='"+branchCode+"' and DEVICE_TYPE="+deviceType;
            statement = connection.createStatement();
            int i=statement.executeUpdate(updateBranchCode);
            if(i>0)
            {
            insertDeviceInDeviceHistory(connection,accountNo, deviceCode, newBranchCode, pin, userId, sessionId, accountBalance, deviceCreationDate, deviceCreationTime, deviceStatus, deviceType,branchCode);
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.changeDeviceBranchCode()  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }

    public static String[] findDeviceInDeviceHistory(String pin,String deviceCode,String accountNo,String branchCode,String deviceType) throws SQLException,NotFoundException {
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        String [] deviceFields=new String[5];
        boolean isExist=false;
        try {
            connection = dbConnectionPool.getConnection();

             String findDevice="select * from tbdevicehistory where DEVICE_CODE='"+deviceCode+"' and ACCOUNT_NO='"+accountNo+"' and DEVICE_TYPE ="+deviceType+
                     " order by INSERT_DATE desc, INSERT_TIME desc  fetch first 1 rows only with ur ";



           statement = connection.createStatement();
            resultSet = statement.executeQuery(findDevice);

            if (resultSet.next()) {
                deviceFields[0]=resultSet.getString("ACCOUNT_NO").trim();
                deviceFields[1]=resultSet.getString("STATUS").trim();
                deviceFields[2]= String.valueOf(resultSet.getLong("ACCOUNT_BALANCE"));
                deviceFields[3]=resultSet.getString("BRANCH_ID").trim();
                deviceFields[4]=resultSet.getString("DEVICE_TYPE").trim();

            }
            else{
                throw new NotFoundException("Can not find device = " + deviceCode + " in tbdeviceHistory.");
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.changeDeviceBranchCode()  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
      return deviceFields;
    }
    public static void activeDevice(String pin,String branchCode,String accountNo,String deviceCode,String deviceType,long balance,String status,String sessionId,String userId) throws SQLException {
        Statement statement = null;
        Connection connection = null;

        try {
            connection = dbConnectionPool.getConnection();
            String insertDate=DateUtil.getSystemDate();
            String insertTime=DateUtil.getSystemTime();
            String insertSql = "insert into TBDEVICE(" +
                    "IIN," +
                    "BRANCH_ID," +
                    "ACCOUNT_NO," +
                    "DEVICE_CODE," +
                    "DEVICE_TYPE," +
                    "ISMODIFIED," +
                    "CREATION_DATE," +
                    "CREATION_TIME," +
                    "ACCOUNT_BALANCE," +
                    "STATUS" +
                    ") values(" +
                    "'" + Constants.BANKE_TEJARAT_BIN_NEW + "'," +
                    "'" + branchCode+ "'," +
                    "'" + accountNo + "'," +
                    "'" + deviceCode + "',"+
                    deviceType+ "," +
                    "'" + Constants.ACTIVE_ISMODIFIED + "'," +
                    "'" +insertDate + "'," +
                    "'" + insertTime  + "'," +
                    balance+"," +
                    "'" + status+ "'" +
                    ")";
            statement = connection.createStatement();
            statement.execute(insertSql);
            insertDeviceInDeviceHistory(connection,accountNo, deviceCode, branchCode, pin, userId, sessionId, balance, insertDate, insertTime, status, deviceType,"");
            connection.commit();

        } catch (SQLException e) {
            log.error("ChannelFacadeNew.changeDeviceBranchCode()  -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }

    //**********ATM**********end**********
    //**********GIFTCARD LIMIT**********START**********
    public static boolean checkGiftCardLimit(String identityCode,String date,long amount,long limitAmount) throws SQLException {

        boolean result=false;
        String hql = "select amount from TBGIFTCARDlimit where " +
                " IDENTITY_CODE = '" + identityCode + "' and TX_DATE='"+date+"' with ur";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(hql);

            if (resultSet.next()) {
                long oldAmount=resultSet.getLong("amount");
                if(oldAmount+amount>limitAmount)
                    result=true;
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("EEROR ::: SQLException ::: Inside ChannelFacadeNew.checkGiftCardLimit, error ::: " + e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return result;
    }
    //**********GIFTCARD LIMIT**********END**********

    //*********setServiceStatus********START**********

    public static void insertTbmsgtrn(String accountNo, String channelType) throws SQLException {

        Connection connection = null;
        Statement smsStatement = null;
        try {
            connection = dbConnectionPool.getConnection();

            String smsSql = "INSERT INTO TBMSGTRN (ID, TRANSACTION_TYPE, TRANSACTION_DATE," +
                    "TRANSACTION_TIME, ACCOUNT_NUMBER, AMOUNT, CHANNEL) " +
                    "VALUES (NEXT VALUE FOR TBMSGTRN_SEQ" +
                    ",'"+Constants.SMS_TRANSACTION_TYPE_SET_SERVICE_STATUS+"'"+
                    ",'"+DateUtil.getSystemDate()+"'"+
                    ",'"+DateUtil.getSystemTime()+"'"+
                    ",'"+accountNo+"'"+
                    ",0"+
                    ",'" + channelType + "'" +
                    ")";
            smsStatement = connection.createStatement();
            smsStatement.execute(smsSql);

            connection.commit();

        } catch (SQLException e) {
            log.error("CFSFacadeNew.insertTbmsgtrn() =   -- Error :: " + e);
            connection.rollback();
            throw e;
        } finally {
            if (smsStatement != null) smsStatement.close();
            dbConnectionPool.returnConnection(connection);
        }
    }
    //*********setServiceStatus********End**********
    //**********pinPad****************Start**********
    public static int changePinpadStatus(String accountNo,String deviceCode,String branchCode,long accountBalance,String sessionId,String channelType,
                                         String pin,String userId,String deviceCreationDate,String deviceCreationTime,
                                         String deviceStatus, String deviceType) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        int updateCount = 0;
        String sql = "update tbdevice set status = '" + Constants.OFFLINE_ATM_STATUS +
                "' where DEVICE_CODE = '" + deviceCode + "' and  ACCOUNT_NO = '" + accountNo + "' and BRANCH_ID = '" + branchCode + "'";
        try {

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            if (updateCount == 1) {
                insertDeviceInDeviceHistory(connection, accountNo, deviceCode, branchCode, pin, userId, sessionId, accountBalance, deviceCreationDate, deviceCreationTime, deviceStatus, Constants.PINPAD_DEVICE_TYPE, "");
            }
            connection.commit();

        } catch (SQLException e) {
            log.error("Error in ChannelFacadeNew--changePinpadStatus = " + e + " -- sql = " + sql);

            if (connection != null) connection.rollback();
            throw e;

        } finally {
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }
        return updateCount;

    }
    //**********pinPad****************End**********

    //*************Manzume*************start*******
    public static String[] getAccountInfoFromCif(String accountNo) throws SQLException, NotFoundException,ModelException {
        String[] cifAccountInfo = new String[7];
        Connection connection = null;
        Statement statementCifAcc = null;
        Statement statementCifCustInfo = null;
        ResultSet resultSetCifAcc = null;
        ResultSet resultSetCifCustInfo = null;
        String accountId="";
        String accountTitle="";
        String firstName="";
        String lastName="";
        String sejamName="";

        try {
            connection = dbConnectionPool.getConnection();

            String findCifAccount = "select ACCOUNT_ID,ACCOUNT_TITLE from cifapp.VW_GETACCINFO where account_number='" + accountNo + "'with ur";
            statementCifAcc = connection.createStatement();
            resultSetCifAcc = statementCifAcc.executeQuery(findCifAccount);
            if (resultSetCifAcc.next()) {
                accountId= resultSetCifAcc.getString("ACCOUNT_ID").trim();
                accountTitle=resultSetCifAcc.getString("ACCOUNT_TITLE").trim();

                String findCustInfo = "select * from cifapp.VW_GETDPICUSTINFO where ACCOUNT_ID=" + accountId + " and IS_PRIMARY ='1' with ur";
                statementCifCustInfo = connection.createStatement();
                resultSetCifCustInfo = statementCifCustInfo.executeQuery(findCustInfo);
                if (resultSetCifCustInfo.next()) {

                    String personType = null !=(resultSetCifCustInfo.getString("PERSON_TYPE"))? resultSetCifCustInfo.getString("PERSON_TYPE").trim() :"";
                    sejamName= null !=(resultSetCifCustInfo.getString("SEJAM_NAME"))? resultSetCifCustInfo.getString("SEJAM_NAME").trim() :"";
                    firstName = null !=(resultSetCifCustInfo.getString("FIRST_NAME"))? resultSetCifCustInfo.getString("FIRST_NAME").trim() :"";
                    lastName = null !=(resultSetCifCustInfo.getString("LAST_NAME"))? resultSetCifCustInfo.getString("LAST_NAME").trim() :"";

                    if (personType.equals("2")) {
                        if(sejamName !="" && sejamName!=null)
                        {
                            cifAccountInfo[0]=sejamName;
                        }
                        else{
                            cifAccountInfo[0]=firstName+" "+lastName;
                        }
                    }
                    else{
                        cifAccountInfo[0]=firstName+" "+lastName;
                    }

                    cifAccountInfo[1]= null !=(resultSetCifCustInfo.getString("CUSTOMER_NUMBER"))? resultSetCifCustInfo.getString("CUSTOMER_NUMBER").trim() :"";
                    cifAccountInfo[2]= null !=(resultSetCifCustInfo.getString("NATIONAL_ID_NUMBER"))? resultSetCifCustInfo.getString("NATIONAL_ID_NUMBER").trim() :"";
                    cifAccountInfo[3]= null !=(resultSetCifCustInfo.getString("FOREIGN_NATIONALS_CODE"))? resultSetCifCustInfo.getString("FOREIGN_NATIONALS_CODE").trim() :"";
                    cifAccountInfo[4]= null !=(resultSetCifCustInfo.getString("CELL_PHONE"))? resultSetCifCustInfo.getString("CELL_PHONE").trim() :"";
                    cifAccountInfo[5]= null !=(resultSetCifCustInfo.getString("POSTAL_CODE"))? resultSetCifCustInfo.getString("POSTAL_CODE").trim() :"";
                    cifAccountInfo[6]= null !=(resultSetCifCustInfo.getString("HOME_ADDRESS"))? resultSetCifCustInfo.getString("HOME_ADDRESS").trim() :"";
                } else {
                    cifAccountInfo[0]=accountTitle;
                }
            } else {
                connection.rollback();
                throw new NotFoundException("Can not find accountNo in cifapp.VW_GETACCINFO::" + accountNo);
            }
            connection.commit();
            return cifAccountInfo;

        } catch (SQLException e) {
            log.error(e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            if (statementCifAcc != null) statementCifAcc.close();
            if (resultSetCifAcc != null) resultSetCifAcc.close();

            if (statementCifCustInfo != null) statementCifCustInfo.close();
            if (resultSetCifCustInfo != null) resultSetCifCustInfo.close();

            dbConnectionPool.returnConnection(connection);
        }
    }

    public static boolean getBranchCode(String modifier, String description) throws SQLException {

        if (manzumeBranchCodeMap.containsKey("" + modifier + description))
            return true;

        String sql;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Boolean isExist = false;
        try {

            sql = "select ID  from TBMNZPARAM where MODIFIER = '" + modifier + "' and DESCRIPTION='" + description + "'  with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String id = resultSet.getString("ID");
                manzumeBranchCodeMap.put("" + modifier + description, id);
                isExist = true;

            } else {
                isExist = false;
            }
            return isExist;
        } catch (SQLException e) {
            log.error("Exp #1 in  ChannelFacad.getBranchCode()::" + e);
            connection.rollback();
            throw e;
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionPool.returnConnection(connection);
        }

    }


    public static Map getVirtualAcc() throws SQLException {
        if (virtualAccMap == null || virtualAccMap.size() == 0)
            virtualAccMap = getParameterMap(Constants.VIRTUAL_ACCOUNT);
        return virtualAccMap;
    }

    private static Map getParameterMap(String modifier) throws SQLException {
        String sql = "select ID, DESCRIPTION FROM TBMNZPARAM where MODIFIER = '" + modifier + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Map accounts = new HashMap();
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String virtualAcc = resultSet.getString("ID").trim();
                String account = resultSet.getString("DESCRIPTION").trim();
               // accounts.put(virtualAcc, account);
                accounts.put(account, virtualAcc);
            }
            connection.commit();
        } catch (SQLException e) {

            log.error("Exp #1 inside ChannelFacadeNew.getParameterMap()::"+e);
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
        return accounts;
    }


    public static String checkBranchCodeFromCif(String accountNo) throws NotFoundException, SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String branchCode="";
        try {
            String sql = "select BRANCH_CODE from cifapp.tb_cif_account  where ACCOUNT_NUMBER ='"+accountNo+"' with ur";

            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
             branchCode=resultSet.getString("BRANCH_CODE").trim();
            }
            else{
                throw new NotFoundException("Inside ChannelFacade.checkBranchCodeFromCif(),Account Not Found in tb_cif_account, AccountNo=" + accountNo);
            }
             return  branchCode;
        } catch (SQLException e) {
            log.error("Exp #1 in ChannelFacade.checkBranchCodeFromCif()::"+e);
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


    public static boolean getLongTermGroup (String modifier,String ID) throws SQLException {

        if (manzumeLongTermAccGroupMap.containsKey("" + modifier + ID))
        return true;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {

            String sql = "select DESCRIPTION  from TBMNZPARAM where MODIFIER = '" + modifier + "' and ID=" + ID + "  with ur";
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            if (resultSet.next()) {
                String desc = resultSet.getString("DESCRIPTION");
                manzumeLongTermAccGroupMap.put("" + modifier + ID, desc);
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            log.error("Exp #1 in ChannelFacade.getLongTermGroup()::" + e);
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

    public static HashMap getAlgorithmDetails(String accountNo) throws SQLException {
        String sql = "SELECT ALGORITHM_NO, CHNIDCHECK FROM " + Constants.TB_CHECK_DIGITS_ALG + " WHERE account_no= '" + accountNo + "' WITH UR";
        ResultSet rs = executeQuery(sql);
        HashMap algDetails = null;
        String algNo = "";
        String chnIdChk = "";
        if (rs.next()) {
            algDetails = new HashMap();
            algNo = rs.getString("ALGORITHM_NO");
            chnIdChk = rs.getString("CHNIDCHECK");
            algDetails.put("algNo", algNo);
            algDetails.put("chnIdChk", chnIdChk);
        }
        return algDetails;

    }



    public static List<String> getIranInsuranceAccountList() throws SQLException {
        if (iranInsuranceAccountList == null || iranInsuranceAccountList.size() == 0)
            iranInsuranceAccountList = getParameterList(Constants.IRAN_INSURANCE_ACCOUNT);
        return iranInsuranceAccountList;
    }


    private static List<String> getParameterList(String modifier) throws SQLException {
        String sql = "select DESCRIPTION FROM TBCOMMONPARAMS where MODIFIER = '" + modifier + "' with ur";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> list = new ArrayList<String>();
        try {
            connection = dbConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String account = resultSet.getString("DESCRIPTION").trim();
                list.add(account);
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
        return list;
    }

    //*************Manzume*************end*******
}







