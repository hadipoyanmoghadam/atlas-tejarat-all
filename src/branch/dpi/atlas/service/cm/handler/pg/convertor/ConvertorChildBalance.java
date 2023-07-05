package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.childInfo.ChildInfoReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: Nov 18, 2018
 * Time: 9:21:45 AM
 */
public class ConvertorChildBalance implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        ChildInfoReq balanceMsg = (ChildInfoReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CARD_BALANCE);
        command.addParam(Fields.PAN,balanceMsg.getCardno());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);

        return command;
    }

}
