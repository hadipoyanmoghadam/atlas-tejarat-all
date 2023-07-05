package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.handler.cms.cancellation.CANCELLATIONType;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: SH.Behnaz
 * Date: June 20, 2015
 * Time: 14:35:45 AM
 */
public class ConvertorCancellation implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        CANCELLATIONType cancellationMsg = (CANCELLATIONType) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_CANCELLATION);
        command.addParam(Fields.REQUEST_NO,cancellationMsg.getRequestNo());
        command.addParam(Fields.DATE,cancellationMsg.getCreationDate());
        command.addParam(Fields.TIME,cancellationMsg.getCreationTime());

        return command;
    }

}
