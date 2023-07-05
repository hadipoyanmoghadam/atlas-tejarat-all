package branch.dpi.atlas.service.cm.handler.manzume.convertor;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: F.Heydari
 * Date: OCT 24, 2022
 * Time: 2:13:42 PM
 */
public class ManzumeBaseConvertor {

    CMCommand format(ManzumeMessage manzumeMessage)
    {
        CMCommand command = new CMCommand();

        command.addParam(Fields.REQUEST_DATE, manzumeMessage.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, manzumeMessage.getRequestTime());
        command.addParam(Fields.BRANCH_CODE, manzumeMessage.getBranchCode());
        command.addParam(Fields.MN_RRN, manzumeMessage.getMessageSequence());

        return command;
    }

}
