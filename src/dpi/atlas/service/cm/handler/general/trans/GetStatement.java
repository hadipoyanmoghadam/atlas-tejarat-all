package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import dpi.atlas.model.sgb.HostInterfaceSGB;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.text.DecimalFormat;

public class GetStatement extends HostHandlerBase {

    final static String[] INVALID_CHARACTER = {"\u0000", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\u0008", "\u000B",
            "\u000C", "\u000E", "\u000F", "\u0010", "\u0011", "\u0012", "\u0013", "\u0014", "\u0015", "\u0016",
            "\u0017", "\u0018", "\u0019", "\u001A", "\u001B", "\u001C", "\u001D", "\u001E", "\u001F", "\u0020"};


    public HostResultSet _post(Object obj, Map holder, HostInterface host) throws CMFault {
        CMMessage msg = (CMMessage) obj;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String acc_no = command.getParam(Fields.ACCOUNT_NO);
        String trans_count = command.getParam(Fields.TRANS_COUNT);
        String branchId = command.getParam(Fields.BRANCH_ID);
        String docSerial = command.getParam(Fields.DOC_SERIAL);
        String start_date = command.getParam(Fields.FROM_DATE);
        String end_date = command.getParam(Fields.TO_DATE);
        String fromTime = command.getParam(Fields.FROM_TIME);
        String toTime = command.getParam(Fields.TO_TIME);
        String sign = command.getParam(Fields.DEBIT_CREDIT);
        String opCode = command.getParam(Fields.OPERATION_CODE);
        String minAmount = command.getParam(Fields.MIN_AMOUNT);
        String maxAmount = command.getParam(Fields.MAX_AMOUNT);
        String msgSeq = command.getParam(Fields.MESSAGE_SEQUENCE);
        String hostId = msg.getAttributeAsString("HostID");
        String accountType = Constants.RIALI_ACCOUNT;
        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        String cbPayId = null != command.getParam(Fields.CB_PAY_ID) ? command.getParam(Fields.CB_PAY_ID) : "";
        if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE))
            accountType = Constants.ARZI_ACCOUNT;

        try {
            msg.setAttribute(Constants.ACC_TYPE, accountType);
        }
        catch (Exception e) {
            if (sessionID != null && !"".equals(sessionID.trim())) {
                System.out.println(" Inside of getStatement - Error of holder.put(Constants.ACC_TYPE, accountType) - session_id = " + sessionID + " -- ErrorType = " + e.getMessage());
            } else {
                System.out.println("Inside of getStatement - Error of holder.put(Constants.ACC_TYPE, accountType) - session_id = oooooooooooooooooooooo" + " -- ErrorType = " + e.getMessage());
            }
            e.toString();  //To change body of catch statement use File | Settings | File Templates.
        }
        trans_count = (trans_count != null && trans_count.length() != 0) ? trans_count : "3";
        branchId = (branchId != null && branchId.length() != 0) ? branchId : "";
        docSerial = (docSerial != null && docSerial.length() != 0) ? docSerial : "";
        start_date = (start_date != null && start_date.length() != 0) ? start_date : "";
        end_date = (end_date != null && end_date.length() != 0) ? end_date : "";
        if (sign == null || "".equals(sign))
            if (hostId.equals(Constants.SGB_HOSTID))
                sign = "";
            else if (hostId.equals(Constants.FAR_HOSTID))
                sign = "0";

        opCode = (opCode != null && opCode.trim().length() != 0) ? opCode : "";
        minAmount = (minAmount != null && minAmount.trim().length() != 0) ? minAmount : "";
        maxAmount = (maxAmount != null && maxAmount.trim().length() != 0) ? maxAmount : "";
        fromTime = (fromTime == null) ? "" : fromTime;
        toTime = (toTime == null) ? "" : toTime;

        if (hostId.equals(Constants.FAR_HOSTID)) {
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
        }
        int counter = Integer.parseInt(trans_count);
        //Make input params
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        inputParams.put(Constants.ACC_NO, acc_no);
        inputParams.put(Constants.START_DATE, start_date);
        inputParams.put(Constants.END_DATE, end_date);
        inputParams.put(Constants.START_TIME, fromTime);
        inputParams.put(Constants.END_TIME, toTime);
        inputParams.put(Constants.BRANCH_ID, branchId);
        inputParams.put(Constants.DOC_SERIAL, docSerial);
        inputParams.put(Constants.SIGN, sign);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.MIN_AMOUNT, minAmount);
        inputParams.put(Constants.MAX_AMOUNT, maxAmount);
        inputParams.put(Constants.ACC_TYPE, accountType);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.CB_PAY_ID, cbPayId);

        HostResultSet rs = null;
        HostResultSet hrs_ret = null;

        try {
            if (command.getParam(Fields.IS_EXTRA_LIST) != null && command.getParam(Fields.IS_EXTRA_LIST).equals("true")) {
                inputParams.put(Constants.TRANS_COUNT, trans_count);
//                if (hostId.equals(Constants.FAR_HOSTID))
//                    rs = ((HostInterfaceNAB) host).getExtraStatement(inputParams);
//                else
                    rs = ((HostInterfaceSGB) host).getExtraStatement(inputParams);
                if (rs.next()) {
                    String coreId = msg.getAttributeAsString(Constants.HOST);
                    hrs_ret = getExtraHostResultSet(rs, hostId, ISOUtil.zeropad(msgSeq, 12), ISOUtil.zeropad(acc_no, 13), coreId);
                }
            } else {
                if (counter > 300)
                    trans_count = "300";
                inputParams.put(Constants.TRANS_COUNT, trans_count);
                rs = host.getStatement(inputParams);
                if (rs.next())
                    hrs_ret = getHostResultSet(rs, hostId);
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        } catch (ISOException e) {
            log.error(e);
        }

        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res, String hostId) throws HostException, ISOException {
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
        md.addColumn(rowindex, "OPCODE");
        rowindex++;
        md.addColumn(rowindex, "LASTAMOUNT");
        rowindex++;
        md.addColumn(rowindex, "EFFECTIVE-DATE");
        rowindex++;
        md.addColumn(rowindex, "PAYID1");
        rowindex++;
        md.addColumn(rowindex, "PAYID2");
        rowindex++;
        md.addColumn(rowindex, "CBPAYID");

        hostResultSet.setMetaData(md);
        original_res.moveFirst();

        if (original_res.next()) {
            if (!hostId.equals(Constants.FAR_HOSTID) && original_res.size() > 1)
                original_res.getRows().remove(0);
        }
//            while (original_res.next()) {
        for (int j = 0; j < original_res.size(); j++) {
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

            row.add(original_res.getString("DEBITCREDIT"));
            String desc = original_res.getString("DESCRIPTION");
            if (desc == null) {
                desc = "";
            }
            for (int i = 0; i < 29; i++)
                desc = desc.replaceAll(INVALID_CHARACTER[i], " ");
            row.add(desc);
            row.add(original_res.getString("BRANCHNO"));
            row.add(original_res.getString("DATETIME"));
            if (hostId.equals(Constants.FAR_HOSTID))
                row.add(original_res.getString("BALCRDB") + original_res.getString("FINALBALANCE")); //Main balance in header tages
            else
                row.add(original_res.getString("FINALBALANCE")); //Main balance in header tages
            row.add(original_res.getString("OPCODE"));
            String lastAmount = original_res.getString("LASTAMOUNT");
            if (lastAmount == null || lastAmount.equals(""))
                row.add("0");
            else
                row.add(lastAmount);
            if (hostId.equals(Constants.SGB_HOSTID)) {
                row.add(original_res.getString("effectiveDate"));
                row.add("");
                row.add("");
                row.add("");
            } else {
                row.add("");
                String fPayId = original_res.getString("payId1").trim();
                fPayId = fPayId.equals("000000000000000000000000000000") ? "" : fPayId;
                row.add(fPayId);
                String sPayId = original_res.getString("payId2").trim();
                sPayId = sPayId.equals("000000000000000000000000000000") ? "" : sPayId;
                row.add(sPayId);
                row.add(original_res.getString("cbPayId").trim());
            }
            hostResultSet.addRow(row);
            original_res.next();
        }
//        }

        original_res.moveFirst();
        original_res.next();
        hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
        return hostResultSet;
    }

    private HostResultSet getExtraHostResultSet(HostResultSet original_res, String hostId, String msgSeq, String accNo, String coreId) throws HostException, ISOException {
        DecimalFormat decimalFormat = new DecimalFormat("###");
        int rowindex = 0;
        String docNo, transDt, transTm, transAmnt, transAmnrCrDb, transDesc, brchId, id1, id2, cbPayId, opCode, lAmnt,
                rowNo = "0", effDt, lAmntCrDb, actionCode = "", farFirstActionCode = "";
        StringBuffer repeatableLine = new StringBuffer();
        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "RESPONSE");
        hostResultSet.setMetaData(md);
        //make very firs line
        String firstLine = "";
        original_res.moveFirst();
        if (original_res.next()) {
            actionCode = original_res.getString("ACTIONCODE").substring(1, 5);
            farFirstActionCode = original_res.getString("ACTIONCODE");
            String accBal;
            if (hostId.equals(Constants.FAR_HOSTID))
                accBal = original_res.getString("BALCRDB") + ISOUtil.zeropad(original_res.getString("FINALBALANCE"), 17); //Main balance in header tages
            else {
                accBal = original_res.getString("FINALBALANCE");
                long bal = Long.parseLong(decimalFormat.format(Double.parseDouble(accBal)));
                if (bal > 0)
                    if (Long.toString(bal).length() <= 17)
                        accBal = "+" + ISOUtil.zeropad(Long.toString(bal), 17);
                    else
                        accBal = Long.toString(bal);
                else if (bal < 0)
                    accBal = "-" + ISOUtil.zeropad((Long.toString(bal).substring(1, Long.toString(bal).length())), 17);
                else
                    accBal = ISOUtil.zeropad(Long.toString(bal), 18);
            }
            if (hostId.equals(Constants.FAR_HOSTID)) {
                String farRowCount = ISOUtil.zeropad(String.valueOf(original_res.size()), 4);
                if (original_res.getString("ACTIONCODE").equals("00000")) farRowCount = "0000";
                firstLine = actionCode.concat(msgSeq).concat(farRowCount).concat(accNo).concat(accBal).concat(coreId);
            } else
                firstLine = actionCode.concat(msgSeq).concat(ISOUtil.zeropad(String.valueOf(original_res.size() - 1), 4).concat(accNo).concat(accBal)).concat(coreId);
            //make repeatable line
            original_res.moveFirst();
            if (!hostId.equals(Constants.FAR_HOSTID) ||
                    (coreId.equals(Constants.FAR_HOSTID) && farFirstActionCode.equals("00000")))
                original_res.getRows().remove(0);
            while (original_res.next()) {
                opCode = original_res.getString("OPCODE");
                opCode = opCode != null && !"".equals(opCode.trim()) ? ISOUtil.zeropad(opCode.trim(), 3) : "000";
                repeatableLine.append(opCode);

                transAmnrCrDb = !"".equals(original_res.getString("DEBITCREDIT").trim()) ? original_res.getString("DEBITCREDIT").trim() : "0"; //transaction amount index
                repeatableLine.append(transAmnrCrDb);

                transAmnt = original_res.getString("AMOUNT");
                if (hostId.equals(Constants.FAR_HOSTID))
                    transAmnt = (transAmnt != null && !"".equals(transAmnt.trim()) ? ISOUtil.zeropad(transAmnt.trim(), 17) : "000000000000000000");
                else {
                    long amnt = -1;
                    if (transAmnt != null && !transAmnt.trim().equals("")) {
                        amnt = Long.parseLong(decimalFormat.format(Double.parseDouble(transAmnt)));
                        transAmnt = ISOUtil.zeropad(String.valueOf(amnt), 17);
                    } else
                        transAmnt = "00000000000000000";
                }
                repeatableLine.append(transAmnt);
                transDt = ISOUtil.zeropad(original_res.getString("TRANSDATE").trim(), 8);
                if (hostId.equals(Constants.SGB_HOSTID) && !transDt.equals("00000000"))
                    transDt = DateUtil.getSystemDate().substring(0, 2) + ISOUtil.zeroUnPad(transDt);
                repeatableLine.append(transDt);

                transTm = ISOUtil.zeropad(original_res.getString("TRANSTIME").trim(), 6);
                repeatableLine.append(transTm);

                if (hostId.equals(Constants.SGB_HOSTID))
                    effDt = (original_res.getString("effectiveDate") != null && !original_res.getString("effectiveDate").trim().equals("")) ? original_res.getString("effectiveDate") : "00000000";
                else
                    effDt = "00000000";
                repeatableLine.append(effDt);

                docNo = original_res.getString("DOCNO");
                if (docNo.length() > 7)
                    docNo = original_res.getString("DOCNO").trim().substring(5, 12);
                else
                    docNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(original_res.getString("DOCNO").trim()), 7);
                repeatableLine.append(docNo);

                transDesc = original_res.getString("DESCRIPTION");
                if (transDesc == null || "".equals(transDesc)) {
                    transDesc = "00000000000000000000000000000000000";
                } else
                    for (int i = 0; i < 29; i++)
                        transDesc = transDesc.replaceAll(INVALID_CHARACTER[i], " ");

                if (transDesc.length() > 35)
                    transDesc = transDesc.substring(0, 35);
                else if (transDesc.length() < 35)
                    transDesc = ISOUtil.zeropad(transDesc, 35);
                repeatableLine.append(transDesc);

                brchId = original_res.getString("BRANCHNO");
                brchId = brchId != null ? ISOUtil.zeropad(ISOUtil.zeroUnPad(brchId), 5) : "00000";
                repeatableLine.append(brchId);

                rowNo = String.valueOf(Integer.parseInt(rowNo) + 1);
                if (String.valueOf(rowNo).length() < 4)
                    rowNo = ISOUtil.zeropad(String.valueOf(rowNo), 4);
                repeatableLine.append(rowNo);

                lAmntCrDb = (original_res.getString("LASTAMNTCRDB") != null && !"".equals(original_res.getString("LASTAMNTCRDB"))) ? original_res.getString("LASTAMNTCRDB") : "0";
                repeatableLine.append(lAmntCrDb);

                lAmnt = original_res.getString("LASTAMOUNT");
                if (lAmnt.equals(Constants.FAR_HOSTID))
                    lAmnt = (lAmnt != null && !"".equals(lAmnt.trim())) ? ISOUtil.zeropad(lAmnt.trim(), 17) : "00000000000000000";
                else {
                    if (lAmnt != null && !"".equals(lAmnt.trim())) {
                        long lamntl = Long.parseLong(decimalFormat.format(Double.parseDouble(lAmnt)));
                        lAmnt = ISOUtil.zeropad(String.valueOf(lamntl), 17);
                    } else
                        lAmnt = "00000000000000000";
                }
                repeatableLine.append(lAmnt);

//                id1 = original_res.getString("payId1");
//                id1 = null != id1 ? ISOUtil.zeropad(ISOUtil.zeroUnPad(id1), 30): "000000000000000000000000000000";
//                repeatableLine.append(id1);
//
//                id2 = original_res.getString("payId2");
//                id2 = null != id2 ? ISOUtil.zeropad(ISOUtil.zeroUnPad(id2), 30): "000000000000000000000000000000";
//                repeatableLine.append(id2);
//
//
//                cbPayId = original_res.getString("cbPayId");
//                cbPayId = null != cbPayId ? ISOUtil.zeropad(ISOUtil.zeroUnPad(cbPayId), 12): "000000000000";
//                repeatableLine.append(cbPayId);
            }
        }
        ArrayList row = new ArrayList();
        row.add(firstLine.concat(repeatableLine.toString()));
        hostResultSet.addRow(row);

        original_res.moveFirst();
        original_res.next();
        hostResultSet.setDataHeaderField("action_code", actionCode);
        return hostResultSet;
    }
}
