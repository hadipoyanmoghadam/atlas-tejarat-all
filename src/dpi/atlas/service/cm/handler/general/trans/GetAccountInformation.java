package dpi.atlas.service.cm.handler.general.trans;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 23, 2015
 * Time: 10:04:03 AM
 */

public class GetAccountInformation extends HostHandlerBase {
    private static Log log=LogFactory.getLog(GetAccountInformation.class);
    public HostResultSet _post(Object o,Map map,HostInterface hostInterface) throws CMFault{
        if(log.isInfoEnabled()) log.info("Inside GetAccountInformation:_post()");
        CMMessage msg=(CMMessage) o;
        HashMap inputParams=(HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        inputParams.put(Constants.ACC_NO,msg.getAttributeAsString(Fields.ACCOUNT_NO));
        HostResultSet hrs;
        HostResultSet hrs_ret=null;
        try{
            hrs=((HostInterfaceNAB)hostInterface).getAccountInfo(inputParams);
            if(hrs.next()){
                hrs_ret=getHostResultSet(hrs);
            }
        } catch (HostException e) {
            String errorCode=Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "accNo");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "accGroup");
        rowindex++;
        md.addColumn(rowindex, "accType");
        rowindex++;
        md.addColumn(rowindex, "sex");
        rowindex++;
        md.addColumn(rowindex, "nationalCode");
        rowindex++;
        md.addColumn(rowindex, "foreingCode");
        rowindex++;
        md.addColumn(rowindex, "idNumber");
        rowindex++;
        md.addColumn(rowindex, "birthDate");
        rowindex++;
        md.addColumn(rowindex, "mobileNo");
        rowindex++;
        md.addColumn(rowindex, "fatherName");
        rowindex++;
        md.addColumn(rowindex, "customerName");
        rowindex++;
        md.addColumn(rowindex, "customerFamily");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("accNo"));
        row.add(original_res.getString("branchCode"));
        row.add(original_res.getString("accGroup"));
        row.add(original_res.getString("accType"));
        row.add(original_res.getString("sex"));
        row.add(original_res.getString("nationalCode"));
        row.add(original_res.getString("foreingCode"));
        row.add(original_res.getString("idNumber"));
        row.add(original_res.getString("birthDate"));
        row.add(original_res.getString("mobileNo"));
        row.add(original_res.getString("fatherName"));
        row.add(original_res.getString("customerName"));
        row.add(original_res.getString("customerFamily"));
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTION_CODE").substring(1, 5));

        return hostResultSet;

    }

}