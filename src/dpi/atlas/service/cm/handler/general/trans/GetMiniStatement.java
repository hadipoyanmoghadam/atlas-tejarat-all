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
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


/**
 * GetMiniStatement class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.21 $ $Date: 2007/10/29 14:04:28 $
 */
public class GetMiniStatement extends HostHandlerBase {

    public HostResultSet _post(Object obj, Map holder, HostInterface host) throws CMFault {
        CMMessage msg = (CMMessage) obj;
        String acc_no = (String) msg.getAttribute(Fields.ACCOUNT_NO);

        String trans_count = (String) msg.getAttribute(Fields.TRANS_COUNT);
        if (trans_count == null)
            trans_count = "3";
        if (trans_count.compareToIgnoreCase("") == 0)
            trans_count = "3";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);

        //acc_no = "1821423";

//        From date: 1841210
//        int daysNum = 1 * 365;
//        if (log.isDebugEnabled())             log.debug("daysNum=" + daysNum);
//        String start_date = getPreviousDate(daysNum);
//        String end_date = getCurrentDate(Calendar.getInstance());

        String start_date = "";
        String end_date = "";

        HostResultSet rs = null;
        try {
            if (sessionID != null && "".equals(sessionID.trim()))
                rs = host.getStatement(sessionID, acc_no, start_date, end_date, Integer.parseInt(trans_count), service, "", "", "");
            else
                rs = host.getStatement(acc_no, start_date, end_date, Integer.parseInt(trans_count), service, "", "", "");
        } catch (HostException e) {
            if (log.isDebugEnabled()) log.debug("e.getErrorCode() = " + e.getErrorCode());
            if (e.getErrorCode() != 200) {
                log.error(e);
                throw new CMFault(CMFault.FAULT_EXTERNAL, e.getMessage());
            }
        }

//        rs.dump(System.out);

        try {
            rs = getHostResultSet(rs);
        } catch (HostException e) {
            log.error(e);
        }

        return rs;
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

    public String getPreviousDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days * (-1));


        return getCurrentDate(calendar);
    }


    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "REF-NO");
        rowindex++;
        md.addColumn(rowindex, "DATE");
        rowindex++;
        md.addColumn(rowindex, "AMOUNT");
        rowindex++;
        md.addColumn(rowindex, "CURRENCY");
        rowindex++;
        md.addColumn(rowindex, "DESC");
        rowindex++;

        hostResultSet.setMetaData(md);

        if (original_res != null) {
            while (original_res.next()) {
                ArrayList row = new ArrayList();

                row.add(ISOUtil.zeroUnPad(original_res.getString("DOCNO")));
                row.add(original_res.getString("TRANSDATE"));
                row.add(NumberConvertor.convert2ISO(original_res.getString("DEBITCREDIT") + original_res.getString("AMOUNT")));
                row.add("IRR");
                //Todo: Ahmadi Commented This because this description has garbage characters.
//                row.add(original_res.getString("DESCRIPTION"));
//                row.add("Test Desc");
                row.add(original_res.getString("DESCRIPTION").substring(0, 3));

                hostResultSet.addRow(row);
            }
            original_res.moveFirst();
        }

        if (original_res != null && !original_res.isEmpty())
            hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        else
            hostResultSet.setDataHeaderField("action_code", ActionCode.APPROVED);
//        hostResultSet.dump(System.out);
        return hostResultSet;

    }


}
