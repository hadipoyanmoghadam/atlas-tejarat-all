package branch.dpi.atlas.service.cm.handler.AMX;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jan 5 2020
 * Time: 02:15 PM
 */
public class CheckAccountNature extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside CheckAccountNature.doProcess(CMMessage msg, Map holder)");
        try {

            if (msg.hasAttribute(Constants.DEST_ACCOUNT_NATURE)) {

                int destAccountNature = Integer.parseInt(msg.getAttributeAsString(Constants.DEST_ACCOUNT_NATURE));
                if (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }
            }
        } catch (CMFault fe) {
            throw fe;
        } catch (Exception e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}