package branch.dpi.atlas.service.cm.handler.pg;

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
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 24, 2020
 * Time: 03:42 PM
 */
public class GetSGBTxCode extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {
            String depositCommand = TJCommand.CMD_DEPOSIT_STOCK_PG;
            String feeCommand = TJCommand.CMD_FEE_STOCK_PG;

            String sgbTxCode = ChannelFacadeNew.getTxTypeSgbTxCode(depositCommand,feeCommand);
            String[] txCode=sgbTxCode.split(",");

            command.addParam(Fields.OPERATION_CODE, txCode[0]);
            command.addParam(Fields.SGB_TX_CODE, txCode[0]);
            command.addParam(Fields.OPERATION_CODE_FEE_AMOUNT, txCode[1]);
            command.addParam(Fields.CHANNEL_TYPE, command.getParam(Fields.TERMINAL_ID));

        } catch (SQLException e) {
            log.error("Data Base error in retrieving TxType with code " + TJCommand.CMD_DEPOSIT_STOCK_PG + " from TBCFSTxType");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        } catch (Exception e) {
            log.error("Exception ::" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
