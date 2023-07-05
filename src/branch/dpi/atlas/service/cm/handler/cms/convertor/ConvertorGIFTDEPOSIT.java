package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.GiftDepositReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: SH.Behnaz
 * Date: December 9, 2015
 * Time: 8:35:45 AM
 */
public class ConvertorGIFTDEPOSIT implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        GiftDepositReq giftDepositMsg = (GiftDepositReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_GIFTCARD_GET_DEPOSIT);
        command.addParam(Fields.BRANCH_CODE,giftDepositMsg.getBranchCode());
        command.addParam(Fields.REQUEST_NO,giftDepositMsg.getRequestNo());
        command.addParam(Fields.BRANCH_DOC_NO,giftDepositMsg.getBranchDocNo());
        command.addParam(Fields.AMOUNT,giftDepositMsg.getAmount());
        command.addParam(Fields.DATE,giftDepositMsg.getTransDate());

        return command;
    }

}
