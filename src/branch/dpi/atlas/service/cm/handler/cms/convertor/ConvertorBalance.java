package branch.dpi.atlas.service.cm.handler.cms.convertor;

import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;

/**
 * User: SH.Behnaz
 * Date: June 8, 2015
 * Time: 9:21:45 AM
 */
public class ConvertorBalance  implements CMSToIMFFormater {

    public CMCommand format(Object o) {

        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_BALANCE);
//        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);

        return command;
    }

}
