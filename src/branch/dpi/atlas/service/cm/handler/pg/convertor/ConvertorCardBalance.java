package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.cardBalance.CardBalanceReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: MARCH 29, 2017
 * Time: 9:21:45 AM
 */
public class ConvertorCardBalance implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        CardBalanceReq balanceMsg = (CardBalanceReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CARD_BALANCE);
        command.addParam(Fields.PAN,balanceMsg.getCardno());
        command.addParam(Fields.SRC_ACCOUNT,balanceMsg.getAccountNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);

        return command;
    }

}
