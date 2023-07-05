package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Jul 8, 2007
 * Time: 12:56:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareSimpleResponse extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        CMResultSet result = new CMResultSet();
        result.setHeaderField(Params.ACTION_CODE, ActionCode.APPROVED);
        msg.setAttribute(CFSConstants.ACTION_CODE, ActionCode.APPROVED);
        result.setRequest(command.toString());

        if (command.getHeaderParam(Fields.MESSAGE_ID) != null) {
            int msg_id = (new Integer(command.getHeaderParam(Fields.MESSAGE_ID))).intValue() + 1;
            result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");
        }
//        msg.setAttribute("result", result.toString());
        if (!msg.hasAttribute(CFSConstants.ADMIN_ENABLE)) {
            msg.setAttribute(CMMessage.RESPONSE, result.toString());
            holder.put(CFSConstants.RESULT, result);
        } else {
            String str = (String) holder.get(CMMessage.RESPONSE);
            if (log.isInfoEnabled()) log.info("Returned string to admin request is : " + str);
            msg.setAttribute(CMMessage.RESPONSE, str);
            holder.put(CFSConstants.RESULT, result);
        }
    }
}
