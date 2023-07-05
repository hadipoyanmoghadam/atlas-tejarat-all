package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;
import java.sql.SQLException;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:18 $
 */

public class GetIMD extends CFSHandlerBase implements Configurable {

    private String imdType;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String imdCode = (String) msg.getAttribute(imdType);

        if (log.isDebugEnabled()) log.debug(imdCode);
        dpi.atlas.model.tj.entity.Imd imd = null;

        try {
            imd = CFSFacadeNew.getIMD(imdCode);
        } catch (SQLException me) {
            if (log.isErrorEnabled())
                log.error("GetIMD-GENERAL_DATA_ERROR");

            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (NotFoundException e) {
            if (log.isInfoEnabled()) log.info("IMD " + imdCode + " Does Not Exist");
            throw new CFSFault(CFSFault.FLT_BANK_NOT_FOUND, new Exception(ActionCode.BANK_NOT_FOUND));
        }

        if (imdType.equals(Fields.SRC_BIN))
            holder.put(Fields.SRC_BIN, imd);
        else if (imdType.equals(Fields.DEST_BIN))
            holder.put(Fields.DEST_BIN, imd);
        else {
            if (log.isDebugEnabled()) log.debug("Invalid imd type" + imdType);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        imdType = cfg.get(CFSConstants.IMD_TYPE);
        if ((imdType == null) || (imdType.trim().equals("")))
            log.fatal("IMD Type is not Specified");
    }
}