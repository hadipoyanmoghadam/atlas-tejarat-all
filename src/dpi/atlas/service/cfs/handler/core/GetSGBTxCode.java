package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.model.tj.entity.TxType;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: Shahram Boroon
 * Date: Mar 13, 2007
 * Time: 10:35:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetSGBTxCode extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        TxType sgbTxType ;
        String  sgbTxCode = "";
        String txCode = (String) msg.getAttribute(Fields.MESSAGE_TYPE);
        if (log.isDebugEnabled()) log.debug("txCode=" + txCode);
        try {
            sgbTxType = CFSFacadeNew.getTxTypeSgbTxCode(txCode);
             sgbTxCode=sgbTxType.getSgbTxCode().trim();
            if (log.isDebugEnabled()) log.debug("sgbTxCode =" + sgbTxCode);
            if (sgbTxCode  == null || "".equals(sgbTxCode)) {
                log.error("SGB Tx Code is null for tx " + sgbTxCode);
                throw new NotFoundException("SGB Trasaction Code is null for Tx " + sgbTxCode);
            } else{
                holder.put(Fields.SGB_TX_CODE, sgbTxCode);
                msg.setAttribute(Fields.SGB_TX_CODE, sgbTxCode);
            }
        } catch (SQLException e) {
            log.error("General data error in retrieving TxType with code " + sgbTxCode + " from TBCFSTxType");
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNSUPPORTED_MESSAGE, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        }
    }
}
