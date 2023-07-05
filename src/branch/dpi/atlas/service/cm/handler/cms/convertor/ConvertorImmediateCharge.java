package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.DChargeReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.ImmediateChargeReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: November 03, 2019
 * Time: 11:21:45 AM
 */
public class ConvertorImmediateCharge implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        ImmediateChargeReq immediateChargeMsg = (ImmediateChargeReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_CROUPCARD_IMMEDIATE_CHARGE);
        command.addParam(Fields.PAN,immediateChargeMsg.getCardNo());
        command.addParam(Fields.ACCOUNT_NO,immediateChargeMsg.getAccountNo());
        command.addParam(Fields.SRC_ACCOUNT,immediateChargeMsg.getAccountNo());
        command.addParam(Fields.AMOUNT,immediateChargeMsg.getAmount());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        return command;
    }

}
