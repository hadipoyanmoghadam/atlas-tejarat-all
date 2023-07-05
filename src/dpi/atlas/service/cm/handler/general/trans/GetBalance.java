package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.iso.MyISODate;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

/**
 * BalanceHandler class
 * with Ahmadi
 *
 * @author <a href="mailto:amjadi.sahar@tgmail.com">Sahar Amjadi</a>
 * @version $Revision: 1.13 $ $Date: 2007/10/29 14:04:28 $
 */
public class GetBalance extends HostHandlerBase {

    private static Log log = LogFactory.getLog(GetBalance.class);
    public static final String CREDIT_VALUE = "0";
    public static final String DEBIT_VALUE = "1";

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BalanceHandler:_post()");
        CMMessage msg = (CMMessage) o;
        String service = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        if (service.equals(Constants.ISO_SERVICE)) {
            return getISOBalance(msg, hostInterface);
        } else {
            return getEBNKBalance(msg, hostInterface);
        }

    }

    public HostResultSet getISOBalance(CMMessage msg, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BalanceHandler--->getISOBalance()");
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String acc_no = command.getParam(Fields.ACCOUNT_NO);
        String host_id = (String) msg.getAttribute(Constants.SRC_HOST_ID);
        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        String accountType = Constants.RIALI_ACCOUNT;
        if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE))
            accountType = Constants.ARZI_ACCOUNT;

        msg.setAttribute(Constants.ACC_TYPE, accountType);
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");
        inputParams.put(Constants.ACC_NO, acc_no);
        inputParams.put(Constants.ACC_TYPE, accountType);

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).getSWBalance(inputParams);
            if (hrs.next()) {
                hrs_ret = getISOHostResultSet(hrs, host_id);
                try {
                    String resAccNo = ISOUtil.zeropad(hrs_ret.getDataHeaderField(Fields.ACC_NO), 13);
                    if (!acc_no.equalsIgnoreCase(resAccNo)) {
                        log.info(" INFO, the Acc and ResAcc is not equal :: reqAcc = " + acc_no + " , resAcc = " + resAccNo);
                    }
                } catch (ISOException e) {
                    e.toString();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;

    }

    private HostResultSet getISOHostResultSet(HostResultSet original_res, String host_id) throws HostException {
        int rowindex = 0;
        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "BAL");
        rowindex++;
        md.addColumn(rowindex, "Currency");
        rowindex++;
        md.addColumn(rowindex, "hostId");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "accountGroup");
        rowindex++;
        md.addColumn(rowindex, "CRDB");
        rowindex++;
        md.addColumn(rowindex, "lastTransDate");
        rowindex++;
        md.addColumn(rowindex, "ledgerBalance");
        rowindex++;
        md.addColumn(rowindex, Constants.AVAILBLE_BAL_SIGN);
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("AVAILABLEBALANCE"));
        row.add("000");
        row.add(host_id);
        row.add(current_date_persian);
        row.add(original_res.getString("BRANCHNO"));
        row.add(original_res.getString("ACCOUNTGROUP").trim());
        if (original_res.getString("CRDB").equals(CREDIT_VALUE))
            row.add(Constants.CREDIT);
        else
            row.add(Constants.DEBIT);
        row.add("");
        String ledgerBal = original_res.getString("LEDGERBALANCE");
        if (Float.parseFloat(ledgerBal) < 0)
            ledgerBal = ledgerBal.substring(1, ledgerBal.length());
        row.add(ledgerBal);
        row.add(original_res.getString("AVAIBLEBALSIGN"));
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        hostResultSet.setDataHeaderField(Fields.ACC_NO, original_res.getString("ACCOUNTNO").trim());

        return hostResultSet;
    }

    public HostResultSet getEBNKBalance(CMMessage msg, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BalanceHandler--->getEBNKBalance");
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String acc_no = command.getParam(Fields.ACCOUNT_NO);
        String host_id = (String) msg.getAttribute(Constants.SRC_HOST_ID);
        String branch_code = (String) msg.getAttribute(Fields.BRANCH_CODE);
        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        String accountType = Constants.RIALI_ACCOUNT;
        if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE))
            accountType = Constants.ARZI_ACCOUNT;

        msg.setAttribute(Constants.ACC_TYPE, accountType);
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");
        inputParams.put(Constants.ACC_NO, acc_no);
        inputParams.put(Constants.ACC_TYPE, accountType);

        if (branch_code == null || "".equals(branch_code.trim()))
            branch_code = "";

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = hostInterface.getBalance(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, host_id, branch_code);
                try {
                    String resAccNo = ISOUtil.zeropad(hrs_ret.getDataHeaderField(Fields.ACC_NO), 13);
                    if (!acc_no.equalsIgnoreCase(resAccNo)) {
                        log.info(" INFO, the Acc and ResAcc is not equal :: reqAcc = " + acc_no + " , resAcc = " + resAccNo);
                    }
                } catch (ISOException e) {
                    e.toString();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;

    }

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String branchCode) throws HostException {
        int rowindex = 0;
        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "BAL");
        rowindex++;
        md.addColumn(rowindex, "Currency");
        rowindex++;
        md.addColumn(rowindex, "hostId");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "accountGroup");
        rowindex++;
        md.addColumn(rowindex, "CRDB");
        rowindex++;
        md.addColumn(rowindex, "lastTransDate");
        rowindex++;
        md.addColumn(rowindex, "ledgerBalance");
        rowindex++;
        md.addColumn(rowindex, Constants.AVAILBLE_BAL_SIGN);
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("AVAILABLEBALANCE"));
        if (host_id.equals(Constants.SGB_HOSTID))
            row.add((original_res.getString("CURRENCY") != null && !"".equals(original_res.getString("CURRENCY")))
                    ? original_res.getString("CURRENCY").trim() : "000");
        else
            row.add("000");
        row.add(host_id);
        row.add(current_date_persian);
        row.add(original_res.getString("BRANCHNO"));
        row.add(original_res.getString("ACCOUNTGROUP").trim());
        if (original_res.getString("CRDB").equals(CREDIT_VALUE))
            row.add(Constants.CREDIT);
        else
            row.add(Constants.DEBIT);
        if (host_id.equals(Constants.SGB_HOSTID))
            row.add(original_res.getString("LASTTRANSDATE"));
        else
            row.add("");
        if (host_id.equals(Constants.FAR_HOSTID)) {
            String ledgerBal = original_res.getString("LEDGERBALANCE");
            if (Float.parseFloat(ledgerBal) < 0)
                ledgerBal = ledgerBal.substring(1, ledgerBal.length());
            row.add(ledgerBal);
        } else {
            String availableBal = original_res.getString("AVAILABLEBALANCE");
            if (Float.parseFloat(availableBal) < 0)
                availableBal = availableBal.substring(1, availableBal.length());
            row.add(availableBal);
        }
        if (host_id.equals(Constants.FAR_HOSTID))
            row.add(original_res.getString("AVAIBLEBALSIGN"));
        else
            row.add("");
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        hostResultSet.setDataHeaderField(Fields.ACC_NO, original_res.getString("ACCOUNTNO").trim());

        return hostResultSet;
    }

    public String getCurrentDate(Calendar calendar) {
        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        try {
            month = ISOUtil.zeropad(month, 2);
            day = ISOUtil.zeropad(day, 2);
        } catch (ISOException e) {
            e.toString();
        }

        return year + month + day;
    }


}

