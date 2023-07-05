package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.giftDisCharge.GIFTDISCHARGEType;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: SH.Behnaz
 * Date: June 17, 2015
 * Time: 9:35:45 AM
 */
public class ConvertorGIFTDISCHARGE implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        GIFTDISCHARGEType giftDisChargeMsg = (GIFTDISCHARGEType) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_GIFTCARD_DISCHARGE);
        command.addParam(Fields.PAN,giftDisChargeMsg.getCardno());
        command.addParam(Fields.AMOUNT,giftDisChargeMsg.getAmount());
        command.addParam(Fields.REQUEST_NO,giftDisChargeMsg.getRequestNo());
        command.addParam(Fields.DATE,giftDisChargeMsg.getCreationDate());
        command.addParam(Fields.TIME,giftDisChargeMsg.getCreationTime());

        return command;
    }

}
