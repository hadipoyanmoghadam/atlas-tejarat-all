package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: Dec 14, 2011
 * Time: 5:09:13 PM
 * To change this template use File | Settings | File Templates.
 */

public class MunicipalityBillPayFollowup extends HostHandlerBase {
    public HostResultSet _post(Object object, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside MunicipalityBillPayFollowup:_post()");
        CMMessage msg = (CMMessage) object;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String billId = command.getParam(Fields.MNCPLTY_BILL_ID);
        String paymentId = command.getParam(Fields.MNCPLTY_PAYMENT_ID);
        HashMap inputParams = new HashMap();
        inputParams.put(Constants.BILL_ID, billId);
        inputParams.put(Constants.PAYMENT_ID, paymentId);
        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB)hostInterface).municpalityBillFlwup(inputParams);
            if (hrs.next())
                hrs_ret = getHostResultSet(hrs);
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;
        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        
        rowindex++;
        md.addColumn(rowindex, Constants.BRANCH_NO);
        rowindex++;
        md.addColumn(rowindex, Constants.TRANS_AMOUNT);
        rowindex++;
        md.addColumn(rowindex, Constants.TRANS_DATE);
        rowindex++;
        md.addColumn(rowindex, Constants.TRANS_TIME);
        hostResultSet.setMetaData(md);
        
        ArrayList row = new ArrayList();
        row.add(original_res.getString(Constants.MNCPLTY_PAY_BRANCH));
        row.add(original_res.getString(Constants.MNCPLTY_BILL_AMNT));
        row.add(original_res.getString(Constants.MNCPLTY_PAY_DATE).trim().substring(0, 8));
        row.add(original_res.getString(Constants.MNCPLTY_PAY_TIME).trim().substring(8, 14));
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        return hostResultSet;
    }
}
