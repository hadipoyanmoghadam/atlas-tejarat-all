package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
  * Created by Behnaz .
  * Date: July 13, 2019
  * Time: 10:46:57 AM
 */
public class FTIsReversed extends CMHandlerBase implements Configurable {
    ArrayList reversed_ac = new ArrayList();

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
        String isReversed = Constants.FAILE;           //ToDo   check ActionCodes ....
        if (actionCode.equals(ActionCode.APPROVED))
            isReversed = Constants.FALSE;
        else {
            Iterator it = reversed_ac.iterator();
            while (it.hasNext()) {
                String ac = (String) it.next();
                if (actionCode.equals(ac))
                    isReversed = Constants.TRUE;
            }
        }
        msg.setAttribute(Constants.FTREVERSED, isReversed);
        CMCommand cmCommand = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        cmCommand.addParam(Constants.ISREVERSED, isReversed);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        if (log.isDebugEnabled()) log.debug("-----------> setConfiguration()");
        String retry = cfg.get("FTReversed");
        StringTokenizer st = new StringTokenizer(retry);
        while (st.hasMoreTokens()) {
            reversed_ac.add(st.nextToken(","));
        }
    }
}
