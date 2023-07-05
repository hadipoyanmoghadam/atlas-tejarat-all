package dpi.atlas.service.cm.handler.general;

import dpi.atlas.model.ModelException;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * HostTxnLogger Class
 *
 * @author <a href="mailto:PNaeimi@dpi2.dpi.net.ir">Parisa Naeimi</a>
 * @version $Revision: 1.7 $ Mar 6, 2006 1:31:39 PM $
 */

public class HostGenerateRefNo extends TJServiceHandler {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            RefNoGeneratorNew generator = RefNoGeneratorNew.getInstance();
            String strRefNo = generator.generateRefNo("FT");
            if (log.isDebugEnabled()) log.debug("strRefNo = " + strRefNo);
            CMCommand cmCommand = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            cmCommand.addHeaderParam(Fields.REFRENCE_NUMBER, strRefNo);
            msg.setAttribute(Constants.ACH_REF_NO, strRefNo);
            msg.setAttribute(CMMessage.REQUEST, cmCommand);
        }
        catch (ModelException me) {
            log.error(me.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, me);
        }
    }

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        super.setConfiguration(configuration);    //To change body of overridden methods use File | Settings | File Templates.
    }
}