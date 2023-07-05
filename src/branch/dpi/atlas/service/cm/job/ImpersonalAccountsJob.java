package branch.dpi.atlas.service.cm.job;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.quartz.BaseQuartzJob;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by R.Nasiri
 * Date: Feb 12, 2020
 * Time: 11:08 AM
 */
public class ImpersonalAccountsJob extends BaseQuartzJob {
    private static Log log = LogFactory.getLog(ImpersonalAccountsJob.class);
    private String driver = "";
    private String url = "";
    private String user = "";
    private String password = "";

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        driver = cfg.get("driver");
        url = cfg.get("url");
        user = cfg.get("user");
        password = cfg.get("password");
    }

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection finConnection = null;
        Connection cmConnection = null;
        Statement selectFinStm = null;
        ResultSet selectFinRs = null;
        Statement selectCmStm = null;
        ResultSet selectCmRs = null;
        Statement updateImpersonal = null;
        Statement insertHistory = null;
        Statement insertImpersonal = null;
        Statement insertSrv = null;
        Statement selectCustomerIdStm = null;
        ResultSet selectCustomerIdRs = null;
        Statement selectDeleteAccStm = null;
        ResultSet selectDeleteAccRs = null;
        Statement deleteImpersonal = null;
        String nationalCode = "0000000000";
        try {
            log.error("Start impersonal account job"+DateUtil.getSystemTime());
            finConnection = createConnection();
            cmConnection = ChannelFacadeNew.getDbConnectionPool().getConnection();
            selectFinStm = finConnection.createStatement();
            selectCmStm = cmConnection.createStatement();
            updateImpersonal = cmConnection.createStatement();
            insertHistory = cmConnection.createStatement();
            selectCustomerIdStm = cmConnection.createStatement();
            insertSrv = cmConnection.createStatement();
            insertImpersonal = cmConnection.createStatement();
            selectDeleteAccStm = cmConnection.createStatement();
            deleteImpersonal = cmConnection.createStatement();

            String currentDate = DateUtil.getSystemDate();
            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            Map<String, String> accountMap = new HashMap<String, String>();
            String selectAccount = "select account_no,account_group from tbimpersonalaccounts with ur";
            selectCmRs = selectCmStm.executeQuery(selectAccount);
            while (selectCmRs.next()) {
                accountMap.put(selectCmRs.getString("account_no").trim(), selectCmRs.getString("account_group").trim());
            }
            cmConnection.commit();

            String selectFinQuery = "select ACNT_NO,GRP from nac.TNGLCDBK with ur";
            selectFinRs = selectFinStm.executeQuery(selectFinQuery);
            while (selectFinRs.next()) {

                String accountNo = selectFinRs.getString("ACNT_NO");
                String grp = selectFinRs.getString("GRP");
                try {
                    if (accountNo.length() < 13)
                        accountNo = ISOUtil.zeropad(accountNo, 13);
                } catch (ISOException e) {
                    log.error("Error:: Account =" + accountNo + "--" + e);
                }
                try {
                    if (grp.length() < 3)
                        grp = ISOUtil.zeropad(grp, 3);
                } catch (ISOException e) {
                    log.error("Error:: Account =" + accountNo + "--and account_group=" + grp + "--" + e);
                }

                if (accountMap.containsKey(accountNo)) {
                    accountMap.remove(accountNo);
                    continue;
                }else{
                    //not found then insert
                    //insert in to tbimpersonalaccounts
                    String insertImpersonalQuery = "insert into tbimpersonalaccounts (account_no,account_group,PROCESS_DAY" +
                            ") values (" +
                            "'" + accountNo + "', " +
                            "'" + grp + "', " +
                            "'" + dayOfYear + "' " +
                            ")";
                    insertImpersonal.execute(insertImpersonalQuery);

                    //insert in to tbcustomersrv
                    String customerId = "";

                    String maxCustomer_id_sql = "select next value for seq_customersrv as maxID from sysibm.sysdummy1 with ur";

                    selectCustomerIdRs = selectCustomerIdStm.executeQuery(maxCustomer_id_sql);
                    selectCustomerIdRs.next();
                    String maxID = selectCustomerIdRs.getString(1).trim();
                    if (maxID != null)
                        customerId = maxID;
                    try {
                        customerId = ISOUtil.zeropad(customerId, 12);
                    } catch (ISOException e) {
                        log.error("Can not zeropad CUSTOMER_ID = '" + customerId + "' in ImpersonalAccounts job : " + e.getMessage());
                    }

                    String insertSrvQuery = "insert into TBCUSTOMERSRV (CUSTOMER_ID,PIN, TPIN, LAST_USAGE_TIME, " +
                            "ACCOUNT_NO, TEMPLATE_ID, STATUS, HOST_ID, CREATION_DATE,ACCOUNT_GROUP," +
                            "NATIONAL_CODE,STATUSMELLI, LANG,SHAHAB,STATUSD, SMSNOTIFICATION" +
                            ") values (" +
                            "'" + customerId + "' ," +
                            "'" + 1234 + "', " +
                            "'" + 1234 + "', " +
                            "current_timestamp, " +
                            "'" + accountNo + "' ," +
                            "1," +
                            "1," +
                            Constants.SGB_HOSTID + "," +
                            "'" + DateUtil.getSystemDate() + "' ," +
                            "'" + grp + "'," +
                            "'" + nationalCode + "'," +
                            1 + "," +
                            1 + "," +
                            "'" + 1 + "'," +
                            "'" + 1 + "'," +
                            "'" + 0 + "'" +
                            " )";
                    try {
                        insertSrv.execute(insertSrvQuery);
                        //insert tbimpersonalaccountsHis
                        String insertImpersonalHisQuery = "insert into tbimpersonalaccountsHis (account_no,account_group,CREATION_DATE,CREATION_TIME,ACTION_TYPE" +
                                ") values (" +
                                "'" + accountNo + "', " +
                                "'" + grp + "', " +
                                "'" + currentDate + "', " +
                                "'" + DateUtil.getSystemTime() + "', " +
                                "'" + Constants.INSERT + "' " +
                                ")";

                        insertHistory.execute(insertImpersonalHisQuery);
                        cmConnection.commit();
                    } catch (SQLException exception) {
                        if (exception.getErrorCode() == Constants.DISTINCT_VIOLATION_ERROR) {   //Distinct Violation Error
                            //Exist in tbcustomersrv
                            //insert tbimpersonalaccountsHis
                            String insertImpersonalHisQuery = "insert into tbimpersonalaccountsHis (account_no,account_group,CREATION_DATE,CREATION_TIME,ACTION_TYPE" +
                                    ") values (" +
                                    "'" + accountNo + "', " +
                                    "'" + grp + "', " +
                                    "'" + currentDate + "', " +
                                    "'" + DateUtil.getSystemTime() + "', " +
                                    "'" + Constants.EXIST + "' " +
                                    ")";
                            insertHistory.execute(insertImpersonalHisQuery);
                            cmConnection.commit();
                        }
                    }
                }
            }
            finConnection.commit();

            for (Map.Entry<String, String> entry : accountMap.entrySet()) {

                String account_no = entry.getKey();
                String account_group = entry.getValue();
                //delete tbimpersonalaccounts
                String deleteImpersonalQuery = "delete from tbimpersonalaccounts where account_no='" + account_no + "'";
                deleteImpersonal.execute(deleteImpersonalQuery);

                //insert tbimpersonalaccountsHis
                String insertImpersonalHisQuery = "insert into tbimpersonalaccountsHis (account_no,account_group,CREATION_DATE,CREATION_TIME,ACTION_TYPE" +
                        ") values (" +
                        "'" + account_no + "', " +
                        "'" + account_group + "', " +
                        "'" + currentDate + "', " +
                        "'" + DateUtil.getSystemTime() + "', " +
                        "'" + Constants.DELETE + "' " +
                        ")";
                insertHistory.execute(insertImpersonalHisQuery);
                cmConnection.commit();
            }
            log.error("ImpersonalAccountsJob Done at "+DateUtil.getSystemTime());
        } catch (Exception e) {
            log.error("ImpersonalAccountsJob Done with Error at "+DateUtil.getSystemTime());
            log.error("Exception in ImpersonalAccountsJob_mainRoutine>> " + e.getMessage());
            try {
                cmConnection.rollback();
            } catch (SQLException e1) {
                log.error("Exception in ImpersonalAccountsJob_rollback>> " + e.getMessage());
            }

        } finally {
            try {
                if (selectDeleteAccRs != null)
                    selectDeleteAccRs.close();
                if (selectCustomerIdRs != null)
                    selectCustomerIdRs.close();
                if (selectCmRs != null)
                    selectCmRs.close();
                if (selectFinRs != null)
                    selectFinRs.close();
                if (selectFinStm != null)
                    selectFinStm.close();
                if (selectCmStm != null)
                    selectCmStm.close();
                if (updateImpersonal != null)
                    updateImpersonal.close();
                if (insertHistory != null)
                    insertHistory.close();
                if (insertImpersonal != null)
                    insertImpersonal.close();
                if (insertSrv != null)
                    insertSrv.close();
                if (selectCustomerIdStm != null)
                    selectCustomerIdStm.close();
                if (selectDeleteAccStm != null)
                    selectDeleteAccStm.close();
                if (deleteImpersonal != null)
                    deleteImpersonal.close();
                if (finConnection != null)
                    finConnection.close();
                ChannelFacadeNew.getDbConnectionPool().returnConnection(cmConnection);
            } catch (Exception e) {
                log.error("Exception in ImpersonalAccountsJob_finally>> " + e.getMessage());
            }
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
}
