package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;

/**
 * User: H.Ghayoumi
 * Date: Apr 16, 2013
 * Time: 5:20:42 PM
 */
public class BranchBaseConvertor {

    CMCommand format(BranchMessage branchMsg)
    {
        CMCommand command = new CMCommand();
        
        command.addParam(Fields.REQUEST_DATE, branchMsg.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, branchMsg.getRequestTime());
        command.addParam(Fields.BRANCH_CODE, branchMsg.getBranchCode());
        command.addParam(Fields.MN_RRN, branchMsg.getMessageSequence());     

        return command;
    }

}
