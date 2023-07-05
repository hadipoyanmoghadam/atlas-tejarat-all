package branch.dpi.atlas.service.cm.handler.credits.convertor;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

/**
 * User: R.Nasiri
 * Date: Sep 4, 2017
 * Time: 03:10 PM
 */
public class CreditsBaseConvertor {

    CMCommand format(CreditsMessage creditsMessage)
    {
        CMCommand command = new CMCommand();
        
        command.addParam(Fields.REQUEST_DATE, creditsMessage.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, creditsMessage.getRequestTime());
        command.addParam(Fields.BRANCH_CODE, creditsMessage.getBranchCode());
        command.addParam(Fields.MN_RRN, creditsMessage.getMessageSequence());

        return command;
    }

}
