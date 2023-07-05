package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.DChargeReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: september 9, 2018
 * Time: 11:21:45 AM
 */
public class ConvertorDCharge implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        DChargeReq dChargeMsg = (DChargeReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_CROUPCARD_DCHARGE);
        command.addParam(Fields.PAN,dChargeMsg.getCardNo());
        command.addParam(Fields.ACCOUNT_NO,dChargeMsg.getAccountNo());
        command.addParam(Fields.SRC_ACCOUNT,dChargeMsg.getAccountNo());
        command.addParam(Fields.DCHARGE_TYPE ,dChargeMsg.getType());
        command.addParam(Fields.AMOUNT,dChargeMsg.getAmount());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        if (dChargeMsg.getCardNo()==null || dChargeMsg.getCardNo().trim().equals(""))
            command.setCommandName(TJCommand.CMD_CMS_CROUPCARD_ALL_DCHARGE);
        return command;
    }

}
