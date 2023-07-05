package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * User: SH Behnaz
 * Date: June 16, 2013
 * Time: 10:35:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetSGBTxCode extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String sgbTxCode = (String) msg.getAttribute(Fields.OPERATION_CODE);

        if (sgbTxCode == null || "".equals(sgbTxCode.trim())) {
            log.error("SGB Tx Code is null for tx " + sgbTxCode);
            throw new CFSFault(CFSFault.FLT_UNSUPPORTED_MESSAGE, ActionCode.UNSUPPORTED_MESSAGE);
        } else
            msg.setAttribute(Fields.SGB_TX_CODE, sgbTxCode.trim());
    }
}