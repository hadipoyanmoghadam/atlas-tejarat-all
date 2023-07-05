package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.giftCharge.GIFTCHARGEType;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: SH.Behnaz
 * Date: June 9, 2015
 * Time: 9:35:45 AM
 */
public class ConvertorGIFTCHARGE implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        GIFTCHARGEType giftChargeMsg = (GIFTCHARGEType) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_GIFTCARD_CHARGE);
        command.addParam(Fields.PAN,giftChargeMsg.getCardno());
        command.addParam(Fields.AMOUNT,giftChargeMsg.getAmount());
        command.addParam(Fields.REQUEST_NO,giftChargeMsg.getRequestNo());
        command.addParam(Fields.DATE,giftChargeMsg.getCreationDate());
        command.addParam(Fields.TIME,giftChargeMsg.getCreationTime());

        return command;
    }

}
