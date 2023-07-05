package branch.dpi.atlas.service.cm.handler.tourist.convertor;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: R.Nasiri
 * Date: Apr 24, 2017
 * Time: 4:17 PM
 */
public class TouristBaseConvertor {

    CMCommand format(TouristMessage branchMsg)
    {
        CMCommand command = new CMCommand();
        
        command.addParam(Fields.REQUEST_DATE, branchMsg.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, branchMsg.getRequestTime());
        command.addParam(Fields.BRANCH_CODE, branchMsg.getBranchCode());
        command.addParam(Fields.MN_RRN, branchMsg.getMessageSequence());

        return command;
    }

}
