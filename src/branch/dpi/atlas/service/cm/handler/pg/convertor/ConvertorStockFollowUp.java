package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositReq;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Appril 20, 2020
 * Time: 4:47 PM
 */
public class ConvertorStockFollowUp implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        StockFollowUpReq stockFollowUpReq = (StockFollowUpReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_FOLLOW_UP_PG);
        command.addParam(Fields.SRC_ACCOUNT, stockFollowUpReq.getSrcAccountNo());
        command.addParam(Fields.DEST_ACCOUNT, stockFollowUpReq.getDestAccountNo());
        command.addParam(Fields.AMOUNT, stockFollowUpReq.getAmount());
        command.addParam(Fields.DATE, stockFollowUpReq.getTransDate());
        command.addParam(Fields.TIME, stockFollowUpReq.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, "");
        command.addParam(Fields.TERMINAL_ID, Fields.SERVICE_PG);
        command.addParam(Fields.ORIG_MESSAGE_DATA, stockFollowUpReq.getOrigMsgData());



        return command;
    }

}
