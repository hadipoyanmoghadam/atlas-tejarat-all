package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.iso.MyISODate;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
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
 * User:F.Heydari
 * Date: AUG 1, 2021
 * Time: 10:04:03 AM
 */

public class ChangeStatusBlock extends HostHandlerBase {
    private static Log log = LogFactory.getLog(ChangeStatusBlock.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside ChangeStatusBlock():");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);

        String accountStatus = msg.getAttributeAsString(Fields.ACCOUNT_STATUS);
        String accountNo=msg.getAttributeAsString(Fields.ACCOUNT_NO);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");


            inputParams.put(Constants.SERVICE, Constants.SIMIN_SERVICE); //BlockService that use Simin serviceName
            inputParams.put(Constants.ACC_NO, accountNo);
            inputParams.put(Constants.BRANCH_CODE,msg.getAttributeAsString(Fields.BRANCH_CODE));
            inputParams.put(Constants.ENEX_BLOCK_AMOUNT,msg.getAttributeAsString(Fields.AMOUNT) != null ? msg.getAttributeAsString(Fields.AMOUNT) : "");
            inputParams.put(Constants.BLOCK_NO, msg.getAttributeAsString(Fields.BLOCK_ROW).substring(3));
            inputParams.put(Constants.DATE,msg.getAttributeAsString(Fields.BLCK_DATE));
            inputParams.put(Constants.TIME, msg.getAttributeAsString(Fields.BLCK_TIME));
            inputParams.put(Constants.BLOCK_DESC, msg.getAttributeAsString(Fields.DESC));
            inputParams.put(Fields.USER_ID, msg.getAttributeAsString(Fields.USER_ID));
            inputParams.put(Constants.BLOCK_STATUS, accountStatus);
         //   inputParams.put(Constants.MERCHANT_TERMINAL_ID,branchMsg.getTerminalId()!=null ? branchMsg.getTerminalId() : "");
            inputParams.put(Constants.MERCHANT_TERMINAL_ID,"");
            inputParams.put(Constants.DST_ACCOUNT, "");
            inputParams.put(Constants.WAGE_AMOUNT, "0");
            inputParams.put(Constants.REF_NO, "");
            inputParams.put(Constants.SRC_HOST_ID, "");
            inputParams.put(Constants.BATCH_PK, "");
            inputParams.put(Constants.MSG_SEQ,"");
            inputParams.put(Constants.OP_CODE, "");
            inputParams.put(Fields.BRANCH_DOC_NO, "");
            inputParams.put(Constants.MERCHANT_RRN,"");
            inputParams.put(Fields.TX_CODE, "");
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, "");
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, "");
            inputParams.put(Constants.DOC_DESC, "");
            inputParams.put(Constants.EXTRA_INFO, "");
            inputParams.put(Constants.BROKER, Constants.BROKER_ID_BLOCK_FOR_FARAGIR);
            inputParams.put(Constants.PROVIDER, Constants.PROVIDER_ID_BLOCK);
            inputParams.put(Constants.MESSAGE_TYPE, "N");  //N=without Document Y=with Document

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            if (accountStatus.equalsIgnoreCase(Constants.BLOCK_ACCOUNT_STATUS))
                //block amount
                hrs = ((HostInterfaceNAB) hostInterface).Account_AmountBlock(inputParams);
            else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_ACCOUNT_STATUS))
                //unblock amount
                hrs = ((HostInterfaceNAB) hostInterface).Account_AmountUNBlock(inputParams);
            else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
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
        md.addColumn(rowindex, "serverUpdateDate");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("AccountNo"));
        row.add(current_date_persian);
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

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