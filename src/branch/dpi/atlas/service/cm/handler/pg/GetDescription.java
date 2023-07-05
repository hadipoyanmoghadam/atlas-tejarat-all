package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 24, 2020
 * Time: 12:47 PM
 */
public class GetDescription extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String sgbTxCode= command.getParam(Fields.SGB_TX_CODE);
        String feeTxCode= command.getParam(Fields.OPERATION_CODE_FEE_AMOUNT);

        try {

            List<String> list = ChannelFacadeNew.getDescription(sgbTxCode,feeTxCode);

            command.addParam(Fields.DOCUMENT_DESCRIPTION, list.get(0).trim());
            command.addParam(Fields.EXTRA_INFO, list.get(1).trim());
            command.addParam(Fields.FEE_DOCUMENT_DESCRIPTION, list.get(2).trim());
            command.addParam(Fields.FEE_EXTRA_INFO, list.get(3).trim());

        } catch (SQLException e) {
            log.error("Data Base error in retrieving description with code " + sgbTxCode + " from TBsgbcode");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        } catch (Exception e) {
            log.error("Exception::" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
