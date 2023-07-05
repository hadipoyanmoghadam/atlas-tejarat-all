package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.globus.ofs.convertor.NumberConvertor;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Nov 5, 2006
 * Time: 10:36:32 AM
 * To change this template use File | Settings | File Templates.
 */

public class GetTransactionStatement extends HostHandlerBase {
    public HostResultSet _post(Object obj, Map holder, HostInterface host) throws CMFault {
        CMMessage msg = (CMMessage) obj;
        String acc_no = (String) msg.getAttribute(Fields.ACCOUNT_NO);

        //Ahmadi added in 1385-08-03: in IVR client,statement response can not be alot(clint restriction),
        //therfore in IVR,we must set this, if this not set, default value is 150,TODO: check default value
        String trans_count = (String) msg.getAttribute(Fields.TRANS_COUNT);
        if (trans_count == null)
            trans_count = "10";

//        test acc: acc_no = "40020101";

        String start_date = msg.getAttributeAsString(Fields.FROM_DATE);
        String end_date = msg.getAttributeAsString(Fields.TO_DATE);
        if (start_date == null)
            start_date = "";
        if (end_date == null)
            end_date = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);

        String debitCredit = msg.getAttributeAsString(Fields.DEBIT_CREDIT);
        String effectiveDate = msg.getAttributeAsString(Fields.Effective_Date);
        String refNo = msg.getAttributeAsString(Fields.REFRENCE_NUMBER);

        HostResultSet rs = null;
        try {
            if (log.isDebugEnabled()) log.debug("acc_no = " + acc_no);
            if (log.isDebugEnabled()) log.debug("effectiveDate = " + effectiveDate);
            if (log.isDebugEnabled()) log.debug("debitCredit = " + debitCredit);
            if (log.isDebugEnabled()) log.debug("refNo = " + refNo);
            if (log.isDebugEnabled()) log.debug("service = " + service);
            if (sessionID != null && "".equals(sessionID.trim()))
                rs = host.specificTransaction(sessionID, acc_no, effectiveDate, debitCredit, refNo, service, "", "", "");
            else
                rs = host.specificTransaction(acc_no, effectiveDate, debitCredit, refNo, service, "", "", "");
        } catch (HostException e) {
            if (log.isDebugEnabled()) log.debug("e.getErrorCode() = " + e.getErrorCode());
            if (e.getErrorCode() != 200) {
                log.error(e);
                throw new CMFault(CMFault.FAULT_EXTERNAL, e.getMessage());
            }
        }

        try {
            rs = getHostResultSet(rs);
        } catch (HostException e) {
            log.error(e);
        }

        return rs;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
//        md.addColumn(rowindex, "ACTION-CODE");
//        rowindex++;
        md.addColumn(rowindex, "REF-NO");
        rowindex++;
        md.addColumn(rowindex, "ACC-NO");
        rowindex++;
        md.addColumn(rowindex, "DATE");
        rowindex++;
        md.addColumn(rowindex, "AMOUNT");
        rowindex++;
        md.addColumn(rowindex, "DESC");
        rowindex++;

        hostResultSet.setMetaData(md);

        if (original_res != null) {
            while (original_res.next()) {
                ArrayList row = new ArrayList();

                //            row.add(original_res.getString("ACTIONCODE").substring(1, 5));
                row.add(ISOUtil.zeroUnPad(original_res.getString("DOCNO")));
                row.add(original_res.getString("ACCOUNTNO"));
                row.add(original_res.getString("TRANSDATE"));
                row.add(NumberConvertor.convert2ISO(original_res.getString("DEBITCREDIT") + original_res.getString("AMOUNT")));
                row.add(original_res.getString("DESCRIPTION"));

                hostResultSet.addRow(row);
            }
            original_res.moveFirst();
//            original_res.next();
        }

//        if (original_res != null && !original_res.isEmpty() && original_res.getString("ACTIONCODE") != null && !original_res.getString("ACTIONCODE").equals(""))
        if (original_res != null && !original_res.isEmpty())
            hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        else
            hostResultSet.setDataHeaderField("action_code", ActionCode.APPROVED);
        return hostResultSet;
    }
}
