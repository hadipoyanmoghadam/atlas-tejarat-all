package branch.dpi.atlas.service.cm.handler.pg.convertor;

import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.imf.cms.CMSToIMFFormater;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Nov 24, 2019
 * Time: 10:13 AM
 */
public class ConvertorAccountStatement implements CMSToIMFFormater {

    public CMCommand format(Object o) {
        AccountStatementReq statementMsg = (AccountStatementReq) o;
        CMCommand command = new CMCommand();

        command.setCommandName(TJCommand.CMD_GROUP_ACCOUNT_STATEMENT);
        command.addParam(Fields.FROM_DATE, statementMsg.getFromDate());
        command.addParam(Fields.TO_DATE, statementMsg.getToDate());
        command.addParam(Fields.SRC_ACCOUNT,statementMsg.getAccountNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.TRANS_COUNT,Constants.MAX_TRANS_NO_IN_LIST);
        command.addParam(Fields.REQUEST_IS_ACCOUNT_BASED,"1");
        return command;
    }

}

