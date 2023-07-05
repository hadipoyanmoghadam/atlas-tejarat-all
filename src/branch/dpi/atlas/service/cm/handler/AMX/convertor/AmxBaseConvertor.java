package branch.dpi.atlas.service.cm.handler.AMX.convertor;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 4:20 PM
 */
public class AmxBaseConvertor {

    CMCommand format(AmxMessage amxMessage)
    {
        CMCommand command = new CMCommand();
        
        command.addParam(Fields.REQUEST_DATE, amxMessage.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, amxMessage.getRequestTime());
        command.addParam(Fields.BRANCH_CODE, amxMessage.getBranchCode());
        command.addParam(Fields.MN_RRN, amxMessage.getMessageSequence());

        return command;
    }

}
