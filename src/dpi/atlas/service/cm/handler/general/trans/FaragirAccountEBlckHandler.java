package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
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
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamadzadeh
 * Date: Jun 25, 2012
 * Time: 10:34:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class FaragirAccountEBlckHandler extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FaragirAccountEBlckHandler.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FaragirAccountEBlckHandler:_post()");
        CMMessage msg = (CMMessage) o;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String acc_no = command.getParam(Fields.ACCOUNT_NO);
        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String hostId = msg.getAttributeAsString("HostID");
        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);

        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        inputParams.put(Constants.ACC_NO, acc_no);
        inputParams.put(Constants.DATE, date);
        inputParams.put(Constants.TIME, time);
        inputParams.put(Constants.BRANCH_ID, "");
        inputParams.put(Constants.DEVICE_ID, "");
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");


        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).getEBlckAcc(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, hostId, ISOUtil.zeropad(acc_no, 13));
                String resAccNo = ISOUtil.zeropad(hrs_ret.getDataHeaderField(Fields.ACC_NO), 13);
                if (!acc_no.equalsIgnoreCase(resAccNo)) {
                    log.info(" INFO, the Acc and ResAcc is not equal :: reqAcc = " + acc_no + " , resAcc = " + resAccNo);
                }
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        } catch (ISOException e) {
            e.toString();  //To change body of catch statement use File | Settings | File Templates.
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String acc_no) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "DateTime");
//        rowindex++;
//        md.addColumn(rowindex,"AccountNo");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(host_id);
        row.add(original_res.getString("DateTime"));
//        row.add(original_res.getString("AccountNo"));

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        hostResultSet.setDataHeaderField(Fields.ACC_NO, original_res.getString("ACCOUNTNO").trim());

        return hostResultSet;
    }

}