package dpi.atlas.model.tj.facade;

import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;

import java.sql.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * **********************************************************
 * <p/>
 * This common is supposed to be used by all parties including CM and  core(s)
 * especially when the engaged parties are installed on different hosts, physically seperetated, and there is a need to using shared memory.
 * <p/>
 * ************************************************************
 */

public class CommonFacade {
    private static Log log = LogFactory.getLog(CommonFacade.class);

//    static Map dbPoolMap = new HashMap();
//    static Map dbPoolMap = new HashMap();

//    Map batchMap = new HashMap();
    static Map batchMap = new HashMap();

//    private static CommonFacade commonFacade = null;

//    public synchronized static CommonFacade getInstance() {
//        if (commonFacade  == null) {
//            commonFacade = new CommonFacade();
//            log.debug("New CommonFacade was created");
//        }
//        return commonFacade;
//    }


//        public static Batch getBatch(Connection connection, Long batch_status) throws SQLException, NotFoundException {
    public static Batch getBatch(Connection connection, Long batch_status, String operator) throws SQLException, NotFoundException {
        if (Constants.ACTIVE_BATCH == batch_status.intValue())
            if (batchMap.containsKey(batch_status.toString()))
                return (Batch) batchMap.get(batch_status.toString());

//      String sql = "select * from tbCFSBatch where batch_status = " + batch_status + " with ur";
        String sql = "select * from tbCFSBatch where batch_status = " + batch_status + " " + operator + " SGB_STATUS = " + batch_status + " " + operator + " FARA_STATUS= " + batch_status + " with ur";
        Batch batch;
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
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
                resultSet.close();
                statement.close();
                if (Constants.ACTIVE_BATCH == batch_status.intValue())
                    batchMap.put(batch_status.toString(), batch);
                return batch;
            }
            resultSet.close();
            statement.close();
            throw new NotFoundException();
        } catch (SQLException e) {
            if (statement != null)
                statement.close();
            throw e;
        }
    }


    public void setStaticBatch(Batch batch) {
        Long key = new Long(batch.getCfsStatus() + batch.getSgbStatus() + batch.getFaraStatus());
        if (batchMap.containsKey(key.toString())) {
            Batch pre_Batch = (Batch) batchMap.remove(key.toString());
            log.error("pre_Batch = " + pre_Batch);
        }
        log.error("batch = " + batch);
        batchMap.put(key.toString(), batch);
    }


    public Batch getBatchPk(Connection connection, Integer batch_status) throws SQLException, NotFoundException {

        if (Constants.ACTIVE_BATCH == batch_status.intValue())
//      if (batchMap.containsKey(hostId + "-" + service_type + "-" + batch_status.toString()))
            if (batchMap.containsKey(batch_status.toString()))
//        return (CMBatch) batchMap.get(hostId + "" + service_type + "-" + batch_status.toString());
                return (Batch) batchMap.get(batch_status.toString());

//    String sql = "select * from tbCFSBatch where host_id =" + hostId + " and SERVICE_TYPE ='" + service_type +
        String sql = "select * from tbCFSBatch where batch_status = " + batch_status + "  and SGB_STATUS = " + batch_status + " and  FARA_STATUS= " + batch_status + " with ur";

        Batch batch = null;
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                Timestamp closeTime = resultSet.getTimestamp("BATCH_CLOSE_DATE");
                java.util.Date batchCloseDate = null;
                if (closeTime != null)
                    batchCloseDate = new java.util.Date(closeTime.getTime());
                java.util.Date batchOpenDate = new java.util.Date(resultSet.getTimestamp("BATCH_OPEN_DATE").getTime());

//        batch = new CMBatch(resultSet.getInt("BATCH_TYPE"), batchOpenDate,
//                resultSet.getInt("BATCH_STATUS"), batchCloseDate, resultSet.getString("SERVICE_TYPE"), resultSet.getInt("HOST_ID"));
                batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS"));

                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
//        batch.setHostId(resultSet.getInt("HOST_ID"));
//        batch.setServiceType(resultSet.getString("SERVICE_TYPE"));

                resultSet.close();
                statement.close();

                if (CFSConstants.ACTIVE_BATCH == batch_status.intValue())
                    batchMap.put(batch_status.toString(), batch);
//          batchMap.put(hostId + "-" + service_type + "-" + batch_status.toString(), batch);

                return batch;
            }


            resultSet.close();
            statement.close();
            throw new NotFoundException();
        } catch (SQLException e) {

            if (statement != null)
                statement.close();
            throw e;
        }

    }

    private void updateBatch(Connection connection, Batch pre_batch) throws SQLException {
//    String sql = "update tbCFSBatch set BATCH_STATUS = ?, BATCH_CLOSE_DATE = ? where CURRENT_BATCH = ? and HOST_ID = ? and SERVICE_TYPE = ?";
        String sql = "update tbCFSBatch set BATCH_STATUS = ?, SGB_STATUS = ?, FARA_STATUS = ?, BATCH_CLOSE_DATE = ? where BATCH_PK = ? ";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
//      statement.setInt(1, pre_batch.getBatchStatus());
            statement.setInt(1, pre_batch.getCfsStatus());
            statement.setInt(2, pre_batch.getSgbStatus());
            statement.setInt(3, pre_batch.getFaraStatus());
            statement.setTimestamp(4, new Timestamp(pre_batch.getBatchCloseDate().getTime()));
            statement.setLong(5, pre_batch.getBatchPk().longValue());

            statement.execute();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e1) {
            }
            throw e;
        }
    }

    public Batch txnCutOverJob(Connection connection) throws Exception {
//    CMBatch batch = new CMBatch();
        Batch batch = new Batch();
//    CMBatch preBatch = null;
        Batch preBatch = null;

        try {
//            preBatch = getBatch(connection, new Long(CFSConstants.ACTIVE_BATCH));
            preBatch = getBatchPk(connection, new Integer(Constants.ACTIVE_BATCH));
        } catch (NotFoundException e) {
        }

        if (preBatch != null) {
            preBatch.setCfsStatus(CFSConstants.CUT_OVER_BATCH);
            preBatch.setSgbStatus(CFSConstants.CUT_OVER_BATCH);
            preBatch.setFaraStatus(CFSConstants.CUT_OVER_BATCH);

            preBatch.setBatchCloseDate(new java.sql.Timestamp(System.currentTimeMillis()));
            batch.setBatchPk(new Long(preBatch.getBatchPk().longValue() + 1));
            updateBatch(connection, preBatch);
        } else {
            try {
                preBatch = getLastClosedBatch(connection);
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
//        batch.setHostId(Integer.valueOf(hostId).intValue());
//        batch.setServiceType(service_type);

        putBatch(connection, batch);

        return batch;
    }

    private Batch getLastClosedBatch(Connection connection) throws NotFoundException, SQLException {

//    String sql = "select * from tbCFSBatch where HOST_ID = ? and SERVICE_TYPE = ? order by batch_pk desc with ur";
        String sql = "select * from tbCFSBatch order by batch_pk desc with ur";
//        CMBatch cmBatch;
        Batch batch;
//        Statement statement = connection.createStatement();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
//      int intHostId = Integer.valueOf(hostId).intValue();
//      statement.setInt(1, intHostId);
//      statement.setString(2, service_type);
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                Timestamp closeTime = resultSet.getTimestamp("BATCH_CLOSE_DATE");
                java.util.Date batchCloseDate = null;
                if (closeTime != null)
                    batchCloseDate = new java.util.Date(closeTime.getTime());
                java.util.Date batchOpenDate = new java.util.Date(resultSet.getTimestamp("BATCH_OPEN_DATE").getTime());
//                cmBatch = new CMBatch(resultSet.getInt("BATCH_TYPE"), batchOpenDate,
                batch = new Batch(resultSet.getInt("BATCH_TYPE"),
                        batchOpenDate,
                        batchCloseDate,
                        resultSet.getInt("BATCH_STATUS"),
                        resultSet.getInt("SGB_STATUS"),
                        resultSet.getInt("FARA_STATUS")
                );
//                        resultSet.getInt("BATCH_STATUS"), batchCloseDate, service_type, intHostId);
                batch.setBatchPk(new Long(resultSet.getLong("BATCH_PK")));
//                cmBatch.setBatchPk(new Long(resultSet.getLong("CURRENT_BATCH")));
                resultSet.close();
                statement.close();
//                return cmBatch;
                return batch;
            }
            resultSet.close();
            statement.close();
            throw new NotFoundException();
        } catch (SQLException e) {
            if (statement != null)
                statement.close();
            throw e;
        }
    }


    private void putBatch(Connection connection, Batch batch) throws SQLException {

//        if (Constants.ACTIVE_BATCH == cmBatch.getBatchStatus())
        if ((Constants.ACTIVE_BATCH == batch.getCfsStatus()) && (Constants.ACTIVE_BATCH == batch.getSgbStatus()) && (Constants.ACTIVE_BATCH == batch.getFaraStatus()))
            setStaticBatch(batch);
//            setStaticBatch(cmBatch);

//        if (Constants.ACTIVE_BATCH == batch_status.intValue())
//            if (batchMap.containsKey(hostId + "" + service_type + "-" + batch_status.toString()))
//                return (CMBatch) batchMap.get(hostId + "" + service_type + "-" + batch_status.toString());


        String sql = "insert into tbCFSBatch " +
                "(BATCH_PK,BATCH_TYPE,BATCH_OPEN_DATE,BATCH_STATUS,SGB_STATUS,FARA_STATUS) values(?,?,?,?,?,?)";
//                "(CURRENT_BATCH,BATCH_TYPE,BATCH_OPEN_DATE,BATCH_STATUS,HOST_ID,SERVICE_TYPE) values(?,?,?,?,?,?)";


        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, batch.getBatchPk().longValue());
            statement.setInt(2, batch.getBatchType());
            statement.setTimestamp(3, new Timestamp(batch.getBatchOpenDate().getTime()));
            statement.setInt(4, batch.getCfsStatus());
            statement.setInt(5, batch.getSgbStatus());
            statement.setInt(6, batch.getFaraStatus());
//            statement.setInt(5, batch.getHostId());
//            statement.setString(6, cmBatch.getServiceType());
            statement.execute();
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e1) {
            }
            throw e;
        }

    }
}
