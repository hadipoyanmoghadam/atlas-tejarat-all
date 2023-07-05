package dpi.atlas.service.cm.handler.general.trans;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.iso.MyISODate;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 28, 2019
 * Time: 9:04:03 AM
 */

public class GetAccountInquiry extends HostHandlerBase {
    private static Log log = LogFactory.getLog(GetAccountInquiry.class);
    public static final String CREDIT_VALUE = "0";


    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside GetAccountInquiry:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
        inputParams.put(Constants.ACC_NO, branchMsg.getAccountNo());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {

            hrs = ((HostInterfaceNAB) hostInterface).getAccountInquiry(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs);
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "accNo");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "BackUpStatus");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("AccountNo"));
        row.add(original_res.getString("BRANCHNO").substring(1));
        row.add(original_res.getString("BackUpStatus"));

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