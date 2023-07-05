package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: May 20, 2008
 * Time: 9:22:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class FarCoreFollowup extends HostHandlerBase {
  private static Log log = LogFactory.getLog(FarCoreFollowup.class);

  public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
    if (log.isInfoEnabled()) log.info("Inside FarCoreFollowup:_post()");

      CMMessage msg = (CMMessage) o;
    String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
    String branch_code = (String) msg.getAttribute(Fields.CHANNEL_BRANCH_NO);
    String acc_no = (String) msg.getAttribute(Fields.SRC_ACC_NO);

    CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
    String date = command.getHeaderParam(Fields.DATE);
    String time = command.getHeaderParam(Fields.TIME);
    String refNo = command.getParam(Fields.REFRENCE_NUMBER);    
    String core = command.getParam(Constants.FLW_CORE_SEQ);
    if (core.equals(Constants.DEST_CORE))
      acc_no = (String) msg.getAttribute(Fields.DEST_ACCOUNT_NO);

    if (service == null || "".equals(service.trim()))
      service = (String) msg.getAttribute(CMMessage.SERVICE_TYPE);

    if (branch_code == null || "".equals(branch_code.trim()))
      branch_code = "";

    HostResultSet hrs;
    HostResultSet hrs_ret = null;
    try {
      if (sessionID != null && "".equals(sessionID.trim()))
        hrs = ((HostInterfaceNAB) hostInterface).Followup(sessionID, acc_no, service, "", date, time, "", "",
                branch_code, refNo);
      else {
        hrs = ((HostInterfaceNAB) hostInterface).Followup(acc_no, service, "", date, time, "", "", branch_code, refNo);
      }
      if (hrs.next()) {
        hrs_ret = getHostResultSet(hrs);
      }
    } catch (HostException e) {
      log.error(e);
      if (log.isDebugEnabled()) log.debug("e.getErrorCode() = " + e.getErrorCode());
      throw new CMFault(CMFault.FAULT_INVALID_CUSTOMER, e.getMessage());
    }
    return hrs_ret;
  }

  private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
    HostResultSet hostResultSet = new HostResultSet();
    hostResultSet.setDataHeaderField("action_code", original_res.getString("ACTIONCODE").substring(1, 5));
    hostResultSet.setDataHeaderField("amount", original_res.getString("transamount"));
    hostResultSet.setDataHeaderField("date_time", original_res.getString("date") + original_res.getString("time"));
    return hostResultSet;
  }
}

