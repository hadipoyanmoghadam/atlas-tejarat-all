package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.model.sgb.HostInterfaceSGB;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.Constants;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: sep 4, 2008
 * Time: 4:50:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetCurrencyRate extends HostHandlerBase {

  public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
    if (log.isInfoEnabled()) log.info("Inside GetCurrencyRate:_post()");
    CMMessage msg = (CMMessage) o;
    CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
    msg.setAttribute(Fields.COMMAND_NAME, command.getCommandName());  
    String currencyCode = command.getParam(Fields.CURRENCY_CODE);
    HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
    inputParams.put(Constants.CURRENCY_CODE, currencyCode);
    HostResultSet hrs = null;
    HostResultSet hrs_ret = null;
    String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
    if (sessionID != null && !"".equals(sessionID.trim()))
      inputParams.put(Constants.SESSION_ID, sessionID);
    else
      inputParams.put(Constants.SESSION_ID, "000000000000");
    try {
      hrs = ((HostInterfaceSGB) hostInterface).getCurrencyRate(inputParams);
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
    md.addColumn(rowindex, Constants.CURRENCY_CODE);
    rowindex++;
    md.addColumn(rowindex, Constants.CURRENCY_UNIT);
    rowindex++;
    md.addColumn(rowindex, Constants.SELL_RATE);
    rowindex++;
    md.addColumn(rowindex, Constants.BUY_RATE);
    rowindex++;
    md.addColumn(rowindex, Constants.DAY_DATE);
    rowindex++;
    md.addColumn(rowindex, Constants.DAY_TIME);
    rowindex++;
    md.addColumn(rowindex, Constants.CURRENCY_DESC);

    hostResultSet.setMetaData(md);

    ArrayList row = new ArrayList();
    row.add(original_res.getString(Constants.CURRENCY_CODE));
    row.add(original_res.getString(Constants.CURRENCY_UNIT));
    row.add(original_res.getString(Constants.SELL_RATE));
    row.add(original_res.getString(Constants.BUY_RATE));
    row.add(original_res.getString(Constants.DAY_DATE));
    row.add(original_res.getString(Constants.DAY_TIME));
    row.add(original_res.getString(Constants.CURRENCY_DESC));

    hostResultSet.addRow(row);     
    hostResultSet.setDataHeaderField(Fields.ACTION_CODE, original_res.getString(Constants.ACTION_CODE).substring(1, 5));

    return hostResultSet;
  }

}



