package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: SH.Behnaz
 * Date: 7, 7, 2019
 * Time: 09:21:45 AM
 */
public class ConvertorWFPStatement implements CMSToIMFFormater {

    public CMCommand format(Object o) {
        StatementReq StmMsg = (StatementReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_CMS_WFP_STATEMENT);
        command.addParam(Fields.FROM_DATE, StmMsg.getFromDate());
        command.addParam(Fields.TO_DATE, StmMsg.getToDate());
        command.addParam(Fields.SRC_ACCOUNT,StmMsg.getAccountNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        return command;
    }

}

