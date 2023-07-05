package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:20 $
 */

public class GetBatch4CM extends CMHandlerBase implements Configurable {
    private boolean getFromCash;

    public void setGetFromCash(boolean getFromCash) {
        this.getFromCash = getFromCash;
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        Batch batch;
        try {
                                   
            batch = ChannelFacadeNew.getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND, getFromCash);

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            command.addParam(CFSConstants.CURRENT_BATCH, batch.getBatchPk().toString());
            msg.setAttribute(CFSConstants.CURRENT_BATCH, batch.getBatchPk().toString());
            msg.setAttribute(CMMessage.REQUEST, command);
        } catch (NotFoundException e) {

            log.error("BatchPk= NOT FOUND!!!!!! " + " - " + "MESSAGE_ID =" + msg.getAttribute(Fields.MESSAGE_ID) + " - " + "SESSION_ID =" + msg.getAttribute(Fields.SESSION_ID) + " - ");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACTIVE_BATCH_NOT_FOUND);
            msg.setAttribute(CMMessage.FAULT_CODE, CMFault.FAULT_AUTH_SERVER);
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        }
        catch (SQLException me) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL_UNKNOWN, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String getBatchFromCash = cfg.get("getBatchFromCash");
        getFromCash = getBatchFromCash.equals("yes");

    }
}


