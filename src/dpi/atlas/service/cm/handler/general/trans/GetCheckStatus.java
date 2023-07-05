package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;

import java.util.Map;
import java.util.ArrayList;

import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Oct 4, 2007
 * Time: 4:49:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetCheckStatus extends HostHandlerBase {

  public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
    if (log.isInfoEnabled()) log.info("Inside GetCheckStatus:_post()");
      CMMessage msg = (CMMessage) o;
      CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
    String acc_no = command.getParam(Fields.ACCOUNT_NO);

    String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
    String host_id = (String) msg.getAttribute(Constants.SRC_HOST_ID);
    String branch_code = (String) msg.getAttribute(Fields.BRANCH_CODE);

    if (service == null || "".equals(service.trim()))
      service = (String) msg.getAttribute(CMMessage.SERVICE_TYPE);
    if (branch_code == null || "".equals(branch_code.trim()))
      branch_code = "";

    HostResultSet hrs = null;
    HostResultSet hrs_ret = null;
    try {
      if (sessionID != null && "".equals(sessionID.trim()))
        hrs = hostInterface.getBalance(sessionID, acc_no, service, "", "", "");
      else
        hrs = hostInterface.getBalance(acc_no, service, "", "", "");

      if (hrs.next()) {
        hrs_ret = getHostResultSet(hrs, host_id, branch_code);
        try {
          String resAccNo = ISOUtil.zeropad(hrs_ret.getDataHeaderField(Fields.ACC_NO), 13);
          if (!acc_no.equalsIgnoreCase(resAccNo)) {
            System.out.println("");
            System.out.println(" ERROR, the Acc and ResAcc is not equal :: reqAcc = " + acc_no + " , resAcc = " + resAccNo);
            System.out.println("");
          }
        } catch (ISOException e) {
          e.toString();  //To change body of catch statement use File | Settings | File Templates.
        }

      }
    } catch (HostException e) {
      String errorCode = Integer.toString(e.getErrorCode());
      //Todo : here "throw new CMFault(errorCode)" is used, providing that this error will be handled by other catch clause in a the caller method, here "TejaratHostPostHandler"
      //TOdo : if we want to handle the excpetion comprehensively, it seems we must 'throw' two kind if exception in singnature of the method; "CMFault" and "HostException",
      //Todo : then in this way we can manage "HostException" by statement "throw new CMFault(errorCode);" and CMFault by statement "throw new CMFault(CMFault.FAULT_EXTERNAL, e.getMessage());"
      throw new CMFault(errorCode);
    }
    return hrs_ret;
  }

  private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String branchCode) throws HostException {
    int rowindex = 0;

    HostResultSet hostResultSet = new HostResultSet();
    HostResultSetMetaData md = new HostResultSetMetaData();
    rowindex++;
    md.addColumn(rowindex, "BAL");
    rowindex++;
    md.addColumn(rowindex, "Currency");
    rowindex++;
    md.addColumn(rowindex, "hostId");
    rowindex++;
    md.addColumn(rowindex, "serverUpdateDate");
    rowindex++;
    md.addColumn(rowindex, "branchCode");
    rowindex++;
    md.addColumn(rowindex, "accountGroup");
    rowindex++;
    md.addColumn(rowindex, "CRDB");
    rowindex++;
    md.addColumn(rowindex, "lastTransDate");
    rowindex++;
    md.addColumn(rowindex, "ledgerBalance");

    hostResultSet.setMetaData(md);

    ArrayList row = new ArrayList();
    row.add(original_res.getString("AVAILABLEBALANCE"));
    row.add("IRR");
    row.add(host_id);
    row.add(branchCode);
    row.add(original_res.getString("ACCOUNTGROUP"));

    if (host_id.equals(Constants.SGB_HOSTID))
      row.add(original_res.getString("LASTTRANSDATE"));
    else
      row.add("");
    if (host_id.equals(Constants.FAR_HOSTID)) {
      String ledgerBal = original_res.getString("LEDGERBALANCE");
      if (Float.parseFloat(ledgerBal) < 0)
        ledgerBal = ledgerBal.substring(1, ledgerBal.length());
      row.add(ledgerBal);
    } else {
      String availableBal = original_res.getString("AVAILABLEBALANCE");
      if (Float.parseFloat(availableBal) < 0)
        availableBal = availableBal.substring(1, availableBal.length());
      row.add(availableBal);
    }
    hostResultSet.addRow(row);

    hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
    hostResultSet.setDataHeaderField(Fields.ACC_NO, original_res.getString("ACCOUNTNO").trim());

    return hostResultSet;
  }

}

