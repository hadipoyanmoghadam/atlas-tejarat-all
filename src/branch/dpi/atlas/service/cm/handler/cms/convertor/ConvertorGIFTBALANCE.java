package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.giftBalance.GiftBalanceReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: SH.Behnaz
 * Date: December 13, 2016
 * Time: 8:35:45 AM
 */
public class ConvertorGIFTBALANCE implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        GiftBalanceReq giftBalanceMsg = (GiftBalanceReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_BALANCE_BATCH);
        command.addParam(Fields.BRANCH_CODE, giftBalanceMsg.getBranchCode());

        return command;
    }

}
