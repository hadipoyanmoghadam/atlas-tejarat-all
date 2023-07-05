package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: August 29, 2018
 * Time: 15:21:45 AM
 */
public class ConvertorChargeTransaction implements CMSToIMFFormater {

    public CMCommand format(Object o) {
        ChargeReportReq chargeMsg = (ChargeReportReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_CHARGE_TRANSACTION);
        command.addParam(Fields.FROM_DATE, chargeMsg.getFromDate());
        command.addParam(Fields.TO_DATE, chargeMsg.getToDate());
        command.addParam(Fields.SRC_ACCOUNT,chargeMsg.getAccountNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        return command;
    }

}

