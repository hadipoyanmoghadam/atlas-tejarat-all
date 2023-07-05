package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;
import dpi.atlas.iso.MyISODate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

import java.util.Map;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: Jun 16, 2008
 * Time: 4:44:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetCustomerInfo extends HostHandlerBase {
    private static Log log = LogFactory.getLog(GetCustomerInfo.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        CMMessage msg = (CMMessage) o;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String accNo = command.getParam(Fields.ACCOUNT_NO);
        String accStatus = msg.getAttributeAsString(Constants.ACCOUNT_STATUS);
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        inputParams.put(Constants.ACC_NO, accNo);

        String host_id = (String) msg.getAttribute(Constants.SRC_HOST_ID);
        String branch_code = (String) msg.getAttribute(Fields.BRANCH_CODE);
        if (branch_code == null || "".equals(branch_code.trim()))
            branch_code = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");
        HostResultSet hrs = null;
        HostResultSet hrs_ret = null;
        try {
            hrs = hostInterface.getCustomerInfo(inputParams);
            if (hrs.next()) {
                try {
                    hrs_ret = getHostResultSet(hrs, host_id, accStatus);
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

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String accountStatus) throws HostException, ISOException {
        int rowindex = 0;
        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "ACCOUNTGROUP");
        rowindex++;
        md.addColumn(rowindex, "accountStatus");
        rowindex++;
        md.addColumn(rowindex, "accountBranchId");
        rowindex++;
        md.addColumn(rowindex, "PrsnTyp");
        rowindex++;
        md.addColumn(rowindex, "customerName");
        rowindex++;
        md.addColumn(rowindex, "fatherName");
        rowindex++;
        md.addColumn(rowindex, "nationalCode");
        rowindex++;
        md.addColumn(rowindex, "birthDate");
        rowindex++;
        md.addColumn(rowindex, "birthPlace");
        rowindex++;
        md.addColumn(rowindex, "phone");
        rowindex++;
        md.addColumn(rowindex, "Currency");
        rowindex++;
        md.addColumn(rowindex, "hostId");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");

        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("ACCOUNTGROUP"));
        row.add(accountStatus);
        String brchId = original_res.getString("ACCOUNTBRANCHID");
        if (brchId != null)
            brchId = ISOUtil.zeropad(brchId, 5);
        else
            brchId = "";
        row.add(brchId);
        row.add(original_res.getString("PRSNTYP"));
        row.add(original_res.getString("CUSTOMERNAME"));
        row.add(original_res.getString("FATHERNAME"));
        row.add(original_res.getString("NATIONALCODE"));
        row.add(original_res.getString("BIRTHDATE"));
        row.add(original_res.getString("BIRTHPLACE"));
        row.add(original_res.getString("PHONENUMBER"));
        row.add("000");
        row.add(host_id);
        row.add(current_date_persian);

        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));

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
