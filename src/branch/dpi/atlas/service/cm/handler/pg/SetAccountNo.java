package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import java.util.Map;

/**
 * Created by sh.Behnaz on 11/19/18.
 */
public class SetAccountNo extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST) ;

            command.addParam(Fields.SRC_ACCOUNT,msg.getAttributeAsString(Fields.ACCOUNT_NO));

        } catch (Exception e) {
            log.error("ERROR :::Inside SetAccountNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

