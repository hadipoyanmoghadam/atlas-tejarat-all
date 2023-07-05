package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.cardBalance.CardBalanceReq;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: March 29, 2020
 * Time: 9:21:45 AM
 */
public class ConvertorStockDeposit implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        StockDepositReq stockDepositReq = (StockDepositReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_DEPOSIT_STOCK_PG);
        command.addParam(Fields.SRC_ACCOUNT, stockDepositReq.getSrcAccountNo());
        command.addParam(Fields.DEST_ACCOUNT, stockDepositReq.getDestAccountNo());
        command.addParam(Fields.AMOUNT, stockDepositReq.getAmount());
        command.addParam(Fields.DATE, stockDepositReq.getTransDate());
        command.addParam(Fields.TIME, stockDepositReq.getTransTime());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        String branchDocNo=stockDepositReq.getRrn();
        branchDocNo=branchDocNo.substring(branchDocNo.length()-6);
        command.addParam(Fields.BRANCH_DOC_NO,branchDocNo);
        command.addParam(Fields.TERMINAL_ID, Fields.SERVICE_PG);
        command.addParam(Fields.PIN, Constants.STOCK_DEPOSIT_MSG_TYPE);

        return command;
    }

}
