package dpi.atlas.service.cm.handler.general.trans.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: January 7, 2020
 * Time: 10:34:03 AM
 */
public class GetSiminStatement extends HostHandlerBase {
    final static String[] INVALID_CHARACTER = {"\u0000", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\u0008", "\u000B",
            "\u000C", "\u000E", "\u000F", "\u0010", "\u0011", "\u0012", "\u0013", "\u0014", "\u0015", "\u0016",
            "\u0017", "\u0018", "\u0019", "\u001A", "\u001B", "\u001C", "\u001D", "\u001E", "\u001F", "\u0020"};


    public HostResultSet _post(Object obj, Map holder, HostInterface host) throws CMFault {
        CMMessage msg = (CMMessage) obj;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        BranchMessage branchMessage = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String acc_no = branchMessage.getAccountNo();
        String trans_count = command.getParam(Fields.TRANS_COUNT);
        String branchId = command.getParam(Fields.BRANCH_ID);
        String docSerial = command.getParam(Fields.BRANCH_DOC_NO);
        String start_date = command.getParam(Fields.FROM_DATE);
        String end_date = command.getParam(Fields.TO_DATE);
        String fromTime = command.getParam(Fields.FROM_TIME);
        String toTime = command.getParam(Fields.TO_TIME);
        String sign = command.getParam(Fields.DEBIT_CREDIT);
        String opCode = command.getParam(Fields.OPERATION_CODE);
        String minAmount = command.getParam(Fields.MIN_AMOUNT);
        String maxAmount = command.getParam(Fields.MAX_AMOUNT);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);

        trans_count = (trans_count != null && trans_count.length() != 0) ? trans_count : "3";
        branchId = (branchId != null && branchId.length() != 0) ? branchId : "";
        docSerial = (docSerial != null && docSerial.length() != 0) ? docSerial : "";
        start_date = (start_date != null && start_date.length() != 0) ? start_date : "";
        end_date = (end_date != null && end_date.length() != 0) ? end_date : "";
        if (sign == null || "".equals(sign))
            sign = "0";

        opCode = (opCode != null && opCode.trim().length() != 0) ? opCode : "";
        minAmount = (minAmount != null && minAmount.trim().length() != 0) ? minAmount : "";
        maxAmount = (maxAmount != null && maxAmount.trim().length() != 0) ? maxAmount : "";
        fromTime = (fromTime == null) ? "" : fromTime;
        toTime = (toTime == null) ? "" : toTime;

        if ("".equals(branchId))
            branchId = "888888";
        if ("".equals(opCode))
            opCode = "0";
        if ("".equals(docSerial))
            docSerial = "0";
        if ("".equals(start_date))
            start_date = "00000000";
        if ("".equals(end_date))
            end_date = "99999999";

        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        inputParams.put(Constants.ACC_NO, acc_no);
        inputParams.put(Constants.START_DATE, start_date);
        inputParams.put(Constants.END_DATE, end_date);
        inputParams.put(Constants.START_TIME, fromTime);
        inputParams.put(Constants.END_TIME, toTime);
        if (Integer.parseInt(trans_count) > 150)
            trans_count = "150";
        inputParams.put(Constants.TRANS_COUNT, trans_count);
        inputParams.put(Constants.BRANCH_ID, branchId);
        inputParams.put(Constants.DOC_SERIAL, docSerial);
        inputParams.put(Constants.SIGN, sign);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.MIN_AMOUNT, minAmount);
        inputParams.put(Constants.MAX_AMOUNT, maxAmount);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        HostResultSet rs = null;
        HostResultSet hrs_ret = null;
        try {
            rs = ((HostInterfaceNAB) host).getBranchStatement(inputParams);
            if (rs.next())
                hrs_ret = getHostResultSet(rs);
        } catch (HostException e) {
            log.error(e);
        } catch (ISOException e) {
            log.error(e);
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException, ISOException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "COMMAND");
        rowindex++;
        md.addColumn(rowindex, "REF-NO");
        rowindex++;
        md.addColumn(rowindex, "ACC-NO");
        rowindex++;
        md.addColumn(rowindex, "DATE");
        rowindex++;
        md.addColumn(rowindex, "TIME");
        rowindex++;
        md.addColumn(rowindex, "AMOUNT");
        rowindex++;
        md.addColumn(rowindex, "CRDB");
        rowindex++;
        md.addColumn(rowindex, "DESC");
        rowindex++;
        md.addColumn(rowindex, "BRANCHNO");
        rowindex++;
        md.addColumn(rowindex, "DATETIME");
        rowindex++;
        md.addColumn(rowindex, "BALANCE");
        rowindex++;
        md.addColumn(rowindex, "AVAILABLE_BALANCE");
        rowindex++;
        md.addColumn(rowindex, "OPCODE");
        rowindex++;
        md.addColumn(rowindex, "LASTAMOUNT");

        hostResultSet.setMetaData(md);
        original_res.moveFirst();

        while (original_res.next()) {
            ArrayList row = new ArrayList();
            row.add(original_res.getString("COMMAND"));
            if (original_res.getString("DOCNO").length() > 7)
                row.add(original_res.getString("DOCNO").trim().substring(5, 12));
            else
                row.add(ISOUtil.zeropad(ISOUtil.zeroUnPad(original_res.getString("DOCNO").trim()), 7));
            row.add(original_res.getString("ACCOUNTNO"));
            row.add(original_res.getString("TRANSDATE"));
            row.add(original_res.getString("TRANSTIME"));

            String amount = original_res.getString("AMOUNT");
            if (amount == null || amount.equals(""))
                row.add("0");
            else
                row.add(amount);

            row.add(original_res.getString("DEBITCREDIT").equalsIgnoreCase("+") ? "C" : "D");
            String desc = original_res.getString("DESCRIPTION");
            if (desc == null) {
                desc = "";
            }
            for (int i = 0; i < 29; i++)
                desc = desc.replaceAll(INVALID_CHARACTER[i], " ");
            row.add(desc);
            row.add(original_res.getString("BRANCHNO"));
            row.add(original_res.getString("DATETIME"));
            row.add(original_res.getString("BALCRDB") + original_res.getString("FINALBALANCE")); //Main balance in header tages

//             set available balance
            row.add(original_res.getString("AvailableBalSign") + original_res.getString("AvailableBalance"));

            row.add(original_res.getString("OPCODE"));
            String lastAmount = original_res.getString("LASTAMOUNT");
            if (lastAmount == null || lastAmount.equals(""))
                row.add("0");
            else
                row.add(lastAmount);

            hostResultSet.addRow(row);
        }
        original_res.moveFirst();
        original_res.next();
        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        return hostResultSet;
    }
}
