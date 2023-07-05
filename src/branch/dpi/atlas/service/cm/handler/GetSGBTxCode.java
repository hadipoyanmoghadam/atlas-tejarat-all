package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 17, 2019
 * Time: 04:40 PM
 */
public class GetSGBTxCode extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String sgbTxCode = "";
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {
            String   txCode = command.getCommandName();

            sgbTxCode = ChannelFacadeNew.getTxTypeSgbTxCode(txCode);
            if (log.isDebugEnabled()) log.debug("sgbTxCode =" + sgbTxCode);
            command.addParam(Fields.SGB_TX_CODE, sgbTxCode.trim());
            command.addParam(Fields.CHANNEL_TYPE, command.getParam(Fields.TERMINAL_ID));

        } catch (SQLException e) {
            log.error("Data Base error in retrieving TxType with code " + sgbTxCode + " from TBCFSTxType");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        } catch (Exception e) {
            log.error("Exception in Utility Core::" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
